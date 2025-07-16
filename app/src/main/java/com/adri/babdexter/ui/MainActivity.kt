package com.adri.babdexter.ui

import com.adri.babdexter.R
import com.adri.babdexter.viewmodel.SpeechViewModel
import android.Manifest
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private val viewModel: SpeechViewModel by viewModels()

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var textOutput: TextView

    private val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            initSpeechRecognizer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.btn_start)
        stopButton = findViewById(R.id.btn_stop)
        textOutput = findViewById(R.id.tv_result)

        permissionRequest.launch(Manifest.permission.RECORD_AUDIO)

        viewModel.resultText.observe(this, Observer {
            textOutput.text = it
        })

        startButton.setOnClickListener {
            viewModel.startListening(this)
        }

        stopButton.setOnClickListener {
            viewModel.stopListening()
        }
    }

    private fun initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        viewModel.setSpeechRecognizer(speechRecognizer)
    }

    override fun onDestroy() {
        viewModel.stopListening()
        speechRecognizer.destroy()
        super.onDestroy()
    }
}
