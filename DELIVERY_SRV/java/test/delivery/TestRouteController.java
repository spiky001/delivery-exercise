




package delivery;





import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import delivery.entities.Route;
import delivery.exceptions.ExistingPointException;
import delivery.exceptions.ExistingRouteException;
import delivery.exceptions.PointNotFoundException;
import delivery.exceptions.RouteNotFoundException;





public class TestRouteController extends InitTest {

    @Before
    public void loadPoints() throws ExistingPointException {

        super.loadPoints();
    }





    @Test
    public void testAddRoute() throws ExistingRouteException, PointNotFoundException {

        _mngr.addRoute(A, C, 1, 20);
        _mngr.addRoute(C, B, 1, 12);

        assertEquals(2, _mngr.getRoutes().size());

    }





    @Test(expected = ExistingRouteException.class)
    public void testAddExistingRoute() throws ExistingRouteException, PointNotFoundException {

        _mngr.addRoute(A, C, 1, 20);
        _mngr.addRoute(A, C, 1, 20);
    }





    @Test(expected = PointNotFoundException.class)
    public void testAddRouteWithNonExistingToPoint() throws ExistingRouteException, PointNotFoundException {

        _mngr.addRoute(A, "X", 1, 20);

    }





    @Test(expected = PointNotFoundException.class)
    public void testAddRouteWithNonExistingFromPoint() throws ExistingRouteException, PointNotFoundException {

        _mngr.addRoute("Y", C, 1, 20);
    }





    @Test(expected = PointNotFoundException.class)
    public void testAddRouteWithNonExistingFromAndToPoint() throws ExistingRouteException, PointNotFoundException {

        _mngr.addRoute("Z", "W", 1, 20);
    }





    @Test
    public void testGetRoute() throws ExistingRouteException, PointNotFoundException {

        Route route = _mngr.addRoute(A, C, 1, 20);
        assertEquals(route, _mngr.getRoute(A, C));
    }





    @Test
    public void testUpdateRoute() throws ExistingRouteException, PointNotFoundException, RouteNotFoundException {

        _mngr.addRoute(A, C, 1, 20);
        Route route = _mngr.getRoute(A, C);

        assertEquals(1, route.getTime());
        assertEquals(20, route.getCost());

        // update route time and cost and assert it was updated
        _mngr.updateRoute(A, C, 3, 7);

        Route updatedRoute = _mngr.getRoute(A, C);

        assertEquals(3, updatedRoute.getTime());
        assertEquals(7, updatedRoute.getCost());
    }





    @Test
    public void testDeleteRoute() throws ExistingRouteException, PointNotFoundException, RouteNotFoundException {

        // add a Route
        _mngr.addRoute(A, C, 1, 20);
        assertEquals(1, _mngr.getRoutes().size());

        // delete the Route and assert that it was deleted
        _mngr.deleteRoute(A, C);
        assertEquals(0, _mngr.getRoutes().size());

    }

}
