Mit Ant von der build.xml 
getDependencies
und
installDependencies
ausführen um Abhängigkeiten die nicht in Maven Repositories zu finden sind einerseits
runterzuladen und dann im lokale Repository zu installieren.

Danach
mvn clean install
im Hauptverzeichnis ausführen um Xirp zu erstellen.

In target erscheint dann ein Installer.

Um Xirp zu starten muss in Ordner lib je nach Betriebssystem der Ordner
windows oder linux gelöscht werden. Danach kann xirp.bat oder xirp.sh zum 
Starten benutzt werden.