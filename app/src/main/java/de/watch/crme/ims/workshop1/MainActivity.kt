package de.watch.crme.ims.workshop1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.watch.crme.ims.workshop1.services.AccelerometerService
import kotlinx.android.synthetic.main.activity_main.*

import android.app.ActivityManager
import android.content.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.watch.crme.ims.workshop1.Utils.APP_IS_OPEN_PREFERENCES_KEY
import de.watch.crme.ims.workshop1.Utils.SHARRED_KEY_PREFERENCES_KEY
import android.media.MediaPlayer
import android.view.View
import de.watch.crme.ims.workshop1.Utils.VECTOR_PREFERENCES_KEY


class MainActivity : AppCompatActivity(){

    private lateinit var sharedPref: SharedPreferences

    private lateinit var mp : MediaPlayer

    private val mAlarmReceiver = object:  BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            freeFallDetected()
        }
    }

    private val mVectorReceiver = object:  BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val vector = intent?.getDoubleExtra(VECTOR_PREFERENCES_KEY, 0.0)

            vector?.let {
                vectorTv.text = "Current vector : $vector"
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()

        sharedPref = getSharedPreferences(SHARRED_KEY_PREFERENCES_KEY, Context.MODE_PRIVATE)

        initAlarmReceiver()

        mp = MediaPlayer.create(this, R.raw.alert)
    }

    private fun initAlarmReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mAlarmReceiver, IntentFilter(AccelerometerService.BROADCAST_ACTION_ALARM)
        )
    }

    private fun initVectorReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mVectorReceiver, IntentFilter(AccelerometerService.BROADCAST_ACTION_ACCELEROMETER)
        )
    }

    private fun stopReceiver(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mVectorReceiver)
    }

    private fun initUI(){

        start_accelometer.setOnClickListener { initService(!isServiceRunning()) }

        stopAlarm.setOnClickListener { cancelTheAlarm() }
    }

    private fun initService(startService : Boolean ){

        if(startService){

            val intent = Intent(this, AccelerometerService::class.java)
            startService(intent)

            changeNameButton(true)

            initVectorReceiver()

        } else {

            val intent = Intent(this, AccelerometerService::class.java)
            stopService(intent)

            changeNameButton(false)

            stopReceiver()
        }
    }

    private fun isServiceRunning() : Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (AccelerometerService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun freeFallDetected(){

        soundTheAlarm()

        start_accelometer.visibility = View.GONE

        stopAlarm.visibility = View.VISIBLE

        background.setBackgroundColor(resources.getColor(R.color.alarmed))

    }

    private fun soundTheAlarm(){

        mp.start()
    }

    private fun cancelTheAlarm(){

        mp.reset()

        start_accelometer.visibility = View.VISIBLE

        stopAlarm.visibility = View.GONE

        background.setBackgroundColor(resources.getColor(R.color.not_alarmed))

        initService(false)

        vectorTv.text = ""
    }

    private fun changeNameButton(start : Boolean){
        if(start){
            start_accelometer.setText(R.string.stop_accelometer)
        } else {
            start_accelometer.setText(R.string.start_accelometer)

        }
    }

    override fun onResume() {
        super.onResume()

        sharedPref.edit().putBoolean(APP_IS_OPEN_PREFERENCES_KEY,true).apply()

    }

    override fun onPause() {
        super.onPause()

        sharedPref.edit().putBoolean(APP_IS_OPEN_PREFERENCES_KEY,false).apply()
    }
}
