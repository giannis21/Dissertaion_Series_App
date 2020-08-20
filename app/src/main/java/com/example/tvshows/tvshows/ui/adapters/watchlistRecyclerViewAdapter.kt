package com.example.tvshows.tvshows.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshows.R
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.databinding.FavoritesLayoutItemBinding
import com.example.tvshows.databinding.WatchlistLayoutItemBinding
import com.example.tvshows.tvshows.ui.watchlist.ClickCallback
import kotlinx.android.synthetic.main.watchlist_layout_item.view.*

class watchlistRecyclerViewAdapter(var context: Context, private val list: MutableList<TvShowDetails>, private var listener: ClickCallback) : RecyclerView.Adapter<watchlistRecyclerViewAdapter.WatchlistCardViewHolder>() {

    private val WATCHLIST_TYPE = 1
    private val FAVORITES_TYPE = 2
    // class WatchlistCardViewHolder(var recyclerviewbinding: WatchlistLayoutItemBinding) : RecyclerView.ViewHolder(recyclerviewbinding.root)

    class WatchlistCardViewHolder : RecyclerView.ViewHolder {
         var watchlist_Binding: WatchlistLayoutItemBinding? = null
         var favorites_Binding: FavoritesLayoutItemBinding? = null

        constructor(binding: FavoritesLayoutItemBinding):super(binding.root){
            favorites_Binding=binding
        }
        constructor(binding: WatchlistLayoutItemBinding):super(binding.root){
            watchlist_Binding=binding
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(list[position].currentFragment.equals("watchlist")){
            return WATCHLIST_TYPE
        }else
            return FAVORITES_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistCardViewHolder {
        val binding = DataBindingUtil.inflate<WatchlistLayoutItemBinding>(LayoutInflater.from(parent.context), R.layout.watchlist_layout_item, parent, false)

        if (viewType == WATCHLIST_TYPE) {
             return WatchlistCardViewHolder(
                 binding
             )
         } else if (viewType == FAVORITES_TYPE) {
             val binding1 = DataBindingUtil.inflate<FavoritesLayoutItemBinding>(LayoutInflater.from(parent.context), R.layout.favorites_layout_item, parent, false)
             return WatchlistCardViewHolder(
                 binding1
             )
         }

         return WatchlistCardViewHolder(
             binding
         )

    }

    override fun onBindViewHolder(holder: WatchlistCardViewHolder, position: Int) {
        if(holder.itemViewType == WATCHLIST_TYPE){
            holder.watchlist_Binding?.tvShow = list[position]
            holder.itemView.watchlist_layout.setOnClickListener {
                listener.onClick(it, list[position].id)
            }
            holder.itemView.delete_icon.setOnClickListener {
                listener.onDeleteIconClick(list[position].id, list[position].name)
            }
        }else if(holder.itemViewType == FAVORITES_TYPE){
            holder.favorites_Binding?.tvShow = list[position]
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun submitList(newList: MutableList<TvShowDetails>, curr_fragment: String) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
