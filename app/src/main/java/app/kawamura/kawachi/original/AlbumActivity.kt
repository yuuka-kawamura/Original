package app.kawamura.kawachi.original

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kawamura.kawachi.original.databinding.ActivityAlbumBinding

class AlbumActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAlbumBinding
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAlbumBinding.inflate(layoutInflater).apply { setContentView(this.root) }
        pref = getSharedPreferences("SharedPref", MODE_PRIVATE)
        var total = pref.getString("Distance", "0.0")?.toDouble() ?: 0.0

        var one=R.drawable.tokyo
        var two=R.drawable.okuooi
        var three=R.drawable.hananuki
        var four=R.drawable.hananuki
        var five=R.drawable.hananuki
        var six=R.drawable.hananuki
        var seven=R.drawable.hananuki
        var eight=R.drawable.hananuki
        var nine=R.drawable.hananuki
        var ten=R.drawable.hananuki
        var eleven=R.drawable.hananuki
        var twelve=R.drawable.hananuki
        var thirteen=R.drawable.hananuki
        var fourteen=R.drawable.hananuki

        if(0<=total)
            one=R.drawable.tokyostation
        if(225<=total)
            one=R.drawable.okuooi
        if(393<=total)
            one=R.drawable.sakusima
        if(681<=total)
            one=R.drawable.hasikuiiwa
        if(1111<=total)
            one=R.drawable.titihaha
        if(1480<=total)
            one=R.drawable.hinata
        if(1747<=total)
            one=R.drawable.kawauchi
        if(2141<=total)
            one=R.drawable.wasiu
        if(2735<=total)
            one=R.drawable.hakumai
        if(3555<=total)
            one=R.drawable.titihaha
        if(4064<=total)
            one=R.drawable.titihaha
        if(5139<=total)
            one=R.drawable.titihaha
        if(5267<=total)
            one=R.drawable.titihaha
        if(5447<=total)
            one=R.drawable.titihaha




        val pictureList = listOf<Picture>(
            Picture(one, "東京駅"),
            Picture(two, "奥大井湖上駅"),
            Picture(three, "佐久島"),
            Picture(four, "橋杭岩"),
            Picture(five, "父母ヶ浜"),
            Picture(six, "日向海岸"),
            Picture(seven, "河内藤園"),
            Picture(eight, "鷲羽山展望台"),
            Picture(nine, "白米千枚田"),
            Picture(ten, "鶴の舞橋"),
            Picture(eleven, "富良野"),
            Picture(twelve, "大内宿"),
            Picture(thirteen, "花貫渓谷"),
            Picture(fourteen, "日本一周")

        )


        /*
        if(225<=total)
            one=R.drawable.tokyo
        */

        binding.recyclerView.adapter = CustomAdapter(pictureList)

        binding.recyclerView.layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)

        val Toolbar: Toolbar
        Toolbar= binding.toolbar.apply {
            setSupportActionBar(this)
        }

        supportActionBar!!.setTitle("アルバム")
        supportActionBar!!.setDisplayShowHomeEnabled(true);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}