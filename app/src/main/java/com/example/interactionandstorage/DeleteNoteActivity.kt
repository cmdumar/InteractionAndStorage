package com.example.interactionandstorage

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import org.json.JSONArray

class DeleteNoteActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var notes: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_note)

        prefs = getSharedPreferences("notes_prefs", MODE_PRIVATE)

        notes = loadNotes()

        val listView = findViewById<ListView>(R.id.deleteNotesListView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notes)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            deleteNote(notes[position])
        }
    }

    private fun loadNotes(): ArrayList<String> {
        val list = ArrayList<String>()
        val json = prefs.getString("notes", "[]")
        val array = JSONArray(json)

        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        return list
    }

    private fun deleteNote(name: String) {
        val json = prefs.getString("notes", "[]")
        val array = JSONArray(json)

        val newArray = JSONArray()

        for (i in 0 until array.length()) {
            if (array.getString(i) != name) {
                newArray.put(array.getString(i))
            }
        }

        prefs.edit().putString("notes", newArray.toString()).apply()

        Toast.makeText(this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show()
        finish()
    }
}
