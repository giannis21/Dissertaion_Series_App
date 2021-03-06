package com.example.tvshows

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.tvshows.data.network.response.details.CreatedBy
import com.example.tvshows.data.network.response.details.Genre
import com.example.tvshows.data.network.response.details.Season
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.tvshows.SharedMethods.getDateInMilli
import com.example.tvshows.tvshows.SharedMethods.getDateInMilli1
import com.example.tvshows.tvshows.ui.show_details.ClickCallback
import com.example.tvshows.tvshows.utils.PreferenceUtils
import com.example.tvshows.utils.Extension_Utils.Companion.setGone
import timber.log.Timber
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


object bindingAdapters {

    var listener: ClickCallback? = null


    @BindingAdapter(value = ["spannableInSearch", "currentSearchName"])
    @JvmStatic
    fun TextView.spannableInSearch(tvshowName1: String?, currentSearchName: String) {
        if (currentSearchName != "") {
            val tvshowName = tvshowName1?.toLowerCase()
            val tvshowNameSpan = SpannableString(tvshowName1)
            val length_of_word = currentSearchName.length
            try {
                val indexOf = tvshowName?.indexOf(currentSearchName.toLowerCase())!!
                if (indexOf >= 0) {
                    tvshowNameSpan.setSpan(
                        BackgroundColorSpan(Color.WHITE),
                        indexOf,
                        length_of_word + indexOf,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    tvshowNameSpan.setSpan(
                        ForegroundColorSpan(Color.BLACK),
                        indexOf,
                        length_of_word + indexOf,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    tvshowNameSpan.setSpan(
                        TextAppearanceSpan(context, R.style.caption_bold),
                        0,
                        tvshowName.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    this.text = tvshowNameSpan
                } else {
                    tvshowNameSpan.setSpan(
                        TextAppearanceSpan(context, R.style.caption),
                        0,
                        tvshowName.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    this.text = tvshowNameSpan
                }
            } catch (e: Exception) {
                tvshowNameSpan.setSpan(
                    TextAppearanceSpan(context, R.style.caption),
                    0,
                    tvshowName?.length!!,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.text = tvshowNameSpan
            }
        } else {
            this.text = tvshowName1
        }

    }


    @BindingAdapter("imageUrl", "progressbar")
    @JvmStatic
    fun loadImage(view: ImageView, url: String?, progressBar: ProgressBar) {

        Glide.with(view.context).load("https://image.tmdb.org/t/p/w500/$url")
            .error(Glide.with(view.context).load(R.drawable.placeholder))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.setGone()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.setGone()
                    return false
                }
            }).into(view)
    }

    @BindingAdapter("imageDetails", "progressBar_details")
    @JvmStatic
    fun loadImageDetails(view: ImageView, url: String?, progressBar: ProgressBar) {

        Glide.with(view.context).load("https://image.tmdb.org/t/p/w500/$url")
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.setGone()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.setGone()
                    return false
                }
            })
            .error(Glide.with(view.context).load(R.drawable.clapperboard)).into(view)
    }

    @BindingAdapter("imageWatchlist")
    @JvmStatic
    fun loadWatchlistImage(view: ImageView, url: String?) {

        Glide.with(view.context).load("https://image.tmdb.org/t/p/w500/$url")

            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .error(Glide.with(view.context).load(R.drawable.clapperboard)).into(view)
    }

    @BindingAdapter("formatDate")
    @JvmStatic
    fun changeformatDate(view: TextView, date: String?) {
        var newDate: String = "-"

        if (!date.isNullOrEmpty()) {
            val date1 = date.split("-")
            newDate = date1[2] + "/" + date1[1] + "/" + date1[0]
        }
        view.text = newDate
    }

    @BindingAdapter("Stringifnull")
    @JvmStatic
    fun setString(view: TextView, overview: String?) {
        var over = ""
        if (view.id == R.id.status_)
            over = "Status: (-)"
        else
            over = "-"

        if (!overview.isNullOrEmpty())
            if (view.id == R.id.status_)
                over = "Status: $overview"
            else
                over = overview

        view.setText(over)
    }

    @BindingAdapter("statusifnull")
    @JvmStatic
    fun setstatus(view: TextView, overview: String?) {

        var over = "Status: (-)"

        if (!overview.isNullOrEmpty())
            over = "Status: $overview"

        view.setText(over)
    }

    @BindingAdapter("genres")
    @JvmStatic
    fun loadgenres(view: TextView, genres: List<Genre>?) {
        var genr = "-"
        if (!genres.isNullOrEmpty()) {
            genr = ""
            genres.indices.forEach { i ->
                genr += genres[i].name
                if (i < genres.size - 1)
                    genr += " | "
            }

        }

        view.setText(genr)   //apla bazo ta eidi tis seiras kai an den iparxo0n bazo "-"
    }

    @BindingAdapter("createdBy")
    @JvmStatic
    fun loadcreators(view: TextView, creators: List<CreatedBy>?) {
        var creator = "-"
        if (!creators.isNullOrEmpty()) {
            creator = ""
            creators.indices.forEach { i ->
                creator += creators[i].name
                if (i < creators.size - 1)
                    creator += ","
            }
        }
        view.setText(creator)   //apla bazo ta eidi tis seiras kai an den iparxo0n bazo "-"
    }

    @BindingAdapter("updateDrawable")
    @JvmStatic
    fun updateDrawable(view: ImageView, tvShowDetails: TvShowDetails) {

        if (tvShowDetails.underNotification)
            view.setColorFilter(ContextCompat.getColor(view.context, R.color.white))
        else
            view.setColorFilter(ContextCompat.getColor(view.context, R.color.colorPrimary))

        if (tvShowDetails.exactDateOfNotification != "") {
            val dateOfNot = tvShowDetails.exactDateOfNotification
           //an ???????? ?????? ???????????????? ???????????? ???????????????? ?????? ?? ???????????????????? ???????? ????????????????????
            if ((getDateInMilli1(dateOfNot) < Calendar.getInstance().timeInMillis) || tvShowDetails.next_episode_to_air?.air_date.isNullOrEmpty()) {
                view.setColorFilter(ContextCompat.getColor(view.context, R.color.notification_stoped))
                view.isEnabled = false
                view.visibility=View.GONE
            } else {
                view.isEnabled = true
            }
        } else {
            if (tvShowDetails.next_episode_to_air == null) { //???? ?????? ???????? ???????????????????? ?????? ???????????? ???? ?????????? ???? ???????????? ???? icon ?????? ??????????????????????
                view.setColorFilter(ContextCompat.getColor(view.context, R.color.notification_stoped))
                view.isEnabled = false
                view.visibility=View.GONE
            }

            tvShowDetails.next_episode_to_air?.let {  //?????? ?????????????? ?????????????? ???? ???????????? ?? ?????? ???? ???????? ?????? ?????? ???????????? ???????? ??????????????????

                val preferredTime1 = PreferenceUtils.get_preferred_time((view.context))!!.split(":")
                val time = preferredTime1[0]
                val minutes = preferredTime1[1]
                val temp = tvShowDetails.next_episode_to_air.air_date + " " + time + ":" + minutes + ":10" //???????????? ?????? ???????????????????? ?????? ?????????????? ???? ?????????????????? ?????? ???????????????? ?????? ?????? ?????? ?????? ???????????? ?????? ???????? ??????????????????

                if (getDateInMilli1(temp) < Calendar.getInstance().timeInMillis) {
                    view.setColorFilter(ContextCompat.getColor(view.context, R.color.notification_stoped))
                    view.isEnabled = false
                }
            }

        }


    }


    @BindingAdapter("inflateData", "nested_scrollview")
    @JvmStatic
    fun inflateData(layout: LinearLayout, data: List<Season>?, nestedScrollView: NestedScrollView) {
        var maxHeight = 0

        if (!data.isNullOrEmpty() && data.size > 0) {


            val startPoint = if (data[0].season_number == 0) 1 else 0

            if (data.size > 4) {    //?????????? ?????? ???? ???????? ?????? ???? ???????? ???? layout ???? ???? seasons
                maxHeight = 450
            } else {
                if (startPoint == 1)   //???????????? ?????????????? ???????????? ???????????????? ?????? ???? season 0 ?????????? ?????? StartPoint=1 ???????????????? ?????? ?????? ???? ?????????????????????? ???? 1?? ???????????????? ?????? ????????????
                    maxHeight = (data.size - 1) * 170
                else
                    maxHeight = data.size * 170
            }
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, maxHeight).apply {
                weight = 1.0f
                gravity = Gravity.CENTER
                setMargins(0, 0, 0, 0)
            }

            val layoutParams =
                ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, maxHeight)
            nestedScrollView.layoutParams = layoutParams


            for (i in startPoint until data.size) {
                val seasons_Txt1 = TextView(layout.context)
                val params1: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1.0f
                    gravity = Gravity.CENTER
                    setMargins(0, 20, 0, 0)
                }

                seasons_Txt1.layoutParams = params1
                seasons_Txt1.id = View.generateViewId()
                seasons_Txt1.setBackgroundColor(
                    ContextCompat.getColor(
                        layout.context,
                        R.color.colorPrimary
                    )
                )
                seasons_Txt1.setTextColor(
                    ContextCompat.getColor(
                        layout.context,
                        R.color.textColorPrimary
                    )
                )
                seasons_Txt1.setPadding(3, 3, 3, 3)

                if (startPoint == 0)
                    seasons_Txt1.text = String.format(
                        layout.context.getString(
                            R.string.season,
                            Integer.toString(i + 1)
                        )
                    )
                else
                    seasons_Txt1.text = String.format(
                        layout.context.getString(
                            R.string.season,
                            Integer.toString(i)
                        )
                    )

                layout.addView(seasons_Txt1)
//--------------------------------------------------------------------------------------------
                val seasons_Txt2 = TextView(layout.context)
                seasons_Txt2.background = ContextCompat.getDrawable(
                    layout.context,
                    R.drawable.bottomcircled_bordered_details
                )
                seasons_Txt2.id = View.generateViewId()
                seasons_Txt2.setPadding(3, 3, 3, 3)
                seasons_Txt2.setBackgroundColor(
                    ContextCompat.getColor(
                        layout.context,
                        R.color.colorAccent
                    )
                )
                seasons_Txt2.setTextColor(ContextCompat.getColor(layout.context, R.color.white))
                seasons_Txt2.text = String.format(
                    layout.context.getString(
                        R.string.episodes,
                        data.get(i).episode_count.toString()
                    )
                )
                layout.addView(seasons_Txt2)
                layout.setPadding(30, 20, 30, 30)

                seasons_Txt1.setOnClickListener {
                    sendSeasonOverview(data[i].overview)
                }
                seasons_Txt2.setOnClickListener {
                    sendSeasonOverview(data[i].overview)

                }
            }

        }
    }

    private fun sendSeasonOverview(overview: String?) {
        if (overview == null)
            listener?.seasonClicked("")
        else
            listener?.seasonClicked(overview)

    }
}
