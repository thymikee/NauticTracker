package com.thymikee.nautictracker.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by thymikee on 23.12.14.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class DummyTest {
    @Before
    public void setup() {
        //do whatever is necessary before every test
    }

    @Test
    public void testShouldFail() {
        Assert.assertTrue(Boolean.TRUE);
    }
}