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

package com.example.xr.arcore.test

import androidx.activity.ComponentActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.xr.arcore.Hand
import androidx.xr.arcore.HandJointType
import androidx.xr.arcore.testing.ArCoreTestRule
import androidx.xr.runtime.HandTrackingMode
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionCreateSuccess
import androidx.xr.runtime.manifest.HAND_TRACKING
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Quaternion
import androidx.xr.runtime.math.Vector3
import com.google.common.truth.Truth.assertThat
import kotlin.test.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ArCoreTestRuleExample {

    // [START androidxr_arcore_testing_setup]
    @Rule @JvmField val arCoreTestRule = ArCoreTestRule()
    private lateinit var activityController: ActivityController<ComponentActivity>
    private lateinit var activity: ComponentActivity
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope
    private lateinit var session: Session

    @Before
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)
        activityController = Robolectric.buildActivity(ComponentActivity::class.java)
        activity = activityController.get()

        // Set up the activity permissions.
        shadowOf(activity.application).grantPermissions(HAND_TRACKING)

        activityController.create().start().resume()

        val sessionCreateResult = Session.create(activity = activity, coroutineContext = testDispatcher)
        session = (sessionCreateResult as SessionCreateSuccess).session

        // Configure the session.
        session.configure(session.config.copy(handTracking = HandTrackingMode.BOTH))
    }
    // [END androidxr_arcore_testing_setup]

    // [START androidxr_arcore_test_case]
    @Test
    fun test_thumbsUp() = runTest(testDispatcher) {
        arCoreTestRule.rightHand.isVisible = true
        arCoreTestRule.rightHand.handJointMap = gestureThumbsUp
        advanceUntilIdle()
        val handState = Hand.right(session)?.state?.value ?: fail("Did not detect a right hand")

        val isThumbsUp = detectThumbsUp(handState)
        assertThat(isThumbsUp).isTrue()
    }
    // [END androidxr_arcore_test_case]

    @Test
    fun test_notThumbsUp() = runTest(testDispatcher) {
        arCoreTestRule.rightHand.isVisible = true
        arCoreTestRule.rightHand.handJointMap = gestureV
        advanceUntilIdle()
        val handState = Hand.right(session)?.state?.value ?: fail("Did not detect a right hand")
        val isThumbsUp = detectThumbsUp(handState)
        assertThat(isThumbsUp).isFalse()
    }
}

