// [START android_permissions_private_alternatives_location_button_kotlin]
package com.example.snippets.location

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.locationbutton.compose.LocationButton
import androidx.core.locationbutton.compose.LocationButtonTextType

@Composable
fun LocationPermissionScreen(onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) {
    // Renders the secure system-trusted Location Button composable
    LocationButton(
        // Callback triggered when the user taps the secure button and makes a decision on the permission dialog
        onPermissionResult = { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        },
        /* ============================================================================
         * VISUAL CUSTOMIZATIONS
         * Un-comment any of the parameters below to customize the button's aesthetics.
         * If omitted, the button falls back to secure, high-contrast system defaults.
         * ============================================================================ */
        /*
        // LABEL TEXT TYPE:
        // Predefined system strings rendered inside the secure process.
        // Options: PreciseLocation, UsePreciseLocation, SharePreciseLocation,
        // NearMyPreciseLocation, or None (for an icon-only button).
        textType = LocationButtonTextType.UsePreciseLocation,

        // COLOR PALETTE:
        // Customize the container background, text label, and icon tint colors.
        backgroundColor = Color(0xFF00796B), // e.g., Material Teal
        textColor = Color.White,
        iconTint = Color(0xFFFFC107),        // e.g., Amber icon tint

        // CORNER RADIUS & SHAPE:
        // Define the resting corner radius and the morphed radius when pressed.
        cornerRadius = 24.dp,        // Rounded capsule shape
        pressedCornerRadius = 12.dp, // Morphs to sharper corners on tap

        // OUTLINE STROKE (BORDERS):
        // Add a contrasting outline stroke around the button bounds.
        strokeColor = Color(0xFF004D40),
        strokeWidth = 2.dp,

        // INTERACTIVE TOUCH PADDING:
        // Defines the secure clickable touch target boundary.
        // Coerced securely by the system between 4.dp and 8.dp.
        clickablePadding = PaddingValues(6.dp)
        */
    )
}
// [END android_permissions_private_alternatives_location_button_kotlin]
