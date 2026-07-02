@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-24
set PATH=%JAVA_HOME%\bin;%PATH%

cd /d %~dp0
echo Starting backend on http://localhost:8080 ...
mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests
pause
