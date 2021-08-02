package com.tapisdev.myapplication.activity.pengguna

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Info
import com.akexorcist.googledirection.util.DirectionConverter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.myapplication.R
import com.tapisdev.myapplication.model.Puskesmas
import com.tapisdev.myapplication.util.Astar
import com.tapisdev.myapplication.util.PermissionHelper
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_lokasi_puskesmas.*

class LokasiPuskesmasActivity : BaseActivity(), OnMapReadyCallback,PermissionHelper.PermissionListener,LocationListener {

    private lateinit var mMap: GoogleMap
    lateinit var i : Intent
    lateinit var lm : LocationManager
    lateinit var puskesmas : Puskesmas
    lateinit var  permissionHelper : PermissionHelper
    lateinit var myAstar: Astar

    var lat  = 0.0
    var lon  = 0.0
    var myLat  = 0.0
    var myLon  = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lokasi_puskesmas)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        lm = (getSystemService(LOCATION_SERVICE) as LocationManager?)!!
        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        myAstar = Astar(this)

        i = intent
        puskesmas = i.getSerializableExtra("puskesmas") as Puskesmas
        lat = puskesmas.lat.toDouble()
        lon = puskesmas.lon.toDouble()

        permissionLocation()

        rlRute.setOnClickListener {
            if (myLat != 0.0){
                showLoading(this)
                myAstar.doAstar()

                var origin =  LatLng(myLat,myLon)
                var destination = LatLng(lat,lon)
                drawJalur(origin,destination)
            }else{
                showInfoMessage("lokasi anda tidak dapat ditemukan, mohon aktifkan GPS anda")
                try {
                    // Request location updates
                    lm?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                } catch(ex: SecurityException) {
                    Log.d("getLoc", "Security Exception, no location available")
                }
            }
        }

        if ( !lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            // Call your Alert message
            showInfoMessage("gps tidak aktif, layanan ini memerlukan GPS untuk Aktif")
        }else{
            try {
                // Request location updates
                lm?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
            } catch(ex: SecurityException) {
                Log.d("getLoc", "Security Exception, no location available")
                showErrorMessage("Lokasi anda tidak ditemukan, harap aktifkan GPS Anda")
            }
        }
        showSweetInfo("Untuk akurasi yang lebih baik, harap aktifkan GPS Anda")
    }

    private fun permissionLocation() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        listPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionHelper.checkAndRequestPermissions(listPermissions)
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

        val lokasi = LatLng(lat, lon)
        val zoomLevel = 16.0f //This goes up to 21
        mMap.addMarker(MarkerOptions().position(lokasi).title("Lokasi Puskesmas"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi,zoomLevel))

    }

    //define the listener and get user location
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            //thetext.text = ("" + location.longitude + ":" + location.latitude)
            //showInfoMessage("lat : "+location.longitude+" | lon : "+location.longitude)
            myLat = location.latitude
            myLon = location.longitude
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onPermissionCheckDone() {
        if ( !lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            // Call your Alert message
            showInfoMessage("gps tidak aktif, layanan ini memerlukan GPS untuk Aktif")
        }else{
            try {
                // Request location updates
                lm?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
            } catch(ex: SecurityException) {
                Log.d("getLoc", "Security Exception, no location available")
                showErrorMessage("Lokasi anda tidak ditemukan, harap aktifkan GPS Anda")
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        Log.d("getLoc",""+location.latitude+" | "+location.toString())
        if (location == null) {
            showErrorMessage("Lokasi Kamu Tidak Dapat Ditemukan")
        } else {
            myLat = location.latitude
            myLon = location.longitude
            //showInfoMessage("lat :"+myLat + " | lon:"+myLon)

        }
    }

    fun drawJalur(koordinatAwal : LatLng,koordinatAkhir : LatLng){
        dismissLoading()
        GoogleDirection.withServerKey("AIzaSyBYR_9WSimaGMFhwRyTSy25DKPrSnl97uc")
            .from(koordinatAwal)
            .to(koordinatAkhir)
            .transportMode(TransportMode.DRIVING)
            .execute(object : DirectionCallback {
                override fun onDirectionSuccess(direction: Direction?) {
                    if (direction!!.isOK) {
                        val route =
                            direction.routeList[0]
                        val leg = route.legList[0]
                        val stepList =
                            leg.stepList
                        val nodeList: ArrayList<LatLng> = leg.directionPoint

                        var distanceInfo  = leg.distance
                        var jarak = distanceInfo.text
                        jarak = jarak.dropLast(2)

                        tvJarak.setText(jarak+" KM")
                        var polylineOptions : PolylineOptions
                        polylineOptions = DirectionConverter.createPolyline(this@LokasiPuskesmasActivity,nodeList,6,
                            Color.BLUE)
                        mMap.addPolyline(polylineOptions)

                    }
                }

                override fun onDirectionFailure(t: Throwable) {
                    Log.d("astar", "Gagal mendapatkan graph ")
                    Toasty.error(applicationContext, "Tidak dapat menemukan rute", Toasty.LENGTH_SHORT)
                        .show()
                }
            })
    }
}
