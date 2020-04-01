package com.example.savefilesandroidqcoroutines

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savefilesandroidqcoroutines.data.ImageDownload
import com.example.savefilesandroidqcoroutines.ui.ImageAdapter
import com.example.savefilesandroidqcoroutines.utils.extensions.requestPermissions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageAdapterSetup()
    }

    private fun imageAdapterSetup(){
        val imageAdapter = ImageAdapter(this){ selectedImageUrl->

            requestPermissions(listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)){ granted->
                if (granted) {
                    val imageDownload = ImageDownload(this)
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            this@MainActivity,
                            "The image has been downloaded to ${imageDownload.download(selectedImageUrl)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Can't save image without storage permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
        imageRecyclerView.layoutManager = LinearLayoutManager(this)
        imageRecyclerView.adapter = imageAdapter
    }
}
