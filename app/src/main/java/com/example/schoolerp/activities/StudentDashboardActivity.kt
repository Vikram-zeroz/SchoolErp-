package com.example.schoolerp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolerp.adapters.NoteAdapter
import com.example.schoolerp.adapters.NoticeAdapter
import com.example.schoolerp.databinding.ActivityStudentDashboardBinding
import com.example.schoolerp.models.Note
import com.example.schoolerp.models.Notice
import com.example.schoolerp.utils.FirebaseHelper
import java.lang.Exception

class StudentDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentDashboardBinding
    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var noteAdapter: NoteAdapter
    private val notices = mutableListOf<Notice>()
    private val notes = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, StudentProfileActivity::class.java))
        }

        setupRecyclerViews()
        loadNotices()
        loadNotes()

        binding.btnLogout.setOnClickListener {
            FirebaseHelper.auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupRecyclerViews() {
        noticeAdapter = NoticeAdapter(notices)
        binding.rvNotices.apply {
            layoutManager = LinearLayoutManager(this@StudentDashboardActivity)
            adapter = noticeAdapter
        }

        noteAdapter = NoteAdapter(notes) { note ->
            viewNoteLink(note)
        }
        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(this@StudentDashboardActivity)
            adapter = noteAdapter
        }
    }

    private fun loadNotices() {
        FirebaseHelper.firestore.collection("notices")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                notices.clear()
                snapshot?.documents?.forEach { doc ->
                    doc.toObject(Notice::class.java)?.let { notices.add(it) }
                }
                noticeAdapter.notifyDataSetChanged()
            }
    }

    private fun loadNotes() {
        FirebaseHelper.firestore.collection("notes")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                notes.clear()
                snapshot?.documents?.forEach { doc ->
                    doc.toObject(Note::class.java)?.let { notes.add(it) }
                }
                noteAdapter.notifyDataSetChanged()
            }
    }

    private fun viewNoteLink(note: Note) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(note.fileUrl))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open link: Invalid URL or no browser app installed.", Toast.LENGTH_LONG).show()
        }
    }
}