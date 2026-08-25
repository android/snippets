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

package com.example.android.basics

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.DataClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.parcelize.TypeParceler
import kotlinx.parcelize.WriteWith
import kotlinx.parcelize.parcelableCreator

private object BasicSnippet {
    // [START android_kotlin_parcelize_basic]
    // import kotlinx.parcelize.Parcelize

    @Parcelize
    class User(val firstName: String, val lastName: String, val age: Int) : Parcelable
    // [END android_kotlin_parcelize_basic]
}

private object CompanionParcelerSnippet {
    // [START android_kotlin_parcelize_companion_parceler]
    @Parcelize
    data class User(val firstName: String, val lastName: String, val age: Int) : Parcelable {
        private companion object : Parceler<User> {
            override fun User.write(parcel: Parcel, flags: Int) {
                // Custom write implementation
            }

            override fun create(parcel: Parcel): User {
                // Custom read implementation
                // [START_EXCLUDE silent]
                return User("", "", 0)
                // [END_EXCLUDE]
            }
        }
    }
    // [END android_kotlin_parcelize_companion_parceler]
}

private object ExternalClassParcelerSnippet {
    // [START android_kotlin_parcelize_external_class_parceler]
    class ExternalClass(val value: Int)

    object ExternalClassParceler : Parceler<ExternalClass> {
        override fun create(parcel: Parcel) = ExternalClass(parcel.readInt())

        override fun ExternalClass.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(value)
        }
    }
    // [END android_kotlin_parcelize_external_class_parceler]
}

private object ClassLocalParcelerSnippet {
    class ExternalClass(val value: Int)

    object ExternalClassParceler : Parceler<ExternalClass> {
        override fun create(parcel: Parcel) = ExternalClass(parcel.readInt())

        override fun ExternalClass.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(value)
        }
    }

    // [START android_kotlin_parcelize_type_parceler_class]
    // Class-local parceler
    @Parcelize
    @TypeParceler<ExternalClass, ExternalClassParceler>()
    class MyClass(val external: ExternalClass) : Parcelable
    // [END android_kotlin_parcelize_type_parceler_class]
}

private object PropertyLocalParcelerSnippet {
    class ExternalClass(val value: Int)

    object ExternalClassParceler : Parceler<ExternalClass> {
        override fun create(parcel: Parcel) = ExternalClass(parcel.readInt())

        override fun ExternalClass.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(value)
        }
    }

    // [START android_kotlin_parcelize_type_parceler_property]
    // Property-local parceler
    @Parcelize
    class MyClass(@TypeParceler<ExternalClass, ExternalClassParceler>() val external: ExternalClass) : Parcelable
    // [END android_kotlin_parcelize_type_parceler_property]
}

private object TypeLocalParcelerSnippet {
    class ExternalClass(val value: Int)

    object ExternalClassParceler : Parceler<ExternalClass> {
        override fun create(parcel: Parcel) = ExternalClass(parcel.readInt())

        override fun ExternalClass.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(value)
        }
    }

    // [START android_kotlin_parcelize_type_parceler_type]
    // Type-local parceler
    @Parcelize
    class MyClass(val external: @WriteWith<ExternalClassParceler>() ExternalClass) : Parcelable
    // [END android_kotlin_parcelize_type_parceler_type]
}

private object ParcelableCreatorSnippet {
    @Parcelize
    class User(val firstName: String, val lastName: String, val age: Int) : Parcelable

    // [START android_kotlin_parcelize_parcelable_creator]
    // import kotlinx.parcelize.parcelableCreator

    fun userFromParcel(parcel: Parcel): User {
        return parcelableCreator<User>().createFromParcel(parcel)
    }
    // [END android_kotlin_parcelize_parcelable_creator]
}

private object IgnoredOnParcelSnippet {
    // [START android_kotlin_parcelize_ignored_on_parcel]
    @Parcelize
    class MyClass(
        val include: String,
        // Don't serialize this property
        @IgnoredOnParcel val ignore: String = "default"
    ) : Parcelable {
        // Silence a warning
        @IgnoredOnParcel
        val computed: String = include + ignore
    }
    // [END android_kotlin_parcelize_ignored_on_parcel]
}

private object RawValueSnippet {
    class ExternalClass(val value: Int)

    // [START android_kotlin_parcelize_raw_value]
    @Parcelize
    class MyClass(val external: @RawValue ExternalClass) : Parcelable
    // [END android_kotlin_parcelize_raw_value]
}

private object SealedClassSnippet {
    // [START android_kotlin_parcelize_sealed_class]
    @Parcelize
    sealed class SealedClass : Parcelable {
        class A(val a: String) : SealedClass()
        class B(val b: Int) : SealedClass()
    }

    @Parcelize
    class MyClass(val a: SealedClass.A, val b: SealedClass.B, val c: SealedClass) : Parcelable
    // [END android_kotlin_parcelize_sealed_class]
}

private object DataWrapperSnippet {
    // [START android_kotlin_parcelize_data_class_wrapper]
    // Common code:
    data class MyData(val x: String, val y: MoreData)
    data class MoreData(val a: String, val b: Int)

    // Platform code:
    @OptIn(kotlinx.parcelize.Experimental::class)
    @Parcelize
    class DataWrapper(val wrapped: @DataClass MyData) : Parcelable
    // [END android_kotlin_parcelize_data_class_wrapper]
}

private object InheritanceBaseSnippet {
    // [START android_kotlin_parcelize_inheritance_base]
    // base parcelize
    @Parcelize
    open class Base(open val s: String) : Parcelable

    @Parcelize
    class Derived(
        val x: Int,
        // all arguments have to be `val` or `var` so we need to override
        // to not introduce new property name
        override val s: String
    ) : Base(s)
    // [END android_kotlin_parcelize_inheritance_base]
}