/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.compose.snippets.glance

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.CheckboxDefaults
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.RadioButton
import androidx.glance.appwidget.RadioButtonDefaults
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.Switch
import androidx.glance.appwidget.SwitchDefaults
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.action.actionStartService
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.appwidget.updateIf
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.compose.snippets.MyActivity
import com.example.compose.snippets.R
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

lateinit var LightColors: ColorScheme
lateinit var DarkColors: ColorScheme

private object GlanceCreateAppWidgetSnippet01 {
    // [START android_compose_glance_receiver01]
    class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget: GlanceAppWidget = TODO("Create GlanceAppWidget")
    }
    // [END android_compose_glance_receiver01]
}

private object GlanceCreateAppWidgetSnippet02 {
    // [START android_compose_glance_receiver02]
    class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
        
        // Let MyAppWidgetReceiver know which GlanceAppWidget to use
        override val glanceAppWidget: GlanceAppWidget = MyAppWidget()
    }
    // [END android_compose_glance_receiver02]

    // [START android_compose_glance_widget]
    class MyAppWidget : GlanceAppWidget() {

        override suspend fun provideGlance(context: Context, id: GlanceId) {

            // In this method, load data needed to render the AppWidget.
            // Use `withContext` to switch to another thread for long running
            // operations.

            provideContent {
                // create your AppWidget here
                Text("Hello World")
            }
        }
    }
    // [END android_compose_glance_widget]
}

private object CreateUI {
    // [START android_compose_glance_createui]
    /* Import Glance Composables
     In the event there is a name clash with the Compose classes of the same name,
     you may rename the imports per https://kotlinlang.org/docs/packages.html#imports
     using the `as` keyword.

    import androidx.glance.Button
    import androidx.glance.layout.Column
    import androidx.glance.layout.Row
    import androidx.glance.text.Text
    */
    class MyAppWidget : GlanceAppWidget() {

        override suspend fun provideGlance(context: Context, id: GlanceId) {
            // Load data needed to render the AppWidget.
            // Use `withContext` to switch to another thread for long running
            // operations.

            provideContent {
                // create your AppWidget here
                MyContent()
            }
        }

        @Composable
        private fun MyContent() {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Where to?", modifier = GlanceModifier.padding(12.dp))
                Row(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        text = "Home",
                        onClick = actionStartActivity<MyActivity>()
                    )
                    Button(
                        text = "Work",
                        onClick = actionStartActivity<MyActivity>()
                    )
                }
            }
        }
    }
    // [END android_compose_glance_createui]
}

private object ActionLaunchActivity {

    // [START android_compose_glance_launchactivity]
    @Composable
    fun MyContent() {
        // ..
        Button(
            text = "Go Home",
            onClick = actionStartActivity<MyActivity>()
        )
    }
    // [END android_compose_glance_launchactivity]
}

private object ActionLaunchService {

    // [START android_compose_glance_launchservice]
    @Composable
    fun MyButton() {
        // ..
        Button(
            text = "Sync",
            onClick = actionStartService<SyncService>(
                isForegroundService = true // define how the service is launched
            )
        )
    }
    // [END android_compose_glance_launchservice]
}

private object ActionLaunchSendBroadcastEvent {

    // [START android_compose_glance_sendbroadcastevent]
    @Composable
    fun MyButton() {
        // ..
        Button(
            text = "Send",
            onClick = actionSendBroadcast<MyReceiver>()
        )
    }
    // [END android_compose_glance_sendbroadcastevent]
}

private object ActionLambda {
    @Composable
    fun actionLambda() {
        // [START android_compose_glance_lambda01]
        Text(
            text = "Submit",
            modifier = GlanceModifier.clickable {
                submitData()
            }
        )
        // [END android_compose_glance_lambda01]
    }

    @Composable
    fun actionLambda2() {
        // [START android_compose_glance_lambda02]
        Button(
            text = "Submit",
            onClick = {
                submitData()
            }
        )
        // [END android_compose_glance_lambda02]
    }
}

private object ActionCallbackSnippet02 {
    // [START android_compose_glance_actioncallback02]
    class RefreshAction : ActionCallback {
        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            // do some work but offset long-term tasks (e.g a Worker)
            MyAppWidget().update(context, glanceId)
        }
    }

    // [END android_compose_glance_actioncallback02]
    /*dummy class*/
    class MyAppWidget : GlanceAppWidget() {
        override suspend fun provideGlance(context: Context, id: GlanceId) {
            TODO("Not yet implemented")
        }
    }
}

