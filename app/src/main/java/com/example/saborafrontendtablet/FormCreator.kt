package com.example.saborafrontendtablet

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.saborafrontendtablet.EsperandoExperiencia.Companion.context
import com.example.saborafrontendtablet.LogIn.Companion.loggedUserName
import com.example.saborafrontendtablet.forms.Pregunta
import com.example.saborafrontendtablet.network.FormsLogic
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder

class FormCreator : AppCompatActivity() {

    private val listaPreguntas = mutableListOf<Pregunta>()  // Aquí definimos la lista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.form_creator)
        context = this

        val spinnerPrimera = findViewById<Spinner>(R.id.spinnerTipoPrimeraPregunta)
        val layoutDinamico = findViewById<LinearLayout>(R.id.layoutDinamicoPrimeraPregunta)
        val fuente = ResourcesCompat.getFont(this@FormCreator, R.font.inter)

        val opcionesTipo = listOf("Escoge un tipo de respuesta", "Selección única", "Escala de Likert")


        val contenedorPreguntas = findViewById<LinearLayout>(R.id.contenedorPreguntas)
        val botonAnyadir = findViewById<Button>(R.id.buttonNuevaPregunta)


        botonAnyadir.setOnClickListener {
            val bloquePregunta = layoutInflater.inflate(R.layout.bloque_nueva_pregunta, contenedorPreguntas, false)
            val spinner = bloquePregunta.findViewById<Spinner>(R.id.spinnerTipoPregunta)
            val layoutDinamico = bloquePregunta.findViewById<LinearLayout>(R.id.layoutDinamico)

            val opcionesTipo = listOf("Escoge un tipo de respuesta", "Selección única", "Escala de Likert")

            // Adapter personalizado para el hint
            val adapter = object : ArrayAdapter<String>(
                this,
                R.layout.spinner_item_form_creator,
                opcionesTipo
            ) {
                override fun isEnabled(position: Int): Boolean {
                    return position != 0 // Desactiva el hint
                }

                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getDropDownView(position, convertView, parent) as TextView
                    view.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                    return view
                }
            }

            spinner.adapter = adapter
            spinner.setSelection(0) // Mostrar hint por defecto

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    layoutDinamico.removeAllViews()

                    if (position == 1) {
                        val numeroInput = EditText(this@FormCreator).apply {
                            hint = "Número de opciones"
                            inputType = InputType.TYPE_CLASS_NUMBER
                            textSize = 16f
                            typeface = fuente

                            val scale = resources.displayMetrics.density
                            val anchoEnDp = 200
                            val margenInicioDp = 24

                            val params = LinearLayout.LayoutParams(
                                (anchoEnDp * scale).toInt(), // ancho personalizado en píxeles
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            params.setMargins((margenInicioDp * scale).toInt(), 16, 16, 16) // margen: izquierda, arriba, derecha, abajo
                            layoutParams = params
                        }
                        layoutDinamico.addView(numeroInput)

                        numeroInput.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                                layoutDinamico.removeViews(1, layoutDinamico.childCount - 1)

                                val cantidad = s.toString().toIntOrNull() ?: return
                                for (i in 1..cantidad) {
                                    val scale = resources.displayMetrics.density
                                    val anchoEnDp = 200
                                    val margenInicioDp = 50

                                    val params = LinearLayout.LayoutParams(
                                        (anchoEnDp * scale).toInt(),
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    params.setMargins((margenInicioDp * scale).toInt(), 50, 50, 30)


                                    val opcion = EditText(this@FormCreator).apply {
                                        hint = "Opción $i"
                                        textSize = 16f
                                        typeface = fuente
                                        layoutParams = params

                                    }
                                    layoutDinamico.addView(opcion)
                                }
                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        })

                    } else if (position == 2) {
                        val layoutLikert = LinearLayout(this@FormCreator).apply {
                            orientation = LinearLayout.HORIZONTAL
                        }

                        val scale = resources.displayMetrics.density
                        val anchoEnDp = 100
                        val margenEntreInputs = 16

                        val paramsMin = LinearLayout.LayoutParams(
                            (anchoEnDp * scale).toInt(),
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 0, (margenEntreInputs * scale).toInt(), 0) // margen derecho para separación
                        }

                        val paramsMax = LinearLayout.LayoutParams(
                            (anchoEnDp * scale).toInt(),
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )


                        val inputMin = EditText(this@FormCreator).apply {
                            hint = "De"
                            inputType = InputType.TYPE_CLASS_NUMBER
                            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                            textSize = 16f
                            typeface = fuente
                            layoutParams = paramsMin
                        }

