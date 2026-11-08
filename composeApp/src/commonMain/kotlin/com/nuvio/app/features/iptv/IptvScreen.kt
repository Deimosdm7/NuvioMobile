package com.nuvio.app.features.iptv

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IptvScreen(
    onPlayChannel: (IptvChannel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var playlistText by rememberSaveable { mutableStateOf("") }
    var playlist by rememberSaveable { mutableStateOf(M3uPlaylist(emptyList())) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("TV ao vivo", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Adicione uma lista M3U colando o conteúdo abaixo. A importação por URL e o salvamento de listas serão adicionados nas próximas etapas.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = playlistText,
            onValueChange = { playlistText = it; error = null },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 7,
            label = { Text("Conteúdo da lista M3U") },
            placeholder = { Text("#EXTM3U\\n#EXTINF:-1,Canal\\nhttps://...") },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val parsed = M3uPlaylistParser.parse(playlistText)
                playlist = parsed
                error = if (parsed.channels.isEmpty()) "Nenhum canal válido foi encontrado." else null
            }) { Text("Importar lista") }
            Text(
                text = "${playlist.channels.size} canais",
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        playlist.warnings.take(2).forEach { warning ->
            Text(warning, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(playlist.channels, key = { it.streamUrl }) { channel ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onPlayChannel(channel) },
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(channel.name, style = MaterialTheme.typography.titleMedium)
                        channel.groupTitle?.takeIf { it.isNotBlank() }?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("Toque para reproduzir", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
