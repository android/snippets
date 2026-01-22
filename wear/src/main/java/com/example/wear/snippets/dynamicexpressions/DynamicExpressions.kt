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

@file:SuppressLint("MissingPermission")

package com.example.wear.snippets.dynamicexpressions

import android.annotation.SuppressLint
import androidx.wear.protolayout.expression.DynamicBuilders.DynamicInstant
import androidx.wear.protolayout.expression.DynamicBuilders.DynamicInt32
import androidx.wear.protolayout.expression.PlatformHealthSources

// [START android_wear_dynamic_expressions_platform_sources]
val systemTime = DynamicInstant.platformTimeWithSecondsPrecision()
val steps: DynamicInt32 = PlatformHealthSources.dailySteps()
// [END android_wear_dynamic_expressions_platform_sources]

// [START android_wear_dynamic_expressions_addition]
val dynamicAdditionResult = DynamicInt32.constant(1).plus(2)
// [END android_wear_dynamic_expressions_addition]

// [START android_wear_dynamic_expressions_animated_addition]
val x = DynamicInt32.constant(1).plus(2).animate()
// [END android_wear_dynamic_expressions_animated_addition]
