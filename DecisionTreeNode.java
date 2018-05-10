import java.util.ArrayList;
import java.util.TreeSet;
/*This is an implementation of a Tree node
 *used for implementing a C4.5 tree building algorithm
 *@author Michael Read
 *@version 1.0
 */
class DecisionTreeNode{

  //The left child node
  private DecisionTreeNode left;
  //the right child node
  private DecisionTreeNode right;

  //The field in the data which is looked at to make a decision
  private String decisionField;
  //The operation on decisionField which decides which child node to visit
  private String decisionOp;
  //Should this be a leaf node, this is the value which this node represents
  private String value;

  public DecisionTreeNode(){
    this.decisionField = "";
    this.decisionOp = "";
    this.value = "";
    this.left = null;
    this.right = null;
  }

  public DecisionTreeNode(String decisionField, String decisionOp, String value){
    this.decisionField = decisionField;
    this.decisionOp = decisionOp;
    this.value = value;
    this.left = new DecisionTreeNode();
    this.right = new DecisionTreeNode();
  }

  public DecisionTreeNode getLeft()          { return left;          }
  public DecisionTreeNode getRight()         { return right;         }
  public String           getDecisionField() { return decisionField; }
  public String           getDecisionOp()    { return decisionOp;    }
  public String           getValue()         { return value;         }

  public void insertLeft(DecisionTreeNode toInsert){
    if(left == null){
      left = toInsert;
    }
    else{
      left.insertLeft(toInsert);
    }
  }

  public void insertRight(DecisionTreeNode toInsert){
    if(right == null){
      right = toInsert;
    }
    else{
      right.insertRight(toInsert);
    }
  }

  public void printPreorder() {
    String indent = "";
    if(this.getValue().equals("N/A")){
      System.out.println(indent + this.getDecisionField() + " " + this.getDecisionOp());
    }
    else{
      System.out.println(indent + this.getValue());
    }
    if(this.getLeft() != null){
      this.getLeft().printPreorder("  ");
    }
    if(this.getRight() != null){
      this.getRight().printPreorder("  ");
    }
  }

  private void printPreorder(String indent){
    if(this.getValue().equals("N/A")){
      System.out.println(indent + this.getDecisionField() + " " + this.getDecisionOp());
    }
    else{
      System.out.println(indent + this.getValue());
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

  private DecisionTreeNode root;
  private int depth;

  public DecisionTree(int depth){
    root = new DecisionTreeNode();
    this.depth = depth;
    System.out.println("Depth: " + depth);
  }

  public DecisionTree buildDecisionTree(String[][] data){
    if(data.length == 0){
      root = new DecisionTreeNode("N/A", "N/A", "FAILURE");
      System.out.println("Found a failure");
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
        root = new DecisionTreeNode("N/A", "N/A", temp.get(0)[0]);
        System.out.println("No further splitting required");
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
        //System.out.println(highestAttribute);
        //System.out.println(highestRatio);
        if(highestRatio == 0){
          int highestIndex = 0;
          for(int i = 0; i < temp.size(); i++){
            if(Double.parseDouble(temp.get(highestIndex)[1]) < Double.parseDouble(temp.get(i)[1])){
              highestIndex = i;
            }
          }
          root = new DecisionTreeNode("N/A", "N/A", temp.get(highestIndex)[0]);
          System.out.println("No further splitting possible");
        }
        else{
          String[][][] splitArray = splitOnAttribute(data, highestAttribute);
          root = new DecisionTreeNode(splitArray[2][0][0], splitArray[2][1][0], "N/A");
          //System.out.println(splitArray[2][0][0]);
          //System.out.println(splitArray[2][1][0]);
          System.out.println("Creating Left...");
          DecisionTree tempLeft = new DecisionTree(depth + 1);
          root.insertLeft(tempLeft.buildDecisionTree(splitArray[0]).getRoot());
          System.out.println("Creating Right...");
          DecisionTree tempRight = new DecisionTree(depth + 1);
          root.insertRight(tempRight.buildDecisionTree(splitArray[1]).getRoot());
        }
      }
    }
    return this;
  }

  public String[][][] splitOnAttribute(String[][] data, int attribute){
    System.out.println("Splitting on " + attribute);
    if(isDouble(data[0][attribute])){
      TreeSet<Double> temp = new TreeSet<Double>();
      for(int i = 0; i < data.length; i++){
        temp.add(Double.parseDouble(data[i][attribute]));
      }
      Double[] tempArray = temp.toArray(new Double[0]);
      double highestRatio = 0.0;
      double bestPartition = 0.0;
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
      System.out.println("---------------");
      for(int i = 0; i < ret[0].length; i++){
        for(int j = 0; j < ret[0][i].length; j++){
          System.out.print(ret[0][i][j] + " ");
        }
        System.out.println();
      }
      System.out.println("---------------");
      ret[1] = (String[][]) partitions.get(1).toArray(new String[0][0]);
      for(int i = 0; i < ret[1].length; i++){
        for(int j = 0; j < ret[1][i].length; j++){
          System.out.print(ret[1][i][j] + " ");
        }
        System.out.println();
      }
      System.out.println("---------------");
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
      System.out.println("---------------");
      for(int i = 0; i < ret[0].length; i++){
        for(int j = 0; j < ret[0][i].length; j++){
          System.out.print(ret[0][i][j] + " ");
        }
        System.out.println();
      }
      System.out.println("---------------");
      ret[1] = (String[][]) partitions.get(1).toArray(new String[0][0]);
      for(int i = 0; i < ret[1].length; i++){
        for(int j = 0; j < ret[1][i].length; j++){
          System.out.print(ret[1][i][j] + " ");
        }
        System.out.println();
      }
      System.out.println("---------------");
      ret[2] = new String[][]{{"" + attribute},{"="+bestPartition}};
      return ret;
    }
  }

  /*Calculates the Gain Ratio for the given dataset
   *Gain Ratio is defined as
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

  /*Calculates the
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

  /*Calculates the Split Info
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

  boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

  public DecisionTreeNode getRoot(){ return root; }
  public void setRoot(DecisionTreeNode newRoot){ root = newRoot; }

}
