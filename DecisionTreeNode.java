import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Random;
/*This is an implementation of a Tree node
 *used for implementing a C4.5 tree building algorithm
 *@author Michael Read
 *@version 1.0
 */
class DecisionTreeNode{

  //The left child node
  public DecisionTreeNode left;
  //the right child node
  public DecisionTreeNode right;
  //the parent node
  private DecisionTreeNode parent;
  //the direction coming from the parent node
  private String direction;

  //The field in the data which is looked at to make a decision
  private int decisionField;
  //The operation on decisionField which decides which child node to visit
  private String decisionOp;
  //Should this be a leaf node, this is the value which this node represents
  private String value;

  /*Default constructor for DecisionTreeNode
   *Initializes all fields to null, empty, -1 values
   */
  public DecisionTreeNode(){
    this.decisionField = -1;
    this.decisionOp = "";
    this.value = "";
    this.left = null;
    this.right = null;
    this.parent = null;
    this.direction = "";
  }

  /*Constructor for DecisionTreeNode
   *sets all fields to the given values
   */
  public DecisionTreeNode(int decisionField, String decisionOp, String value, DecisionTreeNode parent, String direction){
    this.decisionField = decisionField;
    this.decisionOp = decisionOp;
    this.value = value;
    this.left = null;
    this.right = null;
    this.parent = parent;
    this.direction = direction;
  }

  /*Compares the given data entry to the tree to determine if
   *the tree accurately classifies given data.
   *returns 1 if the data is accurately classified
   *returns 0 if the data is incorrectly classified
   */
  public int checkData(String[] data){
    if(getValue().equals("N/A")){
      if(getDecisionOp().substring(0,1).equals("=")){
        if(data[decisionField].equals(getDecisionOp().substring(1))){
          return this.left.checkData(data);
        }
        else{
          return this.right.checkData(data);
        }
      }
      else{
        if(Double.parseDouble(data[decisionField]) < Double.parseDouble(getDecisionOp().substring(1))){
          return this.left.checkData(data);
        }
        else{
          return this.right.checkData(data);
        }
      }
    }
    else{
      if(data[data.length - 1].equals(getValue())){
        return 1;
      }
      else{
        return 0;
      }
    }
  }

  //basic getter functions for the fields
  public DecisionTreeNode getLeft()          { return left;          }
  public DecisionTreeNode getRight()         { return right;         }
  public DecisionTreeNode getParent()        { return parent;        }
  public int              getDecisionField() { return decisionField; }
  public String           getDecisionOp()    { return decisionOp;    }
  public String           getValue()         { return value;         }
  public String           getDirection()     { return direction;     }

  /*inserts a node as the left child of this node
   */
  public void insertLeft(DecisionTreeNode toInsert){
    if(left == null){
      left = toInsert;
    }
    else{
      left.insertLeft(toInsert);
    }
  }

  /*Inserts a node as the right child of this node
   */
  public void insertRight(DecisionTreeNode toInsert){
    if(right == null){
      right = toInsert;
    }
    else{
      right.insertRight(toInsert);
    }
  }

  /*Returns the total number of nodes in the tree having this node as the root
   */
  public int size(){
    if(this.left == null && this.right == null){
      return 1;
    }
    else if(this.left == null){
      return 1 + this.right.size();
    }
    else if(this.right == null){
      return 1 + this.left.size();
    }
    else{
      return 1 + this.left.size() + this.right.size();
    }
  }

  /*Goes through the tree to find a random node in the tree
   */
  public DecisionTreeNode getRandomNode(){
    int size = size();
    int leftSize;
    if(left == null){
      leftSize = 0;
    }
    else{
      leftSize = left.size();
    }
    Random rand = new Random();
    int index = rand.nextInt(size);
    if(index < leftSize){
      return left.getRandomNode();
    }
    else if(index == leftSize){
      return this;
    }
    else{
      return right.getRandomNode();
    }
  }

