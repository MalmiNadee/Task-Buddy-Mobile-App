package com.example.taskbuddy

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskDbHelper(context:Context) : SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_NAME = "tasksapp.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "alltasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PRIORITY = "priority"
        private const val COLUMN_DEADLINE = "deadline"
    }

    override fun onCreate(db: SQLiteDatabase?) { //create table
        val createTableQuery = "CREATE TABLE $TABLE_NAME" +
                "($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, " +
                "$COLUMN_DESCRIPTION TEXT, $COLUMN_PRIORITY TEXT, $COLUMN_DEADLINE TEXT)"
        db?.execSQL(createTableQuery)  //to execute
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {  //drop table
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertTask(task:Task){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_TITLE,task.title)
            put(COLUMN_DESCRIPTION,task.description)
            put(COLUMN_PRIORITY,task.priority.name)
            put(COLUMN_DEADLINE, task.deadline)
        }
        db.insert(TABLE_NAME,null,values)
        db.close()
    }

    fun getAllTasks(): List<Task> {
        val tasksList = mutableListOf<Task>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_DEADLINE DESC"
        val cursor = db.rawQuery(query,null)  //used to iterate through rows of the table



        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val priorityString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)) // Retrieve as String
            val priority = Task.Priority.valueOf(priorityString) // Convert String to Task.Priority enum

            val deadline = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE)) // Retrieve as Long

            val task = Task(id, title.toString(), description.toString(),priority,deadline)
            tasksList.add(task)

        }
        cursor.close()
        db.close()
        return tasksList  //list which consist all the retrieved data from db
    }

    //Update Tasks

     fun updateTask(task: Task) {

            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_TITLE, task.title)   //add variables
                put(COLUMN_DESCRIPTION, task.description)
                put(COLUMN_PRIORITY, task.priority.name)
                put(COLUMN_DEADLINE, task.deadline.toString())
            }
            val whereClause = "$COLUMN_ID = ?"
            val whereArgs = arrayOf(task.id.toString())
            db.update(TABLE_NAME, values, whereClause, whereArgs)
            db.close()
        }


     fun getTaskByID(taskId: Int): Task {

            val db = readableDatabase
            val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $taskId"
            val cursor = db.rawQuery(query, null)
            cursor.moveToFirst()

            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val priorityString =
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)) // Retrieve as String
            val priority = Task.Priority.valueOf(priorityString)
            val deadline =
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE)) // Retrieve as Long


            cursor.close()
            db.close()
            return Task(id, title, description, priority, deadline)


    }

    //Delete Tasks

    fun deleteTask(taskId: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(taskId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
























}