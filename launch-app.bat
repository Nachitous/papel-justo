@echo off
setlocal

set ADB=C:\Users\Nachitous\AppData\Local\Android\Sdk\platform-tools\adb.exe
set EMULATOR=C:\Users\Nachitous\AppData\Local\Android\Sdk\emulator\emulator.exe
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
set AVD=Medium_Phone_API_36.1
set PACKAGE=com.papeljusto.app

echo === Papel Justo Build and Run ===

REM Check if emulator is already running
%ADB% devices | findstr "emulator" >nul 2>&1
if %errorlevel% neq 0 (
    echo [1/4] Launching emulator...
    start "" "%EMULATOR%" -avd %AVD%
    echo Waiting for emulator to boot...
    %ADB% wait-for-device
    :waitboot
    %ADB% shell getprop sys.boot_completed 2>nul | findstr "1" >nul 2>&1
    if %errorlevel% neq 0 (
        timeout /t 2 /nobreak >nul
        goto waitboot
    )
    echo Emulator ready.
) else (
    echo [1/4] Emulator already running.
)

echo [2/4] Building...
call gradlew.bat installDebug
if %errorlevel% neq 0 (
    echo BUILD FAILED
    pause
    exit /b 1
)

echo [3/4] Launching app...
%ADB% shell am start -n %PACKAGE%/.MainActivity

echo [4/4] Done!
pause
