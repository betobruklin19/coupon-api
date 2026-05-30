param(
    [string]$JavaHome = $env:JAVA_HOME
)

if (-not $JavaHome -or -not (Test-Path "$JavaHome\bin\keytool.exe")) {
    Write-Error "Defina JAVA_HOME apontando para o JDK 21 antes de executar este script."
    exit 1
}

$keytool = "$JavaHome\bin\keytool.exe"
$cacerts = "$JavaHome\lib\security\cacerts"
$storepass = "changeit"

function Import-CertToJava([System.Security.Cryptography.X509Certificates.X509Certificate2]$cert, [string]$alias) {
    $temp = [System.IO.Path]::GetTempFileName() + ".cer"
    Export-Certificate -Cert $cert -FilePath $temp -Force | Out-Null
    & $keytool -importcert -noprompt -alias $alias -file $temp -keystore $cacerts -storepass $storepass -trustcacerts 2>&1 | Out-Null
    Remove-Item $temp -Force -ErrorAction SilentlyContinue
}

function Get-ServerCertificate([string]$hostName) {
    $tcp = New-Object System.Net.Sockets.TcpClient($hostName, 443)
    $cert = $null
    $ssl = New-Object System.Net.Security.SslStream($tcp.GetStream(), $false, {
        param($sender, $certificate, $chain, $errors)
        $script:cert = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2($certificate)
        return $true
    })
    $ssl.AuthenticateAsClient($hostName)
    $ssl.Close()
    $tcp.Close()
    return $script:cert
}

Write-Host "Importando certificado de repo.maven.apache.org..."
$mavenCert = Get-ServerCertificate "repo.maven.apache.org"
Import-CertToJava $mavenCert "maven-central-$(Get-Date -Format 'yyyyMMdd')"

Write-Host "Importando certificados raiz do Windows (pode levar alguns segundos)..."
$count = 0
Get-ChildItem Cert:\LocalMachine\Root | ForEach-Object {
    $alias = "win-root-$($_.Thumbprint)"
    try {
        Import-CertToJava $_ $alias
        $count++
    } catch { }
}

Write-Host "Concluido. Certificados processados: $count"
Write-Host "Teste com: mvn -version"
