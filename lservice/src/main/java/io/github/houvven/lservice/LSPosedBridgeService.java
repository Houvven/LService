package io.github.houvven.lservice;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

import androidx.annotation.NonNull;

import org.lsposed.lspd.service.ILSPApplicationService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import io.github.houvven.lservice.util.ShellPackageManager;

class LSPosedBridgeService extends ILSPosedBridgeService.Stub {

    @NonNull
    private final PackageManager packageManager;

    public LSPosedBridgeService(@NonNull Context context) {
        packageManager = context.getPackageManager();
        this.setUid();
    }

    private void setUid() {
        Log.i(TAG, "LSPosedBridgeService initialize.");
        int uid = obtainLSPosedManagerUid();
        try {
            Log.i(TAG, "LSPosedBridgeService initialize, set uid: " + uid);
            Os.setuid(uid);
        } catch (ErrnoException e) {
            Log.e(TAG, "instance initializer: set uid failed.", e);
        }
    }

    @Override
    public int obtainLSPosedManagerUid() {
        try {
            return packageManager.getPackageUid(Constants.managerApkPackageName, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
            Constants.managerApkPaths.stream()
                    .filter(File::exists)
                    .findFirst()
                    .ifPresent(ShellPackageManager::installPackage);

            try {
                int uid = packageManager.getPackageUid(Constants.managerApkPackageName, 0);
                ShellPackageManager.uninstallPackage(Constants.managerApkPackageName);
                return uid;
            } catch (PackageManager.NameNotFoundException e) {
                return Constants.SHELL_UID;
            }
        }
    }

    @Override
    public IBinder obtainLSPosedManagerServiceBinder(@NonNull ILSPApplicationService applicationService) {
        ArrayList<IBinder> binders = new ArrayList<>(1);
        try (ParcelFileDescriptor descriptor = applicationService.requestInjectedManagerBinder(binders)) {
            if (binders.get(0) != null) {
                descriptor.detachFd();
            }
            return binders.get(0);
        } catch (RemoteException | IOException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings({"SpellCheckingInspection"})
    public IBinder obtainLSPosedApplicationServiceBinder() {
        IBinder service = ServiceManager.getService(Constants.SERVICE_NAME);
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(Constants.DESCRIPTOR);
            data.writeInt(2); // org.lsposed.lspd.service.BridgeService.ACTION#ACTION_GET_BINDER
            Random random = new Random();
            data.writeString(String.valueOf(random.nextLong()));
            data.writeStrongBinder(new Binder());
            service.transact(Constants.TRANSACTION_CODE, data, reply, 0);
            reply.readException();
            return reply.readStrongBinder();
        } catch (RemoteException e) {
            return null;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }


    private static final String TAG = "LSPosedBridgeService";
}
