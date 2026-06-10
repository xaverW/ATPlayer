[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

# ATPlayer

Das Programm ATPlayer ist eine Art Suchmaschine für Beiträge der ARD-Audiothek. Das Programm stellt eine Liste mit Links zu den Audios zur Verfügung. Es ist möglich, diese URLs an externe Programme weiterzugeben. Mit diesen Programmen können dann diese Audios angehört oder aufgezeichnet werden.
<br />


## Infos

Das Programm nutzt den Ordner ".p2Atplayer" unter Linux oder den versteckten Ordner "p2Atplayer" unter Windows als Konfig-Ordner. Man kann dem Programm auch einen Ordner für die Einstellungen mitgeben (und es z.B. auf einem USB-Stick verwenden):

```
java -jar ATPlayer.jar ORDNER 
```
<br />


## Systemvoraussetzungen

Unterstützt wird Windows und Linux. Das Programm benötigt eine aktuelle Java-VM ab Version: Java 17. Für Linux-Benutzer wird OpenJDK empfohlen. (FX-Runtime bringt das Programm bereits mit und muss nicht installiert werden).
<br />


## Download

Das Programm wird in verschiedenen Paketen angeboten. Diese unterscheiden sich nur im "Zubehör", das Programm selbst ist in allen Paketen identisch:

* **ATPlayer-XX__Windows==SETUP__DATUM.exe**  
Mit diesem Programmpaket kann das Programm auf Windows installiert werden: Doppelklick und alles wird eingerichtet, auch ein Startbutton auf dem Desktop. Es muss auch kein Java auf dem System installiert sein. (Die Java-Laufzeitumgebung ist enthalten).

* **ATPlayer-XX__DATUM.zip**  
Das Programmpaket bringt nur das Programm und die benötigten Hilfsprogramme aber kein Java mit. Auf dem Rechner muss eine Java-Laufzeitumgebung ab Java17 installiert sein. Dieses Programmpaket kann auf allen Betriebssystemen verwendet werden. Es bringt Startdateien für Linux und Windows mit. Zip entpacken und Programm Starten.

* **ATPlayer-XX__Linux+Java__DATUM.zip**  
**ATPlayer-XX__Win+Java__DATUM.zip**  
Diese Programmpakete bringen die Java-Laufzeitumgebung mit und sind nur für das angegebene Betriebssystem: Linux oder Windows. Es muss kein Java auf dem System installiert sein. (Die Java-Laufzeitumgebung liegt im Ordner: "Java" und kommt von jdk.java.net). Zip entpacken und Programm starten.

* **ATPlayer-XX__Raspberry__DATUM.zip**  
Das ist ein Programmpaket, das auf einem Raspberry verwendet werden kann. Java muss installiert sein und es muss ein aktueller Raspberry mit einer 64Bit CPU mit AArch64 Architektur sein. Zip entpacken und Programm Starten.

Linux / Windows:  
Der VLC-Player muss installiert sein.  

Weitere Infos zum Programm (Start und Benutzung) sind im Download-Paket enthalten oder auf der Website.

zum Download:  
[github.com/xaverW/ATPlayer/releases](https://github.com/xaverW/ATPlayer/releases)  
[https://www.p2tools.de/atplayer/download](https://www.p2tools.de/atplayer/download)
<br />


## Installation

ATPlayer muss nicht installiert werden, das Entpacken der heruntergeladenen ZIP-Datei ist quasi die Installation. Die heruntergeladene ZIP-Datei entpacken und den entpackten Ordner ATPlayer ins Benutzerverzeichnis verschieben. Das Programm kann dann mit Doppelklick auf:  

Linux: “ATPlayer__Linux.sh” oder  
Windows: “ATPlayer__Windows.exe”  
gestartet werden.
<br />


## Website

[www.p2tools.de]( https://www.p2tools.de)


