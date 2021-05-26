package com.example.tvshows.ui.nowplaying

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshows.ItemAdapter
import com.example.tvshows.MainActivity
import com.example.tvshows.R
import com.example.tvshows.data.ApiClient
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import com.example.tvshows.tvshows.SharedMethods.filterList
import com.example.tvshows.tvshows.ui.callbacks.GenresClickCallback
import com.example.tvshows.tvshows.ui.callbacks.OnFragmentNavigationListener
import com.example.tvshows.ui.seen.MainViewModel.Companion.listener_genres_clicked
import com.example.tvshows.ui.seen.MainViewModel.Companion.selected_genres
import com.example.tvshows.utils.Extension_Utils.Companion.hideKeyboard
import com.example.tvshows.utils.Extension_Utils.Companion.setGone
import com.example.tvshows.utils.Extension_Utils.Companion.setVisible
import kotlinx.android.synthetic.main.now_playing_fragment.*


class NowPlayingFragment : Fragment(), GenresClickCallback {

    companion object {
        var filter_listener: ((String) -> (Unit))?=null
        var pages_counter = 1
        private var list_allGenres = mutableListOf<Result_NowPlaying>()
        var navigate_listener: OnFragmentNavigationListener? = null
    }

    private lateinit var viewModelFactory: NowPlayingViewModelFactory
    private lateinit var viewModel: NowPlayingViewModel
    lateinit var adapter: ItemAdapter
    var isScrolling: Boolean = false
    var currentItems: Int = 0
    var totalItems = 0
    var scrollOutItems = 0
    lateinit var manager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val networkConnectionIncterceptor = this.context?.applicationContext?.let {
            NetworkConnectionIncterceptor(it)
        }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = NowPlayingViewModelFactory(repository, requireContext())

        return inflater.inflate(R.layout.now_playing_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as MainActivity).onBackPressedfunction()
        }
        (activity as? MainActivity)?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        pages_counter = 1
        val list: MutableList<Result_NowPlaying> = mutableListOf()
        listener_genres_clicked = this

        //-------Recyclerview--------//
        manager = LinearLayoutManager(this.context)
        recyclerview.layoutManager = manager
        recyclerview.setHasFixedSize(true)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NowPlayingViewModel::class.java)
        adapter = ItemAdapter(this.requireContext(), list, viewModel) { id ->
         context?.hideKeyboard(requireView())
            navigate_listener?.onFragmentNavigation(
                NowPlayingFragmentDirections.actionNowPlayingToShowDetailsFragment(
                    id,
                    "nowPlaying"
                )
            )
        }

        recyclerview.adapter = adapter

        //-------Viewmodel--------//


        viewModel.get_now_playing_per_page(pages_counter)
        addViewModelObservers()


        no_internet_message_now_playing.findViewById<ImageView>(R.id.internet_retry)
            .setOnClickListener {
                viewModel.get_now_playing_per_page(1)
            }
        arrow_up.setOnClickListener {
            manager.scrollToPositionWithOffset(0, 0);
        }
        filter_listener ={
            adapter.filter.filter(it)
        }
        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItems = manager.childCount //Είναι τα items τα οποία βλέπω στην οθόνη του χρήστη(τα visible)
                totalItems = manager.itemCount     //Είναι τα συνολικά items τα οποία έχει η λίστα
                scrollOutItems = manager.findFirstVisibleItemPosition()  //μετράει τα items τα οποία δεν φένονται καθώς σκρολλάρω προς τα κάτω..δηλαδή μου δείχνει τη θέση του πρώτου visible item

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {  //Αν αυτά που δεν βλέπω + αυτά που βλέπω στην οθόνη μου είναι ίσος με τον συνολικό αριθμό της λίστας

                    if (dx < dy) {
                        progressbar_bottom.setVisible()
                        isScrolling = false
                        viewModel.get_now_playing_per_page(++pages_counter)
                        Handler().postDelayed({
                            progressbar_bottom.setGone() //δειχνει την μπαρα φόρτωσης για 190 milliseconds όταν φτάνει στο τέλος της λίστας
                        }, 190)

                    }
                }
            }
        })

    }


    private fun addViewModelObservers() {
        viewModel.show?.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.insertIntoDb(it)
            }
        })

        viewModel.playingNowDb_.observe(viewLifecycleOwner, Observer {
            it?.let {
                submitListToAdapter(it)
                list_allGenres.addAll(it.resultNowPlayings)
            }
        })

        viewModel.connectivityProblem.observe(viewLifecycleOwner, Observer {
            if (it) {
                no_internet_message_now_playing.visibility = View.VISIBLE
            } else
                no_internet_message_now_playing.visibility = View.GONE
        })
    }

    private fun submitListToAdapter(it: NowPlaying) {

        if (selected_genres.isEmpty()) {
            adapter.submitList(it.resultNowPlayings, "nowPlayingFragment", "")
        }else {
            val filtered_list = filterList(list_allGenres)
            if (filtered_list.isEmpty())
                viewModel.get_now_playing_per_page(++pages_counter)
            else {
                val filteredList = filterList(list_allGenres)
                if (filteredList.isNotEmpty()){
                     adapter.submitList(filteredList, "nowPlayingFragment", "")
                }

            }
        }
    }

    override fun genreClicked() {

        if (selected_genres.isEmpty()){
            nowShowFoundNow?.visibility=View.GONE
            adapter.submitList(list_allGenres, "nowPlayingFragment", "")
        } else {
            val filteredList = filterList(list_allGenres)
            if (filteredList.isNotEmpty()) {
                adapter.clear()
                adapter.submitList(filteredList, "nowPlayingFragment", "")
                nowShowFoundNow?.visibility=View.GONE
            } else {
                nowShowFoundNow?.visibility=View.VISIBLE
                adapter.clear()
               // viewModel.get_now_playing_per_page(++pages_counter)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        pages_counter = 1
        manager.scrollToPositionWithOffset(0, 0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentNavigationListener) {
            navigate_listener = context
        }
    }
}

