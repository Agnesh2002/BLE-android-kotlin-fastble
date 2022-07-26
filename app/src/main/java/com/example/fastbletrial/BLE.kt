package com.example.fastbletrial

import android.app.Application
import android.bluetooth.BluetoothGatt
import androidx.lifecycle.MutableLiveData
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleReadCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.nio.charset.StandardCharsets


object BLE {

    private val ble: BleManager = BleManager.getInstance()
    private var deviceList: ArrayList<BleDevice> = ArrayList()
    var bleLiveDeviceList: MutableLiveData<ArrayList<BleDevice>> = MutableLiveData()
    var scanStatus: MutableLiveData<Boolean> = MutableLiveData()
    var connectionStatus: MutableLiveData<String> = MutableLiveData()
    var selectedDevice: MutableLiveData<BleDevice> = MutableLiveData()
    var availableGatt: ArrayList<String> = ArrayList()

    fun initiate(application: Application) {
        ble.init(application)
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    fun checkBleSupport(): Boolean {
        if (ble.isSupportBle) {
            Logger.i("BLE is supported")
            ble.enableBluetooth()
            if (ble.isBlueEnable)
                return true
        } else {
            Logger.e("BLE is not supported")
            return false
        }
        return false
    }

    fun startScan() {
        scanStatus.postValue(true)
        deviceList.clear()
        bleLiveDeviceList.postValue(deviceList)
        ble.scan(object : BleScanCallback() {
            override fun onScanStarted(success: Boolean) {
                scanStatus.postValue(true)
                Logger.d("Scan started : $success")
            }

            override fun onScanning(bleDevice: BleDevice?) {}

            override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
                for (scanResult in scanResultList!!) {
                    deviceList.add(scanResult)
                }
                bleLiveDeviceList.postValue(deviceList)
                scanStatus.postValue(false)
            }
        })
    }

    fun connect(device: BleDevice) {
        selectedDevice.postValue(device)

        ble.connect(device, object : BleGattCallback() {
            override fun onStartConnect() {
                connectionStatus.postValue("trying to connect")
                Logger.i("Trying to connect to ${device.device}")
            }

            override fun onConnectFail(bleDevice: BleDevice, exception: BleException) {
                Logger.e("Connection failed")
                connectionStatus.postValue("connection failed")
            }

            override fun onConnectSuccess(bleDevice: BleDevice, gatt: BluetoothGatt, status: Int) {
                Logger.i("Connected successfully")
                connectionStatus.postValue("connected")

                val availableServices = ble.getBluetoothGattServices(bleDevice)
                availableGatt.clear()
                for (service in availableServices) {
                    for (characteristics in service.characteristics) {
                        availableGatt.add(service.uuid.toString() + " : " + characteristics.uuid + "\n")
                    }
                }
                Logger.i(availableGatt.toString())
            }

            override fun onDisConnected(isActiveDisConnected: Boolean, bleDevice: BleDevice, gatt: BluetoothGatt, status: Int) {
                connectionStatus.postValue("connection lost")
                Logger.e("Disconnected")
            }
        })
    }

    fun readData(bleDevice: BleDevice, uuid_service: String, uuid_characteristic_read: String) {

        ble.read(bleDevice, uuid_service, uuid_characteristic_read, object : BleReadCallback() {
            override fun onReadSuccess(data: ByteArray) {
                val strData = String(data, StandardCharsets.UTF_8)
                Logger.i("READ DATA = $strData")
            }
            override fun onReadFailure(exception: BleException) {
                Logger.e("Reading failed")
            }
        })
    }

    fun writeData(bleDevice: BleDevice, uuid_service: String, uuid_characteristic_write: String, data: ByteArray) {

        BleManager.getInstance().write(bleDevice, uuid_service, uuid_characteristic_write, data, object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                    Logger.i("WRITE SUCCESSFUL")
                }
                override fun onWriteFailure(exception: BleException) {
                    Logger.i("WRITE FAILED")
                }
            })
    }

    fun disconnect(device: BleDevice) {
        ble.disconnect(device)
        ble.destroy()
        connectionStatus.postValue("disconnected")
        Logger.i("Device disconnected successfully")
    }

    fun stopScan()
    {
        ble.cancelScan()
    }


}