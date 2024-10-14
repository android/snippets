package com.example.compose.snippets.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class ExampleStrings(val description: String){
    SIMPLE("simple navigation drawer example"),
    NESTED("navigation drawer with nested items")
}

@Composable
fun NavigationDrawerExamples(){
    var currentExample by remember { mutableStateOf<ExampleStrings?>(null) }

    if (currentExample == null){
        Column(){
            Text(
                text = "Select which example you'd like to see.",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Button(onClick = {currentExample = ExampleStrings.SIMPLE}){
                Text("Simple navigation drawer.")
            }
            Button(onClick = {currentExample = ExampleStrings.NESTED}){
                Text("Navigation drawer with nested items.")
            }
        }

        return
    }

    Column(modifier = Modifier.fillMaxSize()){
        Text(
            text = "Swipe from left to open the ${currentExample!!.description}.",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }

    when (currentExample){
        null,
        ExampleStrings.SIMPLE -> SimpleNavigationDrawerExample()
        ExampleStrings.NESTED -> NestedNavigationDrawerExample()
    }
}

@Preview
@Composable
private fun NavigationDrawerExamplesPreview(){
    NavigationDrawerExamples()
}

// [START android_compose_components_navigationdrawergroupitem]
@Composable
fun NavigationDrawerGroupItem(
    label: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    NavigationDrawerItem(
        label = label,
        selected = isExpanded,
        onClick = { isExpanded = !isExpanded },
        icon = {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
            )
        }
    )
    AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(start = 16.dp)) {
            content()
        }
    }
}
// [END android_compose_components_navigationdrawergroupitem]

// [START android_compose_components_simplenavigationdrawerexample]
@Composable
fun SimpleNavigationDrawerExample() {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(text = "Item 1") },
                    selected = false,
                    onClick = { /* Do something... */ }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Item 2") },
                    selected = false,
                    onClick = { /* Do something... */ }
                )
            }
        }
    ) {
        // Screen content
    }
}
// [END android_compose_components_simplenavigationdrawerexample]

// [START android_compose_components_nestednavigationdrawerexample]
@Composable
fun NestedNavigationDrawerExample() {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer Title", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /* Do something... */ }
                )
                NavigationDrawerGroupItem(
                    label = { Text("Drawer Group Item") },
                    content = {
                        NavigationDrawerItem(
                            label = { Text(text = "Drawer Item") },
                            selected = false,
                            onClick = { /* Do something... */ }
                        )
                        NavigationDrawerGroupItem(
                            label = { Text("Nested Group Item") },
                            content = {
                                NavigationDrawerItem(
                                    label = { Text(text = "Inner Drawer Item") },
                                    selected = false,
                                    onClick = { /* Do something... */ }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Inner Drawer Item") },
                                    selected = false,
                                    onClick = { /* Do something... */ }
                                )
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text(text = "Drawer Item") },
                            selected = false,
                            onClick = { /* Do something... */ }
                        )
                    }
                )
            }
        }
    ) {
        // Screen content
    }
}
// [END android_compose_components_nestednavigationdrawerexample]
