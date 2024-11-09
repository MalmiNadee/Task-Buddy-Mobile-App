package com.example.taskbuddy

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TasksAdapter(private var tasks:List<Task>, context:Context):
    RecyclerView.Adapter<TasksAdapter.TaskViewHolder>()
{
        private val db: TaskDbHelper = TaskDbHelper(context)
        private val coroutineScope = CoroutineScope(Dispatchers.Main)


    class TaskViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val titleTextView: TextView = itemView.findViewById(R.id.titleText)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionText)
        val priorityTextView: TextView = itemView.findViewById(R.id.priorityText)
        val deadlineTextView: TextView = itemView.findViewById(R.id.deadlineText)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder { //setup item layout view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item,parent,false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = tasks.size //write the size



    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) { //set data on the element =>holder - help to set data, position - helps to determine which item clicked
        val task = tasks[position]
        holder.titleTextView.text = task.title
        holder.descriptionTextView.text = task.description
        holder.priorityTextView.text = task.priority.toString()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val deadlineDateFormatted = dateFormat.format(task.deadline)
        holder.deadlineTextView.text = deadlineDateFormatted

        holder.updateButton.setOnClickListener{
            val intent = Intent(holder.itemView.context,UpdateTaskActivity::class.java).apply {
                putExtra("task_id", task.id)
            }
            holder.itemView.context.startActivity((intent))
        }

        holder.deleteButton.setOnClickListener {

                db.deleteTask(task.id)
                //refresh remain data
                refreshData(db.getAllTasks())
                Toast.makeText(
                    holder.itemView.context,
                    "Task Deleted Successfully",
                    Toast.LENGTH_SHORT
                ).show()


        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(newTasks: List<Task>){
        tasks = newTasks
        notifyDataSetChanged() //inform something changed in data
    }




}