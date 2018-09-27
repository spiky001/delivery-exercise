




package delivery.route;





import java.util.Collection;
import java.util.List;
import java.util.Set;

import delivery.database.MapStorage;
import delivery.entities.Point;
import delivery.entities.Route;
import delivery.entities.RouteKey;
import delivery.exceptions.ExistingPointException;
import delivery.exceptions.ExistingRouteException;
import delivery.exceptions.PointNotFoundException;
import delivery.exceptions.RouteNotFoundException;
import delivery.route.entities.Trip;





/**
 * 
 * Entry point class to management all Points and Routes on the Map
 *
 */

public class DeliveryController {

    private static DeliveryController _instance;





    // Prevent external instantiation
    private DeliveryController() {}





    /**
     * Retrieve the singleton object to control all map management operations.
     * 
     * @return {@link DeliveryController}
     */
    public static DeliveryController getInstance() {

        if (_instance == null) {
            synchronized (DeliveryController.class) {
                if (_instance == null) {
                    _instance = new DeliveryController();
                }
            }
        }

        return _instance;
    }





    /**
     * Returns all existing routes on the map
     */
    public Collection<Route> getRoutes() {

        return MapStorage.getInstance().getRoutes().values();
    }





    /**
     * Fetches all the possible paths from a source to a destination point.
     * Direct routes between the 2 points, if they exist, are not considered.
     * Apart from returning all the possible paths, it also identifies the fastest and the cheapest paths (which might not be the same).
     * 
     * @param from
     *            The start point name
     * @param to
     *            The destination point name
     * @return
     *         The list of trips found
     * 
     * @throws PointNotFoundException
     *             When there is no <code>Point</code> in map with the name "from" or "to"
     */
    public List<Trip> getAllTripRoutes(String from, String to) throws PointNotFoundException {

        Point fromPoint = MapStorage.getInstance().getPoint(from);
        Point toPoint = MapStorage.getInstance().getPoint(to);

        MapStorage.getInstance().ensurePointExists(fromPoint);
        MapStorage.getInstance().ensurePointExists(toPoint);

        return TripFactory.getAllTripRoutes(fromPoint, toPoint);
    }





    public Trip getFastestTrip(String from, String to) throws PointNotFoundException {

        List<Trip> trips = getAllTripRoutes(from, to);

        for (Trip trip : trips) {
            if (!trip.isFastest()) {
                continue;
            }
            // return the fastest trip
            return trip;
        }
        return null;
    }





    public Trip getCheapestTrip(String from, String to) throws PointNotFoundException {

        List<Trip> trips = getAllTripRoutes(from, to);

        for (Trip trip : trips) {

            if (!trip.isCheapest()) {
                continue;
            }
            // return the cheapest trip
            return trip;
        }
        return null;
    }





    /**
     * Insert a new point in the map.
     * 
     * A new point can be added if it does not already exist.
     * 
     * @param point
     *            The point to be added to the map.
     *
     * @throws ExistingRouteException
     *             An exception is thrown when the <Point> already exists
     */
    public void addPoint(Point point) throws ExistingPointException {

        if (MapStorage.getInstance().pointExists(point)) {
            throw new ExistingPointException("Point " + point + " already exists.");
        }
        MapStorage.getInstance().addPoint(point);

    }





    /**
     * Adds a route from a start point to an end point
     * 
     * @param from
     *            The start point of the route
     * @param to
     *            The end point of the route
     * @param time
     *            The time that takes to get from the start point to the end point
     * @param cost
     *            The cost that takes to get from the start point to the end point
     * @return
     *         The created <code>Route</code>
     * 
     * @throws ExistingRouteException
     *             An exception is thrown when the <code>Route</code> already exists
     * 
     * @throws PointNotFoundException
     *             When a <Point> does not exist in the Map
     */
    public Route addRoute(String from, String to, int time, int cost)
                    throws ExistingRouteException, PointNotFoundException {

        Route route = new Route(new Point(from), new Point(to), time, cost);

        return addRoute(route);
    }





    /**
     * Adds a route from a start point to an end point
     * 
     * @param route
     *            The route definition
     * 
     * @throws ExistingRouteException
     *             An exception is thrown when the <code>Route</code> already exists
     * 
     * @throws PointNotFoundException
     *             When a <Point> does not exist in the Map
     */
    public Route addRoute(Route route) throws ExistingRouteException, PointNotFoundException {

        if (MapStorage.getInstance().addRoute(route)) {
            return route;
        } else {
            throw new ExistingRouteException("The route (" + route.getFromPoint() + " -> " + route.getToPoint()
                                             + ") already exists");
        }
    }





    /**
     * Returns the existing Route for the given points
     * 
     * @throws PointNotFoundException
     */
    public Route getRoute(String from, String to) throws PointNotFoundException {

        MapStorage.getInstance().ensurePointExists(new Point(from));
        MapStorage.getInstance().ensurePointExists(new Point(to));

        RouteKey routeKey = buildRouteKey(from, to);

        return MapStorage.getInstance().getRoute(routeKey);
    }





    /**
     * Updates a Route
     * 
     * @param from
     *            The start point name
     * @param target
     *            The end point name
     * @param time
     *            The time to be updated
     * @param cost
     *            The cost to be updated
     * 
     * @throws RouteNotFoundException
     *             When there is no <Route> set between the two points
     */
    public void updateRoute(String from, String to, int time, int cost) throws RouteNotFoundException {

        Point fromPoint = MapStorage.getInstance().getPoint(from);
        Point toPoint = MapStorage.getInstance().getPoint(to);

        ensureRouteExists(from, to);

        MapStorage.getInstance().updateRoute(new Route(fromPoint, toPoint, time, cost));

    }





    private void ensureRouteExists(String from, String to) throws RouteNotFoundException {

        if (!MapStorage.getInstance().routeExists(new Point(from), new Point(to))) {

            throw new RouteNotFoundException("There is no route set from point [" + from + "] to [" + to + "].");
        }
    }





    /**
     * Deletes a Route from the Map
     * 
     * @param from
     *            The start point
     * @param to
     *            The end point
     * 
     * @throws RouteNotFoundException
     */
    public void deleteRoute(String from, String to) throws RouteNotFoundException {

        ensureRouteExists(from, to);

        RouteKey routeKey = buildRouteKey(from, to);

        MapStorage.getInstance().delete(routeKey);

    }





    /**
     * Builds a route key
     */
    private RouteKey buildRouteKey(String from, String to) {

        Point fromPoint = MapStorage.getInstance().getPoint(from);
        Point toPoint = MapStorage.getInstance().getPoint(to);

        return new RouteKey(fromPoint, toPoint);
    }





    /**
     * Retrieves the list of Points of the Map
     */

    public Set<Point> getPoints() {

        return MapStorage.getInstance().getPoints();
    }





    /**
     * Retrieves the Point with the given name
     */

    public Point getPoint(String pointName) {

        return MapStorage.getInstance().getPoint(pointName);
    }





    /**
     * Clear all Points and Routes of the Map
     */
    public void clearMap() {

        MapStorage.getInstance().clearMap();

    }

}