fun detectThumbsUp(handState: Hand.State): Boolean {
    val thumbStraight =
        (handState.handJoints[HandJointType.THUMB_METACARPAL]!!.forward - handState.handJoints[HandJointType.THUMB_TIP]!!.forward).length < 0.5

    val fingerJointsCloseToPalm = listOf(
        HandJointType.INDEX_TIP,
        HandJointType.MIDDLE_TIP,
        HandJointType.RING_TIP
    ).all {
        (handState.handJoints[it]!!.translation - handState.handJoints[HandJointType.PALM]!!.translation).length < 0.05
    }
    return thumbStraight && fingerJointsCloseToPalm
}
val gestureThumbsUp =
    mapOf(HandJointType.PALM to Pose(Vector3(0.13593586f, -0.013158908f, -0.30986848f), Quaternion(-0.07648782f, 0.44922686f, -0.5673647f, 0.68588793f)), HandJointType.WRIST to Pose(Vector3(0.18127777f, -0.04036817f, -0.2715446f), Quaternion(-0.03890163f, 0.44938245f, -0.5231991f, 0.72305244f)), HandJointType.THUMB_METACARPAL to Pose(Vector3(0.15840198f, -0.015633538f, -0.27011338f), Quaternion(0.25765032f, 0.6725719f, -0.37797198f, 0.581722f)), HandJointType.THUMB_PROXIMAL to Pose(Vector3(0.12846541f, 0.025532225f, -0.26820466f), Quaternion(0.60207963f, 0.48774832f, -0.2015351f, 0.5991539f)), HandJointType.THUMB_DISTAL to Pose(Vector3(0.118897974f, 0.050692264f, -0.26273555f), Quaternion(0.6276169f, 0.4293863f, -0.2707826f, 0.5902553f)), HandJointType.THUMB_TIP to Pose(Vector3(0.11239989f, 0.07950714f, -0.2583036f), Quaternion(0.6276169f, 0.4293863f, -0.2707826f, 0.5902553f)), HandJointType.INDEX_METACARPAL to Pose(Vector3(0.16033095f, -0.018314373f, -0.28049868f), Quaternion(-0.044817265f, 0.5122015f, -0.5723443f, 0.6387982f)), HandJointType.INDEX_PROXIMAL to Pose(Vector3(0.107179075f, 0.021533456f, -0.31599528f), Quaternion(-0.55457497f, 0.6855494f, -0.102787256f, 0.4603297f)), HandJointType.INDEX_INTERMEDIATE to Pose(Vector3(0.07752076f, 0.007801927f, -0.29495406f), Quaternion(0.7149406f, -0.53468245f, -0.4392363f, 0.10023027f)), HandJointType.INDEX_DISTAL to Pose(Vector3(0.093687735f, 4.9139105E-4f, -0.28197238f), Quaternion(0.61264414f, -0.31699702f, -0.6372203f, 0.34370092f)), HandJointType.INDEX_TIP to Pose(Vector3(0.1166471f, 0.0013936138f, -0.28378844f), Quaternion(0.61264414f, -0.31699702f, -0.6372203f, 0.34370092f)), HandJointType.MIDDLE_METACARPAL to Pose(Vector3(0.161193f, -0.027702726f, -0.28886288f), Quaternion(-0.07648784f, 0.4492268f, -0.5673647f, 0.685888f)), HandJointType.MIDDLE_PROXIMAL to Pose(Vector3(0.11067873f, 0.0013849082f, -0.33087406f), Quaternion(-0.62964034f, 0.63541615f, -0.15290299f, 0.42002377f)), HandJointType.MIDDLE_INTERMEDIATE to Pose(Vector3(0.08015034f, -0.012421122f, -0.30621234f), Quaternion(0.7128128f, -0.5821132f, -0.34882817f, 0.1770908f)), HandJointType.MIDDLE_DISTAL to Pose(Vector3(0.10116805f, -0.017270802f, -0.28525817f), Quaternion(0.49823985f, -0.3021429f, -0.60298073f, 0.54486793f)), HandJointType.MIDDLE_TIP to Pose(Vector3(0.123201214f, -0.012691015f, -0.2940116f), Quaternion(0.49823985f, -0.3021429f, -0.60298073f, 0.54486793f)), HandJointType.RING_METACARPAL to Pose(Vector3(0.16304983f, -0.03894596f, -0.2930074f), Quaternion(-0.1353496f, 0.41923693f, -0.55014765f, 0.70940715f)), HandJointType.RING_PROXIMAL to Pose(Vector3(0.11295157f, -0.020809509f, -0.33422083f), Quaternion(-0.67903876f, 0.58647716f, -0.19874121f, 0.39427507f)), HandJointType.RING_INTERMEDIATE to Pose(Vector3(0.08441943f, -0.032515682f, -0.3099064f), Quaternion(0.71151716f, -0.54315823f, -0.33604163f, 0.2929139f)), HandJointType.RING_DISTAL to Pose(Vector3(0.10925031f, -0.031085238f, -0.29084665f), Quaternion(0.4742162f, -0.33734766f, -0.54695463f, 0.60179424f)), HandJointType.RING_TIP to Pose(Vector3(0.12905067f, -0.026568463f, -0.29879102f), Quaternion(0.4742162f, -0.33734766f, -0.54695463f, 0.60179424f)), HandJointType.LITTLE_METACARPAL to Pose(Vector3(0.16440037f, -0.051221382f, -0.29374436f), Quaternion(-0.20768654f, 0.36514902f, -0.5562916f, 0.71698827f)), HandJointType.LITTLE_PROXIMAL to Pose(Vector3(0.11753103f, -0.044486694f, -0.33393008f), Quaternion(-0.72091824f, 0.5555971f, -0.21803516f, 0.3522066f)), HandJointType.LITTLE_INTERMEDIATE to Pose(Vector3(0.094225325f, -0.05277244f, -0.31044134f), Quaternion(0.7077855f, -0.5113742f, -0.31955776f, 0.3679934f)), HandJointType.LITTLE_DISTAL to Pose(Vector3(0.11225751f, -0.04858123f, -0.2981928f), Quaternion(0.5145514f, -0.39718676f, -0.4414714f, 0.6185326f)), HandJointType.LITTLE_TIP to Pose(Vector3(0.13028295f, -0.044378098f, -0.3016371f), Quaternion(0.5145514f, -0.39718676f, -0.4414714f, 0.6185326f)))
