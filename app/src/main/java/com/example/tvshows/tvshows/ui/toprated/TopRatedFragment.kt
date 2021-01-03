package com.example.tvshows.ui.toprated

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshows.ItemAdapter
import com.example.tvshows.MainActivity
import com.example.tvshows.R
import com.example.tvshows.data.ApiClient
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.netMethods
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import com.example.tvshows.tvshows.SharedMethods
import com.example.tvshows.tvshows.ui.callbacks.GenresClickCallback
import com.example.tvshows.tvshows.ui.callbacks.OnFragmentNavigationListener
import com.example.tvshows.ui.mostpopular.MostPopularFragment
import com.example.tvshows.ui.nowplaying.NowPlayingFragment
import com.example.tvshows.ui.nowplaying.NowPlayingFragmentDirections
import com.example.tvshows.ui.nowplaying.NowPlayingViewModelFactory
import com.example.tvshows.ui.seen.MainViewModel
import com.example.tvshows.ui.seen.MainViewModel.Companion.listener_genres_clicked
import com.example.tvshows.utils.Extension_Utils.Companion.error_toast
import com.example.tvshows.utils.Extension_Utils.Companion.setGone
import com.example.tvshows.utils.Extension_Utils.Companion.setVisible
import com.example.tvshows.utils.Extension_Utils.Companion.success_toast
import kotlinx.android.synthetic.main.connectivity_layout.*
import kotlinx.android.synthetic.main.top_rated_fragment.*

class TopRatedFragment : Fragment(),GenresClickCallback{

    companion object {
        var pages_counterTopRated = 1
        private var list_allGenres= mutableListOf<Result_NowPlaying>()
        var navigate_listener_Top: OnFragmentNavigationListener?=null
    }

    lateinit var adapter: ItemAdapter
    private lateinit var viewModelFactory: NowPlayingViewModelFactory

    var isScrolling: Boolean = false
    var currentItems: Int = 0
    var totalItems = 0
    var scrollOutItems = 0
    lateinit var manager: LinearLayoutManager

    private lateinit var viewModel: TopRatedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val networkConnectionIncterceptor = this.context?.applicationContext?.let {
            NetworkConnectionIncterceptor(it)
        }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = NowPlayingViewModelFactory(repository, requireContext())
        return inflater.inflate(R.layout.top_rated_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listener_genres_clicked =this

        val list: MutableList<Result_NowPlaying> = mutableListOf()
        pages_counterTopRated = 1

        manager = LinearLayoutManager(this.context)
        recyclerview_topRated.layoutManager = manager
        recyclerview_topRated.setHasFixedSize(true)
        adapter = ItemAdapter(this.requireContext(), list){show_id ->
             navigate_listener_Top?.onFragmentNavigation(TopRatedFragmentDirections.actionTopRatedToShowDetailsFragment(show_id,"topRated"))
        }
        recyclerview_topRated.adapter = adapter

        viewModel = ViewModelProvider(this, viewModelFactory).get(TopRatedViewModel::class.java)
        viewModel.getTopRatedPerPage(pages_counterTopRated)

        viewModel.topRated.observe(viewLifecycleOwner, Observer {
            submitListToAdapter(it)
            list_allGenres.addAll(it.resultNowPlayings)
        })
        NowPlayingFragment.filter_listener ={
            adapter.filter.filter(it)
        }
        viewModel.connectivityProblem.observe(viewLifecycleOwner, Observer {
            if (it)
                no_internet_message_top_rated.visibility = View.VISIBLE
            else
                no_internet_message_top_rated.visibility=View.GONE
        })


        arrow_up_topRated.setOnClickListener {
            manager.scrollToPositionWithOffset(0, 0)
        }
        internet_retry.setOnClickListener {
            viewModel.getTopRatedPerPage(1)
         }
        recyclerview_topRated.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                        progressbar_bottom_topRated.setVisible()
                        isScrolling = false
                        viewModel.getTopRatedPerPage(++pages_counterTopRated)

                        Handler().postDelayed({
                            progressbar_bottom_topRated.setGone() //δειχνει την μπαρα φόρτωσης για 190 milliseconds όταν φτάνει στο τέλος της λίστας
                        }, 190)

                    }
                }
            }
        })
    }
    private fun submitListToAdapter(it: NowPlaying) {
        if (MainViewModel.selected_genres.isEmpty())
            adapter.submitList(it.resultNowPlayings, "topRated","")
        else {
            val filtered_list = SharedMethods.filterList(list_allGenres)
            if(filtered_list.isEmpty())
                viewModel.getTopRatedPerPage(++pages_counterTopRated)
            else {
                val filteredList = SharedMethods.filterList(list_allGenres)
                if (!filteredList.isEmpty())
                    adapter.submitList(filteredList, "topRated","")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as MainActivity).onBackPressedfunction()
        }
    }

    override fun genreClicked() {
        context?.success_toast("${MainViewModel.selected_genres.size}")
        if (MainViewModel.selected_genres.isEmpty())
            adapter.submitList(list_allGenres,"topRated","")
        else {
            val filteredList= SharedMethods.filterList(list_allGenres)
            if(!filteredList.isEmpty()){
                adapter.clear()
                adapter.submitList(filteredList,"topRated","")
            }else {
                adapter.clear()
                viewModel.getTopRatedPerPage(++pages_counterTopRated)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        pages_counterTopRated = 1
        manager.scrollToPositionWithOffset(0, 0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnFragmentNavigationListener){
            navigate_listener_Top=context
        }
    }

}