plugins {
    id("com.android.application") version "9.2.1" apply false
    id("org.jetbrains.kotlin.android") version "2.3.10" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
