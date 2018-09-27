




package delivery.route;





import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;

import delivery.database.MapStorage;
import delivery.entities.Point;
import delivery.entities.Route;
import delivery.route.entities.RouteEdge;
import delivery.route.entities.Trip;





public final class TripFactory {

    /**
     * Fetches all the possible paths from a source to a destination point.
     * Direct routes between the 2 points, if they exist, are not considered.
     * Apart from returning all the possible paths, it also identifies the fastest and the cheapest paths (which might not be the same).
     * 
     * @param from
     *            The source point of the route
     * @param to
     *            The destination point of the route
     * @return A <code>List</code> of <code>{@link Trip}</code> with the following structure:<br>
     * 
     */
    public static List<Trip> getAllTripRoutes(Point from, Point to) {

        DefaultDirectedGraph<Point, RouteEdge> graph = new DefaultDirectedGraph<>(RouteEdge.class);

        // Instantiate the graph representation from the DB data
        MapStorage storage = MapStorage.getInstance();

        // Add all vertexes
        for (Point p : storage.getPoints()) {
            graph.addVertex(p);
        }
        // Add all routes (edges); no weight is needed since we'll be doing the calculation ourselves out of the several possible paths
        Collection<Route> availableRoutes = storage.getRoutes().values();
        for (Route r : availableRoutes) {
            RouteEdge e = new RouteEdge(r);
            graph.addEdge(r.getFromPoint(), r.getToPoint(), e);
        }

        List<Trip> trips = new ArrayList<>();

        if (!availableRoutes.isEmpty()) {

            // Use this Dijkstra-like algorithm to fetch all possible paths from the origin to the destination
            AllDirectedPaths<Point, RouteEdge> allPaths = new AllDirectedPaths<>(graph);
            List<GraphPath<Point, RouteEdge>> paths = allPaths.getAllPaths(new Point(from), new Point(to), true, null);

            /*
             * Loop through all solutions in order to:
             * - discard the ones that are not acceptable (i.e. no intermediate steps)
             * - add all information for each path (routes, total time and cost) to the JSON to be returned
             * - identify the cheapest and fastest routes to return them in JSON as well
             */
            long minTime = Long.MAX_VALUE;
            int minTimeIndex = -1;
            long minCost = Long.MAX_VALUE;
            int minCostIndex = -1;

            int currentIndex = -1;
            for (GraphPath<Point, RouteEdge> path : paths) {
                // Ignore paths that don't have at least 2 hops
                if (path.getEdgeList().size() <= 1) {
                    continue;
                }

                currentIndex++;

                long totalTime = 0;
                long totalCost = 0;
                List<Route> routes = new ArrayList<>();

                for (RouteEdge routeEdge : path.getEdgeList()) {

                    routes.add(routeEdge.getRoute());
                    totalTime += routeEdge.getRoute().getTime();
                    totalCost += routeEdge.getRoute().getCost();
                }
                // add a trip
                trips.add(new Trip(path.getStartVertex(), path.getEndVertex(), routes, totalTime, totalCost));

                // Check if this path is the fastest or cheapest
                if (totalTime < minTime || ((totalTime == minTime) && (totalCost < minCost))) {
                    minTimeIndex = currentIndex;
                    minTime = totalTime;
                }
                if (totalCost < minCost || ((totalCost == minCost) && (totalTime < minTime))) {
                    minCostIndex = currentIndex;
                    minCost = totalCost;
                }
            }

            // mark the faster trip
            if (minTimeIndex >= 0) {
                trips.get(minTimeIndex).setFastest(true);
            }
            // mark the cheaper trip
            if (minCostIndex >= 0) {
                trips.get(minCostIndex).setCheapest(true);
            }
        }

        return trips;

    }

}
