package com.example.tvshows

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import com.example.tvshows.databinding.ItemLayoutBinding
import com.example.tvshows.ui.nowplaying.NowPlayingViewModel
import kotlinx.android.synthetic.main.item_layout.view.*
import java.util.*

class ItemAdapter(
    var context: Context, var list: MutableList<Result_NowPlaying>,
    var viewmodel: NowPlayingViewModel? = null,
    var listener_callback: ((Int) -> (Unit))
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>(), Filterable {

    var currentSearchName = ""
    var fulllist = mutableListOf<Result_NowPlaying>()

    class ItemViewHolder(var recyclerviewNowPLayingbinding: ItemLayoutBinding) : RecyclerView.ViewHolder(recyclerviewNowPLayingbinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = DataBindingUtil.inflate<ItemLayoutBinding>(LayoutInflater.from(parent.context), R.layout.item_layout, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.recyclerviewNowPLayingbinding.nowPlaying = list[position]
        holder.recyclerviewNowPLayingbinding.currentSearchName = currentSearchName
        holder.itemView.image.setOnClickListener { listener_callback.invoke(list[position].id) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun submitList(
        newList: MutableList<Result_NowPlaying>,
        curr_fragment: String,
        currentSearchName: String
    ) {

        this.currentSearchName = currentSearchName
        if (curr_fragment.equals("searchFragment"))
            list.clear()

        newList.forEach {
            if (!list.contains(it)) {
                list.add(it)

                if (!fulllist.contains(it))
                    fulllist.add(it)
                notifyDataSetChanged()


            }
            viewmodel?.storeValues(it)
        }
    }

    fun clear() {
        fulllist.clear()
        list.clear()
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return SearchFilter
    }

    private val SearchFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults? {
            val filterList = mutableListOf<Result_NowPlaying>()
            if (constraint.isNullOrEmpty()) {
                filterList.addAll(fulllist)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
                fulllist.forEach { item ->
                    if (item.name.toLowerCase(Locale.ROOT).contains(filterPattern))
                        filterList.add(item)
                }
            }
            val results = FilterResults()
            results.values = filterList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            list.clear()
            list = results.values as MutableList<Result_NowPlaying>
            notifyDataSetChanged()
        }
    }
}

