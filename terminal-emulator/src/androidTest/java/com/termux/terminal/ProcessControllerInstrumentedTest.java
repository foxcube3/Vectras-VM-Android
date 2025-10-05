package com.termux.terminal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

import android.os.Build;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 26)
public class ProcessControllerInstrumentedTest {

    @Test
    public void testSendSignalNoCrash() {
        int[] pidHolder = new int[1];
        int fd = JNI.createSubprocess("/system/bin/sh", "/", new String[]{"-c", "sleep 2"}, null, pidHolder, 24, 80);
        assertNotEquals("FD should be valid", -1, fd);
        int pid = pidHolder[0];
        // Send SIGINT; even if process exits quickly, we just assert JNI didn't report failure.
        boolean sent = ProcessController.sendInterrupt(pid);
        assertTrue("Signal should report success", sent);
        JNI.close(fd);
    }
}
