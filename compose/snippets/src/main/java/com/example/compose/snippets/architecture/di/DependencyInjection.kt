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

package com.example.compose.snippets.architecture.di

private object CarNoDi {
    // [START android_di_intro_car_no_di]
    class Car {

        private val engine = Engine()

        fun start() {
            engine.start()
        }
    }

    fun main(args: Array<String>) {
        val car = Car()
        car.start()
    }
    // [END android_di_intro_car_no_di]
}

private object CarConstructorDi {
    // [START android_di_intro_car_constructor_di]
    class Car(private val engine: Engine) {
        fun start() {
            engine.start()
        }
    }

    fun main(args: Array<String>) {
        val engine = Engine()
        val car = Car(engine)
        car.start()
    }
    // [END android_di_intro_car_constructor_di]
}

private object CarFieldDi {
    // [START android_di_intro_car_field_di]
    class Car {
        lateinit var engine: Engine

        fun start() {
            engine.start()
        }
    }

    fun main(args: Array<String>) {
        val car = Car()
        car.engine = Engine()
        car.start()
    }
    // [END android_di_intro_car_field_di]
}

private object CarServiceLocator {
    // [START android_di_intro_service_locator]
    object ServiceLocator {
        fun getEngine(): Engine = Engine()
    }

    class Car {
        private val engine = ServiceLocator.getEngine()

        fun start() {
            engine.start()
        }
    }

    fun main(args: Array<String>) {
        val car = Car()
        car.start()
    }
    // [END android_di_intro_service_locator]
}

private class Engine {
    fun start() {}
}
