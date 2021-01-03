package com.example.tvshows.ui.settings

import android.app.Dialog
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.addCallback
import com.example.tvshows.MainActivity
import com.example.tvshows.R
import com.example.tvshows.tvshows.utils.PreferenceUtils
import kotlinx.android.synthetic.main.settings_fragment.*
import kotlinx.android.synthetic.main.time_picker_dialog.*

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as MainActivity).onBackPressedfunction()
        }
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        //------------------------------------------------Μολις ανοιγει η οθονη πρεπει να εκτυπωσω τις αποθηκευμενες ρυθμισεις -----------------------------------------------------//

        //------------------------------------------------display τον χρονο -----------------------------------------------//

        display_preferredTimeId.text = PreferenceUtils.get_preferred_time(requireContext())

        //------------------------------------------------ενεργο η οχι το VIBRATION notification state-----------------------------------------------//

        vibrate_checked.isChecked = PreferenceUtils.get_vibration_state(requireContext())!!

//------------------------------------------------ενεργο η οχι το SOUND notification state-----------------------------------------------//

        sound_switch.isChecked = PreferenceUtils.get_sound_state(requireContext())!!

//------------------------------------------------αλλαγη καταστασης του vibration και αποθηκευση ----------------------------------//
        vibrate_checked.setOnClickListener {
            vibrate_checked.isChecked = vibrate_checked.isChecked != true

            if (vibrate_checked.isChecked) {
                PreferenceUtils.set_vibration_state(true, requireContext())
            } else {
                PreferenceUtils.set_vibration_state(false, requireContext())
            }
        }

        sound_switch.setOnClickListener {
            if (sound_switch.isChecked) {
                PreferenceUtils.set_sound_state(true, requireContext())
            } else {
                PreferenceUtils.set_sound_state(false, requireContext())
            }
        }

        timer_picker_id1.setOnClickListener {

            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            dialog.setContentView(R.layout.time_picker_dialog)
            dialog.show()
            dialog.saveTime.setOnClickListener {
                val preferredTime = "${dialog.time_picker_dialog.hour}:${dialog.time_picker_dialog.minute}"
                PreferenceUtils.set_preferred_time(preferredTime, requireContext())

                display_preferredTimeId.text = PreferenceUtils.get_preferred_time(requireContext())
                dialog.dismiss()

            }

        }
    }

}