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

#include <jni.h>
#include <unistd.h>
#include <cstdint>
#include <cstring>
#include <mutex>
#include <thread>
#include <vector>

// [START android_security_jni_bounds_cpp]
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_snippets_security_jni_SecureNativeDataProcessor_processPacketNative(
        JNIEnv* env,
        jclass /*clazz*/,
        jbyteArray packet,
        jint length) {
    constexpr jsize kMaxFrameSize = 2048;
    if (packet == nullptr) {
        jclass exClass = env->FindClass("java/lang/IllegalArgumentException");
        if (exClass != nullptr) {
            env->ThrowNew(exClass, "Packet array must not be null");
            env->DeleteLocalRef(exClass);
        }
        return -1;
    }

    jsize arrayLength = env->GetArrayLength(packet);
    if (length <= 0 || length > kMaxFrameSize || length > arrayLength) {
        jclass exClass = env->FindClass("java/lang/IllegalArgumentException");
        if (exClass != nullptr) {
            env->ThrowNew(exClass, "Packet length out of valid bounds");
            env->DeleteLocalRef(exClass);
        }
        return -1;
    }

    jbyte* elements = env->GetByteArrayElements(packet, nullptr);
    if (elements == nullptr) {
        return -1; // OutOfMemoryError already pending in JVM
    }

    uint8_t frameBuffer[kMaxFrameSize];
    std::memcpy(frameBuffer, elements, static_cast<size_t>(length));

    // Always release pinned array elements; JNI_ABORT avoids copying back read-only data
    env->ReleaseByteArrayElements(packet, elements, JNI_ABORT);

    uint32_t checksum = 0;
    for (jint i = 0; i < length; ++i) {
        checksum = (checksum * 31u) + static_cast<uint32_t>(frameBuffer[i]);
    }
    return static_cast<jint>(checksum);
}
// [END android_security_jni_bounds_cpp]

// [START android_security_jni_toctou_fd_cpp]
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_snippets_security_jni_SecureNativeFileLoader_readHeaderFromFdNative(
        JNIEnv* env,
        jobject /*thiz*/,
        jint fd) {
    if (fd < 0) {
        jclass exClass = env->FindClass("java/lang/IllegalArgumentException");
        if (exClass != nullptr) {
            env->ThrowNew(exClass, "Invalid file descriptor");
            env->DeleteLocalRef(exClass);
        }
        return nullptr;
    }

    char header[256];
    std::memset(header, 0, sizeof(header));

    // Read directly from the verified file descriptor rather than opening a path string
    ssize_t bytesRead = pread(fd, header, sizeof(header) - 1, 0);
    if (bytesRead < 0) {
        jclass ioExClass = env->FindClass("java/io/IOException");
        if (ioExClass != nullptr) {
            env->ThrowNew(ioExClass, "Failed to read header from descriptor");
            env->DeleteLocalRef(ioExClass);
        }
        return nullptr;
    }

    // Sanitize to printable 7-bit ASCII so NewStringUTF never receives invalid Modified UTF-8
    for (ssize_t i = 0; i < bytesRead; ++i) {
        unsigned char c = static_cast<unsigned char>(header[i]);
        if (c == 0 || c > 0x7f) {
            header[i] = '?';
        }
    }
    header[bytesRead] = '\0';
    return env->NewStringUTF(header);
}
// [END android_security_jni_toctou_fd_cpp]

