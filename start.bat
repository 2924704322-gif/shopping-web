@echo off
REM 购物平台信息系统 - 启动脚本
REM 使用 JDK 24 运行（需确认安装路径正确）

set JAVA_HOME=C:\Program Files\Java\jdk-24
set PATH=%JAVA_HOME%\bin;%PATH%

echo Starting Shopping Web Application...
java -jar target\shopping-web-1.0.0.jar --spring.profiles.active=dev

pause
