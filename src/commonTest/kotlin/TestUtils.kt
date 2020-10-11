import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalUnsignedTypes::class)
fun assertUByteArrayEquals(expected: UByteArray, actual: UByteArray) {
    try {
        assertTrue(
            expected.contentEquals(actual)
        )
    } catch (e: AssertionError) {
        // Print prettier error message
        assertEquals(
            expected.contentToString(), actual.contentToString()
        )

        // rethrow original error just in case strings somehow match
        throw e
    }
}
