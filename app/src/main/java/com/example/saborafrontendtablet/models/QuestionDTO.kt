package com.example.saborafrontendtablet.models

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = RangeQuestionDTO::class, name = "RANGE"),
    JsonSubTypes.Type(value = UniqueAnswerQuestionDTO::class, name = "UNIQUE_ANSWER"),
    JsonSubTypes.Type(value = MultipleAnswerQuestionDTO::class, name = "MULTIPLE_ANSWER"),
    JsonSubTypes.Type(value = AnswerRedactionQuestionDTO::class, name = "REDACTION")
)

sealed class QuestionDTO(
    open val id: Int,
    open val question: String
)

data class RangeQuestionDTO(
    override val id: Int,
    override val question: String,
    val min: Int,
    val max: Int,
    val interval: Int
) : QuestionDTO(id, question)

data class MultipleAnswerQuestionDTO(
    override val id: Int,
    override val question: String,
    val options: List<String>
) : QuestionDTO(id, question)

data class UniqueAnswerQuestionDTO(
    override val id: Int,
    override val question: String,
    val options: List<String>
) : QuestionDTO(id, question)

data class AnswerRedactionQuestionDTO(
    override val id: Int,
    override val question: String
) : QuestionDTO(id, question)
