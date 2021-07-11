package com.example.todoapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao {
    @Insert
    suspend fun insertTask(todoModel: TodoModel):Long

    @Query("SELECT * FROM TodoModel where isFinished == 0")
    fun getTask():LiveData<List<TodoModel>>

    @Query("update TodoModel set isFinished = 1 where id=:uid")
    fun finishTask(uid:Long)

    @Query("delete from TodoModel where id=:uid")
    fun deleteTask(uid: Long)
}