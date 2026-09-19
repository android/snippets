#!/usr/bin/env python3
"""
Generate separate dedicated HTML pages for each Composable snippet matching developer.android.com branding.
Copies content, explanations, and structure from developer.android.com/develop/ui/compose/components.
Includes:
- Each example broken out into its own dedicated page (e.g. SingleChoiceSegmentedButton vs MultiChoiceSegmentedButton)
- Left side navigation tab (devsite-book-nav) with component category tree & search filter
- Right side page ordering tab (devsite-page-nav / "On this page") with scrollspy
- Code snippets extracted DIRECTLY from compose/snippets without duplication
- Embedded interactive WASM runner (inline on page, no dialogs)
- Mapping to github.com/android/snippets repository
"""

import os
import re
import json
import html
import urllib.parse

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
ROOT_DIR = os.path.abspath(os.path.join(SCRIPT_DIR, "../.."))
SNIPPETS_SRC_DIR = os.path.join(ROOT_DIR, "compose/snippets/src/main/java/com/example/compose/snippets/components")
RESOURCES_DIR = os.path.join(ROOT_DIR, "preview/wasm/src/wasmJsMain/resources")
COMPONENTS_DIR = os.path.join(RESOURCES_DIR, "components")
DIST_COMPONENTS_DIR = os.path.join(ROOT_DIR, "preview/wasm/build/dist/site/components")

os.makedirs(COMPONENTS_DIR, exist_ok=True)
os.makedirs(DIST_COMPONENTS_DIR, exist_ok=True)

# Load cached developer.android.com content if available
DEV_CONTENT_FILE = "/Users/riggaroo/.gemini/jetski/brain/d9eed6b8-a904-4525-b8d0-3fd6e5cc9450/scratch/android_dev_content.json"
DEV_CONTENT = {}
if os.path.exists(DEV_CONTENT_FILE):
    try:
        with open(DEV_CONTENT_FILE, "r", encoding="utf-8") as f:
            DEV_CONTENT = json.load(f)
    except Exception:
        DEV_CONTENT = {}

