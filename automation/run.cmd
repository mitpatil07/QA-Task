@echo off
set JAVA_HOME=C:\Users\Mitesh\.vscode\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64
set MVN=C:\Users\Mitesh\.vscode\extensions\oracle.oracle-java-26.0.1\nbcode\java\maven\bin\mvn.cmd

echo Running Maven tests...
"%MVN%" test %*
