package com.adempolat.wordleapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.adempolat.wordleapp.databinding.FragmentDailyBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DailyFragment : Fragment() {

    private var _binding: FragmentDailyBinding? = null
    private val binding get() = _binding!!

    private var secretWord = "" // Tahmin edilecek kelime
    private var currentGuessRow = 0
    private var coins = 0
    private var isFirstHintUsed = false
    private var wordLength = 5
    private lateinit var sharedPreferences: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())
    private val hintIndices = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDailyBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        coins = sharedPreferences.getInt("coins", 0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        secretWord = WordListHelper.getRandomWord(wordLength) // Oyunun başında rastgele kelime belirle

        val guessRows = createGuessRows()

        setupGuessRows(guessRows)
        setupGuessRow(guessRows[currentGuessRow])

        binding.submitGuessButton.setOnClickListener {
            val guess = getGuess(guessRows[currentGuessRow]).uppercase()
            if (guess.length == wordLength) {
                binding.submitGuessButton.isEnabled = false // Butonu devre dışı bırak
                provideFeedback(guess, guessRows[currentGuessRow]) {
                    if (guess == secretWord) {
                        coins += 10
                        saveGameData()
                        updateCoins()
                        showWinDialog(currentGuessRow + 1)
                        markDayOnCalendar()
                    } else if (currentGuessRow < 4) {
                        currentGuessRow++
                        setupGuessRow(guessRows[currentGuessRow])
                    } else {
                        showLoseDialog(secretWord)
                    }
                    handler.postDelayed({
                        binding.submitGuessButton.isEnabled = true // 1 saniye sonra butonu tekrar etkinleştir
                    }, 1000)
                }
            }
        }

        binding.hintButton.setOnClickListener {
            if (!isFirstHintUsed) {
                isFirstHintUsed = true
                updateHintButtonText()
                giveHint(guessRows[currentGuessRow])
            } else if (coins >= 30) {
                coins -= 30
                saveGameData()
                updateCoins()
                giveHint(guessRows[currentGuessRow])
            } else {
                showInsufficientCoinsDialog()
            }
        }

        updateCoins()
        updateHintButtonText()
    }

    private fun createGuessRows(): List<List<EditText>> {
        val guessRows = mutableListOf<List<EditText>>()
        for (i in 1..5) {
            val row = mutableListOf<EditText>()
            for (j in 1..wordLength) {
                val resId = resources.getIdentifier("letter$i$j", "id", requireContext().packageName)
                val editText = binding.root.findViewById<EditText>(resId)
                editText.visibility = View.VISIBLE
                row.add(editText)
            }
            guessRows.add(row)
        }
        return guessRows
    }

    private fun saveGameData() {
        with(sharedPreferences.edit()) {
            putInt("coins", coins)
            apply()
        }
    }

    private fun updateHintButtonText() {
        if (!isFirstHintUsed) {
            binding.hintButton.text = "İpucu - 0"
        } else {
            binding.hintButton.text = "İpucu - 30"
        }
    }

    private fun updateCoins() {
        binding.coinsTextView.text = coins.toString()
    }

    private fun setupGuessRows(guessRows: List<List<EditText>>) {
        guessRows.forEach { row ->
            row.forEach { editText ->
                editText.isEnabled = false
            }
        }
    }

    private fun setupGuessRow(guessRow: List<EditText>) {
        // Önce tüm satırları devre dışı bırak
        setupGuessRows(createGuessRows())

        // Mevcut tahmin satırını etkinleştir
        guessRow.forEachIndexed { index, editText ->
            editText.isEnabled = true
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && index < guessRow.size - 1) {
                        var nextIndex = index + 1
                        while (nextIndex < guessRow.size && hintIndices.contains(nextIndex)) {
                            nextIndex++
                        }
                        if (nextIndex < guessRow.size) {
                            guessRow[nextIndex].requestFocus()
                        }
                    } else if (s?.isEmpty() == true && index > 0) {
                        var prevIndex = index - 1
                        while (prevIndex >= 0 && hintIndices.contains(prevIndex)) {
                            prevIndex--
                        }
                        if (prevIndex >= 0) {
                            guessRow[prevIndex].requestFocus()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        // İlk ipucu olmayan EditText'e odaklan
        var firstNonHintIndex = 0
        while (firstNonHintIndex < guessRow.size && hintIndices.contains(firstNonHintIndex)) {
            firstNonHintIndex++
        }
        if (firstNonHintIndex < guessRow.size) {
            guessRow[firstNonHintIndex].requestFocus()
        }
    }

    private fun getGuess(guessRow: List<EditText>): String {
        return guessRow.joinToString("") { it.text.toString() }
    }

    private fun provideFeedback(guess: String, guessRow: List<EditText>, onComplete: () -> Unit) {
        val handler = Handler(Looper.getMainLooper())

        val secretWordCharCount = mutableMapOf<Char, Int>()
        for (char in secretWord) {
            secretWordCharCount[char] = secretWordCharCount.getOrDefault(char, 0) + 1
        }

        val colorAssignments = Array(wordLength) { Color.RED }

        // İlk geçiş: doğru pozisyonları kontrol et
        for (i in guess.indices) {
            if (guess[i] == secretWord[i]) {
                colorAssignments[i] = Color.GREEN
                secretWordCharCount[guess[i]] = secretWordCharCount[guess[i]]!! - 1
            }
        }

        // İkinci geçiş: yanlış pozisyonları kontrol et
        for (i in guess.indices) {
            if (colorAssignments[i] != Color.GREEN && secretWord.contains(guess[i]) && secretWordCharCount[guess[i]]!! > 0) {
                colorAssignments[i] = Color.YELLOW
                secretWordCharCount[guess[i]] = secretWordCharCount[guess[i]]!! - 1
            }
        }

        for (i in guess.indices) {
            handler.postDelayed({
                guessRow[i].setBackgroundColor(colorAssignments[i])
                guessRow[i].setTextColor(Color.BLACK)
                if (i == guess.length - 1) {
                    handler.postDelayed(onComplete, 500) // Animasyonların tamamlanmasını bekleyin
                }
            }, i * 300L) // Her harf için 300ms gecikme
        }
    }

    private fun showWinDialog(attempts: Int) {
        handler.postDelayed({
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_win, null)
            builder.setView(dialogView)
            builder.setCancelable(false)

            val dialog = builder.create()
            dialog.show()

            dialogView.findViewById<TextView>(R.id.winMessage).text = "Tebrikler! $attempts. denemede kazandınız."
            dialogView.findViewById<Button>(R.id.playAgainButton).setOnClickListener {
                dialog.dismiss()
                resetGame()
            }
        }, 500) // Animasyonlar için 500ms gecikme
    }

    private fun showLoseDialog(word: String) {
        handler.postDelayed({
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_lose, null)
            builder.setView(dialogView)
            builder.setCancelable(false)

            val dialog = builder.create()
            dialog.show()

            dialogView.findViewById<TextView>(R.id.loseMessage).text = "Üzgünüm, bilemediniz. Kelime: $word"
            dialogView.findViewById<Button>(R.id.playAgainButton).setOnClickListener {
                dialog.dismiss()
                resetGame()
            }
        }, 500) // Animasyonlar için 500ms gecikme
    }

    private fun showInsufficientCoinsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Yetersiz Altın")
        builder.setMessage("İpucu almak için yeterli altının yok.")
        builder.setPositiveButton("Tamam") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun resetGame() {
        currentGuessRow = 0
        secretWord = WordListHelper.getRandomWord(wordLength) // Yeni rastgele kelime ile sıfırla
        hintIndices.clear()
        isFirstHintUsed = false
        updateHintButtonText()
        val guessRows = createGuessRows()

        guessRows.forEach { row ->
            row.forEach { editText ->
                editText.text.clear()
                editText.setBackgroundColor(Color.WHITE)
                editText.setTextColor(Color.BLACK)
                editText.isEnabled = false
            }
        }
        setupGuessRow(guessRows[currentGuessRow])
    }

    private fun giveHint(guessRow: List<EditText>) {
        val incorrectIndices = secretWord.indices.filter { index ->
            guessRow[index].text.toString().uppercase() != secretWord[index].toString() && !hintIndices.contains(index)
        }

        if (incorrectIndices.isNotEmpty()) {
            val hintIndex = incorrectIndices.random()
            hintIndices.add(hintIndex)
            guessRow[hintIndex].apply {
                setText(secretWord[hintIndex].toString())
                setBackgroundColor(Color.GREEN)
                setTextColor(Color.BLACK)
                isEnabled = false
            }
        }
    }

    private fun markDayOnCalendar() {
        val today = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(today)
        with(sharedPreferences.edit()) {
            putBoolean(formattedDate, true)
            apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
