package com.example.tvshows

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import com.example.tvshows.databinding.ItemLayoutBinding
import kotlinx.android.synthetic.main.item_layout.view.*

class ItemAdapter(var context: Context, var list: MutableList<Result_NowPlaying>, private var listener: RecyclerViewclick_Callback) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(var recyclerviewNowPLayingbinding: ItemLayoutBinding) : RecyclerView.ViewHolder(recyclerviewNowPLayingbinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = DataBindingUtil.inflate<ItemLayoutBinding>(LayoutInflater.from(parent.context), R.layout.item_layout, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        //holder.itemView.main_item_layout.animation=AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation)
        holder.recyclerviewNowPLayingbinding.nowPlaying=list[position]
        holder.itemView.image.setOnClickListener { listener.OnTvShowClick(list[position].id) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun submitList(newList: MutableList<Result_NowPlaying>, curr_fragment:String) {
        if(curr_fragment.equals("searchFragment"))
            list.clear()

        newList.forEach{
            if(!list.contains(it))
            {
                list.add(it)
                notifyDataSetChanged()
            }
        }
    }
    fun clear(){
        list.clear()
        notifyDataSetChanged()
    }

}