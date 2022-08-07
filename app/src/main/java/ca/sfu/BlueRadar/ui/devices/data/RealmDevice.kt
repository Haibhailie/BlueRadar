package ca.sfu.BlueRadar.ui.devices.data

import android.net.MacAddress
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import io.realm.kotlin.types.RealmObject


//Device Object
class Device : RealmObject {
    @PrimaryKey
    var id: Long = 0L

    var deviceType: String = ""

    var deviceName: String = ""

    var deviceTracking: Boolean = false

    //Realm can contain lists of non-realm object data types
    // var deviceLastLocation: List<LatLng>? = null
    // var deviceLastLocation: RealmList<LatLng>? = null
    var deviceLastLocation: LatLng? = LatLng(0.0,0.0)

    var lat: Double = 0.0

    var lng: Double = 0.0

    var deviceConnected: Boolean = false

    var deviceMacAddress: String = ""

    fun deviceLastLocation(): LatLng {
        return LatLng(lat, lng)
    }

    constructor(deviceType: String, deviceName: String, deviceTracking: Boolean,
                deviceLastLocation: LatLng?, deviceConnected: Boolean, deviceMacAddress: String) {
        this.deviceType = deviceType
        this.deviceName = deviceName
        this.deviceTracking = deviceTracking
        this.deviceLastLocation = deviceLastLocation
        this.deviceConnected = deviceConnected
        this.deviceMacAddress = deviceMacAddress
    }
    constructor(){}
}

class _Device {
    //    @PrimaryKey
    var id: Long = 0L

    var deviceType: String = ""

    var deviceName: String = ""

    var deviceTracking: Boolean = false

    var deviceLastLocation: LatLng? = LatLng(0.0,0.0)

    var deviceConnected: Boolean = false

    var deviceMacAddress: String = ""
}