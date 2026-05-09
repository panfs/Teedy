package com.sismics.docs.core.util;

import com.sismics.util.EnvironmentUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestDirectoryUtil {
    private static Object setStaticField(Class<?> clazz, String fieldName, Object newValue) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        Object oldValue = f.get(null);
        f.set(null, newValue);
        return oldValue;
    }

    @Test
    public void getBaseDataDirectory_usesTeedyHomeWhenSet() throws Exception {
        Path teedyHome = Files.createTempDirectory("teedy-home-");
        Path nested = teedyHome.resolve("nested");

        Object oldTeedyHome = setStaticField(EnvironmentUtil.class, "TEEDY_HOME", nested.toString());
        Object oldWebappContext = setStaticField(EnvironmentUtil.class, "webappContext", false);
        try {
            Path base = DirectoryUtil.getBaseDataDirectory();
            Assert.assertEquals(nested, base);
            Assert.assertTrue(Files.isDirectory(base));
        } finally {
            setStaticField(EnvironmentUtil.class, "TEEDY_HOME", oldTeedyHome);
            setStaticField(EnvironmentUtil.class, "webappContext", oldWebappContext);
        }
    }

    @Test
    public void getBaseDataDirectory_usesTempDirInUnitTestMode() throws Exception {
        Object oldTeedyHome = setStaticField(EnvironmentUtil.class, "TEEDY_HOME", null);
        Object oldWebappContext = setStaticField(EnvironmentUtil.class, "webappContext", false);
        try {
            Path base = DirectoryUtil.getBaseDataDirectory();
            Assert.assertEquals(Paths.get(System.getProperty("java.io.tmpdir")), base);
            Assert.assertTrue(Files.isDirectory(base));
        } finally {
            setStaticField(EnvironmentUtil.class, "TEEDY_HOME", oldTeedyHome);
            setStaticField(EnvironmentUtil.class, "webappContext", oldWebappContext);
        }
    }

    @Test
    public void getBaseDataDirectory_usesMacPathInWebappMode() throws Exception {
        Path macHome = Files.createTempDirectory("mac-home-");

        Object oldTeedyHome = setStaticField(EnvironmentUtil.class, "TEEDY_HOME", null);
        Object oldWebappContext = setStaticField(EnvironmentUtil.class, "webappContext", true);
        Object oldOs = setStaticField(EnvironmentUtil.class, "OS", "mac os x");
        Object oldMacHome = setStaticField(EnvironmentUtil.class, "MAC_OS_USER_HOME", macHome.toString());
        try {
            Path base = DirectoryUtil.getBaseDataDirectory();
            Path expected = Paths.get(macHome.toString() + "/Library/Sismics/Docs");
            Assert.assertEquals(expected, base);
            Assert.assertTrue(Files.isDirectory(base));
        } finally {
            setStaticField(EnvironmentUtil.class, "TEEDY_HOME", oldTeedyHome);
            setStaticField(EnvironmentUtil.class, "webappContext", oldWebappContext);
            setStaticField(EnvironmentUtil.class, "OS", oldOs);
            setStaticField(EnvironmentUtil.class, "MAC_OS_USER_HOME", oldMacHome);
        }
    }
}

