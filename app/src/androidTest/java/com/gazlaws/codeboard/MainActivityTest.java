package com.gazlaws.codeboard;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;
import static junit.framework.TestCase.assertNotNull;

public class MainActivityTest {

    @Test
    public void mainActivity_canBeLaunched() throws Exception {

        Context appContext = InstrumentationRegistry.getTargetContext();

        Instrumentation mInstrumentation = InstrumentationRegistry.getInstrumentation();
        Instrumentation.ActivityMonitor monitor = mInstrumentation.addMonitor(MainActivity.class.getName(), null, false);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(mInstrumentation.getTargetContext(), MainActivity.class.getName());
        mInstrumentation.startActivitySync(intent);

        Activity currentActivity = InstrumentationRegistry.getInstrumentation().waitForMonitor(monitor);
        assertNotNull(currentActivity);

        mInstrumentation.removeMonitor(monitor);
    }

}