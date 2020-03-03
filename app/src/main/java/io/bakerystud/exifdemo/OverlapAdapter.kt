package io.bakerystud.exifdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.item_overlap.view.*
import java.text.SimpleDateFormat
import java.util.*

class OverlapAdapter : ListAdapter<Overlap, RecyclerView.ViewHolder>(object :
    DiffUtil.ItemCallback<Overlap>() {
    override fun areItemsTheSame(oldItem: Overlap, newItem: Overlap): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Overlap, newItem: Overlap): Boolean {
        return oldItem == newItem
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_overlap, parent, false)
        return  OverlapViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val date = SimpleDateFormat("YYYY-MM-dd, HH:mm", Locale.getDefault()).format(Date(item.date))
        holder.itemView.textTime.text = date
        holder.itemView.textLocation.text = "${item.gpsRecord.latitude}:${item.gpsRecord.longitude}"
        holder.itemView.recycler.adapter = PhotoListAdapter().apply {
            submitList(item.myPhotos.map { it.path })
        }
        holder.itemView.recycler.layoutManager = LinearLayoutManager(holder.itemView.context)

    }
}

class OverlapViewHolder(private val view: View) : RecyclerView.ViewHolder(view)