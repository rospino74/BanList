/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (BanList.java) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist;

import com.earth2me.essentials.Essentials;
import it.marko.banlist.server.RequestHandler;
import it.marko.banlist.server.Server;
import it.marko.freezer.Freezer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;
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


    @Override
    public void onDisable() {
        super.onDisable();

        //fermo il server
        server.stop();
        printInfo("Fermo il server HTTP");

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
        String keyName = getConfig().getString("ssl.name", "key.jks");
        if (!new File(BanList.getInstance().getDataFolder(), keyName).exists() && getConfig().getBoolean("ssl.active", false))
            saveResource(keyName, true);

        //deve essere abilitato il mute?
        Essentials e = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        if (e == null) {
            //avviso che essentials non è installato
            getLogger().warning("Essentials non è installato! Non potrai vedere i player mutati e quelli carcerati");

            //imposto la variabile
            getConfig().set("show.essentials.Mute", false);
            getConfig().set("show.essentials.Jail", false);
        }

        //deve essere abilitato il freeze?
        Freezer f = (Freezer) getServer().getPluginManager().getPlugin("Freezer");
        if (f == null) {
            //avviso che essentials non è installato
            getLogger().warning("Freezer non è installato! Non potrai vedere i player mutati");

            //imposto la variabile
            getConfig().set("show.Freeze", false);
        }

        //deve essere abilitato vault permissions?
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            //avviso che essentials non è installato
            getLogger().warning("Vault non è installato! Non potrai vedere i gruppi dei player e i bilanci dei player");

            //imposto la variabile
            getConfig().set("show.vault.Permissions", false);
            getConfig().set("show.vault.Economy", false);
        }

        //avvio il server con un runnable
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    //creo il server con la porta definita in output.port
                    //se non sicuro
                    if (getConfig().getBoolean("ssl.active")) {
                        server = Server.buildSecure(getConfig().getInt("output.port"), Executors.newCachedThreadPool());
                    } else {
                        server = Server.buildInsecure(getConfig().getInt("output.port"), Executors.newCachedThreadPool());
                    }

                    enableRoutes("show", getConfig()
                            .getConfigurationSection("show")
                            .getKeys(false));

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

    private void enableRoutes(String path, Set<String> list) {
        list.forEach(obj -> {
            //percorso corrente
            String newPath = path + "." + obj;

            try {
                //cerco di ottenere le chiavi dell'oggetto, se fallisco vuol dire che è una chiave
                Set<String> objects = getConfig()
                        .getConfigurationSection(newPath)
                        .getKeys(false);

                //ripeto con nuovo percorso
                enableRoutes(newPath, objects);
            } catch (ClassCastException | NullPointerException ex) {
                //se non è abilitato ritorno
                if (!getConfig().getBoolean(newPath))
                    return;

                //nome della classe
                String className = "it.marko.banlist.handlers" + newPath.replace("show", "") + "RequestHandler";

                try {
                    //prendo la classe
                    Class<? extends RequestHandler> handler = (Class<? extends RequestHandler>) Class.forName(className);

                    //prendo l'url di output
                    String url = getConfig().getString("output.path" + newPath.replace("show", ""));

                    //aggiungo il contesto
                    server.createContext(url, handler.newInstance());

                } catch (ClassNotFoundException e) {
                    printError("Classe " + className + "non trovata", e);
                } catch (IllegalAccessException | InstantiationException e) {
                    printError("Classe " + className + "non stanziabile", e);
                }
            }
        });
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
