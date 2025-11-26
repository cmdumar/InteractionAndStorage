package com.example.interactionandstorage

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONArray

class AddNoteActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtContent: EditText
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        prefs = getSharedPreferences("notes_prefs", MODE_PRIVATE)

        edtName = findViewById(R.id.noteNameEditText)
        edtContent = findViewById(R.id.noteContentEditText)

        findViewById<Button>(R.id.saveNoteButton).setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val name = edtName.text.toString().trim()
        val content = edtContent.text.toString().trim()

        val message = getString(R.string.warning_empty)

        if (name.isEmpty() || content.isEmpty()) {
            Toast.makeText(
                this@AddNoteActivity,
                message,
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val json = prefs.getString("notes", "[]")
        val array = JSONArray(json)
        array.put(name)

        prefs.edit().putString("notes", array.toString()).apply()

        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
