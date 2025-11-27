package com.example.interactionandstorage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var notesListView: ListView
    private val notesList: MutableList<Note> = mutableListOf()
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notesListView = findViewById(R.id.notesListView)
        notesAdapter = NotesAdapter(this, notesList)
        notesListView.adapter = notesAdapter

        loadNotes()
        updateStorageSubtitle()

        notesListView.setOnItemClickListener { _, _, position, _ ->
            val selectedNote: String = notesList[position].name
            val intent = Intent(this@MainActivity, DeleteNoteActivity::class.java)
            intent.putExtra("note_name", selectedNote)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
        updateStorageSubtitle()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_note -> {
                startActivity(Intent(this@MainActivity, AddNoteActivity::class.java))
                true
            }

            R.id.action_delete_note -> {
                startActivity(Intent(this@MainActivity, DeleteNoteActivity::class.java))
                true
            }

            R.id.action_choose_storage -> {
                showStorageChooser()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadNotes() {
        val notes = NotesRepository.loadNotes(this)
        notesList.clear()
        notesList.addAll(notes)
        notesAdapter.notifyDataSetChanged()
    }

    private fun showStorageChooser() {
        val types = NotesRepository.StorageType.values()
        val labels = types.map { getStorageLabel(it) }.toTypedArray()
        val currentIndex = types.indexOf(NotesRepository.getStorageType(this))

        AlertDialog.Builder(this)
            .setTitle(R.string.storage_select_title)
            .setSingleChoiceItems(labels, currentIndex) { dialog, which ->
                val selectedType = types[which]
                NotesRepository.setStorageType(this, selectedType)
                loadNotes()
                updateStorageSubtitle()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun getStorageLabel(type: NotesRepository.StorageType): String {
        return when (type) {
            NotesRepository.StorageType.SHARED_PREFERENCES -> getString(R.string.storage_option_shared_prefs)
            NotesRepository.StorageType.FILE -> getString(R.string.storage_option_file)
        }
    }

    private fun updateStorageSubtitle() {
        val currentLabel = getStorageLabel(NotesRepository.getStorageType(this))
        supportActionBar?.subtitle = getString(R.string.storage_subtitle, currentLabel)
    }

    private class NotesAdapter(
        context: MainActivity,
        items: MutableList<Note>
    ) : ArrayAdapter<Note>(context, 0, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_note, parent, false)

            val titleTextView = view.findViewById<TextView>(R.id.noteTitleTextView)
            val contentTextView = view.findViewById<TextView>(R.id.noteContentTextView)

            val note = getItem(position)
            titleTextView.text = note?.name.orEmpty()
            contentTextView.text = note?.content.orEmpty()

            return view
        }
    }
}