CATEGORIES = [
    {
        "id": "actions",
        "name": "Actions",
        "components": [
            {
                "id": "segmented-button",
                "title": "Segmented button",
                "file": "SegmentedButton.kt",
                "description": "Segmented buttons help users select options, switch views, or sort elements within a unified horizontal row.",
                "when_to_use": [
                    "<strong>View Switching:</strong> Use to switch between 2 to 5 distinct views (e.g., 'Day', 'Week', 'Month').",
                    "<strong>Attribute Toggles:</strong> Use to toggle multiple independent attributes (e.g., 'Walk', 'Ride', 'Drive').",
                    "<strong>Alternative to Tabs:</strong> Prefer segmented buttons over tabs inside cards or sub-sections where tabs would feel too heavyweight."
                ],
                "how_to_use": [
                    "Wrap segments inside <code>SingleChoiceSegmentedButtonRow</code> or <code>MultiChoiceSegmentedButtonRow</code>.",
                    "Use <code>SegmentedButtonDefaults.itemShape(index, count)</code> to ensure proper corner rounding.",
                    "Pass <code>selected = (index == selectedIndex)</code> or <code>checked = selectedOptions[index]</code>.",
                    "Provide clear label text or icons inside the segment content slot."
                ],
                "api_highlights": [
                    {"param": "selected / checked", "type": "Boolean", "desc": "Controls active selection state."},
                    {"param": "onClick / onCheckedChange", "type": "() -> Unit", "desc": "Callback invoked on interaction."},
                    {"param": "shape", "type": "Shape", "desc": "Corner rounding shape via SegmentedButtonDefaults.itemShape."},
                    {"param": "label", "type": "@Composable () -> Unit", "desc": "Content slot for text label."},
                    {"param": "icon", "type": "@Composable () -> Unit", "desc": "Optional icon slot for selection indicator."}
                ],
                "examples": [
                    {
                        "id": "single-choice-segmented-button",
                        "title": "Single-choice segmented button",
                        "tag": "android_compose_components_singlechoicesegmentedbutton",
                        "description": "A single-choice segmented button row presents mutually exclusive options where only one option can be selected at a time.",
                        "when_to_use": [
                            "<strong>Mutually Exclusive Choices:</strong> Use when users must select exactly one option from 2 to 5 alternatives (e.g., 'Day', 'Month', 'Week').",
                            "<strong>View Switching:</strong> Ideal for calendar view modes, filter modes, or sort order selection."
                        ],
                        "how_to_use": [
                            "Wrap segments inside <code>SingleChoiceSegmentedButtonRow</code>.",
                            "Maintain a <code>selectedIndex</code> state variable with <code>remember { mutableIntStateOf(0) }</code>.",
                            "Pass <code>selected = (index == selectedIndex)</code> to indicate the active state.",
                            "Use <code>SegmentedButtonDefaults.itemShape(index, count)</code> for adaptive corner radius."
                        ],
                        "api_highlights": [
                            {"param": "selected", "type": "Boolean", "desc": "Whether this segment is currently selected."},
                            {"param": "onClick", "type": "() -> Unit", "desc": "Callback invoked when this segment is clicked."},
                            {"param": "shape", "type": "Shape", "desc": "The shape of the segment, computed by SegmentedButtonDefaults.itemShape."},
                            {"param": "label", "type": "@Composable () -> Unit", "desc": "Content slot for the text label."}
                        ]
                    },
                    {
                        "id": "multi-choice-segmented-button",
                        "title": "Multi-choice segmented button",
                        "tag": "android_compose_components_multichoicesegmentedbutton",
                        "description": "A multi-choice segmented button row allows users to select multiple options independently within a unified horizontal row, with optional check icons.",
                        "when_to_use": [
                            "<strong>Independent Toggles:</strong> Use when multiple options can be active simultaneously (e.g., 'Walk', 'Ride', 'Drive', or text formatting 'Bold', 'Italic').",
                            "<strong>Filter Groups:</strong> Use to apply multiple active filters to a data set."
                        ],
                        "how_to_use": [
                            "Wrap segments inside <code>MultiChoiceSegmentedButtonRow</code>.",
                            "Maintain selection state using a <code>mutableStateListOf</code> or boolean states.",
                            "Pass <code>checked = selectedOptions[index]</code> and toggle in <code>onCheckedChange</code>.",
                            "Use <code>icon = { SegmentedButtonDefaults.Icon(selectedOptions[index]) }</code> to show animated check icons."
                        ],
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Whether this segment is currently checked."},
                            {"param": "onCheckedChange", "type": "(Boolean) -> Unit", "desc": "Callback invoked when check state changes."},
                            {"param": "shape", "type": "Shape", "desc": "The shape computed by SegmentedButtonDefaults.itemShape."},
                            {"param": "icon", "type": "@Composable () -> Unit", "desc": "Optional icon slot for checkmark indicator."},
                            {"param": "label", "type": "@Composable () -> Unit", "desc": "Content slot for the segment label or icon."}
                        ]
                    }
                ]
            },
            {
                "id": "button-examples",
                "title": "Button",
                "file": "Button.kt",
                "description": "Buttons allow users to trigger a defined action with a single tap. Material 3 provides five distinct types: Filled, Filled Tonal, Elevated, Outlined, and Text.",
                "when_to_use": [
                    "<strong>Filled:</strong> Highest-emphasis action on a screen (e.g., 'Save', 'Submit').",
                    "<strong>Filled Tonal:</strong> Secondary action requiring prominence without competing with primary.",
                    "<strong>Elevated:</strong> When shadow separation is needed against patterned backgrounds.",
                    "<strong>Outlined:</strong> Secondary, non-destructive actions contrasting with filled.",
                    "<strong>Text:</strong> Lowest-emphasis actions, dialog dismissals, or card actions."
                ],
                "how_to_use": [
                    "Supply an <code>onClick: () -> Unit</code> lambda to execute the button action.",
                    "Place content inside the button slot, typically a <code>Text</code> composable and optional <code>Icon</code>.",
                    "Ensure minimum interactive touch target is 48x48 dp for accessibility."
                ],
                "api_highlights": [
                    {"param": "onClick", "type": "() -> Unit", "desc": "Callback invoked when button is clicked."},
                    {"param": "enabled", "type": "Boolean", "desc": "Controls enabled state."},
                    {"param": "colors", "type": "ButtonColors", "desc": "Container and content colors."},
                    {"param": "elevation", "type": "ButtonElevation?", "desc": "Elevation values for resting/pressed states."}
                ],
                "examples": [
                    {
                        "id": "filled-button",
                        "title": "Filled button",
                        "tag": "android_compose_components_filledbutton",
                        "description": "Filled buttons have the highest visual impact and are used for the primary, most important action on a screen.",
                        "when_to_use": ["Use for the primary action on a screen (e.g., 'Submit', 'Continue', 'Save'). There should typically be only one filled button per screen."],
                        "how_to_use": ["Call <code>Button(onClick = { ... }) { Text(\"Filled\") }</code>."],
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                            {"param": "colors", "type": "ButtonColors", "desc": "Defaults to MaterialTheme.colorScheme.primary container."}
                        ]
                    },
                    {
                        "id": "filled-tonal-button",
                        "title": "Filled tonal button",
                        "tag": "android_compose_components_filledtonalbutton",
                        "description": "Filled tonal buttons are an alternative middle ground between filled and outlined buttons, offering visual prominence for secondary actions.",
                        "when_to_use": ["Use for secondary actions that need prominence without competing with the primary filled button (e.g., 'Next step', 'Add item')."],
                        "how_to_use": ["Call <code>FilledTonalButton(onClick = { ... }) { Text(\"Tonal\") }</code>."],
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                            {"param": "colors", "type": "ButtonColors", "desc": "Defaults to secondaryContainer."}
                        ]
                    },
                    {
                        "id": "elevated-button",
                        "title": "Elevated button",
                        "tag": "android_compose_components_elevatedbutton",
                        "description": "Elevated buttons are high-emphasis buttons with a drop shadow, useful on patterned or complex backgrounds.",
                        "when_to_use": ["Use when buttons require elevation separation from textured backgrounds."],
                        "how_to_use": ["Call <code>ElevatedButton(onClick = { ... }) { Text(\"Elevated\") }</code>."],
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                            {"param": "elevation", "type": "ButtonElevation?", "desc": "Shadow elevation."}
                        ]
                    },
                    {
                        "id": "outlined-button",
                        "title": "Outlined button",
                        "tag": "android_compose_components_outlinedbutton",
                        "description": "Outlined buttons are medium-emphasis buttons with a stroke outline, used for secondary actions like 'Cancel' or 'Back'.",
                        "when_to_use": ["Use for secondary actions contrasting with filled buttons (e.g., 'Cancel', 'Back', 'Save draft')."],
                        "how_to_use": ["Call <code>OutlinedButton(onClick = { ... }) { Text(\"Outlined\") }</code>."],
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                            {"param": "border", "type": "BorderStroke?", "desc": "Border stroke customization."}
                        ]
                    },
                    {
                        "id": "text-button",
                        "title": "Text button",
                        "tag": "android_compose_components_textbutton",
                        "description": "Text buttons have the lowest visual impact and are used for lowest-emphasis actions, dialog dismissals, or inside cards.",
                        "when_to_use": ["Use in dialog dismissals ('Cancel', 'Dismiss') or inside cards where buttons should not distract from content."],
                        "how_to_use": ["Call <code>TextButton(onClick = { ... }) { Text(\"Text Button\") }</code>."],
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."}
                        ]
                    },
                    {
                        "id": "button-with-animated-shape",
                        "title": "Button with animated shape",
                        "tag": "android_compose_components_buttonwithanimatedshape",
                        "description": "Material 3 Expressive button featuring dynamic animated shape morphing on press, hover, and focus states.",
                        "when_to_use": ["Use in Material 3 Expressive UI designs where tactile feedback and lively motion are desired."],
                        "how_to_use": ["Pass <code>shapes = ButtonDefaults.shapes()</code> to <code>Button(...)</code>."],
                        "api_highlights": [
                            {"param": "shapes", "type": "ButtonShapes", "desc": "Expressive animated shape set via ButtonDefaults.shapes()."}
                        ]
                    },
                    {
                        "id": "square-button",
                        "title": "Square button",
                        "tag": "android_compose_components_squarebutton",
                        "description": "Material 3 Expressive square button with subtle corner rounding, ideal for compact grids and toolbars.",
                        "when_to_use": ["Use in toolbars, grid layouts, or expressive designs where compact square buttons fit better."],
                        "how_to_use": ["Pass <code>shape = ButtonDefaults.squareShape</code> to <code>Button(...)</code>."],
                        "api_highlights": [
                            {"param": "shape", "type": "Shape", "desc": "Square shape via ButtonDefaults.squareShape."}
                        ]
                    },
                    {
                        "id": "button-with-icon",
                        "title": "Button with icon",
                        "tag": "android_compose_components_buttonwithicon",
                        "description": "Material 3 Expressive button with icon and medium container height specification.",
                        "when_to_use": ["Use when actions are reinforced with a leading icon and expressive height styling."],
                        "how_to_use": ["Call <code>Button(contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight, hasStartIcon = true))</code>."],
                        "api_highlights": [
                            {"param": "contentPadding", "type": "PaddingValues", "desc": "Calculated via ButtonDefaults.contentPaddingFor()."}
                        ]
                    },
                    {
                        "id": "split-button",
                        "title": "Split button",
                        "tag": "android_compose_components_splitbutton",
                        "description": "Material 3 Expressive split button offering a primary action on the leading side and a dropdown toggle on the trailing side.",
                        "when_to_use": ["Use when a primary action has related secondary options (e.g. 'Save' with 'Save as draft', 'Export as PDF')."],
                        "how_to_use": ["Use <code>SplitButtonLayout(leadingButton = { ... }, trailingButton = { ... })</code>."],
                        "api_highlights": [
                            {"param": "leadingButton", "type": "@Composable () -> Unit", "desc": "Leading primary action button."},
                            {"param": "trailingButton", "type": "@Composable () -> Unit", "desc": "Trailing dropdown/toggle button."}
                        ]
                    },
                    {
                        "id": "button-group",
                        "title": "Connected button group",
                        "tag": "android_compose_components_buttongroup",
                        "description": "Material 3 Expressive connected button group holding multiple toggle buttons with unified connected shapes.",
                        "when_to_use": ["Use for connected filters, view toggles, or multi-option selectors."],
                        "how_to_use": ["Wrap <code>ToggleButton</code> components inside <code>ButtonGroup { ... }</code>."],
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Active toggle state."},
                            {"param": "onCheckedChange", "type": "(Boolean) -> Unit", "desc": "Toggle state callback."}
                        ]
                    },
                    {
                        "id": "toggle-button",
                        "title": "Toggle button",
                        "tag": "android_compose_expressive_components_filledtogglebutton",
                        "description": "Material 3 Expressive filled toggle button switching between active and inactive states.",
                        "when_to_use": ["Use for binary state selection with high visual prominence."],
                        "how_to_use": ["Call <code>ToggleButton(checked = checked, onCheckedChange = { checked = it }) { Text(...) }</code>."],
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Toggle state."},
                            {"param": "onCheckedChange", "type": "(Boolean) -> Unit", "desc": "Toggle callback."}
                        ]
                    },
                    {
                        "id": "elevated-toggle-button",
                        "title": "Elevated toggle button",
                        "tag": "android_compose_expressive_components_elevatedtogglebutton",
                        "description": "Material 3 Expressive elevated toggle button with drop shadow elevation.",
                        "when_to_use": ["Use when elevated separation is needed on complex or patterned backgrounds."],
                        "how_to_use": ["Call <code>ElevatedToggleButton(checked = checked, onCheckedChange = { ... }) { Text(...) }</code>."],
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Toggle state."},
                            {"param": "onCheckedChange", "type": "(Boolean) -> Unit", "desc": "Toggle callback."}
                        ]
                    },
                    {
                        "id": "tonal-toggle-button",
                        "title": "Tonal toggle button",
                        "tag": "android_compose_expressive_components_tonaltogglebutton",
                        "description": "Material 3 Expressive tonal toggle button with medium emphasis secondary container styling.",
                        "when_to_use": ["Use for secondary toggle actions."],
                        "how_to_use": ["Call <code>TonalToggleButton(checked = checked, onCheckedChange = { ... }) { Text(...) }</code>."],
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Toggle state."},
                            {"param": "onCheckedChange", "type": "(Boolean) -> Unit", "desc": "Toggle callback."}
                        ]
                    },
                    {
                        "id": "outlined-toggle-button",
                        "title": "Outlined toggle button",
                        "tag": "android_compose_expressive_components_outlinedtogglebutton",
                        "description": "Material 3 Expressive outlined toggle button with subtle stroke border.",
                        "when_to_use": ["Use for secondary toggle actions without fill background."],
                        "how_to_use": ["Call <code>OutlinedToggleButton(checked = checked, onCheckedChange = { ... }) { Text(...) }</code>."],
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Toggle state."},
                            {"param": "onCheckedChange", "type": "(Boolean) -> Unit", "desc": "Toggle callback."}
                        ]
                    },
                    {
                        "id": "button-with-icon-sample",
                        "title": "Button with icon",
                        "tag": "android_compose_expressive_components_buttonwithicon",
                        "description": "Material 3 Expressive standard button featuring a start icon with adaptive spacing.",
                        "when_to_use": ["Use when icons reinforce button meaning and action context."],
                        "how_to_use": ["Call <code>Button(contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight, hasStartIcon = true)) { Icon(...); Spacer(...); Text(...) }</code>."],
                        "api_highlights": [
                            {"param": "contentPadding", "type": "PaddingValues", "desc": "Calculated via ButtonDefaults.contentPaddingFor."}
                        ]
                    },
                    {
                        "id": "toggle-button-with-icon",
                        "title": "Toggle button with icon",
                        "tag": "android_compose_expressive_components_togglebuttonwithicon",
                        "description": "Material 3 Expressive toggle button displaying dynamic checked/unchecked icons.",
                        "when_to_use": ["Use for toggleable favorite or like actions."],
                        "how_to_use": ["Call <code>ToggleButton(checked = checked, onCheckedChange = { ... }) { Icon(if (checked) Icons.Filled... else Icons.Outlined...); Text(...) }</code>."],
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Toggle state."}
                        ]
                    },
                    {
                        "id": "xsmall-button-with-icon",
                        "title": "Extra small button with icon",
                        "tag": "android_compose_expressive_components_xmsallbuttonwithicon",
                        "description": "Material 3 Expressive compact extra-small button designed for dense layouts.",
                        "when_to_use": ["Use in compact cards, tables, or dense toolbars."],
                        "how_to_use": ["Set <code>modifier = Modifier.heightIn(ButtonDefaults.ExtraSmallContainerHeight)</code>."],
                        "api_highlights": [
                            {"param": "heightIn", "type": "Dp", "desc": "ButtonDefaults.ExtraSmallContainerHeight"}
                        ]
                    },
                    {
                        "id": "xsmall-toggle-button-with-icon",
                        "title": "Extra small toggle button with icon",
                        "tag": "android_compose_expressive_components_xmsalltogglebuttonwithicon",
                        "description": "Material 3 Expressive compact extra-small toggle button with adaptive shape.",
                        "when_to_use": ["Use in dense toggle groups."],
                        "how_to_use": ["Pass <code>shapes = ToggleButtonDefaults.shapesFor(ButtonDefaults.ExtraSmallContainerHeight)</code>."],
                        "api_highlights": [
                            {"param": "shapes", "type": "ToggleButtonShapes", "desc": "Shapes sized for extra small container."}
                        ]
                    },
                    {
                        "id": "medium-button-with-icon",
                        "title": "Medium button with icon",
                        "tag": "android_compose_expressive_components_mediumbuttonwithicon",
                        "description": "Material 3 Expressive medium height button with typography and icon sizing.",
                        "when_to_use": ["Use for standard emphasis actions in expressive designs."],
                        "how_to_use": ["Set <code>modifier = Modifier.heightIn(ButtonDefaults.MediumContainerHeight)</code>."],
                        "api_highlights": [
                            {"param": "heightIn", "type": "Dp", "desc": "ButtonDefaults.MediumContainerHeight"}
                        ]
                    },
                    {
                        "id": "medium-toggle-button-with-icon",
                        "title": "Medium toggle button with icon",
                        "tag": "android_compose_expressive_components_mediumtogglebuttonwithicon",
                        "description": "Material 3 Expressive medium height toggle button.",
                        "when_to_use": ["Use for expressive medium toggle controls."],
                        "how_to_use": ["Pass <code>shapes = ToggleButtonDefaults.shapesFor(ButtonDefaults.MediumContainerHeight)</code>."],
                        "api_highlights": [
                            {"param": "shapes", "type": "ToggleButtonShapes", "desc": "Shapes for medium container height."}
                        ]
                    },
                    {
                        "id": "large-button-with-icon",
                        "title": "Large button with icon",
                        "tag": "android_compose_expressive_components_largebuttonwithicon",
                        "description": "Material 3 Expressive large container height button for prominent actions.",
                        "when_to_use": ["Use on hero landing sections or large screen layouts."],
                        "how_to_use": ["Set <code>modifier = Modifier.heightIn(ButtonDefaults.LargeContainerHeight)</code>."],
                        "api_highlights": [
                            {"param": "heightIn", "type": "Dp", "desc": "ButtonDefaults.LargeContainerHeight"}
                        ]
                    },
                    {
                        "id": "large-toggle-button-with-icon",
                        "title": "Large toggle button with icon",
                        "tag": "android_compose_expressive_components_largetogglebuttonwithicon",
                        "description": "Material 3 Expressive large container height toggle button.",
                        "when_to_use": ["Use for prominent toggle selectors on tablets and large screens."],
                        "how_to_use": ["Pass <code>shapes = ToggleButtonDefaults.shapesFor(ButtonDefaults.LargeContainerHeight)</code>."],
                        "api_highlights": [
                            {"param": "shapes", "type": "ToggleButtonShapes", "desc": "Shapes for large container height."}
                        ]
                    },
                    {
                        "id": "xlarge-button-with-icon",
                        "title": "Extra large button with icon",
                        "tag": "android_compose_expressive_components_xlargebuttonwithicon",
                        "description": "Material 3 Expressive extra large button for maximum prominence.",
                        "when_to_use": ["Use for primary call-to-actions on foldables and tablets."],
                        "how_to_use": ["Set <code>modifier = Modifier.heightIn(ButtonDefaults.ExtraLargeContainerHeight)</code>."],
                        "api_highlights": [
                            {"param": "heightIn", "type": "Dp", "desc": "ButtonDefaults.ExtraLargeContainerHeight"}
                        ]
                    },
                    {
                        "id": "xlarge-toggle-button-with-icon",
                        "title": "Extra large toggle button with icon",
                        "tag": "android_compose_expressive_components_xlargetogglebuttonwithicon",
                        "description": "Material 3 Expressive extra large toggle button.",
                        "when_to_use": ["Use for high-prominence toggle switches on large screens."],
                        "how_to_use": ["Pass <code>shapes = ToggleButtonDefaults.shapesFor(ButtonDefaults.ExtraLargeContainerHeight)</code>."],
                        "api_highlights": [
                            {"param": "shapes", "type": "ToggleButtonShapes", "desc": "Shapes for extra large container height."}
                        ]
                    },
                    {
                        "id": "square-toggle-button",
                        "title": "Square toggle button",
                        "tag": "android_compose_expressive_components_squaretogglebutton",
                        "description": "Material 3 Expressive square toggle button with morphing corner shapes.",
                        "when_to_use": ["Use in compact toolbars or square grid toggle panels."],
                        "how_to_use": ["Pass <code>shapes = ToggleButtonShapes(shape = ToggleButtonDefaults.squareShape, pressedShape = ToggleButtonDefaults.pressedShape, checkedShape = ToggleButtonDefaults.roundShape)</code>."],
                        "api_highlights": [
                            {"param": "shapes", "type": "ToggleButtonShapes", "desc": "Square, pressed, and checked shape configuration."}
                        ]
                    }
                ]
            },
            {
                "id": "floating-action-button",
                "title": "Floating action button",
                "file": "FloatingActionButton.kt",
                "description": "A Floating Action Button (FAB) performs the primary action on a screen. Available in standard, extended, small, and large sizes.",
                "when_to_use": [
                    "<strong>Primary Action:</strong> Single most important action (e.g. 'Compose', 'Add').",
                    "<strong>Extended FAB:</strong> When text improves comprehension.",
                    "<strong>Small FAB:</strong> Compact viewports.",
                    "<strong>Large FAB:</strong> Large screens and foldables."
                ],
                "how_to_use": [
                    "Anchor to a <code>Scaffold</code> using <code>floatingActionButton = { ... }</code>.",
                    "Provide an <code>Icon</code> inside with descriptive <code>contentDescription</code>."
                ],
                "api_highlights": [
                    {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                    {"param": "containerColor", "type": "Color", "desc": "Background color."}
                ],
                "examples": [
                    {
                        "id": "fab",
                        "title": "Floating action button",
                        "tag": "android_compose_components_fab",
                        "description": "Standard 56dp Floating Action Button performing the primary screen action.",
                        "when_to_use": ["Use for the primary action on a screen."],
                        "how_to_use": ["Call <code>FloatingActionButton(onClick = { ... }) { Icon(...) }</code>."],
                        "api_highlights": [{"param": "onClick", "type": "() -> Unit", "desc": "Callback."}]
                    },
                    {
                        "id": "extended-fab",
                        "title": "Extended floating action button",
                        "tag": "android_compose_components_extendedfab",
                        "description": "Extended FAB combining an icon and descriptive text label.",
                        "when_to_use": ["Use when an explicit label alongside the icon improves clarity."],
                        "how_to_use": ["Call <code>ExtendedFloatingActionButton(onClick = { ... }, icon = { ... }, text = { ... })</code>."],
                        "api_highlights": [
                            {"param": "icon", "type": "@Composable () -> Unit", "desc": "Leading icon."},
                            {"param": "text", "type": "@Composable () -> Unit", "desc": "Label text."}
                        ]
                    },
                    {
                        "id": "small-fab",
                        "title": "Small floating action button",
                        "tag": "android_compose_components_smallfab",
                        "description": "Compact FAB for secondary actions or compact viewports.",
                        "when_to_use": ["Use on small screens where standard 56dp FAB would obstruct content."],
                        "how_to_use": ["Call <code>SmallFloatingActionButton(onClick = { ... }) { Icon(...) }</code>."],
                        "api_highlights": [{"param": "onClick", "type": "() -> Unit", "desc": "Callback."}]
                    },
                    {
                        "id": "large-fab",
                        "title": "Large floating action button",
                        "tag": "android_compose_components_largefab",
                        "description": "96dp Large FAB for high prominence on tablets, foldables, and large desktop screens.",
                        "when_to_use": ["Use on large screens and foldables."],
                        "how_to_use": ["Call <code>LargeFloatingActionButton(onClick = { ... }) { Icon(...) }</code>."],
                        "api_highlights": [{"param": "onClick", "type": "() -> Unit", "desc": "Callback."}]
                    },
                    {
                        "id": "floating-toolbar",
                        "title": "Floating toolbar",
                        "tag": "android_compose_components_floatingtoolbar",
                        "description": "Material 3 Expressive horizontal floating toolbar combining quick actions and a vibrant floating action button in an elevated pill surface.",
                        "when_to_use": ["Use to provide persistent, contextual floating actions that float above content."],
                        "how_to_use": ["Call <code>HorizontalFloatingToolbar(expanded = true, floatingActionButton = { ... }) { ... }</code>."],
                        "api_highlights": [
                            {"param": "floatingActionButton", "type": "@Composable () -> Unit", "desc": "FAB slot inside toolbar."},
                            {"param": "content", "type": "@Composable RowScope.() -> Unit", "desc": "Action icons slot."}
                        ]
                    },
                    {
                        "id": "medium-fab",
                        "title": "Medium floating action button",
                        "tag": "android_compose_expressive_components_mediumfab",
                        "description": "Material 3 Expressive medium floating action button.",
                        "when_to_use": ["Use when standard FAB is too small and large FAB is too large."],
                        "how_to_use": ["Call <code>MediumFloatingActionButton(onClick = { ... }) { Icon(...) }</code>."],
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."}
                        ]
                    }
                ]
            },
            {
                "id": "icon-button",
                "title": "Icon button",
                "file": "IconButton.kt",
                "description": "Icon buttons allow users to take actions and make choices with a single tap, using compact icon-only visual representation.",
                "when_to_use": [
                    "<strong>Compact Actions:</strong> Use in toolbars, app bars, cards, and dialog headers.",
                    "<strong>Toggle Actions:</strong> Use to toggle bookmark, favorite, or pin states.",
                    "<strong>Expressive Motion:</strong> Use expressive animated shape variants for tactile feedback."
                ],
                "how_to_use": [
                    "Supply an <code>onClick: () -> Unit</code> lambda to execute the button action.",
                    "Pass an <code>Icon</code> composable inside the content slot with appropriate <code>contentDescription</code>.",
                    "Use <code>IconButtonDefaults.shapes()</code> or <code>toggleableShapes()</code> for expressive morphing animations."
                ],
                "api_highlights": [
                    {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                    {"param": "shapes", "type": "IconButtonShapes?", "desc": "Expressive shape morphing configuration."},
                    {"param": "colors", "type": "IconButtonColors", "desc": "Container and content colors."}
                ],
                "examples": [
                    {
                        "id": "toggle-icon-button",
                        "title": "Toggle icon button",
                        "tag": "android_compose_components_togglebuttonexample",
                        "description": "Icon button that switches between selected and unselected icon states.",
                        "when_to_use": ["Favorite, bookmark, or pin toggles."],
                        "how_to_use": ["Call <code>IconButton(onClick = { isToggled = !isToggled }) { Icon(...) }</code>."],
                        "api_highlights": [{"param": "onClick", "type": "() -> Unit", "desc": "Click callback."}]
                    },
                    {
                        "id": "momentary-icon-button",
                        "title": "Momentary icon button",
                        "tag": "android_compose_components_momentaryiconbuttons",
                        "description": "Momentary repeat-on-press icon button for rapid continuous increments/decrements.",
                        "when_to_use": ["Fast-forward, rewind, or volume steppers."],
                        "how_to_use": ["Track <code>isPressed</code> interaction state to repeat actions on interval."],
                        "api_highlights": [{"param": "interactionSource", "type": "MutableInteractionSource", "desc": "Interaction state tracking."}]
                    },
                    {
                        "id": "animated-icon-button",
                        "title": "Icon button with animated shape",
                        "tag": "android_compose_expressive_components_animatediconbuttons",
                        "description": "Material 3 Expressive icon button with dynamic corner shape morphing on press.",
                        "when_to_use": ["Tactile feedback in expressive Material 3 designs."],
                        "how_to_use": ["Pass <code>shapes = IconButtonDefaults.shapes()</code> to <code>IconButton(...)</code>."],
                        "api_highlights": [{"param": "shapes", "type": "IconButtonShapes", "desc": "IconButtonDefaults.shapes()."}]
                    },
                    {
                        "id": "animated-toggle-icon-button",
                        "title": "Icon toggle button with animated shape",
                        "tag": "android_compose_expressive_components_animatedtoggleiconbuttons",
                        "description": "Material 3 Expressive icon toggle button with morphing shape between checked and unchecked states.",
                        "when_to_use": ["Expressive toggle buttons (lock/unlock, bookmark, favorite)."],
                        "how_to_use": ["Call <code>IconToggleButton(checked = checked, onCheckedChange = { ... }, shapes = IconButtonDefaults.toggleableShapes())</code>."],
                        "api_highlights": [{"param": "shapes", "type": "IconToggleButtonShapes", "desc": "IconButtonDefaults.toggleableShapes()."}]
                    }
                ]
            }
        ]
    },
    {
        "id": "communication",
        "name": "Communication",
        "components": [
            {
                "id": "badge-examples",
                "title": "Badges",
                "file": "Badges.kt",
                "description": "Badges display small status indicators or notification counts on top of icons.",
                "when_to_use": ["Use to inform users of new notifications or unread counts."],
                "how_to_use": ["Wrap inside <code>BadgedBox</code>."],
                "api_highlights": [{"param": "badge", "type": "@Composable () -> Unit", "desc": "Badge slot."}],
                "examples": [
                    {
                        "id": "badge",
                        "title": "Badge",
                        "tag": "android_compose_components_badge",
                        "description": "Standard badge displaying unread status or small count.",
                        "when_to_use": ["Display notification count on navigation items."],
                        "how_to_use": ["Use <code>BadgedBox(badge = { Badge { Text(\"8\") } })</code>."],
                        "api_highlights": [{"param": "badge", "type": "@Composable () -> Unit", "desc": "Badge slot."}]
                    },
                    {
                        "id": "badge-interactive",
                        "title": "Interactive badge",
                        "tag": "android_compose_components_badgeinteractive",
                        "description": "BadgedBox with dynamic numeric count incremented on interaction.",
                        "when_to_use": ["Interactive cart or message counter."],
                        "how_to_use": ["Maintain count in state and update inside <code>Badge { Text(\"$count\") }</code>."],
                        "api_highlights": [{"param": "badge", "type": "@Composable () -> Unit", "desc": "Badge slot."}]
                    }
                ]
            },
            {
                "id": "progress-indicator",
                "title": "Progress indicators",
                "file": "ProgressIndicator.kt",
                "description": "Progress indicators inform users about ongoing operations.",
                "when_to_use": ["Use during background data loading or long-running tasks."],
                "how_to_use": ["Call <code>CircularProgressIndicator()</code> or <code>LinearProgressIndicator()</code>."],
                "api_highlights": [{"param": "progress", "type": "() -> Float", "desc": "Progress lambda."}],
                "examples": [
                    {
                        "id": "indeterminate-progress-indicator",
                        "title": "Indeterminate progress indicator",
                        "tag": "android_compose_components_indeterminateindicator",
                        "description": "Circular indicator showing indeterminate background loading.",
                        "when_to_use": ["When task duration cannot be measured."],
                        "how_to_use": ["Call <code>CircularProgressIndicator()</code>."],
                        "api_highlights": [{"param": "color", "type": "Color", "desc": "Indicator color."}]
                    },
                    {
                        "id": "determinate-progress-indicator",
                        "title": "Determinate progress indicator",
                        "tag": "android_compose_components_determinateindicator",
                        "description": "Linear indicator showing specific progress toward completion.",
                        "when_to_use": ["When percentage completed is known (e.g. file upload)."],
                        "how_to_use": ["Call <code>LinearProgressIndicator(progress = { currentProgress })</code>."],
                        "api_highlights": [{"param": "progress", "type": "() -> Float", "desc": "Progress value (0.0 to 1.0)."}]
                    },
                    {
                        "id": "loading-indicator",
                        "title": "Loading indicator",
                        "tag": "android_compose_components_loadingindicator",
                        "description": "Material 3 Expressive loading indicator providing dynamic, indeterminate loading animation.",
                        "when_to_use": ["Use during content loading or refresh where lively, expressive feedback is needed."],
                        "how_to_use": ["Call <code>LoadingIndicator()</code>."],
                        "api_highlights": [
                            {"param": "modifier", "type": "Modifier", "desc": "Layout modifier."}
                        ]
                    },
                    {
                        "id": "contained-loading-indicator",
                        "title": "Contained loading indicator",
                        "tag": "android_compose_components_containedloadingindicator",
                        "description": "Material 3 Expressive contained loading indicator positioned within an elevated container surface.",
                        "when_to_use": ["Use as a floating or centered overlay indicator above content."],
                        "how_to_use": ["Call <code>ContainedLoadingIndicator()</code>."],
                        "api_highlights": [
                            {"param": "modifier", "type": "Modifier", "desc": "Layout modifier."}
                        ]
                    },
                    {
                        "id": "determinate-linear-wavy-indicator",
                        "title": "Determinate linear wavy progress indicator",
                        "tag": "android_compose_expressive_components_determinatelinearwavyindicator",
                        "description": "Material 3 Expressive linear progress indicator featuring playful wavy animation curves.",
                        "when_to_use": ["Use during file downloads or progress steps where expressive wavy motion enhances delight."],
                        "how_to_use": ["Call <code>LinearWavyProgressIndicator(progress = { animatedProgress })</code>."],
                        "api_highlights": [
                            {"param": "progress", "type": "() -> Float", "desc": "Progress lambda returning 0.0 to 1.0."}
                        ]
                    },
                    {
                        "id": "indeterminate-linear-wavy-indicator",
                        "title": "Indeterminate linear wavy progress indicator",
                        "tag": "android_compose_expressive_components_indeterminatelinearwavyindicator",
                        "description": "Material 3 Expressive indeterminate linear wavy indicator providing smooth animated wave flow.",
                        "when_to_use": ["Use for indeterminate continuous background operations."],
                        "how_to_use": ["Call <code>LinearWavyProgressIndicator()</code>."],
                        "api_highlights": [
                            {"param": "modifier", "type": "Modifier", "desc": "Layout modifier."}
                        ]
                    },
                    {
                        "id": "determinate-circular-wavy-indicator",
                        "title": "Determinate circular wavy progress indicator",
                        "tag": "android_compose_expressive_components_determinatecircularwavyindicator",
                        "description": "Material 3 Expressive circular wavy progress indicator with animated undulating borders.",
                        "when_to_use": ["Use for expressive circular meters, timers, and step progress."],
                        "how_to_use": ["Call <code>CircularWavyProgressIndicator(progress = { animatedProgress })</code>."],
                        "api_highlights": [
                            {"param": "progress", "type": "() -> Float", "desc": "Progress lambda returning 0.0 to 1.0."}
                        ]
                    },
                    {
                        "id": "indeterminate-circular-wavy-indicator",
                        "title": "Indeterminate circular wavy progress indicator",
                        "tag": "android_compose_expressive_components_indeterminatecircularwavyindicator",
                        "description": "Material 3 Expressive indeterminate circular wavy progress indicator.",
                        "when_to_use": ["Use for indeterminate loading with vibrant expressive motion."],
                        "how_to_use": ["Call <code>CircularWavyProgressIndicator()</code>."],
                        "api_highlights": [
                            {"param": "modifier", "type": "Modifier", "desc": "Layout modifier."}
                        ]
                    },
                    {
                        "id": "determinate-linear-expressive-indicator",
                        "title": "Determinate linear progress indicator (Expressive)",
                        "tag": "android_compose_expressive_components_determinatelinearindicator",
                        "description": "Material 3 Expressive linear progress indicator with animated progress tracking and slider controls.",
                        "when_to_use": ["Use for trackable uploads and task completion."],
                        "how_to_use": ["Call <code>LinearProgressIndicator(progress = { animatedProgress })</code>."],
                        "api_highlights": [
                            {"param": "progress", "type": "() -> Float", "desc": "Progress value."}
                        ]
                    },
                    {
                        "id": "indeterminate-linear-expressive-indicator",
                        "title": "Indeterminate linear progress indicator (Expressive)",
                        "tag": "android_compose_expressive_components_indeterminatelinearindicator",
                        "description": "Material 3 Expressive indeterminate linear progress indicator.",
                        "when_to_use": ["Use for indeterminate background operations."],
                        "how_to_use": ["Call <code>LinearProgressIndicator()</code>."],
                        "api_highlights": [
                            {"param": "modifier", "type": "Modifier", "desc": "Layout modifier."}
                        ]
                    },
                    {
                        "id": "determinate-circular-expressive-indicator",
                        "title": "Determinate circular progress indicator (Expressive)",
                        "tag": "android_compose_expressive_components_determinatecircularindicator",
                        "description": "Material 3 Expressive circular progress indicator with animated progress state.",
                        "when_to_use": ["Use for circular progress meters and completion gauges."],
                        "how_to_use": ["Call <code>CircularProgressIndicator(progress = { animatedProgress })</code>."],
                        "api_highlights": [
                            {"param": "progress", "type": "() -> Float", "desc": "Progress value."}
                        ]
                    },
                    {
                        "id": "indeterminate-circular-expressive-indicator",
                        "title": "Indeterminate circular progress indicator (Expressive)",
                        "tag": "android_compose_expressive_components_indeterminatecircularindicator",
                        "description": "Material 3 Expressive indeterminate circular progress indicator.",
                        "when_to_use": ["Use for standard indeterminate loading spinners."],
                        "how_to_use": ["Call <code>CircularProgressIndicator()</code>."],
                        "api_highlights": [
                            {"param": "modifier", "type": "Modifier", "desc": "Layout modifier."}
                        ]
                    }
                ]
            },
            {
                "id": "tooltip-examples",
                "title": "Tooltips",
                "file": "Tooltips.kt",
                "description": "Tooltips provide informative text labels on long-press or hover.",
                "when_to_use": ["Clarify icon button actions or complex UI controls."],
                "how_to_use": ["Wrap element in <code>TooltipBox</code>."],
                "api_highlights": [{"param": "tooltip", "type": "@Composable () -> Unit", "desc": "Tooltip content."}],
                "examples": [
                    {
                        "id": "plain-tooltip",
                        "title": "Plain tooltip",
                        "tag": "android_compose_components_plaintooltipexample",
                        "description": "Short plain text label displayed on element hover or long-press.",
                        "when_to_use": ["Label icon-only buttons."],
                        "how_to_use": ["Use <code>TooltipBox(tooltip = { PlainTooltip { Text(...) } })</code>."],
                        "api_highlights": [{"param": "tooltip", "type": "@Composable () -> Unit", "desc": "Tooltip slot."}]
                    },
                    {
                        "id": "rich-tooltip",
                        "title": "Rich tooltip",
                        "tag": "android_compose_components_richtooltipexample",
                        "description": "Rich tooltip with title, body text, and optional action buttons.",
                        "when_to_use": ["Detailed feature onboarding or contextual help."],
                        "how_to_use": ["Use <code>TooltipBox(tooltip = { RichTooltip(title = { ... }) { Text(...) } })</code>."],
                        "api_highlights": [{"param": "title", "type": "@Composable () -> Unit", "desc": "Title slot."}]
                    }
                ]
            }
        ]
    },
    {
        "id": "containment",
        "name": "Containment",
        "components": [
            {
                "id": "bottom-sheet",
                "title": "Bottom sheets",
                "file": "BottomSheet.kt",
                "description": "Modal bottom sheets display supplementary content anchored to the bottom of the screen.",
                "when_to_use": ["Display context menus, share targets, or sub-tasks without leaving the current screen."],
                "how_to_use": ["Call <code>ModalBottomSheet(onDismissRequest = { ... })</code>."],
                "api_highlights": [{"param": "sheetState", "type": "SheetState", "desc": "Controls expand/collapse."}],
                "examples": [
                    {
                        "id": "partial-bottom-sheet",
                        "title": "Partial bottom sheet",
                        "tag": "android_compose_components_partialbottomsheet",
                        "description": "Modal bottom sheet that anchors to a partial height before expanding.",
                        "when_to_use": ["Bottom sheet with peek state."],
                        "how_to_use": ["Call <code>ModalBottomSheet(sheetState = rememberModalBottomSheetState())</code>."],
                        "api_highlights": [{"param": "onDismissRequest", "type": "() -> Unit", "desc": "Dismiss callback."}]
                    }
                ]
            },
            {
                "id": "card-examples",
                "title": "Cards",
                "file": "Card.kt",
                "description": "Cards contain content and actions about a single subject.",
                "when_to_use": ["Group related content and interactive elements into distinct surface units."],
                "how_to_use": ["Use <code>Card</code>, <code>ElevatedCard</code>, or <code>OutlinedCard</code>."],
                "api_highlights": [{"param": "colors", "type": "CardColors", "desc": "Card colors."}],
                "examples": [
                    {
                        "id": "filled-card",
                        "title": "Filled card",
                        "tag": "android_compose_components_filledcard",
                        "description": "Filled card with container color distinguishing it from the background.",
                        "when_to_use": ["Standard card container on surface background."],
                        "how_to_use": ["Call <code>Card { ... }</code>."],
                        "api_highlights": [{"param": "colors", "type": "CardColors", "desc": "Card colors."}]
                    },
                    {
                        "id": "elevated-card",
                        "title": "Elevated card",
                        "tag": "android_compose_components_elevatedcard",
                        "description": "Card with elevation shadow providing visual separation.",
                        "when_to_use": ["When separation is needed on complex backgrounds."],
                        "how_to_use": ["Call <code>ElevatedCard { ... }</code>."],
                        "api_highlights": [{"param": "elevation", "type": "CardElevation", "desc": "Elevation values."}]
                    },
                    {
                        "id": "outlined-card",
                        "title": "Outlined card",
                        "tag": "android_compose_components_outlinedcard",
                        "description": "Card with a subtle outline border for clean surface grouping.",
                        "when_to_use": ["Clean, flat design without heavy shadows."],
                        "how_to_use": ["Call <code>OutlinedCard { ... }</code>."],
                        "api_highlights": [{"param": "border", "type": "BorderStroke", "desc": "Border stroke."}]
                    }
                ]
            },
            {
                "id": "carousel-examples",
                "title": "Carousel",
                "file": "Carousel.kt",
                "description": "Horizontal scrollable card carousel showcasing list items.",
                "when_to_use": ["Feature galleries, media cards, or product highlights."],
                "how_to_use": ["Call <code>HorizontalMultiBrowseCarousel</code>."],
                "api_highlights": [{"param": "state", "type": "CarouselState", "desc": "State tracking."}],
                "examples": [
                    {
                        "id": "multi-browse-carousel",
                        "title": "Multi-browse carousel",
                        "tag": "android_compose_carousel_multi_browse_basic",
                        "description": "Multi-browse carousel displaying multiple items with peek previews.",
                        "when_to_use": ["Browsing multiple items side-by-side."],
                        "how_to_use": ["Call <code>HorizontalMultiBrowseCarousel(state = rememberCarouselState())</code>."],
                        "api_highlights": [{"param": "preferredItemWidth", "type": "Dp", "desc": "Width of items."}]
                    },
                    {
                        "id": "uncontained-carousel",
                        "title": "Uncontained carousel",
                        "tag": "android_compose_carousel_uncontained_basic",
                        "description": "Uncontained carousel allowing items to scroll freely past edge boundaries.",
                        "when_to_use": ["Continuous horizontal browsing."],
                        "how_to_use": ["Call <code>HorizontalUncontainedCarousel(...)</code>."],
                        "api_highlights": [{"param": "itemSpacing", "type": "Dp", "desc": "Spacing between items."}]
                    }
                ]
            },
            {
                "id": "dialog-examples",
                "title": "Dialogs",
                "file": "Dialog.kt",
                "description": "Dialogs inform users about a task and can contain critical information or require decisions.",
                "when_to_use": ["Alerts, confirmations, and prompt decisions."],
                "how_to_use": ["Use <code>AlertDialog</code> or custom <code>Dialog</code>."],
                "api_highlights": [{"param": "onDismissRequest", "type": "() -> Unit", "desc": "Dismiss callback."}],
                "examples": [
                    {
                        "id": "alert-dialog",
                        "title": "Alert dialog",
                        "tag": "android_compose_components_alertdialog",
                        "description": "Alert dialog with title, text, icon, and confirm/dismiss buttons.",
                        "when_to_use": ["Critical confirmations (e.g. 'Delete item?')."],
                        "how_to_use": ["Call <code>AlertDialog(onDismissRequest = ..., confirmButton = ...)</code>."],
                        "api_highlights": [{"param": "confirmButton", "type": "@Composable () -> Unit", "desc": "Confirm button slot."}]
                    },
                    {
                        "id": "minimal-dialog",
                        "title": "Minimal dialog",
                        "tag": "android_compose_components_minimaldialog",
                        "description": "Minimal custom dialog without pre-styled buttons.",
                        "when_to_use": ["Custom layout dialogs."],
                        "how_to_use": ["Call <code>Dialog(onDismissRequest = ...) { Card { ... } }</code>."],
                        "api_highlights": [{"param": "content", "type": "@Composable () -> Unit", "desc": "Content slot."}]
                    },
                    {
                        "id": "dialog-with-image",
                        "title": "Dialog with image",
                        "tag": "android_compose_components_dialogwithimage",
                        "description": "Dialog featuring an illustration or header image above content.",
                        "when_to_use": ["Promotional or milestone dialogs."],
                        "how_to_use": ["Place <code>Image</code> inside <code>Dialog</code> content."],
                        "api_highlights": [{"param": "painter", "type": "Painter", "desc": "Image painter."}]
                    }
                ]
            },
            {
                "id": "divider-examples",
                "title": "Dividers",
                "file": "Divider.kt",
                "description": "Dividers group and separate content into distinct sections.",
                "when_to_use": ["Separate list items or content groups."],
                "how_to_use": ["Call <code>HorizontalDivider()</code> or <code>VerticalDivider()</code>."],
                "api_highlights": [{"param": "thickness", "type": "Dp", "desc": "Line thickness."}],
                "examples": [
                    {
                        "id": "horizontal-divider",
                        "title": "Horizontal divider",
                        "tag": "android_compose_components_horizontaldivider",
                        "description": "Horizontal thin line separating content sections vertically.",
                        "when_to_use": ["Between list items."],
                        "how_to_use": ["Call <code>HorizontalDivider(thickness = 1.dp)</code>."],
                        "api_highlights": [{"param": "color", "type": "Color", "desc": "Line color."}]
                    },
                    {
                        "id": "vertical-divider",
                        "title": "Vertical divider",
                        "tag": "android_compose_components_verticaldivider",
                        "description": "Vertical thin line separating adjacent elements in a row.",
                        "when_to_use": ["Between row actions or toolbars."],
                        "how_to_use": ["Call <code>VerticalDivider(modifier = Modifier.fillMaxHeight())</code>."],
                        "api_highlights": [{"param": "thickness", "type": "Dp", "desc": "Thickness."}]
                    }
                ]
            },
            {
                "id": "scaffold-example",
                "title": "Scaffold",
                "file": "Scaffold.kt",
                "description": "Fundamental layout structure providing slots for top bar, bottom bar, FAB, and content.",
                "when_to_use": ["Standard screen scaffolding."],
                "how_to_use": ["Call <code>Scaffold(topBar = ..., floatingActionButton = ...) { padding -> ... }</code>."],
                "api_highlights": [{"param": "topBar", "type": "@Composable () -> Unit", "desc": "Top bar slot."}],
                "examples": [
                    {
                        "id": "scaffold",
                        "title": "Scaffold",
                        "tag": "android_compose_components_scaffold",
                        "description": "Standard Scaffold implementation with TopAppBar and FAB.",
                        "when_to_use": ["Screen root layout."],
                        "how_to_use": ["Call <code>Scaffold(...) { innerPadding -> ... }</code>."],
                        "api_highlights": [{"param": "floatingActionButton", "type": "@Composable () -> Unit", "desc": "FAB slot."}]
                    }
                ]
            }
        ]
    },
    {
        "id": "navigation",
        "name": "Navigation",
        "components": [
            {
                "id": "app-bar-examples",
                "title": "App bars",
                "file": "AppBar.kt",
                "description": "Top App Bars provide navigation, title, and actions for screens.",
                "when_to_use": ["Top of screen header."],
                "how_to_use": ["Use <code>CenterAlignedTopAppBar</code>, <code>SmallTopAppBar</code>, etc."],
                "api_highlights": [{"param": "title", "type": "@Composable () -> Unit", "desc": "Title slot."}],
                "examples": [
                    {
                        "id": "center-aligned-top-app-bar",
                        "title": "Center-aligned top app bar",
                        "tag": "android_compose_components_centeralignedtopappbar",
                        "description": "Top app bar with centered headline title and action icons.",
                        "when_to_use": ["Primary screens with centered branding."],
                        "how_to_use": ["Call <code>CenterAlignedTopAppBar(title = { Text(...) })</code>."],
                        "api_highlights": [{"param": "title", "type": "@Composable () -> Unit", "desc": "Title slot."}]
                    },
                    {
                        "id": "small-top-app-bar",
                        "title": "Small top app bar",
                        "tag": "android_compose_components_smalltopappbar",
                        "description": "Standard compact top app bar with start-aligned title.",
                        "when_to_use": ["Secondary or detail screens."],
                        "how_to_use": ["Call <code>TopAppBar(title = { Text(...) })</code>."],
                        "api_highlights": [{"param": "navigationIcon", "type": "@Composable () -> Unit", "desc": "Nav icon slot."}]
                    },
                    {
                        "id": "medium-top-app-bar",
                        "title": "Medium top app bar",
                        "tag": "android_compose_components_mediumtopappbar",
                        "description": "Medium top app bar featuring a larger title area that collapses on scroll.",
                        "when_to_use": ["Browsing screens with medium prominence title."],
                        "how_to_use": ["Call <code>MediumTopAppBar(...)</code>."],
                        "api_highlights": [{"param": "scrollBehavior", "type": "TopAppBarScrollBehavior?", "desc": "Scroll collapse."}]
                    },
                    {
                        "id": "large-top-app-bar",
                        "title": "Large top app bar",
                        "tag": "android_compose_components_largetopappbar",
                        "description": "Large top app bar with prominent headline typography that collapses on scroll.",
                        "when_to_use": ["High-emphasis landing screens."],
                        "how_to_use": ["Call <code>LargeTopAppBar(...)</code>."],
                        "api_highlights": [{"param": "scrollBehavior", "type": "TopAppBarScrollBehavior?", "desc": "Scroll collapse."}]
                    },
                    {
                        "id": "center-aligned-top-app-bar-with-subtitle",
                        "title": "Center-aligned top app bar with subtitle",
                        "tag": "android_compose_expressive_components_centeralignedtopappbarwithsubtitle",
                        "description": "Material 3 Expressive center-aligned top app bar featuring both title and subtitle headers with scroll collapse behavior.",
                        "when_to_use": ["Use when secondary contextual metadata (e.g. document status, subtitle) is needed."],
                        "how_to_use": ["Call <code>CenterAlignedTopAppBar(title = { Text(...) }, subtitle = { Text(...) }, scrollBehavior = scrollBehavior)</code>."],
                        "api_highlights": [
                            {"param": "subtitle", "type": "@Composable () -> Unit", "desc": "Subtitle slot."},
                            {"param": "scrollBehavior", "type": "TopAppBarScrollBehavior", "desc": "Scroll behavior."}
                        ]
                    },
                    {
                        "id": "always-enter-top-app-bar",
                        "title": "Always-enter top app bar",
                        "tag": "android_compose_expressive_components_alwaysentertopappbar",
                        "description": "Material 3 Expressive top app bar with enterAlways scroll behavior, immediately reappearing on scroll up.",
                        "when_to_use": ["Use in feed screens where immediate access to navigation actions on upward scroll is desired."],
                        "how_to_use": ["Set <code>scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()</code>."],
                        "api_highlights": [
                            {"param": "scrollBehavior", "type": "TopAppBarScrollBehavior", "desc": "TopAppBarDefaults.enterAlwaysScrollBehavior()."}
                        ]
                    },
                    {
                        "id": "medium-flexible-top-app-bar",
                        "title": "Medium flexible top app bar",
                        "tag": "android_compose_expressive_components_exituntillcollapsedtopappbar",
                        "description": "Material 3 Expressive medium flexible top app bar with centered title and exitUntilCollapsed behavior.",
                        "when_to_use": ["Use on category or section pages with flexible expanding headers."],
                        "how_to_use": ["Call <code>MediumFlexibleTopAppBar(title = { Text(...) }, subtitle = { Text(...) }, titleHorizontalAlignment = Alignment.CenterHorizontally, scrollBehavior = scrollBehavior)</code>."],
                        "api_highlights": [
                            {"param": "titleHorizontalAlignment", "type": "Alignment.Horizontal", "desc": "Horizontal alignment."},
                            {"param": "scrollBehavior", "type": "TopAppBarScrollBehavior", "desc": "TopAppBarDefaults.exitUntilCollapsedScrollBehavior()."}
                        ]
                    },
                    {
                        "id": "large-flexible-top-app-bar",
                        "title": "Large flexible top app bar",
                        "tag": "android_compose_expressive_components_exituntillcollapsedlargetopappbar",
                        "description": "Material 3 Expressive large flexible top app bar offering high-prominence typography and smooth collapse dynamics.",
                        "when_to_use": ["Use on primary landing screens and detail dashboards."],
                        "how_to_use": ["Call <code>LargeFlexibleTopAppBar(title = { Text(...) }, subtitle = { Text(...) }, titleHorizontalAlignment = Alignment.CenterHorizontally, scrollBehavior = scrollBehavior)</code>."],
                        "api_highlights": [
                            {"param": "scrollBehavior", "type": "TopAppBarScrollBehavior", "desc": "Scroll collapse behavior."}
                        ]
                    }
                ]
            },
            {
                "id": "navigation-examples",
                "title": "Navigation bar & rail",
                "file": "Navigation.kt",
                "description": "Bottom navigation bar and side navigation rail for app destinations.",
                "when_to_use": ["Primary app destinations."],
                "how_to_use": ["Use <code>NavigationBar</code> or <code>NavigationRail</code>."],
                "api_highlights": [{"param": "selected", "type": "Boolean", "desc": "Active item."}],
                "examples": [
                    {
                        "id": "navigation-bar",
                        "title": "Navigation bar",
                        "tag": "android_compose_components_navigationbarexample",
                        "description": "Bottom navigation bar providing access to 3 to 5 top-level destinations.",
                        "when_to_use": ["Compact mobile screens."],
                        "how_to_use": ["Call <code>NavigationBar { NavigationBarItem(...) }</code>."],
                        "api_highlights": [{"param": "selected", "type": "Boolean", "desc": "Selected state."}]
                    },
                    {
                        "id": "navigation-rail",
                        "title": "Navigation rail",
                        "tag": "android_compose_components_navigationrailexample",
                        "description": "Side navigation rail suited for tablets and wide screens.",
                        "when_to_use": ["Expanded tablet or desktop screens."],
                        "how_to_use": ["Call <code>NavigationRail { NavigationRailItem(...) }</code>."],
                        "api_highlights": [{"param": "selected", "type": "Boolean", "desc": "Selected state."}]
                    },
                    {
                        "id": "vertical-items-navigation-bar",
                        "title": "Vertical items navigation bar",
                        "tag": "android_compose_expressive_components_verticalitemsnavigationbarexample",
                        "description": "Material 3 Expressive navigation bar with vertically stacked icon and text items.",
                        "when_to_use": ["Standard bottom navigation bar on phones and compact layouts."],
                        "how_to_use": ["Call <code>ShortNavigationBar { ShortNavigationBarItem(...) }</code>."],
                        "api_highlights": [
                            {"param": "selected", "type": "Boolean", "desc": "Selection state."}
                        ]
                    },
                    {
                        "id": "horizontal-items-navigation-bar",
                        "title": "Horizontal items navigation bar",
                        "tag": "android_compose_expressive_components_horizontalitemsnavigationbarexample",
                        "description": "Material 3 Expressive navigation bar with horizontal icon and label layout.",
                        "when_to_use": ["Use in wide compact screens or landscape orientations."],
                        "how_to_use": ["Call <code>ShortNavigationBar { ShortNavigationBarItem(..., labelPosition = NavigationItemLabelPosition.Start) }</code>."],
                        "api_highlights": [
                            {"param": "labelPosition", "type": "NavigationItemLabelPosition", "desc": "Label position."}
                        ]
                    },
                    {
                        "id": "wide-navigation-rail",
                        "title": "Wide navigation rail",
                        "tag": "android_compose_expressive_components_widenavigationrailexample",
                        "description": "Material 3 Expressive expandable wide navigation rail for medium and large screens.",
                        "when_to_use": ["Use on foldables, tablets, and desktop interfaces."],
                        "how_to_use": ["Call <code>WideNavigationRail(state = rememberWideNavigationRailState(), header = { ... }) { WideNavigationRailItem(...) }</code>."],
                        "api_highlights": [
                            {"param": "state", "type": "WideNavigationRailState", "desc": "Tracks expanded / collapsed rail state."}
                        ]
                    },
                    {
                        "id": "modal-wide-navigation-rail",
                        "title": "Modal wide navigation rail",
                        "tag": "android_compose_expressive_components_modalwidenavigationrailexample",
                        "description": "Material 3 Expressive modal wide navigation rail sliding open over content.",
                        "when_to_use": ["Use when rail content should overlay screen content without resizing layout."],
                        "how_to_use": ["Call <code>ModalWideNavigationRail(state = state, header = { ... }) { WideNavigationRailItem(...) }</code>."],
                        "api_highlights": [
                            {"param": "state", "type": "WideNavigationRailState", "desc": "Rail state controller."}
                        ]
                    },
                    {
                        "id": "dismissible-modal-wide-navigation-rail",
                        "title": "Dismissible modal wide navigation rail",
                        "tag": "android_compose_expressive_components_dismissiblemodalwidenavigationrailexample",
                        "description": "Material 3 Expressive modal wide navigation rail that completely collapses offscreen when dismissed.",
                        "when_to_use": ["Use for temporary navigation side sheets on large tablets."],
                        "how_to_use": ["Call <code>ModalWideNavigationRail(state = state, hideOnCollapse = true) { WideNavigationRailItem(...) }</code>."],
                        "api_highlights": [
                            {"param": "hideOnCollapse", "type": "Boolean", "desc": "Hides rail entirely when collapsed."}
                        ]
                    }
                ]
            },
            {
                "id": "navigation-drawer",
                "title": "Navigation drawer",
                "file": "NavigationDrawer.kt",
                "description": "Modal navigation drawer for navigation destinations on medium and large screens.",
                "when_to_use": ["Large destination lists across entire app."],
                "how_to_use": ["Call <code>ModalNavigationDrawer(drawerContent = { ModalDrawerSheet { ... } })</code>."],
                "api_highlights": [{"param": "drawerState", "type": "DrawerState", "desc": "Drawer state."}],
                "examples": [
                    {
                        "id": "modal-navigation-drawer",
                        "title": "Modal navigation drawer",
                        "tag": "android_compose_components_detaileddrawerexample",
                        "description": "Modal navigation drawer sliding from screen edge.",
                        "when_to_use": ["Side menu navigation."],
                        "how_to_use": ["Call <code>DetailedDrawerExample { ... }</code>."],
                        "api_highlights": [{"param": "drawerState", "type": "DrawerState", "desc": "State."}]
                    }
                ]
            }
        ]
    },
    {
        "id": "selection",
        "name": "Selection",
        "components": [
            {
                "id": "checkbox-examples",
                "title": "Checkbox",
                "file": "Checkbox.kt",
                "description": "Checkboxes allow users to select one or more items from a set.",
                "when_to_use": ["Multi-selection or binary settings."],
                "how_to_use": ["Call <code>Checkbox(checked = ..., onCheckedChange = ...)</code>."],
                "api_highlights": [{"param": "checked", "type": "Boolean", "desc": "Checked state."}],
                "examples": [
                    {
                        "id": "checkbox",
                        "title": "Checkbox",
                        "tag": "android_compose_components_checkbox_minimal",
                        "description": "Standard binary checkbox for selecting or deselecting a single item.",
                        "when_to_use": ["Simple toggle."],
                        "how_to_use": ["Call <code>Checkbox(checked = isChecked, onCheckedChange = { ... })</code>."],
                        "api_highlights": [{"param": "checked", "type": "Boolean", "desc": "Checked state."}]
                    },
                    {
                        "id": "parent-checkbox",
                        "title": "Parent checkbox (Tri-State)",
                        "tag": "android_compose_components_checkbox_parent",
                        "description": "TriStateCheckbox controlling multiple child checkboxes.",
                        "when_to_use": ["Select all / partial selection."],
                        "how_to_use": ["Call <code>TriStateCheckbox(state = parentState, onClick = { ... })</code>."],
                        "api_highlights": [{"param": "state", "type": "ToggleableState", "desc": "On / Off / Indeterminate."}]
                    }
                ]
            },
            {
                "id": "chip-examples",
                "title": "Chips",
                "file": "Chip.kt",
                "description": "Chips help users enter information, make selections, filter content, or trigger actions.",
                "when_to_use": ["Compact contextual actions and filters."],
                "how_to_use": ["Use <code>AssistChip</code>, <code>FilterChip</code>, <code>InputChip</code>, <code>SuggestionChip</code>."],
                "api_highlights": [{"param": "label", "type": "@Composable () -> Unit", "desc": "Label slot."}],
                "examples": [
                    {
                        "id": "assist-chip",
                        "title": "Assist chip",
                        "tag": "android_compose_components_assistchip",
                        "description": "Assist chip triggering an action related to primary content.",
                        "when_to_use": ["Quick action prompts."],
                        "how_to_use": ["Call <code>AssistChip(onClick = {}, label = { Text(...) })</code>."],
                        "api_highlights": [{"param": "onClick", "type": "() -> Unit", "desc": "Action."}]
                    },
                    {
                        "id": "filter-chip",
                        "title": "Filter chip",
                        "tag": "android_compose_components_filterchip",
                        "description": "Filter chip allowing users to filter content by selecting tags.",
                        "when_to_use": ["Content filtering."],
                        "how_to_use": ["Call <code>FilterChip(selected = isSelected, onClick = {}, label = { Text(...) })</code>."],
                        "api_highlights": [{"param": "selected", "type": "Boolean", "desc": "Selected state."}]
                    },
                    {
                        "id": "input-chip",
                        "title": "Input chip",
                        "tag": "android_compose_components_inputchip",
                        "description": "Input chip representing a complex piece of information with trailing dismiss icon.",
                        "when_to_use": ["Email recipients or tag inputs."],
                        "how_to_use": ["Call <code>InputChip(text = \"...\", onDismiss = {})</code>."],
                        "api_highlights": [{"param": "onDismiss", "type": "() -> Unit", "desc": "Dismiss callback."}]
                    },
                    {
                        "id": "suggestion-chip",
                        "title": "Suggestion chip",
                        "tag": "android_compose_components_suggestionchip",
                        "description": "Suggestion chip presenting dynamically generated recommendations.",
                        "when_to_use": ["Recommended search terms or replies."],
                        "how_to_use": ["Call <code>SuggestionChip(onClick = {}, label = { Text(...) })</code>."],
                        "api_highlights": [{"param": "onClick", "type": "() -> Unit", "desc": "Action."}]
                    }
                ]
            },
            {
                "id": "date-picker",
                "title": "Date pickers",
                "file": "DatePickers.kt",
                "description": "Date pickers allow users to select dates from a calendar interface.",
                "when_to_use": ["Date selection in forms or booking."],
                "how_to_use": ["Use <code>DatePicker</code> or <code>DatePickerDialog</code>."],
                "api_highlights": [{"param": "state", "type": "DatePickerState", "desc": "State."}],
                "examples": [
                    {
                        "id": "date-picker-modal",
                        "title": "Modal date picker",
                        "tag": "android_compose_components_datepicker_modal",
                        "description": "Modal dialog for selecting a single calendar date.",
                        "when_to_use": ["Selecting birthday or single event date."],
                        "how_to_use": ["Call <code>DatePickerDialog(...) { DatePicker(...) }</code>."],
                        "api_highlights": [{"param": "onDateSelected", "type": "(Long?) -> Unit", "desc": "Selected millis."}]
                    },
                    {
                        "id": "date-picker-input-modal",
                        "title": "Modal date input",
                        "tag": "android_compose_components_datepicker_inputmodal",
                        "description": "Modal dialog allowing users to enter a date via text input.",
                        "when_to_use": ["Keyboard entry for known dates."],
                        "how_to_use": ["Call <code>DatePicker(initialDisplayMode = DisplayMode.Input)</code>."],
                        "api_highlights": [{"param": "initialDisplayMode", "type": "DisplayMode", "desc": "Display mode."}]
                    },
                    {
                        "id": "date-picker-docked",
                        "title": "Docked date picker",
                        "tag": "android_compose_components_datepicker_docked",
                        "description": "Inline docked date picker anchored to a text input field.",
                        "when_to_use": ["Desktop or tablet forms."],
                        "how_to_use": ["Call <code>DatePickerDocked()</code>."],
                        "api_highlights": [{"param": "state", "type": "DatePickerState", "desc": "State."}]
                    },
                    {
                        "id": "date-range-picker",
                        "title": "Date range picker",
                        "tag": "android_compose_components_datepicker_range",
                        "description": "Modal dialog for selecting a start and end date range.",
                        "when_to_use": ["Booking travel dates."],
                        "how_to_use": ["Call <code>DateRangePickerModal(onDateRangeSelected = {})</code>."],
                        "api_highlights": [{"param": "onDateRangeSelected", "type": "(Pair<Long?, Long?>) -> Unit", "desc": "Selected range."}]
                    }
                ]
            },
            {
                "id": "menu-examples",
                "title": "Menus",
                "file": "Menus.kt",
                "description": "Dropdown menus display a list of choices on temporary surfaces.",
                "when_to_use": ["Action menus or filters."],
                "how_to_use": ["Use <code>DropdownMenu(expanded = ..., onDismissRequest = ...)</code>."],
                "api_highlights": [{"param": "expanded", "type": "Boolean", "desc": "Visibility."}],
                "examples": [
                    {
                        "id": "minimal-dropdown-menu",
                        "title": "Dropdown menu",
                        "tag": "android_compose_components_minimaldropdownmenu",
                        "description": "Basic dropdown menu anchored to an icon button.",
                        "when_to_use": ["More actions menu."],
                        "how_to_use": ["Call <code>MinimalDropdownMenu()</code>."],
                        "api_highlights": [{"param": "expanded", "type": "Boolean", "desc": "State."}]
                    },
                    {
                        "id": "scrollable-dropdown-menu",
                        "title": "Scrollable dropdown menu",
                        "tag": "android_compose_components_longbasicdropdownmenu",
                        "description": "Dropdown menu with a long scrollable list of items.",
                        "when_to_use": ["Large option sets."],
                        "how_to_use": ["Call <code>LongBasicDropdownMenu()</code>."],
                        "api_highlights": [{"param": "expanded", "type": "Boolean", "desc": "State."}]
                    },
                    {
                        "id": "dropdown-menu-with-details",
                        "title": "Dropdown menu with details",
                        "tag": "android_compose_components_dropdownmenuwithdetails",
                        "description": "Dropdown menu items with leading icons, trailing icons, and shortcuts.",
                        "when_to_use": ["Rich action menus."],
                        "how_to_use": ["Call <code>DropdownMenuWithDetails()</code>."],
                        "api_highlights": [{"param": "leadingIcon", "type": "@Composable () -> Unit", "desc": "Leading icon."}]
                    },
                    {
                        "id": "grouped-menu",
                        "title": "Grouped menu with button group",
                        "tag": "android_compose_expressive_components_groupedmenusample",
                        "description": "Material 3 Expressive grouped dropdown menu featuring labeled sections, dividers, and attached button groups.",
                        "when_to_use": ["Complex contextual menus, rich action menus, and social reaction toolbars."],
                        "how_to_use": ["Use <code>DropdownMenuPopup</code> with <code>DropdownMenuGroup</code> and <code>ButtonGroup</code>."],
                        "api_highlights": [
                            {"param": "shapes", "type": "MenuShapes", "desc": "MenuDefaults.groupShape and MenuDefaults.itemShape."}
                        ]
                    }
                ]
            },
            {
                "id": "radio-button",
                "title": "Radio button",
                "file": "RadioButton.kt",
                "description": "Radio buttons allow users to select a single option from a set of mutually exclusive options.",
                "when_to_use": ["Mutually exclusive single selection."],
                "how_to_use": ["Call <code>RadioButton(selected = ..., onClick = ...)</code>."],
                "api_highlights": [{"param": "selected", "type": "Boolean", "desc": "Selected state."}],
                "examples": [
                    {
                        "id": "radio-button-single",
                        "title": "Radio button",
                        "tag": "android_compose_components_radiobuttonsingleselection",
                        "description": "Radio button row with selectable items.",
                        "when_to_use": ["Selecting payment method, delivery speed, etc."],
                        "how_to_use": ["Call <code>RadioButtonSingleSelection()</code>."],
                        "api_highlights": [{"param": "selected", "type": "Boolean", "desc": "Selected state."}]
                    }
                ]
            },
            {
                "id": "slider-examples",
                "title": "Sliders",
                "file": "Slider.kt",
                "description": "Sliders allow users to make selections from a range of values.",
                "when_to_use": ["Volume, brightness, or price range filters."],
                "how_to_use": ["Use <code>Slider</code> or <code>RangeSlider</code>."],
                "api_highlights": [{"param": "value", "type": "Float", "desc": "Current value."}],
                "examples": [
                    {
                        "id": "continuous-slider",
                        "title": "Continuous slider",
                        "tag": "android_compose_components_sliderminimal",
                        "description": "Continuous slider for selecting a numeric value along a bar.",
                        "when_to_use": ["Continuous volume or brightness."],
                        "how_to_use": ["Call <code>Slider(value = position, onValueChange = { position = it })</code>."],
                        "api_highlights": [{"param": "value", "type": "Float", "desc": "Value."}]
                    },
                    {
                        "id": "discrete-slider",
                        "title": "Discrete slider (Steps)",
                        "tag": "android_compose_components_slideradvanced",
                        "description": "Slider with discrete steps and custom thumb.",
                        "when_to_use": ["Rating (1 to 5 stars) or quantized steps."],
                        "how_to_use": ["Call <code>Slider(steps = 4, ...)</code>."],
                        "api_highlights": [{"param": "steps", "type": "Int", "desc": "Number of discrete steps."}]
                    },
                    {
                        "id": "range-slider",
                        "title": "Range slider",
                        "tag": "android_compose_components_rangeslider",
                        "description": "Range slider with two thumbs for selecting a min and max value.",
                        "when_to_use": ["Price range filtering."],
                        "how_to_use": ["Call <code>RangeSlider(value = range, onValueChange = { range = it })</code>."],
                        "api_highlights": [{"param": "value", "type": "ClosedFloatingPointRange<Float>", "desc": "Range."}]
                    }
                ]
            },
            {
                "id": "switch-examples",
                "title": "Switch",
                "file": "Switch.kt",
                "description": "Switches toggle the state of a single item on or off.",
                "when_to_use": ["Settings toggles (Wi-Fi, Bluetooth)."],
                "how_to_use": ["Call <code>Switch(checked = ..., onCheckedChange = ...)</code>."],
                "api_highlights": [{"param": "checked", "type": "Boolean", "desc": "Toggle state."}],
                "examples": [
                    {
                        "id": "minimal-switch",
                        "title": "Minimal switch",
                        "tag": "android_compose_components_switchminimal",
                        "description": "Standard binary toggle switch.",
                        "when_to_use": ["Settings toggle."],
                        "how_to_use": ["Call <code>Switch(checked = checked, onCheckedChange = { checked = it })</code>."],
                        "api_highlights": [{"param": "checked", "type": "Boolean", "desc": "Checked state."}]
                    },
                    {
                        "id": "switch-with-icon",
                        "title": "Switch with icon",
                        "tag": "android_compose_components_switchwithicon",
                        "description": "Switch featuring a custom thumb icon reflecting state.",
                        "when_to_use": ["Visual confirmation of switch state."],
                        "how_to_use": ["Call <code>Switch(thumbContent = { Icon(...) })</code>."],
                        "api_highlights": [{"param": "thumbContent", "type": "@Composable () -> Unit", "desc": "Thumb icon."}]
                    }
                ]
            },
            {
                "id": "time-picker",
                "title": "Time pickers",
                "file": "TimePickers.kt",
                "description": "Time pickers allow users to select time of day in dial or input mode.",
                "when_to_use": ["Alarm or schedule time setting."],
                "how_to_use": ["Call <code>TimePicker(state = rememberTimePickerState())</code>."],
                "api_highlights": [{"param": "state", "type": "TimePickerState", "desc": "Time state."}],
                "examples": [
                    {
                        "id": "dial-time-picker",
                        "title": "Dial time picker",
                        "tag": "android_compose_components_dial",
                        "description": "Time picker featuring an interactive circular clock dial.",
                        "when_to_use": ["Clock dial time selection."],
                        "how_to_use": ["Call <code>TimePicker(state = state)</code>."],
                        "api_highlights": [{"param": "state", "type": "TimePickerState", "desc": "State."}]
                    },
                    {
                        "id": "input-time-picker",
                        "title": "Input time picker",
                        "tag": "android_compose_components_input",
                        "description": "Time picker with text input boxes for hour and minute entry.",
                        "when_to_use": ["Keyboard entry for time."],
                        "how_to_use": ["Call <code>TimeInput(state = state)</code>."],
                        "api_highlights": [{"param": "state", "type": "TimePickerState", "desc": "State."}]
                    }
                ]
            },
            {
                "id": "search-bar",
                "title": "Search bar",
                "file": "SearchBar.kt",
                "description": "Search bars allow users to enter queries and view expandable suggestions.",
                "when_to_use": ["Top of screen search."],
                "how_to_use": ["Use <code>SearchBar</code> or <code>DockedSearchBar</code>."],
                "api_highlights": [{"param": "query", "type": "String", "desc": "Search query."}],
                "examples": [
                    {
                        "id": "search-bar-simple",
                        "title": "Search bar",
                        "tag": "android_compose_components_simple_searchbar",
                        "description": "Full-width expandable search bar with suggestions.",
                        "when_to_use": ["Mobile search screens."],
                        "how_to_use": ["Call <code>SearchBar(...) { ... }</code>."],
                        "api_highlights": [{"param": "inputField", "type": "@Composable () -> Unit", "desc": "Input slot."}]
                    },
                    {
                        "id": "docked-search-bar",
                        "title": "Docked search bar",
                        "tag": "android_compose_components_customizable_searchbar",
                        "description": "Docked search bar anchored to top of screen with popup suggestions.",
                        "when_to_use": ["Tablets or embedded search."],
                        "how_to_use": ["Call <code>CustomizableSearchBar(...)</code>."],
                        "api_highlights": [{"param": "expanded", "type": "Boolean", "desc": "Expanded state."}]
                    }
                ]
            },
            {
                "id": "swipe-to-dismiss",
                "title": "Swipe to dismiss",
                "file": "SwipeToDismissBox.kt",
                "description": "SwipeToDismissBox enables swipe-to-delete gestures on list items.",
                "when_to_use": ["List item deletion or archiving."],
                "how_to_use": ["Call <code>SwipeToDismissBox(state = ..., backgroundContent = ...) { ... }</code>."],
                "api_highlights": [{"param": "state", "type": "SwipeToDismissBoxState", "desc": "Swipe state."}],
                "examples": [
                    {
                        "id": "swipe-to-dismiss-item",
                        "title": "Swipe to dismiss",
                        "tag": "android_compose_components_swipeitemexample",
                        "description": "Swipeable item showing delete background color and icon during swipe.",
                        "when_to_use": ["Swipe-to-delete lists."],
                        "how_to_use": ["Call <code>SwipeToDismissBox(...) { Card { ... } }</code>."],
                        "api_highlights": [{"param": "backgroundContent", "type": "@Composable () -> Unit", "desc": "Background."}]
                    }
                ]
            }
        ]
    }
]

