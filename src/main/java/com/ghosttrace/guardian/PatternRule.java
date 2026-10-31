package com.ghosttrace.guardian;

import java.util.List;
import java.util.regex.Pattern;

public record PatternRule(
    String ruleId,
    String name,
    String category,
    RiskLevel riskLevel,
    Pattern pattern,
    String reason,
    List<String> sideEffects,
    String saferAlternative
) {
    public static final List<PatternRule> DEFAULT_RULES = List.of(
        // Destructive File Deletion
        new PatternRule(
            "DEL-001",
            "Recursive Force File Deletion",
            "filesystem",
            RiskLevel.CRITICAL,
            Pattern.compile("(?i)\\b(rm\\s+-[a-z]*r[a-z]*f|rm\\s+-[a-z]*f[a-z]*r|del\\s+/[fF]\\s+/[sS]|rmdir\\s+/[sS]\\s+/[qQ])\\b"),
            "Irreversibly deletes files and directories without prompt confirmation.",
            List.of("Permanent file loss", "Filesystem corruption if targeted at system root"),
            "Target specific paths or inspect candidates first using 'ls' or 'dir'."
        ),
        new PatternRule(
            "DEL-002",
            "Root / All Files Deletion Pattern",
            "filesystem",
            RiskLevel.CRITICAL,
            Pattern.compile("(?i)\\brm\\s+-[a-z]*f?[a-z]*\\s+(/|/\\*|~|~\\*|\\*)\\b"),
            "Targets root, user home, or entire current directory for deletion.",
            List.of("Total system or user data wipe"),
            "Never execute wildcard deletion at root or home directory levels."
        ),
        new PatternRule(
            "DEL-003",
            "Shredding / Overwriting",
            "filesystem",
            RiskLevel.HIGH,
            Pattern.compile("(?i)\\b(shred|srm|wipefs)\\b"),
            "Securely shreds or clears filesystem signatures, preventing data recovery.",
            List.of("Permanent unrecoverable block overwrite"),
            "Ensure drive or file targets are precisely specified."
        ),

        // Disk Formatting & Raw Devices
        new PatternRule(
            "DISK-001",
            "Filesystem Formatting",
            "disk",
            RiskLevel.CRITICAL,
            Pattern.compile("(?i)\\b(mkfs|format\\s+[a-zA-Z]:|diskpart)\\b"),
            "Formats block devices, wiping all partitions and file structures.",
            List.of("Complete volume destruction"),
            "Double-check target volume labels and partition UUIDs."
        ),
        new PatternRule(
            "DISK-002",
            "Direct Block Device Write (dd)",
            "disk",
            RiskLevel.CRITICAL,
            Pattern.compile("(?i)\\bdd\\s+.*\\bof=/dev/(sd[a-z]|nvme[0-9]n[0-9]|hd[a-z]|null|zero)\\b"),
            "Writes raw binary blocks directly to storage device partitions.",
            List.of("Bootloader destruction", "Partition table destruction"),
            "Verify input/output targets; prefer partition-level tools with safety checks."
        ),

        // System Control & Power
        new PatternRule(
            "SYS-001",
            "System Shutdown / Reboot",
            "system",
            RiskLevel.HIGH,
            Pattern.compile("(?i)\\b(shutdown|reboot|poweroff|halt|init\\s+0|init\\s+6)\\b"),
            "Terminates running OS session and shuts down or restarts the computer.",
            List.of("Loss of unsaved work in other background applications"),
            "Save all open files and project states before triggering power commands."
        ),
        new PatternRule(
            "SYS-002",
            "Killing Core System Processes",
            "system",
            RiskLevel.CRITICAL,
            Pattern.compile("(?i)\\bkill\\s+-9\\s+1\\b|\\btaskkill\\s+/f\\s+/im\\s+csrss\\.exe\\b"),
            "Attempts to forcibly kill system init (PID 1) or critical operating system services.",
            List.of("Instant kernel panic or blue screen crash"),
            "Only kill user-owned application process IDs."
        ),

        // Remote Execution & Exfiltration
        new PatternRule(
            "NET-001",
            "Piped Remote Script Execution",
            "security",
            RiskLevel.CRITICAL,
            Pattern.compile("(?i)\\b(curl|wget|fetch)\\s+[^|]+\\|\\s*(bash|sh|zsh|python[0-9]?|perl|powershell)\\b"),
            "Downloads arbitrary remote code and immediately executes it with current privileges.",
            List.of("Arbitrary remote code execution", "Potential malware installation"),
            "Download script to disk first, inspect contents thoroughly, then run manually."
        ),

        // Permission Escalation
        new PatternRule(
            "PERM-001",
            "Permissive World-Writable Permissions",
            "security",
            RiskLevel.HIGH,
            Pattern.compile("(?i)\\bchmod\\s+(-R\\s+)?(777|a\\+rwx)\\b"),
            "Grants read, write, and execute permissions to all users on the system.",
            List.of("Security boundary bypass", "Malicious modification vulnerability"),
            "Use least privilege principles (e.g. chmod 755 or 644)."
        ),

        // Version Control Destruction
        new PatternRule(
            "VCS-001",
            "Forced Git Push",
            "version_control",
            RiskLevel.HIGH,
            Pattern.compile("(?i)\\bgit\\s+push\\s+.*(-f|--force|--force-with-lease)\\b"),
            "Force-pushes changes, overwriting remote commit history.",
            List.of("Loss of teammate commits", "Remote history corruption"),
            "Prefer standard git merge or rebase workflows before pushing."
        ),
        new PatternRule(
            "VCS-002",
            "Hard Git Reset",
            "version_control",
            RiskLevel.HIGH,
            Pattern.compile("(?i)\\bgit\\s+reset\\s+--hard\\b"),
            "Discards all uncommitted changes and tracked modifications in working tree.",
            List.of("Permanent loss of uncommitted local code"),
            "Stash changes first using 'git stash' before resetting."
        ),
        new PatternRule(
            "VCS-003",
            "Git Filter-Branch History Rewrite",
            "version_control",
            RiskLevel.HIGH,
            Pattern.compile("(?i)\\bgit\\s+filter-branch\\b"),
            "Permanently rewrites entire repository commit history and SHA hashes.",
            List.of("Total commit history recalculation"),
            "Use git-filter-repo or careful interactive rebasing on isolated branches."
        ),

        // Package Management Risks
        new PatternRule(
            "PKG-001",
            "Package Uninstallation",
            "package_management",
            RiskLevel.HIGH,
            Pattern.compile("(?i)\\b(pip\\s+uninstall|npm\\s+uninstall|pnpm\\s+uninstall|yarn\\s+remove|apt(-get)?\\s+remove|pacman\\s+-R)\\b"),
            "Uninstalls packages or system libraries, potentially breaking dependencies.",
            List.of("Broken application dependencies", "Missing runtime libraries"),
            "Verify reverse dependency trees before removing packages."
        )
    );
}
