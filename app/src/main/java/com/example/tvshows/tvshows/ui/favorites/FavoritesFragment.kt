package com.example.tvshows.ui.favorites

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshows.R
import com.example.tvshows.data.ApiClient
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.tvshows.ui.adapters.tvShowGridItemDecoration
import com.example.tvshows.tvshows.ui.watchlist.ClickCallback
import com.example.tvshows.tvshows.ui.adapters.watchlistRecyclerViewAdapter
import com.example.tvshows.ui.nowplaying.FavoritesViewModelFactory
import kotlinx.android.synthetic.main.favorites_fragment.*

class FavoritesFragment : Fragment() , ClickCallback {

    lateinit var adapter: watchlistRecyclerViewAdapter
    private lateinit var viewModelFactory: FavoritesViewModelFactory

    private lateinit var viewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            nested_favorites.background = context?.getDrawable(R.drawable.shr_product_grid_background_shape)
//        }

        val networkConnectionIncterceptor = this.context?.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = FavoritesViewModelFactory(repository, requireContext())
        return inflater.inflate(R.layout.favorites_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FavoritesViewModel::class.java)
        val list: MutableList<TvShowDetails> = mutableListOf()

        recycler_view_fav.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1
            }
        }
        recycler_view_fav.layoutManager = gridLayoutManager
        adapter =
            watchlistRecyclerViewAdapter(
                this.requireContext(),
                list,
                this
            )
        recycler_view_fav.adapter = adapter
        recycler_view_fav.addItemDecoration(
            tvShowGridItemDecoration(
                15,
                19
            )
        )

        viewModel.details.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it, "")
        })

        viewModel.countOfFav.observe(viewLifecycleOwner, Observer {
            if (it == 0)
                nowShowFound_fav.visibility = View.VISIBLE
            else
                nowShowFound_fav.visibility = View.GONE
        })
    }

    override fun onClick(menuItemView1: View, id: Int) {

     }

    override fun onDeleteIconClick(id: Int, name: String) {

    }

}