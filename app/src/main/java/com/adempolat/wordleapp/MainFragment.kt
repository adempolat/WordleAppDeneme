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
import com.adempolat.wordleapp.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var handler: Handler
    private var runnable: Runnable? = null

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

        updateLevelInfo()
    }

    private fun navigateToGameFragment(wordLength: Int) {
        val action = MainFragmentDirections.actionMainFragmentToGameFragment(wordLength)
        findNavController().navigate(action)
    }

    private fun updateLevelInfo() {
        val levels = mapOf(
            "level_5" to binding.fiveLetterLevelText,
            "level_6" to binding.sixLetterLevelText,
            "level_7" to binding.sevenLetterLevelText,
            "level_8" to binding.eightLetterLevelText
        )

        levels.forEach { (key, textView) ->
            val level = sharedPreferences.getInt(key, 1)
            textView.text = "Seviye $level"
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        runnable?.let { handler.removeCallbacks(it) }
    }
}
