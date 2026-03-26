package com.example.cars.templated_apps

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.Template

class MainScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        // [START cars_templated_apps_placelistmaptemplate_setOnContentRefreshListener]
        return PlaceListMapTemplate.Builder()
        // ...
            .setOnContentRefreshListener {
                // Execute any desired lggic
                // ...
                // Then call invalidate() so onGetTemplate() is called again
                invalidate()
            }
            .build()
            // [END cars_templated_apps_placelistmaptemplate_setOnContentRefreshListener]
    }
}
