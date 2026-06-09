package ui.util;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Factory methods for consistently styled widgets.
 * All forms should build their components through this class.
 */
public class UIFactory {

    private UIFactory() {}

    // ── Buttons ───────────────────────────────────────────────────────────────

    /** Primary filled blue button */
    public static JButton primaryButton(String text) {
        return styledButton(text, Theme.PRIMARY, Theme.TEXT_WHITE, Theme.PRIMARY_DARK);
    }

    /** Danger/delete red button */
    public static JButton dangerButton(String text) {
        return styledButton(text, Theme.DANGER, Theme.TEXT_WHITE, new Color(0xB9, 0x1C, 0x1C));
    }

    /** Secondary outlined button */
    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed())
                    g2.setColor(Theme.PRIMARY_LIGHT);
                else if (getModel().isRollover())
                    g2.setColor(new Color(0xEF, 0xF6, 0xFF));
                else
                    g2.setColor(Theme.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.RADIUS, Theme.RADIUS));
                g2.setColor(Theme.PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, Theme.RADIUS, Theme.RADIUS));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(Theme.FONT_BTN);
        b.setForeground(Theme.PRIMARY);
        b.setPreferredSize(new Dimension(b.getPreferredSize().width + 20, Theme.BTN_H));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static JButton styledButton(String text, Color bg, Color fg, Color hover) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? hover :
                             getModel().isRollover() ? hover : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.RADIUS, Theme.RADIUS));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(Theme.FONT_BTN);
        b.setForeground(fg);
        b.setPreferredSize(new Dimension(b.getPreferredSize().width + 20, Theme.BTN_H));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Text inputs ───────────────────────────────────────────────────────────

    /** Standard styled text field */
    public static JTextField textField(int columns) {
        JTextField f = new JTextField(columns);
        styleInput(f);
        return f;
    }

    /** Password field */
    public static JPasswordField passwordField(int columns) {
        JPasswordField f = new JPasswordField(columns);
        styleInput(f);
        return f;
    }

    /** Text area inside a scroll pane */
    public static JScrollPane textArea(JTextArea ta) {
        ta.setFont(Theme.FONT_INPUT);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(new LineBorder(Theme.BORDER, 1, true));
        sp.setPreferredSize(new Dimension(sp.getPreferredSize().width, 80));
        return sp;
    }

    /**
     * Convenience overload: creates a JTextArea with the given rows/columns,
     * wraps it in a styled scroll pane, and returns the JTextArea directly
     * (so callers can append it to a form and keep a reference for getText()).
     */
    public static JTextArea textArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(Theme.FONT_INPUT);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(6, 8, 6, 8));
        return ta;
    }

    /** Varargs convenience overload so callers can write comboBox("A","B","C") */
    public static JComboBox<String> comboBox(String... items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(Theme.FONT_INPUT);
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, Theme.INPUT_H));
        c.setBorder(new LineBorder(Theme.BORDER, 1, true));
        c.setBackground(Theme.BG_CARD);
        return c;
    }

    private static void styleInput(JTextField f) {
        f.setFont(Theme.FONT_INPUT);
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, Theme.INPUT_H));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER, 1, true),
            new EmptyBorder(4, 8, 4, 8)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Theme.BORDER_FOCUS, 2, true),
                    new EmptyBorder(3, 7, 3, 7)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Theme.BORDER, 1, true),
                    new EmptyBorder(4, 8, 4, 8)));
            }
        });
    }

    // ── Combos & spinners ─────────────────────────────────────────────────────

    public static JSpinner spinner(int min, int max, int value) {
        JSpinner s = new JSpinner(new SpinnerNumberModel(value, min, max, 1));
        s.setFont(Theme.FONT_INPUT);
        s.setPreferredSize(new Dimension(80, Theme.INPUT_H));
        ((JSpinner.DefaultEditor) s.getEditor()).getTextField()
            .setBorder(new EmptyBorder(4, 6, 4, 6));
        return s;
    }

    // ── Labels ────────────────────────────────────────────────────────────────

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.TEXT_PRIMARY);
        return l;
    }

    public static JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_LABEL_BOLD);
        l.setForeground(Theme.TEXT_PRIMARY);
        return l;
    }

    public static JLabel sectionHeader(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Theme.TEXT_SECONDARY);
        l.setBorder(new EmptyBorder(12, 0, 4, 0));
        return l;
    }

    /** Inline section sub-label (e.g. "Remarks", "Notes" above a text area) */
    public static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_LABEL_BOLD);
        l.setForeground(Theme.TEXT_PRIMARY);
        l.setBorder(new EmptyBorder(8, 0, 3, 0));
        return l;
    }

    // ── Cards ─────────────────────────────────────────────────────────────────

    /** White rounded card panel */
    public static JPanel card() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 12, 12));
                g2.setColor(Theme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 12, 12));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        return p;
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    /** Apply standard styling to any JTable */
    public static void styleTable(JTable table) {
        table.setFont(Theme.FONT_TABLE);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(Theme.TABLE_SEL);
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.setBackground(Theme.BG_CARD);
        table.setFillsViewportHeight(true);

        JTableHeader hdr = table.getTableHeader();
        hdr.setFont(Theme.FONT_TABLE_HD);
        hdr.setBackground(Theme.TABLE_HEADER);
        hdr.setForeground(Theme.TEXT_PRIMARY);
        hdr.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));
        hdr.setPreferredSize(new Dimension(hdr.getWidth(), 40));
        hdr.setReorderingAllowed(false);

        // alternating rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? Theme.BG_CARD : Theme.TABLE_ROW_ALT);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return comp;
            }
        });
    }

    /** Wrap a table in a scroll pane with card border */
    public static JScrollPane tableScrollPane(JTable table) {
        styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(Theme.BORDER, 1, true));
        sp.getViewport().setBackground(Theme.BG_CARD);
        return sp;
    }

    // ── Form layout helper ────────────────────────────────────────────────────

    /**
     * Build a two-column form grid.
     * Each entry in pairs is: label string, then the component.
     * Pairs are laid out left-to-right, top-to-bottom in a GridBagLayout.
     */
    public static JPanel formGrid(Object... pairs) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(5, 0, 5, 12);
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(5, 0, 5, 0);

        int col = 0, row = 0;
        for (int i = 0; i < pairs.length; i += 2) {
            String labelText = (String) pairs[i];
            Component comp   = (Component) pairs[i + 1];

            lc.gridx = col * 2;      lc.gridy = row;
            fc.gridx = col * 2 + 1;  fc.gridy = row;

            p.add(boldLabel(labelText), lc);
            p.add(comp, fc);

            col++;
            if (col == 2) { col = 0; row++; }
        }
        // flush odd final field to full width
        if (col == 1) {
            fc.gridx = 1; fc.gridy = row;
            fc.gridwidth = 3;
            // already added above — nothing extra needed
        }
        return p;
    }

    // ── Stat card (dashboard) ─────────────────────────────────────────────────

    public static JPanel statCard(String number, String labelText, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 12, 12));
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(0, getHeight()-3, getWidth(), getHeight()-3);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(0, 4));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel num = new JLabel(number);
        num.setFont(Theme.FONT_STAT_NUM);
        num.setForeground(accent);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(Theme.FONT_STAT_LBL);
        lbl.setForeground(Theme.TEXT_SECONDARY);

        card.add(num, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);
        return card;
    }

    // ── Badge ─────────────────────────────────────────────────────────────────

    public static JLabel badge(String text, Color bg) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(Theme.FONT_BADGE);
        l.setForeground(Color.WHITE);
        l.setBorder(new EmptyBorder(3, 8, 3, 8));
        l.setOpaque(false);
        return l;
    }
}
