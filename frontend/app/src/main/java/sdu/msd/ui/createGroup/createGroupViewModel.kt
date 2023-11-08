package sdu.msd.ui.createGroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class reateGroupViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Fragment"
    }
    val text: LiveData<String> = _text
}