IMPORT_MAP = {
    "toggle-button": "androidx.compose.material3.ToggleButton",
    "elevated-toggle-button": "androidx.compose.material3.ElevatedToggleButton",
    "tonal-toggle-button": "androidx.compose.material3.TonalToggleButton",
    "outlined-toggle-button": "androidx.compose.material3.OutlinedToggleButton",
    "button-with-icon-sample": "androidx.compose.material3.Button",
    "toggle-button-with-icon": "androidx.compose.material3.ToggleButton",
    "xsmall-button-with-icon": "androidx.compose.material3.Button",
    "xsmall-toggle-button-with-icon": "androidx.compose.material3.ToggleButton",
    "medium-button-with-icon": "androidx.compose.material3.Button",
    "medium-toggle-button-with-icon": "androidx.compose.material3.ToggleButton",
    "large-button-with-icon": "androidx.compose.material3.Button",
    "large-toggle-button-with-icon": "androidx.compose.material3.ToggleButton",
    "xlarge-button-with-icon": "androidx.compose.material3.Button",
    "xlarge-toggle-button-with-icon": "androidx.compose.material3.ToggleButton",
    "square-toggle-button": "androidx.compose.material3.ToggleButton",
    "medium-fab": "androidx.compose.material3.MediumFloatingActionButton",
    "icon-button": "androidx.compose.material3.IconButton",
    "toggle-icon-button": "androidx.compose.material3.IconButton",
    "momentary-icon-button": "androidx.compose.material3.IconButton",
    "animated-icon-button": "androidx.compose.material3.IconButton",
    "animated-toggle-icon-button": "androidx.compose.material3.IconToggleButton",
    "determinate-linear-wavy-indicator": "androidx.compose.material3.LinearWavyProgressIndicator",
    "indeterminate-linear-wavy-indicator": "androidx.compose.material3.LinearWavyProgressIndicator",
    "determinate-circular-wavy-indicator": "androidx.compose.material3.CircularWavyProgressIndicator",
    "indeterminate-circular-wavy-indicator": "androidx.compose.material3.CircularWavyProgressIndicator",
    "determinate-linear-expressive-indicator": "androidx.compose.material3.LinearProgressIndicator",
    "indeterminate-linear-expressive-indicator": "androidx.compose.material3.LinearProgressIndicator",
    "determinate-circular-expressive-indicator": "androidx.compose.material3.CircularProgressIndicator",
    "indeterminate-circular-expressive-indicator": "androidx.compose.material3.CircularProgressIndicator",
    "center-aligned-top-app-bar-with-subtitle": "androidx.compose.material3.CenterAlignedTopAppBar",
    "always-enter-top-app-bar": "androidx.compose.material3.TopAppBar",
    "medium-flexible-top-app-bar": "androidx.compose.material3.MediumFlexibleTopAppBar",
    "large-flexible-top-app-bar": "androidx.compose.material3.LargeFlexibleTopAppBar",
    "vertical-items-navigation-bar": "androidx.compose.material3.ShortNavigationBar",
    "horizontal-items-navigation-bar": "androidx.compose.material3.ShortNavigationBar",
    "wide-navigation-rail": "androidx.compose.material3.WideNavigationRail",
    "modal-wide-navigation-rail": "androidx.compose.material3.ModalWideNavigationRail",
    "dismissible-modal-wide-navigation-rail": "androidx.compose.material3.ModalWideNavigationRail",
    "grouped-menu": "androidx.compose.material3.DropdownMenuGroup",
    # Actions
    "segmented-button": "androidx.compose.material3.SegmentedButton",
    "single-choice-segmented-button": "androidx.compose.material3.SingleChoiceSegmentedButtonRow",
    "multi-choice-segmented-button": "androidx.compose.material3.MultiChoiceSegmentedButtonRow",
    "button-examples": "androidx.compose.material3.Button",
    "filled-button": "androidx.compose.material3.Button",
    "filled-tonal-button": "androidx.compose.material3.FilledTonalButton",
    "elevated-button": "androidx.compose.material3.ElevatedButton",
    "outlined-button": "androidx.compose.material3.OutlinedButton",
    "text-button": "androidx.compose.material3.TextButton",
    "button-with-animated-shape": "androidx.compose.material3.Button",
    "square-button": "androidx.compose.material3.Button",
    "button-with-icon": "androidx.compose.material3.Button",
    "split-button": "androidx.compose.material3.SplitButtonLayout",
    "button-group": "androidx.compose.material3.ButtonGroup",
    "floating-action-button": "androidx.compose.material3.FloatingActionButton",
    "fab": "androidx.compose.material3.FloatingActionButton",
    "extended-fab": "androidx.compose.material3.ExtendedFloatingActionButton",
    "small-fab": "androidx.compose.material3.SmallFloatingActionButton",
    "large-fab": "androidx.compose.material3.LargeFloatingActionButton",
    "floating-toolbar": "androidx.compose.material3.HorizontalFloatingToolbar",

    # Communication
    "badge-examples": "androidx.compose.material3.Badge",
    "badge": "androidx.compose.material3.Badge",
    "badge-interactive": "androidx.compose.material3.BadgedBox",
    "progress-indicator": "androidx.compose.material3.LinearProgressIndicator",
    "indeterminate-progress-indicator": "androidx.compose.material3.LinearProgressIndicator",
    "determinate-progress-indicator": "androidx.compose.material3.CircularProgressIndicator",
    "loading-indicator": "androidx.compose.material3.LoadingIndicator",
    "contained-loading-indicator": "androidx.compose.material3.ContainedLoadingIndicator",
    "tooltip-examples": "androidx.compose.material3.TooltipBox",
    "plain-tooltip": "androidx.compose.material3.PlainTooltip",
    "rich-tooltip": "androidx.compose.material3.RichTooltip",
    "snackbar-examples": "androidx.compose.material3.SnackbarHost",
    "snackbar": "androidx.compose.material3.SnackbarHost",
    "snackbar-with-action": "androidx.compose.material3.SnackbarHost",

    # Containment
    "bottom-sheet": "androidx.compose.material3.ModalBottomSheet",
    "partial-bottom-sheet": "androidx.compose.material3.ModalBottomSheet",
    "card-examples": "androidx.compose.material3.Card",
    "filled-card": "androidx.compose.material3.Card",
    "elevated-card": "androidx.compose.material3.ElevatedCard",
    "outlined-card": "androidx.compose.material3.OutlinedCard",
    "carousel-examples": "androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel",
    "multi-browse-carousel": "androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel",
    "uncontained-carousel": "androidx.compose.material3.carousel.HorizontalUncontainedCarousel",
    "dialog-examples": "androidx.compose.material3.AlertDialog",
    "alert-dialog": "androidx.compose.material3.AlertDialog",
    "minimal-dialog": "androidx.compose.ui.window.Dialog",
    "dialog-with-image": "androidx.compose.material3.AlertDialog",
    "divider-examples": "androidx.compose.material3.HorizontalDivider",
    "horizontal-divider": "androidx.compose.material3.HorizontalDivider",
    "vertical-divider": "androidx.compose.material3.VerticalDivider",
    "scaffold-example": "androidx.compose.material3.Scaffold",
    "scaffold": "androidx.compose.material3.Scaffold",
    "list-examples": "androidx.compose.material3.ListItem",
    "list-item": "androidx.compose.material3.ListItem",
    "list-item-headline-only": "androidx.compose.material3.ListItem",
    "list-item-with-supporting-text": "androidx.compose.material3.ListItem",
    "list-item-with-leading-icon": "androidx.compose.material3.ListItem",
    "list-item-with-trailing-checkbox": "androidx.compose.material3.ListItem",
    "swipe-to-dismiss": "androidx.compose.material3.SwipeToDismissBox",
    "swipe-to-dismiss-box": "androidx.compose.material3.SwipeToDismissBox",
    "swipe-to-dismiss-item": "androidx.compose.material3.SwipeToDismissBox",
    "pull-to-refresh": "androidx.compose.material3.pulltorefresh.PullToRefreshBox",
    "pull-to-refresh-box": "androidx.compose.material3.pulltorefresh.PullToRefreshBox",
    "pull-to-refresh-custom-indicator": "androidx.compose.material3.pulltorefresh.PullToRefreshBox",

    # Navigation
    "app-bar-examples": "androidx.compose.material3.TopAppBar",
    "center-aligned-top-app-bar": "androidx.compose.material3.CenterAlignedTopAppBar",
    "small-top-app-bar": "androidx.compose.material3.TopAppBar",
    "medium-top-app-bar": "androidx.compose.material3.MediumTopAppBar",
    "large-top-app-bar": "androidx.compose.material3.LargeTopAppBar",
    "bottom-app-bar": "androidx.compose.material3.BottomAppBar",
    "bottom-app-bar-simple": "androidx.compose.material3.BottomAppBar",
    "bottom-app-bar-with-fab": "androidx.compose.material3.BottomAppBar",
    "navigation-examples": "androidx.compose.material3.NavigationBar",
    "navigation-bar": "androidx.compose.material3.NavigationBar",
    "navigation-rail": "androidx.compose.material3.NavigationRail",
    "navigation-drawer": "androidx.compose.material3.ModalNavigationDrawer",
    "modal-navigation-drawer": "androidx.compose.material3.ModalNavigationDrawer",
    "search-bar": "androidx.compose.material3.SearchBar",
    "search-bar-simple": "androidx.compose.material3.SearchBar",
    "docked-search-bar": "androidx.compose.material3.DockedSearchBar",
    "tab-examples": "androidx.compose.material3.PrimaryTabRow",
    "primary-tab-row": "androidx.compose.material3.PrimaryTabRow",
    "secondary-tab-row": "androidx.compose.material3.SecondaryTabRow",

    # Selection
    "checkbox-examples": "androidx.compose.material3.Checkbox",
    "checkbox": "androidx.compose.material3.Checkbox",
    "parent-checkbox": "androidx.compose.material3.TriStateCheckbox",
    "chip-examples": "androidx.compose.material3.AssistChip",
    "assist-chip": "androidx.compose.material3.AssistChip",
    "filter-chip": "androidx.compose.material3.FilterChip",
    "input-chip": "androidx.compose.material3.InputChip",
    "suggestion-chip": "androidx.compose.material3.SuggestionChip",
    "date-picker": "androidx.compose.material3.DatePicker",
    "date-picker-modal": "androidx.compose.material3.DatePickerDialog",
    "date-picker-input-modal": "androidx.compose.material3.DatePicker",
    "date-picker-docked": "androidx.compose.material3.DatePicker",
    "date-range-picker": "androidx.compose.material3.DateRangePicker",
    "time-picker": "androidx.compose.material3.TimePicker",
    "dial-time-picker": "androidx.compose.material3.TimePicker",
    "input-time-picker": "androidx.compose.material3.TimeInput",
    "menu-examples": "androidx.compose.material3.DropdownMenu",
    "minimal-dropdown-menu": "androidx.compose.material3.DropdownMenu",
    "scrollable-dropdown-menu": "androidx.compose.material3.DropdownMenu",
    "dropdown-menu-with-details": "androidx.compose.material3.DropdownMenu",
    "radio-button": "androidx.compose.material3.RadioButton",
    "radio-button-single": "androidx.compose.material3.RadioButton",
    "slider-examples": "androidx.compose.material3.Slider",
    "continuous-slider": "androidx.compose.material3.Slider",
    "discrete-slider": "androidx.compose.material3.Slider",
    "range-slider": "androidx.compose.material3.RangeSlider",
    "switch-examples": "androidx.compose.material3.Switch",
    "minimal-switch": "androidx.compose.material3.Switch",
    "switch-with-icon": "androidx.compose.material3.Switch",

    # Text & Inputs
    "text-field-examples": "androidx.compose.material3.TextField",
    "filled-text-field": "androidx.compose.material3.TextField",
    "outlined-text-field": "androidx.compose.material3.OutlinedTextField",
    "text-field-with-formatting": "androidx.compose.material3.OutlinedTextField",
}

