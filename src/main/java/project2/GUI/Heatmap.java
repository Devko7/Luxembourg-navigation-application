package project2.GUI;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import project2.Stop;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import static project2.GUI.MainFrame.mapViewer;

public class Heatmap extends JComponent {
    private final JXMapViewer map;
    private final Map<Stop, Integer> travelTimes;
    private BufferedImage heatmap;
    private int alpha = 60;
    private boolean gradientMode = true;
    private Map<String, List<Stop>> colorGroups;
    private final HeatmapButton heatmapButton;
    private project2.Point origin;

    public Heatmap(JXMapViewer map, Map<Stop, Integer> travelTimes, Map<String, List<Stop>> colorGroups, project2.Point origin, HeatmapButton button) {
        this.heatmapButton = button;
        this.map = map;
        this.travelTimes = travelTimes;
        this.colorGroups = colorGroups;
        this.origin = origin;
        setOpaque(false);
        setFocusable(true);
        requestFocusInWindow();
        setBounds(0, 0, map.getWidth(), map.getHeight());

        //tooltip for stop names
        UIManager.put("ToolTip.background", Color.WHITE);
        UIManager.put("ToolTip.foreground", Color.BLACK);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(Color.DARK_GRAY));
        setToolTipText("");
        ToolTipManager.sharedInstance().registerComponent(this);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                ToolTipManager.sharedInstance().mouseMoved(
                        new MouseEvent(
                                e.getComponent(), e.getID(),
                                e.getWhen(), e.getModifiersEx(),
                                e.getX(), e.getY(),
                                e.getClickCount(), e.isPopupTrigger(),
                                e.getButton()
                        )
                );
            }
        });


        MouseInputAdapter passThroughAdapter = new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //removing a stop for rightclick
                if (SwingUtilities.isRightMouseButton(e)) {
                    Rectangle viewport = map.getViewportBounds();
                    Point clicked = e.getPoint();

                    for (Map.Entry<String, List<Stop>> entry : colorGroups.entrySet()) {
                        for (Stop stop : entry.getValue()) {
                            GeoPosition pos = new GeoPosition(stop.getPoint().getLat(), stop.getPoint().getLon());
                            Point2D pt = map.getTileFactory().geoToPixel(pos, map.getZoom());

                            int x = (int) (pt.getX() - viewport.getX());
                            int y = (int) (pt.getY() - viewport.getY());

                            if (clicked.distance(x, y) < 10) {
                                heatmapButton.removeStop(stop);
                                return;
                            }
                        }
                    }
                } else {
                    //  if not right click let it pan
                    map.dispatchEvent(SwingUtilities.convertMouseEvent(Heatmap.this, e, map));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                map.dispatchEvent(SwingUtilities.convertMouseEvent(Heatmap.this, e, map));
            }

           @Override
           public void mouseDragged(MouseEvent e) {
                 map.dispatchEvent(SwingUtilities.convertMouseEvent(Heatmap.this, e, map));
           }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                map.dispatchEvent(e);
            }

            @Override
            // keep the keyboard focus but allow the coordinates to be displayed when clicked
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
                map.dispatchEvent(SwingUtilities.convertMouseEvent(Heatmap.this, e, map));
            }
        };

        addMouseListener(passThroughAdapter);
        addMouseMotionListener(passThroughAdapter);
        addMouseWheelListener(passThroughAdapter);

        mapViewer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z"), "undoRemove");
        mapViewer.getActionMap().put("undoRemove", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.undoRemove();
            }
        });

        // up down arrow -> the transparency of the circles
        // esc -> remove heatmap
        // T- toggle heatmap modes
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_UP) {
                    alpha = Math.min(255, alpha + 10);
                    redraw();

                } else if (code == KeyEvent.VK_DOWN) {
                    alpha = Math.max(0, alpha - 10);
                    redraw();

                } else if (code == KeyEvent.VK_T) {
                    gradientMode = !gradientMode;
                    if (!gradientMode) {
                        alpha = 225;
                    }
                    redraw();
                } else if (code == KeyEvent.VK_ESCAPE) {
                    Container parent = getParent();
                    if (parent != null) {
                        parent.remove(Heatmap.this);
                        parent.repaint();
                        parent.revalidate();
                        
                    }
                    MainFrame.statsPanel.setVisible(false);
                    MainFrame.statsPanel.getParent().revalidate();
                    MainFrame.statsPanel.getParent().repaint();
                }
            }
        });

        map.addPropertyChangeListener(evt -> {
            if ("zoom".equals(evt.getPropertyName()) || "centerPosition".equals(evt.getPropertyName())) {
                redraw();
            }
        });
        redraw();
    }

    //draws a point or gradient map depending on the mode being toggled
    private void redraw() {
        int width = map.getWidth();
        int height = map.getHeight();
        heatmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = heatmap.createGraphics();
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle viewport = map.getViewportBounds();

        if (gradientMode) {
            generateGradientHeatmap(g2d, viewport);
        } else {
            generatePointHeatmap(g2d, viewport);
            alpha = 225;
        }
        g2d.dispose();
        repaint();
    }

    private void generateGradientHeatmap(Graphics2D g2d, Rectangle viewport) {
        for (Map.Entry<Stop, Integer> entry : travelTimes.entrySet()) {
            Stop stop = entry.getKey();
            int time = entry.getValue();

            Color color = getColorForTime(time, origin, stop);
            if (color == null) continue;

            GeoPosition pos = new GeoPosition(stop.getPoint().getLat(), stop.getPoint().getLon());
            Point2D pt = map.getTileFactory().geoToPixel(pos, map.getZoom());

            int x = (int) (pt.getX() - viewport.getX());
            int y = (int) (pt.getY() - viewport.getY());

            int radius = getRadiusForTime(time);
            drawPoint(g2d, x, y, radius, color);
        }
    }

    private void generatePointHeatmap(Graphics2D g2d, Rectangle viewport) {
        for (Map.Entry<String, List<Stop>> entry : colorGroups.entrySet()) {
            Color color;
            switch (entry.getKey()) {
                case "green" -> color = new Color(0, 255, 0, alpha);
                case "yellow" -> color = new Color(255, 255, 0, alpha);
                case "orange" -> color = new Color(255, 165, 0, alpha);
                case "darkorange" -> color = new Color(255, 100, 0, alpha);
                case "red" -> color = new Color(255, 0, 0, alpha);
                default -> color = new Color(128, 128, 128, alpha);
            }

            for (Stop stop : entry.getValue()) {
                GeoPosition pos = new GeoPosition(stop.getPoint().getLat(), stop.getPoint().getLon());
                Point2D pt = map.getTileFactory().geoToPixel(pos, map.getZoom());
                int x = (int) (pt.getX() - viewport.getX());
                int y = (int) (pt.getY() - viewport.getY());
                g2d.setColor(color);
                g2d.fillOval(x - 4, y - 4, 10, 10);
            }
        }
    }

    //this is for the gradient mode, it draws a radial gradient for the circles
    private void drawPoint(Graphics2D g2, int cx, int cy, int radius, Color color) {
        //gradeient (o is center 1 is edge)
        float[] dist = {0f, 1f};
        Color[] colors = {
                //the center will be the full color
                new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()),
                //the edge will be transparent
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
        };
        RadialGradientPaint paint = new RadialGradientPaint(new Point(cx, cy), radius, dist, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        g2.setPaint(paint);
        g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }

    private int getRadiusForTime(int time) {
        if (time <= 5) return 60;
        if (time <= 10) return 55;
        if (time <= 20) return 55;
        if (time <= 30) return 55;
        return 50;
    }

    private Color getColorForTime(int time, project2.Point origin, Stop stop) {
        if (time <= 5) {
            double dx = origin.getLat() - stop.getPoint().getLat();
            double dy = origin.getLon() - stop.getPoint().getLon();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0.03) {
                return new Color(255, 0, 0, alpha);
            }
            return new Color(0, 255, 0, alpha);
        }
        if (time <= 10) return new Color(255, 255, 0, alpha);
        if (time <= 20) return new Color(255, 165, 0, alpha);
        if (time <= 30) return new Color(255, 100, 0, alpha);
        if (time <= 120) return new Color(255, 0, 0, alpha);
        return null;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        if (!gradientMode) {
            Rectangle viewport = map.getViewportBounds();
            for (Map.Entry<String, List<Stop>> entry : colorGroups.entrySet()) {
                for (Stop stop : entry.getValue()) {
                    GeoPosition pos = new GeoPosition(stop.getPoint().getLat(), stop.getPoint().getLon());
                    Point2D pt = map.getTileFactory().geoToPixel(pos, map.getZoom());
                    int x = (int) (pt.getX() - viewport.getX());
                    int y = (int) (pt.getY() - viewport.getY());
                    if (e.getPoint().distance(x, y) < 10) {
                        return stop.getName();
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (heatmap != null) {
            g.drawImage(heatmap, 0, 0, null);
        }
    }


}
