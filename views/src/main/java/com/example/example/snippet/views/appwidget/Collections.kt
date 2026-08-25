/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.example.snippet.views.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.Toast
import com.example.example.snippet.views.R

private data class WidgetItem(val text: String)

private const val EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM"

private object CollectionsServiceBoilerplate {
    // [START android_views_appwidgets_collections_service_boilerplate]
    class StackWidgetService : RemoteViewsService() {
        override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
            StackRemoteViewsFactory(this.applicationContext, intent)
    }

    class StackRemoteViewsFactory(
        private val context: Context, intent: Intent
    ) : RemoteViewsService.RemoteViewsFactory {

        // See the RemoteViewsFactory API reference for the full list of methods to implement.
        // [START_EXCLUDE silent]
        override fun onCreate() {}
        override fun onDataSetChanged() {}
        override fun onDestroy() {}
        override fun getCount(): Int = 0
        override fun getViewAt(position: Int): RemoteViews? = null
        override fun getLoadingView(): RemoteViews? = null
        override fun getViewTypeCount(): Int = 1
        override fun getItemId(position: Int): Long = position.toLong()
        override fun hasStableIds(): Boolean = true
        // [END_EXCLUDE]

    }
    // [END android_views_appwidgets_collections_service_boilerplate]
}

private object CollectionsOnUpdate {
    class StackWidgetService : RemoteViewsService() {
        override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
            throw UnsupportedOperationException()
        }
    }

    class StackWidgetProvider : AppWidgetProvider() {
        // [START android_views_appwidgets_collections_onupdate]
        override fun onUpdate(
            context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
        ) {
            // Update each of the widgets with the remote adapter.
            appWidgetIds.forEach { appWidgetId ->

                // Set up the intent that starts the StackViewService, which
                // provides the views for this collection.
                val intent = Intent(context, StackWidgetService::class.java).apply {
                    // Add the widget ID to the intent extras.
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
                }
                // Instantiate the RemoteViews object for the widget layout.
                val views = RemoteViews(context.packageName, R.layout.widget_layout).apply {
                    // Set up the RemoteViews object to use a RemoteViews adapter.
                    // This adapter connects to a RemoteViewsService through the
                    // specified intent.
                    // This is how you populate the data.
                    setRemoteAdapter(R.id.stack_view, intent)

                    // The empty view is displayed when the collection has no items.
                    // It must be in the same layout used to instantiate the
                    // RemoteViews object.
                    setEmptyView(R.id.stack_view, R.id.empty_view)
                }

                // Do additional processing specific to this widget.

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
            super.onUpdate(context, appWidgetManager, appWidgetIds)
        }
        // [END android_views_appwidgets_collections_onupdate]
    }
}

private object CollectionsFactoryOnCreate {
    // [START android_views_appwidgets_collections_factory_oncreate]
    private const val REMOTE_VIEW_COUNT: Int = 10

    class StackRemoteViewsFactory(
        private val context: Context
    ) : RemoteViewsService.RemoteViewsFactory {

        private lateinit var widgetItems: List<WidgetItem>

        override fun onCreate() {
            // In onCreate(), set up any connections or cursors to your data
            // source. Heavy lifting, such as downloading or creating content,
            // must be deferred to onDataSetChanged() or getViewAt(). Taking
            // more than 20 seconds on this call results in an ANR.
            widgetItems = List(REMOTE_VIEW_COUNT) { index -> WidgetItem("$index!") }
        }

        // [START_EXCLUDE silent]
        override fun onDataSetChanged() {}
        override fun onDestroy() {}
        override fun getCount(): Int = widgetItems.size
        override fun getViewAt(position: Int): RemoteViews? = null
        override fun getLoadingView(): RemoteViews? = null
        override fun getViewTypeCount(): Int = 1
        override fun getItemId(position: Int): Long = position.toLong()
        override fun hasStableIds(): Boolean = true
        // [END_EXCLUDE]
    }
    // [END android_views_appwidgets_collections_factory_oncreate]
}

private object CollectionsFactoryGetViewAt {
    class StackRemoteViewsFactory(
        private val context: Context
    ) : RemoteViewsService.RemoteViewsFactory {
        private lateinit var widgetItems: List<WidgetItem>

        override fun onCreate() {}
        override fun onDataSetChanged() {}
        override fun onDestroy() {}
        override fun getCount(): Int = widgetItems.size
        override fun getLoadingView(): RemoteViews? = null
        override fun getViewTypeCount(): Int = 1
        override fun getItemId(position: Int): Long = position.toLong()
        override fun hasStableIds(): Boolean = true

        // [START android_views_appwidgets_collections_factory_getviewat]
        override fun getViewAt(position: Int): RemoteViews {
            // Construct a remote views item based on the widget item XML file
            // and set the text based on the position.
            return RemoteViews(context.packageName, R.layout.widget_item).apply {
                setTextViewText(R.id.widget_item, widgetItems[position].text)
            }
        }
        // [END android_views_appwidgets_collections_factory_getviewat]
    }
}

