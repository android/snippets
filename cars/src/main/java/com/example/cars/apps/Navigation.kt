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

package com.example.cars.apps

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.Alert
import androidx.car.app.model.AlertCallback
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.car.app.model.DateTimeWithZone
import androidx.car.app.model.Distance
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.navigation.NavigationManager
import androidx.car.app.navigation.model.MapController
import androidx.car.app.navigation.model.MapTemplate
import androidx.car.app.navigation.model.MapWithContentTemplate
import androidx.car.app.navigation.model.PlaceListNavigationTemplate
import androidx.car.app.navigation.model.RoutePreviewNavigationTemplate
import androidx.car.app.navigation.model.TravelEstimate
import androidx.car.app.notification.CarAppExtender
import androidx.car.app.validation.HostValidator
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.cars.R
import java.util.Calendar

private const val NOTIFICATION_CHANNEL_ID = "channel_id"
private const val ACTION_OPEN_APP = "action_open_app"

class MyNavigationCarAppService : CarAppService() {
    // [START android_cars_apps_navigation_onCreateSession]
    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        return if (sessionInfo.displayType == SessionInfo.DISPLAY_TYPE_CLUSTER) {
            ClusterSession()
        } else {
            MainDisplaySession()
        }
    }
    // [END android_cars_apps_navigation_onCreateSession]
    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }
}

class ClusterSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        return ClusterScreen(carContext)
    }
}

class ClusterScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        TODO("Not yet implemented")
    }
}

class MainDisplaySession : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        return NavigationMainScreen(carContext)
    }
}

class NavigationMainScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        TODO("Not yet implemented")
    }
}

