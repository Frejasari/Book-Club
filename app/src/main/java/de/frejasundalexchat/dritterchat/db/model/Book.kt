package de.frejasundalexchat.dritterchat.db.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Book(
    @Id var id: Long,
    val createdAd: String,
    val title: String,
    val author: String,
    val currentPage: Int = 0,
    val totalPages: Int,
    val notes: String,
    val coverUrl: String = ""
)