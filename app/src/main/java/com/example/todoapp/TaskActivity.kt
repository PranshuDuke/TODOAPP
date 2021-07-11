package com.example.todoapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


const val DB_name = "todo.db"
class TaskActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var myCalendar: Calendar

    lateinit var dateSetLister:DatePickerDialog.OnDateSetListener
    lateinit var timeSetLister:TimePickerDialog.OnTimeSetListener

    var finalDate = 0L
    var finalTime = 0L

    private val label = arrayListOf("Personal","Business","Insurance","Shopping","Banking")

    val db by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        val dateEdit = findViewById<TextInputEditText>(R.id.dtedit)
        dateEdit.setOnClickListener(this)
        val timeEdit = findViewById<TextInputEditText>(R.id.dtedit2)
        timeEdit.setOnClickListener(this)
        savebtn.setOnClickListener(this)

        setUpSpinner()
    }

    private fun setUpSpinner() {
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,label)
        label.sort()
        val spinner = findViewById<Spinner>(R.id.spinner)
        spinner.adapter = adapter
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.dtedit->{
                setListener()
            }
            R.id.dtedit2->{
                setTimeListener()
            }
            R.id.savebtn->{
                saveTodo()
            }
        }
    }

    private fun saveTodo() {
        val category = spinner.selectedItem.toString()
        val title = titleInpLay.editText?.text.toString()
        val description = titleInpLay2.editText?.text.toString()

        GlobalScope.launch(Dispatchers.Main) {
            val id = withContext(Dispatchers.IO) {
                return@withContext db.todoDao().insertTask(
                    TodoModel(
                        title,
                        description,
                        category,
                        finalDate,
                        finalTime
                    )
                )
            }
            finish()
        }

    }

    private fun setTimeListener() {
        myCalendar = Calendar.getInstance()
        timeSetLister = TimePickerDialog.OnTimeSetListener(){ _: TimePicker, hourOfDay:Int, min:Int->
            myCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
            myCalendar.set(Calendar.MINUTE,min)
            updateTime()
        }

        val timePickerDialog = TimePickerDialog(
            this, timeSetLister, myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE),false
        )
        timePickerDialog.show()
    }

    private fun updateTime() {
        val myFormat = "h:mm a"
        val sdf = SimpleDateFormat(myFormat)
        finalTime = myCalendar.time.time
        val timeED = findViewById<TextInputEditText>(R.id.dtedit2)
        timeED.setText(sdf.format(myCalendar.time))

    }

    private fun setListener() {
        myCalendar = Calendar.getInstance()
        dateSetLister = DatePickerDialog.OnDateSetListener{ _: DatePicker, year:Int, month:Int, dayOfMonth:Int->
            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDate()
        }

        val datePickerDialog = DatePickerDialog(
            this, dateSetLister, myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        val myFormat = "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat)
        finalDate = myCalendar.time.time
        val dateEdit = findViewById<TextInputEditText>(R.id.dtedit)
        dateEdit.setText(sdf.format(myCalendar.time))

        val timeInputLay = findViewById<TextInputLayout>(R.id.titleInpLay4)
        timeInputLay.visibility = View.VISIBLE
    }
}