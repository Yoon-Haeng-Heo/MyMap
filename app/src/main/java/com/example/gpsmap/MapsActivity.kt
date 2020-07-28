package com.example.gpsmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // supportMapFragment 를 가져와서 지도가 준비되면 알림을 받습니다.

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
    /*
    * 사용 가능한 맵을 조작합니다.
    * 지도를 사용할 준비가 되면 이 콜백이 호출됩니다.
    * 여기서 마커나 선, 청취자를 추가하거나 카메라를 이동할 수 있습니다.
    * 호주 시드니 근처에 마커를 추가하고 있습니다.
    * Google Play 서비스가 기기에 설치되어 있지 않은 경우 사용자에게 SupportMapFragment 안에 Google Play 서비스를 설치하라는 메시지가 표시됩니다.
    * 이 메소드는 사용자가 Google Play 서비스를 설치하고 앱으로 돌아온 후에만 호출(혹은 실행)됩니다.
    * */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}