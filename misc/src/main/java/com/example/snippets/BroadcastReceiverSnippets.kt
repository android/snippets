package com.example.snippets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// Warning for reader: This file contains both the code snippets for apps _sending_ broadcasts, as
// those that are _receiving_ broadcasts. Do not consider this a reference implementation.
//
// The actual sample demonstrates how data can be passed from a broadcast receiver back to the UI,
// through an intermediary data repository.

@AndroidEntryPoint
// [START android_broadcast_receiver_2_class]
class MyBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dataRepository: DataRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.snippets.ACTION_UPDATE_DATA") {
            val data = intent.getStringExtra("com.example.snippets.DATA") ?: "No data"
            // Do something with the data, for example send it to a data repository:
            dataRepository.updateData(data)
        }
    }
}
// [END android_broadcast_receiver_2_class]

@HiltViewModel
class BroadcastReceiverViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    dataRepository: DataRepository
) : ViewModel() {
    val data = dataRepository.data

    @Suppress("MemberVisibilityCanBePrivate")
    // [START android_broadcast_receiver_3_definition]
    val myBroadcastReceiver = MyBroadcastReceiver()
    // [END android_broadcast_receiver_3_definition]

    fun registerBroadcastReceiver() {
        // [START android_broadcast_receiver_4_intent_filter]
        val filter = IntentFilter("com.example.snippets.ACTION_UPDATE_DATA")
        // [END android_broadcast_receiver_4_intent_filter]
        // [START android_broadcast_receiver_5_exported]
        val listenToBroadcastsFromOtherApps = false
        val receiverFlags = if (listenToBroadcastsFromOtherApps) {
            ContextCompat.RECEIVER_EXPORTED
        } else {
            ContextCompat.RECEIVER_NOT_EXPORTED
        }
        // [END android_broadcast_receiver_5_exported]

        // [START android_broadcast_receiver_6_register]
        ContextCompat.registerReceiver(context, myBroadcastReceiver, filter, receiverFlags)
        // [END android_broadcast_receiver_6_register]

        // [START android_broadcast_receiver_12_register_with_permission]
        ContextCompat.registerReceiver(
            context, myBroadcastReceiver, filter,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            null, // scheduler that defines thread, null means run on main thread
            receiverFlags
        )
        // [END android_broadcast_receiver_12_register_with_permission]
    }

    fun unregisterBroadcastReceiver() {
        context.unregisterReceiver(myBroadcastReceiver)
    }

    fun sendBroadcast(newData: String, usePermission: Boolean = false) {
        if(!usePermission) {
            // [START android_broadcast_receiver_8_send]
            val intent = Intent("com.example.snippets.ACTION_UPDATE_DATA").apply {
                putExtra("com.example.snippets.DATA", newData)
                setPackage("com.example.snippets")
            }
            context.sendBroadcast(intent)
            // [END android_broadcast_receiver_8_send]
        } else {
            val intent = Intent("com.example.snippets.ACTION_UPDATE_DATA").apply {
                putExtra("com.example.snippets.DATA", newData)
                setPackage("com.example.snippets")
            }
            // [START android_broadcast_receiver_9_send_with_permission]
            context.sendBroadcast(intent, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            // [END android_broadcast_receiver_9_send_with_permission]

        }
    }
}

@Suppress("NAME_SHADOWING")
@Composable
fun LifecycleScopedBroadcastReceiver(
    registerReceiver: () -> Unit,
    unregisterReceiver: () -> Unit
) {
    val registerReceiver by rememberUpdatedState(registerReceiver)
    val unregisterReceiver by rememberUpdatedState(unregisterReceiver)
    // [START android_broadcast_receiver_7_lifecycle_scoped]
    LifecycleStartEffect(true) {
        registerReceiver()
        onStopOrDispose { unregisterReceiver() }
    }
    // [END android_broadcast_receiver_7_lifecycle_scoped]
}

@Composable
fun BroadcastReceiverSample(
    modifier: Modifier = Modifier,
    viewModel: BroadcastReceiverViewModel = hiltViewModel()
) {
    val data by viewModel.data.collectAsStateWithLifecycle()
    BroadcastReceiverSample(
        modifier = modifier,
        data = data,
        registerBroadcastReceiver = viewModel::registerBroadcastReceiver,
        unregisterBroadcastReceiver = viewModel::unregisterBroadcastReceiver,
        sendBroadcast = viewModel::sendBroadcast
    )
}

@Composable
fun BroadcastReceiverSample(
    modifier: Modifier = Modifier,
    data: String,
    registerBroadcastReceiver: () -> Unit,
    unregisterBroadcastReceiver: () -> Unit,
    sendBroadcast: (newData: String) -> Unit
) {
    var newData by remember { mutableStateOf("") }
    Scaffold { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Fill in a word, send broadcast, and see it added to the bottom")
            Spacer(Modifier.height(16.dp))
            TextField(newData, onValueChange = { newData = it }, Modifier.widthIn(min = 160.dp))
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { sendBroadcast(newData) }) {
                    Text("Send broadcast")
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(data, Modifier.verticalScroll(rememberScrollState()))
        }
    }
    LifecycleScopedBroadcastReceiver(registerBroadcastReceiver, unregisterBroadcastReceiver)
}

class MyBroadcastReceiverWithPermission : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // no-op, only used to demonstrate manifest registration of receiver with permission
    }
}

// Ignore following code - it's only used to demonstrate best practices, not part of the sample
@Suppress("unused")
// [START android_broadcast_receiver_13_activity]
class MyActivity : ComponentActivity() {
    private val myBroadcastReceiver = MyBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START_EXCLUDE]
        val filter = IntentFilter("com.example.snippets.ACTION_UPDATE_DATA")
        val listenToBroadcastsFromOtherApps = false
        val receiverFlags = if (listenToBroadcastsFromOtherApps) {
            ContextCompat.RECEIVER_EXPORTED
        } else {
            ContextCompat.RECEIVER_NOT_EXPORTED
        }
        // [END_EXCLUDE]
        ContextCompat.registerReceiver(this, myBroadcastReceiver, filter, receiverFlags)
        setContent { MyApp() }
    }

    override fun onDestroy() {
        super.onDestroy()
        // When you forget to unregister your receiver here, you're causing a leak!
        this.unregisterReceiver(myBroadcastReceiver)
    }
}
// [END android_broadcast_receiver_13_activity]

@Composable
fun MyApp() {}

@Suppress("unused")
// [START android_broadcast_receiver_14_stateless]
@Composable
fun MyStatefulScreen() {
    val myBroadcastReceiver = remember { MyBroadcastReceiver()}
    val context = LocalContext.current
    LifecycleStartEffect(true) {
        // [START_EXCLUDE]
        val filter = IntentFilter("com.example.snippets.ACTION_UPDATE_DATA")
        val listenToBroadcastsFromOtherApps = false
        val flags = if (listenToBroadcastsFromOtherApps) {
            ContextCompat.RECEIVER_EXPORTED
        } else {
            ContextCompat.RECEIVER_NOT_EXPORTED
        }
        // [END_EXCLUDE]
        ContextCompat.registerReceiver(context, myBroadcastReceiver, filter, flags)
        onStopOrDispose { context.unregisterReceiver(myBroadcastReceiver) }
    }
    MyStatelessScreen()
}

@Composable
fun MyStatelessScreen() {
    // Implement your screen
}
// [END android_broadcast_receiver_14_stateless]
