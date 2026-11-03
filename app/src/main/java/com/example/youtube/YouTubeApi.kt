package com.example.youtube

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.example.model.AppLanguage
import com.example.model.SubscriberRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest

data class YouTubeChannel(
    val id: String,
    val title: String,
    val handle: String,
    val url: String,
    val description: String,
    val thumbnailUrl: String,
    val subscriberCount: Long,
    val videoCount: Long
)

object YouTubeApi {

    suspend fun findByHandle(context: Context, rawHandle: String): Result<YouTubeChannel> =
        withContext(Dispatchers.IO) {
            runCatching {
                val key = YouTubeKey.VALUE
                if (key.isBlank()) error("YouTube API key is missing in this build")
                val handle = rawHandle.trim().removePrefix("@")
                if (handle.length < 2) error("Please enter a valid @handle")

                val query = "part=snippet,statistics" +
                    "&forHandle=" + URLEncoder.encode("@$handle", "UTF-8") +
                    "&key=" + key
                val json = get(context, "https://www.googleapis.com/youtube/v3/channels?$query")

                val items = json.optJSONArray("items")
                if (items == null || items.length() == 0) error("Channel not found for @$handle")

                val item = items.getJSONObject(0)
                val snippet = item.getJSONObject("snippet")
                val stats = item.optJSONObject("statistics")
                val thumbs = snippet.optJSONObject("thumbnails")
                val thumb = thumbs?.optJSONObject("high")?.optString("url").orEmpty()
                    .ifBlank { thumbs?.optJSONObject("medium")?.optString("url").orEmpty() }
                    .ifBlank { thumbs?.optJSONObject("default")?.optString("url").orEmpty() }

                val custom = snippet.optString("customUrl").ifBlank { "@$handle" }
                val finalHandle = if (custom.startsWith("@")) custom else "@$custom"

                YouTubeChannel(
                    id = item.optString("id"),
                    title = snippet.optString("title"),
                    handle = finalHandle,
                    url = "https://www.youtube.com/$finalHandle",
                    description = snippet.optString("description"),
                    thumbnailUrl = thumb,
                    subscriberCount = stats?.optString("subscriberCount")?.toLongOrNull() ?: 0L,
                    videoCount = stats?.optString("videoCount")?.toLongOrNull() ?: 0L
                )
            }
        }

    fun pickRange(subs: Long): SubscriberRange {
        val all = SubscriberRange.values()

        fun lowerBound(r: SubscriberRange): Long {
            val label = r.localizedLabel(AppLanguage.ENGLISH)
            val lower = label.lowercase()
            if (lower.contains("under") || lower.contains("below") || lower.contains("<")) return 0L
            val m = Regex("(\\d+(?:\\.\\d+)?)\\s*([KkMm])?").find(label) ?: return 0L
            val n = m.groupValues[1].toDoubleOrNull() ?: return 0L
            val mult = when (m.groupValues[2].lowercase()) {
                "k" -> 1_000.0
                "m" -> 1_000_000.0
                else -> 1.0
            }
            return (n * mult).toLong()
        }

        return all.filter { lowerBound(it) <= subs }.maxByOrNull { lowerBound(it) }
            ?: all.minByOrNull { lowerBound(it) }
            ?: all.first()
    }

    private fun get(context: Context, url: String): JSONObject {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            conn.setRequestProperty("X-Android-Package", context.packageName)
            certSha1(context)?.let { conn.setRequestProperty("X-Android-Cert", it) }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (code !in 200..299) {
                val msg = try {
                    JSONObject(body).getJSONObject("error").optString("message")
                } catch (e: Exception) {
                    ""
                }
                error("YouTube error $code: $msg")
            }
            return JSONObject(body)
        } finally {
            conn.disconnect()
        }
    }

    private fun certSha1(context: Context): String? {
        return try {
            val pm = context.packageManager
            val pkg = context.packageName
            val first = if (Build.VERSION.SDK_INT >= 28) {
                pm.getPackageInfo(pkg, PackageManager.GET_SIGNING_CERTIFICATES)
                    .signingInfo?.apkContentsSigners?.firstOrNull()
            } else {
                legacyFirstSignature(pm, pkg)
            }
            if (first == null) {
                null
            } else {
                MessageDigest.getInstance("SHA-1")
                    .digest(first.toByteArray())
                    .joinToString("") { "%02X".format(it) }
            }
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("DEPRECATION")
    private fun legacyFirstSignature(pm: PackageManager, pkg: String) =
        pm.getPackageInfo(pkg, PackageManager.GET_SIGNATURES).signatures?.firstOrNull()
}