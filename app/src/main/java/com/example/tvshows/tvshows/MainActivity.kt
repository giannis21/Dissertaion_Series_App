package com.example.tvshows


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.tvshows.data.netMethods
import com.example.tvshows.tvshows.ui.callbacks.OnFragmentNavigationListener
import com.example.tvshows.tvshows.utils.PreferenceUtils.Companion.isFirstTime
import com.example.tvshows.tvshows.utils.PreferenceUtils.Companion.set_FirstTime
import com.example.tvshows.ui.mostpopular.MainModelFactory
import com.example.tvshows.ui.nowplaying.NowPlayingFragment
import com.example.tvshows.ui.seen.MainViewModel
import com.example.tvshows.utils.Extension_Utils.Companion.error_toast
import com.example.tvshows.utils.Extension_Utils.Companion.iSVisible
import com.example.tvshows.utils.Extension_Utils.Companion.info_toast
import com.example.tvshows.utils.Extension_Utils.Companion.setGone
import com.example.tvshows.utils.Extension_Utils.Companion.setVisible
import com.skydoves.balloon.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.banner_layout.view.*


class MainActivity : AppCompatActivity(), OnFragmentNavigationListener {

    // private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var viewModelFactory: MainModelFactory
    private lateinit var viewModel: MainViewModel
    private var frameLayout: FrameLayout? = null
    private var doubleClick: Boolean? = false
    private var showmessageOnlyOnce = false
    private var showHide = true
    private var topIconVisible = false
    private var searchcontainerOpened = false
    private var chipsContainerOpened = false
    private lateinit var motion_container :MotionLayout
    companion object {
        lateinit var showProgressBar: ((Boolean) -> Unit)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpToolbar()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setStatusBarColor()


        //motion_container  =motionLayoutId.findViewById<MotionLayout>(R.id.motion_main_container)
        toolbar_main.title = "Now Playing"
        showProgressBar = { showProgressBar ->
            if (showProgressBar) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }

        frameLayout = findViewById(R.id.frameLayout)
        viewModelFactory = MainModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        motionLayoutId.findViewById<ConstraintLayout>(R.id.constraintLayoutNavBar).setOnClickListener { }
//        appBarConfig = AppBarConfiguration(
//            setOf(
//                R.id.now_playing,
//                R.id.Popular,
//                R.id.Top_Rated,
//                R.id.watchlist,
//                R.id.favorites,
//                R.id.seen,
//                R.id.settings,
//                R.id.searchFragment
//            ), drawer_layout
//        )

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)





