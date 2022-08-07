package ca.sfu.BlueRadar.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.sfu.BlueRadar.services.BluetoothService
import ca.sfu.BlueRadar.services.DatabaseService

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "No active tracking devices available..."
    }
    val text: LiveData<String> = _text

    fun checkText(){

    }
}