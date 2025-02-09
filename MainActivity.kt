package com.example.photogallery
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val imageUrls = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = ImageAdapter(imageUrls)

        fetchImages()
    }

    private fun fetchImages() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.unsplash.com/photos/random?count=30&client_id=K2JKAzmLZUdt3Izs_i2tRrbC6KFsqN9i0uSO8Ne7pj8")
            .build()

        thread {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonArray = JSONArray(it)
                    for (i in 0 until jsonArray.length()) {
                        val imageUrl = jsonArray.getJSONObject(i).getJSONObject("urls").getString("regular")
                        imageUrls.add(imageUrl)
                    }
                    runOnUiThread {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

class ImageAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ShapeableImageView = itemView.findViewById(R.id.imageView)

        fun bind(url: String) {
            imageView.load(url) {
                crossfade(true)
            }
        }
    }
}
