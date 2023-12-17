package com.houvven.lsposed.scope.demo

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import io.github.houvven.lservice.ILSPosedBridgeService
import io.github.houvven.lservice.LSPosedBridgeRootService
import org.lsposed.lspd.ILSPManagerService
import org.lsposed.lspd.service.ILSPApplicationService
import java.util.Optional

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LSPosedBridgeRootService.bindRootService(this, LSPosedBridgeServiceConnection)
        Thread {
            while (!LSPosedBridgeServiceConnection.serviceOptional.isPresent) {
                Thread.sleep(200)
            }

            LSPosedBridgeServiceConnection.serviceOptional.ifPresent { bridgeService ->
                val applicationServiceBinder =
                    bridgeService.obtainLSPosedApplicationServiceBinder() ?: return@ifPresent
                val applicationService =
                    ILSPApplicationService.Stub.asInterface(applicationServiceBinder)

                val managerServiceBinder =
                    bridgeService.obtainLSPosedManagerServiceBinder(applicationService)
                        ?: return@ifPresent
                val managerService = ILSPManagerService.Stub.asInterface(managerServiceBinder)
                val xposedApiVersion = managerService.xposedApiVersion
                Log.i("MainActivity", "xposedApiVersion: $xposedApiVersion")
            }
        }.start()
    }

    object LSPosedBridgeServiceConnection : ServiceConnection {

        private const val TAG = "LSPosedBridgeServiceConnection"

        var serviceOptional: Optional<ILSPosedBridgeService> = Optional.empty()
            private set

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.i(TAG, "onServiceConnected: $name, ${service.interfaceDescriptor}")
            serviceOptional = Optional.of(ILSPosedBridgeService.Stub.asInterface(service))
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "onServiceDisconnected: $name")
            serviceOptional = Optional.empty()
        }
    }
}