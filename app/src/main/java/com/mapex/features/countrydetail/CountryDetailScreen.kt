package com.mapex.features.countrydetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mapex.domain.model.Country
import com.mapex.features.countrylist.FlagImage
import com.mapex.ui.components.ShimmerBox
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    viewModel: CountryDetailViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        state.country?.commonName ?: "Detalles del País",
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
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
                    modifier = Modifier.padding(innerPadding)
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Hero flag ──────────────────────────────────────────────────────
        FlagImage(
            url = country.flags,
            description = "Bandera de ${country.commonName}",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        // ── Country name ───────────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = country.commonName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = country.officialName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (country.continents.isNotEmpty()) {
                Text(
                    text = country.continents.joinToString(" · "),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // ── Quick stats row ────────────────────────────────────────────────
        if (country.population > 0 || country.area != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (country.population > 0) {
                    StatCard(
                        label = "Población",
                        value = formatNumber(country.population),
                        modifier = Modifier.weight(1f)
                    )
                }
                if (country.area != null) {
                    StatCard(
                        label = "Área km²",
                        value = formatNumber(country.area.toInt()),
                        modifier = Modifier.weight(1f)
                    )
                }
                if (country.landlocked) {
                    StatCard(
                        label = "Sin litoral",
                        value = "Sí",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ── General info ───────────────────────────────────────────────────
        DetailCard(title = "Información General") {
            DetailRow("Región", country.region)
            DetailRow("Subregión", country.subregion)
            DetailRow("Código ISO 2", country.codeAlpha2)
            DetailRow("Código ISO 3", country.codeAlpha3)
            if (country.tld.isNotEmpty()) {
                DetailRow("Dominio TLD", country.tld.joinToString(", "))
            }
            if (!country.callingCode.isNullOrBlank()) {
                DetailRow("Código tel.", country.callingCode)
            }
        }

        // ── Geography ──────────────────────────────────────────────────────
        DetailCard(title = "Geografía") {
            if (country.capital.isNotEmpty()) {
                DetailRow("Capital", country.capital.joinToString(", "))
            }
            if (country.latlng.size >= 2) {
                DetailRow(
                    label = "Coordenadas",
                    value = "%.4f°, %.4f°".format(country.latlng[0], country.latlng[1])
                )
            }
            DetailRow("Sin litoral", if (country.landlocked) "Sí" else "No")
            if (!country.carSide.isNullOrBlank()) {
                DetailRow(
                    "Conducción",
                    if (country.carSide.equals("right", ignoreCase = true)) "Derecha" else "Izquierda"
                )
            }
            if (!country.startOfWeek.isNullOrBlank()) {
                DetailRow("Inicio semana", country.startOfWeek.replaceFirstChar { it.uppercase() })
            }
        }

        // ── Languages ─────────────────────────────────────────────────────
        if (country.languages.isNotEmpty()) {
            DetailCard(title = "Idiomas") {
                DetailRow("Idiomas", country.languages.joinToString(", "))
            }
        }

        // ── Currencies ────────────────────────────────────────────────────
        if (country.currencies.isNotEmpty()) {
            DetailCard(title = "Monedas") {
                country.currencies.forEach { currency ->
                    DetailRow("•", currency, maxLines = 2)
                }
            }
        }

        // ── Timezones ─────────────────────────────────────────────────────
        if (country.timezones.isNotEmpty()) {
            DetailCard(title = "Zonas Horarias") {
                DetailRow(
                    label = "Zonas",
                    value = country.timezones.joinToString(", "),
                    maxLines = Int.MAX_VALUE
                )
            }
        }

        // ── Social / Misc ──────────────────────────────────────────────────
        val hasMiscData = country.gini != null || !country.googleMapsUrl.isNullOrBlank()
        if (hasMiscData) {
            DetailCard(title = "Datos Adicionales") {
                if (country.gini != null) {
                    DetailRow(
                        "Índice Gini",
                        "%.1f".format(country.gini)
                    )
                }
                if (!country.googleMapsUrl.isNullOrBlank()) {
                    DetailRow("Google Maps", country.googleMapsUrl, maxLines = 2)
                }
            }
        }

        // ── Borders ───────────────────────────────────────────────────────
        if (country.borders.isNotEmpty()) {
            DetailCard(title = "Países Fronterizos") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    country.borders.forEach { border ->
                        // M3 AssistChip for each border country code
                        AssistChip(
                            onClick = {},
                            label = { Text(border, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }

        // ── Coat of arms ──────────────────────────────────────────────────
        if (!country.coatOfArms.isNullOrBlank()) {
            DetailCard(title = "Escudo de Armas") {
                FlagImage(
                    url = country.coatOfArms,
                    description = "Escudo de ${country.commonName}",
                    modifier = Modifier
                        .size(width = 100.dp, height = 120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ── UI helpers ────────────────────────────────────────────────────────────────

/** M3 OutlinedCard section container */
@Composable
private fun DetailCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)   // M3 large radius
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
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
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.38f)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.62f),
            maxLines = maxLines
        )
    }
}

/** Compact stat card used in the quick-stats row */
@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),   // M3 medium radius
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

private fun formatNumber(number: Int): String =
    NumberFormat.getNumberInstance(Locale.getDefault()).format(number)

