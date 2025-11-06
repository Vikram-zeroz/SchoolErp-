package com.example.schoolerp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.databinding.ItemNoteBinding
import com.example.schoolerp.models.Note

class NoteAdapter(
    private val notes: List<Note>,
    private val onDownloadClick: (Note) -> Unit
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
            tvNoteSubject.text = note.subject
            tvFileName.text = note.fileName
            tvUploadedBy.text = "By: ${note.uploadedByName}"

            btnDownload.setOnClickListener {
                onDownloadClick(note)
            }
        }
    }

    override fun getItemCount() = notes.size
}