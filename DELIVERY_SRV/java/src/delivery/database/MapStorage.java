




package delivery.database;





import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import delivery.entities.Point;
import delivery.entities.Route;
import delivery.entities.RouteKey;
import delivery.exceptions.PointNotFoundException;
import delivery.exceptions.RouteNotFoundException;





/***************************************************************************************
 * 
 * An in-memory database of existing points and the routes between them.
 * Allows insertion of points and routes, and clearing the whole map.
 * Inserted routes are validated - no duplicates are not allowed (in the same direction)
 * and the indicated points must exist on the map.
 * 
 * Next steps:
 * - This storage should be improved to be backed by an actual database.
 * - Validating that the map is consistent.
 * - Add logging
 * 
 ***************************************************************************************/
public class MapStorage {

    private Map<String, Point>   _points = new HashMap<>();
    private Map<RouteKey, Route> _routes = new HashMap<>();

    // Singleton object
    private static MapStorage    _instance;





    // Prevent external instantiation
    private MapStorage() {}





    /**
     * Retrieve the singleton object that holds the map data.
     * 
     * @return A <code>MapStorage</code> object that holds the map data.
     */

    public static MapStorage getInstance() {

        if (_instance == null) {
            synchronized (MapStorage.class) {
                if (_instance == null) {
                    _instance = new MapStorage();
                }
            }
        }
        return _instance;
    }





    /**
     * Insert a new point in the map.
     * A new point can be added if it does not already exist.
     * 
     * @param point
     *            The point to be added to the map.
     * @return <code>True</code> if the point was added, <code>False</code> otherwise.
     */
    public synchronized boolean addPoint(Point p) {

        // Check if point is already registered
        if (_points.containsKey(p.getName())) {
            return false;
        } else {
            // Log success inserting
            _points.put(p.getName(), p);
            return true;
        }
    }





    /**
     * Retrieve all the points in the map.
     * 
     * @return A <code>Set</code> where each entry is a <code>Point</code> for a map location.
     *         This object is a copy of the map data, i.e. it can be modified without impacting on the original map.
     */
    public synchronized Set<Point> getPoints() {

        Set<Point> result = new HashSet<>();
        result.addAll(_points.values());
        return result;
    }





    /**
     * Returns the Point on the map with the given name.
     * 
     * @return A <code>Point</code> in the map.
     * 
     */
    public Point getPoint(String pointName) {

        return _points.get(pointName);

    }





    /**
     * Check if already exist a Point with the given name
     * 
     * @param pointName
     *            The point name to check
     * @return
     *         True when there is a <code>{@link Point}</code> with the given name. Otherwise, returns false.
     */
    public boolean pointExists(String pointName) {

        return _points.containsKey(pointName);

    }





    /**
     * Returns the Route that links the two points for the given route key.
     * 
     * @param routeKey
     *            The route key to search for in the map
     * 
     * @return A <code>{@link Route}</code> in the map.
     * 
     */
    public Route getRoute(RouteKey routeKey) {

        Route route = _routes.get(routeKey);
        if (route != null) {
            return route;
        }
        return null;

    }





    /**
     * Validates if exist e Route set between two Points
     * 
     * @param from
     *            The start point
     * @param to
     *            The end point
     * @return
     *         True when there is a <code>{@link Route}</code> set. Otherwise, returns false.
     */
    public boolean routeExists(Point from, Point to) {

        RouteKey routeKey = new RouteKey(from, to);
        return _routes.containsKey(routeKey);
    }





    /**
     * Insert a new route, a directed link between two points in the map.
     * A new route can be added if both the source and destination points exist on the map, and a directed route between them does not already exist.
     * 
     * @param route
     *            The route to be added to the map.
     * @return <code>True</code> if the route was added, <code>False</code> otherwise.
     * 
     * @throws PointNotFoundException
     *             When the given Point does not exist
     * 
     * @throws IllegalArgumentException
     *             When the <code>Point</code> "from" and the <code>Point</code> "to" are the same
     */
    public synchronized boolean addRoute(Route r) throws PointNotFoundException {

        // Check if both From and To points exist on the map
        ensurePointExists(r.getFromPoint());
        ensurePointExists(r.getToPoint());

        // Check if the two points are the same
        if (r.getFromPoint().equals(r.getToPoint())) {
            throw new IllegalArgumentException("The route (" + r.getFromPoint() + " -> " + r.getToPoint()
                                               + ") is not permitted");
        }

        // Check if a link from the source and target points (directed) already exists
        RouteKey routeKey = new RouteKey(r.getFromPoint(), r.getToPoint());
        if (_routes.containsKey(routeKey)) {
            return false;
        } else {
            // Log success inserting
            _routes.put(routeKey, r);
            return true;
        }
    }





    /**
     * Throws an exception when the given Point does not exist on the map
     * 
     * @param p
     *            The point to validate
     * 
     * @throws PointNotFoundException
     *             When the given Point does not exist
     */
    public void ensurePointExists(Point p) throws PointNotFoundException {

        if (!pointExists(p)) {
            throw new PointNotFoundException("The Point [" + p + "] does not exist");
        }
    }





    /**
     * Validates if a given point exists
     * 
     * @param p
     *            The point to validate
     */
    public boolean pointExists(Point p) {

        return _points.containsKey(p.getName());

    }





    /**
     * Updates a Route.
     * 
     * @param r
     *            The route to be updated
     * @return
     *         True when the Route is updated and false when the Route does not exist
     */

    public synchronized boolean updateRoute(Route r) throws RouteNotFoundException {

        RouteKey routeKey = new RouteKey(r.getFromPoint(), r.getToPoint());

        Route route = _routes.get(routeKey);
        if (route == null) {
            return false;
        }

        _routes.put(routeKey, r);
        return true;
    }





    /**
     * Delete a Route from the map with the given Points
     * 
     * @return
     *         True when the <code>Route</code> is deleted. Otherwise returns false.
     */
    public synchronized boolean delete(RouteKey routeKey) {

        return _routes.remove(routeKey) != null;

    }





    /**
     * Retrieve all the routes in the map.
     * 
     * @return A <code>Map</code> where each key is a <code>RouteKey</code> and the value is the <code>Route</code> details.
     *         This object is a copy of the map data, i.e. it can be modified without impacting on the original map.
     */
    public synchronized Map<RouteKey, Route> getRoutes() {

        Map<RouteKey, Route> result = new HashMap<>();
        result.putAll(_routes);
        return result;
    }





    /**
     * Clear all Points and Routes of the Map
     */
    public synchronized void clearMap() {

        _points.clear();
        _routes.clear();
    }





    @Override
    public String toString() {

        return "MapStorage: Points=[" + _points.toString() + "], Routes=[" + _routes.toString() + "]";
    }

}
