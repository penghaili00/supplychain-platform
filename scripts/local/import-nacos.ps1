[CmdletBinding()]
param(
    [string]$ServerAddr = "127.0.0.1:8848",
    [string]$Username = "nacos",
    [string]$Password = "nacos",
    [string]$NamespaceId = "supplychain_local",
    [string]$NamespaceName = "supplychain_local",
    [string]$Group = "DEFAULT_GROUP"
)

$ErrorActionPreference = "Stop"

function Get-BaseUrl {
    param([string]$Address)

    if ($Address.StartsWith("http://") -or $Address.StartsWith("https://")) {
        return $Address.TrimEnd("/")
    }

    return "http://$($Address.TrimEnd('/'))"
}

function Get-AccessToken {
    param(
        [string]$BaseUrl,
        [string]$Username,
        [string]$Password
    )

    try {
        $response = Invoke-RestMethod `
            -Method Post `
            -Uri "$BaseUrl/nacos/v1/auth/users/login" `
            -ContentType "application/x-www-form-urlencoded; charset=UTF-8" `
            -ErrorAction Stop `
            -Body @{
                username = $Username
                password = $Password
            }
    } catch {
        $statusCode = $null
        $responseBody = ""

        if ($_.Exception.Response) {
            $statusCode = [int]$_.Exception.Response.StatusCode
            $stream = $_.Exception.Response.GetResponseStream()
            if ($stream) {
                $reader = New-Object System.IO.StreamReader($stream)
                $responseBody = $reader.ReadToEnd()
                $reader.Dispose()
            }
        }

        if ($responseBody -match "User .* not found" -or $responseBody -match "authorization" -or $statusCode -eq 403) {
            Write-Host "Nacos auth appears to be disabled. Skipping login."
            return $null
        }

        throw
    }

    if (-not $response.accessToken) {
        throw "Nacos login succeeded but no accessToken was returned."
    }

    return $response.accessToken
}

function Test-AnonymousAccess {
    param([string]$BaseUrl)

    try {
        Invoke-RestMethod `
            -Method Get `
            -Uri "$BaseUrl/nacos/v1/console/namespaces" `
            -ErrorAction Stop | Out-Null
        return $true
    } catch {
        return $false
    }
}

function Build-ApiUri {
    param(
        [string]$BaseUrl,
        [string]$Path,
        [string]$AccessToken
    )

    $uri = "$BaseUrl$Path"
    if ([string]::IsNullOrWhiteSpace($AccessToken)) {
        return $uri
    }

    $encodedAccessToken = [System.Uri]::EscapeDataString($AccessToken)

    if ($uri.Contains("?")) {
        return "${uri}&accessToken=$encodedAccessToken"
    }

    return "${uri}?accessToken=$encodedAccessToken"
}

function Ensure-Namespace {
    param(
        [string]$BaseUrl,
        [string]$AccessToken,
        [string]$NamespaceId,
        [string]$NamespaceName
    )

    if ($NamespaceId -eq "public") {
        return
    }

    $response = Invoke-RestMethod `
        -Method Get `
        -Uri (Build-ApiUri -BaseUrl $BaseUrl -Path "/nacos/v1/console/namespaces" -AccessToken $AccessToken)

    $items = @()
    if ($response.data) {
        $items = @($response.data)
    }

    foreach ($item in $items) {
        if ($item.namespace -eq $NamespaceId -or $item.namespaceShowName -eq $NamespaceName) {
            return
        }
    }

    Invoke-RestMethod `
        -Method Post `
        -Uri (Build-ApiUri -BaseUrl $BaseUrl -Path "/nacos/v1/console/namespaces" -AccessToken $AccessToken) `
        -ContentType "application/x-www-form-urlencoded; charset=UTF-8" `
        -Body @{
            customNamespaceId = $NamespaceId
            namespaceName = $NamespaceName
            namespaceDesc = "SupplyChain local bootstrap namespace"
        } | Out-Null
}

$root = (Resolve-Path (Join-Path $PSScriptRoot "..\..")).Path
$configDir = Join-Path $root "nacos"
$baseUrl = Get-BaseUrl -Address $ServerAddr
$accessToken = $null

try {
    $accessToken = Get-AccessToken -BaseUrl $baseUrl -Username $Username -Password $Password
} catch {
    throw "Failed to login to Nacos with the configured username/password. $_"
}

if (-not [string]::IsNullOrWhiteSpace($accessToken)) {
    Write-Host "Nacos login succeeded."
} elseif (Test-AnonymousAccess -BaseUrl $baseUrl) {
    Write-Host "Nacos anonymous access is available. Skipping login."
} else {
    throw "Nacos login did not return an access token, and anonymous access is not available."
}

Ensure-Namespace `
    -BaseUrl $baseUrl `
    -AccessToken $accessToken `
    -NamespaceId $NamespaceId `
    -NamespaceName $NamespaceName

$files = Get-ChildItem -Path $configDir -Filter "*.yml" | Sort-Object Name
if (-not $files) {
    throw "No YAML config files were found under $configDir."
}

foreach ($file in $files) {
    $body = @{
        dataId  = $file.Name
        group   = $Group
        content = Get-Content -Raw -Encoding UTF8 $file.FullName
        type    = "yaml"
    }

    if ($NamespaceId -ne "public") {
        $body.tenant = $NamespaceId
    }

    Invoke-RestMethod `
        -Method Post `
        -Uri (Build-ApiUri -BaseUrl $baseUrl -Path "/nacos/v1/cs/configs" -AccessToken $accessToken) `
        -ContentType "application/x-www-form-urlencoded; charset=UTF-8" `
        -Body $body | Out-Null

    Write-Host "Imported $($file.Name)"
}

Write-Host "Nacos import completed."
