package com.adri.babdexter.audioRecognition

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.TextView
import android.widget.Toast
//import com.adri.babdexter.ui.MainActivity.Companion.REQUEST_SPEECH_RECOGNITION
import java.util.Locale

class Audio {
    private val TAG = "Audio"
    private val context: Context
    private val activity: Activity
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    public fun setListening(isListening: Boolean) {
        this.isListening = isListening
    }

    constructor(context: Context, activity: Activity) {
        this.context = context
        this.activity = activity
    }

/*    fun startSpeechRecognition() {
        if (isListening) return

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora...")
        }

        try {
            activity.startActivityForResult(intent, REQUEST_SPEECH_RECOGNITION)
            isListening = true
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "El reconocimiento de voz no está disponible", Toast.LENGTH_SHORT).show()
        }
    }*/
    // Método para detener el reconocimiento
    fun stopSpeechRecognition() {
        if (isListening) {
            // Detener reconocimiento offline si está activo
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null

            isListening = false
        }
    }
    // offline model
    fun startSpeechRecognitionOffline(textView: TextView) {
        if (isListening) {
            Toast.makeText(context, "Ya estoy escuchando", Toast.LENGTH_SHORT).show()
            return
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray) {}
                override fun onEndOfSpeech() {
                    isListening = false
                }

                override fun onError(error: Int) {
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
                        SpeechRecognizer.ERROR_CLIENT -> "Error del cliente"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permisos insuficientes"
                        SpeechRecognizer.ERROR_NETWORK -> "Error de red"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Tiempo de espera de red agotado"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No se encontró coincidencia"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconocedor ocupado"
                        SpeechRecognizer.ERROR_SERVER -> "Error del servidor"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tiempo de espera de voz agotado"
                        else -> "Error desconocido: $error"
                    }

                    Log.e(TAG, "onError: $errorMessage y isListening: $isListening")

                    // 🔁 Reiniciar escucha tras error si seguía escuchando
                    if (!isListening) {

                        Handler(Looper.getMainLooper()).postDelayed({
                            restartListening()
                        }, 500)
                    }
                }


                override fun onResults(results: Bundle) {
                    val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    matches?.get(0)?.let { text ->
                        // Procesar texto reconocido
                        Log.d(TAG, "Texto reconocido: $text")
                        textView.text = text
                    }
                    if (!isListening) {
                        restartListening()
                    }
                }

                override fun onPartialResults(partialResults: Bundle) {}
                override fun onEvent(eventType: Int, params: Bundle) {}
            })
            restartListening()
        }

        // Iniciar escucha
        speechRecognizer?.startListening(
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            }
        )
        isListening = true
    }

    // Detener cuando ya no se necesite
    fun onDestroy() {
        stopSpeechRecognition()
    }

    // Verificar estado
    fun isListening(): Boolean {
        return isListening
    }
    private fun restartListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizer?.startListening(intent)
        Log.d(TAG, "Reiniciando escucha")
    }

}