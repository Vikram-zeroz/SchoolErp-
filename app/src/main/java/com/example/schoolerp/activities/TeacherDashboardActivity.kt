package com.example.schoolerp.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolerp.adapters.NoteAdapter
import com.example.schoolerp.adapters.NoticeAdapter
import com.example.schoolerp.databinding.ActivityTeacherDashboardBinding
import com.example.schoolerp.models.Note
import com.example.schoolerp.models.Notice
import com.example.schoolerp.utils.FirebaseHelper
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class TeacherDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherDashboardBinding
    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var noteAdapter: NoteAdapter
    private val notices = mutableListOf<Notice>()
    private val notes = mutableListOf<Note>()

    private val dateFormatNotice = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    private val dateFormatNote = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        loadNotices()
        loadNotes()

        binding.fabUploadNotice.setOnClickListener {
            showUploadNoticeDialog()
        }

        binding.fabUploadNote.setOnClickListener {
            showUploadNoteDialog()
        }

        binding.btnLogout.setOnClickListener {
            FirebaseHelper.auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupRecyclerViews() {
        noticeAdapter = NoticeAdapter(notices) { notice ->
            showDeleteConfirmationDialog(notice)
        }
        binding.rvNotices.apply {
            layoutManager = LinearLayoutManager(this@TeacherDashboardActivity)
            adapter = noticeAdapter
        }

        noteAdapter = NoteAdapter(notes) { }
        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(this@TeacherDashboardActivity)
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

    private fun showUploadNoticeDialog() {
        val dialogView =
            layoutInflater.inflate(com.example.schoolerp.R.layout.dialog_upload_notice, null)
        val etTitle =
            dialogView.findViewById<TextInputEditText>(com.example.schoolerp.R.id.etNoticeTitle)
        val etContent =
            dialogView.findViewById<TextInputEditText>(com.example.schoolerp.R.id.etNoticeContent)

        AlertDialog.Builder(this)
            .setTitle("Upload Notice")
            .setView(dialogView)
            .setPositiveButton("Upload") { _, _ ->
                val title = etTitle.text.toString()
                val content = etContent.text.toString()

                if (title.isBlank() || content.isBlank()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                uploadNotice(title, content)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun uploadNotice(title: String, content: String) {
        val currentUser = FirebaseHelper.getCurrentUser() ?: return

        FirebaseHelper.firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { doc ->
                val teacherName = doc.getString("name") ?: "Teacher"

                val currentDate = dateFormatNotice.format(Date())

                val notice = Notice(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    content = content,
                    uploadedBy = currentUser.uid,
                    uploadedByName = teacherName,
                    date = currentDate
                )

                FirebaseHelper.firestore.collection("notices")
                    .document(notice.id)
                    .set(notice)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Notice uploaded successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun showDeleteConfirmationDialog(notice: Notice) {
        AlertDialog.Builder(this)
            .setTitle("Delete Notice")
            .setMessage("Are you sure you want to delete \"${notice.title}\"?")
            .setPositiveButton("Delete") { _, _ ->
                deleteNotice(notice)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteNotice(notice: Notice) {
        val currentUser = FirebaseHelper.getCurrentUser()

        if (currentUser?.uid != notice.uploadedBy) {
            Toast.makeText(this, "You can only delete your own notices", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseHelper.firestore.collection("notices")
            .document(notice.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Notice deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting notice: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun showUploadNoteDialog() {
        val dialogView =
            layoutInflater.inflate(com.example.schoolerp.R.layout.dialog_upload_note, null)
        val etTitle =
            dialogView.findViewById<TextInputEditText>(com.example.schoolerp.R.id.etNoteTitle)
        val etLink =
            dialogView.findViewById<TextInputEditText>(com.example.schoolerp.R.id.etNoteLink)
        val etDescription =
            dialogView.findViewById<TextInputEditText>(com.example.schoolerp.R.id.etNoteDescription)

        AlertDialog.Builder(this)
            .setTitle("Upload Note Link")
            .setView(dialogView)
            .setPositiveButton("Upload") { _, _ ->
                val title = etTitle.text.toString()
                val link = etLink.text.toString()
                val description = etDescription.text.toString()

                if (title.isBlank() || link.isBlank() || description.isBlank()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                uploadNote(title, link, description)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun uploadNote(title: String, link: String, description: String) {
        val currentUser = FirebaseHelper.getCurrentUser() ?: return

        saveNoteToFirestore(
            title,
            description,
            link,
            currentUser.uid
        )
    }

    private fun saveNoteToFirestore(
        title: String,
        description: String,
        fileUrl: String,
        teacherId: String
    ) {
        FirebaseHelper.firestore.collection("users")
            .document(teacherId)
            .get()
            .addOnSuccessListener { doc ->
                val teacherName = doc.getString("name") ?: "Teacher"

                val currentDate = dateFormatNote.format(Date())

                val note = Note(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    fileUrl = fileUrl,
                    date = currentDate,
                    uploadedBy = teacherId,
                    uploadedByName = teacherName
                )

                FirebaseHelper.firestore.collection("notes")
                    .document(note.id)
                    .set(note)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Note uploaded successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
    }
}