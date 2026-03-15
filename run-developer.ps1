# Run RuneLite in developer mode with Green Screen plugin.
# Sets JAVA_HOME if missing so Gradle can run without system env config.

$ErrorActionPreference = 'Stop'
$ProjectRoot = $PSScriptRoot

# Set JAVA_HOME if not already set
if (-not $env:JAVA_HOME) {
    $candidates = @(
        "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot",
        "C:\Program Files\Java\jdk-17",
        (Get-ChildItem "C:\Program Files\Eclipse Adoptium\jdk-*-hotspot" -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1 -ExpandProperty FullName),
        (Get-ChildItem "C:\Program Files\Java\jdk-*" -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1 -ExpandProperty FullName)
    )
    foreach ($jdk in $candidates) {
        if ($jdk -and (Test-Path (Join-Path $jdk "bin\java.exe"))) {
            $env:JAVA_HOME = $jdk
            Write-Host "Using JAVA_HOME: $env:JAVA_HOME"
            break
        }
    }
    if (-not $env:JAVA_HOME) {
        Write-Error "Java not found. Install JDK 11+ (e.g. winget install EclipseAdoptium.Temurin.17.JDK) or set JAVA_HOME."
        exit 1
    }
}

Set-Location $ProjectRoot
& "$ProjectRoot\gradlew.bat" run
