package project2.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SearchBar extends JPanel {

    private final JTextField textField;
    private final JLabel iconLabel;
    private final int cornerRadius = 20;

    public SearchBar(String placeholder, String iconPath, int width, int height) {
        setLayout(null);
        setOpaque(false);
        setBounds(125, 20, width, height);

        textField = new JTextField(placeholder);
        textField.setBounds(0, 0, width, height);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setForeground(Color.GRAY);
        textField.setBackground(new Color(0, 0, 0, 0));
        textField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 35));
        textField.setCaretColor(Color.BLACK);
        textField.setOpaque(false);


        //the "search" text so it will disapear when clicked
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });

        ImageIcon icon = new ImageIcon(iconPath);
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        iconLabel = new JLabel(new ImageIcon(img));
        iconLabel.setBounds(width - 25, (height - 16) / 2, 16, 16);

        add(textField);
        add(iconLabel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.dispose();
        super.paintComponent(g);
    }

    public String getText() {
        String text = textField.getText();
        return text.equals("Search") ? "" : text;
    }

    public JTextField getTextField() {
        return textField;
    }
}
