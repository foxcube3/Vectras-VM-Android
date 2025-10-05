package com.vectras.vm.x11;

import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.view.Surface;

public interface ICmdEntryInterface {
    void windowChanged(Surface surface, String name);
    ParcelFileDescriptor getXConnection();
    ParcelFileDescriptor getLogcatOutput();

    // helper to mirror AIDL-generated interface
    default android.os.IBinder asBinder() {
        return null;
    }

    default boolean isBinderAlive() {
        android.os.IBinder b = asBinder();
        return b != null && b.isBinderAlive();
    }

    abstract class Stub extends Binder implements ICmdEntryInterface {
        public static ICmdEntryInterface asInterface(IBinder obj) {
            if (obj == null) return null;
            // If already an instance, try to query local interface
            android.os.IInterface iin = obj.queryLocalInterface("com.vectras.vm.x11.ICmdEntryInterface");
            if (iin instanceof ICmdEntryInterface) {
                return (ICmdEntryInterface) iin;
            }
            // Return a minimal proxy
            return new ICmdEntryInterface() {
                @Override
                public void windowChanged(Surface surface, String name) {
                }

                @Override
                public ParcelFileDescriptor getXConnection() {
                    return null;
                }

                @Override
                public ParcelFileDescriptor getLogcatOutput() {
                    return null;
                }
            };
        }
        public IBinder asBinder() { return this; }
    }
}
