package com.example.gpsmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import splitties.alertdialog.*
import splitties.toast.toast


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val REQUEST_ACCESS_FINE_LOCATION = 1000
    private lateinit var mMap: GoogleMap
    // 위치 정보를 주기적으로 얻는 데 필요한 객체들을 선언합니다.
    // 1. MyLocationCallBack 은 MapsActivity class 의 inner class 로 생성했습니다.
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback: MyLocationCallBack

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // supportMapFragment 를 가져와서 지도가 준비되면 알림을 받습니다.
        // 프래그먼트 매니저로부터 SupportMapFragment 를 얻습니다. getMapAsync() 메소드로 지도가 준비되면 알림을 받습니다.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationInit()// 2. 1에서 선언한 변수들을 onCreate()메소드의 끝에서 초기화 진행합니다.
    }
    /**
     위치 정보를 얻기 위한 각종 초기화 진행
     3. LocationRequest 는 위치 정보 요청에 대한 세부 정보를 설정합니다.
     여기에 설정하는 프로퍼티의 의미는 다음과 같습니다.
        +priority - 정확도
            PRIORITY_HIGH_ACCURACY - 가장 정확한 위치를 요청합니다.
            PRIORITY_BALANCED_POWER_ACCURACY - 블록 수준의 정확도를 요청합니다.
            PRIORITY_LOW_POWER - 도시 수준의 정확도를 요청합니다.
            PRIORITY_NO_POWER - 추가 전력 소모 없이 최상의 정확도를 요청합니다.
        +interval - 위치를 갱신하는 데 필요한 시간은 밀리초 단위로 입력합니다.
        +fastestInterval - 다른 앱에서 위치를 갱신했을 때 그 정보를 가장 빠른 간격(밀리초 단위)으로 입력합니다.
     이 요청은 GPS 를 사용하여 가장 정확한 위치를 요구하면서 10초마다 위치 정보를 갱신합니다.
     그 사이에 다른 앱에서 위치를 갱신했다면 5초마다 확인하여 그 값을 활용하여 배터리를 절약합니다.
    */
    private fun locationInit(){
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        locationCallback = MyLocationCallBack()
        locationRequest = LocationRequest()
        //GPS 우선
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        //업데이트 인터벌
        //위치 정보가 없을 때는 업데이트 안 함
        //상황에 따라 짧아질 수 있음, 정확하지 않음
        //다른 앱에서 짧은 인터벌로 위치 정보를 요청하면 짧아질 수 있음
        locationRequest.interval = 10000
        //정확함. 이것보다 짧은 업데이트는 하지 않음
        locationRequest.fastestInterval = 5000
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
        mMap = googleMap    //지도가 준비되면 GoogleMap 객체를 얻습니다.

        // Add a marker in Sydney and move the camera
        // 위도와 경도로 시드니의 위치를 정하고 구글 지도 객체에 마커를 추가하고 카메라를 이동합니다.
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onResume() {
        super.onResume()
        addLocationListener()
        //이러한 위치 요청은 액티비티가 활성화되는 onResume()메소드에서 수행하면 5번과 같이 별도의 메소드로 작성합니다.
        permissionCheck(cancel = {
            //위치 정보가 필요한 이유 다이얼로그 표시
            showPermissionInfoDialog()
        },ok={
            addLocationListener()
        })
    }

    //5. 이 부분은 권한 요청을 따로 해야 오류가 없습니다.
    @SuppressLint("MissingPermission")
    private fun addLocationListener(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
            locationCallback,
            null)
    }
    //6.requestLocationUpdates() 메소드에 전달되는 인자 중 LocationCallBack 을 구현한 내부 클래스는 LocationResult 객체를 반환하고
    // lastLocation 프로퍼티로 Location 객체를 얻습니다.
    inner class MyLocationCallBack : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            val location = locationResult?.lastLocation
            //기기의 GPS 설정이 꺼져 있거나 현재 위치 정보를 얻을 수 없는 경우에 Location 객체가 null 일 수 있습니다.
            //Location 객체가 null 이 아닐 때 해당 위도와 경도 위치로 카메라를 이동합니다.
            location?.run{
                //14 level 로 확대하고 현재 위치로 카메라 이동
                val latLng : LatLng = LatLng(latitude,longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f))

                Log.d("MapsActivity!!","위도 : $latitude,경도 : $longitude")
            }

        }
    }
    /**이 메소드는 함수 인자 두 개를 받습니다. 두 함수 모두 인자와 반환값이 없습니다.
     * 이전에 사용자가 권한 요청을 거부한 적이 있다면 cancel 함수를, 권한이 수락되었다면 ok 함수를 호출합니다.
     */
    private fun permissionCheck(cancel : () -> Unit, ok:()->Unit){
        //위치 권한이 있는지 검사
        if(ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //권한이 허용되지 않음
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                cancel()
            }
            else{
                //권한 요청
                ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_ACCESS_FINE_LOCATION)
            }
        }else{
            //권한을 수락했을 때 실행할 함수
            ok() //
        }
    }
    private fun showPermissionInfoDialog(){
        alertDialog {
            message = "권한이 필요한 이유"
            okButton{
                ActivityCompat.requestPermissions(this@MapsActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION)
            }
            cancelButton {  }
        }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_ACCESS_FINE_LOCATION ->{
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    //권한 허용됨
                    addLocationListener()
                }else{
                    //권한 거부
                    toast("권한 거부됨")
                }
                return
            }
        }
    }
    override fun onPause(){
        super.onPause()
        removeLocationListener()
    }
    private fun removeLocationListener(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}

