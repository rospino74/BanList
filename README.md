# BanList
## Cos'è questo BanList?
BanList è un semplicissimo plugin per _Spigot/Bukkit_ che ti permette di avere in formato JSON la lista di utenti bannati nel tuo server Minecraft, sia per indirizzo IP che per username!
## Storia di BanList
Un giorno, non sapendo cosa fare a causa del coronavirus 🦠, ho perso quaranta minuti della mia vita a realizzare il plugin...
## Come si installa?
### Requisiti
Il plugin [Essentials](https://github.com/EssentialsX/Essentials) è necessario per visualizzare gli utenti mutati, ma ritengo che questo ottimo plugin tu lo abbia già installato!
Inoltre, se vuoi vedere la lista di utenti congelati, installa il mio plugin [Freezer](https://github.com/rospino74/Freezer). Non ti basta ancora? Con [Vault](https://github.com/MilkBowl/Vault) installato potrai addirittura ottenere i gruppi di utenti e i loro bilanci!
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
# Percorsi in cui mostrare l'output
output:
  path:
    ban: "/ban"
    freeze: "/freeze"
    essentials:
      mute: "/mute"
      jail: "/jail"
    vault:
      permissions: "/permissions"
      economy: "/economy"
  port: 80

# Cosa devo mostrare?
show:
  ban:
    byIP: true
    byNAME: true
  essentials:
    mute: true
    jail: true
  freeze: true
  vault:
    permissions: true
    economy:
      banks: true
      balances: true

```
* `output.path.ban`: Directory alla quale il server mostrerà come output la lista di utenti bannati
* `output.path.freeze`: Directory alla quale il server mostrerà come output la lista di utenti congelati
* `output.path.essentials.mute`: Directory alla quale il server mostrerà come output la lista di utenti mutati
* `output.path.essentials.jail`: Directory alla quale il server mostrerà come output la lista di utenti reclusi
* `output.path.vault.permissions`: Directory alla quale il server mostrerà come output la lista di permessi
* `output.path.vault.economy`: Directory alla quale il server mostrerà come output informazioni sull'economia
* `output.port`: Porta sulla quale il server comunicherà. Ricordati che la porta deve essere aperta e libera da altri servizi
* `show.ban.byIP`: Il plugin deve mostrate gli utenti bannati per indirizzo IP?
* `show.ban.byNAME`: Il plugin deve mostrate gli utenti bannati per username?
* `show.essentials.mute`: Il plugin deve mostrate gli utenti mutati? Se si è necessario il plugin [Essentials](https://github.com/EssentialsX/Essentials)
* `show.essentials.jail`: Il plugin deve mostrate gli utenti reclusi? Se si è necessario il plugin [Essentials](https://github.com/EssentialsX/Essentials)
* `show.freeze`: Il plugin deve mostrate gli utenti congelati? Se si è necessario il plugin [Freezer](https://github.com/rospino74/Freezer)
* `show.vault.permissions`: Il plugin deve mostrate i gruppi? Se si è necessario il plugin [Vault](https://github.com/MilkBowl/Vault)
* `show.vault.economy`: Il plugin deve mostrate i gruppi? Se si è necessario il plugin [Vault](https://github.com/MilkBowl/Vault)
## Output di esempio
### Ban
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
### Mute
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
### Freeze
```json
{
   "freeze": [
      {
         "name":"MemoryOfLife"
      }
   ]
}
```
| Chiave | Tipo | Significato |
| :--- | :---: | --- |
| `name` | `String` | Nome del player congelato |
### Permissions
```json
{
  "groups": [
    {
      "name": "default",
      "members": [
        "MemoryOfLife"
      ]
    },
    {
      "name": "admin",
      "members": [
        "MemoryOfLife"
      ]
    }
  ]
}
```
| Chiave | Tipo | Significato |
| :--- | :---: | --- |
| `name` | `String` | Nome del gruppo |
| `members` | `Array` di `String` | Membri del gruppo |
### Economy
```json
{
  "balances": [
    {
      "name": "MemoryOfLife",
      "balance": 10000000000000
    }
  ],
  "banks": [
    {
      "name": "banca che ho appena derubato",
      "balance": 0
    }
  ]
}
```
| Chiave | Tipo | Significato |
| :--- | :---: | --- |
| `name` | `String` | Nome del giocatore o della banca |
| `balance` | `int` | Bilancio attuale |
### Jail
```json
{
  "jailed": [
    {
      "name": "MemoryOfLife",
      "until": 1588255790378,
      "forever": false,
      "jail": "a"
    }
  ],
  "jails": [
    {
      "name": "a",
      "location": {
        "x": -142.7315708181958,
        "y": 45,
        "z": 793.8098936062091,
        "world": "SuperFlat"
      }
    }
  ]
}
```
#### Player Carcerato
| Chiave | Tipo | Significato |
| :--- | :---: | --- |
| `name` | `String` | Nome del giocatore |
| `until` | `int` | Data del termine della prigionia. È una data formato Unix |
| `forever` | `bool` | Se è `true` la prigionia è permanente |
| `jail` | `String` | Nome della cella dove il player è prigioniero |
#### Prigione
| Chiave | Tipo | Significato |
| :--- | :---: | --- |
| `name` | `String` | Nome della prigione |
| `location.x` | `float` | Coordinata _X_ della cella |
| `location.y` | `float` | Coordinata _Y_ della cella |
| `location.z` | `float` | Coordinata _Z_ della cella |
| `location.world` | `String` | Nome del mondo nel quale è situata la cella |
## Errori comuni
* `java.net.BindException`: La porta scelta è già in uso, cambiarla nel file di configurazione
* `java.io.IOException`: Si è verificato un errore nel comunicare con un altro sistema
* `org.yaml.snakeyaml.error.YAMLException`: Si è verificato un errore nel leggere il file di configurazione, verifica se è valido!
