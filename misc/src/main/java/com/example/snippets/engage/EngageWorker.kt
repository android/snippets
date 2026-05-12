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

package com.example.snippets.engage

// [START android_engage_worker_implementation]
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.engage.service.AppEngageErrorCode
import com.google.android.engage.service.AppEngageException
import com.google.android.engage.service.AppEngagePublishClient
import com.google.android.engage.service.AppEngagePublishStatusCode
import com.google.android.engage.service.PublishStatusRequest
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await

// [START_EXCLUDE silent]
@SuppressWarnings("unused")
// [END_EXCLUDE]
class EngageWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    // Replace {AppEngagePublishClient} with the "client" class found in references/schemas/{VERTICAL}.md.
    // Client class can vary based on app's vertical.
    // Refer to the references/schemas/{VERTICAL}.md to find the right class.
    // This is an example of using AppEngagePublishClient.
    private val client = AppEngagePublishClient(context)
    private val clusterRequestFactory = ClusterRequestFactory(context)

    override suspend fun doWork(): Result {
        if (runAttemptCount > Constants.MAX_PUBLISHING_ATTEMPTS) {
            // If we keep failing, report it as a service error before giving up.
            updatePublishStatus(AppEngagePublishStatusCode.NOT_PUBLISHED_SERVICE_ERROR)
            return Result.failure()
        }

        // Check if engage service is available before publishing.
        val isAvailable = client.isServiceAvailable.await()

        // If the service is not available, do not attempt to publish and indicate failure.
        if (!isAvailable) {
            return Result.failure()
        }

        val publishType = inputData.getString(Constants.PUBLISH_TYPE_KEY)
        return when (publishType) {
            Constants.PUBLISH_TYPE_RECOMMENDATIONS -> publishRecommendations()
            // Constants.PUBLISH_TYPE_FEATURED -> publishFeatured()
            // Constants.PUBLISH_TYPE_CONTINUATION-> publishContinuation()
            Constants.PUBLISH_TYPE_USER_ACCOUNT_MANAGEMENT -> publishUserAccountManagement()
            else -> Result.failure()
        }
    }

    // Use similar patterns for other clusters (Featured, Continuation, FoodShoppingList, Reservation etc.)
    private suspend fun publishRecommendations(): Result {
        val publishTask: Task<Void> =
            client.publishRecommendationClusters(
                clusterRequestFactory.constructRecommendationClustersRequest()
            )
        return publishAndProvideResult(publishTask)
    }

    private suspend fun publishUserAccountManagement(): Result {
        val publishTask: Task<Void>
        if (isAccountSignedIn()) {
            // If signed in, we delete the sign-in card.
            publishTask = client.deleteUserManagementCluster()
            return publishAndProvideResult(publishTask)
        } else {
            // If not signed in, we publish the sign-in card.
            // Note: Even though we are publishing a card, the status code is NOT_PUBLISHED_REQUIRES_SIGN_IN
            // because the actual content (recommendations/continuation) is not published.
            publishTask =
                client.publishUserAccountManagementRequest(
                    clusterRequestFactory.constructUserAccountManagementClusterRequest()
                )
            return try {
                publishTask.await()
                updatePublishStatus(AppEngagePublishStatusCode.NOT_PUBLISHED_REQUIRES_SIGN_IN)
                Result.success()
            } catch (publishException: Exception) {
                handlePublishException(publishException)
            }
        }
    }

    private fun isAccountSignedIn(): Boolean {
        // Implement your app's sign-in check logic here.
        // [START_EXCLUDE]
        return true
        // [END_EXCLUDE]
    }

    private suspend fun publishAndProvideResult(
        publishTask: Task<Void>
    ): Result {
        return try {
            // An AppEngageException may occur while publishing, so we may not be able to await the result.
            publishTask.await()
            // Update status to PUBLISHED only after successful publication.
            updatePublishStatus(AppEngagePublishStatusCode.PUBLISHED)
            Result.success()
        } catch (publishException: Exception) {
            handlePublishException(publishException)
        }
    }

    private fun handlePublishException(publishException: Exception): Result {
        logPublishing(publishException as AppEngageException)

        // Map AppEngageException error codes to PublishStatusCodes
        val errorStatusCode = when (publishException.errorCode) {
            AppEngageErrorCode.SERVICE_CALL_INVALID_ARGUMENT ->
                AppEngagePublishStatusCode.NOT_PUBLISHED_CLIENT_ERROR
            AppEngageErrorCode.SERVICE_CALL_PERMISSION_DENIED ->
                AppEngagePublishStatusCode.NOT_PUBLISHED_CLIENT_ERROR
            else ->
                AppEngagePublishStatusCode.NOT_PUBLISHED_SERVICE_ERROR
        }
        updatePublishStatus(errorStatusCode)

        // Some errors are recoverable, such as a threading issue, some are unrecoverable
        // such as a cluster not containing all necessary fields. If an error is recoverable, we
        // should attempt to publish again. Setting the result to retry means WorkManager will
        // attempt to run the worker again, thus attempting to publish again.
        return if (isErrorRecoverable(publishException)) Result.retry() else Result.failure()
    }

    private fun updatePublishStatus(statusCode: Int) {
        client
            .updatePublishStatus(PublishStatusRequest.Builder().setStatusCode(statusCode).build())
            .addOnSuccessListener {
                Log.i(TAG, "Successfully updated publish status code to $statusCode")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to update publish status code to $statusCode\n${exception.stackTrace}")
            }
    }

    private fun logPublishing(exception: AppEngageException) {
        val message = when (exception.errorCode) {
            AppEngageErrorCode.SERVICE_NOT_FOUND -> "Service not found"
            AppEngageErrorCode.SERVICE_CALL_EXECUTION_FAILURE -> "Execution failure"
            AppEngageErrorCode.SERVICE_NOT_AVAILABLE -> "Service not available"
            AppEngageErrorCode.SERVICE_CALL_PERMISSION_DENIED -> "Permission denied"
            AppEngageErrorCode.SERVICE_CALL_INVALID_ARGUMENT -> "Invalid argument"
            AppEngageErrorCode.SERVICE_CALL_INTERNAL -> "Internal error"
            AppEngageErrorCode.SERVICE_CALL_RESOURCE_EXHAUSTED -> "Resource exhausted"
            else -> "Unknown error"
        }
        Log.d(TAG, message)
    }

    private fun isErrorRecoverable(publishingException: AppEngageException): Boolean {
        return when (publishingException.errorCode) {
            // Recoverable Error codes
            AppEngageErrorCode.SERVICE_CALL_EXECUTION_FAILURE,
            AppEngageErrorCode.SERVICE_CALL_INTERNAL,
            AppEngageErrorCode.SERVICE_CALL_RESOURCE_EXHAUSTED -> true
            // Non recoverable error codes
            AppEngageErrorCode.SERVICE_NOT_FOUND,
            AppEngageErrorCode.SERVICE_CALL_INVALID_ARGUMENT,
            AppEngageErrorCode.SERVICE_CALL_PERMISSION_DENIED,
            AppEngageErrorCode.SERVICE_NOT_AVAILABLE -> false
            else -> throw IllegalArgumentException(publishingException.localizedMessage)
        }
    }
}
// [END android_engage_worker_implementation]
