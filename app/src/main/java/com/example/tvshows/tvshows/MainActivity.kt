package com.example.tvshows


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.example.tvshows.utils.Extension_Utils.Companion.iSVisible
import com.example.tvshows.utils.Extension_Utils.Companion.setGone
import com.example.tvshows.utils.Extension_Utils.Companion.setVisible
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject

import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity(),NavController.OnDestinationChangedListener {
    private lateinit var appBarConfig: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpToolbar()

        appBarConfig = AppBarConfiguration(
            setOf(
                R.id.now_playing,
                R.id.Popular,
                R.id.Top_Rated,
                R.id.watchlist,
                R.id.favorites,
                R.id.seen,
                R.id.settings,
                R.id.searchFragment
            ), drawer_layout
        )


        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfig)
        nav_view.setupWithNavController(navController)
        loadJSONFromAsset()
    }



    @SuppressLint("InflateParams")
    fun loadJSONFromAsset(): String? {
        val json: String
        try {
            val input: InputStream = assets.open("Genres")
            json = input.bufferedReader().use { it.readText() }.toString()
            val jsonObj = JSONObject(json)
            val jsonArray = jsonObj.getJSONArray("genres")
            for (i in 0..jsonArray.length() - 1) {
                val jsonObject = jsonArray.getJSONObject(i)
                val mChip =
                    this.layoutInflater.inflate(R.layout.item_chip_category, null, false) as Chip
                mChip.text = jsonObject.getString("name")
                mChip.tag = jsonObject.getString("id")
                mChip.chipIcon = (ContextCompat.getDrawable(this, R.drawable.ic_check));
                mChip.setClickable(true)
                mChip.setFocusable(true)
                mChip.setPadding(0, 0, 0, 0)

                mChip.setOnCheckedChangeListener { compoundButton, _ ->
                    compoundButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                    Log.i("main11", "${compoundButton.tag}")
                }
                mChip.setOnClickListener {
                    Toast.makeText(this, "${it.tag}", Toast.LENGTH_SHORT).show()
                    it.isClickable = true
                    it.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_chip_state_list))
                }
                chipsPrograms.addView(mChip)
            }

        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfig)
                || super.onSupportNavigateUp()
    }

    override fun onStop() {
        super.onStop()
        genresId.setGone()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar_main)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setHomeButtonEnabled(false)
        toolbar_main.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        toolbar_main.inflateMenu(R.menu.main_menu)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> {
                if (findNavController(R.id.nav_host_fragment).currentDestination?.id != R.id.showDetailsFragment)
                    if (genresId.iSVisible())
                        genresId.setGone()
                    else
                        genresId.setVisible()
            }
            R.id.search -> {
                getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
                navigateBetweenFragments()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateBetweenFragments() {
        if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.now_playing) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_now_playing_to_searchFragment)
        } else if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.Popular) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_Popular_to_searchFragment)
        } else if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.Top_Rated) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_Top_Rated_to_searchFragment)
        } else if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.seen) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_seen_to_searchFragment)
        } else if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.watchlist) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_watchlist_to_searchFragment)
        } else if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.favorites) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_favorites_to_searchFragment)
        } else if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.settings) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_favorites_to_searchFragment)
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == R.id.showDetailsFragment)
            genresId.setGone()
    }

}
