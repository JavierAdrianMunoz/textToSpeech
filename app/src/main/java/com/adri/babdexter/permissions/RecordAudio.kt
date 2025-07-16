package com.adri.babdexter.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.adri.babdexter.audioRecognition.Audio

class RecordAudio(private val context: Context, private val activity: Activity) {
    private val TAG = "RecordAudio"
    private val audio = Audio(context, activity)
    companion object {
        const val REQUEST_RECORD_AUDIO = 200
    }
    interface PermissionCallback {
        fun onPermissionGranted()
        fun onPermissionDenied()
    }

    interface SpeechRecognitionCallback {
        //fun startSpeechRecognition()
        fun startSpeechOfflineRecognition()
        fun stopSpeechRecognition()
    }
    private var permissionCallback: PermissionCallback? = null
    private var speechRecognitionCallback: SpeechRecognitionCallback? = null

    fun setSpeechRecognitionCallback(callback: SpeechRecognitionCallback) {
        this.speechRecognitionCallback = callback
    }

    fun checkAudioPermission(callback: PermissionCallback) {
        this.permissionCallback = callback

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionCallback?.onPermissionGranted()
            //speechRecognitionCallback?.startSpeechRecognition() // Cambio clave aquí
            toggleRecognition("checkAudioPermission")
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO
            )
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult")
        when (requestCode) {

            REQUEST_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCallback?.onPermissionGranted()
                    //speechRecognitionCallback?.startSpeechRecognition() // Cambio clave aquí
                    toggleRecognition("onRequestPermissionsResult")
                } else {
                    permissionCallback?.onPermissionDenied()
                }
            }
        }
    }

    @Deprecated("Reemplazado por toggleRecognition")
    fun toggleRecognition_old(FROM:String) {
        Log.i(TAG, "toggleRecognition and isListening: ${audio.isListening()} FROM: $FROM")
        if (audio.isListening()) {
            audio.stopSpeechRecognition()
            speechRecognitionCallback?.stopSpeechRecognition()
        } else {
            //speechRecognitionCallback?.startSpeechRecognition()
            //speechRecognitionCallback?.startSpeechOfflineRecognition(audio.isListening())
        }
    }

    fun toggleRecognition(FROM: String) {
        Log.d(TAG, "toggleRecognition and isListening: ${audio.isListening()} FROM: $FROM")
        if (audio.isListening()) {
            audio.stopSpeechRecognition()
            speechRecognitionCallback?.stopSpeechRecognition()
        } else {
            speechRecognitionCallback?.startSpeechOfflineRecognition()
        }
    }

}