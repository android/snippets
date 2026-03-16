package com.example.compose.snippets.navigation3.scenedecorators

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import androidx.navigation3.ui.NavDisplay

// [START android_compose_navigation3_scenedecorators_1]
class MySceneDecoratorStrategy<T : Any> : SceneDecoratorStrategy<T> {

    // [START_EXCLUDE silent]
    private fun shouldDecorate(scene: Scene<T>): Boolean {
        return true
    }
    // [END_EXCLUDE]

    override fun SceneDecoratorStrategyScope<T>.decorateScene(scene: Scene<T>): Scene<T> {
        // `shouldDecorate` determines if the scene should be decorated based on scene.metadata,
        // scene.entries.metadata, or any other relevant state.
        return if (shouldDecorate(scene)) {
            MyDecoratingScene(scene)
        } else {
            scene
        }
    }

}

class MyDecoratingScene<T : Any>(scene: Scene<T>) : Scene<T> {

    // [START_EXCLUDE]
    override val key = scene.key
    override val entries = scene.entries
    override val previousEntries = scene.previousEntries
    // [END_EXCLUDE]

    override val content = @Composable {
        scene.content()
    }
}
// [END android_compose_navigation3_scenedecorators_1]

@Composable
fun SceneDecoratorNavDisplay() {
    val firstSceneDecoratorStrategy = remember { MySceneDecoratorStrategy<NavKey>() }
    val secondSceneDecoratorStrategy = remember { MySceneDecoratorStrategy<NavKey>() }

    // [START android_compose_navigation3_scenedecorators_2]
    NavDisplay(
        // [START_EXCLUDE]
        backStack = emptyList(),
        entryProvider = entryProvider { },
        // [END_EXCLUDE]
        sceneDecoratorStrategies = listOf(firstSceneDecoratorStrategy, secondSceneDecoratorStrategy)
    )
    // [END android_compose_navigation3_scenedecorators_2]
}

// [START android_compose_navigation3_scenedecorators_3]
class CopyingScene<T : Any>(scene: Scene<T>) : Scene<T> {
    override val entries = scene.entries
    override val previousEntries = scene.previousEntries
    override val metadata = scene.metadata

    // [START_EXCLUDE]
    override val key = scene.key
    override val content = @Composable {
        scene.content()
    }
    // [END_EXCLUDE]
}
// [END android_compose_navigation3_scenedecorators_3]

// [START android_compose_navigation3_scenedecorators_4]
class DerivedKeyScene<T : Any>(scene: Scene<T>) : Scene<T> {
    override val key = scene::class to scene.key

    // [START_EXCLUDE]
    override val entries = scene.entries
    override val previousEntries = scene.previousEntries
    override val content = @Composable {
        scene.content()
    }
    // [END_EXCLUDE]
}
// [END android_compose_navigation3_scenedecorators_4]