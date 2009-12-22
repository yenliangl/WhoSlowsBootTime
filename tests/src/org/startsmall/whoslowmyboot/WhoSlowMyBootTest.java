package org.startsmall.whoslowmyboot;

import android.test.ActivityInstrumentationTestCase;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class org.startsmall.whoslowmyboot.WhoSlowMyBootTest \
 * org.startsmall.whoslowmyboot.tests/android.test.InstrumentationTestRunner
 */
public class WhoSlowMyBootTest extends ActivityInstrumentationTestCase<WhoSlowMyBoot> {

    public WhoSlowMyBootTest() {
        super("org.startsmall.whoslowmyboot", WhoSlowMyBoot.class);
    }

}
