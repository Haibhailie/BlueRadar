package ca.sfu.BlueRadar.ui.dashboard

import ca.sfu.BlueRadar.ui.devices.DeviceViewModel
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.BlueRadar.R
import ca.sfu.BlueRadar.navigation.NavigationActivity
import ca.sfu.BlueRadar.ui.devices.data.Device
import com.google.android.material.switchmaterial.SwitchMaterial

class DashboardRecyclerAdapter(
    private val context: Context,
    private var deviceList: List<Device>,
    private var deviceViewModel: DeviceViewModel
) :
        RecyclerView.Adapter<DashboardRecyclerAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = View.inflate(context, R.layout.devices_card_dashboard, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currItem = deviceList[position]
        //holder.imageView.setImageResource(ItemsViewModel.image)
        holder.deviceNameTextView.text = currItem.deviceName

        if (currItem.deviceTracking) {
            holder.deviceIsTrackingTextView.text = "Tracking"
            holder.deviceIsTrackingTextView.setTextColor(Color.GREEN)
        } else {
            holder.deviceIsTrackingTextView.text = "Not Tracking"
            holder.deviceIsTrackingTextView.setTextColor(Color.GRAY)
        }
        //Uncomment when deviceConnected is implemented
        if (currItem.deviceConnected) {
            holder.deviceStatusTextView.text = "Connected"
            holder.deviceStatusTextView.setTextColor(Color.GREEN)
        } else {
            holder.deviceStatusTextView.text = "Not Connected"
            holder.deviceStatusTextView.setTextColor(Color.RED)
        }

        holder.navButton.setOnClickListener {
            //Start the location tracking service
            val navigationIntent = Intent(context, NavigationActivity::class.java)
            navigationIntent.putExtra("deviceLocation", currItem.deviceLastLocation)
            context.startActivity(navigationIntent)
        }
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    fun replace(newExerciseEntryList: List<Device>) {
        deviceList = newExerciseEntryList
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        //val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val deviceNameTextView: TextView = itemView.findViewById(R.id.deviceNameTextViewD)
        val deviceStatusTextView: TextView = itemView.findViewById(R.id.deviceStatusTextViewD)
        val deviceIsTrackingTextView: TextView = itemView.findViewById(R.id.deviceTrackingTextViewD)
        val navButton: ImageButton = itemView.findViewById(R.id.navigateButtonD)
    }

}