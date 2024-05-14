// ILServiceBridge.aidl
package io.github.houvven.lservice;

import android.os.IBinder;
import org.lsposed.lspd.service.ILSPApplicationService;

// Declare any non-default types here with import statements

interface ILServiceBridge {

   IBinder getManagerServiceBinder(ILSPApplicationService applicationService);

   IBinder getApplicationServiceBinder();
}