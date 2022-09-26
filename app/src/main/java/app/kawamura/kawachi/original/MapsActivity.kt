package app.kawamura.kawachi.original

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.time.LocalDate


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mainMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var currentLocation: LatLng //LatLngとは座標のこと
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: Location
    private lateinit var pref: SharedPreferences
    var totaltoday: Double = 0.000000
    var total1: Double = 0.000000
    var total2: Double = 0.000000
    var total3: Double = 0.000000
    var total4: Double = 0.000000
    var total5: Double = 0.000000
    var total6: Double = 0.000000

    //private var total by Delegates.notNull<Double>()

    val handler = Handler()
    var total = 0.00000

    //時間ごとに繰り返す作業
    val runnable: Runnable = object : Runnable {
        override fun run() {
            //一定周期で行いたいことを書く

            if (isPermissionGranted()) {
                enableMyLocation()
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
        //保存してたpreを使う 何も入っていない場合はlocation.を入れる
        pref = getSharedPreferences("SharedPref", MODE_PRIVATE)


        // SupportMapFragmentを取得してマップが使用できる状態になったときに通知を受け取ることができる
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        handler.post(runnable)
        binding.albumButton.setOnClickListener {
            //1.Intentを作る
            val toAlbumActivityIntent = Intent(this, AlbumActivity::class.java)
            //2.Intentの設定（今回はなし）
            //3.Intentを使った画面遷移
            startActivity(toAlbumActivityIntent)
        }

        var date = "2022,10,1"
        totaltoday = pref.getString("total0", "0.0")?.toDouble() ?: 0.0
        total1 = pref.getString("total1", "0.0")?.toDouble() ?: 0.0
        total2 = pref.getString("total2", "0.0")?.toDouble() ?: 0.0
        total3 = pref.getString("total3", "0.0")?.toDouble() ?: 0.0
        total4 = pref.getString("total4", "0.0")?.toDouble() ?: 0.0
        total5 = pref.getString("total5", "0.0")?.toDouble() ?: 0.0
        total6 = pref.getString("total6", "0.0")?.toDouble() ?: 0.0
        date = pref.getString("date", "").toString()

        if (date != LocalDate.now().toString()) {
            total6 = total5
            total5 = total4
            total4 = total3
            total3 = total2
            total2 = total1
            total1 = totaltoday
            totaltoday = 0.0
        }

        //chartのコンポーネントを取得
        val chart: BarChart = findViewById(R.id.bar_chart);

        //グラフのデータを設定
        val value1: ArrayList<BarEntry> = ArrayList()
        value1.add(BarEntry(0F, total6.toFloat()))
        value1.add(BarEntry(1F, total5.toFloat()))
        value1.add(BarEntry(2F, total4.toFloat()))
        value1.add(BarEntry(3F, total3.toFloat()))
        value1.add(BarEntry(4F, total2.toFloat()))
        value1.add(BarEntry(5F, total1.toFloat()))
        value1.add(BarEntry(6F, totaltoday.toFloat()))

        //chartに設定
        val dataSet1 = BarDataSet(value1, "総距離")
        dataSet1.color = R.color.sub

        val dataSets: MutableList<IBarDataSet> = ArrayList()
        dataSets.add(dataSet1)

        chart.data = BarData(dataSets)
        chart.invalidate() // refresh
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
                //カメラの一を調整
                mainMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                //カメラのzoom機能を調整　数が大きいほどアップになる
                mainMap.moveCamera(CameraUpdateFactory.zoomTo(13.0f))


                var prelatitude = pref.getString("Latitude", "0.0")?.toDouble() ?: 0.0
                var prelongtitude = pref.getString("Longitude", "0.0")?.toDouble() ?: 0.0
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
                binding.totalDistance.text = total.toString()
                //計算し終わったらpreの方に移動する
                prelatitude = location.latitude
                prelongtitude = location.longitude

                Log.d("road", prelatitude.toString())
                Log.d("road", prelongtitude.toString())

                //ここでそれぞれpreを保存する作業をする
                val editor = pref.edit()
                editor.putString("Latitude", prelatitude.toString())
                editor.putString("Longitude", prelongtitude.toString())
                editor.putString("Distance", total.toString())
                editor.putString("total0", totaltoday.toString())
                editor.putString("total1", total1.toString())
                editor.putString("total2", total2.toString())
                editor.putString("total3", total3.toString())
                editor.putString("total4", total4.toString())
                editor.putString("total5", total5.toString())
                editor.putString("total6", total6.toString())
                editor.putString("date", LocalDate.now().toString())
                editor.apply()
                calculation()
            }
    }

    fun calculation() {

        when {
            5267 <= total -> {
                binding.placeText.text = "東京駅"
                binding.remainText.text = (5447 - total).toString()
            }
            5139 <= total -> {
                binding.placeText.text = "花貫渓谷"
                binding.remainText.text = (5267 - total).toString()
            }
            4064 <= total -> {
                binding.placeText.text = "大内宿"
                binding.remainText.text = (5139 - total).toString()
            }
            3555 <= total -> {
                binding.placeText.text = "富良野"
                binding.remainText.text = (4064 - total).toString()
            }
            2735 <= total -> {
                binding.placeText.text = "鶴の舞橋"
                binding.remainText.text = (3555 - total).toString()
            }
            2141 <= total -> {
                binding.placeText.text = "白米千枚田"
                binding.remainText.text = (2735 - total).toString()
            }
            1747 <= total -> {
                binding.placeText.text = "鷲羽山展望台"
                binding.remainText.text = (2141 - total).toString()
            }
            1480 <= total -> {
                binding.placeText.text = "河内藤園"
                binding.remainText.text = (1747 - total).toString()
            }
            1111 <= total -> {
                binding.placeText.text = "日向海岸"
                binding.remainText.text = (1480 - total).toString()
            }
            681 <= total -> {
                binding.placeText.text = "父母ヶ浜"
                binding.remainText.text = (1111 - total).toString()
            }
            393 <= total -> {
                binding.placeText.text = "橋杭岩"
                binding.remainText.text = (681 - total).toString()
            }
            225 <= total -> {
                binding.placeText.text = "佐久島"
                binding.remainText.text = (393 - total).toString()
            }
            0 <= total -> {
                binding.placeText.text = "奥大井湖上駅"
                binding.remainText.text = (225 - total).toString()

            }
        }

        /* val targetplaceList = listOf<Place>(
             Place("奥大井湖上駅", 225),
             Place("佐久島", 393),
             Place("橋杭岩", 681),
             Place("父母ヶ浜", 1111),
             Place("日向海岸", 1480),
             Place("河内藤園", 1747),
             Place("鷲羽山展望台", 2141),
             Place("白米千枚田", 2735),
             Place("鶴の舞橋", 3555),
             Place("富良野", 4064),
             Place("大内宿", 5139),
             Place("花貫渓谷", 5267),
             Place("東京駅", 5447)

         )*/
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
        var result = earth_radius * rr / 1000.0
        // 2点間の距離(キロメートル)
        return if (result == Double.NaN) 0.0 else result
    }
}