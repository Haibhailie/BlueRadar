package ca.sfu.BlueRadar.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "BlueRadar But Orange app Woop Woop"
    }
    val text: LiveData<String> = _text
}