private object ActionCallbackSnippet01 {
    // [START android_compose_glance_actioncallback01]
    @Composable
    private fun MyContent() {
        // ..
        Image(
            provider = ImageProvider(R.drawable.ic_hourglass_animated),
            modifier = GlanceModifier.clickable(
                onClick = actionRunCallback<RefreshAction>()
            ),
            contentDescription = "Refresh"
        )
    }

    class RefreshAction : ActionCallback {
        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            // TODO implement
        }
    }
    // [END android_compose_glance_actioncallback01]
}

private object ActionParameters {
    // [START android_compose_glance_actionparameters01]
    private val destinationKey = ActionParameters.Key<String>(
        NavigationActivity.KEY_DESTINATION
    )

    class MyAppWidget : GlanceAppWidget() {

        // ..

        @Composable
        private fun MyContent() {
            // ..
            Button(
                text = "Home",
                onClick = actionStartActivity<NavigationActivity>(
                    actionParametersOf(destinationKey to "home")
                )
            )
            Button(
                text = "Work",
                onClick = actionStartActivity<NavigationActivity>(
                    actionParametersOf(destinationKey to "work")
                )
            )
        }

        override suspend fun provideGlance(context: Context, id: GlanceId) {
            provideContent { MyContent() }
        }
    }
    // [END android_compose_glance_actionparameters01]

    abstract class ActionParametersActivity : Activity() {
        val KEY_DESTINATION = "destination"

        // [START android_compose_glance_actionparameters02]
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val destination = intent.extras?.getString(KEY_DESTINATION) ?: return
            // ...
        }
    }
    // [END android_compose_glance_actionparameters02]

    // [START android_compose_glance_actionparameters03]
    class RefreshAction : ActionCallback {

        private val destinationKey = ActionParameters.Key<String>(
            NavigationActivity.KEY_DESTINATION
        )

        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            val destination: String = parameters[destinationKey] ?: return
            // ...
        }
    }
    // [END android_compose_glance_actionparameters03]
}

@SuppressLint("RememberReturnType")
object ManageAndUpdate {
    abstract
    // [START android_compose_glance_manageupdate01]
    class DestinationAppWidget : GlanceAppWidget() {

        // ...

        @Composable
        fun MyContent() {
            val repository = remember { DestinationsRepository.getInstance() }
            // Retrieve the cache data everytime the content is refreshed
            val destinations by repository.destinations.collectAsState(State.Loading)

            when (destinations) {
                is State.Loading -> {
                    // show loading content
                }

                is State.Error -> {
                    // show widget error content
                }

                is State.Completed -> {
                    // show the list of destinations
                }
            }
        }
    }
    // [END android_compose_glance_manageupdate01]

    suspend fun update02(context: Context, glanceId: GlanceId) {
        // [START android_compose_glance_manageupdate02]
        MyAppWidget().update(context, glanceId)
        // [END android_compose_glance_manageupdate02]

        // [START android_compose_glance_manageupdate03]
        val manager = GlanceAppWidgetManager(context)
        val widget = GlanceSizeModeWidget()
        val glanceIds = manager.getGlanceIds(widget.javaClass)
        glanceIds.forEach { glanceId ->
            widget.update(context, glanceId)
        }
        // [END android_compose_glance_manageupdate03]

        // [START android_compose_glance_manageupdate04]
        // Updates all placed instances of MyAppWidget
        MyAppWidget().updateAll(context)

        // Iterate over all placed instances of MyAppWidget and update if the state of
        // the instance matches the given predicate
        MyAppWidget().updateIf<State>(context) { state ->
            state == State.Completed
        }
        // [END android_compose_glance_manageupdate04]
    }

    // [START android_compose_glance_manageupdate05]
    class DataSyncWorker(
        val context: Context,
        val params: WorkerParameters,
    ) : CoroutineWorker(context, params) {

        override suspend fun doWork(): Result {
            // Fetch data or do some work and then update all instance of your widget
            MyAppWidget().updateAll(context)
            return Result.success()
        }
    }
// [END android_compose_glance_manageupdate05]
}

object BuildUIWithGlance {

