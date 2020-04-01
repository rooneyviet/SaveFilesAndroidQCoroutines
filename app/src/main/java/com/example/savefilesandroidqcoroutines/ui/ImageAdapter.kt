package com.example.savefilesandroidqcoroutines.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.savefilesandroidqcoroutines.R
import com.example.savefilesandroidqcoroutines.utils.extensions.click
import com.example.savefilesandroidqcoroutines.utils.extensions.loadNormalImage
import com.example.savefilesandroidqcoroutines.utils.extensions.visible
import com.example.savefilesandroidqcoroutines.utils.helloGifs
import kotlinx.android.synthetic.main.image_item_layout.view.*

class ImageAdapter(private val mContext: Context,
                   private val selectImage: (selectedUrlString: String) -> Unit)
    :RecyclerView.Adapter<ImageAdapter.ImageViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageAdapter.ImageViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val view: View = LayoutInflater.from(mContext)
            .inflate(R.layout.image_item_layout, parent, false)
        viewHolder = ImageViewHolder(view)

        return viewHolder
    }

    override fun getItemCount(): Int = helloGifs.size

    override fun onBindViewHolder(holder: ImageAdapter.ImageViewHolder, position: Int) {
        val imageUrl = helloGifs[position]

        holder.itemView.imagePreview.loadNormalImage(imageUrl, onLoadingFinished = {
            holder.itemView.copyUrlButton.visible()
            holder.itemView.saveImageButton.visible()
        })

        holder.itemView.copyUrlButton.click {
            val clipboard = mContext?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager;
            val clip = ClipData.newPlainText("label", imageUrl);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(mContext, "The URL has been copied ${imageUrl}", Toast.LENGTH_SHORT).show()
        }

        holder.itemView.saveImageButton.click {
            selectImage.invoke(imageUrl)
        }

    }

    class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }
}