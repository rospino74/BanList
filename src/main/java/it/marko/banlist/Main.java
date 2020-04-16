/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (Main.java) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist;

import com.sun.net.httpserver.HttpServer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    /**
     * Prefisso del plugin per messaggi in chat. Utilizza i {@link ChatColor color codes} di Bukkit.
     *
     * @see ChatColor
     */
    public static final String PREFIX = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[BanList] " + ChatColor.RESET;
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

        //salvo i config di default
        saveDefaultConfig();

        //avvio il server in un runnable
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    //prendo i dati
                    String path = getConfig().getString("output.path");
                    int port = getConfig().getInt("output.port");

                    //creo il server
                    buildHTTPServer(path, port);
                } catch (IOException | YAMLException e) {
                    //stampo l'errore
                    getLogger().log(Level.SEVERE, PREFIX + "Errore nel creare il server");

                    //fornisco una breve spiegazione se l'eccezione è un'instanza di YAMLException
                    if (e instanceof YAMLException)
                        getLogger().log(Level.SEVERE, PREFIX + "Sembra che il file di configurazione non sia valido!");

                    //stampo un blocco con lo stackTrace
                    getLogger().log(Level.SEVERE, ChatColor.RED + "" + ChatColor.BOLD + "===== " + PREFIX + ChatColor.RED + "" + ChatColor.BOLD + "Inizio Report =====", e);
                    getLogger().log(Level.SEVERE, ChatColor.RED + "" + ChatColor.BOLD + "===== Fine Report =====");
                }
            }
        }.runTaskAsynchronously(this);

    }

    /**
     * Crea un'instanza del server HTTP
     * @param path Percorso da utilizzare per la creazione del server
     * @param port Porta da utilizzare per la creazione del server
     * @throws IOException Lancia una {@link IOException} se si verifica un errore nella creazione del server
     *
     * @see #stopHTTPServer()
     */
    private void buildHTTPServer(String path, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(path, new RequestHandler());
        server.setExecutor(null);
        server.start();
    }

    /**
     * Ferma il server creato con {@link #buildHTTPServer(String, int)}
     */
    private void stopHTTPServer() {
        server.stop(0);
    }

    /**
     * Usa questo medoto per ottenere l'istanza della classe {@link Main}
     *
     * @return L'istanza corrente del plugin
     * @see JavaPlugin
     */
    public static Main getInstance() {
        return instance;
    }
}
