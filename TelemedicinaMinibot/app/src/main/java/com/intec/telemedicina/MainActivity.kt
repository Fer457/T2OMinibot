package com.intec.telemedicina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.intec.telemedicina.di.MqttViewModelFactory
import com.intec.telemedicina.di.SplashScreenViewModelFactory
import com.intec.telemedicina.icariascreen.AppNavigation
import com.intec.telemedicina.ui.theme.PlantillaJetpackTheme
import com.intec.telemedicina.viewmodels.MqttViewModel
import com.intec.telemedicina.viewmodels.SplashScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: SplashScreenViewModelFactory

    @Inject
    lateinit var mqttViewModelFactory: MqttViewModelFactory

    private val viewModel by viewModels<SplashScreenViewModel> { viewModelFactory }

    private val mqttViewModel by viewModels<MqttViewModel> { mqttViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the DisplayMetrics object

        setContent {
            PlantillaJetpackTheme {
                Surface(
                    modifier = Modifier.fillMaxHeight(1f),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel, mqttViewModel = mqttViewModel)
                }
                // A surface container using the 'background' color from the theme
            }
        }
    }
}
