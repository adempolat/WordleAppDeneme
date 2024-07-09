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
import androidx.navigation.fragment.navArgs
import com.adempolat.wordleapp.databinding.FragmentGameBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private var secretWord = "" // Tahmin edilecek kelime
    private var currentGuessRow = 0
    private var coins = 0
    private var level = 1
    private var isFirstHintUsed = false
    private var wordLength = 5
    private lateinit var sharedPreferences: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())

    private val args: GameFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        coins = sharedPreferences.getInt("coins", 0)
        wordLength = args.wordLength
        level = sharedPreferences.getInt("level_$wordLength", 1)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        secretWord = WordListHelper.getRandomWord(wordLength) // Oyun başladığında rastgele kelime ayarla

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
                        level++
                        saveGameData()
                        updateCoinsAndLevel()
                        startConfetti()
                        saveStats(currentGuessRow + 1, true)
                        showWinDialog(currentGuessRow + 1)
                    } else if (currentGuessRow < 5) { // 4 yerine 5 yaparak tahmin hakkını 6'ya çıkardık
                        currentGuessRow++
                        setupGuessRow(guessRows[currentGuessRow])
                    } else {
                        saveStats(currentGuessRow + 1, false)
                        showLoseDialog(secretWord)
                    }
                    handler.postDelayed({
                        binding.submitGuessButton.isEnabled = true // 1 saniye sonra butonu yeniden etkinleştir
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
                updateCoinsAndLevel()
                giveHint(guessRows[currentGuessRow])
            } else {
                showInsufficientCoinsDialog()
            }
        }

        updateCoinsAndLevel()
        updateHintButtonText()
    }

    private fun createGuessRows(): List<List<EditText>> {
        val guessRows = mutableListOf<List<EditText>>()
        for (i in 1..6) { // 5 yerine 6 yaparak tahmin hakkını 6'ya çıkardık
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
            putInt("level_$wordLength", level)
            apply()
        }
    }

    private fun saveStats(attempts: Int, won: Boolean) {
        val gamesPlayedKey = "games_played_$wordLength"
        val gamesWonKey = "games_won_$wordLength"
        val attemptsKey = "attempts_${wordLength}_$attempts"
        val winStreakKey = "win_streak_$wordLength"
        val winRecordKey = "win_record_$wordLength"

        val gamesPlayed = sharedPreferences.getInt(gamesPlayedKey, 0) + 1
        val gamesWon = if (won) sharedPreferences.getInt(gamesWonKey, 0) + 1 else sharedPreferences.getInt(gamesWonKey, 0)
        val attemptsCount = sharedPreferences.getInt(attemptsKey, 0) + 1
        var winStreak = sharedPreferences.getInt(winStreakKey, 0)
        var winRecord = sharedPreferences.getInt(winRecordKey, 0)

        if (won) {
            winStreak++
            if (winStreak > winRecord) {
                winRecord = winStreak
            }
        } else {
            winStreak = 0
        }

        with(sharedPreferences.edit()) {
            putInt(gamesPlayedKey, gamesPlayed)
            putInt(gamesWonKey, gamesWon)
            putInt(attemptsKey, attemptsCount)
            putInt(winStreakKey, winStreak)
            putInt(winRecordKey, winRecord)
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

    private fun updateCoinsAndLevel() {
        binding.coinsTextView.text = coins.toString()
        binding.levelTextView.text = "Seviye: $level"
    }

    private fun setupGuessRows(guessRows: List<List<EditText>>) {
        guessRows.forEach { row ->
            row.forEach { editText ->
                editText.isEnabled = false
            }
        }
    }

    private fun setupGuessRow(guessRow: List<EditText>) {
        // Tüm satırları devre dışı bırak
        setupGuessRows(createGuessRows())

        // Geçerli tahmin satırını etkinleştir
        guessRow.forEachIndexed { index, editText ->
            editText.isEnabled = true
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && index < guessRow.size - 1) {
                        guessRow[index + 1].requestFocus()
                    } else if (s?.isEmpty() == true && index > 0) {
                        guessRow[index - 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        guessRow[0].requestFocus()
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
                    handler.postDelayed(onComplete, 500) // Animasyonların tamamlanması için onComplete'i çağırmadan önce bekleyin
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
        }, 500) // Animasyonlar için 500ms gecikme sonrası dialog göster
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
        }, 500) // Animasyonlar için 500ms gecikme sonrası dialog göster
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
            guessRow[index].text.toString().uppercase() != secretWord[index].toString()
        }

        if (incorrectIndices.isNotEmpty()) {
            val hintIndex = incorrectIndices.random()
            guessRow[hintIndex].apply {
                setText(secretWord[hintIndex].toString())
                setBackgroundColor(Color.GREEN)
                setTextColor(Color.BLACK)
                isEnabled = false
            }
        }
    }

    private fun startConfetti() {
        binding.konfettiView.start(
            Party(
                speed = 5f,
                maxSpeed = 30f,
                damping = 0.9f,
                angle = 0,
                spread = 360,
                colors = listOf(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA),
                emitter = Emitter(duration = 5000, TimeUnit.MILLISECONDS).max(500),
                position = Position.Relative(0.5, 0.0)
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
