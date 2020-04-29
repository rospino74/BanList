# BanList
## Cos'è questo BanList?
BanList è un semplicissimo plugin per _Spigot/Bukkit_ che ti permette di avere in formato JSON la lista di utenti bannati nel tuo server Minecraft, sia per indirizzo IP che per username!
## Storia di BanList
Un giorno, non sapendo cosa fare a causa del coronavirus 🦠, ho perso quaranta minuti della mia vita a realizzare il plugin...
## Come si installa?
### Requisiti
Il plugin [Essentials](https://github.com/EssentialsX/Essentials) è necessario per visualizzare gli utenti mutati, ma ritengo che questo ottimo plugin tu lo abbia già installato!
### Utenti base
* Scarica l'[ultima relase](https://github.com/rospino74/BanList/releases/latest)
* Sposta il file `BanList-<version>.jar` nella cartella `plugins` del tuo server Minecraft
* Riavvia il server e all'indirizzo `http://<tuo-dominio-o-ip>:80/ban` troverai la lista di utenti bannati
### Utenti Avanzati
* Clona il repository e importalo nel tuo IDE
* Compila i files Java e Kotlin (è necessario che il tuo IDE sia configurato per quest'ultimo)
* Chiudi tutto in un Jar e dallo da mangiare al tuo server 😋!
## Configurazione
La configurazione di default del plugin è questa:
```yaml
output:
  path:
    ban: "/ban"
    mute: "/mute"
  port: 80
show:
  ban:
    byIP: true
    byNAME: true
  mute: true
```
* `output.path.ban`: Directory alla quale il server mostrerà come output la lista di utenti bannati
* `output.path.mute`: Directory alla quale il server mostrerà come output la lista di utenti mutati
* `output.port`: Porta sulla quale il server comunicherà, ricordati che la porta deve essere aperta e libera da altri servizi
* `show.ban.byIP`: Il plugin deve mostrate gli utenti bannati per indirizzo IP?
* `show.ban.byNAME`: Il plugin deve mostrate gli utenti bannati per username?
* `show.mute`: Il plugin deve mostrate gli utenti mutati? Se si è necessario il plugin [Essentials](https://github.com/EssentialsX/Essentials)
## Output Ban di esempio
```json
{
   "byNAME": [
      {
         "name":"MemoryOfLife",
         "until":1585564044000,
         "forever":false,
         "created":1585564034000,
         "admin":"MemoryOfLife",
         "reason":"Sparisci dal mio server, Canaglia!"
      }
   ],
   "byIP": [
    {
         "name":"127.0.0.1",
         "until":0,
         "forever":true,
         "created":1585564034000,
         "admin":"MemoryOfLife",
         "reason":"Sparisci dal mio server, Canaglia!"
      }
   ]
}
```
| Chiave | Tipo | Significato |
| :--- | :---: | --- |
| `name` | `String` | Nome del player bannato o il suo indirizzo IP |
| `until` | `int` | Data del termine del ban. È una data formato Unix |
| `forever` | `bool` | Se è `true` il ban è permanente |
| `created` | `int` | Data di creazione del ban. È una data formato Unix |
| `admin` | `String` | Nome del admin che ha effetuato il ban. Può essere il nome di un player o `Server` se il ban è eseguito dalla console |
| `reason` | `String` | Motivo del ban |
## Output Mute di esempio
```json
{
   "mute": [
      {
         "name":"MemoryOfLife",
         "until":1585564044000,
         "forever": false,
         "reason":"Stai muto nel mio server, Canaglia!"
      }
   ]
}
```
| Chiave | Tipo | Significato |
| :--- | :---: | --- |
| `name` | `String` | Nome del player mutato |
| `until` | `int` | Data del termine del mute. È una data formato Unix |
| `forever` | `bool` | Se è `true` il mute è permanente |
| `reason` | `String` | Motivo del mute |
## Errori comuni
* `java.net.BindException`: La porta scelta è già in uso, cambiarla nel file di configurazione
* `java.io.IOException`: Si è verificato un errore nel comunicare con un altro sistema
* `org.yaml.snakeyaml.error.YAMLException`: Si è verificato un errore nel leggere il file di configurazione, verifica se è valido!
