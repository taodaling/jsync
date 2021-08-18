package com.daltao.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

public class Config {
    public String pwd = "123456";
    public String root = "";
    public int maxOpenedFile = 1 << 10;
    public int block = 1 << 12;
    //my birthday, smile
    public int port = 50823;
    public String remote = "/";
    public String host = "localhost";
    public boolean delete = false;

    public Config() {
        try {
            cover(readLocalConfig());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        cover(System.getProperties());
    }

    private static Properties readLocalConfig() throws IOException {
        String home = System.getenv("jsync.home");
        if (home == null) {
            return null;
        }
        File homeFile = new File(home);
        File config = new File(homeFile + "/conf/setting.properties");
        if (!config.exists()) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(config)) {
            Properties p = new Properties();
            p.load(fis);
            return p;
        }
    }

    public void cover(Properties properties) {
        if (properties == null) {
            return;
        }
        if (properties.containsKey("pwd")) {
            pwd = properties.getProperty("pwd");
        }
        if (properties.containsKey("root")) {
            root = properties.getProperty("root");
        }
        if (properties.containsKey("maxOpenedFile")) {
            maxOpenedFile = Integer.parseInt(properties.getProperty("maxOpenedFile"));
        }
        if (properties.containsKey("port")) {
            port = Integer.parseInt(properties.getProperty("port"));
        }
        if (properties.containsKey("remote")) {
            remote = properties.getProperty("remote");
        }
        if (properties.containsKey("host")) {
            host = properties.getProperty("host");
        }
        if (properties.containsKey("block")) {
            block = Integer.parseInt(properties.getProperty("block"));
        }
        if(properties.containsKey("delete")){
            delete = Boolean.parseBoolean(properties.getProperty("delete"));
        }
    }

    @Override
    public String toString() {
        return "Config{" +
                "pwd='" + pwd + '\'' +
                ", root='" + root + '\'' +
                ", maxOpenedFile=" + maxOpenedFile +
                ", block=" + block +
                ", port=" + port +
                ", remote='" + remote + '\'' +
                ", host='" + host + '\'' +
                ", delete=" + delete +
                '}';
    }
}
