package com.example.contactmanager

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactSearchTest {

    @Test
    fun emptyQueryMatchesAllContacts() {
        assertTrue(
            contactMatchesQuery(
                "Alex", "+77015550123",
                "alex@example.com", ""
            )
        )
    }

    @Test
    fun searchByNameIgnoresCase() {
        assertTrue(
            contactMatchesQuery(
                "Alex Johnson", "+77015550123",
                "alex@example.com", "ALEX"
            )
        )
    }

    @Test
    fun searchByPhone() {
        assertTrue(
            contactMatchesQuery(
                "Alex", "+77015550123",
                "alex@example.com", "55501"
            )
        )
    }

    @Test
    fun searchByEmail() {
        assertTrue(
            contactMatchesQuery(
                "Alex", "+77015550123",
                "alex@example.com", "example.com"
            )
        )
    }

    @Test
    fun searchTrimsExtraSpaces() {
        assertTrue(
            contactMatchesQuery(
                "Alex Johnson", "+77015550123",
                "alex@example.com", "  ALEX  "
            )
        )
    }

    @Test
    fun incorrectQueryReturnsFalse() {
        assertFalse(
            contactMatchesQuery(
                "Alex", "+77015550123",
                "alex@example.com", "Meirambay"
            )
        )
    }
}
