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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.adempolat.wordleapp.databinding.FragmentMainBinding
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var handler: Handler
    private var runnable: Runnable? = null
    private lateinit var dailyRewardAdapter: DailyRewardAdapter

    private val todayDate: String
        get() {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return sdf.format(Date())
        }

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

        binding.todayDate.text = todayDate

        val coins = sharedPreferences.getInt("coins", 0)
        binding.coinsTextView.text = coins.toString()

        updateLevelInfo("level_5", binding.fiveLetterLevelText, binding.fiveLetterProgressBar)
        updateLevelInfo("level_6", binding.sixLetterLevelText, binding.sixLetterProgressBar)
        updateLevelInfo("level_7", binding.sevenLetterLevelText, binding.sevenLetterProgressBar)
        updateLevelInfo("level_8", binding.eightLetterLevelText, binding.eightLetterProgressBar)

        binding.fiveLetterWordsButton.setOnClickListener {
            navigateToGameFragment(5)
        }

        binding.sixLetterWordsButton.setOnClickListener {
            navigateToGameFragment(6)
        }

        binding.sevenLetterWordsButton.setOnClickListener {
            navigateToGameFragment(7)
        }

        binding.eightLetterWordsButton.setOnClickListener {
            navigateToGameFragment(8)
        }

        binding.dailyWordsButton.setOnClickListener {
            if (isDailyTaskAvailable()) {
                findNavController().navigate(R.id.action_mainFragment_to_dailyFragment)
                saveDailyTaskDate()
            } else {
                showDailyTaskUnavailableDialog()
            }
        }

        binding.btnMarket.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_marketFragment)
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
        val claimedDays = sharedPreferences.getInt("claimed_days", 0)

        if (claimedDays >= 7) {
            showAllRewardsClaimedDialog()
        } else if (currentTime - lastClaimTime >= 60 * 60 * 1000) { // 1 saat
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
                val remainingTime = 60 * 60 * 1000 - elapsedTime // 1 saat

                if (remainingTime > 0) {
                    val remainingMinutes = (remainingTime / 1000 / 60).toInt()
                    val remainingSeconds = ((remainingTime / 1000) % 60).toInt()
                    binding.dailyTaskStatus.text = String.format(
                        "Bir sonraki ödül için: %02d dakika %02d saniye",
                        remainingMinutes,
                        remainingSeconds
                    )
                    handler.postDelayed(this, 1000)
                } else {
                    binding.dailyTaskStatus.text = "Ödülünüz hazır!"
                    binding.claimRewardButton.isEnabled = true
                }
            }
        }
        handler.post(runnable!!)
    }

    private fun isDailyTaskAvailable(): Boolean {
        val lastDailyTaskDate = sharedPreferences.getString("last_daily_task_date", "")
        return lastDailyTaskDate != todayDate
    }

    private fun saveDailyTaskDate() {
        with(sharedPreferences.edit()) {
            putString("last_daily_task_date", todayDate)
            apply()
        }
    }

    private fun showDailyTaskUnavailableDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Günlük Görev")
        builder.setMessage("Günlük görevi sadece bir defa yapabilirsiniz. Yarın tekrar deneyin.")
        builder.setPositiveButton("Tamam") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showAllRewardsClaimedDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Tüm Ödüller Alındı")
        builder.setMessage("Tüm ödülleri aldınız. 50 altın kazandınız!")
        builder.setPositiveButton("Tamam") { dialog, _ ->
            dialog.dismiss()
            val coins = sharedPreferences.getInt("coins", 0) + 50
            with(sharedPreferences.edit()) {
                putInt("coins", coins)
                putInt("claimed_days", 0)
                apply()
            }
            binding.coinsTextView.text = coins.toString()
            dailyRewardAdapter.notifyDataSetChanged()
        }
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        runnable?.let { handler.removeCallbacks(it) }
    }
}
