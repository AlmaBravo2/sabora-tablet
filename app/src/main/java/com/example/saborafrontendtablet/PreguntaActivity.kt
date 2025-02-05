package com.example.saborafrontendtablet

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.saborafrontendtablet.models.AnswerRedactionQuestionDTO
import com.example.saborafrontendtablet.models.FormDTO
import com.example.saborafrontendtablet.models.MultipleAnswerQuestionDTO
import com.example.saborafrontendtablet.models.QuestionDTO
import com.example.saborafrontendtablet.models.RangeQuestionDTO
import com.example.saborafrontendtablet.models.UniqueAnswerQuestionDTO
import com.example.saborafrontendtablet.network.FormsLogic
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.json.JSONArray
import org.json.JSONObject

class PreguntaActivity : AppCompatActivity() {

    private val formsLogic = FormsLogic()
    private val formIds = listOf(29, 30)  // IDs de los formularios en orden
    private var currentFormIndex = 0  // Índice del formulario actual
    private lateinit var questions: List<QuestionDTO>  // Lista de preguntas
    private var currentQuestionIndex = 0  // Índice de la pregunta actual
    private val answers = mutableMapOf<Int, Any>() // Mapa para guardar respuestas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregunta)

        val formsLogic = FormsLogic()
        val formId = "29"

        formsLogic.getFormById(formId) { response ->
            // Si la respuesta es nula, mostramos un error
            if (response == null) {
                Toast.makeText(this, "Error al obtener el formulario", Toast.LENGTH_LONG).show()
                return@getFormById
            }
            // Procesamos la respuesta JSON
            try {
                val objectMapper = jacksonObjectMapper()
                val form = objectMapper.readValue<FormDTO>(response.toString())

                // Ahora tienes el formulario completo, incluyendo las preguntas
                // Puedes acceder a las preguntas a través de form.questions
                questions = form.questions

                // Mostrar la primera pregunta (como ejemplo)
                if (questions.isNotEmpty()) {
                    mostrarPregunta(questions[currentQuestionIndex])
                } else {
                    Toast.makeText(this, "No hay preguntas disponibles", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("PreguntaActivity", "Error al procesar el formulario: ${e.message}")
            }

        }
        // Botón siguiente para avanzar entre preguntas
        findViewById<Button>(R.id.buttonSiguiente).setOnClickListener {
            avanzarPregunta()
        }
    }

    // Método para mostrar una pregunta dependiendo de su tipo
    private fun mostrarPregunta(question: QuestionDTO) {
        when (question) {
            is RangeQuestionDTO -> {
                // Aquí puedes mostrar una UI de tipo rango (por ejemplo, SeekBar)
                Log.i("PreguntaActivity", "Pregunta de tipo RANGE: ${question.question}")
                // Cargar UI para Rango (SeekBar o similar)
                mostrarPreguntaRango(question)
            }

            is UniqueAnswerQuestionDTO -> {
                // Aquí puedes mostrar una UI de selección única (por ejemplo, RadioButton)
                Log.i("PreguntaActivity", "Pregunta de tipo UNIQUE_ANSWER: ${question.question}")
                // Cargar UI para Respuesta Única (RadioButton o similar)
                mostrarPreguntaUnica(question)
            }

            is MultipleAnswerQuestionDTO -> {
                // Aquí puedes mostrar una UI de selección múltiple (por ejemplo, Checkboxes)
                Log.i("PreguntaActivity", "Pregunta de tipo MULTIPLE_ANSWER: ${question.question}")
                // Cargar UI para Respuesta Múltiple (Checkboxes o similar)
                mostrarPreguntaMultiple(question)
            }

            is AnswerRedactionQuestionDTO -> {
                // Aquí puedes mostrar una UI de texto para redactar una respuesta
                Log.i("PreguntaActivity", "Pregunta de tipo REDACTION: ${question.question}")
                // Cargar UI para Redacción (EditText o similar)
                mostrarPreguntaRedaccion(question)
            }
        }
    }

    // Método para avanzar a la siguiente pregunta


    private fun avanzarPregunta() {

        val question = questions[currentQuestionIndex]

        // Validar si el usuario seleccionó una respuesta
        if (!answers.containsKey(question.id)) {
            Toast.makeText(this, "Por favor, selecciona una respuesta", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            mostrarPregunta(questions[currentQuestionIndex])
        } else {
            enviarRespuestas()
            //Toast.makeText(this, "No hay más preguntas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enviarRespuestas() {
        val formsLogic = FormsLogic()
        val formId = 29 // Reemplazar con el ID real del formulario
        val experienceId = 4 // Se debe obtener el ID real de la experiencia
        val userDni = "weJi1HX7ylzFUTWCSDdpAg==" // Se debe obtener dinámicamente del usuario autenticado

        // Crear un JSONArray para las respuestas
        val answersArray = JSONArray()

        // Construir la lista de respuestas como un JSONArray
        answers.forEach { (questionId, answer) ->
            val answerObject = JSONObject().apply {
                put("id", 0) // ID de la respuesta (según la API, podría ser 0 si es nueva)
                put("questionId", questionId)
                put("answer", answer.toString())
            }
            answersArray.put(answerObject) // Agregar el objeto de respuesta al JSONArray
        }

        // Crear el JSON final
        val jsonObject = JSONObject().apply {
            put("formId", formId)
            put("experienceId", experienceId)
            put("userDni", userDni)
            put("answers", answersArray)
        }
        Log.i("Enviando respuestas", jsonObject.toString())
        // Enviar respuestas
        formsLogic.sendAnswers(jsonObject) { success, message ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Respuestas enviadas correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e("Error al enviar respuestas", "Mensaje de error: $message") // Aquí registramos el error en Logcat
                    Toast.makeText(this, "Error al enviar respuestas: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Método para mostrar una pregunta de tipo RANGE
    private fun mostrarPreguntaRango(question: RangeQuestionDTO) {
        val customFont = ResourcesCompat.getFont(this, R.font.inter)

        // Establecer el texto de la pregunta
        val questionText = findViewById<TextView>(R.id.questionText).apply{
            text = question.question
            setTextColor(Color.BLACK)
            textSize = 40f
            typeface = customFont
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT

            ).apply {
                setMargins(40, 100, 40, 16) // Márgenes
            }
        }
        //questionText.text = question.question

        // Obtener el contenedor donde vamos a agregar los elementos dinámicos
        val rangeContainer = findViewById<LinearLayout>(R.id.rangeContainer)

        // Limpiar el contenedor para evitar duplicados
        rangeContainer.removeAllViews()

        // Crear un grupo de RadioButtons
        val radioGroup = RadioGroup(this).apply{
            orientation = RadioGroup.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 150, 0, 40) // Margen superior de 16px (puedes ajustarlo)
            }
        }
        //radioGroup.orientation = RadioGroup.HORIZONTAL

        // Variable para restaurar selección previa
        var selectedRadioButtonId: Int? = null

        // Crear los RadioButtons dinámicamente basados en el rango
        for (value in question.min..question.max step question.interval) {
            // Crear el RadioButton
            val radioButton = RadioButton(this).apply {
                text = value.toString()
                //id = value // Le damos un ID único basado en el valor
                id = View.generateViewId()
                textSize = 50f // Aumentar tamaño de texto (en SP)
                typeface = customFont
                setPadding(20, 16, 50, 16) // Aumentar espacio interno
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Si ya había una respuesta guardada, restaurar la selección
            if (answers[question.id]?.toString() == value.toString()) {
                selectedRadioButtonId = radioButton.id
            }

            // Añadir el RadioButton al grupo
            radioGroup.addView(radioButton)
        }
        // Restaurar selección previa si existe
        selectedRadioButtonId?.let {
            radioGroup.check(it)
        }

        // Agregar listener para capturar la selección
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            if (selectedRadioButton != null) {
                answers[question.id] = selectedRadioButton.text.toString()

            }
        }

        // Añadir el grupo de RadioButtons al contenedor
        rangeContainer.addView(radioGroup)

    }

    // Método para mostrar una pregunta de tipo UNIQUE_ANSWER
    private fun mostrarPreguntaUnica(question: UniqueAnswerQuestionDTO) {
        val customFont = ResourcesCompat.getFont(this, R.font.inter)

        val questionText = findViewById<TextView>(R.id.questionText).apply {
            text = question.question
            setTextColor(Color.BLACK)
            textSize = 40f
            typeface = customFont
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 24, 0, 20) // Margen superior e inferior
            }
        }
        // Similar al tipo RANGE, puedes agregar un RadioGroup con las opciones
       /* val questionText = findViewById<TextView>(R.id.questionText)
        questionText.text = question.question
        questionText.setTextColor(Color.BLACK)*/
        // Obtener el contenedor donde vamos a agregar los elementos dinámicos
        val rangeContainer = findViewById<LinearLayout>(R.id.rangeContainer)
        // Limpiar el contenedor para evitar duplicados
        rangeContainer.removeAllViews()

        val radioGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL// Centra el RadioGroup dentro del contenedor
                setMargins(0, 16, 0, 5)
            }
            gravity = Gravity.CENTER_HORIZONTAL // Centra los RadioButtons dentro del RadioGroup
        }

