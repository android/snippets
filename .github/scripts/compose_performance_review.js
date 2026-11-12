/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Publishes the Jetpack Compose performance review on a pull request:
//  - One sticky summary comment per PR that is edited in place on every run.
//  - Inline review comments anchored to the affected lines, de-duplicated across runs.
//
// Invoked from .github/workflows/compose_performance_skill.yml via actions/github-script with
// REVIEW_PHASE=start (mark the sticky comment as in progress) or REVIEW_PHASE=finish.

const crypto = require('crypto');

const SUMMARY_MARKER = '<!-- compose-performance-review:summary -->';
const RESULTS_MARKER = '<!-- compose-performance-review:results -->';
const FINDING_MARKER_RE = /<!-- compose-performance-review:finding ([0-9a-f]+) -->/;
const HEADING = '## ⚡ Jetpack Compose Performance Review';

module.exports = async ({ github, context, core }) => {
  const env = process.env;
  const phase = env.REVIEW_PHASE || 'finish';
  const prNumber = Number(env.PR_NUMBER || 0);
  const { owner, repo } = context.repo;
  const runUrl = `${context.serverUrl}/${owner}/${repo}/actions/runs/${context.runId}`;

  if (!prNumber) {
    // Commit mode (workflow_dispatch without a PR): there is nowhere to comment.
    await core.summary.addRaw(env.REVIEW_SUMMARY || 'No review output.').write();
    return;
  }

  const { data: pr } = await github.rest.pulls.get({ owner, repo, pull_number: prNumber });
  const headSha = pr.head.sha;
  const shortSha = headSha.slice(0, 7);
  const commitLink = `[\`${shortSha}\`](${pr.html_url}/commits/${headSha})`;
  const botLogin = await resolveBotLogin(github);

  const sticky = await findStickyComment(github, owner, repo, prNumber, botLogin);

  if (phase === 'start') {
    const previous = extractResults(sticky.comment?.body);
    const body = renderSticky({
      status: `⏳ Reviewing ${commitLink}…`,
      runUrl,
      results: previous
        ? `> [!NOTE]\n> The results below are from the previous review and will be replaced when this run finishes.\n\n${previous}`
        : '',
    });
    await upsertSticky(github, owner, repo, prNumber, sticky, body);
    return;
  }

  // ---- finish phase -------------------------------------------------------------------------
  const fileCount = Number(env.FILE_COUNT || 0);
  const summary = (env.REVIEW_SUMMARY || '').trim();
  const reviewOutcome = env.REVIEW_OUTCOME || '';
  const capacityError = env.CAPACITY_ERROR === 'true';

  if (fileCount === 0) {
    await upsertSticky(github, owner, repo, prNumber, sticky, renderSticky({
      status: `✅ No modified Kotlin files to review in ${commitLink}.`,
      runUrl,
    }));
    return;
  }

  if (!summary) {
    const status = capacityError
      ? `⏭️ Skipped for ${commitLink}: the Gemini model is temporarily unavailable. Re-run the workflow to get a review.`
      : `❌ The review failed for ${commitLink} (outcome: \`${reviewOutcome || 'unknown'}\`). See the [workflow run](${runUrl}) for details.`;
    const previous = extractResults(sticky.comment?.body);
    await upsertSticky(github, owner, repo, prNumber, sticky, renderSticky({
      status,
      runUrl,
      results: previous
        ? `> [!NOTE]\n> The results below are from an earlier review.\n\n${previous}`
        : '',
    }));
    return;
  }

  const { overview, findings, parsed } = parseModelResponse(summary);
  if (!parsed) {
    core.warning('Could not parse structured findings from the model response; posting it as-is.');
    await upsertSticky(github, owner, repo, prNumber, sticky, renderSticky({
      status: `⚠️ Reviewed ${commitLink}, but the findings could not be posted inline.`,
      runUrl,
      results: stripHeading(summary),
    }));
    return;
  }

  const diffIndex = await buildDiffIndex(github, owner, repo, prNumber);
  const existing = await listExistingFindingComments(github, owner, repo, prNumber);

  const toPost = [];
  const rows = [];
  const unanchored = [];
  const seen = new Set();

  for (const finding of findings) {
    const fingerprint = fingerprintOf(finding);
    const anchor = anchorFinding(finding, diffIndex);
    const row = { finding, fingerprint, anchor, state: 'new' };
    rows.push(row);

    if (!anchor) {
      row.state = 'outside-diff';
      unanchored.push(row);
    } else if (existing.has(fingerprint) || seen.has(fingerprint)) {
      row.state = 'existing';
    } else {
      toPost.push(row);
    }
    seen.add(fingerprint);
  }

  const failed = await postInlineComments(github, core, {
    owner, repo, prNumber, headSha, rows: toPost,
  });
  for (const row of failed) {
    row.state = 'outside-diff';
    unanchored.push(row);
  }

  // Refresh so the summary table can link to each inline thread.
  const urls = await listExistingFindingComments(github, owner, repo, prNumber);

  const status = findings.length === 0
    ? `✅ No Jetpack Compose performance issues found in ${commitLink}.`
    : `⚠️ ${findings.length} potential Jetpack Compose performance ${plural(findings.length, 'issue')} in ${commitLink}` +
      (toPost.length - failed.length > 0
        ? ` (${toPost.length - failed.length} new inline ${plural(toPost.length - failed.length, 'comment')}).`
        : '.');

  const results = renderResults({ overview, rows, unanchored, urls });
  await upsertSticky(github, owner, repo, prNumber, sticky, renderSticky({ status, runUrl, results }));
  await core.summary.addRaw(`${HEADING}\n\n${status}\n\n${results}`).write();
};

