# Compose Best Practices & Review Guidelines
This report extracts actionable best practices for Jetpack Compose development based on code review comments by `riggaroo` in the `android/snippets` repository.

## Naming Conventions
### Composable functions should always start with capital letters
**Reviewer Comment:**
> Composable functions should always start with capital letters :)

**Code Context:**
```diff
+        Text(
+            text = "Some text",
+            modifier = Modifier
+                .onVisibilityChanged { visible ->
+                    if (visible) {
+                        // Do something if visible
+                    } else {
+                        // Do something if not visible
+                    }
+                }
+                .padding(vertical = 8.dp)
+        )
+        // [END android_compose_modifiers_onVisibilityChanged]
+    }
+}
+
+private object OnVisibilityChangedMinFractionVisible {
+
+    @Composable
+    fun onVisibilityChangedModifierMinFraction(modifier: Modifier = Modifier) {
```

### Composable to start with Capital letter
**Reviewer Comment:**
> Composable to start with Capital letter - also add Example to the end of the name?

**Code Context:**
```diff
+                        .padding(vertical = 16.dp)
+                ) {
+                    Text(
+                        text = "Sample Text",
+                        modifier = Modifier.padding(horizontal = 16.dp)
+                    )
+                }
+            }
+        }
+        // [END android_compose_modifiers_onVisibilityChangedMinFraction]
+    }
+}
+
+private object onVisibilityChangedMinDuration {
+
+    val MutedPlum = Color(0xFF7B4B6B)
+    val PalePink = Color(0xFFF3E9EB)
+
+    @Composable
+    fun onVisibilityChangedMinDuration(
```

---

## Parameter Ordering
### more important parameters should come first - id should be before modifier here
**Reviewer Comment:**
> more important parameters should come first - id should be before modifier here

**Code Context:**
```diff
+                    .background(background),
+                contentAlignment = Alignment.Center,
+            ) {
+                // [START_EXCLUDE]
+                Image(
+                    painter = painterResource(id = imageRes),
+                    contentDescription = "Androidify Bot",
+                )
+                // [END_EXCLUDE]
+            }
+        }
+        // [END android_compose_modifiers_onVisibilityChangedMinDuration]
+    }
+}
+
+
+private object OnFirstVisibleSample {
+
+    @Composable
+    fun OnFirstVisibleExample(modifier: Modifier = Modifier, id: Int) {
```

### more important parameters first, imageRes before modifier
**Reviewer Comment:**
> more important parameters first, imageRes before modifier

**Code Context:**
```diff
+                    Text(
+                        text = "Sample Text",
+                        modifier = Modifier.padding(horizontal = 16.dp)
+                    )
+                }
+            }
+        }
+        // [END android_compose_modifiers_onVisibilityChangedMinFraction]
+    }
+}
+
+private object onVisibilityChangedMinDuration {
+
+    val MutedPlum = Color(0xFF7B4B6B)
+    val PalePink = Color(0xFFF3E9EB)
+
+    @Composable
+    fun onVisibilityChangedMinDuration(
+        modifier: Modifier = Modifier,
+        @DrawableRes imageRes: Int,
```

---

## Modifiers
### Remove modifier here or set it to modifier: Modifier = Modifier
**Reviewer Comment:**
> Remove modifier here or set it to modifier: Modifier = Modifier

**Code Context:**
```diff
+                    fontSize = 20.sp,
+                    fontWeight = FontWeight.Bold
+                )
+                Spacer(modifier = Modifier.height(8.dp))
+                Text(
+                    text = "Y: ${offset.value.y.roundToInt()}",
+                    color = Color.White,
+                    fontSize = 20.sp,
+                    fontWeight = FontWeight.Bold
+                )
+            }
+        }
+    }
+}
+// [END android_compose_touchinput_scroll_scrollable2D_basic]
+
+
+// [START android_compose_touchinput_scroll_scrollable2D_pan_large_viewport]
+@Composable
+fun Panning2DImage(modifier: Modifier) {
```