    @Composable
    fun example1() {
        // [START android_compose_glance_buildUI01]
        Row(modifier = GlanceModifier.fillMaxWidth().padding(16.dp)) {
            val modifier = GlanceModifier.defaultWeight()
            Text("first", modifier)
            Text("second", modifier)
            Text("third", modifier)
        }
        // [END android_compose_glance_buildUI01]
    }

    @Composable
    fun example2() {

        // [START android_compose_glance_buildUI02]
        // Remember to import Glance Composables
        // import androidx.glance.appwidget.layout.LazyColumn

        LazyColumn {
            items(10) { index: Int ->
                Text(
                    text = "Item $index",
                    modifier = GlanceModifier.fillMaxWidth()
                )
            }
        }
        // [END android_compose_glance_buildUI02]
    }

    @Composable
    fun example3() {
        // [START android_compose_glance_buildUI03]
        LazyColumn {
            item {
                Text("First Item")
            }
            item {
                Text("Second Item")
            }
        }
        // [END android_compose_glance_buildUI03]
    }

    @Composable
    fun example4() {
        val peopleNameList = arrayListOf<String>()
        val peopleList = arrayListOf<Person>()

        // [START android_compose_glance_buildUI04]
        LazyColumn {
            items(peopleNameList) { name ->
                Text(name)
            }
        }
        // [END android_compose_glance_buildUI04]

        // [START android_compose_glance_buildUI05]
        LazyColumn {
            item {
                Text("Names:")
            }
            items(peopleNameList) { name ->
                Text(name)
            }

            // or in case you need the index:
            itemsIndexed(peopleNameList) { index, person ->
                Text("$person at index $index")
            }
        }
        // [END android_compose_glance_buildUI05]

        LazyColumn {
            // [START android_compose_glance_buildUI06]
            items(items = peopleList, key = { person -> person.id }) { person ->
                Text(person.name)
            }
            // [END android_compose_glance_buildUI06]
        }
    }
}

object SizeModeSnippets {

    // [START android_compose_glance_buildUI07]
    class MyAppWidget : GlanceAppWidget() {

        override val sizeMode = SizeMode.Single

        override suspend fun provideGlance(context: Context, id: GlanceId) {
            // ...

            provideContent {
                MyContent()
            }
        }

        @Composable
        private fun MyContent() {
            // Size will be the minimum size or resizable
            // size defined in the App Widget metadata
            val size = LocalSize.current
            // ...
        }
    }
    // [END android_compose_glance_buildUI07]
}

object SizeModeSnippets2 {
    // [START android_compose_glance_buildUI08]
    class MyAppWidget : GlanceAppWidget() {

        companion object {
            private val SMALL_SQUARE = DpSize(100.dp, 100.dp)
            private val HORIZONTAL_RECTANGLE = DpSize(250.dp, 100.dp)
            private val BIG_SQUARE = DpSize(250.dp, 250.dp)
        }

        override val sizeMode = SizeMode.Responsive(
            setOf(
                SMALL_SQUARE,
                HORIZONTAL_RECTANGLE,
                BIG_SQUARE
            )
        )

        override suspend fun provideGlance(context: Context, id: GlanceId) {
            // ...

            provideContent {
                MyContent()
            }
        }

        @Composable
        private fun MyContent() {
            // Size will be one of the sizes defined above.
            val size = LocalSize.current
            Column {
                if (size.height >= BIG_SQUARE.height) {
                    Text(text = "Where to?", modifier = GlanceModifier.padding(12.dp))
                }
                Row(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button()
                    Button()
                    if (size.width >= HORIZONTAL_RECTANGLE.width) {
                        Button("School")
                    }
                }
                if (size.height >= BIG_SQUARE.height) {
                    Text(text = "provided by X")
                }
            }
        }
    }

    // [END android_compose_glance_buildUI08]
}

object SizeModeSnippets3 {
    // [START android_compose_glance_buildUI09]

    class MyAppWidget : GlanceAppWidget() {

        override val sizeMode = SizeMode.Exact

        override suspend fun provideGlance(context: Context, id: GlanceId) {
            // ...

            provideContent {
                MyContent()
            }
        }

        @Composable
        private fun MyContent() {
            // Size will be the size of the AppWidget
            val size = LocalSize.current
            Column {
                Text(text = "Where to?", modifier = GlanceModifier.padding(12.dp))
                Row(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button()
                    Button()
                    if (size.width > 250.dp) {
                        Button("School")
                    }
                }
            }
        }
    }
    // [END android_compose_glance_buildUI09]
}

