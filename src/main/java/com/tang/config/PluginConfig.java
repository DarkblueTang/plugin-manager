package com.tang.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.tang.PluginConfigProperties;
import com.tang.PluginProperties;
import com.tang.utils.CommonUtils;
import lombok.Getter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PluginConfig {
    @Getter
    private static List<String> jarPathList;
    @Getter
    private static List<String> classPathFolders = new LinkedList<>();
    private static List<URL> _URLS = new LinkedList<>();

    public static void initConfig() {
        System.out.println("init plugins.......");
        PluginConfigProperties properties = PluginConfigProperties.getProperties();
        String basePath = properties.getBasePath();
        List<PluginProperties> plugins = properties.getPlugins();
        List<String> modules = properties.getModules();
        if (!CommonUtils.isEmpty(plugins)) {

            //增加插件的jar包
            jarPathList = initJarPathList(basePath, plugins);

            if (!CommonUtils.isEmpty(properties.getModules())
                    && !CommonUtils.isEmpty(jarPathList)) {

                //增加工程文件的jar包  idea 以产品为主启动
                initModulesJarPathFromSystemPath(basePath, plugins, modules);

                //增加工程文件的jar包  jar命令 以产品为主启动
                initModulesJarPathFromJarFile(basePath, plugins, modules);

                //增加工程文件的jar包  tomcat 以产品为主启动
                initModulesJarPathFromTomcatWebFile(basePath, plugins, modules);
            }
        }
        System.out.println("plugins jar scaned.......");
    }

    private static void initModulesJarPathFromTomcatWebFile(String basePath,
                                                            List<PluginProperties> plugins,
                                                            List<String> modules) {
        String catalinaHome = System.getProperty("catalina.home");
        if (CommonUtils.isEmpty(catalinaHome)) return;
        try {
            System.out.println("init plugins: launch by tomcat.....");
            // 路径: /WEB-INF/classes
            File file = new File(PluginConfig.class.getClassLoader().getResource("/").getFile());
            // 路径: /WEB-INF
            file = file.getParentFile();
            // 路径: /WEB-INF/lib
            file = new File(file.getAbsolutePath() + File.separator + "lib");

            File[] libs = file.listFiles();
            for (File lib : libs) {
                for (String module : modules) {
                    if (FileNameUtil.getName(lib).contains(module)) {
                        jarPathList.add(lib.getAbsolutePath());
                    }
                }
            }
            System.out.println("init plugins: load tomcat libs success.....");
        } catch (Exception e) {

        }
    }

    private static void initModulesJarPathFromJarFile(String basePath, List<PluginProperties> plugins, List<String> modules) {
        String classPath = System.getProperty("java.class.path");
        String[] split = classPath.split(";");
        if (split.length == 1) {
            String suffix = FileNameUtil.getSuffix(split[0]);
            if (suffix.equals("war") || suffix.equals("jar") || suffix.equals("zip") || suffix.equals("rar")) {
                doInitModulesJarPathFromJarFile(classPath, plugins, modules);
            }
        }
    }

    private static void doInitModulesJarPathFromJarFile(String jarFileName, List<PluginProperties> plugins, List<String> modules) {
        System.out.println("init plugins: launch by jar");
        String dir = System.getProperty("user.dir");
        try {
            String jarFilePath = dir + File.separator + jarFileName;
            ZipFile zipFile = new ZipFile(jarFilePath);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                for (String module : modules) {
                    if (name.contains(module)) {
                        InputStream inputStream = zipFile.getInputStream(entry);
                        String name1 = FileNameUtil.getName(entry.getName());
                        String unCompressedFilePath = dir + File.separator + name1;
                        FileOutputStream fileOutputStream = new FileOutputStream(unCompressedFilePath);
                        IoUtil.copy(inputStream, fileOutputStream);
                        try {
                            fileOutputStream.close();
                        } catch (Exception e) {
                        }
                        jarPathList.add(unCompressedFilePath);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initModulesJarPathFromSystemPath(String basePath, List<PluginProperties> plugins, List<String> modules) {
        String classPath = System.getProperty("java.class.path");
        String[] jars = classPath.split(";");
        boolean flag = false;
        for (String jar : jars) {
            String suffix = FileUtil.getSuffix(jar);
            if (CommonUtils.isEmpty(suffix)) {
                //文件夹
                classPathFolders.add(jar);
                if (!jar.endsWith("target")) {
                    flag = true;
                }
                continue;
            }
            String fileName = FileNameUtil.getName(jar);
            for (String module : modules) {
                if (fileName.contains(module)) {
                    jarPathList.add(jar);
                }
            }
        }
        if (flag) {
            System.out.println("init plugins: launch by ide");
        }
    }

    private static List<String> initJarPathList(String basePath, List<PluginProperties> plugins) {
        LinkedList<String> res = new LinkedList<>();
        for (PluginProperties plugin : plugins) {
            System.out.println(plugin.getName() + " scanning ....");
            String path = basePath + File.separator + plugin.getPath();
            String[] list = new File(path).list();
            if (CommonUtils.isEmpty(list)) continue;
            for (String jarFilePath : list) {
                if (FileNameUtil.getSuffix(jarFilePath).equals("jar")) {
                    res.add(basePath + File.separator + plugin.getPath() + File.separator + jarFilePath);
                }
            }
        }
        return res;
    }

    public static URL[] getUrlJars() {
        if (CommonUtils.isEmpty(jarPathList)) return new URL[]{};
        List<URL> urls = new LinkedList<>();
        for (String path : jarPathList) {
            URL url = null;
            try {
                url = new File(path).toURI().toURL();
            } catch (MalformedURLException e) {
                CommonUtils.newException(e, e.getMessage());
            }
            urls.add(url);
        }
        urls.addAll(_URLS);
        URL[] res = new URL[urls.size()];
        urls.toArray(res);
        return res;
    }
}
