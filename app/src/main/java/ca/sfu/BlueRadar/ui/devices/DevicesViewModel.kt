package ca.sfu.BlueRadar.ui.devices

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DevicesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is where we display the selected devices"
    }
    val text: LiveData<String> = _text
}