object AccessResources {
    @Composable
    fun example1() {
        // [START android_compose_glance_buildUI10]
        LocalContext.current.getString(R.string.glance_title)
        // [END android_compose_glance_buildUI10]

        // [START android_compose_glance_buildUI11]
        Column(
            modifier = GlanceModifier.background(R.color.default_widget_background)
        ) { /**...*/ }

        Image(
            provider = ImageProvider(R.drawable.ic_logo),
            contentDescription = "My image",
        )
        // [END android_compose_glance_buildUI11]
    }
}

object CompoundButton {
    @Composable
    fun example1() {
        // [START android_compose_glance_buildUI12]
        var isApplesChecked by remember { mutableStateOf(false) }
        var isEnabledSwitched by remember { mutableStateOf(false) }
        var isRadioChecked by remember { mutableStateOf(0) }

        CheckBox(
            checked = isApplesChecked,
            onCheckedChange = { isApplesChecked = !isApplesChecked },
            text = "Apples"
        )

        Switch(
            checked = isEnabledSwitched,
            onCheckedChange = { isEnabledSwitched = !isEnabledSwitched },
            text = "Enabled"
        )

        RadioButton(
            checked = isRadioChecked == 1,
            onClick = { isRadioChecked = 1 },
            text = "Checked"
        )
        // [END android_compose_glance_buildUI12]
    }
    // [START android_compose_glance_buildUI13]
    class MyAppWidget : GlanceAppWidget() {

        override suspend fun provideGlance(context: Context, id: GlanceId) {
            val myRepository = MyRepository.getInstance()

            provideContent {
                val scope = rememberCoroutineScope()

                val saveApple: (Boolean) -> Unit =
                    { scope.launch { myRepository.saveApple(it) } }
                MyContent(saveApple)
            }
        }

        @Composable
        private fun MyContent(saveApple: (Boolean) -> Unit) {

            var isAppleChecked by remember { mutableStateOf(false) }

            Button(
                text = "Save",
                onClick = { saveApple(isAppleChecked) }
            )
        }
    }
    // [END android_compose_glance_buildUI13]

    @Composable
    fun example3() {
        val colorAccentDay = Color.Blue
        val colorAccentNight = Color.Blue
        var isChecked by remember { mutableStateOf(false) }

        // [START android_compose_glance_buildUI14]
        CheckBox(
            // ...
            colors = CheckboxDefaults.colors(
                checkedColor = ColorProvider(day = colorAccentDay, night = colorAccentNight),
                uncheckedColor = ColorProvider(day = Color.DarkGray, night = Color.LightGray)
            ),
            checked = isChecked,
            onCheckedChange = { isChecked = !isChecked }
        )

        Switch(
            // ...
            colors = SwitchDefaults.colors(
                checkedThumbColor = ColorProvider(day = Color.Red, night = Color.Cyan),
                uncheckedThumbColor = ColorProvider(day = Color.Green, night = Color.Magenta),
                checkedTrackColor = ColorProvider(day = Color.Blue, night = Color.Yellow),
                uncheckedTrackColor = ColorProvider(day = Color.Magenta, night = Color.Green)
            ),
            checked = isChecked,
            onCheckedChange = { isChecked = !isChecked },
            text = "Enabled"
        )

        RadioButton(
            // ...
            colors = RadioButtonDefaults.colors(
                checkedColor = ColorProvider(day = Color.Cyan, night = Color.Yellow),
                uncheckedColor = ColorProvider(day = Color.Red, night = Color.Blue)
            ),

        )
        // [END android_compose_glance_buildUI14]
    }

    private fun RadioButton(colors: Any) {
    }
}

object GlanceTheming {

    class ExampleAppWidget : GlanceAppWidget() {
        // [START android_compose_glance_glancetheming01]

        override suspend fun provideGlance(context: Context, id: GlanceId) {

            provideContent {
                GlanceTheme {
                    MyContent()
                }
            }
        }

        @Composable
        private fun MyContent() {

            Image(
                colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
                // [START_EXCLUDE]
                contentDescription = "Example Image",
                provider = ImageProvider(R.drawable.ic_logo)
                // [END_EXCLUDE]

            )
        }
        // [END android_compose_glance_glancetheming01]
    }

    class ExampleAppWidget2 : GlanceAppWidget() {

        // [START android_compose_glance_glancetheming06]
        // Remember, use the Glance imports
        // import androidx.glance.material3.ColorProviders
        
