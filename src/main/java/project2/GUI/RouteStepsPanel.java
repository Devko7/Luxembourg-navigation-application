package project2.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import project2.RouteStep;

public class RouteStepsPanel extends JPanel
{
    public RouteStepsPanel(List<RouteStep> routeSteps) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (RouteStep step : routeSteps) {
            add(createStepPanel(step));
            add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }

    private JPanel createStepPanel(RouteStep step)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel iconLabel = new JLabel();
        String mode = step.getMode();
        if ("walk".equalsIgnoreCase(mode)) {
            iconLabel.setIcon(new ImageIcon("src/main/resources/icons/walkingIcon.png"));
        } else if ("ride".equalsIgnoreCase(mode)) {
            iconLabel.setIcon(new ImageIcon("src/main/resources/icons/tramIcon.png"));
        }
        iconLabel.setPreferredSize(new Dimension(60, 60));
        panel.add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(panel.getBackground());

        if ("walk".equalsIgnoreCase(mode))
        {
            textPanel.add(new JLabel("Walk to next point"));
            textPanel.add(new JLabel("Duration: " + step.getDuration() + " mins"));
        } else if ("ride".equalsIgnoreCase(mode)) {
            String routeName = step.getRoute() != null ? step.getRoute().getShortName() : "N/A";
            textPanel.add(new JLabel("Ride: " + routeName));
            textPanel.add(new JLabel("From: " + step.getStartTime() + " — Duration: " + step.getDuration() + " mins"));
            textPanel.add(new JLabel("Get off at: " + step.getStop()));
        }

        panel.add(textPanel, BorderLayout.CENTER);
        return panel;
    }
}