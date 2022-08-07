package ca.sfu.BlueRadar.ui.dashboard

import ca.sfu.BlueRadar.ui.devices.DeviceViewModel
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.BlueRadar.R
import ca.sfu.BlueRadar.navigation.NavigationActivity
import ca.sfu.BlueRadar.ui.devices.data.Device
import com.google.android.material.switchmaterial.SwitchMaterial
import org.w3c.dom.Text
import java.text.DecimalFormat

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

        if(currItem.deviceMacAddress.isNotEmpty()) {
            holder.deviceMacAddTextView.text = "MAC Address: ${currItem.deviceMacAddress}"
        }

        val df = DecimalFormat("#.##")

        if(currItem.deviceLastLocation != null) {
            holder.deviceLatLngTextView.text = "Last Location (Lat/Lon): ${df.format(currItem
                .deviceLastLocation!!
                .latitude)}, ${df.format(currItem.deviceLastLocation!!
                .longitude)}"
        }

        holder.navButton.setOnClickListener {
            //Start the location tracking service
            val navigationIntent = Intent(context, NavigationActivity::class.java)
            navigationIntent.putExtra("deviceLocation", currItem.deviceLastLocation)
            navigationIntent.putExtra("deviceName", currItem.deviceName)
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
        val deviceMacAddTextView: TextView = itemView.findViewById(R.id.deviceMacAddTextViewD)
        val deviceLatLngTextView: TextView = itemView.findViewById(R.id.deviceLatLngTextViewD)
        val navButton: Button = itemView.findViewById(R.id.navigateButtonD)

    }

}