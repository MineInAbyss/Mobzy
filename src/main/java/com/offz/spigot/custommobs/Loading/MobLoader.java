package com.offz.spigot.custommobs.Loading;

import com.offz.spigot.custommobs.MobContext;
import com.offz.spigot.custommobs.Mobs.Type.GroundMobType;
import com.offz.spigot.custommobs.Mobs.Type.MobType;

public class MobLoader {
//    private static MobClassLoader mobClassLoader;

    public static void loadAllMobs(MobContext context) {
        for (GroundMobType groundMobType : GroundMobType.values()) {
            MobType.registerMobType(groundMobType);
        }


//        String mobRelDir = (String) context.getConfig().get("storage.mobpath");
//        URI fullURI = context.getPlugin().getDataFolder().toURI().resolve(mobRelDir);
//
        //Make dirs if missing
//        new File(fullURI.getPath()).mkdirs();
//
//        Collection<File> files = FileUtils.listFiles(
//                new File(fullURI),
//                new RegexFileFilter(".*\\.jar$"),
//                TrueFileFilter.INSTANCE
//        );
//
//        URL[] urls = files.stream().map(a -> {
//            try {
//                return a.toURI().toURL();
//            } catch (MalformedURLException e) {
//                return null;
//            }
//        }).filter(Objects::nonNull).toArray(URL[]::new);
//
//        AccessController.doPrivileged(new PrivilegedAction<Void>() {
//            @Override
//            public Void run() {
//                mobClassLoader = new MobClassLoader(urls, context);
//                return null;
//            }
//        });
//
//
//        files.forEach(a -> {
//            try {
//                JarFile jarFile = new JarFile(a);
//                Enumeration<JarEntry> e = jarFile.entries();
//
//                while (e.hasMoreElements()) {
//                    JarEntry je = e.nextElement();
//                    if (je.isDirectory() || !je.getName().endsWith(".class")) {
//                        continue;
//                    }
                    // -6 because of .class
//                    String className = je.getName().substring(0, je.getName().length() - 6);
//                    className = className.replace('/', '.');
//                    Class c = mobClassLoader.loadClass(className);
//
//                    if (c.isEnum() && MobType.class.isAssignableFrom(c)) {
//                        c.getEnumConstants();
//                    }
//                }
//
//            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        });
    }

    public static void unloadAllMobs() {
        // Unregister
        MobType.unregisterAllMobs();

        // Destroy
//        mobClassLoader = null;
    }
}

