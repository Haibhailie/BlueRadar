package ca.sfu.BlueRadar.util

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converter {

    @TypeConverter
    fun toBytes(points: LatLng): ByteArray {
        val gson = Gson()
        val inputString = gson.toJson(points)
        return inputString.toByteArray()
    }

    @TypeConverter
    fun fromBytes(byteArray: ByteArray): LatLng {
        val jsonString = byteArray.toString(Charsets.UTF_8)
        val gson = Gson()
        val pointsListType = object : TypeToken<ArrayList<LatLng>>() {}.type
        var points: LatLng = LatLng(0.0, 0.0)
        if (jsonString.isNotEmpty())
            points = gson.fromJson(jsonString, pointsListType)
        return points
    }
}