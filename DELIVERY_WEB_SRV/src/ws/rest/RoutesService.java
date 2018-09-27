




package ws.rest;





import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import delivery.database.MapStorage;
import delivery.entities.Point;
import delivery.entities.Route;
import delivery.exceptions.ExistingPointException;
import delivery.exceptions.ExistingRouteException;
import delivery.exceptions.PointNotFoundException;
import delivery.exceptions.RouteNotFoundException;
import delivery.route.DeliveryController;
import delivery.route.entities.Trip;
import ws.rest.auth.Secured;





@Path("/")
public class RoutesService {

    private static final String FROM            = "from";
    private static final String TO              = "to";
    private static final String COST            = "cost";
    private static final String TIME            = "time";

    private static final String RESULT          = "result";
    private static final String OK              = "Ok";

    private static final String POINTS          = "Points";
    private static final String ROUTES          = "Routes";

    private static final String TRIP            = "Trip";
    private static final String TOTAL_TIME      = "TotalTime";
    private static final String TOTAL_COST      = "TotalCost";

    private static final String POSSIBLE_ROUTES = "PossibleRoutes";
    private static final String MIN_TIME        = "MinTime";
    private static final String MIN_COST        = "MinCost";
    private static final String ROUTE           = "Route";





    /**
     * Loads a default Map, all points and routes
     */

