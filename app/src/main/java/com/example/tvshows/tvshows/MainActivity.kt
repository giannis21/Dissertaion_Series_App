package com.example.tvshows


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.example.tvshows.ui.mostpopular.MainModelFactory
import com.example.tvshows.ui.seen.MainViewModel
import com.example.tvshows.ui.seen.MainViewModel.Companion.selected_genres
import com.example.tvshows.utils.Extension_Utils.Companion.error_toast
import com.example.tvshows.utils.Extension_Utils.Companion.iSVisible
import com.example.tvshows.utils.Extension_Utils.Companion.setGone
import com.example.tvshows.utils.Extension_Utils.Companion.setVisible
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(),NavController.OnDestinationChangedListener {
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var viewModelFactory: MainModelFactory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpToolbar()
        viewModelFactory = MainModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        appBarConfig = AppBarConfiguration(
            setOf(R.id.now_playing, R.id.Popular, R.id.Top_Rated, R.id.watchlist, R.id.favorites, R.id.seen, R.id.settings, R.id.searchFragment), drawer_layout)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfig)
        nav_view.setupWithNavController(navController)
        viewModel.loadJSONFromAsset(chipsPrograms)
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
        } else if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.showDetailsFragment) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_showDetailsFragment_to_searchFragment)
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == R.id.showDetailsFragment)
         applicationContext.error_toast("eeeee")
    }

}