// ---- Model output -------------------------------------------------------------------------------

function parseModelResponse(text) {
  // The prompt asks for a short Markdown overview followed by a single ```json fenced block.
  const fenceRe = /```json\s*([\s\S]*?)```/gi;
  let match;
  let last = null;
  while ((match = fenceRe.exec(text)) !== null) last = match;

  const candidates = [];
  if (last) candidates.push({ json: last[1], overview: text.slice(0, last.index) + text.slice(last.index + last[0].length) });
  candidates.push({ json: text, overview: '' });

  for (const { json, overview } of candidates) {
    try {
      const data = JSON.parse(json.trim());
      const list = Array.isArray(data) ? data : data.findings;
      if (!Array.isArray(list)) continue;
      const findings = list
        .filter((f) => f && typeof f.path === 'string' && Number.isInteger(Number(f.line)))
        .map((f) => ({
          path: f.path.replace(/^\.?\//, '').replace(/^[ab]\//, ''),
          line: Number(f.line),
          start_line: Number.isInteger(Number(f.start_line)) ? Number(f.start_line) : null,
          rule_id: String(f.rule_id || f.category || f.title || 'finding'),
          symbol: String(f.symbol || ''),
          category: String(f.category || ''),
          severity: String(f.severity || '').toLowerCase(),
          title: String(f.title || 'Compose performance issue'),
          explanation: String(f.explanation || ''),
          suggestion: typeof f.suggestion === 'string' ? f.suggestion.replace(/\n+$/, '') : '',
          fix_snippet: typeof f.fix_snippet === 'string' ? f.fix_snippet.replace(/\n+$/, '') : '',
        }));
      return { overview: stripHeading(overview).trim(), findings, parsed: true };
    } catch (e) {
      // Try the next candidate.
    }
  }
  return { overview: '', findings: [], parsed: false };
}

function stripHeading(text) {
  return (text || '').replace(/^\s*#{1,2}\s*⚡?\s*Jetpack Compose Performance Review\s*\n/i, '').trim();
}

function fingerprintOf(f) {
  // Line numbers shift between pushes, so key on file + enclosing symbol + rule.
  const key = [f.path, f.symbol, f.rule_id].map((s) => String(s).trim().toLowerCase()).join('|');
  return crypto.createHash('sha1').update(key).digest('hex').slice(0, 12);
}

// ---- Diff anchoring -----------------------------------------------------------------------------

async function buildDiffIndex(github, owner, repo, prNumber) {
  const files = await github.paginate(github.rest.pulls.listFiles, {
    owner, repo, pull_number: prNumber, per_page: 100,
  });
  const index = new Map();
  for (const file of files) {
    if (!file.patch) continue; // Binary or too large to annotate.
    index.set(file.filename, parsePatch(file.patch));
  }
  return index;
}

// Maps each right-side line that appears in the diff to the hunk it belongs to. Only these lines
// can receive review comments.
function parsePatch(patch) {
  const lines = new Map();
  let hunk = -1;
  let next = 0;
  for (const l of patch.split('\n')) {
    const header = /^@@ -\d+(?:,\d+)? \+(\d+)(?:,\d+)? @@/.exec(l);
    if (header) {
      hunk += 1;
      next = Number(header[1]);
      continue;
    }
    if (hunk < 0 || l.startsWith('-') || l.startsWith('\\')) continue;
    lines.set(next, hunk);
    next += 1;
  }
  return lines;
}

function anchorFinding(f, diffIndex) {
  const lines = diffIndex.get(f.path);
  if (!lines) return null;

  let end = f.line;
  let start = f.start_line && f.start_line < f.line ? f.start_line : null;
  let exact = lines.has(end) && (start === null || lines.get(start) === lines.get(end));

  if (!exact) {
    // Snap to the last commentable line within the reported range.
    const from = start ?? end;
    end = null;
    for (let n = f.line; n >= from; n--) {
      if (lines.has(n)) { end = n; break; }
    }
    if (end === null) return null;
    start = null;
  }
  return { path: f.path, line: end, start_line: start, exact };
}

// ---- Inline comments ----------------------------------------------------------------------------

async function listExistingFindingComments(github, owner, repo, prNumber) {
  const comments = await github.paginate(github.rest.pulls.listReviewComments, {
    owner, repo, pull_number: prNumber, per_page: 100,
  });
  const map = new Map();
  for (const c of comments) {
    const m = FINDING_MARKER_RE.exec(c.body || '');
    if (m && !map.has(m[1])) map.set(m[1], c.html_url);
  }
  return map;
}

function renderInlineBody(row) {
  const { finding: f, fingerprint, anchor } = row;
  const severity = { high: '🔴', medium: '🟠', low: '🟡' }[f.severity] || '⚡';
  const parts = [`<!-- compose-performance-review:finding ${fingerprint} -->`];
  parts.push(`${severity} **${f.title}**`);
  if (f.category) parts.push(`_${f.category}_`);
  if (f.explanation) parts.push(f.explanation);

  // A suggestion block replaces exactly the commented range, so only use it when the anchor
  // matches what the model reported.
  if (f.suggestion && anchor.exact) {
    parts.push('```suggestion\n' + f.suggestion + '\n```');
  } else if (f.suggestion || f.fix_snippet) {
    parts.push('**Suggested fix:**\n\n```kotlin\n' + (f.fix_snippet || f.suggestion) + '\n```');
  }
  parts.push('<sub>Generated by the `jetpack-compose-performance` skill. This review is advisory.</sub>');
  return parts.join('\n\n');
}

function toReviewComment(row) {
  const c = { path: row.anchor.path, line: row.anchor.line, side: 'RIGHT', body: renderInlineBody(row) };
  if (row.anchor.start_line) {
    c.start_line = row.anchor.start_line;
    c.start_side = 'RIGHT';
  }
  return c;
}

// Returns rows that could not be posted inline.
async function postInlineComments(github, core, { owner, repo, prNumber, headSha, rows }) {
  if (rows.length === 0) return [];
  const body = `Found ${rows.length} new potential Jetpack Compose performance ${plural(rows.length, 'issue')}. ` +
    'The overall status is kept up to date in the **Jetpack Compose Performance Review** summary comment.';
  try {
    await github.rest.pulls.createReview({
      owner, repo, pull_number: prNumber, commit_id: headSha, event: 'COMMENT', body,
      comments: rows.map(toReviewComment),
    });
    return [];
  } catch (e) {
    // A single invalid anchor rejects the whole review; fall back to one comment at a time.
    core.warning(`Batch review failed (${e.message}); posting comments individually.`);
  }
  const failed = [];
  for (const row of rows) {
    try {
      await github.rest.pulls.createReviewComment({
        owner, repo, pull_number: prNumber, commit_id: headSha, ...toReviewComment(row),
      });
    } catch (e) {
      core.warning(`Could not comment on ${row.anchor.path}:${row.anchor.line}: ${e.message}`);
      failed.push(row);
    }
  }
  return failed;
}

// ---- Sticky summary comment ---------------------------------------------------------------------

async function resolveBotLogin(github) {
  try {
    const { data } = await github.rest.users.getAuthenticated();
    return data.login;
  } catch (e) {
    return 'github-actions[bot]'; // GITHUB_TOKEN cannot call /user.
  }
}

async function findStickyComment(github, owner, repo, prNumber, botLogin) {
  const comments = await github.paginate(github.rest.issues.listComments, {
    owner, repo, issue_number: prNumber, per_page: 100,
  });
  const ours = comments.filter((c) => (c.body || '').includes(SUMMARY_MARKER));
  const comment = ours.find((c) => c.user?.login === botLogin) || ours[0] || null;
  // Comments posted by older versions of this workflow (one per run, no marker).
  const legacy = comments.filter((c) =>
    c.user?.login === botLogin &&
    !(c.body || '').includes(SUMMARY_MARKER) &&
    (c.body || '').trimStart().startsWith(HEADING));
  return { comment, legacy };
}

async function upsertSticky(github, owner, repo, prNumber, sticky, body) {
  if (sticky.comment) {
    await github.rest.issues.updateComment({ owner, repo, comment_id: sticky.comment.id, body });
    return;
  }
  const { data } = await github.rest.issues.createComment({ owner, repo, issue_number: prNumber, body });
  sticky.comment = data;
  // First sticky comment on this PR: collapse the per-run comments left by older versions of
  // this workflow.
  for (const c of sticky.legacy) {
    try {
      await github.graphql(
        'mutation($id: ID!) { minimizeComment(input: {subjectId: $id, classifier: OUTDATED}) { clientMutationId } }',
        { id: c.node_id });
    } catch (e) {
      // Best effort.
    }
  }
}

function extractResults(body) {
  if (!body) return '';
  const i = body.indexOf(RESULTS_MARKER);
  if (i < 0) return '';
  return body.slice(i + RESULTS_MARKER.length)
    .replace(/^\s*> \[!NOTE\]\n(> .*\n)+\n?/, '') // Drop a stale note from a previous run.
    .trim();
}

function renderSticky({ status, runUrl, results = '' }) {
  const updated = new Date().toISOString().replace('T', ' ').replace(/\.\d+Z$/, ' UTC');
  return [
    SUMMARY_MARKER,
    HEADING,
    '',
    `**Status:** ${status}`,
    '',
    `<sub>Last updated ${updated} · [workflow run](${runUrl}) · ` +
      'This comment is updated on every push; findings on changed lines are posted as inline review comments.</sub>',
    '',
    RESULTS_MARKER,
    results,
  ].join('\n').trim() + '\n';
}

function renderResults({ overview, rows, unanchored, urls }) {
  const out = [];
  if (overview) out.push(overview, '');

  if (rows.length > 0) {
    out.push('| File | Line | Finding | Inline comment |', '|---|---|---|---|');
    for (const row of rows) {
      const f = row.finding;
      const url = urls.get(row.fingerprint);
      const link = url ? `[view](${url})` : row.state === 'outside-diff' ? 'outside the diff (details below)' : '—';
      const line = f.start_line && f.start_line < f.line ? `${f.start_line}–${f.line}` : `${f.line}`;
      out.push(`| \`${cell(baseName(f.path))}\` | ${line} | ${cell(f.title)} | ${link} |`);
    }
    out.push('');
  }

  if (unanchored.length > 0) {
    out.push('<details>', `<summary>${unanchored.length} ${plural(unanchored.length, 'finding')} that could not be posted inline</summary>`, '');
    for (const { finding: f } of unanchored) {
      out.push(`#### ${f.title}`, '', `\`${f.path}\` line ${f.line}${f.category ? ` · _${f.category}_` : ''}`, '');
      if (f.explanation) out.push(f.explanation, '');
      const fix = f.fix_snippet || f.suggestion;
      if (fix) out.push('```kotlin', fix, '```', '');
    }
    out.push('</details>');
  }
  return out.join('\n').trim();
}

function cell(s) {
  return String(s).replace(/\|/g, '\\|').replace(/\s*\n\s*/g, ' ');
}

function baseName(p) {
  return p.split('/').pop();
}

function plural(n, word) {
  return n === 1 ? word : `${word}s`;
}
