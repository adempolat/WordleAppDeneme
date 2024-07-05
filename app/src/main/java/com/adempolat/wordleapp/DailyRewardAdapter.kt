package com.adempolat.wordleapp

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DailyRewardAdapter(private val context: Context) : RecyclerView.Adapter<DailyRewardAdapter.ViewHolder>() {

    private val days = (1..30).toList()
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily_reawrd, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val claimedDays = sharedPreferences.getInt("claimed_days", 0)
        holder.bind(days[position], position < claimedDays)
    }

    override fun getItemCount(): Int = days.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        private val coinIcon: ImageView = itemView.findViewById(R.id.coinIcon)

        fun bind(day: Int, isClaimed: Boolean) {
            dayTextView.text = day.toString()
            coinIcon.setImageResource(if (isClaimed) R.drawable.ic_tick else R.drawable.coin)
        }
    }
}
