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


class MainActivity : AppCompatActivity(){

    private lateinit var sharedPref: SharedPreferences

    private val mMessageReceiver = object:  BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            changeNameOfTheButton(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()

        sharedPref = getSharedPreferences(SHARRED_KEY_PREFERENCES_KEY, Context.MODE_PRIVATE)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter(AccelerometerService.BROADCAST_ACTION)
        )
    }

    private fun initUI(){
        start_accelometer.setOnClickListener { initService() }
    }

    private fun initService(){
        if(isServiceRunning()){
            val intent = Intent(this, AccelerometerService::class.java)
            stopService(intent)

            changeNameOfTheButton(false)

        } else {
            val intent = Intent(this, AccelerometerService::class.java)
            startService(intent)

            changeNameOfTheButton(true)
        }
    }

    private fun changeNameOfTheButton(startRunning : Boolean){
        if(startRunning){
            start_accelometer.text = getString(R.string.stop_accelometer)
        } else {
            start_accelometer.text = getString(R.string.start_accelometer)
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

    override fun onResume() {
        super.onResume()

        sharedPref.edit().putBoolean(APP_IS_OPEN_PREFERENCES_KEY,true).apply()

    }

    override fun onPause() {
        super.onPause()

        sharedPref.edit().putBoolean(APP_IS_OPEN_PREFERENCES_KEY,false).apply()
    }
}