### this is the only snippet that has a modifier extra on it - remove it for simplicity of the snippets
**Reviewer Comment:**
> this is the only snippet that has a modifier extra on it - remove it for simplicity of the snippets

**Code Context:**
```diff
+                Spacer(modifier = Modifier.height(8.dp))
+                Text(
+                    text = "Y: ${offset.y.roundToInt()}",
+                    color = Color.White,
+                    fontSize = 20.sp,
+                    // [START_EXCLUDE]
+                    fontWeight = FontWeight.Bold
+                    // [END_EXCLUDE]
+                )
+            }
+        }
+    }
+}
+// [END android_compose_touchinput_scroll_scrollable2D_basic]
+
+
+@Preview
+// [START android_compose_touchinput_scroll_scrollable2D_pan_large_viewport]
+@Composable
+private fun Panning2DImage(modifier: Modifier = Modifier) {
```

---

## State Management
### Should this be remembered?
**Reviewer Comment:**
> Should this be remembered?

**Code Context:**
```diff
+import androidx.compose.ui.platform.LocalTextToolbar
+import androidx.compose.ui.platform.LocalView
+import androidx.compose.ui.platform.TextToolbar
+import androidx.compose.ui.platform.TextToolbarStatus
+import androidx.compose.ui.tooling.preview.Preview
+import androidx.compose.ui.unit.dp
+import com.example.compose.snippets.R
+import com.example.compose.snippets.touchinput.userinteractions.MyAppTheme
+
+@Preview
+@Composable
+private fun TextSelectionCustomActionPreview() {
+    MyAppTheme {
+        TextSelectionCustomAction()
+    }
+}
+
+@Composable
+fun TextSelectionCustomAction(modifier: Modifier = Modifier) {
+    val textToolbar = CustomTextToolbar(
```

### this should be remembered or extracted to global variable, so it doesn't need to recreate on every recomposition
**Reviewer Comment:**
> this should be remembered or extracted to global variable, so it doesn't need to recreate on every recomposition.

**Code Context:**
```diff
+    )
+}
+// [END android_compose_components_appbarselectionactions]
+
+@Preview
+@Composable
+private fun AppBarSelectionActionsPreview() {
+    val selectedItems = setOf(1, 2, 3)
+
+    AppBarSelectionActions(selectedItems)
+}
+
+@OptIn(ExperimentalFoundationApi::class)
+@Preview
+// [START android_compose_components_appbarmultiselectionexample]
+@Composable
+private fun AppBarMultiSelectionExample(
+    modifier: Modifier = Modifier,
+) {
+    val listItems = listOf(1, 2, 3, 4, 5, 6)
```

---

## Snippets and Documentation Structure
### You can move the @Preview to be before the [START] tags for all of these
**Reviewer Comment:**
> You can move the @Preview to be before the [START] tags for all of these :)

**Code Context:**
```diff
+                )
+                .align(Alignment.Center)
+                .background(
+                    color = Color.White,
+                    shape = RoundedCornerShape(20.dp)
+                )
+        ) {
+            Text(
+                "Drop Shadow",
+                modifier = Modifier.align(Alignment.Center),
+                fontSize = 32.sp
+            )
+        }
+    }
+}
+// [END android_compose_graphics_simple_drop_shadow]
+
+// [START android_compose_graphics_simple_inner_shadow]
+@Composable
+@Preview(
```

### and annotate with @Preview to be able to run individually, but place the preview tag above the Start tags so they arent included in the rendered composable.
**Reviewer Comment:**
> and annotate with @Preview to be able to run individually, but place the preview tag above the Start tags so they arent included in the rendered composable.

