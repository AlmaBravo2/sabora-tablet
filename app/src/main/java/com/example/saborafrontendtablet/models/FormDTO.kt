package com.example.saborafrontendtablet.models

import java.util.Date

data class FormDTO(
    val id: Int?,
    val name: String,
    val foodSpecialist: String,
    val creationDate: Date,
    val questions: List<QuestionDTO>

    )