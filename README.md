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


# Environment Setup Requirements
 * Eclipse with installed plugins:
   * Web Developer Tools
   * Jetty

# Setup
 1. Open Ecplise and click on "Open Projects from Filesystem"
 2. Select the projects inside the folder: **DELIVERY_SRV** and **DELIVERY_WEB_SRV**
 3. Ensure that both have no compilaton errors
 4. Rigth click on project **DELIVERY_WEB_SRV** -> Run As -> Run with Jetty
 5. Browse to http://localhost:8080/ and ensure that the following message is shown:
    `The Web Service is Ready!`


# Usage
**Note**: POST, PUT, and DELETE actions require authentication. GET actions are public, they do not require any type of authentication.

This table contains the available URIs to interact with the available objects controlled by the Delivery Service:

|Operation       |HTTP Method |URI Path                  |
|----------------|------------|--------------------------|
|loadDefaultMap  |POST        |/load-default-map         |
|getMap          |GET         |/map                      |
|clearMap        |DELETE      |/clear-map                |
|addPoint        |POST        |/point                    |
|addRoute        |POST        |/route                    |
|updateRoute     |PUT         |/route                    |
|deleteRoute     |DELETE      |/route/{from}/{to}        |
|getRoute        |GET         |/route                    |
|getRoutes       |GET         |/routes                   |
|getAllTrips     |GET         |/all-trips/{from}/{to}    |
|getFastestTrip  |GET         |/fastest-trip/{from}/{to} |
|getCheapestTrip |GET         |/cheapest-trip/{from}/{to}|

## Examples
* Load a default Map bundled with the code
```sh
$ curl -u admin:admin -X PUT http://localhost:8080/rest/load-default-map
{"result":"Ok"}
```

* Get definition of current Map
```sh
$ curl -X GET http://localhost:8080/rest/map
{"Points":["A","B","C","D","E","F","G","H","I"],
 "Routes":[{"cost":50,"from":"F","to":"G","time":40},
 {"cost":20,"from":"A","to":"C","time":1},
 {"cost":50,"from":"D","to":"F","time":4},
 {"cost":50,"from":"F","to":"I","time":45},
 {"cost":5,"from":"A","to":"E","time":30},
 {"cost":1,"from":"A","to":"H","time":10},
 {"cost":5,"from":"I","to":"B","time":65},
 {"cost":73,"from":"G","to":"B","time":64},
 {"cost":1,"from":"H","to":"E","time":30},
 {"cost":12,"from":"C","to":"B","time":1},
 {"cost":5,"from":"E","to":"D","time":3}]}
```

* Get all possible trips from an origin to a destination
```sh
$ curl -X GET http://localhost:8080/rest/all-trips/A/E
{"PossibleRoutes":[{"TotalCost":2,"TotalTime":40,"Routes":[{"cost":1,"from":"A","to":"H","time":10},{"cost":1,"from":"H","to":"E","time":30}]}],"MinCost":2,"MinTime":40}
```

* Get fastest trip from an origin to a destination
```sh
$ curl -X GET http://localhost:8080/rest/fastest-trip/E/B
{"TotalCost":178,"Trip":{"TotalCost":178,"TotalTime":111,"Routes":[{"cost":5,"from":"E","to":"D","time":3},{"cost":50,"from":"D","to":"F","time":4},{"cost":50,"from":"F","to":"G","time":40},{"cost":73,"from":"G","to":"B","time":64}]},"TotalTime":111}
```

* Get cheapest trip from an origin to a destination
```sh
$ curl -X GET http://localhost:8080/rest/cheapest-trip/E/B
{"TotalCost":110,"Trip":{"TotalCost":110,"TotalTime":117,"Routes":[{"cost":5,"from":"E","to":"D","time":3},{"cost":50,"from":"D","to":"F","time":4},{"cost":50,"from":"F","to":"I","time":45},{"cost":5,"from":"I","to":"B","time":65}]},"TotalTime":117}
```

* Add a Point J to the map definition
```sh
$ curl -u admin:admin -X POST http://localhost:8080/rest/point -d point-name=J
{"result":"Ok"}
```

* Add a Route I -> J to the map definition
```sh
$ curl -u admin:admin -X POST http://localhost:8080/rest/route -d from=I -d to=J -d time=5 -d cost=10
{"result":"Ok"}
```

* Get the Route I -> J from the map definition
```sh
$ curl -X GET http://localhost:8080/rest/route/I/J
{"Route":{"cost":10,"from":"I","to":"J","time":5}}
```

* Update Route I -> J in the map definition
```sh
$ curl -u admin:admin -X PUT http://localhost:8080/rest/route -d from=I -d to=J -d time=30 -d cost=20
{"result":"The Route (I->J) was updated! [Time: 30 Cost: 20]"}
```

* Delete the Route I -> J from the map definition
```sh
$ curl -u admin:admin -X DELETE http://localhost:8080/rest/route/I/J
{"result":"The route (I->J) was deleted!"}
```

* Delete the entire Map definition
```sh
$ curl -X DELETE http://localhost:8080/rest/clear-map
Invalid Authentication
```
```sh
$ curl -u admin:admin -X DELETE http://localhost:8080/rest/clear-map
{"result":"Ok"}
```


# ToDo's
The following aspects are not yet be addressed in the solution and should be improved later on:
* Persist Map data to a relational database
* Convert project to Maven-based build and dependencies gathering
* Automated unit tests, including concurrency scenarios
* Review security mechanism (e.g. segregated authorization for different actions)
* Update to latest Jersy component release
* Add logging
