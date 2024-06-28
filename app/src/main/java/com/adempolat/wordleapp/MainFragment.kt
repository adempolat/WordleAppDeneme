package com.adempolat.wordleapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.adempolat.wordleapp.databinding.FragmentMainBinding
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.util.*

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var dailyRewardAdapter: DailyRewardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        handler = Handler(Looper.getMainLooper())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coins = sharedPreferences.getInt("coins", 0)
        binding.coinsTextView.text = coins.toString()

        updateLevelInfo("level_5", binding.fiveLetterLevelText, binding.fiveLetterProgressBar)
        updateLevelInfo("level_6", binding.sixLetterLevelText, binding.sixLetterProgressBar)
        updateLevelInfo("level_7", binding.sevenLetterLevelText, binding.sevenLetterProgressBar)
        updateLevelInfo("level_timer", binding.timerModeLevelText, binding.timerModeProgressBar)

        binding.fiveLetterWordsButton.setOnClickListener {
            navigateToGameFragment(5)
        }

        binding.sixLetterWordsButton.setOnClickListener {
            navigateToGameFragment(6)
        }

        binding.dailyWordsButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_dailyFragment)
        }

        dailyRewardAdapter = DailyRewardAdapter(requireContext())
        binding.dailyRewardRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.dailyRewardRecyclerView.adapter = dailyRewardAdapter

        binding.claimRewardButton.setOnClickListener {
            claimDailyReward()
        }

        setupDailyReward()
    }

    private fun navigateToGameFragment(wordLength: Int) {
        val action = MainFragmentDirections.actionMainFragmentToGameFragment(wordLength)
        findNavController().navigate(action)
    }

    private fun updateLevelInfo(levelKey: String, levelTextView: TextView, progressBar: CircularProgressBar) {
        val level = sharedPreferences.getInt(levelKey, 1)
        levelTextView.text = "Seviye $level"
        progressBar.progress = (level / 100.0 * progressBar.progressMax).toFloat()
    }

    private fun setupDailyReward() {
        val lastClaimTime = sharedPreferences.getLong("last_claim_time", 0)
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastClaimTime >= 60 * 1000) { // 1 dakika
            // Kullanıcıya ödül ver
            binding.claimRewardButton.isEnabled = true
            binding.dailyTaskStatus.text = "Ödülünüz hazır!"
        } else {
            // Geri sayım başlat
            binding.claimRewardButton.isEnabled = false
            startCountdown(lastClaimTime, currentTime)
        }
    }

    private fun claimDailyReward() {
        val coins = sharedPreferences.getInt("coins", 0)
        val newCoins = coins + 10 // Örnek: 10 altın ödül
        val claimedDays = sharedPreferences.getInt("claimed_days", 0) + 1

        with(sharedPreferences.edit()) {
            putInt("coins", newCoins)
            putInt("claimed_days", claimedDays)
            putLong("last_claim_time", System.currentTimeMillis())
            apply()
        }

        binding.coinsTextView.text = newCoins.toString()
        binding.claimRewardButton.isEnabled = false
        dailyRewardAdapter.notifyItemRangeChanged(0, claimedDays)
        startCountdown(System.currentTimeMillis(), System.currentTimeMillis())
    }

    private fun startCountdown(lastClaimTime: Long, currentTime: Long) {
        runnable = object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - lastClaimTime
                val remainingTime = 60 * 1000 - elapsedTime // 1 dakika

                if (remainingTime > 0) {
                    val remainingSeconds = (remainingTime / 1000).toInt()
                    binding.dailyTaskStatus.text = String.format(
                        "Bir sonraki ödül için: %02d saniye",
                        remainingSeconds
                    )
                    handler.postDelayed(this, 1000)
                } else {
                    binding.dailyTaskStatus.text = "Ödülünüz hazır!"
                    binding.claimRewardButton.isEnabled = true
                }
            }
        }
        handler.post(runnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(runnable)
    }
}
