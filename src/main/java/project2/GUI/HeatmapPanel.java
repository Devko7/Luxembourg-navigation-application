package project2.GUI;

import javax.swing.JPanel;

public class HeatmapPanel extends JPanel {
    protected JourneyPlannerPanel journeyPlannerPanel;
    private final HeatmapButton heatmapButton;

    public HeatmapPanel(JourneyPlannerPanel journeyPlannerPanel){
        setLayout(null);
        setOpaque(false);
        
        this.journeyPlannerPanel = journeyPlannerPanel;

        heatmapButton = new HeatmapButton(this);
        heatmapButton.setBounds(70, 170, 220, 70);
        add(heatmapButton);
    }
}

