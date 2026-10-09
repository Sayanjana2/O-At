package com.example.domain.model

data class NotificationModel(
    val id: String,
    val appName: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val category: String, // "Message", "System", "Calendar", "Transfer"
    val isRead: Boolean = false
)
