package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.SampleData
import com.example.repository.LocalCreatorCollabRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Creator Collab Hub", appName)
    }

    @Test
    fun `verify sample creators repository loaded`() = runTest {
        val repository = LocalCreatorCollabRepository()
        val creators = repository.getCreators().first()
        assertTrue(creators.isNotEmpty())
        assertTrue(creators.all { it.isSample })
    }
}
