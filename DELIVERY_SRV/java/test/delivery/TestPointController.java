




package delivery;





import static org.junit.Assert.assertEquals;

import org.junit.Test;

import delivery.entities.Point;
import delivery.exceptions.ExistingPointException;





public class TestPointController extends InitTest {

    @Test
    public void testAddPoint() throws ExistingPointException {

        assertEquals("Should not have any point", 0, _mngr.getPoints().size());

        Point pointA = new Point("A");
        Point pointB = new Point("B");

        _mngr.addPoint(pointA);
        _mngr.addPoint(pointB);

        assertEquals("There must be two points", 2, _mngr.getPoints().size());

    }





    @Test
    public void testGetPoint() throws ExistingPointException {

        Point pointA = new Point("A");

        _mngr.addPoint(pointA);

        assertEquals(pointA, _mngr.getPoint("A"));
    }





    @Test(expected = ExistingPointException.class)
    public void testAddExistingPoint() throws ExistingPointException {

        Point pointA = new Point("A");

        _mngr.addPoint(pointA);
        _mngr.addPoint(pointA);

    }
}
