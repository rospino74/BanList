/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (Main.java) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist;

import com.earth2me.essentials.Essentials;
import com.sun.net.httpserver.HttpServer;
import it.marko.banlist.handlers.BanRequestHandler;
import it.marko.banlist.handlers.MuteRequestHandler;
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
    private boolean isMutedEnabled;

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

        //assegno l'istanza
        Main.instance = this;

        //salvo i config di default
        saveDefaultConfig();

        //deve essere abilitato il mute?
        Essentials e = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        if (e == null) {
            //avviso che essentials non è installato
            getLogger().warning("Essentials non è installato! Non potrai vedere i player mutati");

            //imposto la variabile
            isMutedEnabled = false;
        } else isMutedEnabled = getConfig().getBoolean("show.mute");

        //avvio il server in un runnable
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    //creo il server
                    int port = getConfig().getInt("output.port");
                    buildHTTPServer(port);

                    //prendo i dati per ban
                    String ban_path = getConfig().getString("output.path.ban");
                    //imposto l'Handler
                    server.createContext(ban_path, new BanRequestHandler());

                    //se attivo il mute lo carico
                    if (isMutedEnabled) {
                        String mute_path = getConfig().getString("output.path.mute");
                        server.createContext(mute_path, new MuteRequestHandler());
                    }

                    //eseguo il server
                    server.start();
                } catch (IOException | YAMLException e) {
                    //stampo l'errore
                    getLogger().severe("Errore nel creare il server");

                    //fornisco una breve spiegazione se l'eccezione è un'instanza di YAMLException
                    if (e instanceof YAMLException)
                        getLogger().severe("Sembra che il file di configurazione non sia valido!");

                    //stampo un blocco con lo stackTrace
                    getLogger().log(Level.SEVERE, ChatColor.RED + "" + ChatColor.BOLD + "===== " + PREFIX + ChatColor.RED + "" + ChatColor.BOLD + "Inizio Report =====", e);
                    getLogger().severe( ChatColor.RED + "" + ChatColor.BOLD + "===== Fine Report =====");
                }
            }
        }.runTaskAsynchronously(this);

    }

    /**
     * Crea un'istanza del server HTTP
     *
     * @param port Porta da utilizzare per la creazione del server
     * @return {@link HttpServer} il server appena creato
     * @throws IOException Lancia una {@link IOException} se si verifica un errore nella creazione del server
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
     * Usa questo metodo per ottenere l'istanza della classe {@link Main}
     *
     * @return L'istanza corrente del plugin
     * @see JavaPlugin
     */
    public static Main getInstance() {
        return instance;
    }
}
