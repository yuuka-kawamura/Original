package app.kawamura.kawachi.original

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter( private val pictureList: List<Picture>): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image_picture)
        val name: TextView = view.findViewById(R.id.name_view)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item, viewGroup, false)
        return ViewHolder(view)
    }

   override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val picture = pictureList[position]
        viewHolder.image.setImageResource(picture.image)
        viewHolder.name.text = picture.name

    }
   /*override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val picture = pictureList[position]
       holder.binding.image.setImageResource(picture.image)
       holder.binding.name.text = picture.name

   }*/

    override fun getItemCount() = pictureList.size
}