package project2.GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class JourneyPlannerPanel extends JPanel {
    private final Image backgroundImage = new ImageIcon("src/main/resources/Input_Fields_Box_2.png")
            .getImage();
    private final JTextField from;
    private final JTextField to;
    private final JTextField startTime;
    private final PlanJourneyButton planJourneyButton;
    private final int fixedWidth = 370;
    private final int fixedHeight = 302;

    public JourneyPlannerPanel(JourneyDisplay display) {
        JourneyDisplay journeyDisplay = display;
        
        setLayout(null);
        setOpaque(false);

        // Textfield from
        from = new JTextField();
        from.setBounds(45, -5, fixedWidth - 98, fixedHeight - 134);
        styleTextField(from);
        setupPlaceholder(from, "From:");
        add(from);

        // Textfield to
        to = new JTextField();
        to.setBounds(45, 55, fixedWidth - 98, fixedHeight - 134);
        setupPlaceholder(to, "To:");
        styleTextField(to);
        add(to);

        startTime = new JTextField();
        startTime.setBounds(45, 155, fixedWidth - 98, fixedHeight - 134);
        setupPlaceholder(startTime, "Start Time:");
        styleTextField(startTime);
        add(startTime);

        planJourneyButton = new PlanJourneyButton(this, journeyDisplay);
        planJourneyButton.setBounds(70, 294, 220, 64);
        add(planJourneyButton);
    }

    private void styleTextField(JTextField field) {
        field.setOpaque(false);
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setFont(new Font("SansSerif", Font.PLAIN, 22));
    }

    public PlanJourneyButton getPlanButton() {
        return planJourneyButton;
    }

    public String getFromText() {
        return from.getText().equals("From") ? "" : from.getText();
    }

    public String getToText() {
        return to.getText().equals("To") ? "" : to.getText();
    }

    public String getStartTime() {
        return startTime.getText().equals("Start Time") ? "" : startTime.getText();
    }
    
    public JTextField getFromField() {
        return from;
    }
    public JTextField getToField() {
        return to;
    }

    public JTextField getStartTimeField() {
        return startTime;
    }
    
    private void setupPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, fixedWidth, fixedHeight, this);
    }
}
