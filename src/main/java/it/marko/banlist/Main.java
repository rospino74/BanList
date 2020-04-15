/*
 * Copyright (c) 2020. MemoryOfLife
 * This file (Main.java) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist;

import com.sun.net.httpserver.HttpServer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main extends JavaPlugin {
    private static Main instance;
    private HttpServer server;

    @Override
    public void onDisable() {
        super.onDisable();

        //fermo il server
        stopHTTPServer();

        //rimuovo l'instanza
        Main.instance = null;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        //assegno l'instanza
        Main.instance = this;

        //prendo i dati da config
        saveDefaultConfig();
        String path = getConfig().getString("output.path");
        int port = getConfig().getInt("output.port");

        //avvio il server in un runnable
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    buildHTTPServer(path, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();

    }

    private void buildHTTPServer(String path, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(path, new RequestHandler());
        server.setExecutor(null);
        server.start();
    }

    private void stopHTTPServer() {
        server.stop(0);
    }

    public static Main getInstance() {
        return instance;
    }
}
