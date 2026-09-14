## Summary

Extracts the Kotlin samples from four background-work guides into `:backgroundwork` (`com.example.snippets.backgroundwork`) as region-tagged source, so the guides can import them instead of hardcoding them. Wake-lock samples for the same DAC area already live in that module.

**20 snippets across 4 pages. 16 publish exactly what the page shows. 4 differ, every one listed below with a reason.** Line wrapping is not counted as a difference: `spotlessApply` runs before submission and rewraps long calls without changing what a reader reads.

Region tags all begin `android_background_`; the tables drop that prefix.

### How to read the "why" column

| Why | What a reader of the page would see |
|---|---|
| **`/* ... */`** | The page prints `...`, which Kotlin will not compile. The region publishes the same ellipsis inside a block comment. |
| **hardcoded snippet defect fixed** | The published Kotlin does not compile, and this is not a sample meant to demonstrate an error. The region publishes the corrected line. |

## Per page

### 1. Keep the screen on

https://developer.android.com/develop/background-work/background-tasks/awake/screen-on

1 snippet, all 1 match as published: `awake_screen_on`.

`MainActivity` is registered in this module’s manifest (`MAIN` / `LAUNCHER`). `setContentView(R.layout.activity_main)` inflates a compile-only `FrameLayout` stub in `:backgroundwork`; that layout is not a published region.

### 2. System restrictions on background tasks

https://developer.android.com/develop/background-work/background-tasks/bg-work-restrictions

2 snippets, 1 match, 1 differ.

| Snippet | Page section | Difference | Why |
|---|---|---|---|
| `restrictions_worker_triggered_content` | Determine which content authorities triggered work | Adds the missing class-body `{`; `private val params` so `params.triggeredContentAuthorities` / `params.triggeredContentUris` compile | hardcoded snippet defect fixed |

Matching as published: `restrictions_schedule_work`.

### 3. Testing Worker implementation

https://developer.android.com/develop/background-work/background-tasks/testing/persistent/worker-impl

8 snippets, all 8 match as published: `testing_sleep_worker_basic`, `testing_sleep_worker_test_basic`, `testing_sleep_worker_input_data`, `testing_sleep_worker_test_input_data`, `testing_coroutine_worker`, `testing_coroutine_worker_test`, `testing_rx_worker`, `testing_rx_worker_test`.

These snippets live in `:backgroundwork` `androidTest`. Catalog aliases `androidx-work-testing` and `androidx-work-rxjava2` share `version.ref = "androidx-work-runtime"` and are `androidTestImplementation`, with `androidx.test.ext:junit` / `core` / `runner` on the same configuration. Comments outside the regions note that each `SleepWorker` would normally live in `src/main`.

### 4. Schedule alarms

https://developer.android.com/develop/background-work/services/alarms

9 snippets, 6 match, 3 differ.

| Snippet | Page section | Difference | Why |
|---|---|---|---|
| `alarms_cancel_service` | Set a repeating alarm | `PendingIntent.FLAG_NO_CREATE or FLAG_IMMUTABLE` instead of `FLAG_NO_CREATE` alone | hardcoded snippet defect fixed |
| `alarms_elapsed_realtime_one_time` | Examples of elapsed real time alarms | `/* ... */` for the page’s `...`; `getBroadcast(..., FLAG_IMMUTABLE)` instead of flag `0` | `/* ... */`, hardcoded snippet defect fixed |
| `alarms_rtc_repeating_precise` | Examples of real time clock alarms | same as one-time elapsed | `/* ... */`, hardcoded snippet defect fixed |

Matching as published: `alarms_boot_receiver`, `alarms_elapsed_realtime_repeating`, `alarms_rtc_repeating`, `alarms_cancel_direct`, `alarms_enable_receiver`, `alarms_disable_receiver`.

The two snippets that start with `private var alarmMgr` keep those as class properties. A compile-only `fun schedule()` (silent exclude) holds the statements that follow so they are legal Kotlin; it does not appear on the page. Those property lines are indented to left-align with the `schedule()` body so the published block does not look like top-level statements under the fields.

From API 31 a `PendingIntent` must set `FLAG_IMMUTABLE` or `FLAG_MUTABLE`; these samples do not need a mutable intent.

## Not extracted

Kotlin only. Screen-on, Worker testing, and Alarms pages that have Java twins leave those twins hardcoded for the page edit that retires them.

The keep-the-screen-on XML layout sample (`RelativeLayout` with `android:keepScreenOn="true"`) was not extracted in the skill, so it stays hardcoded.

Manifest samples (exact-alarm permissions, boot receiver, FGS `foregroundServiceType` / `FOREGROUND_SERVICE*` permissions) stay hardcoded per migration criteria (AndroidManifest outside `/build/`).

## Live snippet fixes this surfaced

1. **System restrictions:** `MyWorker` is missing `{` after the constructor. Constructor `params` is not a property, so `params.triggeredContentAuthorities` / `params.triggeredContentUris` in `doWork` would not compile. The region adds `private val params` and keeps those `WorkerParameters` calls.
2. **Schedule alarms:** `PendingIntent.getService` / `getBroadcast` without a mutability flag (`FLAG_NO_CREATE` alone, or `0`). Invalid from API 31.

## Verification

- `./gradlew :backgroundwork:compileDebugKotlin` — passes
- `./gradlew :backgroundwork:compileDebugAndroidTestKotlin` — passes
- `./gradlew :backgroundwork:lintDebug` — passes
- `./gradlew spotlessApply` — run before submitting; the branch is clean afterwards
