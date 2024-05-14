package com.houvven.lsposed.scope.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.houvven.lservice.LServiceBridgeRootService
import kotlinx.coroutines.delay
import org.lsposed.lspd.ILSPManagerService

class MainActivity : ComponentActivity() {

    private var managerService: ILSPManagerService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val connection = LServiceBridgeRootService.DefaultServiceConnection()
        LServiceBridgeRootService.bind(this.applicationContext, connection, apkFile)

        setContent {
            var isBinderAlive by remember { mutableStateOf(false) }
            var api by remember { mutableStateOf("") }
            var xposedApiVersion by remember { mutableStateOf("") }
            var xposedVersionName by remember { mutableStateOf("") }
            var xposedVersionCode by remember { mutableStateOf("") }

            MaterialTheme {
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                ) {
                    Text(text = "api: $api")
                    Text(text = "xposedApiVersion: $xposedApiVersion")
                    Text(text = "xposedVersionName: $xposedVersionName")
                    Text(text = "xposedVersionCode: $xposedVersionCode")
                }
            }

            LaunchedEffect(key1 = this) {
                while (connection.lServiceBridge == null) {
                    delay(200)
                }
                isBinderAlive = true
            }

            LaunchedEffect(key1 = isBinderAlive) {
                while (managerService == null) {
                    runCatching {
                        managerService = connection.getManagerService(connection.applicationService)
                    }.onFailure { delay(200) }
                }
                managerService!!.let {
                    api = it.api
                    xposedApiVersion = it.xposedApiVersion.toString()
                    xposedVersionName = it.xposedVersionName
                    xposedVersionCode = it.xposedVersionCode.toString()
                }
            }

        }
    }

    private val apkFile: String
        get() {
            val file = cacheDir.resolve("lsposed.apk")
            if (!file.exists()) {
                assets.open("lsposed-manager.apk").use { inputStream ->
                    file.outputStream().use { out ->
                        out.write(inputStream.readBytes())
                        out.flush()
                    }
                }
            }
            return file.absolutePath
        }
}