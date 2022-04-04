/*
 * BackwardsNode's Survival Games, a Minecraft Bukkit custom gamemode
 * Copyright (C) 2019-2022 BackwardsNode/BossWasHere
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.backwardsnode.survivalgames.dependency;

import com.backwardsnode.survivalgames.Plugin;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.MessageDigest;

public enum DependencyInjector {
    H2("com.h2database", "h2", "2.1.210", "org.h2.Driver", "edc57299926297fd9315e04de75f8538c4cb5fe97fd3da2a1e5cee6a4c98b5cd"),
    HIKARI_CP("com.zaxxer", "HikariCP", "5.0.1", "com.zaxxer.hikari.HikariConfig", "26d492397e6775b4296737a8919bf04047afe5827fdd2c08b4557595436b3a2b"),
    SQLite("org.xerial", "sqlite-jdbc", "3.36.0.3", "org.sqlite.JDBC", "af3a3376391e186a0fed63ecd414b72a882bf452667b490a0be3abf85b637d3f");

    public static final String REPO_URL = "https://repo1.maven.org/maven2/";

    private final String group;
    private final String artifact;
    private final String version;
    private final String classTest;
    private final String hash;

    DependencyInjector(String group, String artifact, String version, String classTest, String hash) {
        this.group = group;
        this.artifact = artifact;
        this.version = version;
        this.classTest = classTest;
        this.hash = hash;
    }

    public String getLibraryName() {
        return artifact + '-' + version + ".jar";
    }

    public String getDependencyUrl() {
        return REPO_URL + group.replace('.', '/') + '/' +
                artifact + '/' +
                version + '/' +
                getLibraryName();
    }

    public boolean verifyDependency(Plugin plugin) {
        File dependency = getLibraryFile(plugin.getLibraryFolder());

        return verifyDependencyInternal(dependency);
    }

    public boolean inject(Plugin plugin) {
        File dependency = getLibraryFile(plugin.getLibraryFolder());

        return injectInternal(dependency);
    }

    public boolean downloadAndInject(Plugin plugin) {
        File dependency = getLibraryFile(plugin.getLibraryFolder());

        if (!injectInternal(dependency)) {
            plugin.getLogger().info("Downloading dependency " + artifact + '-' + version);
            downloadInternal(dependency);

            return injectInternal(dependency);
        }
        return true;
    }

    public void download(Plugin plugin) {
        File dependency = getLibraryFile(plugin.getLibraryFolder());

        downloadInternal(dependency);
    }

    private boolean injectInternal(File dependency) {
        if (verifyDependencyInternal(dependency)) {

            try {
                URLClassLoader loader = new URLClassLoader(new URL[] { dependency.toURI().toURL() }, getClass().getClassLoader());

                Class.forName(classTest, true, loader);

                return true;

            } catch (ClassNotFoundException e) {
                Bukkit.getLogger().warning("Class loading failed during dependency injection for " + artifact);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    private void downloadInternal(File dependency) {
        try {URL dependencyUrl = new URL(getDependencyUrl());

            URLConnection connection = dependencyUrl.openConnection();

            try (InputStream is = connection.getInputStream()) {
                try (BufferedInputStream bis = new BufferedInputStream(is)) {
                    try (FileOutputStream fos = new FileOutputStream(dependency)) {
                        byte[] dataBuffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = bis.read(dataBuffer, 0, 1024)) != -1) {
                            fos.write(dataBuffer, 0, bytesRead);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean verifyDependencyInternal(File dependency) {
        if (dependency.exists()) {
            try {
                return hash.equals(getFileChecksum(MessageDigest.getInstance("SHA-256"), dependency));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private File getLibraryFile(File libraryDirectory) {
        return new File(libraryDirectory, getLibraryName());
    }

    private static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        FileInputStream fis = new FileInputStream(file);

        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        fis.close();

        byte[] bytes = digest.digest();

        StringBuilder sb = new StringBuilder(2 * bytes.length);
        for(int i = 0; i< bytes.length; i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
}
