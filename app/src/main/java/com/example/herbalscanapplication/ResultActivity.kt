package com.example.herbalscanapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.herbalscanapplication.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val responseTextView = findViewById<TextView>(R.id.tv_nama_tanaman)
        val response = intent.getStringExtra("response")
        responseTextView.text = response

    }
}