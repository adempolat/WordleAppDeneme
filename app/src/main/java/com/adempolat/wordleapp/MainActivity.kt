package com.adempolat.wordleapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adempolat.wordleapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("game_prefs", MODE_PRIVATE)
        updateBackground(sharedPreferences.getString("background", "default")!!)
    }

    fun updateBackground(background: String) {
        val backgroundResource = when (background) {
            "dark" -> R.drawable.background_dark
            "blue" -> R.drawable.background_blue
            "yellow" -> R.drawable.background_yellow
            else -> R.drawable.background_light
        }
        binding.root.setBackgroundResource(backgroundResource)
    }

    fun previewBackground(backgroundResource: Int) {
        binding.root.setBackgroundResource(backgroundResource)
    }

    fun getCurrentBackground(): String {
        return sharedPreferences.getString("background", "default") ?: "default"
    }
}
