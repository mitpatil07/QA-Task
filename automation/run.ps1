# Helper script to execute Maven tests using VS Code Java environment
$env:JAVA_HOME="C:\Users\Mitesh\.vscode\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64"
$mvn = "C:\Users\Mitesh\.vscode\extensions\oracle.oracle-java-26.0.1\nbcode\java\maven\bin\mvn.cmd"

Write-Host "Running Maven tests using VS Code Java & Maven installation..." -ForegroundColor Green
& $mvn test $args
