@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  app startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and APP_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\app-plain.jar;%APP_HOME%\lib\guava-32.1.2-jre.jar;%APP_HOME%\lib\spring-boot-starter-web-2.6.1.jar;%APP_HOME%\lib\spring-session-data-redis-2.6.0.jar;%APP_HOME%\lib\spring-session-core-2.6.0.jar;%APP_HOME%\lib\spring-boot-starter-data-redis-2.6.1.jar;%APP_HOME%\lib\jedis-3.7.0.jar;%APP_HOME%\lib\redisson-3.16.0.jar;%APP_HOME%\lib\jaxb-api-2.3.1.jar;%APP_HOME%\lib\jaxb-core-2.3.0.1.jar;%APP_HOME%\lib\jaxb-impl-2.3.2.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-qual-3.33.0.jar;%APP_HOME%\lib\error_prone_annotations-2.18.0.jar;%APP_HOME%\lib\spring-boot-starter-json-2.6.1.jar;%APP_HOME%\lib\spring-boot-starter-2.6.1.jar;%APP_HOME%\lib\spring-boot-starter-tomcat-2.6.1.jar;%APP_HOME%\lib\spring-webmvc-5.3.13.jar;%APP_HOME%\lib\spring-web-5.3.13.jar;%APP_HOME%\lib\spring-boot-autoconfigure-2.6.1.jar;%APP_HOME%\lib\spring-boot-2.6.1.jar;%APP_HOME%\lib\spring-data-redis-2.6.0.jar;%APP_HOME%\lib\spring-data-keyvalue-2.6.0.jar;%APP_HOME%\lib\spring-context-support-5.3.13.jar;%APP_HOME%\lib\spring-context-5.3.13.jar;%APP_HOME%\lib\spring-aop-5.3.13.jar;%APP_HOME%\lib\spring-expression-5.3.13.jar;%APP_HOME%\lib\spring-tx-5.3.13.jar;%APP_HOME%\lib\spring-oxm-5.3.13.jar;%APP_HOME%\lib\spring-data-commons-2.6.0.jar;%APP_HOME%\lib\spring-beans-5.3.13.jar;%APP_HOME%\lib\spring-core-5.3.13.jar;%APP_HOME%\lib\spring-jcl-5.3.13.jar;%APP_HOME%\lib\lettuce-core-6.1.5.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-logging-2.6.1.jar;%APP_HOME%\lib\logback-classic-1.2.7.jar;%APP_HOME%\lib\log4j-to-slf4j-2.14.1.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.32.jar;%APP_HOME%\lib\slf4j-api-1.7.32.jar;%APP_HOME%\lib\commons-pool2-2.11.1.jar;%APP_HOME%\lib\netty-resolver-dns-4.1.70.Final.jar;%APP_HOME%\lib\netty-handler-4.1.70.Final.jar;%APP_HOME%\lib\netty-codec-dns-4.1.70.Final.jar;%APP_HOME%\lib\netty-codec-4.1.70.Final.jar;%APP_HOME%\lib\netty-transport-4.1.70.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.70.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.70.Final.jar;%APP_HOME%\lib\netty-common-4.1.70.Final.jar;%APP_HOME%\lib\cache-api-1.1.1.jar;%APP_HOME%\lib\reactor-core-3.4.12.jar;%APP_HOME%\lib\rxjava-3.0.12.jar;%APP_HOME%\lib\jboss-marshalling-river-2.0.11.Final.jar;%APP_HOME%\lib\jackson-datatype-jdk8-2.13.0.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.13.0.jar;%APP_HOME%\lib\jackson-module-parameter-names-2.13.0.jar;%APP_HOME%\lib\jackson-databind-2.13.0.jar;%APP_HOME%\lib\jackson-core-2.13.0.jar;%APP_HOME%\lib\jackson-annotations-2.13.0.jar;%APP_HOME%\lib\jackson-dataformat-yaml-2.13.0.jar;%APP_HOME%\lib\snakeyaml-1.29.jar;%APP_HOME%\lib\byte-buddy-1.11.22.jar;%APP_HOME%\lib\jodd-bean-5.1.6.jar;%APP_HOME%\lib\javax.activation-api-1.2.0.jar;%APP_HOME%\lib\jakarta.annotation-api-1.3.5.jar;%APP_HOME%\lib\tomcat-embed-websocket-9.0.55.jar;%APP_HOME%\lib\tomcat-embed-core-9.0.55.jar;%APP_HOME%\lib\tomcat-embed-el-9.0.55.jar;%APP_HOME%\lib\reactive-streams-1.0.3.jar;%APP_HOME%\lib\jboss-marshalling-2.0.11.Final.jar;%APP_HOME%\lib\jodd-core-5.1.6.jar;%APP_HOME%\lib\logback-core-1.2.7.jar;%APP_HOME%\lib\log4j-api-2.14.1.jar


@rem Execute app
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %APP_OPTS%  -classpath "%CLASSPATH%" heartsapp.MainApplication %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable APP_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%APP_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
