package ca.sfu.BlueRadar.ui.devices.data

import com.google.android.gms.maps.model.LatLng
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Flow


object Database {
    // Set realm configuration of datatype Device
    // Open realm configuration as realm
    val configuration = RealmConfiguration.Builder(schema = setOf(Device::class)).build()
    val realm = Realm.open(configuration)

    fun write() {
        val device = Device().apply {
            deviceType = "Phone"
            deviceName = "Test"
            deviceTracking = false
        }

        //writeBlocking can also return the object being inserted into the realm
        val managedDevice = realm.writeBlocking {
            copyToRealm(device)
        }
    }

    //Asynchronous insertion/writing
    fun insert(device: Device) {
        CoroutineScope(Dispatchers.IO).launch {
            realm.write {
                copyToRealm(device)
            }
        }
    }

    // P.S. realm.query<Device> returns as RealmResults<Device>, but it can be interpreted as List<Device>
    fun getAllActiveEntries(): List<Device> {
        return realm.query<Device>("deviceTracking == true").find()
    }

    fun getAllInactiveEntries(): List<Device> {
        return realm.query<Device>("deviceTracking == false").find()
    }

    fun getAllEntries(): List<Device> {
        return realm.query<Device>().find()
    }

    //Need to get working as allEntriesLiveData somehow
    suspend fun queryAsync() {
        realm.query<Device>()
            .asFlow()
            .collect { results: ResultsChange<Device> ->
                when (results) {
                    is InitialResults<Device> -> println("debug: Initial results size ${results.list.size}")
                    is UpdatedResults<Device> -> println("Updated results changes ${results.changes}" +
                            " deletes: ${results.deletions} insertions: ${results.insertions} ")
                }
            }
    }

    fun update(device: Device) {
        CoroutineScope(Dispatchers.IO).launch {
            //Object is frozen and is safe to pass into the asynchronous transaction
            val name = device.deviceName
            println("debug: name of device being updated $name")
            //Find the first device without tracking
            realm.query<Device>("deviceName == $0", name)
                .first()
                .find()
                ?.also { deviceToBeUpdated ->
                    println("debug: name of device in database ${deviceToBeUpdated.deviceName}")
                    realm.write {
                        findLatest(deviceToBeUpdated)?.deviceName = name
                        findLatest(deviceToBeUpdated)?.deviceConnected = device.deviceConnected
                        findLatest(deviceToBeUpdated)?.deviceTracking = device.deviceTracking
                        findLatest(deviceToBeUpdated)?.deviceType = device.deviceType
                        findLatest(deviceToBeUpdated)?.deviceLastLocation = device.deviceLastLocation
                        findLatest(deviceToBeUpdated)?.deviceMacAddress = device.deviceMacAddress
                    }
                }
        }
    }

    suspend fun delete(device: Device) {
        val name = device.deviceName
        realm.write {
            delete(realm.query<Device>("deviceName == $0", name))
        }
    }

    suspend fun deleteAll() {
        //Delete all device
        realm.write {
            val query: RealmQuery<Device> = this.query<Device>()
            delete(query)
        }
    }

    //Insertion without using type conversion

//    suspend fun insert(device: _Device) {
//        val insertion = rDevice().apply {
//            var deviceType: String = device.deviceType
//            var deviceName: String = device.deviceName
//            var lat: Double = device.deviceLastLocation?.latitude ?: 0.0
//            var lng: Double = device.deviceLastLocation?.longitude ?: 0.0
//            var deviceConnected: Boolean = device.deviceConnected
//            var deviceMacAddress: String = device.deviceMacAddress
//        }
//
//            realm.write {
//                copyToRealm(insertion)
//            }
//    }
}