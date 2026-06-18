# Maven Wrapper PowerShell Script for Windows
# Automatically executes Maven using the wrapper

param(
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$MavenArgs
)

$DIRNAME = Split-Path -Parent $MyInvocation.MyCommand.Path
$APP_HOME = $DIRNAME

# Check if maven-wrapper.jar exists
if (-not (Test-Path "$APP_HOME\.mvn\wrapper\maven-wrapper.jar")) {
    Write-Error "Error: maven-wrapper.jar not found at $APP_HOME\.mvn\wrapper\maven-wrapper.jar"
    exit 1
}

# Find Java executable
$JAVA_EXE = "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\java.exe"
if (-not (Test-Path $JAVA_EXE)) {
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
        $JAVA_EXE = "$env:JAVA_HOME\bin\java.exe"
    } else {
        $JAVA_EXE = "java.exe"
    }
}

# Execute Maven wrapper
& $JAVA_EXE "-Dmaven.multiModuleProjectDirectory=$APP_HOME" "-cp" "$APP_HOME\.mvn\wrapper\maven-wrapper.jar" "org.apache.maven.wrapper.MavenWrapperMain" $MavenArgs
exit $LASTEXITCODE
