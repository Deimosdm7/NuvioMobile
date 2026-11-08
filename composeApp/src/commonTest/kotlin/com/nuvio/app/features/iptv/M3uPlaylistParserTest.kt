package com.nuvio.app.features.iptv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class M3uPlaylistParserTest {
    @Test
    fun parsesExtendedMetadataAndStreamUrls() {
        val playlist = M3uPlaylistParser.parse(
            """#EXTM3U
#EXTINF:-1 tvg-id="news.br" tvg-name="News BR" tvg-logo="https://example.com/logo.png" group-title="News",Canal de Notícias
https://example.com/live.m3u8
""",
        )

        assertEquals(1, playlist.channels.size)
        val channel = playlist.channels.single()
        assertEquals("Canal de Notícias", channel.name)
        assertEquals("https://example.com/live.m3u8", channel.streamUrl)
        assertEquals("news.br", channel.tvgId)
        assertEquals("News BR", channel.tvgName)
        assertEquals("https://example.com/logo.png", channel.logoUrl)
        assertEquals("News", channel.groupTitle)
        assertTrue(playlist.warnings.isEmpty())
    }

    @Test
    fun parsesNamesContainingCommasAndExtGroupFallback() {
        val playlist = M3uPlaylistParser.parse(
            """#EXTM3U
#EXTGRP:Esportes
#EXTINF:-1,Canal, com vírgula
rtmp://example.com/live
""",
        )

        assertEquals("Canal, com vírgula", playlist.channels.single().name)
        assertEquals("Esportes", playlist.channels.single().groupTitle)
    }

    @Test
    fun supportsBomAndCrLfAndReportsMissingStream() {
        val playlist = M3uPlaylistParser.parse(
            "\uFEFF#EXTM3U\r\n#EXTINF:-1,Canal sem endereço\r\n",
        )

        assertTrue(playlist.channels.isEmpty())
        assertEquals(1, playlist.warnings.size)
    }

    @Test
    fun ignoresCommentsAndBlankLinesBetweenEntryAndUrl() {
        val playlist = M3uPlaylistParser.parse(
            """#EXTM3U

#EXTINF:-1 group-title="Filmes",Canal teste
#EXTVLCOPT:http-user-agent=Player
# comentário
https://example.com/stream
""",
        )

        assertEquals("Canal teste", playlist.channels.single().name)
        assertEquals("https://example.com/stream", playlist.channels.single().streamUrl)
    }
}
