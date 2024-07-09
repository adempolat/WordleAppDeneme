package com.adempolat.wordleapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.adempolat.wordleapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToggleButtons()
        setupLanguageSpinner()
        setupResetButton()
    }

    private fun setupToggleButtons() {
        binding.switchNotifications.isChecked = sharedPreferences.getBoolean("notifications_enabled", true)
        binding.switchSound.isChecked = sharedPreferences.getBoolean("sound_enabled", true)
        binding.switchConfetti.isChecked = sharedPreferences.getBoolean("confetti_enabled", true)

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPreferences.edit()) {
                putBoolean("notifications_enabled", isChecked)
                apply()
            }
        }

        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPreferences.edit()) {
                putBoolean("sound_enabled", isChecked)
                apply()
            }
        }

        binding.switchConfetti.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPreferences.edit()) {
                putBoolean("confetti_enabled", isChecked)
                apply()
            }
        }
    }

    private fun setupLanguageSpinner() {
        val languages = listOf("English", "Türkçe", "Español", "Deutsch")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter

        val currentLanguage = sharedPreferences.getString("language", "English")
        binding.spinnerLanguage.setSelection(languages.indexOf(currentLanguage))

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                with(sharedPreferences.edit()) {
                    putString("language", languages[position])
                    apply()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupResetButton() {
        binding.buttonResetStats.setOnClickListener {
            with(sharedPreferences.edit()) {
                clear()
                apply()
            }
            // Optionally show a confirmation dialog
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
