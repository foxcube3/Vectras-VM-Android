package android.app;

import android.content.Context;
import android.content.pm.PackageManager;

public class ActivityThread {
    public static ActivityThread systemMain() { return new ActivityThread(); }
    public Context getSystemContext() { return null; }
    public static PackageManager getPackageManager() { return null; }
}
