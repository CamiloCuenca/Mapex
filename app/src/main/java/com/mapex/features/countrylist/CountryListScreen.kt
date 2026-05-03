package com.mapex.features.countrylist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.PublicOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.SubcomposeAsyncImage
import com.mapex.R
import com.mapex.domain.model.Country
import com.mapex.ui.components.ShimmerBox
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryListScreen(
    viewModel: CountryListViewModel,
    onCountrySelected: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagedCountries = viewModel.pagedCountries.collectAsLazyPagingItems()
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val isTopVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex <= 1
        }
    }
    val showFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 5
        }
    }

    // Lógica para Sticky Header (Letra inicial) súper robusta
    val currentInitial = remember(listState.firstVisibleItemIndex, pagedCountries.itemCount) {
        var initial = ""
        val visibleIndex = listState.firstVisibleItemIndex
        
        if (pagedCountries.itemCount > 0) {
            val safeIndex = minOf(visibleIndex, pagedCountries.itemCount - 1)
            for (i in safeIndex downTo 0) {
                val item = pagedCountries.peek(i)
                if (item != null) {
                    val firstLetter = item.commonName.trim().firstOrNull()?.uppercaseChar()?.toString()
                    if (!firstLetter.isNullOrBlank() && firstLetter[0].isLetter()) {
                        initial = firstLetter
                        break
                    }
                }
            }
        }
        initial
    }

    // Mapa de letras para el scroll lateral
    val alphabetMap = remember(state.countries, state.searchQuery, state.selectedContinent) {
        val map = mutableMapOf<Char, Int>()
        val filtered = state.countries.filter { country ->
            val matchesSearch = country.commonName.contains(state.searchQuery, ignoreCase = true)
            val matchesContinent = state.selectedContinent.isEmpty() || 
                country.continents.any { it.equals(state.selectedContinent, ignoreCase = true) }
            matchesSearch && matchesContinent
        }.sortedBy { it.commonName }

        filtered.forEachIndexed { index, country ->
            val firstLetter = country.commonName.trim().firstOrNull()?.uppercaseChar()
            if (firstLetter != null && !map.containsKey(firstLetter) && firstLetter.isLetter()) {
                map[firstLetter] = index
            }
        }
        map.toList().sortedBy { it.first }
    }

    var isDraggingAlphabet by remember { androidx.compose.runtime.mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            
            // MAPEX Custom TopBar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Mapex Logo",
                    modifier = Modifier.size(36.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "MAPEX",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.1.em,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "WORLD EXPLORER",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 0.1.em,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Cabecera retráctil
            AnimatedVisibility(
                visible = isTopVisible,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Column {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.searchByName(it) },
                        placeholder = { Text("Buscar país…") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent,
                        )
                    )

                    if (state.allContinents.isNotEmpty()) {
                        val allContinents = remember(state.allContinents) {
                            listOf("") + state.allContinents
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(allContinents) { continent ->
                                val selected = state.selectedContinent == continent
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.filterByContinent(continent) },
                                    label = { Text(if (continent.isEmpty()) "Todos" else continent) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    border = null
                                )
                            }
                        }
                    }
                }
            }

            // Contador de países
            if (pagedCountries.itemCount > 0) {
                Text(
                    text = "${pagedCountries.itemCount} PAÍSES",
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 0.2.em,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            when {
                state.isLoading && pagedCountries.itemCount == 0 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp
                            )
                            Text(
                                text = "Sincronizando países...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                state.error != null && pagedCountries.itemCount == 0 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "No se pudo cargar la lista de países.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = state.error!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(onClick = { viewModel.reloadCountries() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                pagedCountries.itemCount == 0 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(96.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.PublicOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                "No encontramos coincidencias",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Intenta ajustar tus filtros de búsqueda o el continente seleccionado para ver más países.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    viewModel.searchByName("")
                                    viewModel.filterByContinent("")
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Limpiar filtros")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp), // Espacio para el FAB
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            count = pagedCountries.itemCount,
                            key = pagedCountries.itemKey { it.id },
                            contentType = pagedCountries.itemContentType { "CountryCard" }
                        ) { index ->
                            val country = pagedCountries[index]
                            if (country != null) {
                                CountryListItem(
                                    country = country,
                                    onClick = { onCountrySelected(country.id) }
                                )
                            }
                        }
                        
                        // Indicador de "Cargando más" para el scroll infinito
                        if (pagedCountries.loadState.append is androidx.paging.LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.tertiary,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Lateral Alphabet Scroller
        if (alphabetMap.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp, top = 80.dp, bottom = 80.dp)
                    .pointerInput(alphabetMap) {
                        awaitEachGesture {
                            try {
                                val down = awaitFirstDown()
                                isDraggingAlphabet = true
                                var y = down.position.y
                                var index = (y / size.height * alphabetMap.size)
                                    .toInt()
                                    .coerceIn(0, alphabetMap.size - 1)
                                
                                coroutineScope.launch {
                                    listState.scrollToItem(alphabetMap[index].second)
                                }

                                do {
                                    val event = awaitPointerEvent()
                                    val dragEvent = event.changes.firstOrNull()
                                    if (dragEvent != null && dragEvent.pressed) {
                                        dragEvent.consume()
                                        y = dragEvent.position.y
                                        val newIndex = (y / size.height * alphabetMap.size)
                                            .toInt()
                                            .coerceIn(0, alphabetMap.size - 1)
                                        if (newIndex != index) {
                                            index = newIndex
                                            coroutineScope.launch {
                                                listState.scrollToItem(alphabetMap[newIndex].second)
                                            }
                                        }
                                    }
                                } while (event.changes.any { it.pressed })
                            } finally {
                                isDraggingAlphabet = false
                            }
                        }
                    },
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val currentInitialChar = currentInitial.firstOrNull() ?: ' '
                alphabetMap.forEach { (letter, _) ->
                    val isActive = letter == currentInitialChar || (isDraggingAlphabet && letter == currentInitialChar)
                    Text(
                        text = letter.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Normal,
                        fontSize = if (isActive) 14.sp else 10.sp,
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
                    )
                }
            }
        }

        // Gran letra en el centro al hacer drag
        AnimatedVisibility(
            visible = isDraggingAlphabet && currentInitial.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                ),
                modifier = Modifier.size(96.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = currentInitial,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Floating Action Button: Volver Arriba
        AnimatedVisibility(
            visible = showFab,
            enter = slideInVertically(initialOffsetY = { it * 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it * 2 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 16.dp) // extra padding por seguridad
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Volver arriba")
            }
        }
    }
}

@Composable
fun CountryListItem(
    country: Country,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Acento verde a la izquierda (como en la imagen para el primer elemento, o en todos)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            )
            
            Spacer(Modifier.width(12.dp))

            FlagImage(
                url = country.flags,
                description = "Bandera de ${country.commonName}",
                modifier = Modifier
                    .size(width = 46.dp, height = 32.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = country.commonName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                val subtitle = buildString {
                    if (country.capital.isNotEmpty()) append(country.capital.first())
                    if (country.capital.isNotEmpty() && country.region.isNotBlank()) append(" · ")
                    if (country.region.isNotBlank() && country.region != "N/A") append(country.region)
                }
                
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            if (country.population > 0) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(
                        text = formatPopulationShort(country.population),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.tertiary // Verde
                    )
                    Text(
                        text = "habitantes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun FlagImage(
    url: String?,
    description: String,
    modifier: Modifier = Modifier
) {
    @Composable
    fun Placeholder() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("🏳️", style = MaterialTheme.typography.bodySmall)
        }
    }

    if (url == null) {
        Box(modifier = modifier) { Placeholder() }
        return
    }

    SubcomposeAsyncImage(
        model = url,
        contentDescription = description,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        loading = {
            ShimmerBox(modifier = Modifier.matchParentSize())
        },
        error = {
            Placeholder()
        }
    )
}

private fun formatPopulationShort(population: Int): String = when {
    population >= 1_000_000_000 -> "${population / 1_000_000_000}B"
    population >= 1_000_000 -> "${population / 1_000_000}M"
    population >= 1_000 -> "${population / 1_000}K"
    else -> population.toString()
}
