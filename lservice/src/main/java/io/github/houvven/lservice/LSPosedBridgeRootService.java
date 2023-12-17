package io.github.houvven.lservice;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.topjohnwu.superuser.ipc.RootService;

import java.util.concurrent.Executor;

public class LSPosedBridgeRootService extends RootService {

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return new LSPosedBridgeService(this.getApplicationContext()).asBinder();
    }

    public static void bindRootService(@NonNull Context context, @NonNull ServiceConnection connection) {
        Intent intent = new Intent(context, LSPosedBridgeRootService.class);
        RootService.bind(intent, connection);
    }

    public static void bindRootService(@NonNull Context context, @NonNull Executor executor, @NonNull ServiceConnection connection) {
        Intent intent = new Intent(context, LSPosedBridgeRootService.class);
        RootService.bind(intent, executor, connection);
    }
}
