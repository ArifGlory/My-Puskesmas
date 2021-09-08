package com.tapisdev.myapplication.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.myapplication.R
import com.tapisdev.myapplication.adapter.AdapterPuskesmas
import com.tapisdev.myapplication.databinding.ActivityPersebaranBinding
import com.tapisdev.myapplication.model.Puskesmas
import com.tapisdev.myapplication.util.PermissionHelper
import java.util.ArrayList

class PersebaranActivity : BaseActivity(), OnMapReadyCallback , PermissionHelper.PermissionListener{

    private lateinit var mMap: GoogleMap
    lateinit var  permissionHelper : PermissionHelper

    var TAG_GET_PUSKES = "getPuskes"
    lateinit var adapter: AdapterPuskesmas

    var listPuskesmas = ArrayList<Puskesmas>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_persebaran)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        permissionLocation()
        getDataPuskes()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            permissionLocation()
            Log.d("permission","not granted")
        }else{
            mMap.isMyLocationEnabled = true
        }
    }

    private fun permissionLocation() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        listPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionHelper.checkAndRequestPermissions(listPermissions)
    }

    fun getDataPuskes(){
        showLoading(this)
        puskesRef.get().addOnSuccessListener { result ->
            listPuskesmas.clear()
            dismissLoading()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var puskesmas : Puskesmas = document.toObject(Puskesmas::class.java)
                puskesmas.id_puskesmas = document.id
                listPuskesmas.add(puskesmas)
            }

            if (listPuskesmas.size == 0){
                showInfoMessage("Belum ada data Puskesmas")
            }else{
                setMarkers()
            }


        }.addOnFailureListener { exception ->
            dismissLoading()
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_PUSKES,"err : "+exception.message)
        }
    }

    fun setMarkers(){
        for (i in 0 until listPuskesmas.size){
            var lat = listPuskesmas.get(i).lat.toDouble()
            var lon = listPuskesmas.get(i).lon.toDouble()
            var lokasi = LatLng(lat,lon)

            if (i != listPuskesmas.size -1){
                mMap.addMarker(MarkerOptions().position(lokasi).title(listPuskesmas.get(i).nama_puskesmas))
            }else{
                mMap.addMarker(MarkerOptions().position(lokasi).title(listPuskesmas.get(i).nama_puskesmas))
                val zoomLevel = 10.0f //This goes up to 21
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi,zoomLevel))
            }
        }
    }

    override fun onPermissionCheckDone() {

    }
}