// [START android_security_jni_keystore_cpp]
namespace {
void secureZeroMemory(void* ptr, size_t len) {
    volatile uint8_t* p = reinterpret_cast<volatile uint8_t*>(ptr);
    while (len-- > 0) {
        *p++ = 0;
    }
}
}  // namespace

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_snippets_security_jni_SecureNativeCryptoBridge_processCiphertextNative(
        JNIEnv* env,
        jclass /*clazz*/,
        jbyteArray ciphertext) {
    if (ciphertext == nullptr) {
        jclass exClass = env->FindClass("java/lang/IllegalArgumentException");
        if (exClass != nullptr) {
            env->ThrowNew(exClass, "Ciphertext array must not be null");
            env->DeleteLocalRef(exClass);
        }
        return nullptr;
    }

    jsize length = env->GetArrayLength(ciphertext);
    if (length <= 0) {
        jclass exClass = env->FindClass("java/lang/IllegalArgumentException");
        if (exClass != nullptr) {
            env->ThrowNew(exClass, "Ciphertext array must not be empty");
            env->DeleteLocalRef(exClass);
        }
        return nullptr;
    }

    jbyte* inputBytes = env->GetByteArrayElements(ciphertext, nullptr);
    if (inputBytes == nullptr) {
        return nullptr; // OutOfMemoryError pending
    }

    std::vector<jbyte> workingBuffer(inputBytes, inputBytes + length);
    // Release the pinned Java byte array immediately after copying
    env->ReleaseByteArrayElements(ciphertext, inputBytes, JNI_ABORT);

    jbyteArray result = env->NewByteArray(length);
    if (result != nullptr && !env->ExceptionCheck()) {
        env->SetByteArrayRegion(result, 0, length, workingBuffer.data());
    }

    // Zero transient native buffers before returning to managed code
    secureZeroMemory(workingBuffer.data(), workingBuffer.size());
    return result;
}
// [END android_security_jni_keystore_cpp]

