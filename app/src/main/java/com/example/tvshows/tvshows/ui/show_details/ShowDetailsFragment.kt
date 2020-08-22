package com.example.tvshows.ui.show_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tvshows.R
import com.example.tvshows.data.ApiClient
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
import com.example.tvshows.databinding.ShowDetailsFragmentBinding
 import kotlinx.android.synthetic.main.show_details_fragment.*


class ShowDetailsFragment : Fragment() {

    private lateinit var viewModel: ShowDetailsViewModel
    private lateinit var viewModelFactory: DetailsViewModelFactory
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val networkConnectionIncterceptor = this.context?.applicationContext?.let {
            NetworkConnectionIncterceptor(it)
        }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = DetailsViewModelFactory(repository, requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory).get(ShowDetailsViewModel::class.java)

        val binding:ShowDetailsFragmentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.show_details_fragment,container,false)
        binding.setLifecycleOwner(this)
        val view:View=binding.root
        binding.viewmodel=viewModel
        addObserver()


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linear.animation=AnimationUtils.loadAnimation(context,R.anim.fade_transition_animation)

        val args= arguments?.let { ShowDetailsFragmentArgs.fromBundle(it) }
        viewModel.getTvShowDetails(args?.id.toString(),args?.deriveFrom)

    }

    private fun addObserver() {
        viewModel.details.observe(viewLifecycleOwner, Observer {
        })
    }

    @SuppressLint("InflateParams")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


       // animate()

    }


    fun animate() {
        linear.visibility = LinearLayout.VISIBLE
        val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.animate_layout)
        animation.duration = 1000
        linear.animation = animation
        linear.animate()
        animation.start()
    }

}