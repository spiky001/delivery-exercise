




package delivery.route.entities;





import org.jgrapht.graph.DefaultEdge;

import delivery.entities.Route;





/**
 * Auxiliary inner class for holding edge data for the graph calculations
 */
public class RouteEdge extends DefaultEdge {

    /**
     * 
     */
    private static final long serialVersionUID = 3025460856841326910L;

    private Route             _route;





    public RouteEdge(Route route) {

        _route = route;
    }





    public Route getRoute() {

        return _route;
    }





    @Override
    protected Object getSource() {

        return _route.getFromPoint();
    }





    @Override
    protected Object getTarget() {

        return _route.getToPoint();
    }
}
