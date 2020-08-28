package com.example.tvshows.ui.seen

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.tvshows.R
import com.example.tvshows.data.network.response.genres.Genre
import com.example.tvshows.tvshows.ui.callbacks.GenresClickCallback
import com.example.tvshows.tvshows.ui.show_details.ClickCallback
import com.example.tvshows.utils.Extension_Utils.Companion.error_toast
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


class MainViewModel(var context: Context) : ViewModel() {
    companion object {
        val genres_list = mutableListOf<Genre>()
        val selected_genres = mutableListOf<Genre>()
        var listener_genres_clicked: GenresClickCallback? = null
    }

    var counterOfSelected=0
    var counter_of_clicks=0
    fun loadJSONFromAsset(chipsPrograms: ChipGroup): String? {

        val json: String
        try {
            val input: InputStream = context.assets.open("Genres")
            json = input.bufferedReader().use { it.readText() }.toString()
            val jsonObj = JSONObject(json)
            val jsonArray = jsonObj.getJSONArray("genres")

            for (i in 0..jsonArray.length() - 1) {
                val jsonObject = jsonArray.getJSONObject(i)
                createDynamicChips(jsonObject.getString("id").toInt(),jsonObject.getString("name"),chipsPrograms)
            }

        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    @SuppressLint("InflateParams")
    private fun createDynamicChips(id: Int, name: String, chipsPrograms: ChipGroup) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mChip = inflater.inflate(R.layout.item_chip_category, null, false) as Chip

        genres_list.add(Genre(id, name))

        mChip.text = name
        mChip.tag = id.toString()
        mChip.id = id

        var flag_changeColor=0
        mChip.setOnClickListener {

            val genre= Genre(id, name)
            if(flag_changeColor==0 && ++counterOfSelected<=3){

                selected_genres.add(genre)
                mChip.chipIcon = (ContextCompat.getDrawable(context, R.drawable.ic_check))
                mChip.chipBackgroundColor = context.getColorStateList(R.color.chipChecked)
                flag_changeColor=1
                listener_genres_clicked?.genreClicked()
            }else  {

                selected_genres.remove(genre)
                mChip.chipIcon = null
                counterOfSelected--
                mChip.chipBackgroundColor = context.getColorStateList(R.color.colorPrimary)
                flag_changeColor=0
                listener_genres_clicked?.genreClicked()
            }

        }
        chipsPrograms.addView(mChip)
    }

}