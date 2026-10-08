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

package com.example.compose.snippets.performance.skill

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private object RememberFilterBad {
    // [START android_compose_performance_effects_remember_filter_bad]
    @Composable
    fun FilteredFeed(rawList: List<Post>, query: String) {
        // Avoid: Heavy filtering and sorting on every recomposition
        val filteredList = rawList
            .filter { it.title.contains(query) }
            .sortedBy { it.timestamp }
        PostList(posts = filteredList)
    }
    // [END android_compose_performance_effects_remember_filter_bad]
}

private object RememberFilterGood {
    // [START android_compose_performance_effects_remember_filter_good]
    @Composable
    fun FilteredFeed(rawList: List<Post>, query: String) {
        val filteredList = remember(rawList, query) {
            rawList
                .filter { it.title.contains(query) }
                .sortedBy { it.timestamp }
        }
        PostList(posts = filteredList)
    }
    // [END android_compose_performance_effects_remember_filter_good]
}

private object AllocationBad {
    // [START android_compose_performance_effects_allocation_bad]
    @Composable
    fun DateBadge(timestamp: Long) {
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        Text(text = formatter.format(Date(timestamp)))
    }
    // [END android_compose_performance_effects_allocation_bad]
}

private object AllocationGood {
    // [START android_compose_performance_effects_allocation_good]
    // Reusable formatter allocated once at top-level or remembered
    private val dateFormatter =
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    @Composable
    fun DateBadge(timestamp: Long) {
        val formattedDate = dateFormatter.format(Date(timestamp))

        Text(text = formattedDate)
    }
    // [END android_compose_performance_effects_allocation_good]
}

private object IoBad {
    // [START android_compose_performance_effects_io_bad]
    @Composable
    fun ProfileScreen(fileUri: Uri) {
        var state by remember { mutableStateOf<Data?>(null) }
        LaunchedEffect(fileUri) {
            val data = parseJsonFromDisk(fileUri) // Blocks main dispatcher!
            state = data
        }
    }
    // [END android_compose_performance_effects_io_bad]
}

private object IoGood {
    // [START android_compose_performance_effects_io_good]
    @Composable
    fun ProfileScreen(fileUri: Uri) {
        val state by produceState<Data?>(initialValue = null, fileUri) {
            value = withContext(Dispatchers.IO) { parseJsonFromDisk(fileUri) }
        }
    }
    // [END android_compose_performance_effects_io_good]
}

private object BodySideEffectBad {
    // [START android_compose_performance_effects_body_side_effect_bad]
    @Composable
    fun UserProfile(userId: String, viewModel: ProfileViewModel) {
        // Avoid: Runs on every recomposition and speculative pass
        viewModel.trackProfileImpression(userId)
        Text(text = "User: $userId")
    }
    // [END android_compose_performance_effects_body_side_effect_bad]
}

private object BodySideEffectGood {
    // [START android_compose_performance_effects_body_side_effect_good]
    @Composable
    fun UserProfile(userId: String, viewModel: ProfileViewModel) {
        // Compose 1.12+: keyed SideEffect
        SideEffect(userId) {
            viewModel.trackProfileImpression(userId)
        }
        Text(text = "User: $userId")
    }
    // [END android_compose_performance_effects_body_side_effect_good]
}

@Composable
private fun NonSuspendEffectBad(itemId: String) {
    // [START android_compose_performance_effects_non_suspend_bad]
    LaunchedEffect(itemId) {
        analyticsTracker.trackScreenView(itemId) // Non-suspend function!
    }
    // [END android_compose_performance_effects_non_suspend_bad]
}

@Composable
private fun NonSuspendEffectGood(itemId: String) {
    // [START android_compose_performance_effects_non_suspend_good]
    // Compose 1.12+ (supports keys directly):
    SideEffect(itemId) {
        analyticsTracker.trackScreenView(itemId)
    }
    // [END android_compose_performance_effects_non_suspend_good]
}

private object FlowLifecycleBad {
    // [START android_compose_performance_effects_flow_lifecycle_bad]
    @Composable
    fun HomeFeed(viewModel: FeedViewModel) {
        // Avoid on Android: Flow stays active when activity is stopped
        val uiState by viewModel.feedState.collectAsState()
        FeedContent(uiState = uiState)
    }
    // [END android_compose_performance_effects_flow_lifecycle_bad]
}

private object FlowLifecycleGood {
    // [START android_compose_performance_effects_flow_lifecycle_good]
    // import androidx.lifecycle.compose.collectAsStateWithLifecycle

    @Composable
    fun HomeFeed(viewModel: FeedViewModel) {
        val uiState by viewModel.feedState.collectAsStateWithLifecycle()
        FeedContent(uiState = uiState)
    }
    // [END android_compose_performance_effects_flow_lifecycle_good]
}

private object ReceiverBad {
    // [START android_compose_performance_effects_receiver_bad]
    @Composable
    fun TimeZoneListItem(timeZoneId: String) {
        val context = LocalContext.current
        DisposableEffect(Unit) {
            // Avoid: Multiplied BroadcastReceivers per visible item!
            val receiver = object : BroadcastReceiver() {
                // [START_EXCLUDE]
                override fun onReceive(context: Context?, intent: Intent?) {}
                // [END_EXCLUDE]
            }
            context.registerReceiver(
                receiver,
                IntentFilter(Intent.ACTION_TIMEZONE_CHANGED),
            )
            onDispose { context.unregisterReceiver(receiver) }
        }
        ItemRow(timeZoneId)
    }
    // [END android_compose_performance_effects_receiver_bad]
}

