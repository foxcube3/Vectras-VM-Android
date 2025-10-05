package android.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.content.IIntentSender;

public interface IActivityManager {
    IIntentSender getIntentSender(int a, String b, String c, String d, int e, Intent[] f, String[] g, int h, Bundle i, int j) throws RemoteException;
}
