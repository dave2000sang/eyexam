package com.example.eyexam

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_results.*

class Results : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //overridePendingTransition(R.transition.explode, R.transition.explode)
        setContentView(R.layout.activity_results)
        val resulto = intent.getIntExtra("resulto", 1)
        val resultText = findViewById<TextView>(R.id.resultText)
        println("resulto: " + resulto)
        if(resulto == 0) {
            resultText.setText("Your eyesight is fine: enjoy your day!").toString()
        }
        else {
            resultText.setText("Please visit your optometrist").toString()
        }
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
