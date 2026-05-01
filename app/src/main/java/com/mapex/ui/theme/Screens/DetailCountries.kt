package com.mapex.ui.theme.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DetailCountriesScreen() {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center,
	) {
		Text(text = "Detail Countries")
	}

}