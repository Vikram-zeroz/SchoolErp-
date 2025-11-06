package com.example.schoolerp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.databinding.ItemNoticeBinding
import com.example.schoolerp.models.Notice
import java.text.SimpleDateFormat
import java.util.*

class NoticeAdapter(private val notices: List<Notice>) :
    RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    inner class NoticeViewHolder(val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = notices[position]
        holder.binding.apply {
            tvNoticeTitle.text = notice.title
            tvNoticeContent.text = notice.content
            tvUploadedBy.text = "By: ${notice.uploadedByName}"
            tvTimestamp.text = formatDate(notice.timestamp)
        }
    }

    override fun getItemCount() = notices.size

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}