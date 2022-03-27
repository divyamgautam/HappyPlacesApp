package com.example.happyplacesapp.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplacesapp.R
import com.example.happyplacesapp.models.HappyPlaceModel
import kotlinx.android.synthetic.main.happy_place.view.*

open class HappyPlacesAdapter(
    private val context: Context,
    private var list: ArrayList<HappyPlaceModel>
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListen: onClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.happy_place, parent, false    )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is myViewHolder){
            holder.itemView.ivPlaceImage.setImageURI(Uri.parse(model.image))
            holder.itemView.tvTitle.text = model.title
            holder.itemView.tvDescription.text = model.description
        }
    }

    fun setOnClickListener(onClickListener: onClickListener){
        this.onClickListen = onClickListener
    }

    interface onClickListener{
        fun onClick(position:Int, model: HappyPlaceModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    private class myViewHolder(view: View): RecyclerView.ViewHolder(view)

}