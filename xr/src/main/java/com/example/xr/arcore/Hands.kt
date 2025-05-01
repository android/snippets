/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.xr.arcore

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.xr.arcore.Hand
import androidx.xr.runtime.Config
import androidx.xr.runtime.HandJointType
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionConfigureConfigurationNotSupported
import androidx.xr.runtime.SessionConfigurePermissionsNotGranted
import androidx.xr.runtime.SessionConfigureSuccess
import androidx.xr.runtime.internal.GltfEntity
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Quaternion
import androidx.xr.runtime.math.Vector3
import androidx.xr.scenecore.scene
import kotlinx.coroutines.launch

fun ComponentActivity.configureSession(session: Session) {
    // [START androidxr_arcore_hand_configure]
    val newConfig = session.config.copy(
        handTracking = Config.HandTrackingMode.Enabled
    )
    when (val result = session.configure(newConfig)) {
        is SessionConfigureConfigurationNotSupported ->
            TODO(/* Some combinations of configurations are not valid. Handle this failure case. */)
        is SessionConfigurePermissionsNotGranted ->
            TODO(/* The required permissions in result.permissions have not been granted. */)
        is SessionConfigureSuccess -> TODO(/* Success! */)
    }
    // [END androidxr_arcore_hand_configure]
}

fun ComponentActivity.collectHands(session: Session) {
    lifecycleScope.launch {
        // [START androidxr_arcore_hand_collect]
        Hand.left(session)?.state?.collect { handState -> // or Hand.right(session)
            // Hand state has been updated.
            // Use the state of hand joints to update an entity's position.
            renderPlanetAtHandPalm(handState)
        }
        // [END androidxr_arcore_hand_collect]
    }
    lifecycleScope.launch {
        Hand.right(session)?.state?.collect { rightHandState ->
            renderPlanetAtFingerTip(rightHandState)
        }
    }
}

fun ComponentActivity.renderPlanetAtHandPalm(leftHandState: Hand.State) {
    val session: Session = null!!
    val palmEntity: GltfEntity = null!!
    // [START androidxr_arcore_hand_entityAtHandPalm]
    val palmPose = leftHandState.handJoints[HandJointType.PALM] ?: return

    // the down direction points in the same direction as the palm
    val angle = Vector3.angleBetween(palmPose.rotation * Vector3.Down, Vector3.Up)
    palmEntity.setHidden(angle > Math.toRadians(40.0))

    val transformedPose =
        session.scene.perceptionSpace.transformPoseTo(
            palmPose,
            session.scene.activitySpace,
        )
    val newPosition = transformedPose.translation + transformedPose.down * 0.05f
    palmEntity.setPose(Pose(newPosition, transformedPose.rotation))
    // [END androidxr_arcore_hand_entityAtHandPalm]
}

fun ComponentActivity.renderPlanetAtFingerTip(rightHandState: Hand.State) {
    val session: Session = null!!
    val indexFingerEntity: GltfEntity = null!!

    // [START androidxr_arcore_hand_entityAtIndexFingerTip]
    val tipPose = rightHandState.handJoints[HandJointType.INDEX_TIP] ?: return

    // the forward direction points towards the finger tip.
    val angle = Vector3.angleBetween(tipPose.rotation * Vector3.Forward, Vector3.Up)
    indexFingerEntity.setHidden(angle > Math.toRadians(40.0))

    val transformedPose =
        session.scene.perceptionSpace.transformPoseTo(
            tipPose,
            session.scene.activitySpace,
        )
    val position = transformedPose.translation + transformedPose.forward * 0.03f
    val rotation = Quaternion.fromLookTowards(transformedPose.up, Vector3.Up)
    indexFingerEntity.setPose(Pose(position, rotation))
    // [END androidxr_arcore_hand_entityAtIndexFingerTip]
}
