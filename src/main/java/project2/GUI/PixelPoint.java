package project2.GUI;

public class PixelPoint {
    private int x;
    private int y;
    public PixelPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getLonPixel() {
        return this.x;
    }

    public int getLatPixel() {
        return this.y;
    }

    public void setLongPixel(int x) {
        this.x = x;
    }

    public void setLatPixel(int y) {
        this.y = y;
    }
}
