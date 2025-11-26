package com.example.interactionandstorage

class MainActivity : AppCompatActivity() {
    private var notesListView: ListView? = null
    private var notesList: ArrayList<String>? = null
    private var notesAdapter: ArrayAdapter<String>? = null

    @Override
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notesListView = findViewById(R.id.notesListView)
        notesList = ArrayList()
        notesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notesList)
        notesListView.setAdapter(notesAdapter)

        // Load notes from SharedPreferences or File Storage
        loadNotes()

        // Set up ListView item click listener for deletion
        notesListView.setOnItemClickListener({ parent, view, position, id ->
            val selectedNote: String = notesList.get(position)
            val intent: Intent = Intent(this@MainActivity, DeleteNoteActivity::class.java)
            intent.putExtra("note_name", selectedNote)
            startActivity(intent)
        })
    }

    @Override
    fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main_menu, menu)
        return true
    }

    @Override
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_add_note -> {
                startActivity(Intent(this@MainActivity, AddNoteActivity::class.java))
                return true
            }

            R.id.action_delete_note -> {
                startActivity(Intent(this@MainActivity, DeleteNoteActivity::class.java))
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun loadNotes() {
        // Load the notes from the selected storage method (SharedPreferences or File)
        val sharedPreferences: SharedPreferences = getSharedPreferences("notes_prefs", MODE_PRIVATE)
        val notesJson: String = sharedPreferences.getString("notes", "[]")

        // Use a JSON parser to convert the stored string to a list
        try {
            val notesArray: JSONArray = JSONArray(notesJson)
            for (i in 0 until notesArray.length()) {
                notesList.add(notesArray.getString(i))
            }
            notesAdapter.notifyDataSetChanged()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}