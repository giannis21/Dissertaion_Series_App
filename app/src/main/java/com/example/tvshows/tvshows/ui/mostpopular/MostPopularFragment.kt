package com.example.tvshows.ui.mostpopular

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
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
import com.example.tvshows.utils.Extension_Utils.Companion.setGone
import com.example.tvshows.utils.Extension_Utils.Companion.setVisible
import kotlinx.android.synthetic.main.connectivity_layout.*
import kotlinx.android.synthetic.main.most_popular_fragment.*

class MostPopularFragment : Fragment(), RecyclerViewclick_Callback {
    companion object {
        var pages_counterP = 1
    }

    lateinit var adapter: ItemAdapter
    private lateinit var viewModelFactory: MostPopularViewModelFactory
    private lateinit var viewModel: MostPopularViewModel
    var isScrolling: Boolean = false
    var currentItems: Int = 0
    var totalItems = 0
    var scrollOutItems = 0
    lateinit var manager:LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val networkConnectionIncterceptor = this.context?.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = MostPopularViewModelFactory(repository, requireContext())
        return inflater.inflate(R.layout.most_popular_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val list: MutableList<Result_NowPlaying> = mutableListOf()
        manager = LinearLayoutManager(this.context)
        recyclerview_popular.layoutManager = manager
        recyclerview_popular.setHasFixedSize(true)
        adapter = ItemAdapter(this.requireContext(), list, this)
        recyclerview_popular.adapter = adapter


        viewModel = ViewModelProvider(this, viewModelFactory).get(MostPopularViewModel::class.java)
         viewModel.getMostPopularPerPage(1)
        pages_counterP = 1

        viewModel.popular.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.resultNowPlayings,"MostPopular")
        })
        viewModel.countOfNowPLaying.observe(viewLifecycleOwner, Observer {
            if (it == 0 && adapter.itemCount==0)
                no_internet_message_popular.visibility = View.VISIBLE
            else
                no_internet_message_popular.visibility=View.GONE
        })

        arrow_up_popular.setOnClickListener {
            manager.scrollToPositionWithOffset(0, 0)
        }

         internet_retry.setOnClickListener {
             viewModel.getMostPopularPerPage(1)

        }
        recyclerview_popular.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                        progressbar_bottom_popoular.setVisible()
                        isScrolling = false
                        viewModel.getMostPopularPerPage(++pages_counterP)

                        Handler().postDelayed({
                            progressbar_bottom_popoular.setGone() //δειχνει την μπαρα φόρτωσης για 190 milliseconds όταν φτάνει στο τέλος της λίστας
                        }, 190)

                    }
                }
            }
        })


    }

    override fun onStop() {
        super.onStop()
        pages_counterP = 1
    }

    override fun onStart() {
        super.onStart()
        pages_counterP = 1
        manager.scrollToPositionWithOffset(0, 0)
    }

    override fun OnTvShowClick(id: Int) {
        val action = MostPopularFragmentDirections.actionPopularToShowDetailsFragment(id,"mostPopular")
        findNavController().navigate(action)
    }



}