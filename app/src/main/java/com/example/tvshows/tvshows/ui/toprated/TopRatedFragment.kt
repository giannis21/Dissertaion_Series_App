package com.example.tvshows.ui.toprated

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshows.ItemAdapter
import com.example.tvshows.R
import com.example.tvshows.RecyclerViewclick_Callback
import com.example.tvshows.data.ApiClient
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import com.example.tvshows.tvshows.ui.callbacks.showConnectivityError
import com.example.tvshows.ui.mostpopular.TopRatedViewModelFactory
import com.example.tvshows.utils.Extension_Utils.Companion.setGone
import com.example.tvshows.utils.Extension_Utils.Companion.setVisible
import kotlinx.android.synthetic.main.connectivity_layout.*
import kotlinx.android.synthetic.main.top_rated_fragment.*

class TopRatedFragment : Fragment(), RecyclerViewclick_Callback {

    companion object {
        var pages_counterTopRated = 1
    }

    lateinit var adapter: ItemAdapter
    private lateinit var viewModelFactory: TopRatedViewModelFactory

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
        viewModelFactory = TopRatedViewModelFactory(repository, requireContext())
        return inflater.inflate(R.layout.top_rated_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val list: MutableList<Result_NowPlaying> = mutableListOf()
        pages_counterTopRated = 1

        manager = LinearLayoutManager(this.context)
        recyclerview_topRated.layoutManager = manager
        recyclerview_topRated.setHasFixedSize(true)
        adapter = ItemAdapter(this.requireContext(), list, this)
        recyclerview_topRated.adapter = adapter

        viewModel = ViewModelProvider(this, viewModelFactory).get(TopRatedViewModel::class.java)

        viewModel.getTopRatedPerPage(pages_counterTopRated)


        viewModel.topRated.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.resultNowPlayings,"MostPopular")
        })

        viewModel.countOfNowPLaying.observe(viewLifecycleOwner, Observer {
            if (it == 0 && adapter.itemCount==0)
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

    override fun onStart() {
        super.onStart()
        pages_counterTopRated = 1
        manager.scrollToPositionWithOffset(0, 0)
    }

    override fun OnTvShowClick(id: Int) {
        val action = TopRatedFragmentDirections.actionTopRatedToShowDetailsFragment(id,"topRated")
        findNavController().navigate(action)
    }

}