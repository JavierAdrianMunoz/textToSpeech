# SpeechRecognizer MVVM App

Aplicación Android desarrollada en Kotlin que implementa reconocimiento de voz continuo utilizando `SpeechRecognizer`, siguiendo el patrón de arquitectura **MVVM (Model-View-ViewModel)**.

---

## Características

- 🎤 Escucha continua sin detenerse tras una pausa.
- 🧠 Arquitectura MVVM limpia y desacoplada.
- 📝 Texto reconocido se concatena y se muestra en pantalla.
- 🚫 Control de errores: evita reinicios excesivos y detiene si hay muchos fallos consecutivos.
- ✅ Uso de permisos en tiempo de ejecución.
- 📦 Fácil de extender o adaptar a otro flujo de voz.


## 🚀 Cómo usar

1. Clona este repositorio:
   ```bash
   git clone https://github.com/tuusuario/speech-mvvm-app.git
Abre el proyecto en Android Studio.

Concede el permiso de micrófono al ejecutar la app.

Presiona "Iniciar escucha" para comenzar a dictar.

Presiona "Detener escucha" para finalizar.

⚙️ Dependencias
Este proyecto no requiere librerías externas. Usa solo:

SpeechRecognizer (API de Android)

LiveData y ViewModel (Jetpack)

Handler para manejar reinicios controlados

## Permisos requeridos
Asegúrate de declarar en AndroidManifest.xml:

xml
Copiar
Editar
<uses-permission android:name="android.permission.RECORD_AUDIO" />
Y solicitarlo en tiempo de ejecución en MainActivity.

## Manejo de errores
Se controla el reinicio de la escucha según el tipo de error.

Si hay 5 errores consecutivos (ERROR_NO_MATCH, ERROR_NETWORK, etc.), la app detiene la escucha para evitar saturar el sistema.

No se reinicia la escucha ante errores críticos como ERROR_CLIENT, ERROR_RECOGNIZER_BUSY, etc.



## Autor
Desarrollador Javier Adrian

Licencia
MIT License - puedes usar, modificar y distribuir libremente.
