package com.intec.t2o.screens

import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.ainirobot.coreservice.client.actionbean.Pose
import com.intec.t2o.R
import com.intec.t2o.chatGPT.ChatGPTManager
import com.intec.t2o.components.DrivingComposable
import com.intec.t2o.components.HomeScreenButtonCard
import com.intec.t2o.components.NavigationButton
import com.intec.t2o.components.PressableEyes
import com.intec.t2o.viewmodels.MqttViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavController,
    mqttViewModel: MqttViewModel
) {

    Log.d("Current Screen", "HomeScreen")

    var showDrivingComposable by remember { mutableStateOf(false) }
    val adminMode by mqttViewModel.adminState.collectAsState(initial = false)

    // Observación de eventos
    val greetVisitorEventCount by mqttViewModel.greetVisitorEventCount.collectAsState()
    val isInWelcomeProcess by mqttViewModel.isInWelcomeProcess.collectAsState()

    // Observación de texto reconocido
    val speechText by mqttViewModel.speechText.collectAsState()

    val chatGPTManager = ChatGPTManager("");

    LaunchedEffect(key1 = true) {
        mqttViewModel.triggerGreetVisitorEvent()
        mqttViewModel.startWelcomeProcess()
    }

    LaunchedEffect(key1 = speechText) {
        delay(5000)
        mqttViewModel.clearRecognizedText()
    }

    LaunchedEffect(
        key1 = greetVisitorEventCount,
        key2 = isInWelcomeProcess
    ) { // Usar el contador como key
        if (greetVisitorEventCount > 0 && isInWelcomeProcess) { // Asegurarse de que hay al menos un evento para procesar
            Log.d("EVENT TRIGGER", "Procesando evento de saludo")
            mqttViewModel.speak(
                "Hola, soy Píter, bienvenido a intec róbots, en qué puedo ayudarte?",
                true
            )
            {
                Log.d("EVENT TRIGGER", "Evento de saludo procesado")
                mqttViewModel.finishWelcomeProcess()
            }
        }
    }

    LaunchedEffect(key1 = speechText) {
        Log.d("LaunchedEffect", "Ejecutando con speechText: $speechText")

        if (speechText.contains("Peter", ignoreCase = true)) {
            delay(1000)
            val prompt = speechText.replace("Peter", "")

            chatGPTManager.fetchGPT3ChatSinonimo(prompt) { sinonimo ->

                when {
                    sinonimo.contains("reunion", ignoreCase = true) -> {
                        mqttViewModel.speak("porfavor sigame a la sala de reuniones", true)
                        {
                            Log.d("peter", "reunion")
                        }
                    }

                    sinonimo.contains("baño", ignoreCase = true) -> {
                        mqttViewModel.speak("porfavor sigame y le guiare a los baños", true)
                        {
                            Log.d("peter", "baño")
                        }
                    }

                    sinonimo.contains("mensajeria", ignoreCase = true) -> {
                        mqttViewModel.speak(
                            "porfavor sigame y le guiare a la zona de correos",
                            true
                        )
                        {
                            Log.d("peter", "mensajeria")
                        }
                    }

                    sinonimo.contains("no encontrado", ignoreCase = true) -> {
                        chatGPTManager.fetchGPT3ChatResponse(speechText) { response ->
                            mqttViewModel.speak(response, true)
                            {
                                Log.d("peter", "chatgpt")
                            }
                        }
                    }
                }
            }
        }
    }

    val imageEmotionsLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    FuturisticGradientBackground {

        if (!mqttViewModel.isNavigating.value) {

            Column(modifier = Modifier.fillMaxSize()) {
                Cabecera(mqttViewModel = mqttViewModel)
                Botones(
                    mqttViewModel = mqttViewModel,
                )
                if (adminMode) {
                    LazyRowUbicaciones(
                        mqttViewModel = mqttViewModel,
                        modifier = Modifier,
                    )
                } else {

                    if (speechText.isNotEmpty()) {
                        Log.d("HomeScreen STT", "Speech Text not empty: $speechText")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(Color.Transparent)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = speechText,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center) // Asegura que el Text esté centrado dentro del Box.
                                    .fillMaxWidth() // Hace que el Text ocupe todo el ancho disponible.
                            )
                        }
                    } else {
                        Log.d("HomeScreen STT", "Speech Text empty")
                        Image(
                            painter = rememberAsyncImagePainter(
                                R.drawable.speechrecognition,
                                imageEmotionsLoader
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 5.dp)
                        )
                    }
                }
            }
        } else {
            Log.d("SECUENCIA DRIVING", "PressableEyes")
            PressableEyes(
                modifier = Modifier.fillMaxSize(),
                onClick = {
                    mqttViewModel.isNavigating.value = false
                    mqttViewModel.detenerNavegacion()
                    showDrivingComposable = true
                }
            )
        }

        if (showDrivingComposable) {
            Log.d("SECUENCIA DRIVING", "DrivingComposable")
            DrivingComposable(
                navController = navController,
                mqttViewModel = mqttViewModel,
                onCancel = {
                    mqttViewModel.setReturningHome(true)
                    showDrivingComposable = false
                },
                onContinue = {
                    mqttViewModel.currentNavigationContext.value =
                        MqttViewModel.NavigationState.HomeScreen
                    mqttViewModel.reanudarNavegacion()
                    showDrivingComposable = false
                }
            )
        }
    }
}