  /*Prints the tree having this node as the root in Pre order
   */
  public void printPreorder() {
    String indent = "";
    if(this.getValue().equals("N/A")){
      System.out.println(indent + "Split on attribute " + this.getDecisionField() + ", based on if " + this.getDecisionOp());
    }
    else{
      System.out.println(indent + "Value: " + this.getValue());
    }
    if(this.getLeft() != null){
      this.getLeft().printPreorder("  ");
    }
    if(this.getRight() != null){
      this.getRight().printPreorder("  ");
    }
  }

  /*Helper method for printPreorder() for better formatting
   */
  private void printPreorder(String indent){
    if(this.getValue().equals("N/A")){
      System.out.println(indent + "Split on attribute " + this.getDecisionField() + ", based on if " + this.getDecisionOp());
    }
    else{
      System.out.println(indent + "Value: " + this.getValue());
    }
    if(this.getLeft() != null){
      this.getLeft().printPreorder(indent + "  ");
    }
    if(this.getRight() != null){
      this.getRight().printPreorder(indent + "  ");
    }
  }

}

class DecisionTree{

  //The root of the tree
  private DecisionTreeNode root;

  /*Constructor for the DecisionTree class
   *Initializes the root to a new DecisionTreeNode
   */
  public DecisionTree(){
    root = new DecisionTreeNode();
  }

  /*randomly prunes the tree until it reaches a given number of failures,
   *meaning it fails to remove a node numFailures times. A node is determined
   *to be ok to removed if the removal results in a drop in the originalAccuracy
   *less than the given maxAccuracyDrop
   */
  public void randomPrune(int numFailures, double originalAccuracy,
  double maxAccuracyDrop, String[] possibleValues, String[][] data){
    DecisionTreeNode randomNode = root.getRandomNode();
    boolean failure = true;
    if(randomNode.getParent() != null){
      DecisionTreeNode randomParent = randomNode.getParent();
      for(int i = 0; i < possibleValues.length; i++){
        DecisionTreeNode tempNode = new DecisionTreeNode(-1, "N/A", possibleValues[i], randomNode.getParent(), randomNode.getDirection());
        if(randomNode.getDirection().equals("left")){
          randomParent.left = tempNode;
          if(testData(data) >= originalAccuracy - maxAccuracyDrop){
            failure = false;
            break;
          }
          else{
            randomParent.left = randomNode;
          }
        }
        else{
          randomParent.right = tempNode;
          if(testData(data) >= originalAccuracy - maxAccuracyDrop){
            failure = false;
            break;
          }
          else{
            randomParent.right = randomNode;
          }
        }
      }
      if(failure && numFailures >= 1){
        randomPrune(numFailures - 1, originalAccuracy, maxAccuracyDrop, possibleValues, data);
      }
      else if(!failure){
        randomPrune(numFailures, originalAccuracy, maxAccuracyDrop, possibleValues, data);
      }
    }
    else{
      for(int i = 0; i < possibleValues.length; i++){
        DecisionTreeNode tempNode = new DecisionTreeNode(-1, "N/A", possibleValues[i], null, randomNode.getDirection());
        root = tempNode;
        if(testData(data) >= originalAccuracy - maxAccuracyDrop){
          failure = false;
          break;
        }
        else{
          root = randomNode;
          randomPrune(numFailures - 1, originalAccuracy, maxAccuracyDrop, possibleValues, data);
        }
      }
    }
  }

  /*Tests the given dataset against the tree to determine the accuracy of the
   *decision tree. Returns the accuracy as a decimal value
   */
  public double testData(String[][] data){
    int numAccurate = 0;
    int total = data.length;
    for(int i = 0; i < total; i++){
      numAccurate = numAccurate + root.checkData(data[i]);
    }
    return (numAccurate + 0.0)/(total + 0.0);
  }

