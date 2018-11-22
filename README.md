
# Honors By Contract

An implementation of the C4.5 Decision Tree building algorithm. Developed as my Honors by Contract project for Marist College with assistance from my professor, [Pablo Rivas](https://github.com/pablorp80).

## Getting Started

To run, compile DecisionTreeNode.java and TreeBuilder.java, then run TreeBuilder with your dataset.

If you are using your own dataset, the dataset should be formatted as follows:
```
field a1,field a2,field a3,field a4,...,target a
field b1,field b2,field b3,field b4,...,target b
field c1,field c2,field c3,field c4,...,target c
...
```

## Implementation

The DecisionTreeNode.java file contains all of the functions needed to create a C4.5 decision tree

The TreeBuilder.java file reads in a correctly formatted dataset, and feeds that data into the DecisionTree and DecisionTreeNode to generate a C4.5 decision tree
