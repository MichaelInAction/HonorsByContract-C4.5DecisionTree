/*This is an implementation of a Tree node
 *used for implementing a C4.5 tree building algorithm
 *@author Michael Read
 *@version 1.0
 */
class TreeNode{

  //The left child node
  private TreeNode left;
  //the right child node
  private TreeNode right;

  //The field in the data which is looked at to make a decision
  private String decisionField;
  //The operation on decisionField which decides which child node to visit
  private String decisionOp;
  //Should this be a leaf node, this is the value which this node represents
  private String value;

  public void TreeNode(String decisionField, String decisionOp, String value){
    this.decisionField = decisionField;
    this.decisionOp = decisionOp;
    this.value = value;
  }

  public TreeNode getLeft()          { return left;          }
  public TreeNode getRight()         { return right;         }
  public String   getDecisionField() { return decisionField; }
  public String   getDecisionOp()    { return decisionOp;    }
  public String   getValue()         { return value;         }

  public void insertLeft(TreeNode toInsert){
    if(left == null){
      left = toInsert;
    }
    left.insertLeft(toInsert);
  }

  public void insertRight(TreeNode toInsert){
    if(right == null){
      right = toInsert;
    }
    right.insertRight(toInsert);
  }

}

class Tree{

  private TreeNode root;

  private int numNodes;
  private int numYes;

  public Tree(){
    root = new TreeNode();
  }

  public double percentageYes(){
    return numYes/numNodes;
  }

  public TreeNode getRoot(){ return root; }
  public void setRoot(TreeNode newRoot){ root = newRoot; }

}
