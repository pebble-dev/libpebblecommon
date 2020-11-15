import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

@OptIn(ExperimentalContracts::class)
inline fun <reified T> assertIs(obj: Any) {
    contract {
        returns() implies (obj is T)
    }
    assertTrue(
        obj is T,
        "$obj should be ${T::class.simpleName}"
    )
}
