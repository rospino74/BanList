@echo off
title Building Task
@rem ----> Compilo <----
set DIRNAME=%~dp0
call "%DIRNAME%gradlew.bat" shadowJar
echo Compilazione completata
echo Spostamento in corso...
move /Y build\libs\*.jar %1
echo Spostamento completato
exit