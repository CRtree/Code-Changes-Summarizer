package com.samuel.zuo.setting;

import javax.swing.*;
import java.awt.*;

public class PlaceholderTextField extends JTextField {
    private String placeholder;

    public PlaceholderTextField() {
    }

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (placeholder == null || placeholder.isEmpty() || !getText().isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getDisabledTextColor());

        // Calculate the vertical position to center the placeholder text
        FontMetrics fontMetrics = g.getFontMetrics();
        int textHeight = fontMetrics.getHeight();
        int textY = (getHeight() - textHeight) / 2 + fontMetrics.getAscent();

        // Add horizontal padding
        int padding = fontMetrics.charWidth(' '); // Width of a space character
        int textX = getInsets().left + padding;

        g2.drawString(placeholder, textX, textY);
        g2.dispose();
    }
}
