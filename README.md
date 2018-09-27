# Delivery Service
This is a simple REST service solution for a delivery service, to infer the list of routes between two points in a Map.
It allows:
* Managing points and routes of the map;
* Retrieving the routes within a defined origin and destination points.

Some rules are applied when calculating routes:
* The route must not perform a direct delivery between the origin and the destination - for example, if a
delivery must be made from point A to point B, it must choose the routes that pass through intermediate
points, as for example, A - C - B but not A - B directly;
* Each route has a cost and time associated, so it is possible to retrieve the best option depending on the cost
or time. For a given origin and destination, multiple possible routes might exist, but one of them might be the cheapest and another one might be the fastest.