@Composable
fun Cabecera(mqttViewModel: MqttViewModel) {
    var clickCount by remember { mutableStateOf(0) }

    val imageEmotionsLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                R.drawable.blue_neutral,
                imageEmotionsLoader
            ),
            contentDescription = "logo",
            modifier = Modifier
                .size(60.dp)
                .clickable {
                    clickCount++
                    if (clickCount == 5) {
                        mqttViewModel.navigateToMqttScreen()
                        clickCount = 0
                    }
                }
        )
        Text(
            text = "¿Cuál es el motivo de su visita?",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun LazyRowUbicaciones(
    mqttViewModel: MqttViewModel,
    modifier: Modifier = Modifier,
) {

    mqttViewModel.getListPoses()
    val destinations: List<Pose> by mqttViewModel.posesList.collectAsState()

    LazyRow(
        modifier = modifier.then(
            Modifier
                .background(Color.Transparent)
                .fillMaxWidth()
        ),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
    ) {
        // Los botones que se crean a partir de la lista del viewModel
        items(items = destinations) { item ->
            Log.d("DESTINATIONS", item.name)
            // Un botón con el onClick y el estilo que quieras
            NavigationButton(item.name, onClick = {
                Log.d("asdsadasdadsdsada", item.name)
                mqttViewModel.isNavigating.value = true
                mqttViewModel.startNavigation(item.name) {
                    Log.d(
                        "HomeScreen",
                        "Navigation started"
                    )
                    mqttViewModel.isNavigating.value = false
                }
            })
        }
    }
}

@Composable
fun Botones(
    mqttViewModel: MqttViewModel,
) {

    val iconos = listOf(
        Icons.Default.Info,
        Icons.Default.Person,
        ImageVector.vectorResource(R.drawable.shipping)
    )
    val textos = listOf("Información", "Reunión", "Entregas")
    val indicaciones =
        listOf(
            "\"quiero más información...\"",
            "\"tengo una reunión...\"",
            "\"tengo una entrega...\""
        )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .wrapContentWidth(),
        contentAlignment = Alignment.Center
    ) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center
        ) {
            items(3) { index ->

                HomeScreenButtonCard(
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

@Composable
fun MqttButton(mqttViewModel: MqttViewModel) {
    Box(contentAlignment = Alignment.TopStart) {
        Image(
            painter = painterResource(id = R.drawable.intecrobots_circulo),
            contentDescription = "logo",
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.mqtt_button_size))
                .clickable {
                    mqttViewModel.navigateToAdminPanelScreen()
                })
    }
}

@Composable
fun ClockInBtn(mqttViewModel: MqttViewModel) {
    Box(contentAlignment = Alignment.TopStart) {
        Image(
            painter = painterResource(id = R.drawable.clockicon),
            contentDescription = "logo",
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.mqtt_button_size))
                .clickable {
                    mqttViewModel.navigateToClockInScreen()
                })
    }
}
