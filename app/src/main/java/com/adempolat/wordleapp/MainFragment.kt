package com.adempolat.wordleapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.adempolat.wordleapp.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fiveLetterLevel = sharedPreferences.getInt("level_5", 1)
        val sixLetterLevel = sharedPreferences.getInt("level_6", 1)
        val sevenLetterLevel = sharedPreferences.getInt("level_7", 1)

        binding.fiveLetterWordsButton.text = "5 Harfli Kelimeler - Seviye: $fiveLetterLevel"
        binding.sixLetterWordsButton.text = "6 Harfli Kelimeler - Seviye: $sixLetterLevel"
        binding.sevenLetterWordsButton.text = "7 Harfli Kelimeler - Seviye: $sevenLetterLevel"

        binding.fiveLetterWordsButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_gameFragment)
        }

        // Diğer butonlar için henüz işlem yapmıyoruz
        // binding.sixLetterWordsButton.setOnClickListener { }
        // binding.sevenLetterWordsButton.setOnClickListener { }
        // binding.timedModeButton.setOnClickListener { }

        val coins = sharedPreferences.getInt("coins", 0)
        binding.coinsTextView.text = coins.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
