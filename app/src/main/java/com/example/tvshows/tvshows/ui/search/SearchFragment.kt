package com.example.tvshows.ui.search

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tvshows.ItemAdapter
import com.example.tvshows.MainActivity
import com.example.tvshows.R
import com.example.tvshows.data.ApiClient
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.netMethods
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import com.example.tvshows.tvshows.ui.adapters.paging.PagedItemAdapter
import com.example.tvshows.tvshows.ui.adapters.paging.ShowsDataSource
import com.example.tvshows.ui.nowplaying.NowPlayingViewModelFactory
import com.example.tvshows.utils.Extension_Utils.Companion.error_toast
import com.example.tvshows.utils.Extension_Utils.Companion.hideKeyboard
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchFragment : Fragment() {


    private lateinit var viewModelFactory: NowPlayingViewModelFactory
    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val networkConnectionIncterceptor = this.context?.applicationContext?.let {
            NetworkConnectionIncterceptor(it)
        }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = NowPlayingViewModelFactory(repository, requireContext())
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)
        (activity as? MainActivity)?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        val manager = LinearLayoutManager(this.context)
        recyclerview_search.layoutManager = manager
        recyclerview_search.setHasFixedSize(true)
        val adapter = PagedItemAdapter(requireContext())
        recyclerview_search.adapter = adapter

        recyclerview_search.adapter = adapter
        adapter.listener_callback = {it ->
            navigateTo(it)
        }
        ShowsDataSource.firstResults = {
            if(it == 0 && searcheditText.text!!.isNotEmpty()){
                putPlaceholders(SearchResult.NO_RESULT, adapter, "")
                //viewModel.reserfactory()
                //adapter.currentList?.dataSource?.invalidate()
            }
        }
        viewModel.itemPagedList.observe(viewLifecycleOwner, Observer {
            it?.let {
                    adapter.submitList(it)
            }

        })

        searcheditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtils.isEmpty(s.toString())) {
                    adapter.setQuery(s.toString())
                    viewModel.searchTvShows1(s.toString().trim())
                    adapter.currentList?.dataSource?.invalidate()
                    putPlaceholders(SearchResult.HAS_RESULT, adapter, s.toString())
                } else {
                    adapter.currentList?.dataSource?.invalidate()
                    putPlaceholders(SearchResult.EMPTY_SEARCH_TEXT, adapter, s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


        })

    }

    private fun putPlaceholders(res: SearchResult, adapter: PagedItemAdapter, searchQuery: String) {

        when (res) {
            SearchResult.EMPTY_SEARCH_TEXT -> {
                viewModel.reserfactory()
                Handler().postDelayed({
                    search?.visibility = View.VISIBLE
                    no_res.visibility = View.GONE

                }, 200)
            }

            SearchResult.HAS_RESULT -> {
                search?.visibility = View.GONE
                no_res?.visibility = View.GONE
            }

            else -> {
                lifecycleScope.launch(Dispatchers.Main){
                    Handler().postDelayed({
                        if (adapter.itemCount == 0) {
                            search.visibility = View.GONE
                            no_res.visibility = View.VISIBLE
                        }
                    }, 200)
                }

            }
        }
    }

    fun navigateTo(id: Int) {
        if (netMethods.hasInternet(requireContext(), true)) {
            val action = SearchFragmentDirections.actionSearchFragmentToShowDetailsFragment(id, "searchFragment")
            findNavController().navigate(action)
        } else {
            context?.error_toast("No internet connection!")
        }
    }

}

    enum class SearchResult {
        NO_RESULT,
        HAS_RESULT,
        EMPTY_SEARCH_TEXT
    }
