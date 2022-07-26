package com.example.fastbletrial

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fastbletrial.databinding.FragmentBleBinding
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class BleFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: FragmentBleBinding
    private lateinit var viewModel: BleViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBleBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[BleViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        binding.btnScan.setOnClickListener {
            viewModel.checkScanStatus()
        }

        viewModel.locationPermissionStatus.observeForever {
            if(!it)
                requestLocationPermission()
        }

        BLE.bleLiveDeviceList.observeForever {
            if(isAdded)
            {
                binding.recyclerViewDevices.adapter = CustomAdapterBLE(it)
                binding.recyclerViewDevices.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerViewDevices.addItemDecoration(DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL))
                binding.recyclerViewDevices.adapter?.notifyDataSetChanged()
            }
        }


        return binding.root
    }

    private fun requestLocationPermission()
    {
        EasyPermissions.requestPermissions(this,"Location permission is necessary to scan BLE devices",1, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.permissionPermanentlyDenied(this, perms.first()))
            AppSettingsDialog.Builder(requireActivity()).build().show()
        else
            requestLocationPermission()
    }

}