        //   setupActionBarWithNavController(navController, appBarConfig)
        //  nav_view.setupWithNavController(navController)
        viewModel.loadJSONFromAsset(chipsPrograms)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.showDetailsFragment || destination.id == R.id.seen ||
                destination.id == R.id.favorites || destination.id == R.id.watchlist || destination.id == R.id.settings || destination.id == R.id.searchFragment
            ) {
                if (genresId.iSVisible())
                    genresId.setGone()

                showHide = true
                searchcontainer.visibility = View.GONE
                moveMainContainer("up")
                moveMainContainerForChips("up")
                searchHereEdittext.isEnabled = false
                text_input_layout.visibility = View.GONE
            }
            if(destination.id == R.id.showDetailsFragment || destination.id == R.id.searchFragment){
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }else{
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            updateAppTitle(destination.id)
        }

        searchHereEdittext?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                NowPlayingFragment.filter_listener?.invoke(s.toString())
            }
        })



        searchHere.setOnClickListener {
            linearbtns.visibility = View.GONE
            text_input_layout.visibility = View.VISIBLE
            searchHereEdittext.isEnabled = true
        }
        searchOnline.setOnClickListener {
            searchHereEdittext.isEnabled = false
            searchcontainer.visibility = View.GONE
            moveMainContainer("up")
            goToSearchFragment()
        }

        setGlobalActionListeners()

        intent?.extras?.getString("id")?.let {
            val dataBundle = Bundle()
            dataBundle.putInt("id", it.toInt())
            dataBundle.putString("deriveFrom", "deepLink")

            navController.setGraph(R.navigation.mobile_navigation)
            navController.navigate(R.id.showDetailsFragment, dataBundle)
            toolbar_main.title = "Tv Show Details"
        } ?: kotlin.run {
            intent?.extras?.getString("deriveFrom")?.let {
                if(it == "deepLinkWatchlist"){
                    navController.setGraph(R.navigation.mobile_navigation)
                    navController.navigate(R.id.watchlist)
                    toolbar_main.title = "Watchlist"
                }

            }
        }

    }

    private fun updateAppTitle(id: Int) {
        when (id) {
            R.id.showDetailsFragment -> toolbar_main.title = "Tv Show Details"
            R.id.searchFragment -> toolbar_main.title = "Search Tv Shows"
            R.id.Popular -> toolbar_main.title = "Most Popular"
            R.id.now_playing -> toolbar_main.title = "Now Playing"
            R.id.Top_Rated -> toolbar_main.title = "Top Rated"
            R.id.watchlist -> toolbar_main.title = "Watchlist"
            R.id.favorites -> toolbar_main.title = "Favorites"
            R.id.seen -> toolbar_main.title = "Seen"
            R.id.settings -> toolbar_main.title = "Settings"
        }
    }

    private fun setGlobalActionListeners() {
        motionLayoutId.findViewById<TextView>(R.id.mostPopulartext).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_Popular)
        }
        motionLayoutId.findViewById<ImageView>(R.id.mostPopIcon).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_Popular)
        }
        motionLayoutId.findViewById<ImageView>(R.id.nowPlayingIcon).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_now_playing)
        }
        motionLayoutId.findViewById<TextView>(R.id.nowPlayingTxt).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_now_playing)
        }
        motionLayoutId.findViewById<ImageView>(R.id.TopRatedicon).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_Top_Rated)
        }
        motionLayoutId.findViewById<TextView>(R.id.TopRatedTxt).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_Top_Rated)
        }
        motionLayoutId.findViewById<ImageView>(R.id.watchlistIicon).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_watchlist)
        }
        motionLayoutId.findViewById<TextView>(R.id.watchlisttxt).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_watchlist)
        }
        motionLayoutId.findViewById<ImageView>(R.id.favoritesIcon).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_favorites)
        }
        motionLayoutId.findViewById<TextView>(R.id.favoritestxt).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_favorites)
        }
        motionLayoutId.findViewById<ImageView>(R.id.seenIcon).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_seen)
        }
        motionLayoutId.findViewById<TextView>(R.id.seentxt).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_seen)
        }
        motionLayoutId.findViewById<ImageView>(R.id.settingsIcon).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_settings)
        }
        motionLayoutId.findViewById<TextView>(R.id.settingstxt).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_settings)
        }
        motionLayoutId.findViewById<ImageView>(R.id.closeIcon).setOnClickListener {
            if(isFirstTime(this)){
                createBaloon()
                set_FirstTime(false,this)
            }

            topIconVisible = true
            motionLayoutId.visibility = View.GONE
            invalidateOptionsMenu() //ξανα καλειται η onPrepareOptionsMenu

            val id= motionLayoutId.findViewById<MotionLayout>(R.id.motionLayoutId)
            id?.transitionToStart()

        }
    }

    private fun createBaloon() {
        val balloon = createBalloon(this.applicationContext) {
            setArrowSize(15)
            setWidthRatio(0.98f)
            setWidth(BalloonSizeSpec.WRAP)
            setHeight(65)
            setArrowPosition(0.945f)
            setCornerRadius(4f)
            setTextSize(15f)
            setArrowOrientation(ArrowOrientation.TOP)
            setText("Here you can show side menu again!")
            setTextColorResource(android.R.color.background_dark)
            setBackgroundColorResource(R.color.textColorPrimary)
            setBalloonAnimation(BalloonAnimation.ELASTIC)
            setLifecycleOwner(lifecycleOwner)
        }
        include.showAlignBottom(balloon)
        balloon.dismissWithDelay(5000L)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val topMenuIcon = menu.findItem(R.id.TopMenuIcon)
        topMenuIcon.isVisible = topIconVisible
        return true
    }

    fun onBackPressedfunction() {
        if (!showmessageOnlyOnce) {
            this.info_toast("tab back again to exit!")
            showmessageOnlyOnce = true
        }

        if (doubleClick!!) {
            finish()
        }
        doubleClick = true
        Handler().postDelayed({
            doubleClick = false
            showmessageOnlyOnce = false
        }, 2000)

    }

    fun showBanner(value: String) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.banner_layout, null)

        runOnUiThread {
            frameLayout?.let { cLayout ->
                cLayout.addView(view, 0)
                cLayout.bringToFront()
                cLayout.redBannerTxtV.text = value
                Handler().postDelayed({
                    cLayout.removeView(view)
                }, 2000)
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
                || super.onSupportNavigateUp()
    }



    private fun setUpToolbar() {
        setSupportActionBar(toolbar_main)
        toolbar_main.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        toolbar_main.inflateMenu(R.menu.main_menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.TopMenuIcon -> {
                motionLayoutId.visibility = View.VISIBLE
                topIconVisible = false
                invalidateOptionsMenu()
            }
            R.id.filter -> {
                if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.now_playing
                    || findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.Top_Rated
                    || findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.Popular
                )

                    if (genresId.iSVisible()) {
                        genresId.setGone()
                        moveMainContainerForChips("up")
                    } else {
                        genresId.setVisible()
                        moveMainContainerForChips("down")
                    }
            }
            R.id.search_menu -> {
                if (correctFragment()) {
                    searchHereEdittext.isEnabled = false
                    if (showHide) {
                        moveMainContainer("down")
                        Handler().postDelayed({
                            searchcontainer.animate().alpha(1.0f)
                            linearbtns.visibility = View.VISIBLE
                            searchcontainer.visibility = View.VISIBLE
                            text_input_layout.visibility = View.GONE
                        }, 500)

                        showHide = false
                    } else {
                        moveMainContainer("up")
                        searchcontainer.animate().alpha(0.0f)
                        showHide = true
                    }
                } else {
                    showHide = true
                    searchcontainer.visibility = View.GONE
                    if(findNavController(R.id.nav_host_fragment).currentDestination?.id != R.id.searchFragment)
                        goToSearchFragment()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun moveMainContainer(s: String) {
        if (s == "up") {
            searchcontainer.visibility = View.GONE
            var offset = 0
            if (chipsContainerOpened)
                offset = 47.toPx

            ObjectAnimator.ofFloat(main_container, "translationY", offset.toFloat()).apply {
                duration = 1000
                addStateListener()
                start()
                searchcontainerOpened = false
            }

        } else {
            searchcontainer.visibility = View.VISIBLE
            var offset = 0
            if (chipsContainerOpened)
                offset = 47.toPx
            if(!searchcontainerOpened)
                offset+= 190
            ObjectAnimator.ofFloat(
                main_container, "translationY", 10f + offset).apply {
                duration = 1000
                addStateListener()
                start()
                searchcontainerOpened = true
            }
            searchHereEdittext.isEnabled = false
        }

    }

    private fun moveMainContainerForChips(s: String) {
        if (s == "up") {
            var offset = 0f
            if (searchcontainerOpened) {
                offset = 190f  + 10f  //searchcontainer.height.toFloat() =190
                searchcontainer.visibility = View.VISIBLE
            } else {
                searchcontainer.visibility = View.GONE
            }
            ObjectAnimator.ofFloat(main_container, "translationY", offset).apply {
                duration = 1000
                addStateListener()
                start()
                chipsContainerOpened = false
            }

        } else {
            var offset = 0f
            if (searchcontainerOpened) {
                offset = 190f + 10f //searchcontainer.height.toFloat() =190
                searchcontainer.visibility = View.VISIBLE
            } else {
                searchcontainer.visibility = View.GONE
            }
            ObjectAnimator.ofFloat(main_container, "translationY", offset + 47.toPx).apply {
                duration = 1000
                addStateListener()
                start()
                chipsContainerOpened = true
            }
            searchHereEdittext.isEnabled = false
        }

    }

    private fun ObjectAnimator.addStateListener() {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                findViewById<View>(R.id.search_menu).isEnabled = false
                findViewById<View>(R.id.filter).isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                findViewById<View>(R.id.search_menu).isEnabled = true
                findViewById<View>(R.id.filter).isEnabled = true
            }
        })
    }


    private fun correctFragment(): Boolean {
        if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.now_playing ||
            findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.Top_Rated ||
            findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.Popular) {
            return true
        }
        return false
    }

    private fun goToSearchFragment() {
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_searchFragment)
    }


    override fun onFragmentNavigation(actionID: NavDirections) {
        if (netMethods.hasInternet(this.applicationContext, true)) {
            findNavController(R.id.nav_host_fragment).navigate(actionID)
        } else {
            this.applicationContext?.error_toast("No internet connection!")
        }
    }

   private val Int.toPx: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun setStatusBarColor() {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
    }
}
