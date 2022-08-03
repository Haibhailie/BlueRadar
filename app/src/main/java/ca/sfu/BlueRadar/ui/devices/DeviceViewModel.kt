package ca.sfu.BlueRadar.ui.devices

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import ca.sfu.BlueRadar.ui.devices.data.Device
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

/**
 *
 * Exercise Entry view model connects application the database with DAO
 *
 */
class DeviceViewModel(private val deviceDatabaseDao: DeviceDatabaseDao) : ViewModel() {
    val allEntriesLiveData: LiveData<List<Device>> = deviceDatabaseDao.getAllEntries()
        .asLiveData()

    val activeEntriesLiveData: LiveData<List<Device>> =
        deviceDatabaseDao.getAllActiveEntries(true).asLiveData()

    val inactiveEntriesLiveData: LiveData<List<Device>> =
        deviceDatabaseDao.getAllInactiveEntries(false).asLiveData()

    fun insert(entry: Device) {
        CoroutineScope(Dispatchers.IO).launch {
            deviceDatabaseDao.insertEntry(entry)
        }
    }

    fun delete(id: Long) {
        if (allEntriesLiveData.value!!.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                deviceDatabaseDao.deleteEntry(id)
            }
        }
    }

    fun deleteAll() {
        CoroutineScope(Dispatchers.IO).launch {
            deviceDatabaseDao.deleteAll()
        }
    }

    fun update(device: Device) {
        CoroutineScope(Dispatchers.IO).launch {
            deviceDatabaseDao.update(device)
        }
    }

}

class DeviceViewModelFactory(private val repository: DeviceDatabaseDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T { //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if (modelClass.isAssignableFrom(DeviceViewModel::class.java))
            return DeviceViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}