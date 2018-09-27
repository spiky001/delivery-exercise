




package delivery.entities;





/**
 * A class that represents the unique data of a route, for testing duplicate routes.
 * The uniqueness is established by the source and destination points on the map only.
 * 
 * It implements the <code>equals</code> and <code>hashCode</code> methods,
 * allowing it to be used as keys in <code>Set</code> or <code>Map</code> objects, for instance.
 */
public class RouteKey {

    private Point _from;
    private Point _to;





    public RouteKey(Point fromPoint, Point toPoint) {

        _from = fromPoint;
        _to = toPoint;
    }





    public Point getFromPoint() {

        return _from;
    }





    public Point getToPoint() {

        return _to;
    }





    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RouteKey)) {
            return false;
        }
        RouteKey other = (RouteKey) obj;
        return (_from.equals(other._from) && _to.equals(other._to));
    }





    @Override
    public int hashCode() {

        return _from.hashCode() * 31 + _to.hashCode();
    }





    @Override
    public String toString() {

        return "RouteKey: " + _from.toString() + " -> " + _to.toString();
    }
}
