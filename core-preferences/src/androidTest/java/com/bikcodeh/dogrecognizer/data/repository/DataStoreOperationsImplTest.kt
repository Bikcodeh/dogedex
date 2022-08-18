package com.bikcodeh.dogrecognizer.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.bikcodeh.dogrecognizer.core_preferences.data.repository.DataStoreOperationsImpl
import com.bikcodeh.dogrecognizer.core_preferences.domain.repository.DataStoreOperations
import com.bikcodeh.dogrecognizer.core_testing.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class DataStoreOperationsImplTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private lateinit var context: Context

    private lateinit var dataStoreOperations: DataStoreOperations

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        dataStoreOperations = DataStoreOperationsImpl(context)
    }

    @Test
    @SmallTest
    fun getUser_should_return_an_empty_user() = runTest {
        val result = dataStoreOperations.getUser().first()
        assertThat(result).isNotNull()
        assertThat(result.email).isEqualTo("")
        assertThat(result.authenticationToken).isEqualTo("")
        assertThat(result.id).isEqualTo(-1L)
    }

    @Test
    @SmallTest
    fun saveUser_should_save_an_user_with_all_its_params() = runTest {
        dataStoreOperations.saveUser(2L, "test@gmail.com", "token")
        val result = dataStoreOperations.getUser().first()
        assertThat(result).isNotNull()
        assertThat(result.email).isEqualTo("test@gmail.com")
        assertThat(result.authenticationToken).isEqualTo("token")
        assertThat(result.id).isEqualTo(2L)
    }

    @Test
    @SmallTest
    fun deleteUser_should_delete_the_previous_saved_user() = runTest {
        dataStoreOperations.saveUser(2L, "test@gmail.com", "token")
        val result = dataStoreOperations.getUser().first()
        assertThat(result).isNotNull()
        assertThat(result.email).isEqualTo("test@gmail.com")
        assertThat(result.authenticationToken).isEqualTo("token")
        assertThat(result.id).isEqualTo(2L)
        dataStoreOperations.deleteUser()
        val resultDelete = dataStoreOperations.getUser().first()
        assertThat(resultDelete).isNotNull()
        assertThat(resultDelete.email).isEqualTo("")
        assertThat(resultDelete.authenticationToken).isEqualTo("")
        assertThat(resultDelete.id).isEqualTo(-1L)
    }
}