**Code Context:**
```diff
+import androidx.compose.ui.geometry.Offset
+import androidx.compose.ui.graphics.Brush
+import androidx.compose.ui.graphics.Color
+import androidx.compose.ui.graphics.graphicsLayer
+import androidx.compose.ui.layout.ContentScale
+import androidx.compose.ui.layout.onSizeChanged
+import androidx.compose.ui.res.painterResource
+import androidx.compose.ui.text.TextStyle
+import androidx.compose.ui.text.font.FontWeight
+import androidx.compose.ui.text.style.TextAlign
+import androidx.compose.ui.unit.IntOffset
+import androidx.compose.ui.unit.IntSize
+import androidx.compose.ui.unit.dp
+import androidx.compose.ui.unit.sp
+import com.example.compose.snippets.R
+import kotlin.math.roundToInt
+
+// [START android_compose_touchinput_scroll_scrollable2D_basic]
+@Composable
+fun Scrollable2DSample() {
```

### Just to note, we generally create functions that separate out each snippet logically, otherwise reading the file in its entirety is a bit difficult if they are all in the same function.
**Reviewer Comment:**
> Just to note, we generally create functions that separate out each snippet logically, otherwise reading the file in its entirety is a bit difficult if they are all in the same function.

**Code Context:**
```diff
@@ -336,6 +319,23 @@ protected void onCreate(@Nullable Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);
             setContentView(R.layout.activity_main);

+            // [START android_activity_embedding_splitAttributesBuilder_java]
+            SplitAttributes.Builder splitAttributesBuilder = new SplitAttributes.Builder()
```

---

## Accessibility
### Add content descriptions here
**Reviewer Comment:**
> Add content descriptions here

**Code Context:**
```diff
+
+    Box(
+        modifier
+            .fillMaxSize()
+            .semantics { isTraversalGroup = true }
+    ) {
+        SearchBar(
+            modifier = Modifier
+                .align(Alignment.TopCenter)
+                .semantics { traversalIndex = 0f },
+            inputField = {
+                SearchBarDefaults.InputField(
+                    query = text,
+                    onQueryChange = { text = it },
+                    onSearch = { expanded = false },
+                    expanded = expanded,
+                    onExpandedChange = { expanded = it },
+                    placeholder = { Text("Hinted search text") },
+                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
+                    trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
```

---

## Navigation
### What do you think about making something like a Destination enum that contains a list of destinations
**Reviewer Comment:**
> What do you think about making something like a Destination enum that contains a list of destinations, with their paths, then we can put them in a LazyColumn list of items.
>
> I did this in my playground of experiments and it works pretty well when adding more and more snippets. https://github.com/riggaroo/compose-playtime/blob/main/app/src/main/java/dev/riggaroo/composeplaytime/MainActivity.kt#L56

**Code Context:**
```diff
+
+import androidx.compose.foundation.layout.Arrangement
+import androidx.compose.foundation.layout.Column
+import androidx.compose.foundation.layout.fillMaxSize
+import androidx.compose.foundation.layout.fillMaxWidth
+import androidx.compose.foundation.layout.padding
+import androidx.compose.material3.Button
+import androidx.compose.material3.Text
+import androidx.compose.runtime.Composable
+import androidx.compose.ui.Alignment
+import androidx.compose.ui.Modifier
+import androidx.compose.ui.text.TextStyle
+import androidx.compose.ui.text.font.FontWeight
+import androidx.compose.ui.text.style.TextAlign
+import androidx.compose.ui.unit.dp
+import androidx.compose.ui.unit.sp
+
+@Composable
+fun LandingScreen(
+    toBrushExamples: () -> Unit,
```

### Nice, you can remove the specific callbacks here then, and just have a generic, `onNavigateTo: (Destination) -> Unit` parameter
**Reviewer Comment:**
> Nice, you can remove the specific callbacks here then, and just have a generic, `onNavigateTo: (Destination) -> Unit` parameter, then you only need one callback.

