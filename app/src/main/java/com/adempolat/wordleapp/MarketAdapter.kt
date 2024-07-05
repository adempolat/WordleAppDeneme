package com.adempolat.wordleapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MarketAdapter(
    private val context: Context,
    private val backgrounds: List<String>,
    private val prices: List<Int>
) : RecyclerView.Adapter<MarketAdapter.ViewHolder>() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
    private val handler = Handler()
    private var currentBackground = sharedPreferences.getString("background", "default")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_background, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(backgrounds[position], prices[position])
    }

    override fun getItemCount(): Int = backgrounds.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val backgroundText: TextView = itemView.findViewById(R.id.backgroundText)
        private val backgroundPrice: TextView = itemView.findViewById(R.id.backgroundPrice)
        private val backgroundPreview: ImageView = itemView.findViewById(R.id.backgroundPreview)
        private val purchaseButton: Button = itemView.findViewById(R.id.purchaseButton)
        private val previewButton: Button = itemView.findViewById(R.id.previewButton)
        private val imgCoin: ImageView = itemView.findViewById(R.id.imgCoin)

        fun bind(background: String, price: Int) {
            backgroundText.text = background.capitalize()
            backgroundPrice.text = if (price == 0) "Free" else "$price"


            val backgroundResource = when (background) {
                "dark" -> R.drawable.background_dark
                "blue" -> R.drawable.background_blue
                "yellow" -> R.drawable.background_yellow
                else -> R.drawable.background_light
            }
            backgroundPreview.setBackgroundResource(backgroundResource)

            if (background == currentBackground) {
                purchaseButton.text = "Applied"
                purchaseButton.setBackgroundColor(Color.GREEN)
            } else if (isPurchased(background)) {
                purchaseButton.text = "Apply"
                purchaseButton.setBackgroundColor(context.getColor(R.color.purple_700))
            } else {
                purchaseButton.text = "Buy"
                purchaseButton.setBackgroundColor(context.getColor(R.color.purple_700))
            }

            if (isPurchased(background)) {
                previewButton.visibility = View.GONE
            } else {
                previewButton.visibility = View.VISIBLE
            }

            purchaseButton.setOnClickListener {
                if (isPurchased(background)) {
                    applyBackground(background)
                } else {
                    buyBackground(background, price)
                }
            }

            previewButton.setOnClickListener {
                previewBackground(background)
            }
        }

        private fun isPurchased(background: String): Boolean {
            return sharedPreferences.getBoolean("purchased_$background", false)
        }

        private fun buyBackground(background: String, price: Int) {
            val coins = sharedPreferences.getInt("coins", 0)
            if (coins >= price) {
                val newCoins = coins - price
                with(sharedPreferences.edit()) {
                    putInt("coins", newCoins)
                    putBoolean("purchased_$background", true)
                    apply()
                }
                applyBackground(background)
            }
        }

        private fun applyBackground(background: String) {
            currentBackground = background
            with(sharedPreferences.edit()) {
                putString("background", background)
                apply()
            }
            (context as MainActivity).updateBackground(background)
            notifyDataSetChanged() // Satın alındıktan sonra butonları güncelle
        }

        private fun previewBackground(background: String) {
            val backgroundResource = when (background) {
                "dark" -> R.drawable.background_dark
                "blue" -> R.drawable.background_blue
                "yellow" -> R.drawable.background_yellow
                else -> R.drawable.background_light
            }
            val mainActivity = context as MainActivity
            mainActivity.previewBackground(backgroundResource)

            handler.postDelayed({
                mainActivity.updateBackground(currentBackground!!)
            }, 10000) // 10 saniye sonra arka planı eski haline getir
        }
    }
}
