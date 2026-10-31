plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.secrets) apply false
    alias(libs.plugins.google.services) apply false
}

// Automatically ensure debug.keystore exists from debug.keystore.base64 if missing
val debugKeystore = file("debug.keystore")
val debugKeystoreB64 = file("debug.keystore.base64")
if (!debugKeystore.exists() && debugKeystoreB64.exists()) {
    try {
        val cleanBase64 = debugKeystoreB64.readText().replace("\r", "").replace("\n", "").trim()
        val decoded = java.util.Base64.getDecoder().decode(cleanBase64)
        debugKeystore.writeBytes(decoded)
    } catch (_: Exception) {}
}