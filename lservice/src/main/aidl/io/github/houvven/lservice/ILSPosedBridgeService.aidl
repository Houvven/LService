// ILSPosedBridgeService.aidl
package io.github.houvven.lservice;

import android.os.IBinder;
import org.lsposed.lspd.service.ILSPApplicationService;

// Declare any non-default types here with import statements

interface ILSPosedBridgeService {

   int obtainLSPosedManagerUid();

   IBinder obtainLSPosedManagerServiceBinder(ILSPApplicationService applicationService);

   IBinder obtainLSPosedApplicationServiceBinder();
}