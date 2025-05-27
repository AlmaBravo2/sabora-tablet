package com.example.saborafrontendtablet.forms

data class Pregunta (

    val tipo: String,
    val id: Int,
    val texto: String,
    val opciones: List<String>? = null,
    val min: Int? = null,
    val max: Int? = null,
    val interval: Int? = null,


)
