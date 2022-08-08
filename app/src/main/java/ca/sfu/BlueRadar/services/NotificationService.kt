package ca.sfu.BlueRadar.services


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import ca.sfu.BlueRadar.R
import ca.sfu.BlueRadar.ui.devices.DevicesFragment
import java.text.SimpleDateFormat
import java.util.*

class NotificationService: Service(){

    private var notificationID = 1
    private var channelID = "notification channel"
    private lateinit var myBroadcastReceiver: MyBroadcastReceiver
    private lateinit var notificationManager: NotificationManager
    private lateinit var preferences: SharedPreferences

    companion object {
        val STOP_SERVICE_ACTION = "stop service action"
    }

    override fun onCreate() {
        super.onCreate()
        println("debug: onCreate called")
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        setupNotification()

        myBroadcastReceiver = MyBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(STOP_SERVICE_ACTION)
        registerReceiver(myBroadcastReceiver, intentFilter)

        //Check if notification should be enabled or disabled
        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key){
                "preference_notifications" -> {
                    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                    val checkBox = prefs.getBoolean("preference_notifications", true)
                    if(checkBox){
                        setupNotification()
                    }else{
                        notificationManager.cancel(notificationID)
                        notificationManager.cancelAll()
//                        unregisterReceiver(myBroadcastReceiver)
                    }
                }
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        println("debug: onStartCommand called")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        println("debug: onDestroy called")
        stopSelf()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun setupNotification() {
        notificationID = createID()
        val notificationIntent = Intent(this, DevicesFragment::class.java)
        val contentIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_MUTABLE
        )

        var notificationBuilder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.blueradar_logo)
            .setContentTitle("BlueRadar")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setContentText("Tracking your device(s)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).setOngoing(true)
            .setContentIntent(contentIntent)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val checkBox = prefs.getBoolean("preference_notifications", true)
        if(checkBox) {
            val notification = notificationBuilder.build()

            val notificationChannel =
                NotificationChannel(channelID, "my_channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(notificationID, notification)
        }
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stopSelf()
            notificationManager.cancel(notificationID)
            notificationManager.cancelAll()
            unregisterReceiver(myBroadcastReceiver)
        }
    }

    private fun createID(): Int {
        val now = Date()
        return SimpleDateFormat("ddHHmmss", Locale.US).format(now).toInt()
    }

//    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
//        println("debug: sharedpreference change, boolean value")
//        if(key == "preference_notifications"){
//            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
//            val checkBox = prefs.getBoolean("preference_notifications", true)
//            println("debug: sharedpreference change, boolean value ${checkBox}")
//            if(!checkBox) {
//                notificationManager.cancelAll()
//            }
//        }
//    }
}
