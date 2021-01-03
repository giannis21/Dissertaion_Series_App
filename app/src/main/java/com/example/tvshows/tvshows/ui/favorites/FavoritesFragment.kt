package com.example.tvshows.ui.favorites

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
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.tvshows.ui.adapters.ClickState
import com.example.tvshows.tvshows.ui.adapters.watchlistRecyclerViewAdapter
import com.example.tvshows.tvshows.ui.callbacks.OnFragmentNavigationListener
import com.example.tvshows.tvshows.ui.favorites.PopUpMenuStates
import com.example.tvshows.tvshows.ui.favorites.popUpMenu_favorites.showPopUpMenu_fav
import com.example.tvshows.ui.nowplaying.NowPlayingViewModelFactory
import com.example.tvshows.ui.toprated.TopRatedFragment
import com.example.tvshows.utils.Extension_Utils.Companion.success_toast
import kotlinx.android.synthetic.main.favorites_fragment.*

class FavoritesFragment : Fragment() {

    lateinit var adapter: watchlistRecyclerViewAdapter
    private lateinit var viewModelFactory: NowPlayingViewModelFactory

    private lateinit var viewModel: FavoritesViewModel
    companion object{
        var navigate_listener_fav: OnFragmentNavigationListener?=null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val networkConnectionIncterceptor = this.context?.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = NowPlayingViewModelFactory(repository, requireContext())
        return inflater.inflate(R.layout.favorites_fragment, container, false)
    }



    private fun handleClick(view: View, obj: TvShowDetails, handleName: ClickState) {
        when (handleName) {
            ClickState.MoreInfo -> {
                showPopUpMenu_fav(context, view) { state ->
                    when (state) {
                        PopUpMenuStates.moveToSeen -> viewModel.moveToSeen(obj)
                        PopUpMenuStates.moveTowatchlist -> viewModel.moveTowatchlist(obj)
                        else ->  findNavController().navigate(FavoritesFragmentDirections.actionFavoritesToShowDetailsFragment(obj.id, "favorites"))
                    }
                }
            }
            ClickState.deleteIcon -> {
                viewModel.deleteTvshow(obj.id)
                context?.success_toast("${obj.name} succesfully deleted!")
            }
            else -> {

            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as MainActivity).onBackPressedfunction()
        }

        val list: MutableList<TvShowDetails> = mutableListOf()

        recycler_view_fav.setHasFixedSize(true)
        val manager = LinearLayoutManager(this.context)
        recycler_view_fav.layoutManager = manager
        adapter = watchlistRecyclerViewAdapter(this.requireContext(), list) { view1, tvShowDetails, handleName ->
            handleClick(view1, tvShowDetails, handleName)
        }

        recycler_view_fav.adapter = adapter

        viewModel = ViewModelProvider(this, viewModelFactory).get(FavoritesViewModel::class.java)
        viewModel.details.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it, "")
        })

        viewModel.countOfFav.observe(viewLifecycleOwner, Observer {
            if (it == 0 && (adapter.itemCount == 1 || adapter.itemCount == 0))
                nowShowFound_fav.visibility = View.VISIBLE
            else
                nowShowFound_fav.visibility = View.GONE
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnFragmentNavigationListener){
            navigate_listener_fav =context
        }
    }
}