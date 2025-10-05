package android.content;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public interface IIntentSender {
    void send(int code, Intent intent, String resolvedType, IBinder token, IIntentReceiver receiver, String requiredPermission, Bundle options) throws Exception;
}
