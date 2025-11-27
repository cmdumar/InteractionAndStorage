package com.example.interactionandstorage

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DeleteNoteActivity : AppCompatActivity() {

    private val notes: MutableList<Note> = mutableListOf()
    private lateinit var adapter: ArrayAdapter<String>
    private val noteNames: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_note)

        val listView = findViewById<ListView>(R.id.deleteNotesListView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, noteNames)
        listView.adapter = adapter

        refreshNotes()

        listView.setOnItemClickListener { _, _, position, _ ->
            deleteNote(position)
        }
    }

    private fun refreshNotes() {
        notes.clear()
        notes.addAll(NotesRepository.loadNotes(this))
        noteNames.clear()
        noteNames.addAll(notes.map { it.name })
        adapter.notifyDataSetChanged()
    }

    private fun deleteNote(position: Int) {
        val noteName = notes.getOrNull(position)?.name ?: return
        NotesRepository.deleteNote(this, noteName)
        Toast.makeText(this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show()
        finish()
    }
}
