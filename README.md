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

### Finding places: 
User places from local database:
![Selecting place](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/find%20place%201.png)


<img src="https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/find%20place%201.png" width="360" height="640">


Selecting place category:
![Selecting place](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/find%20place%202.png)


Found places from category "Shopping center" in radious 500m from user location:
![Selecting place](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/find%20place%203.png)


Finding places by keywords:
![Selecting place](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/find%20place%204.png)


Selecting place:
![Selecting place](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/find%20place%205.png)


Selected place added on selected places list:
![Selecting place](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/selected%20places%201.png)


Creating sample selected places list:
![Selecting place](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/selected%20places%202.png)


Finding best route for selected places list:
!map](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/finding%20best%20route.png)


Best route for driving mode with optimization by distance:
![map](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/map%201.png)

![map](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/map%202.png)


Best route for driving mode with optimization by time:
![map](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/map%203.png)

![map](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/map%204.png)


Best route for walking mode with optimization by distance:
![map](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/map%205.png)


Best route for cycling mode with optimization by distance:
![map](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/map%206.png)


Start navigation to an example point:
![navigation](https://github.com/j-b11/OptimalRouteFinder/blob/master/Screenshots/navigate.png)

