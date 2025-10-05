package android.content;

import android.content.Intent;
import android.os.Bundle;

public interface IIntentReceiver {
    void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) throws Exception;

    abstract class Stub implements IIntentReceiver {
        @Override
        public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) throws Exception {
            // placeholder
        }
    }
}