  /*Constructs a c4.5 decision tree based on the given data
   */
  public DecisionTree buildDecisionTree(String[][] data, DecisionTreeNode parent, String direction){
    if(data.length == 0){
      root = new DecisionTreeNode(-1, "N/A", "FAILURE", parent, direction);
    }
    else{
      ArrayList<String[]> temp = new ArrayList<String[]>();
      for(int i = 0; i < data.length; i++){
        if(temp.size() != 0){
          boolean found = false;
          for(int j = 0; j < temp.size(); j++){
            if(data[i][data[i].length - 1].equals(temp.get(j)[0])){
              temp.get(j)[1] = Integer.parseInt(temp.get(j)[1]) + 1 + "";
              found = true;
            }
          }
          if(!found){
            String[] tempArray = {data[i][data[i].length-1], "1"};
            temp.add(tempArray);
          }
        }
        else{
          String[] tempArray = {data[i][data[i].length-1], "1"};
          temp.add(tempArray);
        }
      }
      if(temp.size() == 1){
        root = new DecisionTreeNode(-1, "N/A", temp.get(0)[0], parent, direction);
      }
      else{
        double highestRatio = 0.0;
        int highestAttribute = 0;
        for(int i = 0; i < data[0].length - 1; i++){
          double tempRatio = gainRatio(data, i);
          if(tempRatio > highestRatio){
            highestAttribute = i;
            highestRatio = tempRatio;
          }
        }
        if(highestRatio == 0){
          int highestIndex = 0;
          for(int i = 0; i < temp.size(); i++){
            if(Double.parseDouble(temp.get(highestIndex)[1]) < Double.parseDouble(temp.get(i)[1])){
              highestIndex = i;
            }
          }
          root = new DecisionTreeNode(-1, "N/A", temp.get(highestIndex)[0], parent, direction);
        }
        else{
          String[][][] splitArray = splitOnAttribute(data, highestAttribute);
          root = new DecisionTreeNode(Integer.parseInt(splitArray[2][0][0]), splitArray[2][1][0], "N/A", parent, direction);
          DecisionTree tempLeft = new DecisionTree();
          root.insertLeft(tempLeft.buildDecisionTree(splitArray[0], root, "left").getRoot());
          DecisionTree tempRight = new DecisionTree();
          root.insertRight(tempRight.buildDecisionTree(splitArray[1], root, "right").getRoot());
        }
      }
    }
    return this;
  }

