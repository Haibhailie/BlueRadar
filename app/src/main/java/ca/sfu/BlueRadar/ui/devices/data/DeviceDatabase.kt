package ca.sfu.BlueRadar.ui.devices.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmQuery
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import java.util.concurrent.Flow


/**
 *
 * Exercise entry's database instance.
// */
//@Database(entities = [Device::class], version = 1)
//abstract class DeviceDatabase: RoomDatabase() {
//    abstract val deviceDatabaseDao: DeviceDatabaseDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: DeviceDatabase? = null
//
//        fun getInstance(context: Context) : DeviceDatabase {
//            synchronized(this) {
//                var instance = INSTANCE
//                if(instance == null){
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        DeviceDatabase::class.java, "device_table"
//                    ).build()
//                    INSTANCE = instance
//                }
//                return instance
//            }
//        }
//    }
//}