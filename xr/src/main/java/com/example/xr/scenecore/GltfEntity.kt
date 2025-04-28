package com.example.xr.scenecore

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.xr.scenecore.GltfModel
import androidx.xr.scenecore.GltfModelEntity
import androidx.xr.scenecore.Session
import androidx.xr.scenecore.SpatialCapabilities
import androidx.xr.scenecore.getSpatialCapabilities
import kotlinx.coroutines.guava.await

private suspend fun loadGltfFile(session: Session) {
    // [START androidxr_scenecore_gltfmodel_create]
    val gltfModel = GltfModel.create(session, "models/saturn_rings.glb").await()
    // [END androidxr_scenecore_gltfmodel_create]
}

private fun createModelEntity(session: Session, gltfModel: GltfModel) {
    // [START androidxr_scenecore_gltfmodelentity_create]
    if (session.getSpatialCapabilities()
            .hasCapability(SpatialCapabilities.SPATIAL_CAPABILITY_3D_CONTENT)
    ) {
        val gltfEntity = GltfModelEntity.create(session, gltfModel)
    }
    // [END androidxr_scenecore_gltfmodelentity_create]
}

private fun animateEntity(gltfEntity: GltfModelEntity) {
    // [START androidxr_scenecore_gltfmodelentity_animation]
    gltfEntity.startAnimation(loop = true, animationName = "Walk")
    // [END androidxr_scenecore_gltfmodelentity_animation]
}

private fun ComponentActivity.startSceneViewer() {
    // [START androidxr_scenecore_sceneviewer]
    val url =
        "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/FlightHelmet/glTF/FlightHelmet.gltf"
    val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
    val intentUri =
        Uri.parse("https://arvr.google.com/scene-viewer/1.2")
            .buildUpon()
            .appendQueryParameter("file", url)
            .build()
    sceneViewerIntent.setDataAndType(intentUri, "model/gltf-binary")
    startActivity(sceneViewerIntent)
    // [END androidxr_scenecore_sceneviewer]
}