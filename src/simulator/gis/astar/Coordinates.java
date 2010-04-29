package simulator.gis.astar;


public class Coordinates
{

    private final double latitude;
    private final double longitude;

    public Coordinates( final double latitude, final double longitude )
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongtude()
    {
        return longitude;
    }

    @Override
    public String toString()
    {
        return "longitude=" + longitude + ", latitude=" + latitude;
    }
}
