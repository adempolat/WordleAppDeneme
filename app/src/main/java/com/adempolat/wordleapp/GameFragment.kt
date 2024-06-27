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
import com.adempolat.wordleapp.databinding.FragmentGameBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private var secretWord = "" // The word to guess
    private var currentGuessRow = 0
    private var coins = 0
    private var level = 1
    private var isFirstHintUsed = false
    private lateinit var sharedPreferences: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())
    private val hintIndices = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        coins = sharedPreferences.getInt("coins", 0)
        level = sharedPreferences.getInt("level_5", 1)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        secretWord = WordListHelper.getRandomWord() // Set random word at the start of the game

        val guessRows = listOf(
            listOf(binding.letter11, binding.letter12, binding.letter13, binding.letter14, binding.letter15),
            listOf(binding.letter21, binding.letter22, binding.letter23, binding.letter24, binding.letter25),
            listOf(binding.letter31, binding.letter32, binding.letter33, binding.letter34, binding.letter35),
            listOf(binding.letter41, binding.letter42, binding.letter43, binding.letter44, binding.letter45),
            listOf(binding.letter51, binding.letter52, binding.letter53, binding.letter54, binding.letter55)
        )

        setupGuessRows(guessRows)
        setupGuessRow(guessRows[currentGuessRow])

        binding.submitGuessButton.setOnClickListener {
            val guess = getGuess(guessRows[currentGuessRow]).uppercase()
            if (guess.length == 5) {
                binding.submitGuessButton.isEnabled = false // Disable the button
                provideFeedback(guess, guessRows[currentGuessRow]) {
                    if (guess == secretWord) {
                        coins += 10
                        level++
                        saveGameData()
                        updateCoinsAndLevel()
                        startConfetti()
                        showWinDialog(currentGuessRow + 1)
                    } else if (currentGuessRow < 4) {
                        currentGuessRow++
                        setupGuessRow(guessRows[currentGuessRow])
                    } else {
                        showLoseDialog(secretWord)
                    }
                    handler.postDelayed({
                        binding.submitGuessButton.isEnabled = true // Re-enable the button after 1 second
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

    private fun saveGameData() {
        with(sharedPreferences.edit()) {
            putInt("coins", coins)
            putInt("level_5", level)
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
        binding.levelTextView.text = "Level: $level"
    }

    private fun setupGuessRows(guessRows: List<List<EditText>>) {
        guessRows.forEach { row ->
            row.forEach { editText ->
                editText.isEnabled = false
            }
        }
    }

    private fun setupGuessRow(guessRow: List<EditText>) {
        // Disable all rows first
        setupGuessRows(listOf(
            listOf(binding.letter11, binding.letter12, binding.letter13, binding.letter14, binding.letter15),
            listOf(binding.letter21, binding.letter22, binding.letter23, binding.letter24, binding.letter25),
            listOf(binding.letter31, binding.letter32, binding.letter33, binding.letter34, binding.letter35),
            listOf(binding.letter41, binding.letter42, binding.letter43, binding.letter44, binding.letter45),
            listOf(binding.letter51, binding.letter52, binding.letter53, binding.letter54, binding.letter55)
        ))

        // Enable the current guess row
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

        // Focus the first non-hint EditText
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

        val colorAssignments = Array(5) { Color.RED }

        // First pass: check for correct positions
        for (i in guess.indices) {
            if (guess[i] == secretWord[i]) {
                colorAssignments[i] = Color.GREEN
                secretWordCharCount[guess[i]] = secretWordCharCount[guess[i]]!! - 1
            }
        }

        // Second pass: check for incorrect positions
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
                    handler.postDelayed(onComplete, 500) // Wait for animations to complete before calling onComplete
                }
            }, i * 300L) // 300ms delay for each letter
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
        }, 500) // Show dialog after 500ms delay for animations
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
        }, 500) // Show dialog after 500ms delay for animations
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
        secretWord = WordListHelper.getRandomWord() // Reset with a new random word
        hintIndices.clear()
        isFirstHintUsed = false
        updateHintButtonText()
        val guessRows = listOf(
            listOf(binding.letter11, binding.letter12, binding.letter13, binding.letter14, binding.letter15),
            listOf(binding.letter21, binding.letter22, binding.letter23, binding.letter24, binding.letter25),
            listOf(binding.letter31, binding.letter32, binding.letter33, binding.letter34, binding.letter35),
            listOf(binding.letter41, binding.letter42, binding.letter43, binding.letter44, binding.letter45),
            listOf(binding.letter51, binding.letter52, binding.letter53, binding.letter54, binding.letter55)
        )

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
