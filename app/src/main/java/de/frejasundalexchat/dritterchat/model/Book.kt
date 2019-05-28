package de.frejasundalexchat.dritterchat.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Book(@Id var id: Long, val title: String, val imgUrl: String, val pageCount: Int)