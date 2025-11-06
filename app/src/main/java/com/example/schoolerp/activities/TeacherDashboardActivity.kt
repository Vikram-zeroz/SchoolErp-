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
import java.util.*

class TeacherDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherDashboardBinding
    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var noteAdapter: NoteAdapter
    private val notices = mutableListOf<Notice>()
    private val notes = mutableListOf<Note>()
    private var selectedFileUri: Uri? = null

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedFileUri = result.data?.data
            showUploadNoteDialog()
        }
    }

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
            pickFile()
        }

        binding.btnLogout.setOnClickListener {
            FirebaseHelper.auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupRecyclerViews() {
        noticeAdapter = NoticeAdapter(notices)
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
        val dialogView = layoutInflater.inflate(com.example.schoolerp.R.layout.dialog_upload_notice, null)
        val etTitle = dialogView.findViewById<TextInputEditText>(com.example.schoolerp.R.id.etNoticeTitle)
        val etContent = dialogView.findViewById<TextInputEditText>(com.example.schoolerp.R.id.etNoticeContent)

        AlertDialog.Builder(this)
            .setTitle("Upload Notice")
            .setView(dialogView)
            .setPositiveButton("Upload") { _, _ ->
                val title = etTitle.text.toString()
                val content = etContent.text.toString()
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

                val notice = Notice(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    content = content,
                    uploadedBy = currentUser.uid,
                    uploadedByName = teacherName
                )

                FirebaseHelper.firestore.collection("notices")
                    .document(notice.id)
                    .set(notice)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Notice uploaded successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }

    private fun showUploadNoteDialog() {
        val dialogView = layoutInflater.inflate(com.example.schoolerp.R.layout.dialog_upload_note, null)
        val etTitle = dialogView.findViewById<TextInputEditText>(com.example.schoolerp.R.id.etNoteTitle)
        val etSubject = dialogView.findViewById<TextInputEditText>(com.example.schoolerp.R.id.etNoteSubject)

        AlertDialog.Builder(this)
            .setTitle("Upload Note")
            .setView(dialogView)
            .setPositiveButton("Upload") { _, _ ->
                val title = etTitle.text.toString()
                val subject = etSubject.text.toString()
                uploadNote(title, subject)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun uploadNote(title: String, subject: String) {
        val fileUri = selectedFileUri ?: return
        val fileName = getFileName(fileUri)
        val currentUser = FirebaseHelper.getCurrentUser() ?: return

        val storageRef = FirebaseHelper.storage.reference
            .child("notes/${UUID.randomUUID()}_$fileName")

        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    saveNoteToFirestore(title, subject, downloadUri.toString(), fileName, currentUser.uid)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveNoteToFirestore(title: String, subject: String, fileUrl: String, fileName: String, teacherId: String) {
        FirebaseHelper.firestore.collection("users")
            .document(teacherId)
            .get()
            .addOnSuccessListener { doc ->
                val teacherName = doc.getString("name") ?: "Teacher"

                val note = Note(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    subject = subject,
                    fileUrl = fileUrl,
                    fileName = fileName,
                    uploadedBy = teacherId,
                    uploadedByName = teacherName
                )

                FirebaseHelper.firestore.collection("notes")
                    .document(note.id)
                    .set(note)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Note uploaded successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun getFileName(uri: Uri): String {
        var result = "file"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index >= 0) result = cursor.getString(index)
            }
        }
        return result
    }
}