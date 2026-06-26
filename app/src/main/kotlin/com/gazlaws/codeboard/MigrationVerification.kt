package com.gazlaws.codeboard

/**
 * Dummy Kotlin class to verify that the project successfully compiles Kotlin code
 * as part of the modernization migration.
 */
class MigrationVerification {
    fun checkInteroperability() {
        // Just referencing the class is enough for compilation check
        val className = MainActivity::class.java.simpleName
        println("Successfully verified interoperability with Java class: $className")
    }
}
