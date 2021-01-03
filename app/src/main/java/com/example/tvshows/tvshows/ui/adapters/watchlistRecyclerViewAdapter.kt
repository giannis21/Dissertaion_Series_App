package com.example.tvshows.tvshows.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshows.R
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.databinding.FavoritesLayoutItemBinding
import com.example.tvshows.databinding.WatchlistLayoutItemBinding
import com.example.tvshows.tvshows.ui.callbacks.ClickCallback
import kotlinx.android.synthetic.main.favorites_layout_item.view.*
import kotlinx.android.synthetic.main.watchlist_layout_item.view.*
import kotlinx.android.synthetic.main.watchlist_layout_item.view.delete_icon

class watchlistRecyclerViewAdapter(var context: Context, private val list: MutableList<TvShowDetails>,var callback: ((View,TvShowDetails,ClickState) ->Unit)) : RecyclerView.Adapter<watchlistRecyclerViewAdapter.WatchlistCardViewHolder>() {

    private val WATCHLIST_TYPE = 1
    private val FAVORITES_TYPE = 2

    class WatchlistCardViewHolder : RecyclerView.ViewHolder {
        var watchlist_Binding: WatchlistLayoutItemBinding? = null
        var favorites_Binding: FavoritesLayoutItemBinding? = null

        constructor(binding: FavoritesLayoutItemBinding) : super(binding.root) {
            favorites_Binding = binding
        }

        constructor(binding: WatchlistLayoutItemBinding) : super(binding.root) {
            watchlist_Binding = binding
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position].currentFragment == "watchlist") {
            return WATCHLIST_TYPE
        } else
            return FAVORITES_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistCardViewHolder {
        val binding = DataBindingUtil.inflate<WatchlistLayoutItemBinding>(LayoutInflater.from(parent.context), R.layout.watchlist_layout_item, parent, false)

        if (viewType == WATCHLIST_TYPE) {
            return WatchlistCardViewHolder(binding)
        } else if (viewType == FAVORITES_TYPE) {
            val binding1 = DataBindingUtil.inflate<FavoritesLayoutItemBinding>(LayoutInflater.from(parent.context), R.layout.favorites_layout_item, parent, false)
            return WatchlistCardViewHolder(binding1)
        }

        return WatchlistCardViewHolder(binding)

    }

    override fun onBindViewHolder(holder: WatchlistCardViewHolder, position: Int) {
        if (holder.itemViewType == WATCHLIST_TYPE) {

            holder.watchlist_Binding?.tvShow = list[position]
            holder.itemView.watchlist_layout.setOnClickListener {
                callback.invoke(it,list[position],ClickState.MoreInfo)
            }
            holder.itemView.delete_icon.setOnClickListener {
                callback.invoke(it,list[position],ClickState.deleteIcon)
            }
            holder.itemView.notification_icon.setOnClickListener {
                callback.invoke(it,list[position],ClickState.NotificationClick)
            }

        } else if (holder.itemViewType == FAVORITES_TYPE) {

            holder.favorites_Binding?.tvShow = list[position]
            holder.itemView.fav_layout.setOnClickListener {
                 callback.invoke(it,list[position],ClickState.MoreInfo)
            }
            holder.itemView.delete_icon.setOnClickListener {
                callback.invoke(it,list[position],ClickState.deleteIcon)
             }

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

    fun underNotification(obj: TvShowDetails, b: Boolean) {
         list.find { it.id == obj.id }?.underNotification=b
         notifyDataSetChanged()
    }
}
