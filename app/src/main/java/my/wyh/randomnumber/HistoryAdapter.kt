package my.wyh.randomnumber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(
    private val items: List<HistoryItem>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeText: TextView = itemView.findViewById(R.id.timeText)
        val contentText: TextView = itemView.findViewById(R.id.contentText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.timeText.text = item.time
        holder.contentText.text = "于 " + item.min + " 与 " + item.max + " 间取得 " + item.value
        
        // 非线性入场动画
        holder.itemView.alpha = 0f
        holder.itemView.translationX = -50f
        holder.itemView.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(300)
            .setStartDelay(position * 50L)
            .setInterpolator(PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f))
            .start()
    }

    override fun getItemCount(): Int = items.size
}
