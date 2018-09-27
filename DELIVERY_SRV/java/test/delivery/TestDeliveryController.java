




package delivery;





import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import delivery.entities.Point;
import delivery.entities.Route;
import delivery.exceptions.ExistingPointException;
import delivery.exceptions.ExistingRouteException;
import delivery.exceptions.PointNotFoundException;
import delivery.route.entities.Trip;





public class TestDeliveryController extends InitTest {

    @Before
    public void loadPoints() throws ExistingPointException {

        super.loadPoints();

        // load Routes
        Route[] routes = { new Route(new Point(A), new Point(C), 1, 20), new Route(new Point(A), new Point(H), 10, 1),
                           new Route(new Point(A), new Point(E), 30, 5), new Route(new Point(C), new Point(B), 1, 12),
                           new Route(new Point(D), new Point(F), 4, 50), new Route(new Point(E), new Point(D), 3, 5),
                           new Route(new Point(F), new Point(I), 45, 50), new Route(new Point(F), new Point(G), 40, 50),
                           new Route(new Point(G), new Point(B), 64, 73), new Route(new Point(H), new Point(E), 30, 1),
                           new Route(new Point(I), new Point(B), 65, 5) };

        for (Route r : routes) {
            try {
                _mngr.addRoute(r);
            } catch (ExistingRouteException | PointNotFoundException e) {
                e.printStackTrace();
            }
        }
    }





    @Test
    public void TestGetAllTripRoutes() throws PointNotFoundException {

        // get all trips between A and B
        List<Trip> trips = _mngr.getAllTripRoutes(A, B);

        // Should return 5 trips
        // [A -> C , C -> B ]
        // [A -> E , E -> D , D -> F , F -> I , I -> B ]
        // [A -> E , E -> D , D -> F , F -> G , G -> B ]
        // [A -> H , H -> E , E -> D , D -> F , F -> I , I -> B ]
        // [A -> H , H -> E , E -> D , D -> F , F -> G , G -> B ]

        assertEquals(5, trips.size());

        // get all trips between A and E
        trips = _mngr.getAllTripRoutes(A, E);

        // Should return only one trip
        // [A -> H , H -> E]
        // The direct Route [A->E] should be discarded
        assertEquals(1, trips.size());

    }





    @Test
    public void TestGetFastestTrip() throws PointNotFoundException {

        // get fastest trips between E and B
        Trip trips = _mngr.getFastestTrip(E, B);
        assertEquals("Should have two routes", 4, trips.getRoutes().size());

        // Assert that the Routes are:
        // Route: E -> D
        // Route: D -> F
        // Route: F -> G
        // Route: G -> B
        assertTrue(trips.getRoutes().contains(_mngr.getRoute(E, D)));
        assertTrue(trips.getRoutes().contains(_mngr.getRoute(D, F)));
        assertTrue(trips.getRoutes().contains(_mngr.getRoute(F, G)));
        assertTrue(trips.getRoutes().contains(_mngr.getRoute(G, B)));
    }





    @Test
    public void TestGetCheapestTrip() throws PointNotFoundException {

        // get Cheapest trips between E and B
        Trip trips = _mngr.getCheapestTrip(E, B);
        assertEquals("Should have two routes", 4, trips.getRoutes().size());

        // Assert that the Routes are:
        // Route: E -> D
        // Route: D -> F
        // Route: F -> I
        // Route: I -> B
        assertTrue(trips.getRoutes().contains(_mngr.getRoute(E, D)));
        assertTrue(trips.getRoutes().contains(_mngr.getRoute(D, F)));
        assertTrue(trips.getRoutes().contains(_mngr.getRoute(F, I)));
        assertTrue(trips.getRoutes().contains(_mngr.getRoute(I, B)));
    }

}
