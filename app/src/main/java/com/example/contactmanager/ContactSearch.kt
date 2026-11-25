package com.example.contactmanager

internal fun contactMatchesQuery(
    name: String,
    phone: String,
    email: String,
    query: String
): Boolean {
    val normalizedQuery = query.trim()

    if (normalizedQuery.isEmpty()) return true

    return name.contains(normalizedQuery, ignoreCase = true) ||
        phone.contains(normalizedQuery, ignoreCase = true) ||
        email.contains(normalizedQuery, ignoreCase = true)
}