                        val inputMax = EditText(this@FormCreator).apply {
                            hint = "A"
                            inputType = InputType.TYPE_CLASS_NUMBER
                            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                            textSize = 16f
                            typeface = fuente
                            layoutParams = paramsMax
                        }

                        layoutLikert.addView(inputMin)
                        layoutLikert.addView(inputMax)
                        layoutDinamico.addView(layoutLikert)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            val botonEliminar = bloquePregunta.findViewById<Button>(R.id.buttonEliminarPregunta)
            botonEliminar.setOnClickListener {
                contenedorPreguntas.removeView(bloquePregunta)
            }

            contenedorPreguntas.addView(bloquePregunta)
        }

        // Adapter personalizado para el hint
        val adapter = object : ArrayAdapter<String>(
            this,
            R.layout.spinner_item_form_creator,
            opcionesTipo
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0 // Desactiva el hint
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                return view
            }
        }

        spinnerPrimera.adapter = adapter
        spinnerPrimera.setSelection(0) // Mostrar hint por defecto

        //spinner.adapter = ArrayAdapter(this, R.layout.spinner_item_form_creator, opcionesTipo)

        spinnerPrimera.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                layoutDinamico.removeAllViews()
                if (position == 1) { // Selección única
                    val numeroInput = EditText(this@FormCreator).apply {
                        hint = "Número de opciones"
                        inputType = InputType.TYPE_CLASS_NUMBER
                        textSize = 16f
                        typeface = fuente

                        val scale = resources.displayMetrics.density
                        val anchoEnDp = 200
                        val margenInicioDp = 24

                        val params = LinearLayout.LayoutParams(
                            (anchoEnDp * scale).toInt(), // ancho personalizado en píxeles
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins((margenInicioDp * scale).toInt(), 16, 16, 16) // margen: izquierda, arriba, derecha, abajo
                        layoutParams = params
                    }
                    layoutDinamico.addView(numeroInput)

                    numeroInput.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            layoutDinamico.removeViews(1, layoutDinamico.childCount - 1)

                            val cantidad = s.toString().toIntOrNull() ?: return
                            for (i in 1..cantidad) {
                                val scale = resources.displayMetrics.density
                                val anchoEnDp = 200
                                val margenInicioDp = 50

                                val params = LinearLayout.LayoutParams(
                                    (anchoEnDp * scale).toInt(),
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                params.setMargins((margenInicioDp * scale).toInt(), 50, 50, 30)


                                val opcion = EditText(this@FormCreator).apply {
                                    hint = "Opción $i"
                                    textSize = 16f
                                    typeface = fuente
                                    layoutParams = params

                                }
                                layoutDinamico.addView(opcion)
                            }
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }
                    })

                } else if (position == 2) { // Escala de Likert
                    val layoutLikert = LinearLayout(this@FormCreator).apply {
                        orientation = LinearLayout.HORIZONTAL
                    }

                    val scale = resources.displayMetrics.density
                    val anchoEnDp = 100
                    val margenEntreInputs = 16

                    val paramsMin = LinearLayout.LayoutParams(
                        (anchoEnDp * scale).toInt(),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, (margenEntreInputs * scale).toInt(), 0) // margen derecho para separación
                    }

                    val paramsMax = LinearLayout.LayoutParams(
                        (anchoEnDp * scale).toInt(),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    val inputMin = EditText(this@FormCreator).apply {
                        hint = "De"
                        inputType = InputType.TYPE_CLASS_NUMBER
                        textSize = 16f
                        typeface = fuente
                        layoutParams = paramsMin


                    }

                    val inputMax = EditText(this@FormCreator).apply {
                        hint = "A"
                        inputType = InputType.TYPE_CLASS_NUMBER
                        textSize = 16f
                        typeface = fuente
                        layoutParams = paramsMax

                    }

                    layoutLikert.addView(inputMin)
                    layoutLikert.addView(inputMax)
                    layoutDinamico.addView(layoutLikert)
                }
            }


            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }



        val botonEnviarFormulario = findViewById<Button>(R.id.guardarFormButton)

        botonEnviarFormulario.setOnClickListener {
            val nombreFormulario = findViewById<EditText>(R.id.tituloForm).text
            val foodSpecialist = LogIn.loggedUserDni ?: "12345678A"
            val formId = (0..9999).random()                // Genera un ID o usa uno real

            val preguntas = mutableListOf<Pregunta>()

            val textoPrimeraPregunta = findViewById<EditText>(R.id.primeraPregunta)?.text?.toString()
            val spinnerPrimeraPregunta = findViewById<Spinner>(R.id.spinnerTipoPrimeraPregunta)
            val tipoPrimera = spinnerPrimeraPregunta?.selectedItem?.toString()

            if (textoPrimeraPregunta.isNullOrBlank() || tipoPrimera == "Escoge un tipo de respuesta") {
                Toast.makeText(this, "Completa la primera pregunta antes de guardar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tipoPreguntaPrimera = when (tipoPrimera) {
                "Selección única" -> "MULTIPLE_ANSWER"
                "Escala de Likert" -> "RANGE"
                else -> "UNKNOWN"
            }

            var opcionesPrimera: List<String>? = null
            var minPrimera: Int? = null
            var maxPrimera: Int? = null
            var intervalPrimera: Int? = null

            val layoutDinamicoPrimera = findViewById<LinearLayout>(R.id.layoutDinamicoPrimeraPregunta)

            if (tipoPreguntaPrimera == "MULTIPLE_ANSWER") {
                opcionesPrimera = mutableListOf()
                for (j in 1 until layoutDinamicoPrimera.childCount) {
                    val opcion = (layoutDinamicoPrimera.getChildAt(j) as? EditText)?.text?.toString()
                    if (!opcion.isNullOrBlank()) {
                        (opcionesPrimera as MutableList).add(opcion)
                    } else {
                        Toast.makeText(this, "Completa todas las opciones de la primera pregunta.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
            } else if (tipoPreguntaPrimera == "RANGE") {
                val layoutLikert = layoutDinamicoPrimera.getChildAt(0) as LinearLayout
                val minEdit = layoutLikert.getChildAt(0) as EditText
                val maxEdit = layoutLikert.getChildAt(1) as EditText
                minPrimera = minEdit.text.toString().toIntOrNull()
                maxPrimera = maxEdit.text.toString().toIntOrNull()
                intervalPrimera = 1
            }

            preguntas.add(
                Pregunta(
                    tipo = tipoPreguntaPrimera,
                    id = (1000..9999).random(),
                    texto = textoPrimeraPregunta!!,
                    opciones = opcionesPrimera,
                    min = minPrimera,
                    max = maxPrimera,
                    interval = intervalPrimera
                )
            )


            val contenedorPreguntas = findViewById<LinearLayout>(R.id.contenedorPreguntas)

            for (i in 0 until contenedorPreguntas.childCount) {
                val bloque = contenedorPreguntas.getChildAt(i) as ViewGroup
                val textoPregunta = bloque.findViewById<EditText>(R.id.editTextPregunta)?.text?.toString()
                val tipo = bloque.findViewById<Spinner>(R.id.spinnerTipoPregunta)?.selectedItem?.toString()

                if (textoPregunta.isNullOrBlank() || tipo == "Escoge un tipo de respuesta") {
                    Toast.makeText(this, "Por favor, completa todas las preguntas antes de guardar.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener  // Detiene el proceso de guardado
                }

                val tipoPregunta = when (tipo) {
                    "Selección única" -> "MULTIPLE_ANSWER"
                    "Escala de Likert" -> "RANGE"
                    else -> "UNKNOWN"
                }

                var opciones : List<String>? = null
                val opcionesLayout = bloque.findViewById<LinearLayout>(R.id.layoutDinamico)
                var min: Int? = null
                var max: Int? = null
                var interval: Int? = null

                if (tipoPregunta == "MULTIPLE_ANSWER") {
                    opciones = mutableListOf()
                    for (j in 1 until opcionesLayout.childCount) {
                        val opcion = (opcionesLayout.getChildAt(j) as? EditText)?.text?.toString()
                        if (!opcion.isNullOrBlank()) {
                            (opciones as MutableList).add(opcion)
                        }else{
                            Toast.makeText(this, "Por favor, completa todas las preguntas antes de guardar.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener  // Detiene el proceso de guardado
                        }
                    }

                }


                else if (tipoPregunta == "RANGE") {
                    val layoutLikert = opcionesLayout.getChildAt(0) as LinearLayout
                    val minEdit = layoutLikert.getChildAt(0) as EditText
                    val maxEdit = layoutLikert.getChildAt(1) as EditText
                    min = minEdit.text.toString().toIntOrNull()
                    max = maxEdit.text.toString().toIntOrNull()
                    interval = 1
                }

                preguntas.add(
                    Pregunta(
                        tipo = tipoPregunta,
                        id = (1000..9999).random(),
                        texto = textoPregunta!!,
                        opciones = opciones,
                        min = min,
                        max = max,
                        interval = interval
                    )
                )

                }

            val gson = GsonBuilder().create()
            val json = gson.toJson(preguntas)

            Log.d("JSON_OUTPUT", json)

            // Llama al método de lógica
            val formsLogic = FormsLogic()
            formsLogic.enviarForm(formId, nombreFormulario.toString(), foodSpecialist, preguntas)
        }
        }

    companion object {

        lateinit var context: Context


        fun getAppContext(): Context {
            return context
        }
    }
}