class NavigationDemo(
    private val carContext: CarContext,
    private val paneBuilder: Pane.Builder,
    private val actionStrip: ActionStrip,
    private val header: Header,
    private val mapController: MapController,
    private val itemListBuilder: ItemList.Builder,
    private val mapActionStrip: ActionStrip,
    private val title: CarText,
    private val actionTitle: CarText,
    private val screen: Screen
) {

    fun mapTemplateMigration() {
        // [START android_cars_apps_navigation_map_template_migration]
        // MapTemplate (deprecated)
        val templateDeprecated = MapTemplate.Builder()
            .setPane(paneBuilder.build())
            .setActionStrip(actionStrip)
            .setHeader(header)
            .setMapController(mapController)
            .build()

        // MapWithContentTemplate
        val template = MapWithContentTemplate.Builder()
            .setContentTemplate(
                PaneTemplate.Builder(paneBuilder.build())
                    .setHeader(header)
                    .build()
            )
            .setActionStrip(actionStrip)
            .setMapController(mapController)
            .build()
        // [END android_cars_apps_navigation_map_template_migration]
    }

    fun placeListNavigationTemplateMigration() {
        // [START android_cars_apps_navigation_place_list_template_migration]
        // PlaceListNavigationTemplate (deprecated)
        val templateDeprecated = PlaceListNavigationTemplate.Builder()
            .setItemList(itemListBuilder.build())
            .setHeader(header)
            .setActionStrip(actionStrip)
            .setMapActionStrip(mapActionStrip)
            .build()

        // MapWithContentTemplate
        val template = MapWithContentTemplate.Builder()
            .setContentTemplate(
                ListTemplate.Builder()
                    .setSingleList(itemListBuilder.build())
                    .setHeader(header)
                    .build()
            )
            .setActionStrip(actionStrip)
            .setMapController(
                MapController.Builder()
                    .setMapActionStrip(mapActionStrip)
                    .build()
            )
            .build()
        // [END android_cars_apps_navigation_place_list_template_migration]
    }

    fun routePreviewNavigationTemplateMigration() {
        // [START android_cars_apps_navigation_route_preview_template_migration]
        // RoutePreviewNavigationTemplate (deprecated)
        val templateDeprecated = RoutePreviewNavigationTemplate.Builder()
            .setItemList(
                ItemList.Builder()
                    .addItem(
                        Row.Builder()
                            .setTitle(title)
                            .build()
                    )
                    .build()
            )
            .setHeader(header)
            .setNavigateAction(
                Action.Builder()
                    .setTitle(actionTitle)
                    .setOnClickListener { /* onClick */ }
                    .build()
            )
            .setActionStrip(actionStrip)
            .setMapActionStrip(mapActionStrip)
            .build()

        // MapWithContentTemplate
        val template = MapWithContentTemplate.Builder()
            .setContentTemplate(
                ListTemplate.Builder()
                    .setSingleList(
                        ItemList.Builder()
                            .addItem(
                                Row.Builder()
                                    .setTitle(title)
                                    .addAction(
                                        Action.Builder()
                                            .setTitle(actionTitle)
                                            .setOnClickListener { /* onClick */ }
                                            .build()
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .setHeader(header)
                    .build()
            )
            .setActionStrip(actionStrip)
            .setMapController(
                MapController.Builder()
                    .setMapActionStrip(mapActionStrip)
                    .build()
            )
            .build()
        // [END android_cars_apps_navigation_route_preview_template_migration]
    }

    fun retrieveNavigationManager() {
        // [START android_cars_apps_navigation_manager_retrieval]
        val navigationManager = carContext.getCarService(NavigationManager::class.java)
        // [END android_cars_apps_navigation_manager_retrieval]
    }

    fun getDateTimeZone(): DateTimeWithZone {
        val calendar = Calendar.getInstance()
        val timeInMillis = calendar.timeInMillis
        val timeZone = calendar.timeZone
        return DateTimeWithZone.create(timeInMillis, timeZone)
    }

    fun customizeTravelEstimate() {
        val arrivalTimeAtDestination = getDateTimeZone()
        // [START android_cars_apps_navigation_travel_estimate_customization]
        TravelEstimate.Builder(
            Distance.create(350.0, Distance.UNIT_METERS),
            arrivalTimeAtDestination
        )
            .setTripIcon(
                CarIcon.Builder(
                    IconCompat.createWithResource(carContext, R.drawable.ic_garage)
                ).build()
            )
            .setTripText(CarText.create("Custom Text"))
            .build()
        // [END android_cars_apps_navigation_travel_estimate_customization]
    }

    fun buildNotification(context: Context, carScreenTitle: String) {
        // [START android_cars_apps_navigation_notification_building]
        NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_NAVIGATION)
            .extend(
                CarAppExtender.Builder()
                    .setContentTitle(carScreenTitle)
                    .setContentIntent(
                        PendingIntent.getBroadcast(
                            context,
                            ACTION_OPEN_APP.hashCode(),
                            Intent(ACTION_OPEN_APP).setComponent(
                                ComponentName(context, MyNotificationReceiver::class.java)
                            ),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    .setImportance(NotificationManagerCompat.IMPORTANCE_HIGH)
                    .build()
            )
            .build()
        // [END android_cars_apps_navigation_notification_building]
    }

    fun refreshListener() {
        // [START android_cars_apps_navigation_refresh_listener]
        PlaceListNavigationTemplate.Builder()
            .setOnContentRefreshListener {
                // Execute any desired logic
                // Then call invalidate() so onGetTemplate() is called again
                screen.invalidate()
            }
            .build()
        // [END android_cars_apps_navigation_refresh_listener]
    }

    fun createAlert(firstAction: Action, secondAction: Action, alertCallback: AlertCallback) {
        // [START android_cars_apps_navigation_alert_creation]
        Alert.Builder(
            1, // alertId
            CarText.create("Hello"), // title
            5000 // durationMillis
        )
            // The fields below are optional
            .addAction(firstAction)
            .addAction(secondAction)
            .setSubtitle(CarText.create("Subtitle"))
            .setIcon(CarIcon.APP_ICON)
            .setCallback(alertCallback)
            .build()
        // [END android_cars_apps_navigation_alert_creation]
    }
}

class MyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {}
}
