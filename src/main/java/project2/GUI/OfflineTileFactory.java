package project2.GUI;

import org.jxmapviewer.viewer.TileFactoryInfo;


public class OfflineTileFactory extends TileFactoryInfo
{
    private static final int MAX_ZOOM = 19;

    public OfflineTileFactory(String name, String filename)
    {
        super(name, 2, 8, 19, 128, true, true, filename,"x", "y", "z");                        // 5/15/10.png
    }

    @Override
    public String getTileUrl(int x, int y, int zoom)
    {

        int invZoom = MAX_ZOOM - zoom ;
        String url = this.baseURL + "/" + invZoom + "/" + x + "/" + y + ".png";
        return url;
    }

}