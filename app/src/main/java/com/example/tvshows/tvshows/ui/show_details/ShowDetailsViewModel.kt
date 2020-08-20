package com.example.tvshows.ui.show_details

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvshows.R
import com.example.tvshows.TvShowRoomDatabase
import com.example.tvshows.bindingAdapters.listener
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.local_repository
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.tvshows.ui.show_details.ClickCallback
import com.example.tvshows.utils.Extension_Utils.Companion.setGone
import com.example.tvshows.utils.Extension_Utils.Companion.setVisible
import com.example.tvshows.utils.Extension_Utils.Companion.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.layout_bottom_sheet.view.*
import kotlinx.android.synthetic.main.season_overview_dialog.view.*
import kotlinx.coroutines.launch


class ShowDetailsViewModel(var remoteRepository: RemoteRepository, var context: Context) : ViewModel(), ClickCallback {

    var details_ = MutableLiveData<TvShowDetails>()
    lateinit var currentTvShow:TvShowDetails
    private var local_repository: local_repository
    val details: LiveData<TvShowDetails>
        get() = details_

    init {
        val TvshowsDao = TvShowRoomDatabase.getDatabase(context).tvShowDao()
        local_repository = local_repository(TvshowsDao)
        listener = this
    }

    fun getTvShowDetails(id: String, currentFragment: String?) {
        viewModelScope.launch {
            try {
                if(currentFragment.equals("watchList")){
                    currentTvShow = local_repository.getTvShowDetails(id)
                }else {
                    currentTvShow = remoteRepository.getTvShowDetails(id)
                }

                details_.value = currentTvShow
            } catch (ex: Exception) {
                context.toast(ex.message.toString())
            }
        }
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
            seasons.text=context.getString(R.string.hide_seasons)                                               //και αλλάζω το text απο show details σε hide details
            main_scrollview.post(Runnable { main_scrollview.fullScroll(ScrollView.FOCUS_DOWN) })                //όταν κάνω ορατό το layout θέλω το main scrollview να πηγαίνει τέρμα κάτω
        } else {                                                                                                //έτσι ώστε να είναι ορατό το layout με τις σεζον χωρίς να το κάνει ο χρήστης
            NestedScrollView.setGone()
            seasons.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_details, 0)
            seasons.text=context.getString(R.string.show_seasons)
        }
    }

    // καλείται απο το xml αρχείο..Αυτή η συνάρτηση δημιουργεί το bottom_sheet απο το οποίο ο χρήστης μπορεί να προσθέτει σειρές στις αγαπημένες- σε αυτες που έχει δει- στο watchlist
    fun pin_icon_clicked() {
        val dialog = BottomSheetDialog(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)
        dialog.show()

        view.add_to_watchlist.setOnClickListener {
            addTodatabase("watchlist",dialog)
        }
        view.add_to_favorites.setOnClickListener {
            addTodatabase("favorites", dialog)
        }
        view.add_to_seen.setOnClickListener {
            addTodatabase("seen", dialog)
        }
    }

    fun addTodatabase(current_fragment: String, dialog: BottomSheetDialog){
        currentTvShow.currentFragment=current_fragment
        local_repository.insertTvshowDetailstoDb(currentTvShow,viewModelScope)
        context.toast("${currentTvShow.name} added succesfully to $current_fragment!")
        dialog.dismiss()
    }





//Εδώ πρόκειται για μια συνάρτηση η οποία είναι απο το interface ClickCallback και καλείται μόλις ο χρήστης πατήσει μια επιθυμητή σεζόν απο κάποιο show
    override fun seasonClicked(overview: String) {
        if (overview.equals(""))    //Αν δεν παρέχεται απο το API περιγραφή της σεζον δεν χρειάζεται να εμφανίσω το dialog με τις πληροφορίες.Απλά εμφανίζω ένα μήνυμα
            context.toast("No season overview provided!")
        else {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            val layoutInflaterAndroid = LayoutInflater.from(context)
            val view2: View = layoutInflaterAndroid.inflate(R.layout.season_overview_dialog, null)
            builder.setView(view2)
            view2.season_overview.text = overview
            val alertDialog: AlertDialog = builder.create()
            alertDialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog.show()
            view2.action_btn.setOnClickListener { alertDialog.dismiss() }
        }
    }
}