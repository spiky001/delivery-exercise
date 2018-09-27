




package delivery;





import org.junit.After;

import delivery.entities.Point;
import delivery.exceptions.ExistingPointException;
import delivery.route.DeliveryController;





public class InitTest {

    protected static final DeliveryController _mngr = DeliveryController.getInstance();

    protected static final String             A     = "A";
    protected static final String             B     = "B";
    protected static final String             C     = "C";
    protected static final String             D     = "D";
    protected static final String             E     = "E";
    protected static final String             F     = "F";
    protected static final String             G     = "G";
    protected static final String             H     = "H";
    protected static final String             I     = "I";





    /**
     * After each test, clear the map Reset all data added during a unit test
     */
    @After
    public void clearMap() {

        DeliveryController.getInstance().clearMap();

    }





    /**
     * Load a set of Points
     */
    public void loadPoints() throws ExistingPointException {

        String[] points = { A, B, C, D, E, F, G, H, I };

        for (String pointName : points) {
            _mngr.addPoint(new Point(pointName));
        }
    }

}
