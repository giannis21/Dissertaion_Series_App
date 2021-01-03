package com.example.tvshows.ui.watchlist

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import java.text.SimpleDateFormat
import java.util.*


class WatchlistFragment : Fragment(), ReminderBroadcast.NotificationShow {

    var navigate_listener_watchlist: OnFragmentNavigationListener? = null
    var mConnection:ServiceConnection?=null


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
        val networkConnectionIncterceptor = this.context?.applicationContext?.let { NetworkConnectionIncterceptor(
            it
        ) }
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
        ReminderBroadcast.notificationListener=this
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
            adapter.submitList(it, "")
        })

        viewModel.countOfWatchlist.observe(viewLifecycleOwner, Observer {
            if (it == 0 && (adapter.itemCount == 1 || adapter.itemCount == 0))
                nowShowFound.visibility = View.VISIBLE
            else
                nowShowFound.visibility = View.GONE
        })
    }


    @SuppressLint("SimpleDateFormat")
    private fun handleClick(view: View, obj: TvShowDetails, handleName: ClickState) {
        when (handleName) {
            ClickState.MoreInfo -> {
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
                if(obj.underNotification){
                    obj.next_episode_to_air?.air_date?.let {
                        viewModel.underNotification(obj.id, false, obj.next_episode_to_air.air_date)
                        viewModel.update_exact_time_of_notification(obj.id, "")
                    }
                    updateNotificationStatus(obj, false)
                }else{
                    obj.next_episode_to_air?.air_date?.let {
                        viewModel.underNotification(obj.id, true, obj.next_episode_to_air.air_date)
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        //val dateOfNot= sdf.format(getDate(obj).time)

                        viewModel.update_exact_time_of_notification(
                            obj.id,
                            sdf.format(Calendar.getInstance().time)
                        )
                    }
                    updateNotificationStatus(obj, true)
                }

            }
        }
    }


    private fun updateNotificationStatus(tvShow: TvShowDetails, cancelOrStart: Boolean) {
        if (cancelOrStart) {
            val info = JsonObject()


            info.addProperty("id", tvShow.id)
            info.addProperty("release_date", tvShow.next_episode_to_air?.air_date)
            info.addProperty("name", tvShow.name)
            info.addProperty("episode", tvShow.next_episode_to_air?.episode_number)


            val intent1 = Intent(context, serviceManager::class.java).apply {
                putExtra("info",info.toString())

            }
            requireContext().startService(intent1)

        } else {
            cancelAlarm(tvShow)
        }
    }

    private fun getNotificationId(tvShow: TvShowDetails): Int {
        val id=tvShow.id.toString()
        val dateInMilli= SharedMethods.getDateInMilli(tvShow.next_episode_to_air?.air_date.toString())
        return id.toInt()+dateInMilli.toInt()
    }

    fun cancelAlarm(tvShow: TvShowDetails) {

        val intent = Intent(context, ReminderBroadcast::class.java)
        intent.action = tvShow.id.toString() + tvShow.next_episode_to_air?.air_date.toString()
        val alarmIntent = PendingIntent.getBroadcast(
            context,
            getNotificationId(tvShow),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmMgr = context?.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(alarmIntent)
    }
    private fun getDate(tvShow: TvShowDetails): Calendar {
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
        if(context is OnFragmentNavigationListener){
            navigate_listener_watchlist =context
        }
    }

    override fun notification_showed(id: String) {
        viewModel.update_exact_time_of_notification(id.toInt(), "1990-01-12 12:11:01") //βαζω μια περασμενη ημερομηνια ετσι ωστε να γινει disabled to button του notification
    }

}