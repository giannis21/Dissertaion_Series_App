package com.example.tvshows.ui.watchlist

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.*
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshows.MainActivity
import com.example.tvshows.R
import com.example.tvshows.data.ApiClient
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.tvshows.SharedMethods
import com.example.tvshows.tvshows.SharedMethods.listOfActiveNot
import com.example.tvshows.tvshows.ui.adapters.ClickState
import com.example.tvshows.tvshows.ui.adapters.tvShowGridItemDecoration
import com.example.tvshows.tvshows.ui.adapters.watchlistRecyclerViewAdapter
import com.example.tvshows.tvshows.ui.callbacks.OnFragmentNavigationListener
import com.example.tvshows.tvshows.ui.favorites.PopUpMenuStates
import com.example.tvshows.tvshows.ui.watchlist.ReminderBroadcast
import com.example.tvshows.tvshows.ui.watchlist.popUpMenu_watchlist.showPopUpMenu
import com.example.tvshows.tvshows.ui.watchlist.serviceManager
import com.example.tvshows.tvshows.utils.PreferenceUtils
import com.example.tvshows.ui.nowplaying.NowPlayingViewModelFactory
import com.example.tvshows.utils.Extension_Utils.Companion.success_toast
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.watchlist_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class WatchlistFragment : Fragment(), ReminderBroadcast.NotificationShow {

    var navigate_listener_watchlist: OnFragmentNavigationListener? = null
    var mConnection: ServiceConnection? = null


    lateinit var adapter: watchlistRecyclerViewAdapter
    private lateinit var viewModelFactory: NowPlayingViewModelFactory
    private lateinit var viewModel: WatchlistViewModel
    lateinit var obj: TvShowDetails
    private var foregroundService: serviceManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val networkConnectionIncterceptor = this.context?.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = NowPlayingViewModelFactory(repository, requireContext())
        return inflater.inflate(R.layout.watchlist_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as MainActivity).onBackPressedfunction()
        }

        viewModel = ViewModelProvider(this, viewModelFactory).get(WatchlistViewModel::class.java)
        val list: MutableList<TvShowDetails> = mutableListOf()
        ReminderBroadcast.notificationListener = this
        recycler_view.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1
            }
        }
        recycler_view.layoutManager = gridLayoutManager
        adapter = watchlistRecyclerViewAdapter(this.requireContext(), list) { view, tvShowDetails, handleName ->
            handleClick(view, tvShowDetails, handleName)
        }

        recycler_view.adapter = adapter
        recycler_view.addItemDecoration(tvShowGridItemDecoration(15, 19))

        viewModel.details.observe(viewLifecycleOwner, Observer {
            var stopService=false
            var isThereNotificationExpired=false
            var isThereActiveNotification=false
             lifecycleScope.launch(Dispatchers.Default) {
                it.forEach {

                    val dateOfNot = it.exactDateOfNotification
                    println("test ${it.name} --- ${it.underNotification}--- ${it.exactDateOfNotification}")
                    if (it.underNotification && (SharedMethods.getDateInMilli1(dateOfNot) < Calendar.getInstance().timeInMillis)  && !it.next_episode_to_air?.air_date.isNullOrEmpty()  && dateOfNot != "") {
                        isThereNotificationExpired=true
                    }
                    if (it.underNotification && (SharedMethods.getDateInMilli1(dateOfNot) > Calendar.getInstance().timeInMillis)  && !it.next_episode_to_air?.air_date.isNullOrEmpty()  && dateOfNot != "") {
                        isThereActiveNotification=true
                    }
                }
                 if(isThereActiveNotification && !isThereNotificationExpired){
                     stopService=false
                 }else if(!isThereActiveNotification && isThereNotificationExpired){
                     stopService=true
                 }else
                     stopService = !(isThereActiveNotification && isThereNotificationExpired)

                if (stopService) {
                    if (isServiceRunning(serviceClass = serviceManager::class.java)) {
                        val intent1 = Intent(context, serviceManager::class.java)
                        requireContext().stopService(intent1)
                    }
                }
            }
            adapter.submitList(it, "")
        })

        viewModel.countOfWatchlist.observe(viewLifecycleOwner, Observer {
            if (it == 0 && (adapter.itemCount == 1 || adapter.itemCount == 0)) {
                nowShowFound.visibility = View.VISIBLE
                if (isServiceRunning(serviceClass = serviceManager::class.java)){
                    val intent1 = Intent(context, serviceManager::class.java)
                    requireContext().stopService(intent1)
                }
            } else
                nowShowFound.visibility = View.GONE
        })
        fab_watchlist.setOnClickListener {
            Handler().postDelayed({ showPopUp(fab_watchlist ) }, 200)
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun handleClick(view: View, obj: TvShowDetails, handleName: ClickState) {
        when (handleName) {
            ClickState.MoreInfo -> {
                Handler().postDelayed({ showPopUp(fab_watchlist ) }, 200)
                showPopUpMenu(context, view) { state ->
                    when (state) {
                        PopUpMenuStates.moveToSeen -> viewModel.moveToSeen(obj)
                        PopUpMenuStates.move_to_favorites -> viewModel.moveToFavorites(obj)
                        else -> findNavController().navigate(
                            WatchlistFragmentDirections.actionWatchlistToShowDetailsFragment(
                                obj.id,
                                "watchList"
                            )
                        )

                    }
                }
            }
            ClickState.deleteIcon -> {
                viewModel.deleteTvshow(obj.id)
                context?.success_toast("${obj.name} succesfully deleted!")
            }
            else -> {
                if (obj.underNotification) {
                    obj.next_episode_to_air?.air_date?.let {
                        viewModel.underNotification(obj.id, false, obj.next_episode_to_air.air_date)
                        viewModel.update_exact_time_of_notification(obj.id, "")
                    }
                    updateNotificationStatus(obj, false)
                } else {
                    obj.next_episode_to_air?.air_date?.let {
                        viewModel.underNotification(obj.id, true, obj.next_episode_to_air.air_date)
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        //val dateOfNot= sdf.format(getDate(obj).time)
                        viewModel.update_exact_time_of_notification(obj.id, sdf.format(getExactDateOfNotification(obj).time))
                        // viewModel.update_exact_time_of_notification(obj.id, sdf.format(Calendar.getInstance().time))
                    }
                    updateNotificationStatus(obj, true)
                }

            }
        }
    }


    private fun updateNotificationStatus(tvShow: TvShowDetails, cancelOrStart: Boolean) {
        val info = JsonObject()
        info.addProperty("id", tvShow.id)
        info.addProperty("release_date", tvShow.next_episode_to_air?.air_date)
        info.addProperty("name", tvShow.name)
        info.addProperty("episode", tvShow.next_episode_to_air?.episode_number)
        val intent1 = Intent(context, serviceManager::class.java).apply {
            putExtra("info", info.toString())
        }
        if (cancelOrStart) {
            if (!listOfActiveNot.contains(info.toString()))
                listOfActiveNot.add(info.toString())

            if (isServiceRunning(serviceClass = serviceManager::class.java))
                requireContext().stopService(intent1)

            ContextCompat.startForegroundService(requireContext(),intent1)

        } else {
            if (listOfActiveNot.contains(info.toString()))
                listOfActiveNot.removeAt(listOfActiveNot.indexOf(info.toString()))

            cancelAlarm(tvShow)
         }
    }
    private fun showPopUp(view: View, location: Rect? =null) {

        val popUpView: View = layoutInflater.inflate(R.layout.banner_layout, null)
        lateinit var mpopup: PopupWindow
        mpopup = PopupWindow(popUpView, RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT, true) // Creation of popup

        mpopup.animationStyle = android.R.style.Animation_Dialog
        mpopup.showAtLocation(fab_watchlist, Gravity.TOP, 0, 0) // Displaying popup

        dimBehind(mpopup)
        popUpView.findViewById<ConstraintLayout>(R.id.BannerConstraint).background = ContextCompat.getDrawable(requireContext(), R.drawable.info_elevation_color)

        popUpView.findViewById<View>(R.id.imageView).visibility=View.GONE
        val messageTxt = popUpView.findViewById<View>(R.id.redBannerTxtV) as TextView
        messageTxt.text = "If notification button does not exist, you cannot set notification for the show. If it is disabled try changing the hour of notification to be shown through settings"

        val handler = Handler()
        handler.postDelayed(
            {
                mpopup.dismiss()
            },
            9000
        )

    }
    fun dimBehind(popupWindow: PopupWindow) {
        val container = popupWindow.contentView.rootView
        val context: Context = popupWindow.contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.7f
        wm.updateViewLayout(container, p)
    }
    @SuppressWarnings("deprecation")
    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }

    private fun getNotificationId(tvShow: TvShowDetails): Int {
        val id = tvShow.id.toString()
        val dateInMilli = SharedMethods.getDateInMilli(tvShow.next_episode_to_air?.air_date.toString())
        return id.toInt() + dateInMilli.toInt()
    }

    fun cancelAlarm(tvShow: TvShowDetails) {

        val intent = Intent(context, ReminderBroadcast::class.java)
        intent.action = tvShow.id.toString() + tvShow.next_episode_to_air?.air_date.toString()
        val alarmIntent = PendingIntent.getBroadcast(context, getNotificationId(tvShow), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmMgr = context?.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(alarmIntent)
    }

    private fun getExactDateOfNotification(tvShow: TvShowDetails): Calendar {
        val preferredTime = PreferenceUtils.get_preferred_time(requireContext())
        val newdate = tvShow.next_episode_to_air?.air_date.toString().split('-')

        val preferredTime1 = preferredTime!!.split(":")
        val time = preferredTime1[0]
        val minutes = preferredTime1[1]
        val year = newdate[0]
        val month = newdate[1]
        val day = newdate[2]

        val myAlarmDate: Calendar = Calendar.getInstance()
        myAlarmDate.set(Calendar.YEAR, year.toInt())
        myAlarmDate.set(Calendar.MONTH, month.toInt() - 1) //ο μηνας ξεκιναει απο 0
        myAlarmDate.set(Calendar.DAY_OF_MONTH, day.toInt())
        myAlarmDate.set(Calendar.HOUR_OF_DAY, time.toInt())
        myAlarmDate.set(Calendar.MINUTE, minutes.toInt())
        myAlarmDate.set(Calendar.SECOND, 10)
        return myAlarmDate
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentNavigationListener) {
            navigate_listener_watchlist = context
        }
    }

    override fun notification_showed(id: String, release_date: String) {
        viewModel.update_exact_time_of_notification(id.toInt(), "1990-01-12 12:11:01") //βαζω μια περασμενη ημερομηνια ετσι ωστε να γινει disabled to button του notification
        listOfActiveNot.firstOrNull { it.contains(id)}.let { show ->
            listOfActiveNot.remove(show)
        }
        viewModel.underNotification(id.toInt(), false, release_date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listOfActiveNot.clear()
    }
}