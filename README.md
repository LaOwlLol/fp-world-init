# fp-world-init :
_A computational geometry demo intended for use in map generation._

This project is still very much a work in progress.  It was intended as a tool for map generation. The target end goal is be something like [this](http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/) with interactive tools for editing.

This project currently wraps a modification of [ajwerner's Fortune's algorithm implementation](https://github.com/ajwerner/fortune).  This program modifies that implementation to build it's own doubly connected edge list graph ( described in [Computational Geometry (de Berg et al)](https://www.amazon.com/Computational-Geometry-Applications-Mark-Berg/dp/3540779736) ) to execute operations on. 

#### Clone this repo:

```
git clone https://github.com/LaOwlLol/fp-world-init.git
```

Or however you prefer to clone.

#### Build: 

```
./gradlew build
```
#### Generate a Map:

To generate a map from random points run the gradle task 'generator'.

```
./gradlew generator
```

The application will animate the creation of a graph with fortune's algorithm.  

   ##### Controls: 
   - **regenerate** - destroys the graph and builds a new one. 
   - **\>>** - high-lights a vertex and its adjacent edges.
   - **Face->** - selects all the edges adjacent to a face (due to incomplete graph model this does not not work properly).
   - **clear** - clears the face selection.
   
You can save the graph out to a file with the save button. By default it will be saved to ~/home/<username>/VoronoiGraphs/voronoi (on linux).  You can change the file name (not the location) by typing a name in the text field next to the save button.

#### View a Map

To view a map saved from the generator application run the gradle task 'view'.

```
./gradlew view
```

You will need to type in the name of a file to load from ~/home/<username>/VoronoiGraphs/  and **_press_** the **load** button.  By default the file attempted to load would be 'voronoi'.

   ##### Controls: 
   - **Vert->** - select a vertex and its adjacent edges.
   - **I/O** - toggle incoming/outgoing edges adjacent to selected vertex
   - **Face->** - selects all the edges adjacent to a face (due to incomplete graph model this does not not work properly).
   - **clear** - clears the vert/face selection.