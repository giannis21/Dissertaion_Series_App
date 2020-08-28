package com.example.tvshows.ui.seen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tvshows.R
import com.example.tvshows.data.ApiClient
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.tvshows.ui.adapters.watchlistRecyclerViewAdapter
import com.example.tvshows.tvshows.ui.callbacks.ClickCallback
import com.example.tvshows.tvshows.ui.seen.popUpMenu_topRated.showPopUpMenu_seen

import com.example.tvshows.ui.mostpopular.seenViewModelFactory
import com.example.tvshows.utils.Extension_Utils.Companion.success_toast

import kotlinx.android.synthetic.main.seen_fragment.*

class SeenFragment : Fragment(), ClickCallback {

    lateinit var adapter: watchlistRecyclerViewAdapter
    private lateinit var viewModelFactory: seenViewModelFactory
    private lateinit var viewModel: SeenViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val networkConnectionIncterceptor = this.context?.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = seenViewModelFactory(repository, requireContext())
        return inflater.inflate(R.layout.seen_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val list: MutableList<TvShowDetails> = mutableListOf()

        recycler_view_seen.setHasFixedSize(true)
        val manager = LinearLayoutManager(this.context)
        recycler_view_seen.layoutManager = manager
        adapter = watchlistRecyclerViewAdapter(this.requireContext(), list, this)
        recycler_view_seen.adapter = adapter

        viewModel = ViewModelProvider(this, viewModelFactory).get(SeenViewModel::class.java)
        viewModel.details.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it, "")
        })

        viewModel.countOfseen.observe(viewLifecycleOwner, Observer {
            if (it == 0 && (adapter.itemCount==1 ||  adapter.itemCount==0))
                nowShowFound_seen.visibility = View.VISIBLE
            else
                nowShowFound_seen.visibility = View.GONE
        })
    }

    override fun onClick(menuItemView1: View, obj: TvShowDetails) {
        showPopUpMenu_seen(context, menuItemView1, obj, viewModel)
    }

    override fun onDeleteIconClick(id: Int, name: String) {
        viewModel.deleteTvshow(id)
        context?.success_toast("$name succesfully deleted!")
    }

}