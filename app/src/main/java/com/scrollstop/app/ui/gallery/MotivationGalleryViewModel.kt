package com.scrollstop.app.ui.gallery

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scrollstop.app.ScrollStopApp
import com.scrollstop.app.data.local.entity.MotivationalItemEntity
import com.scrollstop.app.data.preferences.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MotivationGalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as ScrollStopApp
    private val motivationRepo = app.motivationRepository
    private val prefsRepo = app.userPreferencesRepository

    val motivationalItems: StateFlow<List<MotivationalItemEntity>> = motivationRepo.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userPreferences: StateFlow<UserPreferences> = prefsRepo.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun addCustomMotivation(imageUri: Uri?, quote: String, author: String) {
        viewModelScope.launch {
            motivationRepo.addCustomMotivation(imageUri, quote, author)
        }
    }

    fun setActiveItem(id: Long) {
        viewModelScope.launch {
            motivationRepo.setActiveItem(id)
        }
    }

    fun deleteItem(item: MotivationalItemEntity) {
        viewModelScope.launch {
            motivationRepo.deleteItem(item)
        }
    }

    fun toggleRandomMode(useRandom: Boolean) {
        viewModelScope.launch {
            prefsRepo.setUseRandomMotivation(useRandom)
        }
    }
}
