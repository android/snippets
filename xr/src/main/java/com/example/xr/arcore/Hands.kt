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

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.xr.arcore.Hand
import androidx.xr.arcore.HandJointType
import androidx.xr.runtime.Config
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionConfigureSuccess
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Quaternion
import androidx.xr.runtime.math.Vector3
import androidx.xr.runtime.math.toRadians
import androidx.xr.scenecore.GltfModelEntity
import androidx.xr.scenecore.scene
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun ComponentActivity.configureSession(session: Session) {
    // [START androidxr_arcore_hand_configure]
    val newConfig = session.config.copy(
        handTracking = Config.HandTrackingMode.BOTH
    )
    when (val result = session.configure(newConfig)) {
        is SessionConfigureSuccess -> TODO(/* Success! */)
        else ->
            TODO(/* The session could not be configured. See SessionConfigureResult for possible causes. */)
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

fun secondaryHandDetection(activity: Activity, session: Session) {
    fun detectGesture(handState: Flow<Hand.State>) {}
    // [START androidxr_arcore_hand_handedness]
    val handedness = Hand.getPrimaryHandSide(activity.contentResolver)
    val secondaryHand =
        if (handedness == Hand.HandSide.LEFT) Hand.right(session) else Hand.left(session)
    val handState = secondaryHand?.state ?: return
    detectGesture(handState)
    // [END androidxr_arcore_hand_handedness]
}

fun ComponentActivity.renderPlanetAtHandPalm(leftHandState: Hand.State) {
    val session: Session = null!!
    val palmEntity: GltfModelEntity = null!!
    // [START androidxr_arcore_hand_entityAtHandPalm]
    val palmPose = leftHandState.handJoints[HandJointType.HAND_JOINT_TYPE_PALM] ?: return

    // the down direction points in the same direction as the palm
    val angle = Vector3.angleBetween(palmPose.rotation * Vector3.Down, Vector3.Up)
    palmEntity.setEnabled(angle > Math.toRadians(40.0))

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
    val indexFingerEntity: GltfModelEntity = null!!

    // [START androidxr_arcore_hand_entityAtIndexFingerTip]
    val tipPose = rightHandState.handJoints[HandJointType.HAND_JOINT_TYPE_INDEX_TIP] ?: return

    // the forward direction points towards the finger tip.
    val angle = Vector3.angleBetween(tipPose.rotation * Vector3.Forward, Vector3.Up)
    indexFingerEntity.setEnabled(angle > Math.toRadians(40.0))

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

private fun detectPinch(session: Session, handState: Hand.State): Boolean {
    // [START androidxr_arcore_hand_pinch_gesture]
    val thumbTip = handState.handJoints[HandJointType.HAND_JOINT_TYPE_THUMB_TIP] ?: return false
    val thumbTipPose = session.scene.perceptionSpace.transformPoseTo(thumbTip, session.scene.activitySpace)
    val indexTip = handState.handJoints[HandJointType.HAND_JOINT_TYPE_INDEX_TIP] ?: return false
    val indexTipPose = session.scene.perceptionSpace.transformPoseTo(indexTip, session.scene.activitySpace)
    return Vector3.distance(thumbTipPose.translation, indexTipPose.translation) < 0.05
    // [END androidxr_arcore_hand_pinch_gesture]
}

private fun detectStop(session: Session, handState: Hand.State): Boolean {
    // [START androidxr_arcore_hand_stop_gesture]
    val threshold = toRadians(angleInDegrees = 30f)
    fun pointingInSameDirection(joint1: HandJointType, joint2: HandJointType): Boolean {
        val forward1 = handState.handJoints[joint1]?.forward ?: return false
        val forward2 = handState.handJoints[joint2]?.forward ?: return false
        return Vector3.angleBetween(forward1, forward2) < threshold
    }
    return pointingInSameDirection(HandJointType.HAND_JOINT_TYPE_INDEX_PROXIMAL, HandJointType.HAND_JOINT_TYPE_INDEX_TIP) &&
        pointingInSameDirection(HandJointType.HAND_JOINT_TYPE_MIDDLE_PROXIMAL, HandJointType.HAND_JOINT_TYPE_MIDDLE_TIP) &&
        pointingInSameDirection(HandJointType.HAND_JOINT_TYPE_RING_PROXIMAL, HandJointType.HAND_JOINT_TYPE_RING_TIP)
    // [END androidxr_arcore_hand_stop_gesture]
}
