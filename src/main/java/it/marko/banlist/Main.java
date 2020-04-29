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
import it.marko.banlist.handlers.FreezeRequestHandler;
import it.marko.freezer.Freezer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
    private boolean isFreezeEnabled;


    @Override
    public void onDisable() {
        super.onDisable();

        //fermo il server
        stopHTTPServer(1);

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
                    //creo il server con la porta definita in output.port
                    buildHTTPServer(getConfig().getInt("output.port"), Executors.newCachedThreadPool());

                    //prendo il ban path
                    String banPath = getConfig().getString("output.path.ban");
                    //aggiungo un route per ban
                    server.createContext(banPath, new BanRequestHandler());

                    //se attivo il freeze lo carico
                    if (isFreezeEnabled) {
                        String freezePath = getConfig().getString("output.path.freeze");
                        server.createContext(freezePath, new FreezeRequestHandler());
                    }
                  
                  //se attivo il mute lo carico
                    if (isMutedEnabled) {
                        String mute_path = getConfig().getString("output.path.mute");
                        server.createContext(mute_path, new MuteRequestHandler());
                    }

                    //avvio il server
                    printInfo("Avvio il server");
                    server.start();
                } catch (IOException | YAMLException e) {
                    //fornisco una breve spiegazione se l'eccezione è un'instanza di YAMLException
                    if (e instanceof YAMLException)
                        getLogger().severe("Sembra che il file di configurazione non sia valido!");

                    //stampo l'errore
                    printError(e);
                }
            }
        }.runTaskAsynchronously(this);

    }

    /**
     * Crea un'instanza del server HTTP
     *
     * @param port Porta da utilizzare per la creazione del server
     * @param executor Processo sul quale eseguire il server
     * @throws IOException Lancia una {@link IOException} se si verifica un errore nella creazione del server
     * @see #stopHTTPServer(int)
     */
    private HttpServer buildHTTPServer(int port, Executor executor) throws IOException {
        //avviso
        printInfo("Creo il server sulla porta " + port);

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(executor);

        //ritorno il server
        return server;
    }

    /**
     * Ferma il server creato con {@link #buildHTTPServer(int, Executor)}
     *
     * @param errCode messaggio di errore
     */
    private void stopHTTPServer(int errCode) {
        //se il server non esiste ritorno
        if (server == null)
            return;

        //avviso
        printInfo("Fermo il server");

        //fermo il server
        server.stop(errCode);
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

    /**
     * Stampo l'errore in un box
     *
     * @param level     Livello di logging
     * @param reason    Motivo dell'errore
     * @param throwable Eccezione causa dell'errore
     * @see #printError(Throwable)
     * @see #printError(String, Throwable)
     * @see #printError(String)
     * @see #printInfo(String)
     */
    public void printError(Level level, String reason, Throwable throwable) {
        //stampo l'errore se reason != null
        if (reason != null)
            getLogger().log(level, reason);

        //stampo un blocco con lo stackTrace se throwable != null
        if (throwable != null) {
            getLogger().log(level, "========== Inizio Report ==========", throwable);
            getLogger().log(level, "==========  Fine Report  ==========");
        }
    }

    /**
     * Stampo l'errore in un box con livello {@link Level#SEVERE}
     *
     * @param reason    Motivo dell'errore
     * @param throwable Eccezione causa dell'errore
     * @see #printError(Throwable)
     * @see #printError(Level, String, Throwable)
     * @see #printError(String)
     * @see #printInfo(String)
     */
    public void printError(String reason, Throwable throwable) {
        printError(Level.SEVERE, reason, throwable);
    }

    /**
     * Stampo l'eccezione in un box con livello {@link Level#SEVERE}
     *
     * @param throwable Eccezione causa dell'errore
     * @see #printError(String, Throwable)
     * @see #printError(Level, String, Throwable)
     * @see #printError(String)
     * @see #printInfo(String)
     */
    public void printError(Throwable throwable) {
        printError(Level.SEVERE, null, throwable);
    }

    /**
     * Stampo l'errore in un box con livello {@link Level#WARNING}
     *
     * @param reason Motivo dell'errore
     * @see #printError(String, Throwable)
     * @see #printError(Level, String, Throwable)
     * @see #printError(Throwable)
     * @see #printInfo(String)
     */
    public void printError(String reason) {
        printError(reason, null);
    }

    /**
     * Stampo un messaggio in un box con livello {@link Level#INFO}
     *
     * @param reason Messaggio da stampare
     * @see #printError(String, Throwable)
     * @see #printError(Level, String, Throwable)
     * @see #printError(Throwable)
     */
    public void printInfo(String reason) {
        printError(Level.INFO, reason, null);
    }
}
