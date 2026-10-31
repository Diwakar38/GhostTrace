package com.ghosttrace.terminal;

public class TerminalConsole {

    // ANSI Colors
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String ITALIC = "\u001B[3m";

    // Phosphor CRT Green (#00FF66)
    public static final String GREEN = "\u001B[38;2;0;255;102m";
    public static final String DARK_GREEN = "\u001B[38;2;0;100;40m";
    public static final String MINT = "\u001B[38;2;120;255;180m";

    // Amber CRT Glow (#FFAA00)
    public static final String AMBER = "\u001B[38;2;255;170;0m";

    // Spectral Cyan & Purple
    public static final String CYAN = "\u001B[38;2;0;220;255m";
    public static final String PURPLE = "\u001B[38;2;180;100;255m";

    // Warning Red
    public static final String RED = "\u001B[38;2;255;85;85m";
    public static final String BRIGHT_RED = "\u001B[38;2;255;0;50m";

    // Dim Gray
    public static final String GRAY = "\u001B[38;2;120;120;120m";

    public static String green(String text) { return GREEN + text + RESET; }
    public static String darkGreen(String text) { return DARK_GREEN + text + RESET; }
    public static String amber(String text) { return AMBER + text + RESET; }
    public static String cyan(String text) { return CYAN + text + RESET; }
    public static String red(String text) { return RED + text + RESET; }
    public static String gray(String text) { return GRAY + text + RESET; }
    public static String bold(String text) { return BOLD + text + RESET; }

    public static void printHeader() {
        System.out.println(GREEN + """
            ╔═════════════════════════════════════════════════════════════════════════════╗
            ║  ██████╗ ██╗  ██╗ ██████╗ ███████╗████████╗████████╗██████╗  █████╗  ██████╗███████╗ ║
            ║ ██╔════╝ ██║  ██║██╔═══██╗██╔════╝╚══██╔══╝╚══██╔══╝██╔══██╗██╔══██╗██╔════╝██╔════╝ ║
            ║ ██║  ███╗███████║██║   ██║███████╗   ██║      ██║   ██████╔╝███████║██║     █████╗   ║
            ║ ██║   ██║██╔══██║██║   ██║╚════██║   ██║      ██║   ██╔══██╗██╔══██║██║     ██╔══╝   ║
            ║ ╚██████╔╝██║  ██║╚██████╔╝███████║   ██║      ██║   ██║  ██║██║  ██║╚██████╗███████╗ ║
            ║  ╚═════╝ ╚═╝  ╚═╝ ╚═════╝ ╚══════╝   ╚═╝      ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝╚══════╝ ║
            ║                                                                             ║
            ║  [+] THE AI THAT HAUNTS YOUR TERMINAL  ::  JAVA EDITION v1.2.0             ║
            ║  [+] Offline-First  ::  Autonomous Safety Guardian  ::  Haunted Memory       ║
            ╚═════════════════════════════════════════════════════════════════════════════╝
            """ + RESET);
    }

    public static void printBox(String title, String content, String borderColor, String titleColor) {
        String[] lines = content.split("\r?\n");
        int maxLen = title.length() + 4;
        for (String l : lines) {
            maxLen = Math.max(maxLen, stripAnsi(l).length());
        }
        maxLen = Math.min(maxLen, 86);

        StringBuilder sb = new StringBuilder();
        sb.append(borderColor).append("┌─ ").append(titleColor).append(title).append(borderColor).append(" ");
        int padTop = maxLen - title.length() - 3;
        for (int i = 0; i < Math.max(0, padTop); i++) sb.append("─");
        sb.append("┐\n").append(RESET);

        for (String line : lines) {
            sb.append(borderColor).append("│ ").append(RESET);
            sb.append(line);
            int pad = maxLen - stripAnsi(line).length();
            for (int i = 0; i < Math.max(0, pad); i++) sb.append(" ");
            sb.append(borderColor).append(" │\n").append(RESET);
        }

        sb.append(borderColor).append("└");
        for (int i = 0; i < maxLen + 2; i++) sb.append("─");
        sb.append("┘\n").append(RESET);

        System.out.print(sb);
    }

    public static String stripAnsi(String str) {
        if (str == null) return "";
        return str.replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
