@ECHO off
Color 6
TITLE Mateo's Battleship Game
CALL:intro
ECHO Loading Classes
powershell.exe -Command "Start-Sleep -MilliSeconds 250" 
:: This isn't necessary
ECHO Compiling
javac %USERPROFILE%\Downloads\PlayBattleship.java
ECHO Executing
powershell.exe -Command "Start-Sleep -MilliSeconds 1899" 
:: :D
cls && Color 7
java %USERPROFILE%\Downloads\PlayBattleship.java && ECHO( && ECHO( && PAUSE && cls
CALL:despedida
EXIT

:despedida
ECHO(
ECHO(
ECHO Thank you for running my program!
SET /p input = Press any key to exit.
EXIT /b

:intro
ECHO ---------------------------
ECHO ^|       Battleship        ^|
ECHO ---------------------------
EXIT /b