// [START android_security_jni_thread_global_ref_cpp]
namespace {
JavaVM* gJavaVm = nullptr;
std::mutex gListenerMutex;
jobject gListenerGlobalRef = nullptr;
}  // namespace

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* /*reserved*/) {
    gJavaVm = vm;
    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_snippets_security_jni_SecureSessionMonitor_registerListener(
        JNIEnv* env,
        jobject /*thiz*/,
        jobject listener) {
    if (gJavaVm == nullptr) {
        env->GetJavaVM(&gJavaVm);
    }
    std::lock_guard<std::mutex> lock(gListenerMutex);
    if (gListenerGlobalRef != nullptr) {
        env->DeleteGlobalRef(gListenerGlobalRef);
        gListenerGlobalRef = nullptr;
    }
    if (listener != nullptr) {
        // Promote local jobject to a Global Reference so it remains valid across threads
        gListenerGlobalRef = env->NewGlobalRef(listener);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_snippets_security_jni_SecureSessionMonitor_dispatchBackgroundCheck(
        JNIEnv* /*env*/,
        jobject /*thiz*/) {
    std::thread worker([]() {
        if (gJavaVm == nullptr) {
            return;
        }
        JNIEnv* threadEnv = nullptr;
        if (gJavaVm->AttachCurrentThread(&threadEnv, nullptr) != JNI_OK ||
            threadEnv == nullptr) {
            return;
        }

        jobject localListener = nullptr;
        {
            std::lock_guard<std::mutex> lock(gListenerMutex);
            if (gListenerGlobalRef != nullptr) {
                localListener = threadEnv->NewLocalRef(gListenerGlobalRef);
            }
        }

        if (localListener != nullptr) {
            jclass listenerClass = threadEnv->GetObjectClass(localListener);
            if (listenerClass != nullptr && !threadEnv->ExceptionCheck()) {
                jmethodID methodId = threadEnv->GetMethodID(
                    listenerClass, "onStatusUpdate", "(I)V");
                if (methodId != nullptr && !threadEnv->ExceptionCheck()) {
                    threadEnv->CallVoidMethod(localListener, methodId, 1);
                }
                threadEnv->DeleteLocalRef(listenerClass);
            }
            threadEnv->DeleteLocalRef(localListener);
        }

        // Clear any pending JVM exception before detaching the native thread
        if (threadEnv->ExceptionCheck()) {
            threadEnv->ExceptionClear();
        }
        gJavaVm->DetachCurrentThread();
    });
    worker.detach();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_snippets_security_jni_SecureSessionMonitor_unregisterListener(
        JNIEnv* env,
        jobject /*thiz*/) {
    std::lock_guard<std::mutex> lock(gListenerMutex);
    if (gListenerGlobalRef != nullptr) {
        env->DeleteGlobalRef(gListenerGlobalRef);
        gListenerGlobalRef = nullptr;
    }
}
// [END android_security_jni_thread_global_ref_cpp]

// [START android_security_jni_exception_check_cpp]
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_snippets_security_jni_SecureConfigParser_validateOptions(
        JNIEnv* env,
        jclass /*clazz*/,
        jobject options) {
    if (options == nullptr) {
        return JNI_FALSE;
    }

    jclass optionsClass = env->GetObjectClass(options);
    if (optionsClass == nullptr || env->ExceptionCheck()) {
        return JNI_FALSE;
    }

    jmethodID buildHeaderMethod = env->GetMethodID(
        optionsClass, "buildAuthHeader", "()Ljava/lang/String;");
    if (buildHeaderMethod == nullptr || env->ExceptionCheck()) {
        env->DeleteLocalRef(optionsClass);
        return JNI_FALSE;
    }

    // Check ExceptionCheck() immediately after CallObjectMethod before any other JNI call
    jstring headerStr = static_cast<jstring>(
        env->CallObjectMethod(options, buildHeaderMethod));
    if (env->ExceptionCheck() || headerStr == nullptr) {
        env->DeleteLocalRef(optionsClass);
        return JNI_FALSE;
    }

    const char* headerChars = env->GetStringUTFChars(headerStr, nullptr);
    if (headerChars == nullptr || env->ExceptionCheck()) {
        env->DeleteLocalRef(headerStr);
        env->DeleteLocalRef(optionsClass);
        return JNI_FALSE;
    }
    bool validHeader = headerChars[0] != '\0';
    env->ReleaseStringUTFChars(headerStr, headerChars);
    env->DeleteLocalRef(headerStr);

    jfieldID hostField = env->GetFieldID(
        optionsClass, "host", "Ljava/lang/String;");
    if (hostField == nullptr || env->ExceptionCheck()) {
        env->DeleteLocalRef(optionsClass);
        return JNI_FALSE;
    }

    jfieldID accountField = env->GetFieldID(
        optionsClass, "accountId", "Ljava/lang/String;");
    if (accountField == nullptr || env->ExceptionCheck()) {
        env->DeleteLocalRef(optionsClass);
        return JNI_FALSE;
    }

    jstring hostStr = static_cast<jstring>(
        env->GetObjectField(options, hostField));
    if (env->ExceptionCheck() || hostStr == nullptr) {
        env->DeleteLocalRef(optionsClass);
        return JNI_FALSE;
    }

    jstring accountStr = static_cast<jstring>(
        env->GetObjectField(options, accountField));
    if (env->ExceptionCheck() || accountStr == nullptr) {
        env->DeleteLocalRef(hostStr);
        env->DeleteLocalRef(optionsClass);
        return JNI_FALSE;
    }

    const char* hostChars = env->GetStringUTFChars(hostStr, nullptr);
    if (hostChars == nullptr || env->ExceptionCheck()) {
        env->DeleteLocalRef(accountStr);
        env->DeleteLocalRef(hostStr);
        env->DeleteLocalRef(optionsClass);
        return JNI_FALSE;
    }

    const char* accountChars = env->GetStringUTFChars(accountStr, nullptr);
    if (accountChars == nullptr || env->ExceptionCheck()) {
        env->ReleaseStringUTFChars(hostStr, hostChars);
        env->DeleteLocalRef(accountStr);
        env->DeleteLocalRef(hostStr);
        env->DeleteLocalRef(optionsClass);
        return JNI_FALSE;
    }

    bool nonEmpty = validHeader && hostChars[0] != '\0' && accountChars[0] != '\0';

    env->ReleaseStringUTFChars(hostStr, hostChars);
    env->ReleaseStringUTFChars(accountStr, accountChars);
    env->DeleteLocalRef(accountStr);
    env->DeleteLocalRef(hostStr);
    env->DeleteLocalRef(optionsClass);
    return nonEmpty ? JNI_TRUE : JNI_FALSE;
}
// [END android_security_jni_exception_check_cpp]
