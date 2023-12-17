package io.github.houvven.lservice.util;

import com.topjohnwu.superuser.ShellUtils;

import java.io.File;
import java.nio.file.Path;

public class ShellPackageManager {

    public static boolean installPackage(Path path) {
        return installPackage(path.toFile());
    }

    public static boolean installPackage(File file) {
        return ShellUtils.fastCmdResult("pm install -r " + file.getAbsolutePath());
    }

    public static boolean uninstallPackage(String packageName) {
        return ShellUtils.fastCmdResult("pm uninstall " + packageName);
    }
}
