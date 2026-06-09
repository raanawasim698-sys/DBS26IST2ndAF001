package ui.util;

import java.awt.*;

/**
 * Central theme constants for the HMS application.
 * Change values here to restyle the entire app at once.
 */
public class Theme {

    // ── Brand colours ────────────────────────────────────────────────────────
    public static final Color PRIMARY        = new Color(0x1A, 0x56, 0xDB); // blue
    public static final Color PRIMARY_DARK   = new Color(0x10, 0x3A, 0xA3);
    public static final Color PRIMARY_LIGHT  = new Color(0xE8, 0xF0, 0xFE);
    public static final Color ACCENT         = new Color(0x05, 0x96, 0x69); // green
    public static final Color SUCCESS        = new Color(0x05, 0x96, 0x69); // alias for ACCENT
    public static final Color DANGER         = new Color(0xDC, 0x26, 0x26);
    public static final Color WARNING        = new Color(0xD9, 0x77, 0x06);

    // ── Neutrals ─────────────────────────────────────────────────────────────
    public static final Color BG_MAIN        = new Color(0xF3, 0xF4, 0xF6);
    public static final Color BG_CARD        = Color.WHITE;
    public static final Color BG_SIDEBAR     = new Color(0x1E, 0x29, 0x3B);
    public static final Color BG_SIDEBAR_HOV = new Color(0x2D, 0x3E, 0x55);
    public static final Color BG_SIDEBAR_SEL = new Color(0x1A, 0x56, 0xDB);

    public static final Color TEXT_PRIMARY   = new Color(0x11, 0x18, 0x27);
    public static final Color TEXT_SECONDARY = new Color(0x6B, 0x72, 0x80);
    public static final Color TEXT_SIDEBAR   = new Color(0xD1, 0xD5, 0xDB);
    public static final Color TEXT_WHITE     = Color.WHITE;

    public static final Color BORDER         = new Color(0xE5, 0xE7, 0xEB);
    public static final Color BORDER_INPUT   = new Color(0xE5, 0xE7, 0xEB); // alias for BORDER
    public static final Color BORDER_FOCUS   = new Color(0x1A, 0x56, 0xDB);

    // ── Table ─────────────────────────────────────────────────────────────────
    public static final Color TABLE_HEADER   = new Color(0xF9, 0xFA, 0xFB);
    public static final Color TABLE_ROW_ALT  = new Color(0xF9, 0xFA, 0xFB);
    public static final Color TABLE_SEL      = new Color(0xDB, 0xEA, 0xFF);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE      = new Font("Segoe UI", Font.BOLD,   22);
    public static final Font FONT_SUBTITLE   = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font FONT_LABEL      = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font FONT_LABEL_BOLD = new Font("Segoe UI", Font.BOLD,   13);
    public static final Font FONT_INPUT      = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font FONT_BTN        = new Font("Segoe UI", Font.BOLD,   13);
    public static final Font FONT_SIDEBAR    = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font FONT_SIDEBAR_HD = new Font("Segoe UI", Font.BOLD,   11);
    public static final Font FONT_TABLE_HD   = new Font("Segoe UI", Font.BOLD,   12);
    public static final Font FONT_TABLE_HDR  = new Font("Segoe UI", Font.BOLD,   12); // alias for FONT_TABLE_HD
    public static final Font FONT_TABLE      = new Font("Segoe UI", Font.PLAIN,  12);
    public static final Font FONT_STAT_NUM   = new Font("Segoe UI", Font.BOLD,   28);
    public static final Font FONT_STAT_LBL   = new Font("Segoe UI", Font.PLAIN,  12);
    public static final Font FONT_BADGE      = new Font("Segoe UI", Font.BOLD,   10);

    // ── Dimensions ────────────────────────────────────────────────────────────
    public static final int  SIDEBAR_W       = 220;
    public static final int  BTN_H           = 36;
    public static final int  INPUT_H         = 36;
    public static final int  RADIUS          = 8;

    private Theme() {}
}