    @Path("/load-default-map")
    @PUT
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadDefaultMap() throws JSONException {

        DeliveryController controller = DeliveryController.getInstance();

        // clear existing Map
        controller.clearMap();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(RoutesService.class.getResourceAsStream("default-map.txt"),
                                                                         Charset.forName("UTF-8")))) {

            String line;
            boolean readingPoints = true;
            while ((line = r.readLine()) != null) {
                if (line.length() == 0) {
                    readingPoints = false;
                    continue;
                }
                if (readingPoints) {
                    // Each line is the name of a point
                    try {
                        controller.addPoint(new Point(line.trim()));
                    } catch (ExistingPointException e) {
                        return errorResponse(e);
                    }
                } else {
                    // Each line contains a route, with the fields separated by
                    // spaces

                    try (Scanner lineScanner = new Scanner(line)) {
                        lineScanner.useDelimiter(",");
                        String fromPoint = lineScanner.next();
                        String toPoint = lineScanner.next();
                        int cost = lineScanner.nextInt();
                        int time = lineScanner.nextInt();
                        try {
                            controller.addRoute(fromPoint, toPoint, cost, time);
                        } catch (ExistingRouteException | PointNotFoundException e) {
                            return errorResponse(e);
                        }
                    }
                }
            }

        } catch (IOException e) {
            return errorResponse(e);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RESULT, OK);
        return response(jsonObject);
    }





    /**
     * Returns all existing Points and Routes in the Map
     * 
     * @return
     * @throws JSONException
     */
    @Path("/map")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMap() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        JSONArray points = new JSONArray();
        for (Point p : DeliveryController.getInstance().getPoints()) {
            points.put(p.getName());
        }
        jsonObject.put(POINTS, points);

        JSONArray routes = new JSONArray();
        for (Route r : DeliveryController.getInstance().getRoutes()) {
            routes.put(routeToJSON(r));
        }
        jsonObject.put(ROUTES, routes);

        return response(jsonObject);
    }





    /**
     * Delete all Points and Routes
     * 
     * @return
     * @throws JSONException
     */
    @Path("/clear-map")
    @Secured
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearMap() throws JSONException {

        MapStorage.getInstance().clearMap();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RESULT, OK);

        return response(jsonObject);
    }





    /**
     * Return the Route betwen the two given points
     */

    @Path("/route/{from}/{to}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoute(@PathParam("from") String from, @PathParam("to") String to) throws JSONException {

        Route route;
        try {
            route = DeliveryController.getInstance().getRoute(from, to);
        } catch (PointNotFoundException e) {
            return errorResponse(e);
        }

        JSONObject jsonObject = new JSONObject();

        // If the route does not exists
        if (route == null) {
            jsonObject.put("Nok", "Route not found " + route);
            return Response.status(Response.Status.NOT_FOUND).entity(jsonObject.toString()).build();
        }

        jsonObject.put(ROUTE, routeToJSON(route));

        return response(jsonObject);
    }





    /**
     * List all Routes
     */

    @Path("/routes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoutes() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        Collection<Route> routes = DeliveryController.getInstance().getRoutes();

        jsonObject.put(ROUTES, routes);

        return response(jsonObject);
    }





    /**
     * Retrieves all possible trips between two points
     */
    @Path("/all-trips/{from}/{to}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTrips(@PathParam("from") String from, @PathParam("to") String to) throws JSONException {

        List<Trip> trips;
        try {
            trips = DeliveryController.getInstance().getAllTripRoutes(from, to);
        } catch (PointNotFoundException e) {
            return errorResponse(e);
        }

        JSONObject jsonResult = new JSONObject();
        JSONArray jsonPaths = new JSONArray();
        long minTime = 0;
        long minCost = 0;

        for (Trip trip : trips) {
            JSONObject jsonTrip = tripToJSON(trip);
            if (trip.isFastest()) {
                minTime = trip.getTotalTime();
            }
            if (trip.isCheapest()) {
                minCost = trip.getTotalCost();
            }
            jsonPaths.put(jsonTrip);
        }

        jsonResult.put(POSSIBLE_ROUTES, jsonPaths);
        jsonResult.put(MIN_TIME, minTime);
        jsonResult.put(MIN_COST, minCost);

        return response(jsonResult);
    }





    /**
     * Retrieves the fastest trip, total time and cost between two points
     */
    @Path("/fastest-trip/{from}/{to}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFastestTrip(@PathParam("from") String from, @PathParam("to") String to) throws JSONException {

        Trip trip;

        try {
            trip = DeliveryController.getInstance().getFastestTrip(from, to);
        } catch (PointNotFoundException e) {
            return errorResponse(e);
        }

        JSONObject jsonResult = new JSONObject();
        JSONObject jsonTrip = tripToJSON(trip);
        jsonResult.put(TRIP, jsonTrip);
        jsonResult.put(TOTAL_TIME, trip.getTotalTime());
        jsonResult.put(TOTAL_COST, trip.getTotalCost());

        return response(jsonResult);

    }





    /**
     * Retrieves the cheapest trip, total time and cost between two points
     */

    @Path("/cheapest-trip/{from}/{to}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCheapestTrip(@PathParam("from") String from, @PathParam("to") String to) throws JSONException {

        Trip trip;

        try {
            trip = DeliveryController.getInstance().getCheapestTrip(from, to);
        } catch (PointNotFoundException e) {
            return errorResponse(e);
        }

        JSONObject jsonResult = new JSONObject();
        JSONObject jsonTrip = tripToJSON(trip);
        jsonResult.put(TRIP, jsonTrip);
        jsonResult.put(TOTAL_TIME, trip.getTotalTime());
        jsonResult.put(TOTAL_COST, trip.getTotalCost());

        return response(jsonResult);
    }





    /**
     * Adds a Point in the Map
     */
    @Path("/point")
    @Secured
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPoint(@FormParam("point-name") String pointName) throws JSONException {

        try {
            DeliveryController.getInstance().addPoint(new Point(pointName));

        } catch (ExistingPointException e) {
            return errorResponse(e);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RESULT, OK);
        return response(jsonObject);
    }





    /**
     * Adds a Route between two Points in the Map
     */
    @Path("/route")
    @Secured
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRoute(@FormParam("from") String from, @FormParam("to") String to, @FormParam("time") int time,
                             @FormParam("cost") int cost)
                    throws JSONException {

        try {
            DeliveryController.getInstance().addRoute(from, to, time, cost);
        } catch (Exception e) {
            return errorResponse(e);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RESULT, OK);

        return response(jsonObject);
    }





    /**
     * Update a Route
     */
    @Path("/route")
    @Secured
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRoute(@FormParam("from") String from, @FormParam("to") String to, @FormParam("time") int time,
                                @FormParam("cost") int cost)
                    throws JSONException {

        try {

            DeliveryController.getInstance().updateRoute(from, to, time, cost);

        } catch (Exception e) {
            return errorResponse(e);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RESULT,
                       "The Route (" + from + "->" + to + ") was updated! [Time: " + time + " Cost: " + cost + "]");
        return response(jsonObject);
    }





    /**
     * Delete a Route
     */
    @Path("/route/{from}/{to}")
    @Secured
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoute(@PathParam("from") String from, @PathParam("to") String to) throws JSONException {

        try {
            DeliveryController.getInstance().deleteRoute(from, to);

        } catch (RouteNotFoundException e) {
            return errorResponse(e);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RESULT, "The route (" + from + "->" + to + ") was deleted!");
        return response(jsonObject);
    }





    /**
     * Default Response
     */
    private Response response(JSONObject result) {

        return Response.status(Response.Status.OK).entity(result.toString()).build();

    }





    /**
     * Default Response on error
     */
    private Response errorResponse(Exception e) {

        // TODO: log the exception error
        e.printStackTrace();
        return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    }





    /**
     * Converts a Route to a JSONObject
     */
    private JSONObject routeToJSON(Route r) throws JSONException {

        JSONObject jsonRoute = new JSONObject();
        jsonRoute.put(FROM, r.getFromPoint().getName());
        jsonRoute.put(TO, r.getToPoint().getName());
        jsonRoute.put(COST, r.getCost());
        jsonRoute.put(TIME, r.getTime());

        return jsonRoute;
    }





    /**
     * Converts a Trip to a JSONObject
     */
    private JSONObject tripToJSON(Trip trip) throws JSONException {

        JSONObject jsonTrip = new JSONObject();
        JSONArray jsonRoutes = new JSONArray();

        for (Route r : trip.getRoutes()) {
            jsonRoutes.put(routeToJSON(r));
        }

        jsonTrip.put(ROUTES, jsonRoutes);
        jsonTrip.put(TOTAL_TIME, trip.getTotalTime());
        jsonTrip.put(TOTAL_COST, trip.getTotalCost());
        return jsonTrip;
    }
}
