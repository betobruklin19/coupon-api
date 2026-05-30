@echo off
setlocal
if "%JAVA_HOME%"=="" (
    echo ERROR: Configure JAVA_HOME apontando para o JDK 21.
    exit /b 1
)
if defined MAVEN_HOME (
    "%MAVEN_HOME%\bin\mvn.cmd" %*
) else (
    call "%~dp0mvnw.cmd" %*
)
