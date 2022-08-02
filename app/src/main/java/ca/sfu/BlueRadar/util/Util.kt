package ca.sfu.BlueRadar.util

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
}