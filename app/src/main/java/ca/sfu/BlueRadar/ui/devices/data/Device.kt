package ca.sfu.BlueRadar.ui.devices.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 * Device class entity (device_table)
 *
 */
@Entity(tableName = "device_table")
data class Device (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "device_type")
    var deviceType: String = "",

    @ColumnInfo(name = "device_name")
    var deviceName: String = "",

    @ColumnInfo(name = "device_tracking")
    var deviceTracking: Boolean = false,

    @ColumnInfo(name = "device_last_location")
    var deviceLastLocation: String = "",

)