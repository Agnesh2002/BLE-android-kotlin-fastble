package com.example.fastbletrial

import android.Manifest
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import pub.devrel.easypermissions.EasyPermissions

class BleViewModel(application: Application) : AndroidViewModel(application) {

    var buttonText:MutableLiveData<String> = MutableLiveData("start scanning")
    var locationPermissionStatus: MutableLiveData<Boolean> = MutableLiveData()
    var pBarVisibility: MutableLiveData<Boolean> = MutableLiveData(false)

    fun checkScanStatus()
    {
        BLE.scanStatus.observeForever {
            if(it)
            {
                buttonText.postValue("stop scanning")
                pBarVisibility.postValue(true)
            }
            else
            {
                if(BLE.bleLiveDeviceList.value!!.isEmpty())
                    Toast.makeText(getApplication(),"No BLE devices found, try scanning again", Toast.LENGTH_SHORT).show()
                buttonText.postValue("start scanning")
                pBarVisibility.postValue(false)
            }
        }

        if(buttonText.value == "start scanning")
            initiate()
        else
            stopScanning()
    }


    private fun initiate()
    {
        BLE.initiate(getApplication())
        if(BLE.checkBleSupport())
            checkLocationPermission()
    }

    private fun checkLocationPermission()
    {
        val hasLocationPermission = EasyPermissions.hasPermissions(getApplication(),Manifest.permission.ACCESS_COARSE_LOCATION)

        if(!hasLocationPermission)
            locationPermissionStatus.postValue(false)
        else
            BLE.startScan()
    }

    private fun stopScanning()
    {
        BLE.stopScan()
    }



}