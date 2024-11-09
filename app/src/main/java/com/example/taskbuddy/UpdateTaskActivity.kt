package com.example.taskbuddy

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taskbuddy.databinding.ActivityUpdateTaskBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateTaskBinding
    private lateinit var db: TaskDbHelper
    private  var taskId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDbHelper(this)

        taskId = intent.getIntExtra("task_id",-1)
        if(taskId == -1){
            finish()
            return
        }



            val task = db.getTaskByID(taskId)
            binding.updateTitle.setText(task.title)
            binding.updateDescription.setText(task.description)
            binding.updatePriority.setSelection(Task.Priority.entries.indexOf(task.priority))

            // Parse and set the deadline
            val deadlineDisplayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val deadlineDate = Date(task.deadline)
            binding.updateDeadline.setText(deadlineDisplayFormat.format(deadlineDate))

            binding.updateDeadline.setOnClickListener {
                showDatePickerDialog()
            }


        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitle.text.toString()
            val newDescription = binding.updateDescription.text.toString()
            val newPriority = Task.Priority.entries[binding.updatePriority.selectedItemPosition]
            val newDeadlineString = binding.updateDeadline.text.toString()
            val newDeadline = deadlineDisplayFormat.parse(newDeadlineString)
            val updatedTask = Task(taskId, newTitle, newDescription, newPriority, newDeadline!!.time)


                db.updateTask(updatedTask)
                finish()
                Toast.makeText(this, "Task Updated Successfully", Toast.LENGTH_SHORT).show()
            }




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
                binding.updateDeadline.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

//    private fun parsePriority(priority:String):Task.Priority{
//        return when (priority.uppercase(Locale.getDefault())) {
//            "LOW" -> Task.Priority.LOW
//            "MEDIUM" -> Task.Priority.MEDIUM
//            "HIGH" -> Task.Priority.HIGH
//            else -> Task.Priority.MEDIUM // Default to medium if not recognized
//        }
//    }
}