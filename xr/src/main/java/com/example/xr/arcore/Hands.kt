package com.example.xr.arcore

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.xr.arcore.Hand
import androidx.xr.arcore.HandJointType
import androidx.xr.compose.platform.setSubspaceContent
import androidx.xr.runtime.Session
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Quaternion
import androidx.xr.runtime.math.Vector3
import androidx.xr.scenecore.Entity
import androidx.xr.scenecore.GltfModel
import androidx.xr.scenecore.GltfModelEntity
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

class SampleHandsActivity : ComponentActivity() {
    lateinit var session: Session
    lateinit var scenecoreSession: androidx.xr.scenecore.Session
    lateinit var sessionHelper: SessionLifecycleHelper

    var palmEntity: Entity? = null
    var indexFingerEntity: Entity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSubspaceContent { }

        scenecoreSession = androidx.xr.scenecore.Session.create(this@SampleHandsActivity)
        lifecycleScope.launch {
            val model = GltfModel.create(scenecoreSession, "models/saturn_rings.glb").await()
            palmEntity = GltfModelEntity.create(scenecoreSession, model).apply {
                setScale(0.3f)
                setHidden(true)
            }
            indexFingerEntity = GltfModelEntity.create(scenecoreSession, model).apply {
                setScale(0.2f)
                setHidden(true)
            }
        }

        sessionHelper = SessionLifecycleHelper(
            onCreateCallback = { session = it },
            onResumeCallback = {
                collectHands(session)
            }
        )
        lifecycle.addObserver(sessionHelper)
    }
}

fun SampleHandsActivity.collectHands(session: Session) {
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

@SuppressLint("RestrictedApi") // HandJointType is mistakenly @Restrict: b/397415504
fun SampleHandsActivity.renderPlanetAtHandPalm(leftHandState: Hand.State) {
    val palmEntity = palmEntity ?: return
    // [START androidxr_arcore_hand_entityAtHandPalm]
    val palmPose = leftHandState.handJoints[HandJointType.PALM] ?: return

    // the down direction points in the same direction as the palm
    val angle = Vector3.angleBetween(palmPose.rotation * Vector3.Down, Vector3.Up)
    palmEntity.setHidden(angle > Math.toRadians(40.0))

    val transformedPose =
        scenecoreSession.perceptionSpace.transformPoseTo(
            palmPose,
            scenecoreSession.activitySpace,
        )
    val newPosition = transformedPose.translation + transformedPose.down * 0.05f
    palmEntity.setPose(Pose(newPosition, transformedPose.rotation))
    // [END androidxr_arcore_hand_entityAtHandPalm]
}

@SuppressLint("RestrictedApi") // HandJointType is mistakenly @Restrict: b/397415504
fun SampleHandsActivity.renderPlanetAtFingerTip(rightHandState: Hand.State) {
    val indexFingerEntity = indexFingerEntity ?: return

    // [START androidxr_arcore_hand_entityAtIndexFingerTip]
    val tipPose = rightHandState.handJoints[HandJointType.INDEX_TIP] ?: return

    // the forward direction points towards the finger tip.
    val angle = Vector3.angleBetween(tipPose.rotation * Vector3.Forward, Vector3.Up)
    indexFingerEntity.setHidden(angle > Math.toRadians(40.0))

    val transformedPose =
        scenecoreSession.perceptionSpace.transformPoseTo(
            tipPose,
            scenecoreSession.activitySpace,
        )
    val position = transformedPose.translation + transformedPose.forward * 0.03f
    val rotation = Quaternion.fromLookTowards(transformedPose.up, Vector3.Up)
    indexFingerEntity.setPose(Pose(position, rotation))
    // [END androidxr_arcore_hand_entityAtIndexFingerTip]
}