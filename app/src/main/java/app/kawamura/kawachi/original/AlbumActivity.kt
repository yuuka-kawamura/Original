package app.kawamura.kawachi.original

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kawamura.kawachi.original.databinding.ActivityAlbumBinding

class AlbumActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAlbumBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAlbumBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        val animalList = listOf<Picture>(
            Picture(R.drawable.tokyo, "東京駅"),
            Picture(R.drawable.tokyo, "東京駅")
            /*Picture(R.drawable.dog, "イヌ"),
            Picture(R.drawable.gorilla, "ゴリラ"),
            Picture(R.drawable.horse, "ウマ"),
        Picture(R.drawable.lion, "ライオン")*/
        )
        binding.recyclerView.adapter = CustomAdapter(animalList)

        binding.recyclerView.layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)

    }
}