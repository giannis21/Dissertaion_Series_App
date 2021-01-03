package com.example.tvshows.ui.seen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tvshows.MainActivity
import com.example.tvshows.R
import com.example.tvshows.data.ApiClient
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.netMethods
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.tvshows.ui.adapters.ClickState
import com.example.tvshows.tvshows.ui.adapters.watchlistRecyclerViewAdapter
import com.example.tvshows.tvshows.ui.callbacks.OnFragmentNavigationListener
import com.example.tvshows.tvshows.ui.favorites.PopUpMenuStates
import com.example.tvshows.tvshows.ui.seen.popUpMenu_topRated.showPopUpMenu_seen
import com.example.tvshows.ui.favorites.FavoritesFragment
import com.example.tvshows.ui.favorites.FavoritesFragmentDirections

import com.example.tvshows.ui.nowplaying.NowPlayingViewModelFactory
import com.example.tvshows.ui.watchlist.WatchlistFragment
import com.example.tvshows.utils.Extension_Utils.Companion.setGone
import com.example.tvshows.utils.Extension_Utils.Companion.setVisible
import com.example.tvshows.utils.Extension_Utils.Companion.success_toast

import kotlinx.android.synthetic.main.seen_fragment.*

class SeenFragment : Fragment() {

    lateinit var adapter: watchlistRecyclerViewAdapter
    private lateinit var viewModelFactory: NowPlayingViewModelFactory
    private lateinit var viewModel: SeenViewModel
    var navigate_listener_seen: OnFragmentNavigationListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val networkConnectionIncterceptor = this.context?.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = NowPlayingViewModelFactory(repository, requireContext())
        return inflater.inflate(R.layout.seen_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val list: MutableList<TvShowDetails> = mutableListOf()

        recycler_view_seen.setHasFixedSize(true)
        val manager = LinearLayoutManager(this.context)
        recycler_view_seen.layoutManager = manager
        adapter = watchlistRecyclerViewAdapter(this.requireContext(), list){view,tvShowDetails,handleName ->
            handleClick(view,tvShowDetails,handleName)
        }
        recycler_view_seen.adapter = adapter

        viewModel = ViewModelProvider(this, viewModelFactory).get(SeenViewModel::class.java)
        viewModel.details.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it, "")
        })

        viewModel.countOfseen.observe(viewLifecycleOwner, Observer {
            if (it == 0 )
                nowShowFound_seen.setVisible()
            else
                nowShowFound_seen.setGone()
        })
    }

    private fun handleClick(view: View, obj: TvShowDetails, handleName: ClickState) {
        if(handleName == ClickState.MoreInfo){
            showPopUpMenu_seen(context, view){state ->
                when (state) {
                    PopUpMenuStates.moveTowatchlist -> viewModel.moveToWatchlist(obj)
                    PopUpMenuStates.move_to_favorites -> viewModel.moveToFavorites(obj)
                    else ->findNavController().navigate(SeenFragmentDirections.actionSeenToShowDetailsFragment(obj.id, "seen"))
                }
            }
        }else if(handleName== ClickState.deleteIcon){
            viewModel.deleteTvshow(obj.id)
            context?.success_toast("${obj.name} succesfully deleted!")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as MainActivity).onBackPressedfunction()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnFragmentNavigationListener){
            navigate_listener_seen =context
        }
    }
}