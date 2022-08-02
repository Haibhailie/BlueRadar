package ca.sfu.BlueRadar.ui.devices.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 *
 * DAO for the exercise entry database.
 *
 */
@Dao
interface DeviceDatabaseDao {
    @Insert
    suspend fun insertEntry(exerciseEntry: Device)

    //A Flow is an async sequence of values
    //Flow produces values one at a time (instead of all at once) that can generate values
    //from async operations like network requests, database calls, or other async code.
    //It supports coroutines throughout its API, so you can transform a flow using coroutines as well!
    //Code inside the flow { ... } builder block can suspend. So the function is no longer marked with suspend modifier.
    //See more details here: https://kotlinlang.org/docs/flow.html#flows
    @Query("SELECT DISTINCT * FROM device_table GROUP BY device_mac_address")
    fun getAllEntries(): Flow<List<Device>>

    @Query("DELETE FROM device_table")
    fun deleteAll()

    @Query("DELETE FROM device_table WHERE id = :key") //":" indicates that it is a Bind variable
    fun deleteEntry(key: Long)

    @Update(entity = Device::class)
    fun update(device:Device)

//    @Update
}