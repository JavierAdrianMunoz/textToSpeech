package com.adri.babdexter.viewmodel

import android.content.Context
import android.content.Intent
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class SpeechViewModel : ViewModel() {

    companion object {
        private const val TAG = "SpeechViewModel"
        private const val MAX_ERRORS_ALLOWED = 5
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var consecutiveErrors = 0

    private val _resultText = MutableLiveData("")
    val resultText: LiveData<String> = _resultText

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "✅ Ready for speech")
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "🎤 Comenzó a hablar")
        }

        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {
            Log.d(TAG, "📴 Fin del habla detectado")
        }

        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
                SpeechRecognizer.ERROR_CLIENT -> "Error del cliente (no reiniciar)"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permisos insuficientes"
                SpeechRecognizer.ERROR_NETWORK -> "Error de red"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Tiempo de red agotado"
                SpeechRecognizer.ERROR_NO_MATCH -> "No se encontró coincidencia"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconocedor ocupado"
                SpeechRecognizer.ERROR_SERVER -> "Error del servidor"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tiempo de espera de voz agotado"
                else -> "Error desconocido ($error)"
            }

            Log.e(TAG, "❌ onError: $errorMessage (código $error), isListening=$isListening")

            // No reiniciar si no estamos escuchando o el error es grave
            if (!isListening ||
                error == SpeechRecognizer.ERROR_CLIENT ||
                error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ||
                error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY
            ) {
                stopListening()
                return
            }

            // Reiniciar con límite de errores
            if (consecutiveErrors < MAX_ERRORS_ALLOWED) {
                consecutiveErrors++
                Handler(Looper.getMainLooper()).postDelayed({
                    restartListening()
                }, 1000)
            } else {
                Log.w(TAG, "⚠️ Demasiados errores consecutivos. Se detiene.")
                stopListening()
            }
        }

        override fun onResults(results: Bundle?) {
            consecutiveErrors = 0

            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                val spokenText = it.firstOrNull()
                spokenText?.let { text ->
                    val current = _resultText.value.orEmpty()
                    _resultText.value = "$current $text".trim()
                }
            }

            if (isListening) {
                Handler(Looper.getMainLooper()).postDelayed({
                    restartListening()
                }, 500)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    fun setSpeechRecognizer(sr: SpeechRecognizer) {
        speechRecognizer = sr
        sr.setRecognitionListener(recognitionListener)
    }

    fun startListening(context: Context) {
        if (speechRecognizer == null || isListening) return

        isListening = true
        consecutiveErrors = 0

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        try {
            speechRecognizer?.startListening(intent)
            Log.d(TAG, "🎙️ Escucha iniciada")
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error al iniciar escucha: ${e.message}")
            isListening = false
        }
    }

    fun stopListening() {
        if (!isListening) return
        isListening = false
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            Log.d(TAG, "🛑 Escucha detenida")
        } catch (e: Exception) {
            Log.e(TAG, "Error al detener escucha: ${e.message}")
        }
    }

    private fun restartListening() {
        try {
            speechRecognizer?.cancel()
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            }
            speechRecognizer?.startListening(intent)
            Log.i(TAG, "🔁 Reiniciando escucha...")
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error al reiniciar escucha: ${e.message}")
            isListening = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
        speechRecognizer?.destroy()
    }
}