private object CollectionsProviderPendingIntent {
    class StackWidgetService : RemoteViewsService() {
        override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
            throw UnsupportedOperationException()
        }
    }

    // [START android_views_appwidgets_collections_provider_pending_intent]
    const val TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION"
    const val EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM"

    class StackWidgetProvider : AppWidgetProvider() {

        // ...

        // Called when the BroadcastReceiver receives an Intent broadcast.
        // Checks whether the intent's action is TOAST_ACTION. If it is, the
        // widget displays a Toast message for the current item.
        override fun onReceive(context: Context, intent: Intent) {
            val mgr: AppWidgetManager = AppWidgetManager.getInstance(context)
            if (intent.action == TOAST_ACTION) {
                val appWidgetId: Int = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                // EXTRA_ITEM represents a custom value provided by the Intent
                // passed to the setOnClickFillInIntent() method to indicate the
                // position of the clicked item. See StackRemoteViewsFactory in
                // Set the fill-in Intent for details.
                val viewIndex: Int = intent.getIntExtra(EXTRA_ITEM, 0)
                Toast.makeText(context, "Touched view $viewIndex", Toast.LENGTH_SHORT).show()
            }
            super.onReceive(context, intent)
        }

        override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            // Update each of the widgets with the remote adapter.
            appWidgetIds.forEach { appWidgetId ->

                // Sets up the intent that points to the StackViewService that
                // provides the views for this collection.
                val intent = Intent(context, StackWidgetService::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    // When intents are compared, the extras are ignored, so embed
                    // the extra sinto the data so that the extras are not ignored.
                    data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
                }
                val rv = RemoteViews(context.packageName, R.layout.widget_layout).apply {
                    setRemoteAdapter(R.id.stack_view, intent)

                    // The empty view is displayed when the collection has no items.
                    // It must be a sibling of the collection view.
                    setEmptyView(R.id.stack_view, R.id.empty_view)
                }

                // This section makes it possible for items to have individualized
                // behavior. It does this by setting up a pending intent template.
                // Individuals items of a collection can't set up their own pending
                // intents. Instead, the collection as a whole sets up a pending
                // intent template, and the individual items set a fillInIntent
                // to create unique behavior on an item-by-item basis.
                val toastPendingIntent: PendingIntent = Intent(
                    context,
                    StackWidgetProvider::class.java
                ).run {
                    // Set the action for the intent.
                    // When the user touches a particular view, it has the effect of
                    // broadcasting TOAST_ACTION.
                    action = TOAST_ACTION
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))

                    // The template must be mutable, because each item fills in its
                    // own extras through setOnClickFillInIntent().
                    PendingIntent.getBroadcast(
                        context, 0, this,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    )
                }
                rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, rv)
            }
            super.onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }
    // [END android_views_appwidgets_collections_provider_pending_intent]
}

private object CollectionsFactoryFillInIntent {
    // [START android_views_appwidgets_collections_factory_fill_in_intent]
    private const val REMOTE_VIEW_COUNT: Int = 10

    class StackRemoteViewsFactory(
        private val context: Context,
        intent: Intent
    ) : RemoteViewsService.RemoteViewsFactory {

        private lateinit var widgetItems: List<WidgetItem>
        private val appWidgetId: Int = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        override fun onCreate() {
            // In onCreate(), set up any connections or cursors to your data source.
            // Heavy lifting, such as downloading or creating content, must be
            // deferred to onDataSetChanged() or getViewAt(). Taking more than 20
            // seconds on this call results in an ANR.
            widgetItems = List(REMOTE_VIEW_COUNT) { index -> WidgetItem("$index!") }
            // ...
        }

        // [START_EXCLUDE]
        override fun onDataSetChanged() {}
        override fun onDestroy() {}
        override fun getCount(): Int = widgetItems.size
        override fun getLoadingView(): RemoteViews? = null
        override fun getViewTypeCount(): Int = 1
        override fun getItemId(position: Int): Long = position.toLong()
        override fun hasStableIds(): Boolean = true
        // [END_EXCLUDE]

        override fun getViewAt(position: Int): RemoteViews {
            // Construct a remote views item based on the widget item XML file
            // and set the text based on the position.
            return RemoteViews(context.packageName, R.layout.widget_item).apply {
                setTextViewText(R.id.widget_item, widgetItems[position].text)

                // Set a fill-intent to fill in the pending intent template.
                // that is set on the collection view in StackWidgetProvider.
                val fillInIntent = Intent().apply {
                    Bundle().also { extras ->
                        extras.putInt(EXTRA_ITEM, position)
                        putExtras(extras)
                    }
                }
                // Make it possible to distinguish the individual on-click
                // action of a given item.
                setOnClickFillInIntent(R.id.widget_item, fillInIntent)
                // ...
            }
        }
        // ...
    }
    // [END android_views_appwidgets_collections_factory_fill_in_intent]
}

private fun remoteCollectionItemsSnippet(
    context: Context,
    remoteView: RemoteViews
) {
    val ID_1 = 1L
    val ID_2 = 2L

    // [START android_views_appwidgets_collections_remote_collection_items]
    val itemLayouts = listOf(
        R.layout.item_type_1,
        R.layout.item_type_2,
        // ...
    )

    remoteView.setRemoteAdapter(
        R.id.list_view,
        RemoteViews.RemoteCollectionItems.Builder()
            .addItem(/* id= */ ID_1, RemoteViews(context.packageName, R.layout.item_type_1))
            .addItem(/* id= */ ID_2, RemoteViews(context.packageName, R.layout.item_type_2))
            // ...
            .setViewTypeCount(itemLayouts.count())
            .build()
    )
    // [END android_views_appwidgets_collections_remote_collection_items]
}
