package io.github.houvven.lservice;

import android.content.Context;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"SpellCheckingInspection"})
class Constants {

    final static String managerApkPackageName = "org.lsposed.manager";

    final static List<File> managerApkPaths = Stream.of(
            "/data/adb/modules/zygisk_lsposed/manager.apk",
            "/data/adb/modules/riru_lsposed/manager.apk",
            "/data/adb/lspd/manager.apk"
    ).map(File::new).collect(Collectors.toList());

    final static int SHELL_UID = 2000;
    final static String SERVICE_NAME = Context.ACTIVITY_SERVICE;
    final static int TRANSACTION_CODE = 1598837584;
    final static String DESCRIPTOR = "LSPosed";
}
