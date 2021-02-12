set JAVA_HOME=C:\tools\graalvm-ce-java11-21.0.0
set PATH=%JAVA_HOME%\bin;%PATH%
cd keyconnect-cli
mvn -DskipTests -P release package
