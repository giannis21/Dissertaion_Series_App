package com.example.tvshows.ui.search

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tvshows.ItemAdapter
import com.example.tvshows.R
import com.example.tvshows.RecyclerViewclick_Callback
import com.example.tvshows.data.ApiClient
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import kotlinx.android.synthetic.main.search_fragment.*


class SearchFragment : Fragment(), RecyclerViewclick_Callback {


    private lateinit var viewModelFactory: SearchViewModelFactory
    lateinit var adapter: ItemAdapter
    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val networkConnectionIncterceptor = this.context?.applicationContext?.let {
            NetworkConnectionIncterceptor(it)
        }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = SearchViewModelFactory(repository, requireContext())
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)

        val list: MutableList<Result_NowPlaying> = mutableListOf()


        //-------Recyclerview--------//
        val manager = LinearLayoutManager(this.context)
        recyclerview_search.layoutManager = manager
        recyclerview_search.setHasFixedSize(true)
        adapter = ItemAdapter(this.requireContext(), list, this)
        recyclerview_search.adapter = adapter


        searchbtn.setOnClickListener{
            if (!TextUtils.isEmpty(searcheditText.text.toString())){
                viewModel.searchTvShows(1,searcheditText.text.toString())
            }
        }

        viewModel.searched.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.resultNowPlayings,"searchFragment")
            hideKeyboardFrom()
        })


    }

    override fun OnTvShowClick(id: Int) {
        val action= SearchFragmentDirections.actionSearchFragmentToShowDetailsFragment(id,"searchFragment")
        findNavController().navigate(action)
    }
    fun hideKeyboardFrom() {
        val imm: InputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}