package com.example.schoolerp.activities

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolerp.adapters.NoteAdapter
import com.example.schoolerp.adapters.NoticeAdapter
import com.example.schoolerp.databinding.ActivityStudentDashboardBinding
import com.example.schoolerp.models.Note
import com.example.schoolerp.models.Notice
import com.example.schoolerp.utils.FirebaseHelper

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
            downloadNote(note)
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

    private fun downloadNote(note: Note) {
        val request = DownloadManager.Request(Uri.parse(note.fileUrl))
            .setTitle(note.title)
            .setDescription("Downloading ${note.fileName}")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, note.fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        Toast.makeText(this, "Downloading ${note.fileName}", Toast.LENGTH_SHORT).show()
    }
}