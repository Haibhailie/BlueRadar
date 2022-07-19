package ca.sfu.BlueRadar.ui.devices.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 *
 * Exercise entry's database instance.
 */
@Database(entities = [Device::class], version = 1)
abstract class DeviceDatabase: RoomDatabase() {
    abstract val deviceDatabaseDao: DeviceDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: DeviceDatabase? = null

        fun getInstance(context: Context) : DeviceDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DeviceDatabase::class.java, "device_table"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}