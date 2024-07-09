package com.adempolat.wordleapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.adempolat.wordleapp.databinding.FragmentStatsBinding
import kotlin.math.round

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var progressBars: List<ProgressBar>
    private lateinit var attemptTextViews: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

        progressBars = listOf(
            binding.progress1, binding.progress2, binding.progress3,
            binding.progress4, binding.progress5, binding.progress6
        )

        attemptTextViews = listOf(
            binding.attempts1, binding.attempts2, binding.attempts3,
            binding.attempts4, binding.attempts5, binding.attempts6
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val wordLengths = listOf("5 Harfli", "6 Harfli", "7 Harfli", "8 Harfli")
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            wordLengths
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWordLength.adapter = spinnerAdapter

        binding.spinnerWordLength.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateStats()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        updateStats()
    }

    private fun updateStats() {
        val wordLength = binding.spinnerWordLength.selectedItem.toString().substringBefore(" ").toInt()

        // Set stats for overall performance
        binding.textGamesPlayed.text = sharedPreferences.getInt("games_played_$wordLength", 0).toString()
        binding.textGamesWon.text = sharedPreferences.getInt("games_won_$wordLength", 0).toString()

        val gamesPlayed = sharedPreferences.getInt("games_played_$wordLength", 0)
        val gamesWon = sharedPreferences.getInt("games_won_$wordLength", 0)
        val winPercentage = if (gamesPlayed != 0) (gamesWon * 100 / gamesPlayed) else 0
        binding.textWinPercentage.text = "$winPercentage%"

        // Ortalama tahmin sayısını hesapla ve göster
        val totalAttempts = (1..6).sumOf { i -> sharedPreferences.getInt("attempts_${wordLength}_$i", 0) * i }
        val avgAttempts = if (gamesWon != 0) round(totalAttempts.toDouble() / gamesWon * 10) / 10 else 0.0
        binding.textAvgAttempts.text = avgAttempts.toString()

        binding.textWinStreak.text = sharedPreferences.getInt("win_streak_$wordLength", 0).toString()
        binding.textWinRecord.text = sharedPreferences.getInt("win_record_$wordLength", 0).toString()

        // Set stats for individual attempts
        for (i in 1..6) {
            val key = "attempts_${wordLength}_$i"
            val count = sharedPreferences.getInt(key, 0)
            val totalAttemptsCount = (1..6).sumOf { sharedPreferences.getInt("attempts_${wordLength}_$it", 0) }
            val percentage = if (totalAttemptsCount != 0) (count * 100 / totalAttemptsCount) else 0

            progressBars[i - 1].progress = percentage
            attemptTextViews[i - 1].text = count.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
