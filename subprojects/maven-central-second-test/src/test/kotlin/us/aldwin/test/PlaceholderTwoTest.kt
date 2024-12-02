package us.aldwin.test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlaceholderTwoTest {
    @Test
    fun `it returns the expected information`() {
        assertEquals("Placeholder: Initial Version (2)", PlaceholderTwo.placeholder())
    }
}
