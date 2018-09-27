




package delivery.route.entities;





import java.util.List;

import delivery.entities.Point;
import delivery.entities.Route;





/**
 * * Each <code>Trip</code> contains:
 * 
 * <pre>
 *   "From" - The start point of the trip
 *   "To" - The end point of the trip
 *   "Routes" - The list of Routes in the trip
 *   "TotalTime" - the total time of the trip
 *   "TotalCost" - the total cost of the trip
 *   
 *   "Cheapest" - indicates if it is the cheapest trip in all paths
 *   "Fastest" - indicates if it is the fastest trip in all paths
 * </pre>
 * 
 */
public class Trip {

    // the start point of the Trip
    private Point             _from;
    // the end point of the Trip
    private Point             _to;
    private List<Route>       _routes;
    private long              _totalTime;
    private long              _totalCost;

    // the following transient variables are only defined when calculating a trip list between two points
    // indicates if it is the fastest trip in all paths
    private transient boolean _fastest;
    // indicates if it is the cheapest trip in all paths
    private transient boolean _cheapest;





    public Trip(Point from, Point to, List<Route> routes, long totalTime, long totalCost) {
        super();
        _from = from;
        _to = to;
        _routes = routes;
        _totalTime = totalTime;
        _totalCost = totalCost;
    }





    /**
     * Returns the total time of the route
     */
    public long getTotalTime() {

        return _totalTime;
    }





    /**
     * Returns the total cost of the route
     */
    public long getTotalCost() {

        return _totalCost;
    }





    public List<Route> getRoutes() {

        return _routes;
    }





    public Point getFrom() {

        return _from;
    }





    public Point getTo() {

        return _to;
    }





    public void setTotalTime(long totalTime) {

        _totalTime = totalTime;
    }





    public boolean isCheapest() {

        return _cheapest;
    }





    public void setCheapest(boolean cheapest) {

        _cheapest = cheapest;
    }





    public boolean isFastest() {

        return _fastest;
    }





    public void setFastest(boolean fastest) {

        _fastest = fastest;
    }





    @Override
    public String toString() {

        return "Trip: " + _routes + " Total Time: " + getTotalTime() + " Total Cost: " + getTotalCost() + "\n";
    }

}
