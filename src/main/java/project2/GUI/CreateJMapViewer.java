package project2.GUI;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
public class CreateJMapViewer
{
    public static JXMapViewer createMapViewer()
    {
        JXMapViewer mapViewer = new JXMapViewer();
        NetworkChecker checker = new NetworkChecker();
    

        // Set up tile factory for OpenStreetMap
        if(checker.isConnected()) {
            System.out.println("Connected to the internet, using online map.");
            TileFactoryInfo info1 = new OSMTileFactoryInfo();
            DefaultTileFactory tileFactory = new DefaultTileFactory(info1);
            mapViewer.setTileFactory(tileFactory);
            mapViewer.setZoom(8);
        } else {
            System.out.println("No internet connection, using offline map.");
            TileFactoryInfo info2 = new OfflineTileFactory("Offline map", "jar:file:src/main/resources/OfflineMap.zip!");
            DefaultTileFactory tileFactory = new DefaultTileFactory(info2);
             mapViewer.setTileFactory(tileFactory);
            mapViewer.setZoom(7);
        }
        

        // Initial position and zoom
        GeoPosition initialPosition = new GeoPosition(49.5950, 5.9896);
        mapViewer.setAddressLocation(initialPosition);
        

        // Add panning interaction
        PanMouseInputListener panListener = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(panListener);
        mapViewer.addMouseMotionListener(panListener);
        return mapViewer;
    }
}