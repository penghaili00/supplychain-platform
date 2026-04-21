<#
.SYNOPSIS
    SupplyChain项目本地环境引导脚本，启动Docker基础设施服务并导入Nacos配置

.PARAMETER ComposeFile
    Docker Compose配置文件路径，默认为"docker-compose.yml"

.PARAMETER ServerAddr
    Nacos服务器地址，优先使用环境变量SUPPLYCHAIN_NACOS_ADDR，默认为"127.0.0.1:8848"

.PARAMETER Username
    Nacos用户名，优先使用环境变量SUPPLYCHAIN_NACOS_USERNAME，默认为"nacos"

.PARAMETER Password
    Nacos密码，优先使用环境变量SUPPLYCHAIN_NACOS_PASSWORD，默认为"nacos"

.PARAMETER NamespaceId
    Nacos命名空间ID，优先使用环境变量SUPPLYCHAIN_NACOS_NAMESPACE，默认为"supplychain_local"

.PARAMETER NamespaceName
    Nacos命名空间名称，优先使用环境变量SUPPLYCHAIN_NACOS_NAMESPACE_NAME，默认为"supplychain_local"

.PARAMETER Group
    Nacos配置分组，优先使用环境变量SUPPLYCHAIN_NACOS_GROUP，默认为"DEFAULT_GROUP"

.PARAMETER SkipComposeUp
    跳过执行docker compose up命令的开关

.PARAMETER SkipNacosImport
    跳过导入Nacos配置的开关
#>
[CmdletBinding()]
param(
    [string]$ComposeFile = "docker-compose.yml",
    [string]$ServerAddr = $(if ($env:SUPPLYCHAIN_NACOS_ADDR) { $env:SUPPLYCHAIN_NACOS_ADDR } else { "127.0.0.1:8848" }),
    [string]$Username = $(if ($env:SUPPLYCHAIN_NACOS_USERNAME) { $env:SUPPLYCHAIN_NACOS_USERNAME } else { "nacos" }),
    [string]$Password = $(if ($env:SUPPLYCHAIN_NACOS_PASSWORD) { $env:SUPPLYCHAIN_NACOS_PASSWORD } else { "nacos" }),
    [string]$NamespaceId = $(if ($env:SUPPLYCHAIN_NACOS_NAMESPACE) { $env:SUPPLYCHAIN_NACOS_NAMESPACE } else { "supplychain_local" }),
    [string]$NamespaceName = $(if ($env:SUPPLYCHAIN_NACOS_NAMESPACE_NAME) { $env:SUPPLYCHAIN_NACOS_NAMESPACE_NAME } elseif ($env:SUPPLYCHAIN_NACOS_NAMESPACE) { $env:SUPPLYCHAIN_NACOS_NAMESPACE } else { "supplychain_local" }),
    [string]$Group = $(if ($env:SUPPLYCHAIN_NACOS_GROUP) { $env:SUPPLYCHAIN_NACOS_GROUP } else { "DEFAULT_GROUP" }),
    [switch]$SkipComposeUp,
    [switch]$SkipNacosImport
)

$ErrorActionPreference = "Stop"

<#
.SYNOPSIS
    等待TCP端口可用

.DESCRIPTION
    通过TCP连接测试等待指定主机的端口变为可用状态，超时则抛出异常

.PARAMETER Host
    目标主机地址

.PARAMETER Port
    目标端口号

.PARAMETER TimeoutSeconds
    超时时间（秒），默认180秒

.OUTPUTS
    无返回值，端口可用时返回，超时则抛出异常
#>
function Wait-TcpPort {
    param(
        [string]$Host,
        [int]$Port,
        [int]$TimeoutSeconds = 180
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        $client = New-Object System.Net.Sockets.TcpClient
        try {
            $async = $client.BeginConnect($Host, $Port, $null, $null)
            if ($async.AsyncWaitHandle.WaitOne(2000) -and $client.Connected) {
                $client.EndConnect($async)
                return
            }
        } catch {
        } finally {
            $client.Dispose()
        }

        Start-Sleep -Seconds 3
    }

    throw "Timed out waiting for ${Host}:$Port"
}

