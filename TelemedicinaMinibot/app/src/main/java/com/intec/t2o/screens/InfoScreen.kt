package com.intec.t2o.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.intec.t2o.R
import com.intec.t2o.components.GoBackButton
import com.intec.t2o.components.InfoScreenButtonCard
import com.intec.t2o.ui.theme.textColor
import com.intec.t2o.viewmodels.MqttViewModel
import com.intec.t2o.viewmodels.NumericPanelViewModel


@Composable
fun InfoScreen(
    navController: NavController,
    mqttViewModel: MqttViewModel,
    numericPanelViewModel: NumericPanelViewModel
) {
    val iconos = listOf(
        Icons.Default.DateRange,
        Icons.Default.Build,
        Icons.Default.Build,
    )
    val textos = listOf("Cita", "Ejemplo", "Ejemplo")
    val indicaciones =
        listOf(
            "\"quiero más pedir cita...\"",
            "\"tengo una ejemplo...\"",
            "\"tengo una ejemplo...\""
        )

    FuturisticGradientBackground {
        GoBackButton(onClick = { mqttViewModel.navigateToHomeScreen() })
        Text(
            text = "Info Screen:",
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            style = MaterialTheme.typography.headlineLarge
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .wrapContentWidth(),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                modifier = Modifier.padding(top = 80.dp),
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
            ) {
                items(3) { index ->

                    InfoScreenButtonCard(
                        text = textos[index],
                        indicacion = indicaciones[index],
                        icon = iconos[index]
                    )
                    {
                        mqttViewModel.clearRecognizedText()
                        when (index) {
                            0 -> mqttViewModel.navigateToInfoScreen()
                            1 -> mqttViewModel.navigateToNumericPanelScreen()
                            2 -> mqttViewModel.navigateToPackageAndMailManagementScreen()
                        }
                    }
                }
            }
        }
    }
}