val gestureV =
    mapOf(HandJointType.PALM to Pose(Vector3(0.2234865f, -0.07976054f, -0.21628146f), Quaternion(0.5706851f, 0.03149831f, 0.07317911f, 0.8172951f)), HandJointType.WRIST to Pose(Vector3(0.23113234f, -0.14235981f, -0.19278167f), Quaternion(0.5747781f, -0.005840625f, 0.11225852f, 0.81055176f)), HandJointType.THUMB_METACARPAL to Pose(Vector3(0.20714317f, -0.123696394f, -0.21087125f), Quaternion(0.3100977f, -0.023733396f, 0.339164f, 0.88783103f)), HandJointType.THUMB_PROXIMAL to Pose(Vector3(0.19834094f, -0.09403943f, -0.25307828f), Quaternion(-0.03379123f, -0.32680202f, 0.58814466f, 0.7390159f)), HandJointType.THUMB_DISTAL to Pose(Vector3(0.21320038f, -0.08474395f, -0.27508157f), Quaternion(-0.0207383f, -0.39749625f, 0.57455784f, 0.71515733f)), HandJointType.THUMB_TIP to Pose(Vector3(0.23246855f, -0.07269515f, -0.2955593f), Quaternion(-0.0207383f, -0.39749625f, 0.57455784f, 0.71515733f)), HandJointType.INDEX_METACARPAL to Pose(Vector3(0.21609627f, -0.11685363f, -0.2053007f), Quaternion(0.54485095f, 0.121148534f, 0.0858834f, 0.82527846f)), HandJointType.INDEX_PROXIMAL to Pose(Vector3(0.19345093f, -0.049083613f, -0.23437752f), Quaternion(0.586476f, 0.119359225f, 0.106421545f, 0.79402375f)), HandJointType.INDEX_INTERMEDIATE to Pose(Vector3(0.18062322f, -0.012817434f, -0.2446229f), Quaternion(0.5423127f, 0.11090955f, 0.11145594f, 0.82533234f)), HandJointType.INDEX_DISTAL to Pose(Vector3(0.173931f, 0.0068317307f, -0.25334585f), Quaternion(0.5099013f, 0.07584885f, 0.0950405f, 0.8515955f)), HandJointType.INDEX_TIP to Pose(Vector3(0.16864908f, 0.026561381f, -0.26518023f), Quaternion(0.5099013f, 0.07584885f, 0.0950405f, 0.8515955f)), HandJointType.MIDDLE_METACARPAL to Pose(Vector3(0.22839816f, -0.11352901f, -0.20367038f), Quaternion(0.5706851f, 0.03149831f, 0.07317911f, 0.8172951f)), HandJointType.MIDDLE_PROXIMAL to Pose(Vector3(0.21857485f, -0.04599206f, -0.22889253f), Quaternion(0.5973367f, -0.032230783f, -0.010606925f, 0.80127245f)), HandJointType.MIDDLE_INTERMEDIATE to Pose(Vector3(0.22126037f, -0.0051010866f, -0.24055904f), Quaternion(0.5784258f, 8.600886E-4f, 0.017277872f, 0.8155515f)), HandJointType.MIDDLE_DISTAL to Pose(Vector3(0.22078234f, 0.024031831f, -0.2505384f), Quaternion(0.5753808f, 0.008077383f, 0.026221696f, 0.8174253f)), HandJointType.MIDDLE_TIP to Pose(Vector3(0.21972649f, 0.046870448f, -0.25996387f), Quaternion(0.5753808f, 0.008077383f, 0.026221696f, 0.8174253f)), HandJointType.RING_METACARPAL to Pose(Vector3(0.23981902f, -0.11477184f, -0.20335926f), Quaternion(0.55009776f, -0.062150095f, 0.08299228f, 0.82863873f)), HandJointType.RING_PROXIMAL to Pose(Vector3(0.24060301f, -0.052950513f, -0.22931267f), Quaternion(0.28292912f, 0.015880022f, -0.060402635f, 0.9571053f)), HandJointType.RING_INTERMEDIATE to Pose(Vector3(0.2405244f, -0.031430718f, -0.26329195f), Quaternion(-0.14633991f, 0.060540486f, -0.02554371f, 0.98704964f)), HandJointType.RING_DISTAL to Pose(Vector3(0.23659474f, -0.04035987f, -0.29386503f), Quaternion(-0.35714754f, 0.07298706f, 0.011346126f, 0.9311229f)), HandJointType.RING_TIP to Pose(Vector3(0.2337395f, -0.055983663f, -0.30956727f), Quaternion(-0.35714754f, 0.07298706f, 0.011346126f, 0.9311229f)), HandJointType.LITTLE_METACARPAL to Pose(Vector3(0.25019777f, -0.118370585f, -0.20323984f), Quaternion(0.5104549f, -0.16275606f, 0.07208905f, 0.84127843f)), HandJointType.LITTLE_PROXIMAL to Pose(Vector3(0.2624146f, -0.06454122f, -0.22922264f), Quaternion(0.03228906f, 0.13241854f, -0.02786832f, 0.9902758f)), HandJointType.LITTLE_INTERMEDIATE to Pose(Vector3(0.25264052f, -0.06346615f, -0.2627445f), Quaternion(-0.35992625f, 0.1361967f, 0.033422504f, 0.9223809f)), HandJointType.LITTLE_DISTAL to Pose(Vector3(0.24733093f, -0.07822391f, -0.27920222f), Quaternion(-0.5559289f, 0.16889648f, 0.09974979f, 0.8077544f)), HandJointType.LITTLE_TIP to Pose(Vector3(0.24553189f, -0.096395634f, -0.2853927f), Quaternion(-0.5559289f, 0.16889648f, 0.09974979f, 0.8077544f)))
