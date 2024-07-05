package com.adempolat.wordleapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adempolat.wordleapp.databinding.FragmentMarketBinding

class MarketFragment : Fragment() {

    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private val backgrounds = listOf("default", "dark", "blue", "yellow")
    private val prices = listOf(0, 200, 100, 100)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMarketBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.coinsTextView.text = sharedPreferences.getInt("coins", 0).toString()
        binding.marketRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.marketRecyclerView.adapter = MarketAdapter(requireContext(), backgrounds, prices)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
