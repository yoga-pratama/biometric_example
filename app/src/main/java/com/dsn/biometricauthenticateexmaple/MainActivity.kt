package com.dsn.biometricauthenticateexmaple


import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dsn.biometricauthenticateexmaple.ui.theme.BiometricAuthenticateExmapleTheme


class MainActivity : AppCompatActivity() {
    private val promptManager by lazy { BiometricPromptManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiometricAuthenticateExmapleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val biometricResult by promptManager.promptResults.collectAsState(
                        initial = null
                    )

                    val enrollLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = {
                            print("Enroll result: $it")
                        }
                    )
                    LaunchedEffect(biometricResult) {
                        if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
                            // Do something
                            if (Build.VERSION.SDK_INT >= 30) {
                                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                                    .apply {
                                        putExtra(
                                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                        )

                                    }
                                enrollLauncher.launch(enrollIntent)
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            promptManager.showBiometricPrompt(
                                title = "Biometric Authentication",
                                description = "Please authenticate to proceed"
                            )
                        }) {
                            Text("Authenticate")
                        }

                        biometricResult?.let { result ->
                            Text(
                                text = when (result) {
                                    is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                                        result.error
                                    }

                                    is BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                                        "Authentication failed"
                                    }

                                    is BiometricPromptManager.BiometricResult.AuthenticationSucceeded -> {
                                        "Authentication succeeded"
                                    }

                                    is BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                                        "Biometric authentication is not set up"
                                    }

                                    is BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                                        "Biometric authentication is not available"
                                    }

                                    is BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                                        "Biometric hardware is not available"
                                    }

                                    else -> {
                                        "Biometric authentication failed"
                                    }
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}

