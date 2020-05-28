/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (BanList.java) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist;

import com.earth2me.essentials.Essentials;
import it.marko.banlist.handlers.BanRequestHandler;
import it.marko.banlist.handlers.FreezeRequestHandler;
import it.marko.banlist.handlers.OfflinePlayersRequestHandler;
import it.marko.banlist.handlers.OnlinePlayersRequestHandler;
import it.marko.banlist.handlers.essentials.JailRequestHandler;
import it.marko.banlist.handlers.essentials.MuteRequestHandler;
import it.marko.banlist.handlers.vault.EconomyRequestHandler;
import it.marko.banlist.handlers.vault.PermsRequestHandler;
import it.marko.banlist.server.Server;
import it.marko.freezer.Freezer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class BanList extends JavaPlugin {
    /**
     * Prefisso del plugin per messaggi in chat. Utilizza i {@link ChatColor color codes} di Bukkit.
     *
     * @see ChatColor
     */
    public static final String PREFIX = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[BanList] " + ChatColor.RESET;
    private static BanList instance;
    private Server server;
    private boolean isMutedEnabled;
    private boolean isJailedEnabled;
    private boolean isFreezeEnabled;
    private boolean isPermsEnabled;
    private boolean isEconomyEnabled;


    @Override
    public void onDisable() {
        super.onDisable();

        //fermo il server
        server.stop();

        //rimuovo l'instanza
        BanList.instance = null;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        //assegno l'istanza
        BanList.instance = this;

        //salvo i config di default
        saveDefaultConfig();

        //salvo il certificato se non esiste e ssl abilitato
        if(!new File(BanList.getInstance().getDataFolder(), getConfig().getString("ssl.name")).exists() && getConfig().getBoolean("ssl.active"))
            saveResource(getConfig().getString("ssl.name"), true);

        //deve essere abilitato il mute?
        Essentials e = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        if (e == null) {
            //avviso che essentials non è installato
            getLogger().warning("Essentials non è installato! Non potrai vedere i player mutati");

            //imposto la variabile
            isMutedEnabled = false;
        } else isMutedEnabled = getConfig().getBoolean("show.essentials.mute");

        //deve essere abilitato il jail?
        if (e == null) {
            //avviso che essentials non è installato
            getLogger().warning("Essentials non è installato! Non potrai vedere i player carcerati");

            //imposto la variabile
            isJailedEnabled = false;
        } else
            isJailedEnabled = getConfig().getBoolean("show.essentials.jail.jailed") || getConfig().getBoolean("show.essentials.jail.jails");

        //deve essere abilitato il freeze?
        Freezer f = (Freezer) getServer().getPluginManager().getPlugin("Freezer");
        if (f == null) {
            //avviso che essentials non è installato
            getLogger().warning("Freezer non è installato! Non potrai vedere i player mutati");

            //imposto la variabile
            isFreezeEnabled = false;
        } else isFreezeEnabled = getConfig().getBoolean("show.freeze");

        //deve essere abilitato vault permissions?
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            //avviso che essentials non è installato
            getLogger().warning("Vault non è installato! Non potrai vedere i gruppi dei player");

            //imposto la variabile
            isPermsEnabled = false;
        } else isPermsEnabled = getConfig().getBoolean("show.vault.permissions");

        //deve essere abilitato vault economy?
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            //avviso che essentials non è installato
            getLogger().warning("Vault non è installato! Non potrai vedere i bilanci dei player");

            //imposto la variabile
            isEconomyEnabled = false;
        } else
            isEconomyEnabled = getConfig().getBoolean("show.vault.economy.bank") || getConfig().getBoolean("show.vault.economy.balances");

        //avvio il server con un runnable
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    //creo il server con la porta definita in output.port
                    //se non sicuro
                    if(getConfig().getBoolean("ssl.active")) {
                        server = Server.buildSecure(getConfig().getInt("output.port"), Executors.newCachedThreadPool());
                    } else {
                        server = Server.buildInsecure(getConfig().getInt("output.port"), Executors.newCachedThreadPool());
                    }

                    //prendo il ban path
                    String banPath = getConfig().getString("output.path.ban");
                    //aggiungo un route per ban
                    server.createContext(banPath, new BanRequestHandler());

                    //se attivo il freeze lo carico
                    if (isFreezeEnabled) {
                        String freezePath = getConfig().getString("output.path.freeze");
                        server.createContext(freezePath, new FreezeRequestHandler());
                    }

                    //se attivo player Online lo carico
                    if (getConfig().getBoolean("show.onlinePlayers")) {
                        String onlinePath = getConfig().getString("output.path.onlinePlayers");
                        server.createContext(onlinePath, new OnlinePlayersRequestHandler());
                    }
                    //se attivo player offline lo carico
                    if (getConfig().getBoolean("show.offlinePlayers")) {
                        String offlinePath = getConfig().getString("output.path.offlinePlayers");
                        server.createContext(offlinePath, new OfflinePlayersRequestHandler());
                    }

                    //se attivo il mute lo carico
                    if (isMutedEnabled) {
                        String mutePath = getConfig().getString("output.path.essentials.mute");
                        server.createContext(mutePath, new MuteRequestHandler());
                    }

                    //se attivo il mute lo carico
                    if (isJailedEnabled) {
                        String jailPath = getConfig().getString("output.path.essentials.jail");
                        server.createContext(jailPath, new JailRequestHandler());
                    }

                    //se attivo vault permissions lo carico
                    if (isPermsEnabled) {
                        String pexPath = getConfig().getString("output.path.vault.permissions");
                        server.createContext(pexPath, new PermsRequestHandler());
                    }

                    //se attivo vault economy lo carico
                    if (isEconomyEnabled) {
                        String pexPath = getConfig().getString("output.path.vault.economy");
                        server.createContext(pexPath, new EconomyRequestHandler());
                    }

                    //avvio il server
                    printInfo("Avvio il server HTTP");
                    server.run();
                } catch (IOException | YAMLException | GeneralSecurityException e) {
                    //fornisco una breve spiegazione se l'eccezione è un'instanza di YAMLException o di GeneralSecurityException
                    if (e instanceof YAMLException) {
                        getLogger().severe("Sembra che il file di configurazione non sia valido!");
                    } else if (e instanceof GeneralSecurityException) {
                        getLogger().severe("Sembra che il file di sicurezza non sia valido!");
                    }

                    //stampo l'errore
                    printError(e);

                    //fermo il plugin
                    printError("Disabilito il plugin");
                    getServer().getPluginManager().disablePlugin(BanList.this);
                }
            }
        }.runTaskAsynchronously(this);

    }






    }

    /**
     * Usa questo metodo per ottenere l'istanza della classe {@link BanList}
     *
     * @return L'istanza corrente del plugin
     * @see JavaPlugin
     * @see Plugin
     */
    public static BanList getInstance() {
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
        printError(null, throwable);
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
