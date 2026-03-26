package com.example.cars.misc

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session

// [START android_cars_misc_helloworldsession]
class HelloWorldSession : Session() {
    // ...
    override fun onCreateScreen(intent: Intent) : Screen {
        // Handle the intent when the app is being started for the first time
        return HelloWorldScreen(carContext)
    }

    override fun onNewIntent(intent: Intent) {
        // Handle the intent when the app is already running
    }
}
// [END android_cars_misc_helloworldsession]