  /*Splits the given dataset into two datasets on the given attribute
   */
  public String[][][] splitOnAttribute(String[][] data, int attribute){
    if(isDouble(data[0][attribute])){
      TreeSet<Double> temp = new TreeSet<Double>();
      for(int i = 0; i < data.length; i++){
        temp.add(Double.parseDouble(data[i][attribute]));
      }
      Double[] tempArray = temp.toArray(new Double[0]);
      double highestRatio = 0.0;
      double bestPartition = tempArray[0];
      ArrayList<ArrayList<String[]>> partitions = new ArrayList<ArrayList<String[]>>();
      partitions.add(new ArrayList<String[]>());
      partitions.add(new ArrayList<String[]>());
      for(int i = 0; i < tempArray.length; i++){
        partitions.add(new ArrayList<String[]>());
        partitions.add(new ArrayList<String[]>());
        for(int j = 0; j < data.length; j++){
          if(Double.parseDouble(data[j][attribute]) < tempArray[i]){
            partitions.get(0).add(data[j]);
          }
          else{
            partitions.get(1).add(data[j]);
          }
        }
        if(highestRatio < gainRatio((String[][]) partitions.get(0).toArray(new String[0][0]), attribute)){
          highestRatio = gainRatio((String[][]) partitions.get(0).toArray(new String[0][0]), attribute);
          bestPartition = tempArray[i];
        }
        else if(highestRatio < gainRatio((String[][]) partitions.get(1).toArray(new String[0][0]), attribute)){
          highestRatio = gainRatio((String[][]) partitions.get(1).toArray(new String[0][0]), attribute);
          bestPartition = tempArray[i];
        }
        else if(tempArray.length == 2 && tempArray[i] > bestPartition){
          bestPartition = tempArray[i];
        }
        partitions.remove(0);
        partitions.remove(0);
      }
      partitions.add(new ArrayList<String[]>());
      partitions.add(new ArrayList<String[]>());
      for(int j = 0; j < data.length; j++){
        if(Double.parseDouble(data[j][attribute]) < bestPartition){
          partitions.get(0).add(data[j]);
        }
        else{
          partitions.get(1).add(data[j]);
        }
      }
      String[][][] ret = new String[3][][];
      ret[0] = (String[][]) partitions.get(0).toArray(new String[0][0]);
      ret[1] = (String[][]) partitions.get(1).toArray(new String[0][0]);
      ret[2] = new String[][]{{"" + attribute},{"<" + bestPartition}};
      return ret;
    }
    else{
      TreeSet<String> temp = new TreeSet<String>();
      for(int i = 0; i < data.length; i++){
        temp.add(data[i][attribute]);
      }
      String[] tempArray = temp.toArray(new String[0]);
      double highestRatio = 0.0;
      String bestPartition = tempArray[0];
      ArrayList<ArrayList<String[]>> partitions = new ArrayList<ArrayList<String[]>>();
      partitions.add(new ArrayList<String[]>());
      partitions.add(new ArrayList<String[]>());
      for(int i = 0; i < tempArray.length; i++){
        partitions.add(new ArrayList<String[]>());
        partitions.add(new ArrayList<String[]>());
        for(int j = 0; j < data.length; j++){
          if(data[j][attribute].equals(tempArray[i])){
            partitions.get(0).add(data[j]);
          }
          else{
            partitions.get(1).add(data[j]);
          }
        }
        if(highestRatio < gainRatio((String[][]) partitions.get(0).toArray(new String[0][0]), attribute)){
          highestRatio = gainRatio((String[][]) partitions.get(0).toArray(new String[0][0]), attribute);
          bestPartition = tempArray[i];
        }
        else if(highestRatio < gainRatio((String[][]) partitions.get(1).toArray(new String[0][0]), attribute)){
          highestRatio = gainRatio((String[][]) partitions.get(1).toArray(new String[0][0]), attribute);
          bestPartition = tempArray[i];
        }
        partitions.remove(0);
        partitions.remove(0);
      }
      partitions.add(new ArrayList<String[]>());
      partitions.add(new ArrayList<String[]>());
      for(int j = 0; j < data.length; j++){
        if(data[j][attribute].equals(bestPartition)){
          partitions.get(0).add(data[j]);
        }
        else{
          partitions.get(1).add(data[j]);
        }
      }
      String[][][] ret = new String[3][][];
      ret[0] = (String[][]) partitions.get(0).toArray(new String[0][0]);
      ret[1] = (String[][]) partitions.get(1).toArray(new String[0][0]);
      ret[2] = new String[][]{{"" + attribute},{"="+bestPartition}};
      return ret;
    }
  }

  /*Calculates the Gain Ratio for the given dataset
   */
  public double gainRatio(String[][] data, int attribute){
    return gain(data, attribute) / splitInfo(data, attribute);
  }

  /*Calculates the Gain for the given dataset
   *Gain is defined as
   */
  public static double gain(String[][] data, int attribute){
    return info(data) - info(data, attribute);
  }

