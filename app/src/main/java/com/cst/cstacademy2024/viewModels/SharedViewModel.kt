package com.cst.cstacademy2024.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cst.cstacademy2024.models.User

class SharedViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    fun setUser(user: User) {
        _user.value = user
    }
}
