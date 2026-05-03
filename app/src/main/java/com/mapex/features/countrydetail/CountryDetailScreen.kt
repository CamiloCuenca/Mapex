package com.mapex.features.countrydetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mapex.domain.model.Country
import com.mapex.features.countrylist.FlagImage
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    viewModel: CountryDetailViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Text(
                            "Cargando detalles…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "No se pudo cargar el país.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = state.error!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = onBackClick) {
                            Text("Volver")
                        }
                    }
                }
            }

            state.country != null -> {
                CountryDetailContent(
                    country = state.country!!,
                    onBackClick = onBackClick
                )
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("País no encontrado")
                }
            }
        }
    }
}

// ── Full detail content ───────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CountryDetailContent(
    country: Country,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón volver superior izquierdo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            OutlinedButton(
                onClick = onBackClick,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Volver",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Bandera
        FlagImage(
            url = country.flags,
            description = "Bandera de ${country.commonName}",
            modifier = Modifier
                .width(100.dp)
                .height(65.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(Modifier.height(16.dp))

        // Nombre
        Text(
            text = country.commonName,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        // Continente (Verde)
        val continentText = if (country.continents.isNotEmpty()) {
            country.continents.joinToString(" · ").uppercase()
        } else if (country.region.isNotBlank()) {
            country.region.uppercase()
        } else {
            ""
        }
        
        if (continentText.isNotBlank()) {
            Text(
                text = continentText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.2.em,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        // Grid 2x2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // POBLACION
            GridCard(
                label = "POBLACIÓN",
                value = if (country.population > 0) formatNumberShort(country.population) else "N/A",
                modifier = Modifier.weight(1f)
            )
            // AREA
            GridCard(
                label = "ÁREA KM²",
                value = if (country.area != null) formatNumberShort(country.area.toInt()) else "N/A",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // PIB (Gini as placeholder if PIB is not available, or just standard)
            GridCard(
                label = "ÍNDICE GINI",
                value = if (country.gini != null) "%.1f".format(country.gini) else "N/A",
                modifier = Modifier.weight(1f)
            )
            // DENSIDAD
            val density = if (country.population > 0 && country.area != null && country.area > 0) {
                (country.population / country.area).roundToInt()
            } else null
            
            GridCard(
                label = "DENSIDAD",
                value = if (density != null) "$density/km²" else "N/A",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(32.dp))

        // ── INFORMACION GENERAL ───────────────────────────────────────────────────
        DetailSection(title = "INFORMACIÓN GENERAL") {
            DetailRow("Capital", if (country.capital.isNotEmpty()) country.capital.joinToString(", ") else "N/A")
            DetailRow("Idioma", if (country.languages.isNotEmpty()) country.languages.joinToString(", ") else "N/A")
            val currencyStr = country.currencies.joinToString(", ")
            DetailRow("Moneda", if (currencyStr.isNotBlank()) currencyStr else "N/A")
            DetailRow("Continente", if (country.continents.isNotEmpty()) country.continents.joinToString(", ") else country.region)
        }

        Spacer(Modifier.height(16.dp))

        // ── INDICADORES ───────────────────────────────────────────────────
        DetailSection(title = "INDICADORES") {
            DetailRow("Esperanza vida", "N/A") // Not in model yet, placeholder
            DetailRow("Código ISO", country.codeAlpha2)
            DetailRow("Zona horaria", if (country.timezones.isNotEmpty()) country.timezones.first() else "N/A")
            DetailRow("Sin Litoral", if (country.landlocked) "Sí" else "No")
        }
        
        // ── FRONTERAS ───────────────────────────────────────────────────────
        if (country.borders.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            DetailSection(title = "FRONTERIZOS") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    country.borders.forEach { border ->
                        AssistChip(
                            onClick = {},
                            label = { Text(border, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ── UI helpers ────────────────────────────────────────────────────────────────

@Composable
private fun GridCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.height(86.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.05.em
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary // Azul vibrante
            )
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.1.em,
                color = MaterialTheme.colorScheme.primary, // Azul en cualquier tema
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    maxLines: Int = 1
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f),
            maxLines = maxLines,
            textAlign = TextAlign.End
        )
    }
}

private fun formatNumberShort(number: Int): String = when {
    number >= 1_000_000_000 -> "${number / 1_000_000_000.0}B".replace(".0B", "B")
    number >= 1_000_000 -> "${number / 1_000_000.0}M".replace(".0M", "M")
    number >= 1_000 -> "${number / 1_000.0}K".replace(".0K", "K")
    else -> number.toString()
}
