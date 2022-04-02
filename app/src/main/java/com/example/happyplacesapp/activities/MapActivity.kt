package com.example.happyplacesapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplacesapp.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {

    var binding: ActivityMapBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.toolbarActivityMap?.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}