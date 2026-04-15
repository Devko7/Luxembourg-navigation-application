# How to connect to a database:

Have an environment variable: "ROUTING_ENGINE_MYSQL_JDBC" configured, containing a URL (with username and password) to a MySQL database. If you experience issues, make sure to set the global variable "local_infile" to 1 in your MySQL server.

*Note 1:* 
According to the project specification the database that the environment variable contains the URL for is empty and will be recursively deleted after use. This means that the DataBaseConnection class is intended for use with a completely empty database. It will create tables when the instance of the DataBaseConnection is requested, not before.

*Note 2:*
Since the files that we had and therefore would use were: agency, calendar, calendar_dates, trips, stops, stop_times and transfers, all of these files must be present in the zip file you provided.

---
# Interacting with the RoutingEngine:

If you just want to run the RoutingEngine without the GUI, simply run the *RoutingEngine.java* file after you have connected to a database. After "Starting" has been displayed in the console, input your desired input in a valid JSON format:

- **Loading data**: Type {"load": "your_directory/gtfs.zip"}, where "your_directory" is the path to your zip file containing GTFS data, and "gtfs.zip" is your file with GTFS data.
- **Route finding**: Type {"routeFrom":{"lat":0.0,"lon":0.0},"startingAt":"08:00","to":{"lat":0.0,"lon":0.0}}, where {"lat":0.0,"lon":0.0} are the coordinates of your start and end points respectively, and "08:00" is your desired start time for the journey.

The RoutingEngine will return a list of routeStep objects, as described in the manual, of every step of the journey that needs to be taken, according to fastest time. Note that, the algorithm might choose to walk further to another stop (i.e. more than 500m) before getting on public transport, this is because it has found that it is more optimal to do so.

---
# Console User Interface

A console-based UI is also available, simply run the *ConsoleInterface.java* file. 

You will then be prompted if you wish to load new GTFS data. Typing "y" or "Y" and hitting Enter will start the RoutingEngine, after the engine replies with {"ok", "loaded"}, type any input that is not in JSON format (this will terminate the RoutingEngine), alternatively you can populate the database you are connected to with your GTFS data on your own. If you don't wish to load new data, type "n" or "N" and hit Enter to continue.

Now you will be prompted if you wish to open the GUI. Typing "y" or "Y" and hitting Enter will open it in a new window. Type "n" or "N" if you don't wish to open it.

If you chose not to open the GUI, you will be asked to provide the coordinates of your starting and ending points, as well as the start time of your journey. After hitting Enter on the last prompt the RoutingEngine will calculate the route and return the String converted routeStep's.

---
# Graphical User Interface (GUI)

**WARNING:** GUI functionalities will work ONLY if you are connected to a database and have loaded in GTFS data, either by having your database already populated or uploading new data using the Console UI or RoutingEngine!

If you wish to only use the GUI, run the *MainFrame.java* file.

### Journey Planner Panel:
On the upper-right corner of the screen you will see a panel with text fields "From:", "To:", and "Start Time". Type the latitude and longitude coordinates (separated by a space) of your desired start point in the "From:" text field. Do the same for your end point in the "To:" field. Type your desired start time in the "Start Time:" field in the form "HH:MM".

### Clicking on the map (point conversion):
The first time you click on the map, the position of your cursor on that point on the map will be converted to real-world coordinates that will be used for your starting point (they will appear in the "From:" text field in the Journey Planner Panel). The second point you click on the map will be your end point (it's coordinates will appear in the "To:" text field in the Journey Planner Panel).

### Plan Journey Button:
After both the "From:" and "To:" are filled with valid coordinates, as well as a start time has been specified in "Start Time:" in the Journey Planner Panel you can hit the "Plan Journey" button below the panel. This will calculate the journey you need to take between those two points in terms of quickest time.

### Journey Display:
After calculation of the journey, it will be displayed on the map. The blue dashed lines represent walking steps, the red solid lines represent public transport lines, and the white circles represent transfer stops.

### RouteStep Visualization:
After calculating a journey, all walking steps, rides, and transfers will be displayed in a seperate window with all steps listed.

### Heatmap:
If you have valid coordinates in the "From:" text field in the Journey Planner Panel and a start time in it's corresponding text field, then you can press the "Heatmap" button at the bottom-left portion of the screen. This will show you a heatmap of travel times throughout the city. The travel times vary from 0-5 (i.e. from green to red), with 0 representing very short travel times, and 5 representing very long travel times.
- By pressing the arrow keys up or down you can increase or decrease the opacity of the colors of the heatmap, respectively.
- If you press "T" you will be able to see the "Individual Stops" mode of the heatmap, where you would be able to hover over all colored points (which are all public transport stops) and see their name and corresponding color.
- While in "Individual Stops" mode, if you hover over a stop and press right-click on your mouse on it you will delete that stop and the heatmap will be recalculated according to that. Press Ctrl+Z to return the stop, the heatmap will automatically be recalculated again.
- If you press "Esc" you will disable the heatmap.