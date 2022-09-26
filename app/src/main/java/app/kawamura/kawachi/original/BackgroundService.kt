package app.kawamura.kawachi.original

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

class BackgroundService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: Location
    private lateinit var pref: SharedPreferences
    var totaltoday: Double = 0.000000
    private lateinit var currentLocation: LatLng

    val handler = Handler()
    val runnable: Runnable = object : Runnable {
        override fun run() {
            //一定周期で行いたいことを書く
            enableMyLocation()
            handler.postDelayed(this, 10000)
        }

    }

    override fun onCreate() {
        super.onCreate()
        pref = getSharedPreferences("SharedPref", MODE_PRIVATE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.post(runnable)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }


    fun enableMyLocation() {

                var prelatitude =
                    pref.getString("Latitude", location.latitude.toString())?.toDouble() ?: 0.0
                var prelongtitude =
                    pref.getString("Longitude", location.longitude.toString())?.toDouble() ?: 0.0
                var total: Double = 0.000000

                //total = (pref.getString("Distance", total.toString()))!!.toDouble()
                total = pref.getString("Distance", "0.0")?.toDouble() ?: 0.0

                if (prelatitude != null && prelongtitude != null) {
                    total += distance(
                        prelatitude.toDouble(),
                        prelongtitude.toDouble(),
                        location.latitude,
                        location.longitude
                    )
                    totaltoday += distance(
                        prelatitude.toDouble(),
                        prelongtitude.toDouble(),
                        location.latitude,
                        location.longitude
                    )

                    Log.d("road", "backgroundtotal" + total.toString())
                    Log.d(
                        "road",
                        "backgrounddistance" + distance(
                            prelatitude.toDouble(),
                            prelongtitude.toDouble(),
                            location.latitude,
                            location.longitude
                        ).toString()
                    )

                }
                Log.d("road", "latitude" + location.latitude.toString())
                Log.d("road", "longitude" + location.longitude.toString())
                //計算し終わったらpreの方に移動する
                prelatitude = location.latitude
                prelongtitude = location.longitude

                Log.d("road", "pre" + prelatitude.toString())
                Log.d("road", "pre" + prelongtitude.toString())

                //ここでそれぞれpreを保存する作業をする
                val editor = pref.edit()
                editor.putString("Latitude", prelatitude.toString())
                editor.putString("Longitude", prelongtitude.toString())
                editor.putString("Distance", total.toString())
                editor.apply()

            }

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
            var result = earth_radius * rr / 1000.0
            // 2点間の距離(キロメートル)
            return if (result == Double.NaN) 0.0 else result
        }
    }