        // Crear los RadioButtons dinámicamente con las opciones
        question.options.forEachIndexed { index, option ->
            val radioButton = RadioButton(this).apply {
                text = option
                id = View.generateViewId() // Genera un ID único
                textSize = 26f // Ajusta el tamaño del texto
                setTextColor(Color.BLACK)
                typeface = ResourcesCompat.getFont(context, R.font.inter)
                setPadding(10, 12, 10, 10) // Ajusta el padding interno
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(30, 10, 30, 10) // Ajusta los márgenes exteriores
                    gravity = Gravity.CENTER_HORIZONTAL
                }

            }
            radioGroup.addView(radioButton)
        }
        // Agregar listener para capturar la selección
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            if (selectedRadioButton != null) {
                answers[question.id] = selectedRadioButton.text.toString()
            }
        }

        // Agregar el grupo de RadioButtons al contenedor
        rangeContainer.addView(radioGroup)
    }

    // Método para mostrar una pregunta de tipo MULTIPLE_ANSWER
    private fun mostrarPreguntaMultiple(question: MultipleAnswerQuestionDTO) {
        // Similar al tipo UNIQUE_ANSWER, puedes agregar CheckBoxes con las opciones
    }

    // Método para mostrar una pregunta de tipo REDACTION
    private fun mostrarPreguntaRedaccion(question: AnswerRedactionQuestionDTO) {
        // Aquí podrías agregar un EditText donde el usuario pueda redactar la respuesta
    }
}