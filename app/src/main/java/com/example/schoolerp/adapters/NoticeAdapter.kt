package com.example.schoolerp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.databinding.ItemNoticeBinding
import com.example.schoolerp.models.Notice

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
        }
    }

    override fun getItemCount() = notices.size
}