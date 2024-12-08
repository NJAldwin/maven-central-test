package us.aldwin.test

/**
 * A placeholder object for testing Maven Central publishing.
 */
public object PlaceholderTwo {
    private const val EXPECTED = "Initial Version (2)"

    /**
     * This is a placeholder function that returns a greeting string.
     */
    public fun placeholder(): String {
        return "Placeholder: $EXPECTED"
    }

    /**
     * This is a placeholder function that returns the original placeholder string from [Placeholder].
     */
    public fun originalPlaceholder(): String {
        return Placeholder.placeholder()
    }
}
