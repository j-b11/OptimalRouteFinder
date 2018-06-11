# OptimalRouteFinder
Finding the shortest or the fastest path on the map based on places which you have chosen, solving Traveling Salesman Problem. 


The traveling salesman problem (TSP) is a popular mathematics problem that asks for the most efficient trajectory possible given a set of points and distances that must all be visited. In computer science, the problem can be applied to the most efficient route for data to travel between various nodes.
The Traveling Salesman Problem is typical of a large class of "hard" optimization problems that have intrigued mathematicians and computer scientists for years. Most important, it has applications in science and engineering. For example, in the manufacture of a circuit board, it is important to determine the best order in which a laser will drill thousands of holes. An efficient solution to this problem reduces production costs for the manufacturer. 


The travelling salesman problem is regarded as difficult to solve. If there is a way to break this problem into smaller component problems, the components will be at least as complex as the original one. This is what computer scientists call NP-hard problems.


The description of the problem and the ways to implement solutions to this problem have been very well presented by Google Developers [here](https://developers.google.com/optimization/routing/tsp) ( [Google Optimization Tools](https://developers.google.com/optimization/) ). 


------------------------------------------------------------------
*Using:*

-[Google Maps for Android](https://developers.google.com/maps/documentation/android-sdk/intro)

-[Google Places web service](https://developers.google.com/places/web-service/intro)

-[Google Distance Matrix web service](https://developers.google.com/maps/documentation/distance-matrix/intro)

-[Google Directions web service](https://developers.google.com/maps/documentation/directions/intro)

Communication with all those services is based on HTTP REST protocol. Getting a JSON file as response, which is parsing to get all necessary data.

The minimal version of Android SDK: 19

If you are going to use this project you should replace the default Google Maps Api Key with your own.

You can get your own Google Maps Api Key [here](https://developers.google.com/maps/documentation/android-sdk/signup).


## Screenshots:

Main menu:

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/main%20menu.png" width="360" height="640">

When you select "Plan route", you have to create list of places. The route will be based on these places.
First, you have to specify the radius. This will allow you to search for places only at a specified radius from your current location
Then, you can select place from user places (local database):

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/find%20place%201.png" width="360" height="640">


Or you can aslo select other category. In this case, places will be searched using the [Google Places](https://developers.google.com/places/web-service/intro) web service:

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/find%20place%202.png" width="360" height="640">


There is an example of found places from category "Shopping center" in radius 500 m from user location:

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/find%20place%203.png" width="360" height="640">


You can also finding places by keywords:

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/find%20place%204.png" width="360" height="640">

When you select some place, you have to click on it and then click "Add place":

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/find%20place%205.png" width="360" height="640">


This will add selected place on selected places list:

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/selected%20places%201.png" width="360" height="640">

Then you can add more places to selected places list or find a route based on these places.
There is an example of selected places list: 

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/selected%20places%202.png" width="360" height="640">


When you select "Find road" button, the application will start finding the best route:

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/finding%20best%20route.png" width="360" height="640">


This is an example of found route for driving mode with optimization by distance:

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/map%201.png" width="360" height="640">

Below the map, you can find more information about found route, e.g. shortest path, which has been found or the longest possible path. There is also information about the order in which selected places should be visited so that the route will be optimal. In this section, there is also a distance matrix or a time matrix from which the best possible route has been found. This matrix was made using the [Google Distance Matrix](https://developers.google.com/maps/documentation/distance-matrix/intro) web service.

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/map%202.png" width="360" height="640">


Sample route for driving mode with optimization by time:

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/map%203.png" width="360" height="640">

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/map%204.png" width="360" height="640">


Sample route for walking mode with optimization by distance:

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/map%205.png" width="360" height="640">


Sample route for cycling mode with optimization by distance:

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/map%206.png" width="360" height="640">

If you want you can click on any marker on the map and then select the navigation button on the bottom of the map. This will automatically start navigation to the selected point:

<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/navigate.png" width="360" height="640">

