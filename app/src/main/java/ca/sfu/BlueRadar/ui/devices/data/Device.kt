package ca.sfu.BlueRadar.ui.devices.data

import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type
/**
 *
 * Device class entity (device_table)
 *
 */
@TypeConverters(ArrayConverter::class)
@Entity(tableName = "device_table")
data class Device(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "device_type")
    var deviceType: String = "",

    @ColumnInfo(name = "device_name")
    var deviceName: String = "",

    @ColumnInfo(name = "device_tracking")
    var deviceTracking: Boolean = false,

    @ColumnInfo(name = "device_last_location")
    var deviceLastLocation: LatLng? = LatLng(0.0,0.0),

    @ColumnInfo(name = "device_connected")
    var deviceConnected: Boolean = false
)

class ArrayConverter {
    private val gson = Gson()

    @TypeConverter
    fun toLatLng(json: String): LatLng? {
        val type: Type = object : TypeToken<LatLng?>() {}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun fromLatLng(latLng: LatLng?): String{
        val type: Type = object : TypeToken<LatLng?>() {}.type
        return gson.toJson(latLng, type)
    }

}