package com.capstone.project.trashhub.view.success

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.capstone.project.trashhub.R
import com.capstone.project.trashhub.view.home.HomeActivity
import java.text.SimpleDateFormat

class SuccessActivity : AppCompatActivity() {

    private lateinit var buttonBackHome : Button
    private lateinit var date : TextView
    private lateinit var dateFormat : SimpleDateFormat
    private lateinit var calendar: Calendar

    @SuppressLint("SimpleDateFormat", "WeekBasedYear")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        buttonBackHome = findViewById(R.id.button_back_to_home)
        date = findViewById(R.id.tv_tanggal_berhasil_pesan)

        dateFormat = SimpleDateFormat("EE, dd MM YY")
        calendar = Calendar.getInstance()

        date.text = dateFormat.format(calendar.time)
        buttonBackHome.setOnClickListener {
            Intent(this,HomeActivity::class.java).let {
                startActivity(it)
            }
        }
    }
}