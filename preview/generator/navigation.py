"""
Navigation generator for Compose Previews site (left sidebar devsite-book-nav).
"""
import os
import sys

_DIR = os.path.dirname(os.path.abspath(__file__))
if _DIR not in sys.path:
    sys.path.insert(0, _DIR)

from component_data import CATEGORIES


def build_left_sidebar_html(current_page_id=None, is_root=False):
    """Generates the developer.android.com devsite-book-nav left sidebar with search filter."""
    overview_href = "index.html" if is_root else "../index.html"
    overview_active = " active" if is_root and current_page_id is None else ""
    theme_builder_href = "components/theme-builder.html" if is_root else "theme-builder.html"
    theme_builder_active = " active" if current_page_id == "theme-builder" else ""
    html_parts = []
    html_parts.append(f"""<nav class="devsite-book-nav" id="bookNav" aria-label="Component Navigation">
        <div class="nav-filter-box">
            <span class="nav-filter-icon">&#128269;</span>
            <input type="text" id="navFilterInput" placeholder="Filter components..." aria-label="Filter components" oninput="filterNav(this.value)">
        </div>
        <div class="nav-tree">
            <div class="nav-tree-section">
                <a href="{overview_href}" class="nav-overview-link{overview_active}">
                    <span>&#9638;</span>
                    <span>Components Overview</span>
                </a>
                <a href="{theme_builder_href}" class="nav-overview-link{theme_builder_active}">
                    <span>Theme Builder</span>
                </a>
            </div>
    """)

    for cat in CATEGORIES:
        html_parts.append(f"""
            <div class="nav-tree-category" data-category="{cat['id']}">
                <div class="nav-category-header">
                    <span class="nav-category-title">{cat['name']}</span>
                </div>
                <ul class="nav-category-list">
        """)
        for comp in cat["components"]:
            examples = comp.get("examples", [])
            is_comp_active = comp["id"] == current_page_id
            comp_active = " active" if is_comp_active else ""
            comp_href = f"components/{comp['id']}.html" if is_root else f"{comp['id']}.html"

            if len(examples) > 1:
                is_comp_active = comp["id"] == current_page_id
                is_child_active = any(ex["id"] == current_page_id for ex in examples)
                is_expanded = is_comp_active or is_child_active
                sub_style = "display: block;" if is_expanded else "display: none;"
                arrow_char = "&#9662;" if is_expanded else "&#9656;"

                html_parts.append(f"""
                    <li class="nav-item nav-item-parent" data-title="{comp['title'].lower()}">
                        <div class="nav-parent-row">
                            <a href="{comp_href}" class="nav-link{comp_active}" onclick="handleParentNavClick(this, event)">
                                <span class="nav-link-title">{comp['title']}</span>
                            </a>
                            <button type="button" class="nav-sub-toggle" onclick="toggleNavSub(this, event)" aria-label="Toggle {comp['title']} sub options">
                                <span class="nav-sub-arrow">{arrow_char}</span>
                            </button>
                        </div>
                        <ul class="nav-sub-list" style="{sub_style}">
                """)
                for ex in examples:
                    is_ex_active = ex["id"] == current_page_id
                    ex_active = " active" if is_ex_active else ""
                    ex_href = f"components/{ex['id']}.html" if is_root else f"{ex['id']}.html"
                    html_parts.append(f"""
                            <li class="nav-sub-item" data-title="{ex['title'].lower()}">
                                <a href="{ex_href}" class="nav-sub-link{ex_active}">
                                    {ex['title']}
                                </a>
                            </li>
                    """)
                html_parts.append("""
                        </ul>
                    </li>
                """)
            else:
                html_parts.append(f"""
                    <li class="nav-item" data-title="{comp['title'].lower()}">
                        <a href="{comp_href}" class="nav-link{comp_active}">
                            {comp['title']}
                        </a>
                    </li>
                """)
        html_parts.append("""
                </ul>
            </div>
        """)

    html_parts.append("""
        </div>
    </nav>
    """)
    return "".join(html_parts)
