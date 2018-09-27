




package delivery.entities;





/**
 * A class that represents a point on the map.
 * It implements the <code>equals</code> and <code>hashCode</code> methods,
 * allowing it to be used as keys in <code>Set</code> objects, for instance.
 */
public class Point {

    private String _name;





    public Point(String pointName) {

        _name = pointName;
    }





    public Point(Point point) {
        _name = point.getName();
    }





    public String getName() {

        return _name;
    }





    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((_name == null) ? 0 : _name.hashCode());
        return result;
    }





    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        if (_name == null) {
            if (other._name != null)
                return false;
        } else if (!_name.equals(other._name))
            return false;
        return true;
    }





    @Override
    public String toString() {

        return _name;
    }

}
