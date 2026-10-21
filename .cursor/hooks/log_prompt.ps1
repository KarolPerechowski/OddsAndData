# Appends the submitted user prompt into AI.md between prompt-history markers.
# Always allows the prompt to continue (fail open).
$ErrorActionPreference = "Stop"

function Write-Continue {
    [Console]::Out.WriteLine('{"continue":true}')
}

try {
    $raw = [Console]::In.ReadToEnd()
    if ([string]::IsNullOrWhiteSpace($raw)) {
        Write-Continue
        exit 0
    }

    $payload = $raw | ConvertFrom-Json
    $prompt = [string]$payload.prompt
    if ([string]::IsNullOrWhiteSpace($prompt)) {
        Write-Continue
        exit 0
    }

    $repoRoot = if ($PSScriptRoot) {
        (Resolve-Path (Join-Path $PSScriptRoot "..\..")).Path
    } else {
        (Get-Location).Path
    }
    $aiPath = Join-Path $repoRoot "AI.md"
    if (-not (Test-Path -LiteralPath $aiPath)) {
        Write-Continue
        exit 0
    }

    $start = "<!-- prompt-history:start -->"
    $end = "<!-- prompt-history:end -->"
    $content = [System.IO.File]::ReadAllText($aiPath)
    $endIndex = $content.LastIndexOf($end)
    if ($endIndex -lt 0 -or $content.LastIndexOf($start) -lt 0) {
        Write-Continue
        exit 0
    }

    $timestamp = [DateTime]::UtcNow.ToString("yyyy-MM-ddTHH:mm:ssZ")
    $conversationId = [string]$payload.conversation_id
    if ([string]::IsNullOrWhiteSpace($conversationId)) {
        $header = "### $timestamp"
    } else {
        $header = "### $timestamp (conversation $conversationId)"
    }

    $safePrompt = $prompt.Replace("``````", "'''")
    $nl = [Environment]::NewLine
    $fence = "``````"
    $entry = $nl + $header + $nl + $nl + $fence + "text" + $nl + $safePrompt + $nl + $fence + $nl + $nl

    $updated = $content.Insert($endIndex, $entry)
    $utf8NoBom = New-Object System.Text.UTF8Encoding $false
    [System.IO.File]::WriteAllText($aiPath, $updated, $utf8NoBom)
    Write-Continue
    exit 0
} catch {
    Write-Continue
    exit 0
}
