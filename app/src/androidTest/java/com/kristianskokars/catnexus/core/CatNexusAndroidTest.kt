package com.kristianskokars.catnexus.core

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.kristianskokars.catnexus.core.data.data_source.local.CatDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

abstract class CatNexusAndroidTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var db: CatDatabase

    protected lateinit var context: Context

    @Before
    open fun setup() {
        context = ApplicationProvider.getApplicationContext()
        hiltRule.inject()
        db.clearAllTables()
    }

    @After
    open fun teardown() {
        db.clearAllTables()
    }
}
