import java.util.Scanner;
import java.util.TreeSet;
import java.util.ArrayList;
/*Implements the c4.5 algorithm to construct
 *a binary decision tree using a set of
 *training data
 *@author Michael Read
 *@version 1.0
 */
class TreeBuilder{

  /*Reads in a formatted input, and builds and prunes a c4.5 binary decision tree
   */
  public static void main(String[] args){
    //Reads in the input
    Scanner input = new Scanner(System.in);
    String tempData = "";
    int count = 0;
    while(input.hasNextLine()){
      count++;
      tempData = tempData + input.nextLine() + ";";
    }
    //Turn the input into a useable array
    String[] tempArray = tempData.split(";");
    String[][] data = new String[tempArray.length][tempArray.length];
    for(int i = 0; i < tempArray.length; i++){
      data[i] = tempArray[i].split(",");
    }
    //Build the decision tree
    DecisionTree tree = new DecisionTree();
    tree.buildDecisionTree(data, null, "N/A");
    //Output the info about the tree before pruning
    System.out.println("Original Tree\n");
    tree.getRoot().printPreorder();
    System.out.println("\nSize: " + tree.getRoot().size());
    System.out.println("\nAccuracy: " + tree.testData(data));
    //Find all possible values the target value can have
    //(Helpful for pruning)
    TreeSet<String> possibleValues = new TreeSet<String>();
    for(int i = 0; i < data.length; i++){
      possibleValues.add(data[i][data[i].length-1]);
    }
    String[] possibleValuesArray = possibleValues.toArray(new String[0]);
    //Prune the tree
    tree.randomPrune(5, tree.testData(data), .05, possibleValuesArray, data);
    //Output pruned tree info
    System.out.println("Pruned Tree\n");
    tree.getRoot().printPreorder();
    System.out.println("\nPruned Tree Size: " + tree.getRoot().size());
    System.out.println("\nPruned Tree Accuracy: " + tree.testData(data));
  }
}
