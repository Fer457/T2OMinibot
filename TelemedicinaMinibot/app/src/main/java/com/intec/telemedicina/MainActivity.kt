package com.intec.telemedicina

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ainirobot.coreservice.client.ApiListener
import com.ainirobot.coreservice.client.RobotApi
import com.ainirobot.coreservice.client.speech.SkillApi
import com.ainirobot.coreservice.client.speech.SkillCallback
import com.intec.telemedicina.di.MqttViewModelFactory
import com.intec.telemedicina.di.SplashScreenViewModelFactory
import com.intec.telemedicina.icariascreen.AppNavigation
import com.intec.telemedicina.robot.modulecallback.ModuleCallback
import com.intec.telemedicina.robotinterface.RobotManager
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

    var skillApi = SkillApi()

    var robotApi = RobotApi.getInstance()

    @Inject
    lateinit var robotMan: RobotManager

    // TODO: Pass robotManager to the viewmodel and try out the navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the DisplayMetrics object

        RobotApi.getInstance().connectServer(this, object :
            ApiListener {
            override fun handleApiDisabled() {}
            override fun handleApiConnected() {
                // Server is connected, set the callback for receiving requests, including voice commands, system events, etc.
                RobotApi.getInstance().setCallback(ModuleCallback())
                skillApi.connectApi(applicationContext, object : ApiListener {
                    override fun handleApiDisabled() {
                        TODO("Not yet implemented")
                    }

                    override fun handleApiConnected() {
                        skillApi.registerCallBack(object : SkillCallback(){
                            override fun onSpeechParResult(s: String) {
                                // Resultado temporal del reconocimiento de voz
                                Log.d("RobotViewModel ASR", s)
                            }

                            override fun onStart() {
                                // Inicio del reconocimiento
                                Log.d("RobotViewModel ASR", "onStart")
                            }

                            override fun onStop() {
                                // Fin del reconocimiento
                                Log.d("RobotViewModel ASR", "onStop")
                            }

                            override fun onVolumeChange(volume: Int) {
                                // Cambio en el volumen de la voz reconocida
                                Log.d("RobotViewModel ASR", "onVolumeChange")
                            }

                            override fun onQueryEnded(status: Int) {
                                // Manejar el fin de la consulta basado en el estado
                                Log.d("RobotViewModel ASR", "onQueryEnded")
                            }

                            override fun onQueryAsrResult(asrResult: String) {
                                // asrResult: resultado final del reconocimiento
                                Log.d("RobotViewModel ASR", asrResult)
                            }
                        })
                        Log.d("SKILLAPI","Skill api connected!")
                    }

                    override fun handleApiDisconnected() {
                        TODO("Not yet implemented")
                    }
                })
                robotMan = RobotManager(skillApi,applicationContext)
            }

            override fun handleApiDisconnected() {
                //Disconnect
            }
        })

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
