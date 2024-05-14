package io.github.houvven.lservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.topjohnwu.superuser.ipc.RootService;

import org.lsposed.lspd.ILSPManagerService;
import org.lsposed.lspd.service.ILSPApplicationService;

public class LServiceBridgeRootService extends RootService {

    @Override
    public IBinder onBind(Intent intent) {
        String path = intent.getStringExtra("apkPath");
        return new LServiceBridge(this.getApplicationContext(), path).asBinder();
    }

    public static void bind(Context context, ServiceConnection connection, String apkPath) {
        Intent intent = new Intent(context, LServiceBridgeRootService.class);
        intent.putExtra("apkPath", apkPath);
        RootService.bind(intent, connection);
    }


    public static class DefaultServiceConnection implements ServiceConnection {

        private ILServiceBridge lServiceBridge = null;

        public ILServiceBridge getLServiceBridge() {
            return this.lServiceBridge;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, String.format("connect: %s", name));
            lServiceBridge = LServiceBridge.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, String.format("disconnect: %s", name));
            lServiceBridge = null;
            RootService.unbind(this);
        }

        public ILSPApplicationService getApplicationService() throws RemoteException, NullPointerException {
            IBinder binder = lServiceBridge.getApplicationServiceBinder();
            return ILSPApplicationService.Stub.asInterface(binder);
        }

        public ILSPManagerService getManagerService(ILSPApplicationService applicationService) throws RemoteException, NullPointerException {
            IBinder binder = lServiceBridge.getManagerServiceBinder(applicationService);
            return ILSPManagerService.Stub.asInterface(binder);
        }

        private static final String TAG = "DefaultLServiceConnection";
    }
}