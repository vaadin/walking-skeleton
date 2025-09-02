$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$VaadinProKey = ""

# Cross-platform home directory detection
if ($IsWindows -or $env:OS -eq "Windows_NT")
{
    $HomeDir = $env:USERPROFILE
}
else
{
    $HomeDir = $env:HOME
}

$ProKeyPath = Join-Path $HomeDir ".vaadin" "proKey"

# Function to write colored output to stderr
function Write-ErrorMessage
{
    param([string]$Message)
    Write-Host $Message -ForegroundColor Red
}

function Write-WarningMessage
{
    param([string]$Message)
    Write-Host $Message -ForegroundColor Yellow
}

function Write-InfoMessage
{
    param([string]$Message)
    Write-Host $Message -ForegroundColor Cyan
}

function Write-SuccessMessage
{
    param([string]$Message)
    Write-Host $Message -ForegroundColor Green
}

# Read Vaadin Pro key if available
if (Test-Path $ProKeyPath)
{
    try
    {
        # Read the JSON file and extract the proKey value
        $JsonContent = Get-Content $ProKeyPath -Raw
        $JsonObject = $JsonContent | ConvertFrom-Json
        $VaadinProKey = $JsonObject.proKey

        if ($VaadinProKey)
        {
            Write-SuccessMessage "✅ Found Vaadin Pro key"
        }
        else
        {
            Write-WarningMessage  "⚠️ Could not parse Vaadin Pro key from ~/.vaadin/proKey"
        }
    }
    catch
    {
        Write-WarningMessage "⚠️ Error reading Vaadin Pro key file: $( $_.Exception.Message )"
        $VaadinProKey = ""
    }
}
else
{
    Write-InfoMessage "ℹ️ No Vaadin Pro key found at $ProKeyPath (continuing without it)"
}

# Validate Docker is available
try
{
    $null = Get-Command docker -ErrorAction Stop
    # Test if Docker daemon is running
    try
    {
        docker version --format '{{.Server.Version}}' | Out-Null
    }
    catch
    {
        Write-ErrorMessage "❌ Error: Docker daemon is not running"
        exit 1
    }
}
catch
{
    Write-ErrorMessage "❌ Error: Docker is not installed or not in PATH"
    exit 1
}

# Build the Docker image
Write-Host "🔨 Building Docker image..." -ForegroundColor Blue

try
{
    if ($VaadinProKey)
    {
        $Process = Start-Process docker -ArgumentList @("build", "--build-arg", "VAADIN_PRO_KEY=$VaadinProKey", ".") -Wait -PassThru -NoNewWindow
    }
    else
    {
        $Process = Start-Process docker -ArgumentList @("build", ".") -Wait -PassThru -NoNewWindow
    }

    if ($Process.ExitCode -ne 0)
    {
        if ($VaadinProKey)
        {
            Write-ErrorMessage "❌ Docker build failed with Vaadin Pro key"
        }
        else
        {
            Write-ErrorMessage "❌ Docker build failed"
        }
        exit 1
    }

    Write-SuccessMessage "✅ Docker build completed successfully"
}
catch
{
    Write-ErrorMessage "❌ Error executing Docker build: $( $_.Exception.Message )"
    exit 1
}