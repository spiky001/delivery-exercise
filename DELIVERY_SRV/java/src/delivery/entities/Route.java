




package delivery.entities;





/**
 * A class that represents a route on the map.
 * It stores the source and target points, the time and cost of the route.
 */
public class Route {

    private Point _from;
    private Point _to;
    private int   _time;
    private int   _cost;





    public Route(Point fromPoint, Point toPoint, int time, int cost) {

        _from = fromPoint;
        _to = toPoint;
        _time = time;
        _cost = cost;
    }





    public Route(Route r) {

        _from = r.getFromPoint();
        _to = r.getToPoint();
        _time = r.getTime();
        _cost = r.getCost();
    }





    public Point getFromPoint() {

        return _from;
    }





    public Point getToPoint() {

        return _to;
    }





    public int getTime() {

        return _time;
    }





    public int getCost() {

        return _cost;
    }





    @Override
    public String toString() {

        return "Route: " + _from.toString() + " -> " + _to.toString() + " (time=" + _time + ", cost=" + _cost + ")";
    }

}