private object ReceiverGood {
    // [START android_compose_performance_effects_receiver_good]
    @Composable
    fun TimeZoneList(timeZoneIds: List<String>) {
        val context = LocalContext.current
        var currentTimeZone by remember {
            mutableStateOf(TimeZone.getDefault())
        }

        DisposableEffect(context) {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context?, intent: Intent?) {
                    currentTimeZone = TimeZone.getDefault()
                }
            }
            context.registerReceiver(
                receiver,
                IntentFilter(Intent.ACTION_TIMEZONE_CHANGED),
            )
            onDispose { context.unregisterReceiver(receiver) }
        }

        LazyColumn {
            items(timeZoneIds, key = { it }) { id ->
                TimeZoneListItem(
                    timeZoneId = id,
                    currentTimeZone = currentTimeZone,
                )
            }
        }
    }
    // [END android_compose_performance_effects_receiver_good]

    @Composable
    private fun TimeZoneListItem(timeZoneId: String, currentTimeZone: TimeZone) {}
}

@Composable
private fun OptionalEffectBad(onInit: (() -> Unit)?) {
    // [START android_compose_performance_effects_optional_bad]
    // Avoid: Launches coroutine for synchronous work, runs even if null
    LaunchedEffect(onInit) {
        onInit?.invoke()
    }
    // [END android_compose_performance_effects_optional_bad]
}

@Composable
private fun OptionalEffectGood(onInit: (() -> Unit)?) {
    // [START android_compose_performance_effects_optional_good]
    if (onInit != null) {
        SideEffect(onInit) {
            onInit()
        }
    }
    // [END android_compose_performance_effects_optional_good]
}

private object UpdatedStateBad {
    // [START android_compose_performance_effects_updated_state_bad]
    @Composable
    fun PeriodicTicker(intervalMs: Long, onTick: () -> Unit) {
        LaunchedEffect(intervalMs) {
            while (isActive) {
                delay(intervalMs)
                onTick() // Stale reference if onTick callback instance changes!
            }
        }
    }
    // [END android_compose_performance_effects_updated_state_bad]
}

private object UpdatedStateGood {
    // [START android_compose_performance_effects_updated_state_good]
    @Composable
    fun PeriodicTicker(intervalMs: Long, onTick: () -> Unit) {
        val currentOnTick by rememberUpdatedState(onTick)
        LaunchedEffect(intervalMs) {
            while (isActive) {
                delay(intervalMs)
                currentOnTick()
            }
        }
    }
    // [END android_compose_performance_effects_updated_state_good]
}

@Composable
private fun LegacyTimerBad() {
    var showBanner by remember { mutableStateOf(true) }
    // [START android_compose_performance_effects_timer_bad]
    DisposableEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable { showBanner = false }
        handler.postDelayed(runnable, 3000L)
        onDispose { handler.removeCallbacks(runnable) }
    }
    // [END android_compose_performance_effects_timer_bad]
}

@Composable
private fun CoroutineTimerGood() {
    var showBanner by remember { mutableStateOf(true) }
    // [START android_compose_performance_effects_timer_good]
    LaunchedEffect(Unit) {
        delay(3000L)
        showBanner = false
    }
    // [END android_compose_performance_effects_timer_good]
}

private object CombineEffectsBad {
    // [START android_compose_performance_effects_combine_bad]
    @Composable
    fun ShoppingCartScreen(viewModel: CartViewModel) {
        // Avoid: 3 LaunchedEffects allocate 3 effect nodes and launchers
        LaunchedEffect(Unit) { viewModel.loadCart() }
        LaunchedEffect(Unit) { viewModel.loadPaymentMethods() }
        LaunchedEffect(Unit) { viewModel.loadDeliveryAddresses() }

        CartContent()
    }
    // [END android_compose_performance_effects_combine_bad]
}

private object CombineEffectsGood {
    // [START android_compose_performance_effects_combine_good]
    @Composable
    fun ShoppingCartScreen(viewModel: CartViewModel) {
        // Optimized: 1 LaunchedEffect node launches concurrent jobs
        LaunchedEffect(Unit) {
            launch { viewModel.loadCart() }
            launch { viewModel.loadPaymentMethods() }
            launch { viewModel.loadDeliveryAddresses() }
        }

        CartContent()
    }
    // [END android_compose_performance_effects_combine_good]
}

@Composable
private fun PostList(posts: List<Post>) {}

private class Data

private fun parseJsonFromDisk(fileUri: Uri): Data = Data()

private class ProfileViewModel {
    fun trackProfileImpression(userId: String) {}
}

private object analyticsTracker {
    fun trackScreenView(itemId: String) {}
}

private class FeedUiState

private class FeedViewModel {
    val feedState: StateFlow<FeedUiState> = MutableStateFlow(FeedUiState())
}

@Composable
private fun FeedContent(uiState: FeedUiState) {}

@Composable
private fun ItemRow(timeZoneId: String) {}

private class CartViewModel {
    suspend fun loadCart() {}
    suspend fun loadPaymentMethods() {}
    suspend fun loadDeliveryAddresses() {}
}

@Composable
private fun CartContent() {}
