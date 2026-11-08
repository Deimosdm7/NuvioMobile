package com.nuvio.app.features.iptv

/**
 * A channel parsed from a user-provided M3U/M3U8 playlist.
 *
 * The parser deliberately stores source metadata only; it does not fetch or play streams.
 */
data class IptvChannel(
    val name: String,
    val streamUrl: String,
    val tvgId: String? = null,
    val tvgName: String? = null,
    val logoUrl: String? = null,
    val groupTitle: String? = null,
    val attributes: Map<String, String> = emptyMap(),
)

data class M3uPlaylist(
    val channels: List<IptvChannel>,
    val warnings: List<String> = emptyList(),
)

/**
 * Small, platform-independent parser for the common extended M3U format used by IPTV providers.
 *
 * Supports #EXTINF metadata (including quoted attributes), #EXTGRP, CRLF/LF line endings,
 * blank lines, and stream URLs on the line following each #EXTINF record.
 */
object M3uPlaylistParser {
    fun parse(content: String): M3uPlaylist {
        val lines = content.removePrefix("\uFEFF").lineSequence().map(String::trim).toList()
        val channels = mutableListOf<IptvChannel>()
        val warnings = mutableListOf<String>()
        var pending: PendingChannel? = null
        var currentGroup: String? = null

        lines.forEachIndexed { index, line ->
            when {
                line.isBlank() -> Unit
                line.startsWith("#EXTINF:", ignoreCase = true) -> {
                    if (pending != null) {
                        warnings += "Entry before line ${index + 1} has no stream URL."
                    }
                    pending = parseExtInf(line.substringAfter(':'))
                    if (pending == null) warnings += "Invalid EXTINF record on line ${index + 1}."
                }
                line.startsWith("#EXTGRP:", ignoreCase = true) -> {
                    currentGroup = line.substringAfter(':').trim().ifBlank { null }
                }
                line.startsWith("#") -> Unit
                pending != null -> {
                    val entry = pending!!
                    channels += IptvChannel(
                        name = entry.name.ifBlank { line.substringAfterLast('/').ifBlank { "Unknown channel" } },
                        streamUrl = line,
                        tvgId = entry.attributes["tvg-id"],
                        tvgName = entry.attributes["tvg-name"],
                        logoUrl = entry.attributes["tvg-logo"],
                        groupTitle = entry.attributes["group-title"] ?: currentGroup,
                        attributes = entry.attributes,
                    )
                    pending = null
                    currentGroup = null
                }
            }
        }

        if (pending != null) warnings += "Final entry has no stream URL."
        return M3uPlaylist(channels = channels, warnings = warnings)
    }

    private fun parseExtInf(value: String): PendingChannel? {
        val separator = findTitleSeparator(value)
        if (separator < 0) return null
        val metadata = value.substring(0, separator)
        val title = value.substring(separator + 1).trim()
        val attributes = ATTRIBUTE_REGEX.findAll(metadata).associate { match ->
            match.groupValues[1] to match.groupValues[2].ifEmpty { match.groupValues[3] }
        }
        return PendingChannel(name = title, attributes = attributes)
    }

    private fun findTitleSeparator(value: String): Int {
        var quoted = false
        value.forEachIndexed { index, char ->
            when (char) {
                '"' -> quoted = !quoted
                ',' -> if (!quoted) return index
            }
        }
        return -1
    }

    private data class PendingChannel(
        val name: String,
        val attributes: Map<String, String>,
    )

    private val ATTRIBUTE_REGEX = Regex("""([\\w-]+)\\s*=\\s*(?:"([^"]*)"|([^\\s,]*))""")
}
