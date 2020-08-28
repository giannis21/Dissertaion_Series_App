package com.example.tvshows.ui.favorites

import android.os.Bundle
import android.util.Log
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
import com.example.tvshows.tvshows.ui.callbacks.ClickCallback
import com.example.tvshows.tvshows.ui.adapters.watchlistRecyclerViewAdapter
import com.example.tvshows.tvshows.ui.favorites.popUpMenu_favorites.showPopUpMenu_fav
import com.example.tvshows.ui.nowplaying.FavoritesViewModelFactory
import com.example.tvshows.utils.Extension_Utils.Companion.success_toast
import kotlinx.android.synthetic.main.favorites_fragment.*

class FavoritesFragment : Fragment() , ClickCallback {

    lateinit var adapter: watchlistRecyclerViewAdapter
    private lateinit var viewModelFactory: FavoritesViewModelFactory

    private lateinit var viewModel: FavoritesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val networkConnectionIncterceptor = this.context?.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = FavoritesViewModelFactory(repository, requireContext())
        return inflater.inflate(R.layout.favorites_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val list: MutableList<TvShowDetails> = mutableListOf()

        recycler_view_fav.setHasFixedSize(true)
        val manager = LinearLayoutManager(this.context)
        recycler_view_fav.layoutManager = manager
        adapter = watchlistRecyclerViewAdapter(this.requireContext(), list, this)
        recycler_view_fav.adapter = adapter


        viewModel = ViewModelProvider(this, viewModelFactory).get(FavoritesViewModel::class.java)
        viewModel.details.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it, "")
            it.forEach{
               Log.i("aaaa,","${it.currentFragment}  ${it.id}")
            }
        })

        viewModel.countOfFav.observe(viewLifecycleOwner, Observer {
            if (it == 0 && (adapter.itemCount==1 ||  adapter.itemCount==0))
                nowShowFound_fav.visibility = View.VISIBLE
            else
                nowShowFound_fav.visibility = View.GONE
        })
    }


    override fun onClick(menuItemView1: View, obj: TvShowDetails) {
        showPopUpMenu_fav(context, menuItemView1, obj, viewModel)
    }

    override fun onDeleteIconClick(id: Int, name: String) {
        viewModel.deleteTvshow(id)
        context?.success_toast("$name succesfully deleted!")
    }
}