**Code Context:**
```diff
+
+import androidx.compose.foundation.layout.Arrangement
+import androidx.compose.foundation.layout.Column
+import androidx.compose.foundation.layout.fillMaxSize
+import androidx.compose.foundation.layout.fillMaxWidth
+import androidx.compose.foundation.layout.padding
+import androidx.compose.material3.Button
+import androidx.compose.material3.Text
+import androidx.compose.runtime.Composable
+import androidx.compose.ui.Alignment
+import androidx.compose.ui.Modifier
+import androidx.compose.ui.text.TextStyle
+import androidx.compose.ui.text.font.FontWeight
+import androidx.compose.ui.text.style.TextAlign
+import androidx.compose.ui.unit.dp
+import androidx.compose.ui.unit.sp
+
+@Composable
+fun LandingScreen(
+    toBrushExamples: () -> Unit,
```

---

## Visibility
### Make all composables in this file private
**Reviewer Comment:**
> Make all composables in this file private

**Code Context:**
```diff
+import androidx.compose.ui.geometry.Offset
+import androidx.compose.ui.graphics.Brush
+import androidx.compose.ui.graphics.Color
+import androidx.compose.ui.graphics.graphicsLayer
+import androidx.compose.ui.layout.ContentScale
+import androidx.compose.ui.layout.onSizeChanged
+import androidx.compose.ui.res.painterResource
+import androidx.compose.ui.text.TextStyle
+import androidx.compose.ui.text.font.FontWeight
+import androidx.compose.ui.text.style.TextAlign
+import androidx.compose.ui.unit.IntOffset
+import androidx.compose.ui.unit.IntSize
+import androidx.compose.ui.unit.dp
+import androidx.compose.ui.unit.sp
+import com.example.compose.snippets.R
+import kotlin.math.roundToInt
+
+// [START android_compose_touchinput_scroll_scrollable2D_basic]
+@Composable
+fun Scrollable2DSample() {
```

### same thing here, you can make it private and more descriptive about what the snippet shows.
**Reviewer Comment:**
> same thing here, you can make it private and more descriptive about what the snippet shows.

**Code Context:**
```diff
+        }
+    }
+}
+
+@Composable
+fun HomeScreen(
+    modifier: Modifier = Modifier, navController: NavHostController
+) {
+
+}
+
+@Composable
+fun SettingsScreen(
+    modifier: Modifier = Modifier, navController: NavHostController
+) {
+
+}
+
+@Composable
+fun HomeScreenDrawer() {
```

---

## Text and Links
### You dont need the `pop` operations here, just move the non-linked text out of the withLink() {}
**Reviewer Comment:**
> You dont need the `pop` operations here, just move the non-linked text out of the withLink() {} lambda.
>
> ```
> Text(
>         buildAnnotatedString {
>             append("Go to the ")
>             withLink(
>                 LinkAnnotation.Url(
>                     "https://developer.android.com/",
>                     TextLinkStyles(style = SpanStyle(color = Color.Blue))
>                 )
>             ) {
>                 append("Android Developers ")
>
>             }
>
>             append("website, and check out the")
>             withLink(
>                 LinkAnnotation.Url(
>                     "https://developer.android.com/jetpack/compose",
>                     TextLinkStyles(style = SpanStyle(color = Color.Green))
>                 )
>             ) {
>                 append("Compose guidance")
>             }
>             append(".")
>         }
>     )
> ```

**Code Context:**
```diff
-                    "https://developer.android.com/jetpack/compose",
+                    "https://developer.android.com/",
                     TextLinkStyles(style = SpanStyle(color = Color.Blue))
                 )
             ) {
-                append("Jetpack Compose")
+                append("Android Developers ")
+                pop()
+                append("website, and check out the")
+            }
+            withLink(
+                LinkAnnotation.Url(
+                    "https://developer.android.com/jetpack/compose",
+                    TextLinkStyles(style = SpanStyle(color = Color.Green))
+                )
+            ) {
+                append("Compose guidance")
+                pop()
+                append(".")
+                pop()
```

---