        // Example Imports from your own app
        // import com.example.myapp.ui.theme.DarkColors
        // import com.example.myapp.ui.theme.LightColors

        object MyAppWidgetGlanceColorScheme {

            val colors = ColorProviders(
                light = LightColors,
                dark = DarkColors
            )
        }
        // [END android_compose_glance_glancetheming06]
        // [START android_compose_glance_glancetheming02]
        override suspend fun provideGlance(context: Context, id: GlanceId) {
            // ...

            provideContent {
                GlanceTheme(colors = MyAppWidgetGlanceColorScheme.colors) {
                    MyContent()
                }
            }
        }

        @Composable
        private fun MyContent() {

            Image(
                colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
                // [START_EXCLUDE]
                provider = ImageProvider(R.drawable.ic_logo),
                contentDescription = "Example"
                // [END_EXCLUDE]
            )
        }
        // [END android_compose_glance_glancetheming02]
    }

    class ExampleAppWidget3 : GlanceAppWidget() {

        object MyAppWidgetGlanceColorScheme {
            val colors = ColorProviders(
                light = LightColors,
                dark = DarkColors
            )
        }

        // [START android_compose_glance_glancetheming03]
        override suspend fun provideGlance(context: Context, id: GlanceId) {

            provideContent {
                GlanceTheme(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        GlanceTheme.colors
                    else
                        MyAppWidgetGlanceColorScheme.colors
                ) {
                    MyContent()
                }
            }
        }

        @Composable
        private fun MyContent() {
            // ...
            Image(
                colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
                // [START_EXCLUDE]
                provider = ImageProvider(R.drawable.ic_logo),
                contentDescription = "Example"
                // [END_EXCLUDE]
            )
        }
        // [END android_compose_glance_glancetheming03]
    }

    @Composable
    fun shapeExample() {
        // Note : android_compose_glance_glancetheming04 is found in button_outline.xml
        // [START android_compose_glance_glancetheming05]
        GlanceModifier.background(
            imageProvider = ImageProvider(R.drawable.button_outline)
        )
        // [END android_compose_glance_glancetheming05]
    }
}

object GlanceInteroperability {
    @Composable
    fun example01() {
        // [START android_compose_glance_glanceinteroperability01]
        val packageName = LocalContext.current.packageName
        Column(modifier = GlanceModifier.fillMaxSize()) {
            Text("Isn't that cool?")
            AndroidRemoteViews(RemoteViews(packageName, R.layout.example_layout))
        }
        // [END android_compose_glance_glanceinteroperability01]
    }

    @Composable
    fun example02() {
        val packageName = null

        // [START android_compose_glance_glanceinteroperability02]

        AndroidRemoteViews(
            remoteViews = RemoteViews(packageName, R.layout.my_container_view),
            containerViewId = R.id.example_view
        ) {
            Column(modifier = GlanceModifier.fillMaxSize()) {
                Text("My title")
                Text("Maybe a long content...")
            }
        }
        // [END android_compose_glance_glanceinteroperability02]
    }
}

/**
 * Dummy interface
 */
interface MyRepository {

    suspend fun saveApple(a: Any)

    companion object {
        fun getInstance(): MyRepository {
            TODO("Example")
        }
    }
}

/**dummy function*/
private fun RowScope.Button(s: String = "") {
    TODO("Not yet implemented")
}

/** dummy class */
data class Person(val id: String, val name: String)

/** dummy class */
class GlanceSizeModeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        TODO("Not yet implemented")
    }
}

/** dummy class */
class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        TODO("Not yet implemented")
    }
}

/**
 * Dummy Interface
 */
sealed interface State {

    object Loading : State
    object Error : State
    object Completed : State
}

/**
 * Dummy class
 */
class DestinationsRepository {

    lateinit var destinations: StateFlow<State>

    companion object {
        fun getInstance(): DestinationsRepository {
            TODO("Not yet implemented")
        }
    }
}

/**
 * Dummy activity for snippet
 */
class NavigationActivity : AppCompatActivity() {
    companion object {
        val KEY_DESTINATION: String = "destination"
    }
}

/**
 * Dummy lambda
 */
private fun submitData() {
    TODO("Not yet implemented")
}

/**
 * Dummy broadcast receiver for snippets
 */
class MyReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        TODO("Not yet implemented")
    }
}

/**
 * Dummy service for snippets
 */
class SyncService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}
