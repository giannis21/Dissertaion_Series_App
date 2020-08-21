package com.example.tvshows.ui.seen

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshows.R
import com.example.tvshows.data.ApiClient
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.tvshows.ui.adapters.tvShowGridItemDecoration
import com.example.tvshows.tvshows.ui.adapters.watchlistRecyclerViewAdapter
import com.example.tvshows.tvshows.ui.callbacks.ClickCallback

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
        viewModel = ViewModelProvider(this, viewModelFactory).get(SeenViewModel::class.java)
        val list: MutableList<TvShowDetails> = mutableListOf()

        recycler_view_seen.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1
            }
        }
        recycler_view_seen.layoutManager = gridLayoutManager
        adapter = watchlistRecyclerViewAdapter(this.requireContext(), list, this)
        recycler_view_seen.adapter = adapter
        recycler_view_seen.addItemDecoration(tvShowGridItemDecoration(15, 19))

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

    override fun onClick(menuItemView1: View, id: Int) {
        val ctw : Context = ContextThemeWrapper(context, R.style.popupTheme)
        val popup = PopupMenu(ctw, menuItemView1)

        popup.inflate(R.menu.popup_menu_seen)
        popup.show()

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.move_to_watchlist_menu_seen -> {
                    viewModel.moveFromSeenToWatchlist(id)
                    true
                }
                R.id.move_to_favorites_menu_seen -> {
                    viewModel.moveFromSeenToFavorites(id)
                    true
                }
                R.id.more_info_menu_seen -> {
                    val action = SeenFragmentDirections.actionSeenToShowDetailsFragment(id, "seen")
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }
    }

    override fun onDeleteIconClick(id: Int, name: String) {
        viewModel.deleteTvshow(id)
        context?.success_toast("$name succesfully deleted!")
    }

}