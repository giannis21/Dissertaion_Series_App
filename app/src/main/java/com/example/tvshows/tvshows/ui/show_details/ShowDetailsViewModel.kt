package com.example.tvshows.ui.show_details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvshows.MainActivity
import com.example.tvshows.R
import com.example.tvshows.TvShowRoomDatabase
import com.example.tvshows.bindingAdapters.listener
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.local_repository
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.tvshows.data.network.response.RateResponse
import com.example.tvshows.tvshows.ui.show_details.ClickCallback
import com.example.tvshows.tvshows.ui.show_details.Details_dialogs
import com.example.tvshows.tvshows.ui.show_details.Details_dialogs.Companion.displayRatingDialog
import com.example.tvshows.tvshows.ui.show_details.Details_dialogs.Companion.displaySeasonDialog
import com.example.tvshows.tvshows.utils.PreferenceUtils.Companion.getguest_session
import com.example.tvshows.tvshows.utils.PreferenceUtils.Companion.setguest_session
import com.example.tvshows.utils.Extension_Utils.Companion.error_toast
import com.example.tvshows.utils.Extension_Utils.Companion.setGone
import com.example.tvshows.utils.Extension_Utils.Companion.setVisible
import com.example.tvshows.utils.Extension_Utils.Companion.success_toast
import com.example.tvshows.utils.Extension_Utils.Companion.warning_toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.layout_bottom_sheet.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ShowDetailsViewModel(var remoteRepository: RemoteRepository, var context: Context) : ViewModel(), ClickCallback {

    var details_ = MutableLiveData<TvShowDetails>()
    var rateResponse:MutableLiveData<String> ?= MutableLiveData<String>(null)

    var success_submitted: MutableLiveData<Boolean>? = MutableLiveData<Boolean>(false)
    var showId=""
    lateinit var currentTvShow: TvShowDetails
     var allShows: LiveData<MutableList<TvShowDetails>>
    private var local_repository: local_repository
    val details: LiveData<TvShowDetails>
        get() = details_

    init {
        val TvshowsDao = TvShowRoomDatabase.getDatabase(context).tvShowDao()
        local_repository = local_repository(TvshowsDao)
        listener = this
        allShows=local_repository.getTvShowDetailsAll()
    }

    fun getTvShowDetails(id: String, currentFragment: String?) {
        showId=id
        val det = Details_dialogs(context)
        det.displayLoadingDialog(context)
        viewModelScope.launch {
            try {
                currentTvShow = if (currentFragment.equals("watchList")
                            ||  currentFragment.equals("favorites") || currentFragment.equals("seen") || currentFragment.equals("deepLink")) {

                   local_repository.getTvShowDetails(id)
                } else {
                    remoteRepository.getTvShowDetails(id)
                }
                det.hideLoadingDialog()
                details_.value = currentTvShow

            } catch (ex: Exception) {
                context.error_toast(ex.message.toString())
                det.hideLoadingDialog()
            }

        }
    }

    fun displayRating_dialog() {
        displayRatingDialog(context, this)
    }

    // καλείται απο το xml αρχείο και ανοίγει στο browser την ιστοσελίδα της τρέχουσας σειράς
    fun addUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }

    // καλείται απο το xml αρχείο..Αυτη η συνάρτηση κάνει ορατό το layout όπου υπάρχουν οι σεζον της καθε σειράς
    fun seasonClicked(NestedScrollView: NestedScrollView, seasons: TextView, main_scrollview: ScrollView) {
        if (NestedScrollView.isGone) {
            NestedScrollView.setVisible()
            seasons.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_bottom, 0) //όταν είναι ορατό εμφανίζω το εικονίδιο που έχει το bottom arrow
            seasons.text = context.getString(R.string.hide_seasons)                                              //και αλλάζω το text απο show details σε hide details
            main_scrollview.post(Runnable { main_scrollview.fullScroll(ScrollView.FOCUS_DOWN) })                //όταν κάνω ορατό το layout θέλω το main scrollview να πηγαίνει τέρμα κάτω
        } else {                                                                                                //έτσι ώστε να είναι ορατό το layout με τις σεζον χωρίς να το κάνει ο χρήστης
            NestedScrollView.setGone()
            seasons.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_details, 0)
            seasons.text = context.getString(R.string.show_seasons)
        }
    }

    // καλείται απο το xml αρχείο..Αυτή η συνάρτηση δημιουργεί το bottom_sheet απο το οποίο ο χρήστης μπορεί να προσθέτει σειρές στις αγαπημένες- σε αυτες που έχει δει- στο watchlist
    fun pin_icon_clicked() {
     //  ShowDetailsFragment.showBanner()


        val dialog = BottomSheetDialog(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)

        dialog.show()

        view.add_to_watchlist.setOnClickListener {
            addTodatabase("watchlist", dialog)
        }
        view.add_to_favorites.setOnClickListener {
            addTodatabase("favorites", dialog)
        }
        view.add_to_seen.setOnClickListener {
            addTodatabase("seen", dialog)
        }
    }

    fun addTodatabase(current_fragment: String, dialog: BottomSheetDialog) {
        currentTvShow.currentFragment = current_fragment

        viewModelScope.launch {
            val exists = local_repository.rowExists(
                currentTvShow.id.toString(),
                current_fragment,
                viewModelScope
            )
            if (!exists) {
                currentTvShow.exactDateOfNotification=""
                local_repository.insertTvshowDetailstoDb(currentTvShow, viewModelScope)
                context.success_toast("${currentTvShow.name} added succesfully to $current_fragment!")
            } else {
                context.error_toast("${currentTvShow.name} already exists to $current_fragment!")
            }
        }
        dialog.dismiss()
    }

    //Εδώ πρόκειται για μια συνάρτηση η οποία είναι απο το interface ClickCallback και καλείται μόλις ο χρήστης πατήσει μια επιθυμητή σεζόν απο κάποιο show
    override fun seasonClicked(overview: String) {
        if (overview.equals(""))                     //Αν δεν παρέχεται απο το API περιγραφή της σεζον δεν χρειάζεται να εμφανίσω το dialog με τις πληροφορίες.Απλά εμφανίζω ένα μήνυμα
            context.warning_toast("No season overview provided!")
        else {
            displaySeasonDialog(context, overview)
        }
    }

    fun rateTvshow(rate: String) {
        val jsonObj = JsonObject()
        jsonObj.addProperty("value", rate)
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                if (getguest_session(context).equals("")) {
                    val user_session = async { remoteRepository.getguest_session() }.await()
                    setguest_session(user_session.guest_session_id, context)
                }
                async { remoteRepository.rateTvShow(showId,getguest_session(context),jsonObj)}.await()
            }.onFailure {
                success_submitted?.postValue(false)
                rateResponse?.postValue(it.toString())
                context.error_toast(it.message.toString())
            }.onSuccess {
                rateResponse?.postValue(it.toString())
            }

        }

    }

}