package app.kawamura.kawachi.original

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import app.kawamura.kawachi.original.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.properties.Delegates


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mainMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var currentLocation: LatLng //LatLngとは座標のこと
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: Location
    //private var total by Delegates.notNull<Double>()

    val handler = Handler()
    var total = 0.00000

    //時間ごとに繰り返す作業
    val runnable: Runnable = object : Runnable {
        override fun run() {
            //一定周期で行いたいことを書く

            if (isPermissionGranted()) {
                // rearlatitude=
                enableMyLocation()
                //Log.d("road",rearlatitude.toString())

            }
            handler.postDelayed(this, 10000)
        }

    }

    // ユーザーがパーミッションダイアログを操作した時
    @RequiresApi(Build.VERSION_CODES.N)
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // 正確な位置情報が許可された時
                    enableMyLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // おおよその位置情報が許可された時
                    enableMyLocation()
                }
                else -> {
                    // パーミッションが拒否されてしまった時
                }
            }
        }

    // 位置情報の権限が許可されているかどうかを確認するメソッド
    private fun isPermissionGranted(): Boolean =
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> true
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> true
            else -> false
        }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestPermission() {
        // 既に許可されていれば権限のリクエストを行わない
        if (isPermissionGranted()) return

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ユーザーに位置情報の許可を送る
        requestPermission()

        // SupportMapFragmentを取得してマップが使用できる状態になったときに通知を受け取ることができる
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        handler.post(runnable)

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
    override fun onMapReady(googleMap: GoogleMap) {
        mainMap = googleMap

        /*  // Add a marker in Sydney and move the camera
          val sydney = LatLng(36.0, 140.0)
          mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
          mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

        //  if (isPermissionGranted()) {
        //    enableMyLocation()
        //}
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        // 位置情報を有効にする
        mainMap.isMyLocationEnabled = true

        //現在位置を取得する
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location == null) return@addOnSuccessListener

                //現在地にカメラが移動する
                currentLocation = LatLng(location.latitude, location.longitude)
                Log.d("road", location.latitude.toString())
                Log.d("road", location.longitude.toString())
                mainMap.addMarker(
                    MarkerOptions().position(currentLocation).title("Marker in Current")
                )
                mainMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))

                //保存してたpreを使う 何も入っていない場合はlocation.を入れる
                val pref: SharedPreferences =
                    getSharedPreferences("SharedPref", MODE_PRIVATE)
                var prelatitude = pref.getString("Latitude", location.latitude.toString())
                var prelongtitude = pref.getString("Longitude", location.longitude.toString())

                if (prelatitude != null) {
                    if (prelongtitude != null) {
                        total += distance(
                            prelatitude.toDouble(),
                            prelongtitude.toDouble(),
                            location.latitude,
                            location.longitude
                        )

                        Log.d(
                            "road",
                            distance(
                                prelatitude.toDouble(),
                                prelongtitude.toDouble(),
                                location.latitude,
                                location.longitude
                            ).toString()
                        )
                    }
                }
                binding.totalDistance.text = total.toString()
                //計算し終わったらpreの方に移動する
                prelatitude = location.latitude.toString()
                prelongtitude = location.longitude.toString()

                Log.d("road", prelatitude.toString())
                Log.d("road", prelongtitude.toString())

                //ここでそれぞれpreを保存する作業をする
                val editor = pref.edit()
                editor.putString("Latitude", prelatitude.toString())
                editor.putString("Longitude", prelongtitude.toString())
                editor.apply()

            }
    }

    // 球面三角法により、キロメートルを求める
    fun distance(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Double {

        // 緯度経度をラジアンに変換
        val rlat1 = Math.toRadians(lat1)
        val rlng1 = Math.toRadians(lng1)
        val rlat2 = Math.toRadians(lat2)
        val rlng2 = Math.toRadians(lng2)

        // 2点の中心角(ラジアン)を求める
        val a = Math.sin(rlat1) * Math.sin(rlat2) +
                Math.cos(rlat1) * Math.cos(rlat2) *
                Math.cos(rlng1 - rlng2)
        val rr = Math.acos(a)

        // 地球赤道半径(メートル)
        val earth_radius = 6378140.0

        // 2点間の距離(キロメートル)
        return earth_radius * rr / 1000
    }
}