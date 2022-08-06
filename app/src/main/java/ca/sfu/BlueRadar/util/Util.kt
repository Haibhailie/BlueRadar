package ca.sfu.BlueRadar.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter

object Util {

    fun <T> removeDuplicates(list: ArrayList<T>): ArrayList<T>? {
        // Create a new ArrayList
        val newList = ArrayList<T>()
        // Traverse through the first list
        for (element in list) {
            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {
                newList.add(element)
            }
        }
        // return the new list
        return newList
    }

    val filter = IntentFilter().apply {
        addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        addAction(BluetoothDevice.ACTION_FOUND)
        addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
    }
}