package project2.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class HeatmapStatistics extends JPanel {
    private final JLabel titleLabel = new JLabel("Heatmap Statistics");
    private final JLabel totalLabel = new JLabel();
    private final JLabel greenLabel = new JLabel();
    private final JLabel yellowLabel = new JLabel();
    private final JLabel orangeLabel = new JLabel();
    private final JLabel darkorange = new JLabel();
    private final JLabel redLabel = new JLabel();

    public HeatmapStatistics() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 0),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        setOpaque(true);

        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Font statFont = new Font("SansSerif", Font.BOLD, 12);
        for (JLabel label : new JLabel[]{totalLabel, greenLabel, yellowLabel, orangeLabel, darkorange, redLabel}) {
            label.setFont(statFont);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        greenLabel.setForeground(new Color(0, 200, 0));
        yellowLabel.setForeground(new Color(230, 180, 0));
        orangeLabel.setForeground(new Color(255, 140, 0));
        darkorange.setForeground(new Color(255, 100, 0));
        redLabel.setForeground(new Color(220, 0, 0));
        totalLabel.setForeground(Color.DARK_GRAY);

        add(titleLabel);
        add(Box.createVerticalStrut(8));
        add(greenLabel);
        add(yellowLabel);
        add(orangeLabel);
        add(darkorange);
        add(redLabel);
        add(Box.createVerticalStrut(8));
        add(totalLabel);

        updateStats(Map.of("green", 0, "yellow", 0, "orange", 0, "darkorange", 0, "red", 0));
    }

    public void updateStats(Map<String, Integer> stats) {
        greenLabel.setText("Green stops (0–5 min): " + stats.getOrDefault("green", 0));
        yellowLabel.setText("Yellow stops (6–10 min): " + stats.getOrDefault("yellow", 0));
        orangeLabel.setText("Orange stops (11–20 min): " + stats.getOrDefault("orange", 0));
        darkorange.setText("Dark orange stops (21–30 min): " + stats.getOrDefault("darkorange", 0));
        redLabel.setText("Red stops (31+ min): " + stats.getOrDefault("red", 0));

        int total = stats.values().stream().mapToInt(i -> i).sum();
        totalLabel.setText("Total stops: " + total);
        revalidate();
        repaint();

    }
}
