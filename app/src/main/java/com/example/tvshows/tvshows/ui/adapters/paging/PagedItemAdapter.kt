package com.example.tvshows.tvshows.ui.adapters.paging

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.tvshows.R
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
 import com.example.tvshows.utils.Extension_Utils.Companion.setGone
import kotlinx.android.synthetic.main.item_layout.view.*
import java.lang.Exception
import java.util.*

class PagedItemAdapter(var context: Context,var query:String ="") : PagedListAdapter<Result_NowPlaying, PagedItemAdapter.MyViewHolder>(DiffUtilCallBack) {

    var listener_callback: ((Int) -> (Unit))?=null
    var listener_itemCount: ((Int) -> (Unit))?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it,listener_callback,this.query,context)
        }
    }

    override fun getItemCount(): Int {
        this.listener_itemCount?.invoke(super.getItemCount())
        return super.getItemCount()
    }

    @JvmName("setQuery1")
    fun setQuery(query11: String){
        this.query=query11
    }
    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(item: Result_NowPlaying, listener_callback: ((Int) -> Unit)?, query: String, context: Context){
            itemView.name.text= getSpannableStyling(item.name,query,context)
            itemView.image.setOnClickListener { listener_callback?.invoke(item.id) }
            setImage(item)
            itemView.rate_average.text =item.vote_average.toString()

        }

        private fun getSpannableStyling(tvshowName1:String,query:String,context:Context):Spannable{
            val tvshowName = tvshowName1.toLowerCase(Locale.ROOT)
            val tvshowNameSpan = SpannableString(tvshowName1)
            val length_of_word = query.length
            if(query != ""){

                return try {
                    val indexOf = tvshowName.indexOf(query.toLowerCase(Locale.ROOT))
                    if (indexOf >= 0) {
                        tvshowNameSpan.setSpan(BackgroundColorSpan(Color.parseColor("#bfaeae")),indexOf, length_of_word + indexOf, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        tvshowNameSpan.setSpan(ForegroundColorSpan(Color.BLACK), indexOf, length_of_word + indexOf, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvshowNameSpan.setSpan(TextAppearanceSpan(context, R.style.caption_bold), 0, tvshowName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        tvshowNameSpan
                    } else {
                        tvshowNameSpan.setSpan(TextAppearanceSpan(context, R.style.caption), 0, tvshowName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        tvshowNameSpan
                    }
                } catch (e: Exception) {
                    tvshowNameSpan.setSpan(TextAppearanceSpan(context, R.style.caption), 0, tvshowName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    tvshowNameSpan
                }
            }else{
                return SpannableString(tvshowName1)
            }
        }
        private fun setImage(item: Result_NowPlaying) {

            Glide.with(itemView.image.context).load("https://image.tmdb.org/t/p/w500/${item.backdrop_path}")
                .error(Glide.with(itemView.image.context).load(R.drawable.placeholder))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        itemView.progressBar.setGone()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        itemView.progressBar.setGone()
                        return false
                    }
                }).into(itemView.image)

        }
    }

    companion object {
        val DiffUtilCallBack = object : DiffUtil.ItemCallback<Result_NowPlaying>() {
            override fun areItemsTheSame(
                oldItem: Result_NowPlaying,
                newItem: Result_NowPlaying
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Result_NowPlaying,
                newItem: Result_NowPlaying
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}