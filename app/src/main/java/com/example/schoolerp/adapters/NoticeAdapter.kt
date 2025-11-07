package com.example.schoolerp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.R
import com.example.schoolerp.models.Notice

class NoticeAdapter(
    private val notices: List<Notice>,
    private val onDeleteClick: (Notice) -> Unit = {} // Add delete callback
) : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvNoticeTitle)
        val tvContent: TextView = view.findViewById(R.id.tvNoticeContent)
        val tvUploadedBy: TextView = view.findViewById(R.id.tvUploadedBy)
        val tvDate: TextView = view.findViewById(R.id.tvNoticeDate)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteNotice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notice, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = notices[position]
        holder.tvTitle.text = notice.title
        holder.tvContent.text = notice.content
        holder.tvUploadedBy.text = "By: ${notice.uploadedByName}"
        holder.tvDate.text = notice.date

        holder.btnDelete.setOnClickListener {
            onDeleteClick(notice)
        }
    }

    override fun getItemCount() = notices.size
}