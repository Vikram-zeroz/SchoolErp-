package com.example.schoolerp.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.databinding.ItemNoteBinding
import com.example.schoolerp.models.Note

class NoteAdapter(
    private val notes: List<Note>,
    private val onLinkClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.binding.apply {
            tvNoteTitle.text = note.title
            // Using 'description' field
            tvNoteDescription.text = note.description
            // Using 'date' field
            tvNoteDate.text = note.date
            tvUploadedBy.text = "By: ${note.uploadedByName}"

            // Using btnViewLink (from updated XML)
            btnViewLink.setOnClickListener {
                onLinkClick(note)
                openLink(holder.itemView.context, note.fileUrl)
            }
        }
    }

    override fun getItemCount() = notes.size

    private fun openLink(context: android.content.Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Error opening link: Invalid URL or no app to handle it.", Toast.LENGTH_LONG).show()
        }
    }
}