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

package com.example.cars.parked

import android.car.Car
import android.car.drivingstate.CarUxRestrictions
import android.car.drivingstate.CarUxRestrictionsManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.media3.common.ForwardingSimpleBasePlayer
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.google.common.util.concurrent.ListenableFuture

// [START android_cars_parked_car_aware_player]
@UnstableApi
class CarAwarePlayer(context: Context) :
    ForwardingSimpleBasePlayer(ExoPlayer.Builder(context).build()) {
    private var shouldPreventPlay = false
    private var pausedByUxRestrictions = false
    private lateinit var carUxRestrictionsManager: CarUxRestrictionsManager

    init {
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
            val car = Car.createCar(context)
            carUxRestrictionsManager =
                car.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE) as CarUxRestrictionsManager
            shouldPreventPlay =
                carUxRestrictionsManager.currentCarUxRestrictions.isRequiresDistractionOptimization
            invalidateState()

            carUxRestrictionsManager.registerListener { restrictions: CarUxRestrictions ->
                shouldPreventPlay = restrictions.isRequiresDistractionOptimization
                if (!shouldPreventPlay && pausedByUxRestrictions) {
                    handleSetPlayWhenReady(true)
                    invalidateState()
                } else if (shouldPreventPlay && isPlaying) {
                    pausedByUxRestrictions = true
                    handleSetPlayWhenReady(false)
                    invalidateState()
                }
            }
        }
    }

    override fun getState(): State {
        val state = super.getState()
        return state.buildUpon()
            .setAvailableCommands(
                state.availableCommands.buildUpon()
                    .removeIf(COMMAND_PLAY_PAUSE, shouldPreventPlay)
                    .build()
            )
            .build()
    }

    override fun handleRelease(): ListenableFuture<*> {
        if (::carUxRestrictionsManager.isInitialized) {
            carUxRestrictionsManager.unregisterListener()
        }
        return super.handleRelease()
    }
}
// [END android_cars_parked_car_aware_player]
