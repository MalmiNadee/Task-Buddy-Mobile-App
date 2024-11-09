package com.example.taskbuddy


import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskbuddy.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView
import java.text.SimpleDateFormat
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: TaskDbHelper
    private lateinit var taskAdapter: TasksAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDbHelper(this)
        //setup adapter
        taskAdapter = TasksAdapter(db.getAllTasks(), this)

        //setup recycler view
        binding.tasksRecycler.layoutManager = LinearLayoutManager(this)
        binding.tasksRecycler.adapter = taskAdapter

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }
        // Initialize search view
        val searchView = binding.searchView

        // Setup search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filteredTasks = db.getAllTasks().filter { task ->
                    task.title.contains(newText, ignoreCase = true) ||
                            task.priority.toString().equals(newText, ignoreCase = true) ||
                            SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(task.deadline).contains(newText, ignoreCase = true)
                }
                taskAdapter.refreshData(filteredTasks)
                return true
            }
        })


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


       override fun onResume() { //automatically refresh data
           super.onResume()
           taskAdapter.refreshData((db.getAllTasks()))
       }

}