  /*Calculates the Info for the given dataset
   *Info is defined as I(P)
   *where P = (\C1\/\T\, \C2\/\T\, ... ,\Ck\/\T\)
   *C is the classes which the dataset is partitioned into
   *and I(P) = -(p1*log(p1) + p2*log(p2) + ... + pn*log(pn))
   */
  public static double info(String[][] data){
    ArrayList<String[]> temp = new ArrayList<String[]>();
    for(int i = 0; i < data.length; i++){
      if(temp.size() != 0){
        boolean found = false;
        for(int j = 0; j < temp.size(); j++){
          if(data[i][data[i].length - 1].equals(temp.get(j)[0])){
            temp.get(j)[1] = Integer.parseInt(temp.get(j)[1]) + 1 + "";
            found = true;
          }
        }
        if(!found){
          String[] tempArray = {data[i][data[i].length-1], "1"};
          temp.add(tempArray);
        }
      }
      else{
        String[] tempArray = {data[i][data[i].length-1], "1"};
        temp.add(tempArray);
      }
    }
    double P[] = new double[temp.size()];
    for(int i = 0; i < temp.size(); i++){
      P[i]=(Double.parseDouble(temp.get(i)[1]) / data.length);
    }
    Double ret = 0.0;
    for(int i = 0; i < P.length; i++){
      //Since Java doesn't have an easy way to calculate log base 2
      //I use log base e and do some logarithm calculations
      //to find the log base 2
      ret = ret + P[i]*Math.log(P[i])/Math.log(2);
    }
    return -1.0*ret;
  }

  /*Calculates the info for the given dataset for the given attribute
   */
  public static double info(String[][] data, int attribute){

    ArrayList<String[]> temp = new ArrayList<String[]>();
    for(int i = 0; i < data.length; i++){
      if(temp.size() != 0){
        boolean found = false;
        for(int j = 0; j < temp.size(); j++){
          if(data[i][attribute].equals(temp.get(j)[0])){
            temp.get(j)[1] = Integer.parseInt(temp.get(j)[1]) + 1 + "";
            found = true;
          }
        }
        if(!found){
          String[] tempArray = {data[i][attribute], "1"};
          temp.add(tempArray);
        }
      }
      else{
        String[] tempArray = {data[i][attribute], "1"};
        temp.add(tempArray);
      }
    }

    double sum = 0;
    for(int i = 0; i < temp.size(); i++){
      String Ti = temp.get(i)[0];
      String[][] tempArray = new String[Integer.parseInt(temp.get(i)[1])][2];
      int count = 0;
      for(int j = 0; j < data.length; j++){
        if(data[j][attribute].equals(Ti)){
          tempArray[count][0] = Ti;
          tempArray[count][1] = data[j][data[j].length - 1];
          count++;
        }
      }
      sum = sum + (count + 0.0) / (data.length) * info(tempArray);
    }
    return sum;
  }

  /*Calculates the Split Info for the given dataset for the given attribute
   */
  public static double splitInfo(String[][] data, int attribute){

    ArrayList<String[]> temp = new ArrayList<String[]>();
    for(int i = 0; i < data.length; i++){
      if(temp.size() != 0){
        boolean found = false;
        for(int j = 0; j < temp.size(); j++){
          if(data[i][attribute].equals(temp.get(j)[0])){
            temp.get(j)[1] = Integer.parseInt(temp.get(j)[1]) + 1 + "";
            found = true;
          }
        }
        if(!found){
          String[] tempArray = {data[i][attribute], "1"};
          temp.add(tempArray);
        }
      }
      else{
        String[] tempArray = {data[i][attribute], "1"};
        temp.add(tempArray);
      }
    }

    double ret = 0.0;
    for(int i = 0; i < temp.size(); i++){
      ret = ret - (Double.parseDouble(temp.get(i)[1])/data.length)*
        Math.log((Double.parseDouble(temp.get(i)[1])/data.length))/Math.log(2);
    }

    return ret;
  }

  /*Returns true if the given String s is an integer in
   *the given base radix.
   */
  public static boolean isInteger(String s, int radix) {
    if(s.isEmpty()) return false;
    for(int i = 0; i < s.length(); i++) {
        if(i == 0 && s.charAt(i) == '-') {
            if(s.length() == 1) return false;
            else continue;
        }
        if(Character.digit(s.charAt(i),radix) < 0) return false;
    }
    return true;
  }

  /*Returns true if the given String s is a double
   */
  public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

  //Basic getter and setter for the root node
  public DecisionTreeNode getRoot(){ return root; }
  public void setRoot(DecisionTreeNode newRoot){ root = newRoot; }

}
