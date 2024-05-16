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

import com.topjohnwu.superuser.ShellUtils;

import org.lsposed.lspd.service.ILSPApplicationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class LServiceBridge extends ILServiceBridge.Stub {

    private final PackageManager pm;

    private boolean isInstalledAutomatically = false;

    public LServiceBridge(Context ctx, String apkPath) {
        this.pm = ctx.getPackageManager();
        try {
            int uid = getUid(apkPath);
            if (uid == -1) {
                throw new RuntimeException("get uid failed.");
            }
            Os.setuid(uid);
            if (isInstalledAutomatically) {
                // 异步执行
                new Thread(() -> {
                    ShellUtils.fastCmd(String.format("pm uninstall %s", MANAGE_PACKAGE_NAME));
                }).start();
            }
            Log.i(TAG, String.format("set uid success, %d", uid));
        } catch (ErrnoException | InterruptedException e) {
            Log.e(TAG, "set uid failed.", e);
        }
    }

    @Override
    public IBinder getManagerServiceBinder(ILSPApplicationService applicationService) throws RemoteException {
        ArrayList<IBinder> binders = new ArrayList<>(1);
        try (ParcelFileDescriptor descriptor = applicationService.requestInjectedManagerBinder(binders)) {
            if (binders.get(0) != null) {
                descriptor.detachFd();
            }
            return binders.get(0);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public IBinder getApplicationServiceBinder() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            IBinder binder = ServiceManager.getService(Context.ACTIVITY_SERVICE);
            data.writeInterfaceToken(LSPosed);
            data.writeInt(2); // org.lsposed.lspd.service.BridgeService.ACTION#ACTION_GET_BINDER
            Random random = new Random();
            data.writeString(String.valueOf(random.nextLong()));
            data.writeStrongBinder(new Binder());
            binder.transact(TRANSACTION_CODE, data, reply, 0);
            reply.readException();
            return reply.readStrongBinder();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    private int getUid(String apkPath) throws InterruptedException {
        try {
            return pm.getPackageUid(MANAGE_PACKAGE_NAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            boolean installed = ShellUtils.fastCmdResult(String.format("pm install -r %s", apkPath));
            if (installed) {
                isInstalledAutomatically = true;
                Thread.sleep(100);
                return getUid(apkPath);
            }
            return -1;
        }
    }


    private static final String TAG = "LServiceBridge";

    private static final String LSPosed = "LSPosed";
    private static final int TRANSACTION_CODE = 1598837584;
    private static final String MANAGE_PACKAGE_NAME = "org.lsposed.manager";
}