def extract_snippet(filepath, tag):
    """Extracts a snippet bounded by [START tag] and [END tag] from filepath."""
    if not os.path.exists(filepath):
        return ""
    with open(filepath, "r", encoding="utf-8") as f:
        lines = f.readlines()

    in_tag = False
    in_exclude = False
    result = []
    start_pat = re.compile(rf"//\s*\[START\s+{re.escape(tag)}\]")
    end_pat = re.compile(rf"//\s*\[END\s+{re.escape(tag)}\]")

    for line in lines:
        if start_pat.search(line):
            in_tag = True
            continue
        if end_pat.search(line):
            in_tag = False
            continue
        if in_tag:
            if "// [START_EXCLUDE]" in line:
                in_exclude = True
                continue
            if "// [END_EXCLUDE]" in line:
                in_exclude = False
                continue
            if re.search(r"//\s*\[(START|END)[_\s]", line):
                continue
            if not in_exclude:
                result.append(line)

    return "".join(result).strip()

def build_left_sidebar_html(current_page_id=None, is_root=False):
    """Generates the developer.android.com devsite-book-nav left sidebar with search filter."""
    overview_href = "index.html" if is_root else "../index.html"
    overview_active = " active" if is_root else ""
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
                html_parts.append(f"""
                    <li class="nav-item nav-item-parent" data-title="{comp['title'].lower()}">
                        <div class="nav-parent-row">
                            <a href="{comp_href}" class="nav-link{comp_active}">
                                <span class="nav-link-title">{comp['title']}</span>
                            </a>
                            <button type="button" class="nav-sub-toggle" onclick="toggleNavSub(this, event)" aria-label="Toggle {comp['title']} sub options">
                                <span class="nav-sub-arrow">&#9656;</span>
                            </button>
                        </div>
                        <ul class="nav-sub-list" style="display: none;">
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

HTML_TEMPLATE = """<!DOCTYPE html>
<html lang="en" data-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{title} &mdash; Jetpack Compose &mdash; Android Developers</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Google+Sans:wght@400;500;600;700&family=Google+Sans+Text:wght@400;500;600;700&family=Google+Sans+Code:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="../style.css">
</head>
<body>
    <header class="devsite-header">
        <a href="../index.html" class="devsite-brand" title="Android Developers">
            <img src="https://www.gstatic.com/devrel-devsite/prod/v826fb839c0b38141980abf47020a61dc3ff340cbde624f57178f4c01d184ef87/android/images/lockup.png" alt="Android Developers" class="devsite-brand-logo">
            <span class="devsite-brand-badge">Compose Previews</span>
        </a>
        <div class="devsite-header-actions">
            <a href="https://github.com/android/snippets" target="_blank" class="header-btn" title="View android/snippets on GitHub">
                GitHub Repository
            </a>
            <button class="header-btn" onclick="toggleTheme()" id="themeBtn" title="Toggle Light / Dark Mode">
                &#9681; Theme
            </button>
        </div>
    </header>

    <div class="doc-layout">
        {left_sidebar_html}

        <article class="devsite-article">
            <div class="page-header" id="overview">
                <span class="category-tag">{category_name}</span>
                <div class="title-with-tag" style="display: flex; align-items: center; flex-wrap: wrap; gap: 12px; margin-bottom: 12px;">
                    <h1 class="component-title" style="margin-bottom: 0;">{title}</h1>
                    <span class="import-tag" title="Click to copy import" onclick="navigator.clipboard.writeText('{import_symbol}').then(() => {{ const el = this; const old = el.innerHTML; el.innerHTML = '<code>Copied!</code>'; setTimeout(() => el.innerHTML = old, 1500); }})"><code>{import_symbol}</code></span>
                </div>
                <p class="component-lead">{lead_description}</p>
            </div>

            {family_overview_html}

            <section id="preview-screenshot">
                <h2 class="section-heading">Preview Screenshot</h2>
                <div class="screenshot-card">
                    <img src="../screenshots/{screenshot_id}.png" alt="{title} Preview Screenshot" class="preview-img" onerror="this.onerror=null; this.src='../screenshots/{fallback_screenshot_id}.png';">
                </div>
            </section>

            <section id="interactive-preview">
                <h2 class="section-heading">Interactive Preview</h2>
                <p style="color: var(--text-secondary); margin-bottom: 16px;">
                    Test and interact with this running composable live directly in your browser:
                </p>
                <div class="wasm-runner-card">
                    <div class="wasm-runner-header">
                        <span class="wasm-badge">
                            <span class="wasm-dot"></span>
                            Interactive Preview
                        </span>
                        <div class="wasm-theme-picker">
                            <span class="theme-picker-label">Theme:</span>
                            <div class="theme-swatches">
                                <button type="button" class="theme-swatch active" data-preset="android_green" data-color="#3DDC84" title="Android Green" style="background-color: #3DDC84;" onclick="changeWasmTheme('android_green', '#3DDC84', this)"></button>
                                <button type="button" class="theme-swatch" data-preset="baseline_purple" data-color="#6750A4" title="Material 3 Purple" style="background-color: #6750A4;" onclick="changeWasmTheme('baseline_purple', '#6750A4', this)"></button>
                                <button type="button" class="theme-swatch" data-preset="google_blue" data-color="#4285F4" title="Google Blue" style="background-color: #4285F4;" onclick="changeWasmTheme('google_blue', '#4285F4', this)"></button>
                                <button type="button" class="theme-swatch" data-preset="ocean_teal" data-color="#006A6A" title="Ocean Teal" style="background-color: #006A6A;" onclick="changeWasmTheme('ocean_teal', '#006A6A', this)"></button>
                                <button type="button" class="theme-swatch" data-preset="terracotta_rose" data-color="#9C4146" title="Terracotta Rose" style="background-color: #9C4146;" onclick="changeWasmTheme('terracotta_rose', '#9C4146', this)"></button>
                                <button type="button" class="theme-swatch" data-preset="amber_gold" data-color="#825500" title="Amber Gold" style="background-color: #825500;" onclick="changeWasmTheme('amber_gold', '#825500', this)"></button>
                                <button type="button" class="theme-swatch" data-preset="monochrome" data-color="#5F6368" title="Monochrome" style="background-color: #5F6368;" onclick="changeWasmTheme('monochrome', '#5F6368', this)"></button>
                                <label class="theme-swatch theme-custom-label" title="Custom Seed Color">
                                    <input type="color" id="customColorInput" value="#3DDC84" onchange="changeWasmCustomColor(this.value)">
                                    <span class="custom-color-icon">+</span>
                                </label>
                            </div>
                        </div>
                        <div class="wasm-actions">
                            <button class="wasm-btn" onclick="reloadWasm()" title="Reload WASM Canvas">
                                &#8635; Reload
                            </button>
                            <a id="wasmFullscreenBtn" href="../wasm.html?snippet={id}&standalone=true" target="_blank" class="wasm-btn" title="Open Fullscreen in New Tab">
                                &#x26F6; Fullscreen
                            </a>
                        </div>
                    </div>
                    <div class="wasm-frame-container">
                        <iframe id="wasmFrame" src="../wasm.html?snippet={id}" class="wasm-frame" title="{title} Interactive Preview"></iframe>
                    </div>
                    <div class="wasm-runner-footer">
                        <span>Compose Multiplatform (WASM-JS) &bull; Skiko Canvas</span>
                    </div>
                </div>
            </section>

            <section id="when-to-use">
                <h2 class="section-heading">When to Use</h2>
                <ul class="doc-list">
                    {when_to_use_html}
                </ul>
            </section>

            <section id="how-to-use">
                <h2 class="section-heading">How to Use</h2>
                <ul class="doc-list">
                    {how_to_use_html}
                </ul>
            </section>

            <section id="code-snippets">
                <h2 class="section-heading">
                    <span>Code Snippet from <code>{file_name}</code></span>
                </h2>
                <div class="code-container">
                    <div class="code-header">
                        <span>{file_name}</span>
                        <button class="copy-btn" onclick="copyCode()">Copy</button>
                    </div>
                    <pre class="code-block"><code id="codeSnippet" class="language-kotlin">{code_snippet_escaped}</code></pre>
                    <div class="code-footer">
                        <a href="https://github.com/android/snippets/blob/main/compose/snippets/src/main/java/com/example/compose/snippets/components/{file_name}" target="_blank" class="code-link">
                            <span>View full source</span>
                            <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><path d="M19 19H5V5h7V3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2v-7h-2v7zM14 3v2h3.59l-9.83 9.83 1.41 1.41L19 6.41V10h2V3h-7z"/></svg>
                        </a>
                    </div>
                </div>
                <p class="wasm-disclaimer" style="margin-top: 14px; font-size: 0.85rem; color: var(--text-secondary); line-height: 1.5;">
                    Powered by Compose Multiplatform for WebAssembly: Note - there may be slight differences in rendering on an Android device in comparison to web.
                </p>
            </section>

            <section id="api-surface">
                <h2 class="section-heading">Key API Parameters</h2>
                <table class="api-table">
                    <thead>
                        <tr>
                            <th>Parameter</th>
                            <th>Type</th>
                            <th>Description</th>
                        </tr>
                    </thead>
                    <tbody>
                        {api_rows_html}
                    </tbody>
                </table>
            </section>

            <div class="feedback-card">
                <div>
                    <div class="feedback-title">Found a problem with this component or preview?</div>
                    <div class="feedback-desc">Report an issue on GitHub with your current page context pre-filled.</div>
                </div>
                <a href="{report_issue_url}" target="_blank" rel="noopener noreferrer" class="header-btn report-btn" style="padding: 8px 16px; font-weight: 500;">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor" style="vertical-align: -2px;"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/></svg>
                    <span>Report a problem</span>
                </a>
            </div>

            <div class="bottom-nav">
                {prev_link_html}
                <a href="../index.html" class="bottom-nav-link" style="text-align: center;">
                    <span class="bottom-nav-label">Back to</span>
                    <span class="bottom-nav-title">All Components Catalog</span>
                </a>
                {next_link_html}
            </div>
        </article>

        <aside class="devsite-page-nav" id="onThisPageNav" aria-label="On this page">
            <div class="page-nav-title">On this page</div>
            <ul class="page-nav-list">
                <li class="page-nav-item"><a href="#overview" class="page-nav-link active">Overview</a></li>
                {family_nav_item}
                <li class="page-nav-item"><a href="#preview-screenshot" class="page-nav-link">Preview screenshot</a></li>
                <li class="page-nav-item"><a href="#interactive-preview" class="page-nav-link">Interactive preview</a></li>
                <li class="page-nav-item"><a href="#when-to-use" class="page-nav-link">When to use</a></li>
                <li class="page-nav-item"><a href="#how-to-use" class="page-nav-link">How to use</a></li>
                <li class="page-nav-item"><a href="#code-snippets" class="page-nav-link">Code snippet</a></li>
                <li class="page-nav-item"><a href="#api-surface" class="page-nav-link">API parameters</a></li>
            </ul>
        </aside>
    </div>

    <script src="../js/prism.min.js"></script>
    <script src="../js/prism-kotlin.min.js"></script>
    <script>
        function toggleTheme() {{
            const html = document.documentElement;
            const current = html.getAttribute('data-theme') || 'light';
            const next = current === 'light' ? 'dark' : 'light';
            html.setAttribute('data-theme', next);
            localStorage.setItem('theme', next);
            const frame = document.getElementById('wasmFrame');
            if (frame && frame.contentWindow) {{
                frame.contentWindow.postMessage(JSON.stringify({{
                    type: 'SET_DARK_THEME',
                    dark: next === 'dark'
                }}), '*');
            }}
        }}

        let currentWasmPreset = 'android_green';
        let currentWasmColor = '#3DDC84';

        function updateWasmTheme() {{
            const html = document.documentElement;
            const theme = html.getAttribute('data-theme') || 'light';
            const hash = `theme=${{theme}}&preset=${{currentWasmPreset}}&seed=${{encodeURIComponent(currentWasmColor)}}`;

            const fsBtn = document.getElementById('wasmFullscreenBtn');
            if (fsBtn) {{
                const href = fsBtn.getAttribute('href') || '';
                const base = href.split('#')[0];
                fsBtn.setAttribute('href', `${{base}}#${{hash}}`);
            }}

            const frame = document.getElementById('wasmFrame');
            if (frame && frame.contentWindow) {{
                try {{
                    frame.contentWindow.location.hash = hash;
                }} catch (e) {{}}
                try {{
                    frame.contentWindow.postMessage(JSON.stringify({{
                        type: 'SET_THEME',
                        theme: theme,
                        preset: currentWasmPreset,
                        color: currentWasmColor
                    }}), '*');
                }} catch (e) {{}}
            }}
        }}

        function toggleTheme() {{
            const html = document.documentElement;
            const current = html.getAttribute('data-theme') || 'light';
            const next = current === 'light' ? 'dark' : 'light';
            html.setAttribute('data-theme', next);
            localStorage.setItem('theme', next);
            updateWasmTheme();
        }}

        function changeWasmTheme(preset, color, btn) {{
            currentWasmPreset = preset;
            currentWasmColor = color;
            document.querySelectorAll('.theme-swatch').forEach(s => s.classList.remove('active'));
            if (btn) btn.classList.add('active');
            updateWasmTheme();
        }}

        function changeWasmCustomColor(color) {{
            currentWasmPreset = 'custom';
            currentWasmColor = color;
            document.querySelectorAll('.theme-swatch').forEach(s => s.classList.remove('active'));
            updateWasmTheme();
        }}

        function reloadWasm() {{
            const frame = document.getElementById('wasmFrame');
            if (frame) frame.src = frame.src;
        }}

        function copyCode() {{
            const code = document.getElementById('codeSnippet').innerText;
            navigator.clipboard.writeText(code).then(() => {{
                const btn = document.querySelector('.copy-btn');
                btn.textContent = 'Copied!';
                setTimeout(() => {{ btn.textContent = 'Copy'; }}, 2000);
            }});
        }}

        function toggleNavSub(target, event) {{
            if (event) {{
                event.preventDefault();
                event.stopPropagation();
            }}
            const parentItem = target.closest('.nav-item-parent');
            if (!parentItem) return;
            const subList = parentItem.querySelector('.nav-sub-list');
            const arrow = parentItem.querySelector('.nav-sub-arrow');
            if (!subList) return;
            if (subList.style.display === 'none') {{
                subList.style.display = 'block';
                if (arrow) arrow.innerHTML = '&#9662;';
            }} else {{
                subList.style.display = 'none';
                if (arrow) arrow.innerHTML = '&#9656;';
            }}
        }}

        function filterNav(query) {{
            query = query.toLowerCase().trim();
            const categories = document.querySelectorAll('.nav-tree-category');
            categories.forEach(cat => {{
                const items = cat.querySelectorAll('.nav-item');
                let catHasMatch = false;

                items.forEach(item => {{
                    const title = item.getAttribute('data-title') || '';
                    const subList = item.querySelector('.nav-sub-list');
                    const subArrow = item.querySelector('.nav-sub-arrow');
                    const subItems = item.querySelectorAll('.nav-sub-item');
                    let itemMatch = title.includes(query);
                    let subMatch = false;

                    subItems.forEach(sub => {{
                        const subTitle = sub.getAttribute('data-title') || '';
                        const match = subTitle.includes(query);
                        if (match) {{
                            subMatch = true;
                            sub.style.display = 'block';
                        }} else if (query) {{
                            sub.style.display = 'none';
                        }} else {{
                            sub.style.display = 'block';
                        }}
                    }});

                    if (!query) {{
                        item.style.display = 'block';
                        if (subList) {{
                            subList.style.display = 'none';
                            if (subArrow) subArrow.innerHTML = '&#9656;';
                        }}
                    }} else if (itemMatch || subMatch) {{
                        item.style.display = 'block';
                        catHasMatch = true;
                        if (subList) {{
                            subList.style.display = 'block';
                            if (subArrow) subArrow.innerHTML = '&#9662;';
                        }}
                    }} else {{
                        item.style.display = 'none';
                    }}
                }});

                cat.style.display = (!query || catHasMatch) ? 'block' : 'none';
            }});
        }}

        // Initialize theme from storage
        const savedTheme = localStorage.getItem('theme');
        if (savedTheme) {{
            document.documentElement.setAttribute('data-theme', savedTheme);
        }}

        // Scrollspy for Right TOC Nav & Frame Theme Sync
        window.addEventListener('DOMContentLoaded', () => {{
            const frame = document.getElementById('wasmFrame');
            if (frame) {{
                frame.addEventListener('load', () => {{
                    setTimeout(updateWasmTheme, 150);
                }});
            }}
            updateWasmTheme();

            const observer = new IntersectionObserver((entries) => {{
                entries.forEach(entry => {{
                    if (entry.isIntersecting) {{
                        const id = entry.target.getAttribute('id');
                        document.querySelectorAll('.page-nav-link').forEach(link => {{
                            link.classList.toggle('active', link.getAttribute('href') === '#' + id);
                        }});
                    }}
                }});
            }}, {{ rootMargin: '0px 0px -70% 0px' }});

            document.querySelectorAll('article section, .page-header').forEach(section => {{
                observer.observe(section);
            }});
        }});
    </script>
</body>
</html>
"""

SNIPPET_CODE_MAP_FILE = os.path.join(
    ROOT_DIR, "preview/wasm/src/wasmJsMain/kotlin/com/example/compose/preview/wasm/registry/SnippetCodeMap.kt"
)

def escape_kotlin_multiline(s: str) -> str:
    s = s.replace("$", '${"$" + ""}')
    s = s.replace('"""', '${"\\"\\"\\""}')
    return s

def generate_snippet_code_map():
    entries = []
    seen_ids = set()

    for cat in CATEGORIES:
        for comp in cat["components"]:
            file_name = comp["file"]
            real_filepath = os.path.join(SNIPPETS_SRC_DIR, file_name)
            examples = comp.get("examples", [])
            for ex in examples:
                ex_id = ex["id"]
                if ex_id not in seen_ids:
                    code = extract_snippet(real_filepath, ex["tag"])
                    escaped = escape_kotlin_multiline(code)
                    entries.append(f'        "{ex_id}" to """{escaped}"""')
                    seen_ids.add(ex_id)
            comp_id = comp["id"]
            if comp_id not in seen_ids:
                if examples:
                    snippets = [extract_snippet(real_filepath, ex["tag"]) for ex in examples]
                    code = "\n\n".join([s for s in snippets if s])
                else:
                    code = ""
                escaped = escape_kotlin_multiline(code)
                entries.append(f'        "{comp_id}" to """{escaped}"""')
                seen_ids.add(comp_id)

    header = """/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.preview.wasm.registry

object SnippetCodeMap {
    private val codeMap: Map<String, String> = mapOf(
"""
    footer = """
    )

    fun getCode(id: String): String = codeMap[id] ?: "// No source code available for $id"
}
"""
    content = header + ",\n".join(entries) + footer
    with open(SNIPPET_CODE_MAP_FILE, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"Generated SnippetCodeMap.kt with {len(entries)} snippet codes.")

def generate_all_pages():
    generate_snippet_code_map()
    all_pages = []

    # Collect all pages to generate: individual examples + component family overviews
    for cat in CATEGORIES:
        for comp in cat["components"]:
            examples = comp.get("examples", [])
            file_name = comp["file"]
            real_filepath = os.path.join(SNIPPETS_SRC_DIR, file_name)

            # Add each individual example as a dedicated page
            for ex in examples:
                all_pages.append({
                    "id": ex["id"],
                    "title": ex["title"],
                    "category_id": cat["id"],
                    "category_name": cat["name"],
                    "file_name": file_name,
                    "lead_description": ex["description"],
                    "when_to_use": ex.get("when_to_use", comp.get("when_to_use", [])),
                    "how_to_use": ex.get("how_to_use", comp.get("how_to_use", [])),
                    "api_highlights": ex.get("api_highlights", comp.get("api_highlights", [])),
                    "tag": ex["tag"],
                    "parent_comp_id": comp["id"],
                    "parent_comp_title": comp["title"],
                    "screenshot_id": ex["id"],
                    "fallback_screenshot_id": comp["id"],
                    "is_family_overview": False
                })

            # Add the parent component page (overview/hub for this component family)
            all_pages.append({
                "id": comp["id"],
                "title": comp["title"],
                "category_id": cat["id"],
                "category_name": cat["name"],
                "file_name": file_name,
                "lead_description": comp["description"],
                "when_to_use": comp.get("when_to_use", []),
                "how_to_use": comp.get("how_to_use", []),
                "api_highlights": comp.get("api_highlights", []),
                "tag": examples[0]["tag"] if examples else "",
                "examples": examples,
                "screenshot_id": comp["id"],
                "fallback_screenshot_id": comp["id"],
                "is_family_overview": True
            })

    total = len(all_pages)
    print(f"Generating {total} dedicated component & example pages...")

    for i, p in enumerate(all_pages):
        page_id = p["id"]
        title = p["title"]
        cat_id = p["category_id"]
        cat_name = p["category_name"]
        file_name = p["file_name"]
        lead_desc = p["lead_description"]
        real_filepath = os.path.join(SNIPPETS_SRC_DIR, file_name)

        # Code snippet extracted directly from compose/snippets (without START / END comment tags)
        if p["is_family_overview"]:
            # On family overview, extract all snippets in this family
            snippets = []
            for ex in p.get("examples", []):
                code = extract_snippet(real_filepath, ex["tag"])
                if code:
                    snippets.append(code)
            code_snippet = "\n\n".join(snippets)
        else:
            # On individual example page, extract ONLY this specific snippet
            code = extract_snippet(real_filepath, p["tag"])
            code_snippet = code if code else ""

        code_snippet_escaped = html.escape(code_snippet)

        when_to_use_html = "\n".join([f"<li>{item}</li>" for item in p["when_to_use"]])
        how_to_use_html = "\n".join([f"<li>{item}</li>" for item in p["how_to_use"]])

        api_rows = []
        for api in p["api_highlights"]:
            api_rows.append(f"""<tr>
                <td><code>{html.escape(api['param'])}</code></td>
                <td><code>{html.escape(api['type'])}</code></td>
                <td>{html.escape(api['desc'])}</td>
            </tr>""")
        api_rows_html = "\n".join(api_rows)

        # Family overview cards HTML (if component has multiple examples)
        family_overview_html = ""
        family_nav_item = ""
        if p["is_family_overview"] and len(p.get("examples", [])) > 1:
            family_cards = []
            for ex in p["examples"]:
                family_cards.append(f"""
                <a href="{ex['id']}.html" class="example-card">
                    <div class="example-card-title">{html.escape(ex['title'])}</div>
                    <div class="example-card-desc">{html.escape(ex['description'])}</div>
                    <div class="example-card-link">View &rarr;</div>
                </a>
                """)
            family_overview_html = f"""
            <section id="component-types">
                <h2 class="section-heading">Component Types in this Family</h2>
                <div class="examples-grid">
                    {''.join(family_cards)}
                </div>
            </section>
            """
            family_nav_item = '<li class="page-nav-item"><a href="#component-types" class="page-nav-link">Component types</a></li>'

        left_sidebar_html = build_left_sidebar_html(page_id)

        # Prev / Next navigation
        prev_link_html = ""
        if i > 0:
            prev_p = all_pages[i - 1]
            prev_link_html = f"""<a href="{prev_p['id']}.html" class="bottom-nav-link">
                <span class="bottom-nav-label">&larr; Previous</span>
                <span class="bottom-nav-title">{prev_p['title']}</span>
            </a>"""
        else:
            prev_link_html = "<div></div>"

        next_link_html = ""
        if i < total - 1:
            next_p = all_pages[i + 1]
            next_link_html = f"""<a href="{next_p['id']}.html" class="bottom-nav-link" style="text-align: right;">
                <span class="bottom-nav-label">Next &rarr;</span>
                <span class="bottom-nav-title">{next_p['title']}</span>
            </a>"""
        else:
            next_link_html = "<div></div>"

        import_symbol = IMPORT_MAP.get(page_id, IMPORT_MAP.get(p.get("parent_comp_id", ""), f"androidx.compose.material3.{title.replace(' ', '')}"))

        issue_title = urllib.parse.quote(f"[Preview Issue] Problem with {title} ({page_id})")
        issue_body = urllib.parse.quote(
f"""### Problem Report

**Component / Example:** {title} (`{page_id}`)
**Compose Import:** `{import_symbol}`
**Source File:** [`compose/snippets/src/main/java/com/example/compose/snippets/components/{file_name}`](https://github.com/android/snippets/blob/main/compose/snippets/src/main/java/com/example/compose/snippets/components/{file_name})

---

### Description of the Problem
<!-- Please describe what went wrong, what rendering or behavior issue occurred in the WASM preview or screenshot, or what code snippet needs updating -->


### Steps to Reproduce
1. Open component preview for `{title}`
2. 

### Expected Behavior
<!-- What did you expect to happen? -->


### Additional Context
<!-- Any browser or device details -->
"""
        )
        report_issue_url = f"https://github.com/android/snippets/issues/new?title={issue_title}&body={issue_body}"

        page_content = HTML_TEMPLATE.format(
            id=page_id,
            title=title,
            import_symbol=import_symbol,
            report_issue_url=report_issue_url,
            category_id=cat_id,
            category_name=cat_name,
            file_name=file_name,
            lead_description=lead_desc,
            left_sidebar_html=left_sidebar_html,
            family_overview_html=family_overview_html,
            family_nav_item=family_nav_item,
            screenshot_id=p["screenshot_id"],
            fallback_screenshot_id=p["fallback_screenshot_id"],
            when_to_use_html=when_to_use_html,
            how_to_use_html=how_to_use_html,
            code_snippet_escaped=code_snippet_escaped,
            api_rows_html=api_rows_html,
            prev_link_html=prev_link_html,
            next_link_html=next_link_html
        )

        out_path = os.path.join(COMPONENTS_DIR, f"{page_id}.html")
        with open(out_path, "w", encoding="utf-8") as f:
            f.write(page_content)

        dist_out_path = os.path.join(DIST_COMPONENTS_DIR, f"{page_id}.html")
        with open(dist_out_path, "w", encoding="utf-8") as f:
            f.write(page_content)

    print(f"All {total} pages generated successfully!")
    generate_index_page()

def generate_index_page():
    left_sidebar_html = build_left_sidebar_html(current_page_id=None, is_root=True)

    category_sections = []
    for cat in CATEGORIES:
        comp_cards = []
        for comp in cat["components"]:
            examples = comp.get("examples", [])
            # If the component family has multiple examples, add the family overview card first
            if len(examples) > 1:
                comp_cards.append(f"""
                <div class="catalog-card catalog-card-family">
                    <a href="components/{comp['id']}.html" style="text-decoration: none; color: inherit;">
                        <div class="catalog-card-img-container">
                            <img src="screenshots/{comp['id']}.png" alt="{comp['title']} Overview Preview" class="catalog-card-img" loading="lazy" onerror="this.onerror=null; this.src='screenshots/{examples[0]['id']}.png';">
                        </div>
                        <div class="catalog-card-body">
                            <span class="catalog-card-category">{cat['name']} &bull; Overview</span>
                            <h3 class="catalog-card-title">{comp['title']} (All variants)</h3>
                            <p class="catalog-card-desc">{comp['description']}</p>
                        </div>
                    </a>
                </div>
                """)

            # Add each individual example as its own dedicated card
            if examples:
                for ex in examples:
                    comp_cards.append(f"""
                    <div class="catalog-card">
                        <a href="components/{ex['id']}.html" style="text-decoration: none; color: inherit;">
                            <div class="catalog-card-img-container">
                                <img src="screenshots/{ex['id']}.png" alt="{ex['title']} Preview" class="catalog-card-img" loading="lazy" onerror="this.onerror=null; this.src='screenshots/{comp['id']}.png';">
                            </div>
                            <div class="catalog-card-body">
                                <span class="catalog-card-category">{cat['name']}</span>
                                <h3 class="catalog-card-title">{ex['title']}</h3>
                                <p class="catalog-card-desc">{ex['description']}</p>
                            </div>
                        </a>
                    </div>
                    """)
            else:
                comp_cards.append(f"""
                <div class="catalog-card">
                    <a href="components/{comp['id']}.html" style="text-decoration: none; color: inherit;">
                        <div class="catalog-card-img-container">
                            <img src="screenshots/{comp['id']}.png" alt="{comp['title']} Preview" class="catalog-card-img" loading="lazy">
                        </div>
                        <div class="catalog-card-body">
                            <span class="catalog-card-category">{cat['name']}</span>
                            <h3 class="catalog-card-title">{comp['title']}</h3>
                            <p class="catalog-card-desc">{comp['description']}</p>
                        </div>
                    </a>
                </div>
                """)

        category_sections.append(f"""
        <section id="cat-{cat['id']}" style="margin-bottom: 48px;">
            <h2 class="section-heading" style="border-bottom: 2px solid var(--android-green);">{cat['name']}</h2>
            <div class="catalog-grid">
                {''.join(comp_cards)}
            </div>
        </section>
        """)

    index_html = f"""<!DOCTYPE html>
<html lang="en" data-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Jetpack Compose Material Components &mdash; Android Developers</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Google+Sans:wght@400;500;600;700&family=Google+Sans+Text:wght@400;500;600;700&family=Google+Sans+Code:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <header class="devsite-header">
        <a href="index.html" class="devsite-brand" title="Android Developers">
            <img src="https://www.gstatic.com/devrel-devsite/prod/v826fb839c0b38141980abf47020a61dc3ff340cbde624f57178f4c01d184ef87/android/images/lockup.png" alt="Android Developers" class="devsite-brand-logo">
            <span class="devsite-brand-badge">Compose Previews</span>
        </a>
        <div class="devsite-header-actions">
            <a href="https://github.com/android/snippets" target="_blank" class="header-btn" title="View android/snippets on GitHub">
                GitHub Repository
            </a>
            <button class="header-btn" onclick="toggleTheme()">&#9681; Theme</button>
        </div>
    </header>
    <div class="doc-layout">
        {left_sidebar_html}
        <article class="devsite-article">
            <h1 style="font-family: 'Google Sans', sans-serif; font-size: 2.2rem; font-weight: 700; margin-bottom: 8px;">
                Material 3 Components in Jetpack Compose
            </h1>
            <p style="font-size: 1.1rem; color: var(--text-secondary); margin-bottom: 32px;">
                Browse, preview, and test running WebAssembly snippets for official Material 3 Compose components.
            </p>
            {''.join(category_sections)}
            <div class="feedback-card">
                <div>
                    <div class="feedback-title">Found a problem with any component preview or snippet?</div>
                    <div class="feedback-desc">Report an issue on GitHub to help us improve the Android Snippets catalog.</div>
                </div>
                <a href="https://github.com/android/snippets/issues/new?title=%5BPreview%20Issue%5D%20Problem%20on%20Compose%20Previews%20Catalog&body=%23%23%23%20Problem%20Report%0A%0A**Page%3A**%20Compose%20Previews%20Catalog%20Overview%0A%0A---%0A%0A%23%23%23%20Description%20of%20the%20Problem%0A%3C!--%20Please%20describe%20what%20went%20wrong%20or%20what%20needs%20fixing%20--%3E%0A" target="_blank" rel="noopener noreferrer" class="header-btn report-btn" style="padding: 8px 16px; font-weight: 500;">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor" style="vertical-align: -2px;"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/></svg>
                    <span>Report a problem</span>
                </a>
            </div>
        </article>
    </div>
    <script>
        function toggleTheme() {{
            const html = document.documentElement;
            const current = html.getAttribute('data-theme') || 'light';
            const next = current === 'light' ? 'dark' : 'light';
            html.setAttribute('data-theme', next);
            localStorage.setItem('theme', next);
        }}
        function toggleNavSub(target, event) {{
            if (event) {{
                event.preventDefault();
                event.stopPropagation();
            }}
            const parentItem = target.closest('.nav-item-parent');
            if (!parentItem) return;
            const subList = parentItem.querySelector('.nav-sub-list');
            const arrow = parentItem.querySelector('.nav-sub-arrow');
            if (!subList) return;
            if (subList.style.display === 'none') {{
                subList.style.display = 'block';
                if (arrow) arrow.innerHTML = '&#9662;';
            }} else {{
                subList.style.display = 'none';
                if (arrow) arrow.innerHTML = '&#9656;';
            }}
        }}
        function filterNav(query) {{
            query = query.toLowerCase().trim();
            const categories = document.querySelectorAll('.nav-tree-category');
            categories.forEach(cat => {{
                const items = cat.querySelectorAll('.nav-item');
                let catHasMatch = false;

                items.forEach(item => {{
                    const title = item.getAttribute('data-title') || '';
                    const subList = item.querySelector('.nav-sub-list');
                    const subArrow = item.querySelector('.nav-sub-arrow');
                    const subItems = item.querySelectorAll('.nav-sub-item');
                    let itemMatch = title.includes(query);
                    let subMatch = false;

                    subItems.forEach(sub => {{
                        const subTitle = sub.getAttribute('data-title') || '';
                        const match = subTitle.includes(query);
                        if (match) {{
                            subMatch = true;
                            sub.style.display = 'block';
                        }} else if (query) {{
                            sub.style.display = 'none';
                        }} else {{
                            sub.style.display = 'block';
                        }}
                    }});

                    if (!query) {{
                        item.style.display = 'block';
                        if (subList) {{
                            subList.style.display = 'none';
                            if (subArrow) subArrow.innerHTML = '&#9656;';
                        }}
                    }} else if (itemMatch || subMatch) {{
                        item.style.display = 'block';
                        catHasMatch = true;
                        if (subList) {{
                            subList.style.display = 'block';
                            if (subArrow) subArrow.innerHTML = '&#9662;';
                        }}
                    }} else {{
                        item.style.display = 'none';
                    }}
                }});

                cat.style.display = (!query || catHasMatch) ? 'block' : 'none';
            }});
        }}
        const savedTheme = localStorage.getItem('theme');
        if (savedTheme) document.documentElement.setAttribute('data-theme', savedTheme);
    </script>
</body>
</html>
"""

    with open(os.path.join(RESOURCES_DIR, "index.html"), "w", encoding="utf-8") as f:
        f.write(index_html)
    with open(os.path.join(ROOT_DIR, "preview/wasm/build/dist/site/index.html"), "w", encoding="utf-8") as f:
        f.write(index_html)

    style_src = os.path.join(RESOURCES_DIR, "style.css")
    style_dist = os.path.join(ROOT_DIR, "preview/wasm/build/dist/site/style.css")
    if os.path.exists(style_src):
        import shutil
        shutil.copyfile(style_src, style_dist)

    js_src = os.path.join(RESOURCES_DIR, "js")
    js_dist = os.path.join(ROOT_DIR, "preview/wasm/build/dist/site/js")
    if os.path.exists(js_src):
        import shutil
        os.makedirs(js_dist, exist_ok=True)
        for f in os.listdir(js_src):
            shutil.copyfile(os.path.join(js_src, f), os.path.join(js_dist, f))

if __name__ == "__main__":
    generate_all_pages()
