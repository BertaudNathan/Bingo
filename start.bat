@echo off
echo Compilation avec Gradle...
call gradlew.bat clean build

if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors de la compilation
    pause
    exit /b 1
)

echo Copie du plugin vers le serveur...
copy "build\libs\Bingo-1.0-SNAPSHOT.jar" "server\plugins\Bingo.jar"

if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors de la copie du JAR
    pause
    exit /b 1
)

echo Demarrage du serveur Spigot avec 4 Go de RAM...
cd "server"
java -Xmx4096M -Xms4096M -jar spigot-1.21.11.jar nogui
