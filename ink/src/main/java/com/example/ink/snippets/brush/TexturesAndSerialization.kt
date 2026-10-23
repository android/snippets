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

package com.example.ink.snippets.brush

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.ink.brush.BrushFamily
import androidx.ink.brush.BrushPaint
import androidx.ink.brush.BrushTip
import androidx.ink.brush.SelfOverlap
import androidx.ink.brush.TextureBitmapStore
import androidx.ink.brush.Version
import androidx.ink.storage.BrushFamilyDecodeCallback
import androidx.ink.storage.decode
import androidx.ink.storage.encode
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ink.snippets.R
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

// [START android_compose_stylus_ink_brush_texture_bitmap_store]
class CustomBrushTextureStore(context: Context) : TextureBitmapStore {
    private val resources = context.resources
    private val textureResources: Map<String, Int> = mapOf(
        "pencil-grain" to R.drawable.pencil_grain,
        "watercolor-wash" to R.drawable.watercolor_wash,
    )
    private val loadedBitmaps = mutableMapOf<String, Bitmap>()

    private val _generation = MutableStateFlow(0)
    val generation: StateFlow<Int> = _generation.asStateFlow()

    override operator fun get(clientTextureId: String): Bitmap? {
        return loadedBitmaps.getOrPut(clientTextureId) {
            textureResources[clientTextureId]?.let { resId ->
                BitmapFactory.decodeResource(resources, resId)
            } ?: return null
        }
    }

    fun registerBitmap(textureId: String, bitmap: Bitmap) {
        loadedBitmaps[textureId] = bitmap
        _generation.update { it + 1 }
    }
}
// [END android_compose_stylus_ink_brush_texture_bitmap_store]

// [START android_compose_stylus_ink_brush_tiling_texture_layer]
fun createTexturedBrushFamily(): BrushFamily {
    val grainPaint = BrushPaint(
        selfOverlap = SelfOverlap.ANY,
        textureLayers = listOf(
            BrushPaint.TilingTexture(
                clientTextureId = "pencil-grain",
                sizeX = 1.0f,
                sizeY = 1.0f,
                sizeUnit = BrushPaint.TextureLayer.SizeUnit.BRUSH_SIZE,
                blendMode = BrushPaint.TextureLayer.BlendMode.MODULATE,
            ),
        ),
        colorFunctions = listOf(BrushPaint.ColorFunction.OpacityMultiplier(0.85f)),
    )

    return BrushFamily(
        tip = BrushTip(scaleX = 1f, scaleY = 1f, cornerRounding = 1f),
        paint = grainPaint,
    )
}
// [END android_compose_stylus_ink_brush_tiling_texture_layer]

// [START android_compose_stylus_ink_brush_save_load_file]
suspend fun saveCustomBrushToFile(
    context: Context,
    uri: Uri,
    brushFamily: BrushFamily,
    textureBitmapStore: TextureBitmapStore,
) = withContext(Dispatchers.IO) {
    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
        brushFamily.encode(outputStream, textureBitmapStore)
    }
}

suspend fun loadCustomBrushFromFile(
    inputStream: InputStream,
    textureStore: CustomBrushTextureStore,
    allowDevelopmentFeatures: Boolean = false,
): BrushFamily = withContext(Dispatchers.IO) {
    val maxVersion = if (allowDevelopmentFeatures) {
        Version.DEVELOPMENT
    } else {
        Version.MAX_SUPPORTED
    }
    inputStream.use { stream ->
        BrushFamily.decode(
            input = stream,
            maxVersion = maxVersion,
            getClientTextureId = BrushFamilyDecodeCallback { textureId, bitmap ->
                if (bitmap != null) {
                    textureStore.registerBitmap(textureId, bitmap)
                }
                textureId
            },
        )
    }
}
// [END android_compose_stylus_ink_brush_save_load_file]

// [START android_compose_stylus_ink_brush_room_entity_blob]
@Entity(tableName = "custom_brushes")
data class CustomBrushEntity(
    @PrimaryKey val name: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val brushBytes: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CustomBrushEntity) return false
        return name == other.name && brushBytes.contentEquals(other.brushBytes)
    }

    override fun hashCode(): Int = 31 * name.hashCode() + brushBytes.contentHashCode()
}

fun encodeBrushForDatabase(
    brushFamily: BrushFamily,
    textureBitmapStore: TextureBitmapStore = TextureBitmapStore { null },
): ByteArray {
    return ByteArrayOutputStream().use { stream ->
        brushFamily.encode(stream, textureBitmapStore)
        stream.toByteArray()
    }
}
// [END android_compose_stylus_ink_brush_room_entity_blob]
