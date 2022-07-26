package com.example.fastbletrial

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fastbletrial.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentChange(BleFragment())

        BLE.connectionStatus.observeForever {
            if (it=="trying to connect")
            {
                AlertWindow.buildWindow(this, "Connecting...","Please wait till the connection is active", R.drawable.ic_baseline_bluetooth_searching_24)
                AlertWindow.showWindow()
            }
            if(it=="connection failed")
            {
                AlertWindow.buildWindow(this, "Connection Failed","Please try again", android.R.drawable.ic_dialog_alert)
                AlertWindow.builder
                    .setPositiveButton("Try again") { _, _ ->
                        AlertWindow.dismissWindow()
                        BLE.connect(BLE.selectedDevice.value!!)
                    }
                    .setNegativeButton("Exit") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }

                AlertWindow.showWindow()
            }
            if(it=="connected")
            {
                AlertWindow.dismissWindow()
                fragmentChange(DeviceFragment())
            }
            if(it=="connection lost")
            {
                AlertWindow.buildWindow(this, "Connection Lost","Please try reconnecting",R.drawable.ic_baseline_bluetooth_disabled_24)
                AlertWindow.builder
                    .setPositiveButton("Reconnect") { _, _ ->
                        AlertWindow.dismissWindow()
                        BLE.connect(BLE.selectedDevice.value!!)
                    }
                    .setNegativeButton("Exit") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        fragmentChange(BleFragment())
                    }

               AlertWindow.showWindow()
            }
            if(it=="disconnected")
            {
                Toast.makeText(applicationContext, "Device disconnected successfully", Toast.LENGTH_SHORT).show()
                fragmentChange(BleFragment())
            }
        }
    }

    private fun fragmentChange(fragment: Fragment)
    {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, fragment).commit()
    }
}