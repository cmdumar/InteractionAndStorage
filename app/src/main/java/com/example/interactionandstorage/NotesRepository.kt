package com.example.interactionandstorage

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class Note(val name: String, val content: String)

private interface NotesStorage {
    fun load(context: Context): MutableList<Note>
    fun save(context: Context, notes: List<Note>)
}

object NotesRepository {

    private const val STORAGE_PREFS = "notes_storage_settings"
    private const val STORAGE_KEY = "storage_type"
    private const val NOTES_PREFS = "notes_prefs"
    private const val NOTES_FILE = "notes_file.json"

    enum class StorageType {
        SHARED_PREFERENCES,
        FILE
    }

    private val prefsStorage: NotesStorage = SharedPrefsNotesStorage()
    private val fileStorage: NotesStorage = FileNotesStorage()

    fun getStorageType(context: Context): StorageType {
        val prefs = context.getSharedPreferences(STORAGE_PREFS, Context.MODE_PRIVATE)
        val stored = prefs.getString(STORAGE_KEY, StorageType.SHARED_PREFERENCES.name)
        return runCatching { StorageType.valueOf(stored ?: StorageType.SHARED_PREFERENCES.name) }
            .getOrDefault(StorageType.SHARED_PREFERENCES)
    }

    fun setStorageType(context: Context, type: StorageType) {
        val prefs = context.getSharedPreferences(STORAGE_PREFS, Context.MODE_PRIVATE)
        prefs.edit { putString(STORAGE_KEY, type.name) }
    }

    fun loadNotes(context: Context): MutableList<Note> {
        return currentStorage(context).load(context)
    }

    fun addNote(context: Context, note: Note) {
        val storage = currentStorage(context)
        val notes = storage.load(context)
        notes.add(note)
        storage.save(context, notes)
    }

    fun deleteNote(context: Context, noteName: String) {
        val storage = currentStorage(context)
        val notes = storage.load(context).filterNot { it.name == noteName }
        storage.save(context, notes)
    }

    private fun currentStorage(context: Context): NotesStorage {
        return when (getStorageType(context)) {
            StorageType.SHARED_PREFERENCES -> prefsStorage
            StorageType.FILE -> fileStorage
        }
    }

    private class SharedPrefsNotesStorage : NotesStorage {
        override fun load(context: Context): MutableList<Note> {
            val prefs = context.getSharedPreferences(NOTES_PREFS, Context.MODE_PRIVATE)
            val json = prefs.getString("notes", "[]") ?: "[]"
            return NoteJsonHelper.parse(json)
        }

        override fun save(context: Context, notes: List<Note>) {
            val prefs = context.getSharedPreferences(NOTES_PREFS, Context.MODE_PRIVATE)
            prefs.edit { putString("notes", NoteJsonHelper.toJson(notes)) }
        }
    }

    private class FileNotesStorage : NotesStorage {
        override fun load(context: Context): MutableList<Note> {
            val file = File(context.filesDir, NOTES_FILE)
            if (!file.exists()) {
                return mutableListOf()
            }
            val json = runCatching { file.readText() }.getOrDefault("[]")
            return NoteJsonHelper.parse(json)
        }

        override fun save(context: Context, notes: List<Note>) {
            val file = File(context.filesDir, NOTES_FILE)
            runCatching {
                if (!file.exists()) {
                    file.createNewFile()
                }
                file.writeText(NoteJsonHelper.toJson(notes))
            }
        }
    }

    private object NoteJsonHelper {
        fun parse(json: String): MutableList<Note> {
            return runCatching {
                val array = JSONArray(json)
                val notes = mutableListOf<Note>()
                for (i in 0 until array.length()) {
                    val element = array.get(i)
                    when (element) {
                        is JSONObject -> notes.add(
                            Note(
                                name = element.optString("name"),
                                content = element.optString("content")
                            )
                        )

                        is String -> notes.add(Note(name = element, content = ""))
                    }
                }
                notes
            }.getOrDefault(mutableListOf())
        }

        fun toJson(notes: List<Note>): String {
            val array = JSONArray()
            notes.forEach { note ->
                val obj = JSONObject()
                obj.put("name", note.name)
                obj.put("content", note.content)
                array.put(obj)
            }
            return array.toString()
        }
    }
}

