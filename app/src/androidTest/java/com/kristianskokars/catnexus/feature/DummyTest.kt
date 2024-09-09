package com.kristianskokars.catnexus.feature

import com.kristianskokars.catnexus.core.CatNexusAndroidTest
import dagger.hilt.android.testing.HiltAndroidTest
import io.kotest.matchers.shouldBe
import org.junit.Test

@HiltAndroidTest
class DummyTest : CatNexusAndroidTest() {
    @Test
    fun dummyTest() {
        println("test")

        true shouldBe true
    }
}