<#
.SYNOPSIS
    等待HTTP服务就绪

.DESCRIPTION
    通过HTTP请求测试等待指定的URI变为可访问状态，超时则抛出异常

.PARAMETER Uri
    目标HTTP/HTTPS URI地址

.PARAMETER TimeoutSeconds
    超时时间（秒），默认180秒

.OUTPUTS
    无返回值，服务就绪时返回，超时则抛出异常
#>
function Wait-HttpReady {
    param(
        [string]$Uri,
        [int]$TimeoutSeconds = 180
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            Invoke-WebRequest -Uri $Uri -UseBasicParsing -TimeoutSec 5 | Out-Null
            return
        } catch {
            Start-Sleep -Seconds 3
        }
    }

    throw "Timed out waiting for $Uri"
}

<#
.SYNOPSIS
    获取标准化的基础URL

.DESCRIPTION
    将地址转换为标准格式，确保包含http://或https://协议前缀，并去除尾部斜杠

.PARAMETER Address
    原始地址字符串

.OUTPUTS
    [string] 格式化后的基础URL
#>
function Get-BaseUrl {
    param([string]$Address)

    if ($Address.StartsWith("http://") -or $Address.StartsWith("https://")) {
        return $Address.TrimEnd("/")
    }

    return "http://$($Address.TrimEnd('/'))"
}

# 解析项目根目录路径（脚本所在目录的上两级）
$root = (Resolve-Path (Join-Path $PSScriptRoot "..\..")).Path
$composePath = Join-Path $root $ComposeFile

# 检查Docker是否已安装并添加到PATH
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw "Docker was not found in PATH."
}

Push-Location $root
try {
    # 启动Docker容器服务（MySQL、Redis、Nacos、RabbitMQ）
    if (-not $SkipComposeUp) {
        docker compose -f $composePath up -d mysql redis nacos rabbitmq
    }

    # 等待各服务的TCP端口就绪
    Wait-TcpPort -Host "127.0.0.1" -Port ([int]$(if ($env:SUPPLYCHAIN_MYSQL_PORT) { $env:SUPPLYCHAIN_MYSQL_PORT } else { 3306 }))
    Wait-TcpPort -Host "127.0.0.1" -Port ([int]$(if ($env:SUPPLYCHAIN_REDIS_PORT) { $env:SUPPLYCHAIN_REDIS_PORT } else { 6379 }))
    Wait-TcpPort -Host "127.0.0.1" -Port ([int]$(if ($env:SUPPLYCHAIN_RABBITMQ_AMQP_PORT) { $env:SUPPLYCHAIN_RABBITMQ_AMQP_PORT } else { 5672 }))

    # 等待Nacos HTTP服务就绪
    $baseUrl = Get-BaseUrl -Address $ServerAddr
    Wait-HttpReady -Uri "$baseUrl/nacos/"

    # 导入Nacos配置（除非被跳过）
    if (-not $SkipNacosImport) {
        & (Join-Path $PSScriptRoot "import-nacos.ps1") `
            -ServerAddr $ServerAddr `
            -Username $Username `
            -Password $Password `
            -NamespaceId $NamespaceId `
            -NamespaceName $NamespaceName `
            -Group $Group
    }

    # 输出环境就绪信息和后续操作指引
    Write-Host ""
    Write-Host "Local infrastructure is ready."
    Write-Host "Next:"
    Write-Host "1. Copy .env.example to .env if you want to customize ports or secrets."
    Write-Host "2. Export the same SUPPLYCHAIN_* variables in your shell before starting services."
    Write-Host "3. Start supplychain-service-provider first so Flyway can initialize the schema."
} finally {
    Pop-Location
}
