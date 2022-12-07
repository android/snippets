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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
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
@Preview
@Composable
fun ImageBitmapSnippets() {
    // [START android_compose_images_bitmap_load]
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description)
    )
    // [END android_compose_images_bitmap_load]

    // [START android_compose_images_bitmap_simple]
    val imageBitmap = ImageBitmap.imageResource(R.drawable.dog)
    // [END android_compose_images_bitmap_simple]
}

@Preview
@Composable
fun ImageVectorSnippet() {
    // [START android_compose_images_vector_load]
    Image(
        painter = painterResource(id = R.drawable.baseline_shopping_cart_24),
        contentDescription = stringResource(id = R.string.shopping_cart_content_desc)
    )
    // [END android_compose_images_vector_load]

    // [START android_compose_images_vector_simple]
    val imageVector = ImageVector.vectorResource(id = R.drawable.baseline_shopping_cart_24)
    // [END android_compose_images_vector_simple]
}
