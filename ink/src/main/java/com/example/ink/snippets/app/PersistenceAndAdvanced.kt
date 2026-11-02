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

package com.example.ink.snippets.app

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputEventHandler
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withSave
import androidx.ink.brush.Brush
import androidx.ink.brush.BrushFamily
import androidx.ink.brush.StockBrushes
import androidx.ink.brush.TextureBitmapStore
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.storage.decode
import androidx.ink.storage.encode
import androidx.ink.strokes.Stroke
import androidx.ink.strokes.StrokeInputBatch
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// [START android_compose_stylus_ink_app_serialize_deserialize_stroke]
@Serializable
data class SerializedStroke(
    val inputs: ByteArray,
    val brush: SerializedBrush,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SerializedStroke) return false
        return inputs.contentEquals(other.inputs) && brush == other.brush
    }

    override fun hashCode(): Int = 31 * inputs.contentHashCode() + brush.hashCode()
}

@Serializable
data class SerializedBrush(
    val size: Float,
    val color: Long,
    val epsilon: Float,
    val stockBrush: SerializedStockBrush,
    val clientBrushFamilyId: String? = null,
)

@Serializable
enum class SerializedStockBrush {
    MarkerLatest,
    PressurePenLatest,
    HighlighterLatest,
    DashedLineLatest,
}

private val stockBrushToEnum = mapOf(
    StockBrushes.marker() to SerializedStockBrush.MarkerLatest,
    StockBrushes.pressurePen() to SerializedStockBrush.PressurePenLatest,
    StockBrushes.highlighter() to SerializedStockBrush.HighlighterLatest,
    StockBrushes.dashedLine() to SerializedStockBrush.DashedLineLatest,
)

private val enumToStockBrush = stockBrushToEnum.entries.associate { (k, v) -> v to k }

fun serializeStroke(
    stroke: Stroke,
    customBrushes: Map<String, BrushFamily> = emptyMap(),
): String {
    val encodedInputs = ByteArrayOutputStream().use { outputStream ->
        stroke.inputs.encode(outputStream)
        outputStream.toByteArray()
    }
    val customBrushId = customBrushes.entries.firstOrNull { it.value == stroke.brush.family }?.key
    val serializedBrush = SerializedBrush(
        size = stroke.brush.size,
        color = stroke.brush.colorLong,
        epsilon = stroke.brush.epsilon,
        stockBrush = stockBrushToEnum[stroke.brush.family] ?: SerializedStockBrush.MarkerLatest,
        clientBrushFamilyId = customBrushId,
    )
    return Json.encodeToString(SerializedStroke(inputs = encodedInputs, brush = serializedBrush))
}

fun deserializeStroke(
    data: String,
    customBrushes: Map<String, BrushFamily> = emptyMap(),
): Stroke? {
    return try {
        val serialized = Json.decodeFromString<SerializedStroke>(data)
        val inputs = ByteArrayInputStream(serialized.inputs).use { inputStream ->
            StrokeInputBatch.decode(inputStream)
        }
        val family = serialized.brush.clientBrushFamilyId?.let { customBrushes[it] }
            ?: enumToStockBrush[serialized.brush.stockBrush]
            ?: StockBrushes.marker()
        val brush = Brush.createWithColorLong(
            family = family,
            colorLong = serialized.brush.color,
            size = serialized.brush.size,
            epsilon = serialized.brush.epsilon,
        )
        Stroke(brush = brush, inputs = inputs)
    } catch (e: Exception) {
        null
    }
}
// [END android_compose_stylus_ink_app_serialize_deserialize_stroke]

// [START android_compose_stylus_ink_app_room_database_repository]
@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "strokes_data")
    val strokesData: List<String> = emptyList(),
)

class RoomConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? = list?.let { json.encodeToString(it) }

    @TypeConverter
    fun toStringList(jsonString: String?): List<String> {
        if (jsonString.isNullOrEmpty()) return emptyList()
        return try {
            json.decodeFromString<List<String>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getById(id: Long): DocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DocumentEntity)
}

@Database(entities = [DocumentEntity::class], version = 1)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
}

class StrokeRepositoryImpl(
    private val dao: DocumentDao,
) : StrokeRepository {
    override suspend fun saveStrokes(documentId: Long, strokes: List<Stroke>) {
        withContext(Dispatchers.IO) {
            val serializedList = strokes.map { serializeStroke(it) }
            dao.upsert(DocumentEntity(id = documentId, strokesData = serializedList))
        }
    }

    override suspend fun loadStrokes(documentId: Long): List<Stroke> =
        withContext(Dispatchers.IO) {
            val doc = dao.getById(documentId) ?: return@withContext emptyList()
            doc.strokesData.mapNotNull { deserializeStroke(it) }
        }
}
// [END android_compose_stylus_ink_app_room_database_repository]

// [START android_compose_stylus_ink_app_export_drawing_to_png]
suspend fun exportDrawingToPng(
    width: Int,
    height: Int,
    strokes: List<Stroke>,
    textureStore: TextureBitmapStore,
    outputFile: File,
    backgroundColor: Int = android.graphics.Color.WHITE,
): Uri = withContext(Dispatchers.IO) {
    val exportBitmap = createBitmap(width, height)
    val canvas = Canvas(exportBitmap)
    canvas.drawColor(backgroundColor)

    val renderer = CanvasStrokeRenderer.create(textureStore = textureStore)
    strokes.forEach { stroke ->
        canvas.withSave {
            renderer.draw(
                canvas = this,
                stroke = stroke,
                strokeToScreenTransform = Matrix()
            )
        }
    }

    FileOutputStream(outputFile).use { output ->
        exportBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
    }
    Uri.fromFile(outputFile)
}
// [END android_compose_stylus_ink_app_export_drawing_to_png]

// [START android_compose_stylus_ink_app_pointer_input_sibling_fallthrough]
internal fun Modifier.pointerInputWithSiblingFallthrough(
    pointerInputEventHandler: PointerInputEventHandler
) = this then PointerInputSiblingFallthroughElement(pointerInputEventHandler)

private class PointerInputSiblingFallthroughModifierNode(
    pointerInputEventHandler: PointerInputEventHandler
) : PointerInputModifierNode, DelegatingNode() {

    var pointerInputEventHandler: PointerInputEventHandler
        get() = delegateNode.pointerInputEventHandler
        set(value) {
            delegateNode.pointerInputEventHandler = value
        }

    val delegateNode = delegate(
        SuspendingPointerInputModifierNode(pointerInputEventHandler)
    )

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        delegateNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        delegateNode.onCancelPointerInput()
    }

    override fun sharePointerInputWithSiblings() = true
}

private data class PointerInputSiblingFallthroughElement(
    val pointerInputEventHandler: PointerInputEventHandler
) : ModifierNodeElement<PointerInputSiblingFallthroughModifierNode>() {

    override fun create() =
        PointerInputSiblingFallthroughModifierNode(pointerInputEventHandler)

    override fun update(node: PointerInputSiblingFallthroughModifierNode) {
        node.pointerInputEventHandler = pointerInputEventHandler
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "pointerInputWithSiblingFallthrough"
        properties["pointerInputEventHandler"] = pointerInputEventHandler
    }
}
// [END android_compose_stylus_ink_app_pointer_input_sibling_fallthrough]
