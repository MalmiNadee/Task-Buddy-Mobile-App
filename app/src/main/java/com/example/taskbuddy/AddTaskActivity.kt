package com.example.taskbuddy

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle

import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taskbuddy.databinding.ActivityAddTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var db: TaskDbHelper


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDbHelper(this)


        binding.deadline.setOnClickListener {
            showDatePickerDialog()
        }

        binding.saveButton.setOnClickListener{
            val title = binding.title.text.toString()  //take inputs from user
            val description = binding.description.text.toString()
            val priority = binding.priority.selectedItem.toString()

            val deadlineString = binding.deadline.text.toString()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val deadlineDate = dateFormat.parse(deadlineString)

            val deadlineDatePart = Calendar.getInstance()
            deadlineDatePart.time = deadlineDate!!

                val task = Task(0, title, description, parsePriority(priority), deadlineDatePart.timeInMillis)
                db.insertTask(task)
                finish()
                Toast.makeText(this, "Task Added Successfully", Toast.LENGTH_SHORT).show()



        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun parsePriority(priority:String):Task.Priority{
        return when (priority.uppercase(Locale.getDefault())) {
            "LOW" -> Task.Priority.LOW
            "MEDIUM" -> Task.Priority.MEDIUM
            "HIGH" -> Task.Priority.HIGH
            else -> Task.Priority.MEDIUM // Default to medium if not recognized
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, monthOfYear)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }.time
                binding.deadline.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate))
                binding.deadline.tag = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}