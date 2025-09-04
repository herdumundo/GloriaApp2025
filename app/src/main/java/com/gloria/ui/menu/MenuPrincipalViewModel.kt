package com.gloria.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.entity.LoggedUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuPrincipalViewModel @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository
) : ViewModel() {
    
    private val _loggedUser = MutableStateFlow<LoggedUser?>(null)
    val loggedUser: StateFlow<LoggedUser?> = _loggedUser.asStateFlow()
    
    init {
        loadLoggedUser()
    }
    
    private fun loadLoggedUser() {
        viewModelScope.launch {
            loggedUserRepository.getLoggedUser().collect { user ->
                _loggedUser.value = user
            }
        }
    }
    
    fun getLoggedUserSync(): LoggedUser? {
        return _loggedUser.value
    }
}
