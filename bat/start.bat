@echo off
cls
color 0a
cd  ..
: spring-javaformat:apply
call mvn spring-javaformat:apply
goto menu
:menu
echo.
echo. * * * * * * * * * * mvn commands * * * * * * * * * *
echo. * * 快速使用mvn命令编译maven工程 * *
echo. * 0 -Dmaven.test.skip=true 默认为true，可通过02、03、04方式设置不跳过测试
echo. * *
echo. * 1 mvn clean
echo. * *
echo. * 2 mvn clean install
echo. * *
echo. * 3 mvn clean compile
echo. * *
echo. * 4 mvn clean package
echo. * *
echo. * 99 exit
echo. * *
echo. * * * * * * * * * * * * * * * * * * * * * * * * * * *
echo.
echo 请输入选择项序号：
set /p ID=

set skipTest=-Dmaven.test.skip=true

if "%ID%"=="1" goto clean

if "%ID%"=="2" goto install

if "%ID%"=="3" goto compile

if "%ID%"=="4" goto package

if "%ID%"=="02" (set skipTest= & goto install)

if "%ID%"=="03" (set skipTest= & goto compile)

if "%ID%"=="04" (set skipTest= & goto package)

if "%ID%"=="99" goto quit
goto quit

:clean
echo. 开始clean
call mvn clean
echo.
echo.
pause
goto menu

:install
echo. 开始执行：mvn clean install %skipTest%
call mvn clean install %skipTest%
echo.
echo.
pause
goto menu

:compile
echo. 开始执行：mvn clean compile %skipTest%
call mvn clean compile %skipTest%
echo.
echo.
pause
goto menu

:package
echo. 开始执行：mvn clean package %skipTest%
call mvn clean package %skipTest%
echo.
echo.
pause
goto menu

:quit
if not {%ID%}=={99} echo 输入的选项不正确 & pause & goto menu