package com.storycraft;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

public class DynamicModule extends MainMiniPlugin {

    protected class DynamicURLClassLoader extends URLClassLoader {

        public DynamicURLClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        public void addURL(URL url) {
            super.addURL(url);
        }
        
    }

    private File moduleDir;

    private Map<String, ClassLoader> loadedList;
    private Map<String, MiniPlugin> miniPluginList;

    public DynamicModule() {
        this.loadedList = new HashMap<>();
        this.miniPluginList = new HashMap<>();
    }

    @Override
    public void onLoad(MainPlugin plugin) {
        this.moduleDir = new File(plugin.getOriginalDataFolder(), "modules");
    }

    @Override
    public void onEnable() {
        loadAll();
    }

    public File getModuleDir() {
        return moduleDir;
    }

    protected String getClassName(File f) {
        String fileName = f.getName();
        int lastIndex = fileName.lastIndexOf(".");

        if (lastIndex < 0)
            return fileName;

        return fileName.substring(0, lastIndex);
    }

    public void loadModule(File moduleClassFile) throws Exception {
        if (isModuleLoaded(moduleClassFile))
            return;
        
        String className = getClassName(moduleClassFile);

        URLClassLoader classLoader = new URLClassLoader(new URL[] {moduleClassFile.getParentFile().toURI().toURL()}, DynamicModule.class.getClassLoader());

        Class<? extends MiniPlugin> clazz = (Class<? extends MiniPlugin>) classLoader.loadClass(className);

        MiniPlugin miniPlugin = clazz.getConstructor().newInstance();

        getPlugin().getMiniPluginLoader().addMiniPlugin(miniPlugin);

        miniPluginList.put(moduleClassFile.getName(), miniPlugin);
        loadedList.put(moduleClassFile.getName(), classLoader);
    }

    public void unloadModule(File moduleClassFile) throws Exception {
        if (!isModuleLoaded(moduleClassFile))
            return;
        
        loadedList.remove(moduleClassFile.getName());
        getPlugin().getMiniPluginLoader().removeMiniPlugin(miniPluginList.remove(moduleClassFile.getName()));

        System.gc();
    }

    public boolean isModuleLoaded(File file) {
        return loadedList.containsKey(file.getName());
    }

    public List<String> getLoadedList() {
        return new ArrayList<>(loadedList.keySet());
    }

    protected void loadAll() {
        moduleDir.mkdirs();

        File[] files = moduleDir.listFiles((file) -> {
            return file.getName().endsWith(".class") && !file.getName().contains("$");
        });

        for (File file : files) {
            try {
                loadModule(file);
            } catch (Exception e) {
                e.printStackTrace();
                getPlugin().getConsoleSender().sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "Module", "모듈 " + file.getName() + " 로드 실패. " + e.getLocalizedMessage()));
            }
        }
    }

}