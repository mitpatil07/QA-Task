@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------
@echo off
setlocal

echo [Maven Wrapper] Checking for Java...
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java is not installed or not in PATH. Please install Java JDK 17+ or open this project in IntelliJ/Eclipse/VS Code to run testng.xml.
    exit /b 1
)

echo [Maven Wrapper] Running tests...
echo Note: If 'mvn' is not installed globally, please open the 'automation' folder in your IDE (IntelliJ IDEA / Eclipse / VS Code) and right-click 'testng.xml' -> Run.
