/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.compose.snippets.images

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.compose.snippets.R

/*
* Copyright 2022 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
// Snippets for https://developer.android.com/jetpack/compose/graphics/images/loading
@Preview
@Composable
fun LoadingImageFromDisk() {
    // [START android_compose_images_load_disk]
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description)
    )
    // [END android_compose_images_load_disk]
}

@Preview
@Composable
fun LoadingImageFromInternetCoil() {
    // [START android_compose_images_load_internet_coil]
    AsyncImage(
        model = "https://example.com/image.jpg",
        contentDescription = "Translated description of what the image contains"
    )
    // [END android_compose_images_load_internet_coil]
}

@OptIn(ExperimentalGlideComposeApi::class)
@Preview
@Composable
fun LoadingImageFromInternetGlide() {
    // [START android_compose_images_load_internet_glide]
    GlideImage(
        model = "https://example.com/image.jpg",
        contentDescription = "Translated description of what the image contains"
    )
    // [END android_compose_images_load_internet_glide]
}
