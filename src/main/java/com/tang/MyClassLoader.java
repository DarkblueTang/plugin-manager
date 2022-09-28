package com.tang;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.tang.config.PluginConfig;
import com.tang.utils.CommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.List;

public class MyClassLoader extends URLClassLoader {
    static MyClassLoader classLoader;
    private static List<String> classPathFolders;

    public MyClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public MyClassLoader(URL[] urls) {
        super(urls);
    }

    public MyClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    try {
                        c = super.findClass(name);
                    } catch (Exception exceptione) {
                    }
                    try {
                        c = defineByFolder(name);
                    } catch (Exception exceptione) {
                    }
                    if (c == null) {
                        c = getParent().loadClass(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    private Class<?> defineByFolder(String name) {
        String fileName = name.replace(".", File.separator) + ".class";
        for (String classPathFolder : classPathFolders) {
            String filePath = classPathFolder + File.separator + fileName;
            if (FileUtil.exist(filePath)) {
                try {
                    byte[] bytes = IoUtil.readBytes(new FileInputStream(filePath));
                    return defineClass(name, bytes, 0, bytes.length);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static MyClassLoader getMyClassLoader(ClassLoader parent) {
        if (CommonUtils.isEmpty(classLoader)) {
            synchronized (MyClassLoader.class) {
                if (CommonUtils.isEmpty(classLoader)) {
                    initClassLoader(parent);
                }
            }
        }
        return classLoader;
    }

    private static void initClassLoader(ClassLoader parent) {
        classLoader = new MyClassLoader(PluginConfig.getUrlJars(), parent);
        classLoader.setClassPathFolder(PluginConfig.getClassPathFolders());
    }

    private void setClassPathFolder(List<String> folders) {
        classPathFolders = folders;
    }
}
