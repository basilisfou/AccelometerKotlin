package de.watch.crme.ims.workshop1.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.content.SharedPreferences
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.watch.crme.ims.workshop1.Utils.APP_IS_OPEN_PREFERENCES_KEY
import de.watch.crme.ims.workshop1.Utils.SHARRED_KEY_PREFERENCES_KEY
import de.watch.crme.ims.workshop1.MainActivity

class AccelerometerService : Service(), SensorEventListener {

    private var mSensorManager : SensorManager?= null
    private var mAccelerometer : Sensor ?= null
    private var sharedPref: SharedPreferences? = null

    companion object{

        const val BROADCAST_ACTION_ALARM = "broadcast.action.alarm"

        const val BROADCAST_ACTION_ACCELEROMETER = "broadcast.action.accelerometer"

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onCreate() {
        super.onCreate()

        init()

        sharedPref = getSharedPreferences(SHARRED_KEY_PREFERENCES_KEY, Context.MODE_PRIVATE)
    }


    private fun init(){
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        mSensorManager?.registerListener(this,mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type ==Sensor.TYPE_ACCELEROMETER){

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val mAccel = Math.sqrt((x * x + y * y + z * z).toDouble())

            Log.d("ACCELEROMETER"," x: $x , y: $y , z: $z , vector : $mAccel")

            if(mAccel <= 2){

                unregisterSesnsorListener()

                notifyFreeFall()
            }
        }
    }

    private fun notifyFreeFall(){

        val isOpen = sharedPref?.getBoolean(APP_IS_OPEN_PREFERENCES_KEY, false)

        if(isOpen == true){
            startLocalBroadcastReceiver()
        } else {
            openActivity()
        }
    }

    private fun startLocalBroadcastReceiver(){
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(BROADCAST_ACTION_ALARM))
    }



    private fun openActivity(){
        val dialogIntent = Intent(this, MainActivity::class.java)
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(dialogIntent)
    }

    private fun unregisterSesnsorListener(){
        mSensorManager?.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        mSensorManager?.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}