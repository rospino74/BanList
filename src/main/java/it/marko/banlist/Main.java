/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (Main.java) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist;

import com.sun.net.httpserver.HttpServer;
import it.marko.banlist.handlers.BanRequestHandler;
import it.marko.banlist.handlers.FreezeRequestHandler;
import it.marko.freezer.Freezer;
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
    private boolean isFreezeEnabled;

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

        //deve essere abilitato il freeze?
        Freezer f = (Freezer) getServer().getPluginManager().getPlugin("Freezer");
        if (f == null) {
            //avviso che essentials non è installato
            getLogger().warning("Freezer non è installato! Non potrai vedere i player mutati");

            //imposto la variabile
            isFreezeEnabled = false;
        } else isFreezeEnabled = getConfig().getBoolean("show.freeze");

        //avvio il server in un runnable
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    //prendo i dati
                    String banPath = getConfig().getString("output.ban.path");

                    //creo il server con la porta definita in output.port
                    buildHTTPServer(getConfig().getInt("output.port"));

                    //aggiungo un route per ban
                    server.createContext(banPath, new BanRequestHandler());

                    //se attivo il freeze lo carico
                    if (isFreezeEnabled) {
                        String freezePath = getConfig().getString("output.path.freeze");
                        server.createContext(freezePath, new FreezeRequestHandler());
                    }
                } catch (IOException | YAMLException e) {
                    //stampo l'errore
                    getLogger().severe(PREFIX + "Errore nel creare il server");

                    //fornisco una breve spiegazione se l'eccezione è un'instanza di YAMLException
                    if (e instanceof YAMLException)
                        getLogger().severe(PREFIX + "Sembra che il file di configurazione non sia valido!");

                    //stampo un blocco con lo stackTrace
                    getLogger().log(Level.SEVERE, ChatColor.RED + "" + ChatColor.BOLD + "===== " + PREFIX + ChatColor.RED + "" + ChatColor.BOLD + "Inizio Report =====", e);
                    getLogger().severe(ChatColor.RED + "" + ChatColor.BOLD + "===== Fine Report =====");
                }
            }
        }.runTaskAsynchronously(this);

    }

    /**
     * Crea un'instanza del server HTTP
     * @param port Porta da utilizzare per la creazione del server
     * @throws IOException Lancia una {@link IOException} se si verifica un errore nella creazione del server
     *
     * @see #stopHTTPServer()
     */
    private HttpServer buildHTTPServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(null);

        //ritorno il server
        return server;
    }

    /**
     * Ferma il server creato con {@link #buildHTTPServer(int)}
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
