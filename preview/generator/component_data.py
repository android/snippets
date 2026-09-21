"""
Component categories, metadata, and import mappings for Compose Previews generator.
"""

CATEGORIES = [
    {
        "id": "actions",
        "name": "Actions",
        "components": [
            {
                "id": "button-examples",
                "title": "Button",
                "file": "Button.kt",
                "description": "Buttons allow users to trigger a defined action with a single tap. Material 3 provides five distinct types: Filled, Filled Tonal, Elevated, Outlined, and Text.",
                "api_highlights": [
                    {"param": "onClick", "type": "() -> Unit", "desc": "Callback invoked when button is clicked."},
                    {"param": "enabled", "type": "Boolean", "desc": "Controls enabled state."},
                    {"param": "colors", "type": "ButtonColors", "desc": "Container and content colors."},
                    {
                        "param": "elevation",
                        "type": "ButtonElevation?",
                        "desc": "Elevation values for resting/pressed states.",
                    },
                ],
                "examples": [
                    {
                        "id": "filled-button",
                        "title": "Filled button",
                        "tag": "android_compose_components_filledbutton",
                        "description": "Filled buttons have the highest visual impact and are used for the primary, most important action on a screen.",
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                            {
                                "param": "colors",
                                "type": "ButtonColors",
                                "desc": "Defaults to MaterialTheme.colorScheme.primary container.",
                            },
                        ],
                    },
                    {
                        "id": "filled-tonal-button",
                        "title": "Filled tonal button",
                        "tag": "android_compose_components_filledtonalbutton",
                        "description": "Filled tonal buttons are an alternative middle ground between filled and outlined buttons, offering visual prominence for secondary actions.",
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                            {"param": "colors", "type": "ButtonColors", "desc": "Defaults to secondaryContainer."},
                        ],
                    },
                    {
                        "id": "elevated-button",
                        "title": "Elevated button",
                        "tag": "android_compose_components_elevatedbutton",
                        "description": "Elevated buttons are high-emphasis buttons with a drop shadow, useful on patterned or complex backgrounds.",
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                            {"param": "elevation", "type": "ButtonElevation?", "desc": "Shadow elevation."},
                        ],
                    },
                    {
                        "id": "outlined-button",
                        "title": "Outlined button",
                        "tag": "android_compose_components_outlinedbutton",
                        "description": "Outlined buttons are medium-emphasis buttons with a stroke outline, used for secondary actions like 'Cancel' or 'Back'.",
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                            {"param": "border", "type": "BorderStroke?", "desc": "Border stroke customization."},
                        ],
                    },
                    {
                        "id": "text-button",
                        "title": "Text button",
                        "tag": "android_compose_components_textbutton",
                        "description": "Text buttons have the lowest visual impact and are used for lowest-emphasis actions, dialog dismissals, or inside cards.",
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                        ],
                    },
                    {
                        "id": "button-with-animated-shape",
                        "title": "Button with animated shape",
                        "tag": "android_compose_components_buttonwithanimatedshape",
                        "description": "Material 3 Expressive button featuring dynamic animated shape morphing on press, hover, and focus states.",
                        "api_highlights": [
                            {
                                "param": "shapes",
                                "type": "ButtonShapes",
                                "desc": "Expressive animated shape set via ButtonDefaults.shapes().",
                            },
                        ],
                    },
                    {
                        "id": "square-button",
                        "title": "Square button",
                        "tag": "android_compose_components_squarebutton",
                        "description": "Material 3 Expressive square button with subtle corner rounding, ideal for compact grids and toolbars.",
                        "api_highlights": [
                            {"param": "shape", "type": "Shape", "desc": "Square shape via ButtonDefaults.squareShape."},
                        ],
                    },
                    {
                        "id": "button-with-icon",
                        "title": "Button with icon",
                        "tag": "android_compose_components_buttonwithicon",
                        "description": "Material 3 Expressive button with icon and medium container height specification.",
                        "api_highlights": [
                            {
                                "param": "contentPadding",
                                "type": "PaddingValues",
                                "desc": "Calculated via ButtonDefaults.contentPaddingFor().",
                            },
                        ],
                    },
                    {
                        "id": "split-button",
                        "title": "Split button",
                        "tag": "android_compose_components_splitbutton",
                        "description": "Material 3 Expressive split button offering a primary action on the leading side and a dropdown toggle on the trailing side.",
                        "api_highlights": [
                            {
                                "param": "leadingButton",
                                "type": "@Composable () -> Unit",
                                "desc": "Leading primary action button.",
                            },
                            {
                                "param": "trailingButton",
                                "type": "@Composable () -> Unit",
                                "desc": "Trailing dropdown/toggle button.",
                            },
                        ],
                    },
                    {
                        "id": "toggle-button",
                        "title": "Toggle button",
                        "tag": "android_compose_expressive_components_filledtogglebutton",
                        "description": "Material 3 Expressive filled toggle button switching between active and inactive states.",
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Toggle state."},
                            {"param": "onCheckedChange", "type": "(Boolean) -> Unit", "desc": "Toggle callback."},
                        ],
                    },
                    {
                        "id": "elevated-toggle-button",
                        "title": "Elevated toggle button",
                        "tag": "android_compose_expressive_components_elevatedtogglebutton",
                        "description": "Material 3 Expressive elevated toggle button with drop shadow elevation.",
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Toggle state."},
                            {"param": "onCheckedChange", "type": "(Boolean) -> Unit", "desc": "Toggle callback."},
                        ],
                    },
                    {
                        "id": "tonal-toggle-button",
                        "title": "Tonal toggle button",
                        "tag": "android_compose_expressive_components_tonaltogglebutton",
                        "description": "Material 3 Expressive tonal toggle button with medium emphasis secondary container styling.",
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Toggle state."},
                            {"param": "onCheckedChange", "type": "(Boolean) -> Unit", "desc": "Toggle callback."},
                        ],
                    },
                    {
                        "id": "outlined-toggle-button",
                        "title": "Outlined toggle button",
                        "tag": "android_compose_expressive_components_outlinedtogglebutton",
                        "description": "Material 3 Expressive outlined toggle button with subtle stroke border.",
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Toggle state."},
                            {"param": "onCheckedChange", "type": "(Boolean) -> Unit", "desc": "Toggle callback."},
                        ],
                    },
                    {
                        "id": "button-with-icon-sample",
                        "title": "Button with icon",
                        "tag": "android_compose_expressive_components_buttonwithicon",
                        "description": "Material 3 Expressive standard button featuring a start icon with adaptive spacing.",
                        "api_highlights": [
                            {
                                "param": "contentPadding",
                                "type": "PaddingValues",
                                "desc": "Calculated via ButtonDefaults.contentPaddingFor.",
                            },
                        ],
                    },
                    {
                        "id": "toggle-button-with-icon",
                        "title": "Toggle button with icon",
                        "tag": "android_compose_expressive_components_togglebuttonwithicon",
                        "description": "Material 3 Expressive toggle button displaying dynamic checked/unchecked icons.",
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Toggle state."},
                        ],
                    },
                    {
                        "id": "xsmall-button-with-icon",
                        "title": "Extra small button with icon",
                        "tag": "android_compose_expressive_components_xmsallbuttonwithicon",
                        "description": "Material 3 Expressive compact extra-small button designed for dense layouts.",
                        "api_highlights": [
                            {"param": "heightIn", "type": "Dp", "desc": "ButtonDefaults.ExtraSmallContainerHeight"},
                        ],
                    },
                    {
                        "id": "xsmall-toggle-button-with-icon",
                        "title": "Extra small toggle button with icon",
                        "tag": "android_compose_expressive_components_xmsalltogglebuttonwithicon",
                        "description": "Material 3 Expressive compact extra-small toggle button with adaptive shape.",
                        "api_highlights": [
                            {
                                "param": "shapes",
                                "type": "ToggleButtonShapes",
                                "desc": "Shapes sized for extra small container.",
                            },
                        ],
                    },
                    {
                        "id": "medium-button-with-icon",
                        "title": "Medium button with icon",
                        "tag": "android_compose_expressive_components_mediumbuttonwithicon",
                        "description": "Material 3 Expressive medium height button with typography and icon sizing.",
                        "api_highlights": [
                            {"param": "heightIn", "type": "Dp", "desc": "ButtonDefaults.MediumContainerHeight"},
                        ],
                    },
                    {
                        "id": "medium-toggle-button-with-icon",
                        "title": "Medium toggle button with icon",
                        "tag": "android_compose_expressive_components_mediumtogglebuttonwithicon",
                        "description": "Material 3 Expressive medium height toggle button.",
                        "api_highlights": [
                            {
                                "param": "shapes",
                                "type": "ToggleButtonShapes",
                                "desc": "Shapes for medium container height.",
                            },
                        ],
                    },
                    {
                        "id": "large-button-with-icon",
                        "title": "Large button with icon",
                        "tag": "android_compose_expressive_components_largebuttonwithicon",
                        "description": "Material 3 Expressive large container height button for prominent actions.",
                        "api_highlights": [
                            {"param": "heightIn", "type": "Dp", "desc": "ButtonDefaults.LargeContainerHeight"},
                        ],
                    },
                    {
                        "id": "large-toggle-button-with-icon",
                        "title": "Large toggle button with icon",
                        "tag": "android_compose_expressive_components_largetogglebuttonwithicon",
                        "description": "Material 3 Expressive large container height toggle button.",
                        "api_highlights": [
                            {
                                "param": "shapes",
                                "type": "ToggleButtonShapes",
                                "desc": "Shapes for large container height.",
                            },
                        ],
                    },
                    {
                        "id": "xlarge-button-with-icon",
                        "title": "Extra large button with icon",
                        "tag": "android_compose_expressive_components_xlargebuttonwithicon",
                        "description": "Material 3 Expressive extra large button for maximum prominence.",
                        "api_highlights": [
                            {"param": "heightIn", "type": "Dp", "desc": "ButtonDefaults.ExtraLargeContainerHeight"},
                        ],
                    },
                    {
                        "id": "xlarge-toggle-button-with-icon",
                        "title": "Extra large toggle button with icon",
                        "tag": "android_compose_expressive_components_xlargetogglebuttonwithicon",
                        "description": "Material 3 Expressive extra large toggle button.",
                        "api_highlights": [
                            {
                                "param": "shapes",
                                "type": "ToggleButtonShapes",
                                "desc": "Shapes for extra large container height.",
                            },
                        ],
                    },
                    {
                        "id": "square-toggle-button",
                        "title": "Square toggle button",
                        "tag": "android_compose_expressive_components_squaretogglebutton",
                        "description": "Material 3 Expressive square toggle button with morphing corner shapes.",
                        "api_highlights": [
                            {
                                "param": "shapes",
                                "type": "ToggleButtonShapes",
                                "desc": "Square, pressed, and checked shape configuration.",
                            },
                        ],
                    },
                ],
            },
            {
                "id": "floating-action-button",
                "title": "Floating action button",
                "file": "FloatingActionButton.kt",
                "description": "A Floating Action Button (FAB) performs the primary action on a screen. Available in standard, extended, small, and large sizes.",
                "api_highlights": [
                    {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                    {"param": "containerColor", "type": "Color", "desc": "Background color."},
                ],
                "examples": [
                    {
                        "id": "fab",
                        "title": "Floating action button",
                        "tag": "android_compose_components_fab",
                        "description": "Standard 56dp Floating Action Button performing the primary screen action.",
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Callback."},
                        ],
                    },
                    {
                        "id": "extended-fab",
                        "title": "Extended floating action button",
                        "tag": "android_compose_components_extendedfab",
                        "description": "Extended FAB combining an icon and descriptive text label.",
                        "api_highlights": [
                            {"param": "icon", "type": "@Composable () -> Unit", "desc": "Leading icon."},
                            {"param": "text", "type": "@Composable () -> Unit", "desc": "Label text."},
                        ],
                    },
                    {
                        "id": "small-fab",
                        "title": "Small floating action button",
                        "tag": "android_compose_components_smallfab",
                        "description": "Compact FAB for secondary actions or compact viewports.",
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Callback."},
                        ],
                    },
                    {
                        "id": "large-fab",
                        "title": "Large floating action button",
                        "tag": "android_compose_components_largefab",
                        "description": "96dp Large FAB for high prominence on tablets, foldables, and large desktop screens.",
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Callback."},
                        ],
                    },
                    {
                        "id": "floating-toolbar",
                        "title": "Floating toolbar",
                        "tag": "android_compose_components_floatingtoolbar",
                        "description": "Material 3 Expressive horizontal floating toolbar combining quick actions and a vibrant floating action button in an elevated pill surface.",
                        "api_highlights": [
                            {
                                "param": "floatingActionButton",
                                "type": "@Composable () -> Unit",
                                "desc": "FAB slot inside toolbar.",
                            },
                            {
                                "param": "content",
                                "type": "@Composable RowScope.() -> Unit",
                                "desc": "Action icons slot.",
                            },
                        ],
                    },
                    {
                        "id": "medium-fab",
                        "title": "Medium floating action button",
                        "tag": "android_compose_expressive_components_mediumfab",
                        "description": "Material 3 Expressive medium floating action button.",
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                        ],
                    },
                ],
            },
            {
                "id": "icon-button",
                "title": "Icon button",
                "file": "IconButton.kt",
                "description": "Icon buttons allow users to take actions and make choices with a single tap, using compact icon-only visual representation.",
                "api_highlights": [
                    {"param": "onClick", "type": "() -> Unit", "desc": "Action callback."},
                    {
                        "param": "shapes",
                        "type": "IconButtonShapes?",
                        "desc": "Expressive shape morphing configuration.",
                    },
                    {"param": "colors", "type": "IconButtonColors", "desc": "Container and content colors."},
                ],
                "examples": [
                    {
                        "id": "toggle-icon-button",
                        "title": "Toggle icon button",
                        "tag": "android_compose_components_togglebuttonexample",
                        "description": "Icon button that switches between selected and unselected icon states.",
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Click callback."},
                        ],
                    },
                    {
                        "id": "momentary-icon-button",
                        "title": "Momentary icon button",
                        "tag": "android_compose_components_momentaryiconbuttons",
                        "description": "Momentary repeat-on-press icon button for rapid continuous increments/decrements.",
                        "api_highlights": [
                            {
                                "param": "interactionSource",
                                "type": "MutableInteractionSource",
                                "desc": "Interaction state tracking.",
                            },
                        ],
                    },
                    {
                        "id": "animated-icon-button",
                        "title": "Icon button with animated shape",
                        "tag": "android_compose_expressive_components_animatediconbuttons",
                        "description": "Material 3 Expressive icon button with dynamic corner shape morphing on press.",
                        "api_highlights": [
                            {"param": "shapes", "type": "IconButtonShapes", "desc": "IconButtonDefaults.shapes()."},
                        ],
                    },
                    {
                        "id": "animated-toggle-icon-button",
                        "title": "Icon toggle button with animated shape",
                        "tag": "android_compose_expressive_components_animatedtoggleiconbuttons",
                        "description": "Material 3 Expressive icon toggle button with morphing shape between checked and unchecked states.",
                        "api_highlights": [
                            {
                                "param": "shapes",
                                "type": "IconToggleButtonShapes",
                                "desc": "IconButtonDefaults.toggleableShapes().",
                            },
                        ],
                    },
                ],
            },
            {
                "id": "button-group",
                "title": "Connected button group",
                "file": "Button.kt",
                "description": "Connected button groups hold multiple toggle buttons with unified connected shapes, helping users select options or switch views.",
                "api_highlights": [
                    {"param": "checked", "type": "Boolean", "desc": "Active toggle state."},
                    {
                        "param": "onCheckedChange",
                        "type": "(Boolean) -> Unit",
                        "desc": "Callback invoked when toggle state changes.",
                    },
                    {
                        "param": "content",
                        "type": "@Composable ButtonGroupScope.() -> Unit",
                        "desc": "Slot for child toggle buttons.",
                    },
                ],
                "examples": [
                    {
                        "id": "button-group",
                        "title": "Connected button group",
                        "tag": "android_compose_components_buttongroup",
                        "description": "Material 3 Expressive connected button group holding multiple toggle buttons with unified connected shapes.",
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Active toggle state."},
                            {"param": "onCheckedChange", "type": "(Boolean) -> Unit", "desc": "Toggle state callback."},
                        ],
                    },
                ],
            },
        ],
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
                "api_highlights": [
                    {"param": "badge", "type": "@Composable () -> Unit", "desc": "Badge slot."},
                ],
                "examples": [
                    {
                        "id": "badge",
                        "title": "Badge",
                        "tag": "android_compose_components_badge",
                        "description": "Standard badge displaying unread status or small count.",
                        "api_highlights": [
                            {"param": "badge", "type": "@Composable () -> Unit", "desc": "Badge slot."},
                        ],
                    },
                    {
                        "id": "badge-interactive",
                        "title": "Interactive badge",
                        "tag": "android_compose_components_badgeinteractive",
                        "description": "BadgedBox with dynamic numeric count incremented on interaction.",
                        "api_highlights": [
                            {"param": "badge", "type": "@Composable () -> Unit", "desc": "Badge slot."},
                        ],
                    },
                ],
            },
            {
                "id": "progress-indicator",
                "title": "Progress indicators",
                "file": "ProgressIndicator.kt",
                "description": "Progress indicators inform users about ongoing operations.",
                "api_highlights": [
                    {"param": "progress", "type": "() -> Float", "desc": "Progress lambda."},
                ],
                "examples": [
                    {
                        "id": "indeterminate-progress-indicator",
                        "title": "Indeterminate progress indicator",
                        "tag": "android_compose_components_indeterminateindicator",
                        "description": "Circular indicator showing indeterminate background loading.",
                        "api_highlights": [
                            {"param": "color", "type": "Color", "desc": "Indicator color."},
                        ],
                    },
                    {
                        "id": "determinate-progress-indicator",
                        "title": "Determinate progress indicator",
                        "tag": "android_compose_components_determinateindicator",
                        "description": "Linear indicator showing specific progress toward completion.",
                        "api_highlights": [
                            {"param": "progress", "type": "() -> Float", "desc": "Progress value (0.0 to 1.0)."},
                        ],
                    },
                    {
                        "id": "loading-indicator",
                        "title": "Loading indicator",
                        "tag": "android_compose_components_loadingindicator",
                        "description": "Material 3 Expressive loading indicator providing dynamic, indeterminate loading animation.",
                        "api_highlights": [
                            {"param": "modifier", "type": "Modifier", "desc": "Layout modifier."},
                        ],
                    },
                    {
                        "id": "contained-loading-indicator",
                        "title": "Contained loading indicator",
                        "tag": "android_compose_components_containedloadingindicator",
                        "description": "Material 3 Expressive contained loading indicator positioned within an elevated container surface.",
                        "api_highlights": [
                            {"param": "modifier", "type": "Modifier", "desc": "Layout modifier."},
                        ],
                    },
                    {
                        "id": "determinate-linear-wavy-indicator",
                        "title": "Determinate linear wavy progress indicator",
                        "tag": "android_compose_expressive_components_determinatelinearwavyindicator",
                        "description": "Material 3 Expressive linear progress indicator featuring playful wavy animation curves.",
                        "api_highlights": [
                            {
                                "param": "progress",
                                "type": "() -> Float",
                                "desc": "Progress lambda returning 0.0 to 1.0.",
                            },
                        ],
                    },
                    {
                        "id": "indeterminate-linear-wavy-indicator",
                        "title": "Indeterminate linear wavy progress indicator",
                        "tag": "android_compose_expressive_components_indeterminatelinearwavyindicator",
                        "description": "Material 3 Expressive indeterminate linear wavy indicator providing smooth animated wave flow.",
                        "api_highlights": [
                            {"param": "modifier", "type": "Modifier", "desc": "Layout modifier."},
                        ],
                    },
                    {
                        "id": "determinate-circular-wavy-indicator",
                        "title": "Determinate circular wavy progress indicator",
                        "tag": "android_compose_expressive_components_determinatecircularwavyindicator",
                        "description": "Material 3 Expressive circular wavy progress indicator with animated undulating borders.",
                        "api_highlights": [
                            {
                                "param": "progress",
                                "type": "() -> Float",
                                "desc": "Progress lambda returning 0.0 to 1.0.",
                            },
                        ],
                    },
                    {
                        "id": "indeterminate-circular-wavy-indicator",
                        "title": "Indeterminate circular wavy progress indicator",
                        "tag": "android_compose_expressive_components_indeterminatecircularwavyindicator",
                        "description": "Material 3 Expressive indeterminate circular wavy progress indicator.",
                        "api_highlights": [
                            {"param": "modifier", "type": "Modifier", "desc": "Layout modifier."},
                        ],
                    },
                    {
                        "id": "determinate-linear-expressive-indicator",
                        "title": "Determinate linear progress indicator (Expressive)",
                        "tag": "android_compose_expressive_components_determinatelinearindicator",
                        "description": "Material 3 Expressive linear progress indicator with animated progress tracking and slider controls.",
                        "api_highlights": [
                            {"param": "progress", "type": "() -> Float", "desc": "Progress value."},
                        ],
                    },
                    {
                        "id": "indeterminate-linear-expressive-indicator",
                        "title": "Indeterminate linear progress indicator (Expressive)",
                        "tag": "android_compose_expressive_components_indeterminatelinearindicator",
                        "description": "Material 3 Expressive indeterminate linear progress indicator.",
                        "api_highlights": [
                            {"param": "modifier", "type": "Modifier", "desc": "Layout modifier."},
                        ],
                    },
                    {
                        "id": "determinate-circular-expressive-indicator",
                        "title": "Determinate circular progress indicator (Expressive)",
                        "tag": "android_compose_expressive_components_determinatecircularindicator",
                        "description": "Material 3 Expressive circular progress indicator with animated progress state.",
                        "api_highlights": [
                            {"param": "progress", "type": "() -> Float", "desc": "Progress value."},
                        ],
                    },
                    {
                        "id": "indeterminate-circular-expressive-indicator",
                        "title": "Indeterminate circular progress indicator (Expressive)",
                        "tag": "android_compose_expressive_components_indeterminatecircularindicator",
                        "description": "Material 3 Expressive indeterminate circular progress indicator.",
                        "api_highlights": [
                            {"param": "modifier", "type": "Modifier", "desc": "Layout modifier."},
                        ],
                    },
                ],
            },
            {
                "id": "tooltip-examples",
                "title": "Tooltips",
                "file": "Tooltips.kt",
                "description": "Tooltips provide informative text labels on long-press or hover.",
                "api_highlights": [
                    {"param": "tooltip", "type": "@Composable () -> Unit", "desc": "Tooltip content."},
                ],
                "examples": [
                    {
                        "id": "plain-tooltip",
                        "title": "Plain tooltip",
                        "tag": "android_compose_components_plaintooltipexample",
                        "description": "Short plain text label displayed on element hover or long-press.",
                        "api_highlights": [
                            {"param": "tooltip", "type": "@Composable () -> Unit", "desc": "Tooltip slot."},
                        ],
                    },
                    {
                        "id": "rich-tooltip",
                        "title": "Rich tooltip",
                        "tag": "android_compose_components_richtooltipexample",
                        "description": "Rich tooltip with title, body text, and optional action buttons.",
                        "api_highlights": [
                            {"param": "title", "type": "@Composable () -> Unit", "desc": "Title slot."},
                        ],
                    },
                ],
            },
        ],
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
                "api_highlights": [
                    {"param": "sheetState", "type": "SheetState", "desc": "Controls expand/collapse."},
                ],
                "examples": [
                    {
                        "id": "partial-bottom-sheet",
                        "title": "Partial bottom sheet",
                        "tag": "android_compose_components_partialbottomsheet",
                        "description": "Modal bottom sheet that anchors to a partial height before expanding.",
                        "api_highlights": [
                            {"param": "onDismissRequest", "type": "() -> Unit", "desc": "Dismiss callback."},
                        ],
                    },
                ],
            },
            {
                "id": "card-examples",
                "title": "Cards",
                "file": "Card.kt",
                "description": "Cards contain content and actions about a single subject.",
                "api_highlights": [
                    {"param": "colors", "type": "CardColors", "desc": "Card colors."},
                ],
                "examples": [
                    {
                        "id": "filled-card",
                        "title": "Filled card",
                        "tag": "android_compose_components_filledcard",
                        "description": "Filled card with container color distinguishing it from the background.",
                        "api_highlights": [
                            {"param": "colors", "type": "CardColors", "desc": "Card colors."},
                        ],
                    },
                    {
                        "id": "elevated-card",
                        "title": "Elevated card",
                        "tag": "android_compose_components_elevatedcard",
                        "description": "Card with elevation shadow providing visual separation.",
                        "api_highlights": [
                            {"param": "elevation", "type": "CardElevation", "desc": "Elevation values."},
                        ],
                    },
                    {
                        "id": "outlined-card",
                        "title": "Outlined card",
                        "tag": "android_compose_components_outlinedcard",
                        "description": "Card with a subtle outline border for clean surface grouping.",
                        "api_highlights": [
                            {"param": "border", "type": "BorderStroke", "desc": "Border stroke."},
                        ],
                    },
                ],
            },
            {
                "id": "carousel-examples",
                "title": "Carousel",
                "file": "Carousel.kt",
                "description": "Horizontal scrollable card carousel showcasing list items.",
                "api_highlights": [
                    {"param": "state", "type": "CarouselState", "desc": "State tracking."},
                ],
                "examples": [
                    {
                        "id": "multi-browse-carousel",
                        "title": "Multi-browse carousel",
                        "tag": "android_compose_carousel_multi_browse_basic",
                        "description": "Multi-browse carousel displaying multiple items with peek previews.",
                        "api_highlights": [
                            {"param": "preferredItemWidth", "type": "Dp", "desc": "Width of items."},
                        ],
                    },
                    {
                        "id": "uncontained-carousel",
                        "title": "Uncontained carousel",
                        "tag": "android_compose_carousel_uncontained_basic",
                        "description": "Uncontained carousel allowing items to scroll freely past edge boundaries.",
                        "api_highlights": [
                            {"param": "itemSpacing", "type": "Dp", "desc": "Spacing between items."},
                        ],
                    },
                ],
            },
            {
                "id": "dialog-examples",
                "title": "Dialogs",
                "file": "Dialog.kt",
                "description": "Dialogs inform users about a task and can contain critical information or require decisions.",
                "api_highlights": [
                    {"param": "onDismissRequest", "type": "() -> Unit", "desc": "Dismiss callback."},
                ],
                "examples": [
                    {
                        "id": "alert-dialog",
                        "title": "Alert dialog",
                        "tag": "android_compose_components_alertdialog",
                        "description": "Alert dialog with title, text, icon, and confirm/dismiss buttons.",
                        "api_highlights": [
                            {
                                "param": "confirmButton",
                                "type": "@Composable () -> Unit",
                                "desc": "Confirm button slot.",
                            },
                        ],
                    },
                    {
                        "id": "minimal-dialog",
                        "title": "Minimal dialog",
                        "tag": "android_compose_components_minimaldialog",
                        "description": "Minimal custom dialog without pre-styled buttons.",
                        "api_highlights": [
                            {"param": "content", "type": "@Composable () -> Unit", "desc": "Content slot."},
                        ],
                    },
                    {
                        "id": "dialog-with-image",
                        "title": "Dialog with image",
                        "tag": "android_compose_components_dialogwithimage",
                        "description": "Dialog featuring an illustration or header image above content.",
                        "api_highlights": [
                            {"param": "painter", "type": "Painter", "desc": "Image painter."},
                        ],
                    },
                ],
            },
            {
                "id": "divider-examples",
                "title": "Dividers",
                "file": "Divider.kt",
                "description": "Dividers group and separate content into distinct sections.",
                "api_highlights": [
                    {"param": "thickness", "type": "Dp", "desc": "Line thickness."},
                ],
                "examples": [
                    {
                        "id": "horizontal-divider",
                        "title": "Horizontal divider",
                        "tag": "android_compose_components_horizontaldivider",
                        "description": "Horizontal thin line separating content sections vertically.",
                        "api_highlights": [
                            {"param": "color", "type": "Color", "desc": "Line color."},
                        ],
                    },
                    {
                        "id": "vertical-divider",
                        "title": "Vertical divider",
                        "tag": "android_compose_components_verticaldivider",
                        "description": "Vertical thin line separating adjacent elements in a row.",
                        "api_highlights": [
                            {"param": "thickness", "type": "Dp", "desc": "Thickness."},
                        ],
                    },
                ],
            },
            {
                "id": "scaffold-example",
                "title": "Scaffold",
                "file": "Scaffold.kt",
                "description": "Fundamental layout structure providing slots for top bar, bottom bar, FAB, and content.",
                "api_highlights": [
                    {"param": "topBar", "type": "@Composable () -> Unit", "desc": "Top bar slot."},
                ],
                "examples": [
                    {
                        "id": "scaffold",
                        "title": "Scaffold",
                        "tag": "android_compose_components_scaffold",
                        "description": "Standard Scaffold implementation with TopAppBar and FAB.",
                        "api_highlights": [
                            {"param": "floatingActionButton", "type": "@Composable () -> Unit", "desc": "FAB slot."},
                        ],
                    },
                ],
            },
        ],
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
                "api_highlights": [
                    {"param": "title", "type": "@Composable () -> Unit", "desc": "Title slot."},
                ],
                "examples": [
                    {
                        "id": "center-aligned-top-app-bar",
                        "title": "Center-aligned top app bar",
                        "tag": "android_compose_components_centeralignedtopappbar",
                        "description": "Top app bar with centered headline title and action icons.",
                        "api_highlights": [
                            {"param": "title", "type": "@Composable () -> Unit", "desc": "Title slot."},
                        ],
                    },
                    {
                        "id": "small-top-app-bar",
                        "title": "Small top app bar",
                        "tag": "android_compose_components_smalltopappbar",
                        "description": "Standard compact top app bar with start-aligned title.",
                        "api_highlights": [
                            {"param": "navigationIcon", "type": "@Composable () -> Unit", "desc": "Nav icon slot."},
                        ],
                    },
                    {
                        "id": "medium-top-app-bar",
                        "title": "Medium top app bar",
                        "tag": "android_compose_components_mediumtopappbar",
                        "description": "Medium top app bar featuring a larger title area that collapses on scroll.",
                        "api_highlights": [
                            {"param": "scrollBehavior", "type": "TopAppBarScrollBehavior?", "desc": "Scroll collapse."},
                        ],
                    },
                    {
                        "id": "large-top-app-bar",
                        "title": "Large top app bar",
                        "tag": "android_compose_components_largetopappbar",
                        "description": "Large top app bar with prominent headline typography that collapses on scroll.",
                        "api_highlights": [
                            {"param": "scrollBehavior", "type": "TopAppBarScrollBehavior?", "desc": "Scroll collapse."},
                        ],
                    },
                    {
                        "id": "center-aligned-top-app-bar-with-subtitle",
                        "title": "Center-aligned top app bar with subtitle",
                        "tag": "android_compose_expressive_components_centeralignedtopappbarwithsubtitle",
                        "description": "Material 3 Expressive center-aligned top app bar featuring both title and subtitle headers with scroll collapse behavior.",
                        "api_highlights": [
                            {"param": "subtitle", "type": "@Composable () -> Unit", "desc": "Subtitle slot."},
                            {"param": "scrollBehavior", "type": "TopAppBarScrollBehavior", "desc": "Scroll behavior."},
                        ],
                    },
                    {
                        "id": "always-enter-top-app-bar",
                        "title": "Always-enter top app bar",
                        "tag": "android_compose_expressive_components_alwaysentertopappbar",
                        "description": "Material 3 Expressive top app bar with enterAlways scroll behavior, immediately reappearing on scroll up.",
                        "api_highlights": [
                            {
                                "param": "scrollBehavior",
                                "type": "TopAppBarScrollBehavior",
                                "desc": "TopAppBarDefaults.enterAlwaysScrollBehavior().",
                            },
                        ],
                    },
                    {
                        "id": "medium-flexible-top-app-bar",
                        "title": "Medium flexible top app bar",
                        "tag": "android_compose_expressive_components_exituntillcollapsedtopappbar",
                        "description": "Material 3 Expressive medium flexible top app bar with centered title and exitUntilCollapsed behavior.",
                        "api_highlights": [
                            {
                                "param": "titleHorizontalAlignment",
                                "type": "Alignment.Horizontal",
                                "desc": "Horizontal alignment.",
                            },
                            {
                                "param": "scrollBehavior",
                                "type": "TopAppBarScrollBehavior",
                                "desc": "TopAppBarDefaults.exitUntilCollapsedScrollBehavior().",
                            },
                        ],
                    },
                    {
                        "id": "large-flexible-top-app-bar",
                        "title": "Large flexible top app bar",
                        "tag": "android_compose_expressive_components_exituntillcollapsedlargetopappbar",
                        "description": "Material 3 Expressive large flexible top app bar offering high-prominence typography and smooth collapse dynamics.",
                        "api_highlights": [
                            {
                                "param": "scrollBehavior",
                                "type": "TopAppBarScrollBehavior",
                                "desc": "Scroll collapse behavior.",
                            },
                        ],
                    },
                ],
            },
            {
                "id": "navigation-examples",
                "title": "Navigation bar & rail",
                "file": "Navigation.kt",
                "description": "Bottom navigation bar and side navigation rail for app destinations.",
                "api_highlights": [
                    {"param": "selected", "type": "Boolean", "desc": "Active item."},
                ],
                "examples": [
                    {
                        "id": "navigation-bar",
                        "title": "Navigation bar",
                        "tag": "android_compose_components_navigationbarexample",
                        "description": "Bottom navigation bar providing access to 3 to 5 top-level destinations.",
                        "api_highlights": [
                            {"param": "selected", "type": "Boolean", "desc": "Selected state."},
                        ],
                    },
                    {
                        "id": "navigation-rail",
                        "title": "Navigation rail",
                        "tag": "android_compose_components_navigationrailexample",
                        "description": "Side navigation rail suited for tablets and wide screens.",
                        "api_highlights": [
                            {"param": "selected", "type": "Boolean", "desc": "Selected state."},
                        ],
                    },
                    {
                        "id": "vertical-items-navigation-bar",
                        "title": "Vertical items navigation bar",
                        "tag": "android_compose_expressive_components_verticalitemsnavigationbarexample",
                        "description": "Material 3 Expressive navigation bar with vertically stacked icon and text items.",
                        "api_highlights": [
                            {"param": "selected", "type": "Boolean", "desc": "Selection state."},
                        ],
                    },
                    {
                        "id": "horizontal-items-navigation-bar",
                        "title": "Horizontal items navigation bar",
                        "tag": "android_compose_expressive_components_horizontalitemsnavigationbarexample",
                        "description": "Material 3 Expressive navigation bar with horizontal icon and label layout.",
                        "api_highlights": [
                            {
                                "param": "labelPosition",
                                "type": "NavigationItemLabelPosition",
                                "desc": "Label position.",
                            },
                        ],
                    },
                    {
                        "id": "wide-navigation-rail",
                        "title": "Wide navigation rail",
                        "tag": "android_compose_expressive_components_widenavigationrailexample",
                        "description": "Material 3 Expressive expandable wide navigation rail for medium and large screens.",
                        "api_highlights": [
                            {
                                "param": "state",
                                "type": "WideNavigationRailState",
                                "desc": "Tracks expanded / collapsed rail state.",
                            },
                        ],
                    },
                    {
                        "id": "modal-wide-navigation-rail",
                        "title": "Modal wide navigation rail",
                        "tag": "android_compose_expressive_components_modalwidenavigationrailexample",
                        "description": "Material 3 Expressive modal wide navigation rail sliding open over content.",
                        "api_highlights": [
                            {"param": "state", "type": "WideNavigationRailState", "desc": "Rail state controller."},
                        ],
                    },
                    {
                        "id": "dismissible-modal-wide-navigation-rail",
                        "title": "Dismissible modal wide navigation rail",
                        "tag": "android_compose_expressive_components_dismissiblemodalwidenavigationrailexample",
                        "description": "Material 3 Expressive modal wide navigation rail that completely collapses offscreen when dismissed.",
                        "api_highlights": [
                            {
                                "param": "hideOnCollapse",
                                "type": "Boolean",
                                "desc": "Hides rail entirely when collapsed.",
                            },
                        ],
                    },
                ],
            },
            {
                "id": "navigation-drawer",
                "title": "Navigation drawer",
                "file": "NavigationDrawer.kt",
                "description": "Modal navigation drawer for navigation destinations on medium and large screens.",
                "api_highlights": [
                    {"param": "drawerState", "type": "DrawerState", "desc": "Drawer state."},
                ],
                "examples": [
                    {
                        "id": "modal-navigation-drawer",
                        "title": "Modal navigation drawer",
                        "tag": "android_compose_components_detaileddrawerexample",
                        "description": "Modal navigation drawer sliding from screen edge.",
                        "api_highlights": [
                            {"param": "drawerState", "type": "DrawerState", "desc": "State."},
                        ],
                    },
                ],
            },
        ],
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
                "api_highlights": [
                    {"param": "checked", "type": "Boolean", "desc": "Checked state."},
                ],
                "examples": [
                    {
                        "id": "checkbox",
                        "title": "Checkbox",
                        "tag": "android_compose_components_checkbox_minimal",
                        "description": "Standard binary checkbox for selecting or deselecting a single item.",
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Checked state."},
                        ],
                    },
                    {
                        "id": "parent-checkbox",
                        "title": "Parent checkbox (Tri-State)",
                        "tag": "android_compose_components_checkbox_parent",
                        "description": "TriStateCheckbox controlling multiple child checkboxes.",
                        "api_highlights": [
                            {"param": "state", "type": "ToggleableState", "desc": "On / Off / Indeterminate."},
                        ],
                    },
                ],
            },
            {
                "id": "chip-examples",
                "title": "Chips",
                "file": "Chip.kt",
                "description": "Chips help users enter information, make selections, filter content, or trigger actions.",
                "api_highlights": [
                    {"param": "label", "type": "@Composable () -> Unit", "desc": "Label slot."},
                ],
                "examples": [
                    {
                        "id": "assist-chip",
                        "title": "Assist chip",
                        "tag": "android_compose_components_assistchip",
                        "description": "Assist chip triggering an action related to primary content.",
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action."},
                        ],
                    },
                    {
                        "id": "filter-chip",
                        "title": "Filter chip",
                        "tag": "android_compose_components_filterchip",
                        "description": "Filter chip allowing users to filter content by selecting tags.",
                        "api_highlights": [
                            {"param": "selected", "type": "Boolean", "desc": "Selected state."},
                        ],
                    },
                    {
                        "id": "input-chip",
                        "title": "Input chip",
                        "tag": "android_compose_components_inputchip",
                        "description": "Input chip representing a complex piece of information with trailing dismiss icon.",
                        "api_highlights": [
                            {"param": "onDismiss", "type": "() -> Unit", "desc": "Dismiss callback."},
                        ],
                    },
                    {
                        "id": "suggestion-chip",
                        "title": "Suggestion chip",
                        "tag": "android_compose_components_suggestionchip",
                        "description": "Suggestion chip presenting dynamically generated recommendations.",
                        "api_highlights": [
                            {"param": "onClick", "type": "() -> Unit", "desc": "Action."},
                        ],
                    },
                ],
            },
            {
                "id": "date-picker",
                "title": "Date pickers",
                "file": "DatePickers.kt",
                "description": "Date pickers allow users to select dates from a calendar interface.",
                "api_highlights": [
                    {"param": "state", "type": "DatePickerState", "desc": "State."},
                ],
                "examples": [
                    {
                        "id": "date-picker-modal",
                        "title": "Modal date picker",
                        "tag": "android_compose_components_datepicker_modal",
                        "description": "Modal dialog for selecting a single calendar date.",
                        "api_highlights": [
                            {"param": "onDateSelected", "type": "(Long?) -> Unit", "desc": "Selected millis."},
                        ],
                    },
                    {
                        "id": "date-picker-input-modal",
                        "title": "Modal date input",
                        "tag": "android_compose_components_datepicker_inputmodal",
                        "description": "Modal dialog allowing users to enter a date via text input.",
                        "api_highlights": [
                            {"param": "initialDisplayMode", "type": "DisplayMode", "desc": "Display mode."},
                        ],
                    },
                    {
                        "id": "date-picker-docked",
                        "title": "Docked date picker",
                        "tag": "android_compose_components_datepicker_docked",
                        "description": "Inline docked date picker anchored to a text input field.",
                        "api_highlights": [
                            {"param": "state", "type": "DatePickerState", "desc": "State."},
                        ],
                    },
                    {
                        "id": "date-range-picker",
                        "title": "Date range picker",
                        "tag": "android_compose_components_datepicker_range",
                        "description": "Modal dialog for selecting a start and end date range.",
                        "api_highlights": [
                            {
                                "param": "onDateRangeSelected",
                                "type": "(Pair<Long?, Long?>) -> Unit",
                                "desc": "Selected range.",
                            },
                        ],
                    },
                ],
            },
            {
                "id": "menu-examples",
                "title": "Menus",
                "file": "Menus.kt",
                "description": "Dropdown menus display a list of choices on temporary surfaces.",
                "api_highlights": [
                    {"param": "expanded", "type": "Boolean", "desc": "Visibility."},
                ],
                "examples": [
                    {
                        "id": "minimal-dropdown-menu",
                        "title": "Dropdown menu",
                        "tag": "android_compose_components_minimaldropdownmenu",
                        "description": "Basic dropdown menu anchored to an icon button.",
                        "api_highlights": [
                            {"param": "expanded", "type": "Boolean", "desc": "State."},
                        ],
                    },
                    {
                        "id": "scrollable-dropdown-menu",
                        "title": "Scrollable dropdown menu",
                        "tag": "android_compose_components_longbasicdropdownmenu",
                        "description": "Dropdown menu with a long scrollable list of items.",
                        "api_highlights": [
                            {"param": "expanded", "type": "Boolean", "desc": "State."},
                        ],
                    },
                    {
                        "id": "dropdown-menu-with-details",
                        "title": "Dropdown menu with details",
                        "tag": "android_compose_components_dropdownmenuwithdetails",
                        "description": "Dropdown menu items with leading icons, trailing icons, and shortcuts.",
                        "api_highlights": [
                            {"param": "leadingIcon", "type": "@Composable () -> Unit", "desc": "Leading icon."},
                        ],
                    },
                    {
                        "id": "grouped-menu",
                        "title": "Grouped menu with button group",
                        "tag": "android_compose_expressive_components_groupedmenusample",
                        "description": "Material 3 Expressive grouped dropdown menu featuring labeled sections, dividers, and attached button groups.",
                        "api_highlights": [
                            {
                                "param": "shapes",
                                "type": "MenuShapes",
                                "desc": "MenuDefaults.groupShape and MenuDefaults.itemShape.",
                            },
                        ],
                    },
                ],
            },
            {
                "id": "radio-button",
                "title": "Radio button",
                "file": "RadioButton.kt",
                "description": "Radio buttons allow users to select a single option from a set of mutually exclusive options.",
                "api_highlights": [
                    {"param": "selected", "type": "Boolean", "desc": "Selected state."},
                ],
                "examples": [
                    {
                        "id": "radio-button-single",
                        "title": "Radio button",
                        "tag": "android_compose_components_radiobuttonsingleselection",
                        "description": "Radio button row with selectable items.",
                        "api_highlights": [
                            {"param": "selected", "type": "Boolean", "desc": "Selected state."},
                        ],
                    },
                ],
            },
            {
                "id": "slider-examples",
                "title": "Sliders",
                "file": "Slider.kt",
                "description": "Sliders allow users to make selections from a range of values.",
                "api_highlights": [
                    {"param": "value", "type": "Float", "desc": "Current value."},
                ],
                "examples": [
                    {
                        "id": "continuous-slider",
                        "title": "Continuous slider",
                        "tag": "android_compose_components_sliderminimal",
                        "description": "Continuous slider for selecting a numeric value along a bar.",
                        "api_highlights": [
                            {"param": "value", "type": "Float", "desc": "Value."},
                        ],
                    },
                    {
                        "id": "discrete-slider",
                        "title": "Discrete slider (Steps)",
                        "tag": "android_compose_components_slideradvanced",
                        "description": "Slider with discrete steps and custom thumb.",
                        "api_highlights": [
                            {"param": "steps", "type": "Int", "desc": "Number of discrete steps."},
                        ],
                    },
                    {
                        "id": "range-slider",
                        "title": "Range slider",
                        "tag": "android_compose_components_rangeslider",
                        "description": "Range slider with two thumbs for selecting a min and max value.",
                        "api_highlights": [
                            {"param": "value", "type": "ClosedFloatingPointRange<Float>", "desc": "Range."},
                        ],
                    },
                ],
            },
            {
                "id": "switch-examples",
                "title": "Switch",
                "file": "Switch.kt",
                "description": "Switches toggle the state of a single item on or off.",
                "api_highlights": [
                    {"param": "checked", "type": "Boolean", "desc": "Toggle state."},
                ],
                "examples": [
                    {
                        "id": "minimal-switch",
                        "title": "Minimal switch",
                        "tag": "android_compose_components_switchminimal",
                        "description": "Standard binary toggle switch.",
                        "api_highlights": [
                            {"param": "checked", "type": "Boolean", "desc": "Checked state."},
                        ],
                    },
                    {
                        "id": "switch-with-icon",
                        "title": "Switch with icon",
                        "tag": "android_compose_components_switchwithicon",
                        "description": "Switch featuring a custom thumb icon reflecting state.",
                        "api_highlights": [
                            {"param": "thumbContent", "type": "@Composable () -> Unit", "desc": "Thumb icon."},
                        ],
                    },
                ],
            },
            {
                "id": "time-picker",
                "title": "Time pickers",
                "file": "TimePickers.kt",
                "description": "Time pickers allow users to select time of day in dial or input mode.",
                "api_highlights": [
                    {"param": "state", "type": "TimePickerState", "desc": "Time state."},
                ],
                "examples": [
                    {
                        "id": "dial-time-picker",
                        "title": "Dial time picker",
                        "tag": "android_compose_components_dial",
                        "description": "Time picker featuring an interactive circular clock dial.",
                        "api_highlights": [
                            {"param": "state", "type": "TimePickerState", "desc": "State."},
                        ],
                    },
                    {
                        "id": "input-time-picker",
                        "title": "Input time picker",
                        "tag": "android_compose_components_input",
                        "description": "Time picker with text input boxes for hour and minute entry.",
                        "api_highlights": [
                            {"param": "state", "type": "TimePickerState", "desc": "State."},
                        ],
                    },
                ],
            },
            {
                "id": "search-bar",
                "title": "Search bar",
                "file": "SearchBar.kt",
                "description": "Search bars allow users to enter queries and view expandable suggestions.",
                "api_highlights": [
                    {"param": "query", "type": "String", "desc": "Search query."},
                ],
                "examples": [
                    {
                        "id": "search-bar-simple",
                        "title": "Search bar",
                        "tag": "android_compose_components_simple_searchbar",
                        "description": "Full-width expandable search bar with suggestions.",
                        "api_highlights": [
                            {"param": "inputField", "type": "@Composable () -> Unit", "desc": "Input slot."},
                        ],
                    },
                    {
                        "id": "docked-search-bar",
                        "title": "Docked search bar",
                        "tag": "android_compose_components_customizable_searchbar",
                        "description": "Docked search bar anchored to top of screen with popup suggestions.",
                        "api_highlights": [
                            {"param": "expanded", "type": "Boolean", "desc": "Expanded state."},
                        ],
                    },
                ],
            },
            {
                "id": "swipe-to-dismiss",
                "title": "Swipe to dismiss",
                "file": "SwipeToDismissBox.kt",
                "description": "SwipeToDismissBox enables swipe-to-delete gestures on list items.",
                "api_highlights": [
                    {"param": "state", "type": "SwipeToDismissBoxState", "desc": "Swipe state."},
                ],
                "examples": [
                    {
                        "id": "swipe-to-dismiss-item",
                        "title": "Swipe to dismiss",
                        "tag": "android_compose_components_swipeitemexample",
                        "description": "Swipeable item showing delete background color and icon during swipe.",
                        "api_highlights": [
                            {"param": "backgroundContent", "type": "@Composable () -> Unit", "desc": "Background."},
                        ],
                    },
                ],
            },
        ],
    },
    {
        "id": "theming",
        "name": "Styles & Theming",
        "components": [
            {
                "id": "theme-builder",
                "title": "Theme builder",
                "file": "ThemeBuilder.kt",
                "description": "Interactive Material Theme Builder for Jetpack Compose. Customize seed colors, inspect dynamic color roles, preview real-world app mockups, and copy generated Compose theme code.",
                "api_highlights": [
                    {"param": "colorScheme", "type": "ColorScheme", "desc": "Light or dark Material 3 color scheme."},
                    {
                        "param": "MaterialTheme",
                        "type": "@Composable () -> Unit",
                        "desc": "The root theme wrapper applying colorScheme, typography, and shapes.",
                    },
                ],
                "examples": [
                    {
                        "id": "theme-builder",
                        "title": "Theme builder",
                        "tag": "android_compose_components_themebuilder",
                        "description": "Interactive Material Theme Builder for Jetpack Compose with real-world app preview and code generator.",
                        "api_highlights": [
                            {"param": "colorScheme", "type": "ColorScheme", "desc": "Material 3 color scheme."},
                        ],
                    },
                ],
            },
        ],
    },
]

IMPORT_MAP = {
    "theme-builder": "androidx.compose.material3.MaterialTheme",
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

