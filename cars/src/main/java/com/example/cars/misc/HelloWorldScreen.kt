package com.example.cars.misc

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Header
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template

// [START android_cars_misc_helloworldscreen]
class HelloWorldScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val row = Row.Builder().setTitle("Hello world!").build()
        val pane = Pane.Builder().addRow(row).build()
        val header = Header.Builder()
            .setStartHeaderAction(Action.APP_ICON)
            .build()
        return PaneTemplate.Builder(pane)
            .setHeader(header)
            .build()
    }
}
// [END android_cars_misc_helloworldscreen]
