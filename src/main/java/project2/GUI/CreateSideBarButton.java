package project2.GUI;

import javax.swing.*;
import java.awt.*;

public class CreateSideBarButton
{
    public JPanel createSidebarButton(String iconPath, String labelText, int x, int y)
    {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setBounds(x, y, 80, 80);
        container.setOpaque(false);

        // adding the icon for the button
        ImageIcon icon = new ImageIcon(iconPath);
        Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(img));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        // the label for the button
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(Color.DARK_GRAY);

        // positioning
        container.add(button, BorderLayout.CENTER);
        container.add(label, BorderLayout.SOUTH);

        return container;
    }

}
