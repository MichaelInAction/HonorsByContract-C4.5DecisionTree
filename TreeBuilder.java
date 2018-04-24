import java.util.Scanner;
/*Implements the c4.5 algorithm to construct
 *a binary decision tree using a set of
 *training data
 *@author Michael Read
 *@version 1.0
 */
class TreeBuilder{

  public static void main(String[] args){
    Scanner input = new Scanner(System.in);
    String tempData = "";
    int count = 0;
    while(input.hasNextLine()){
      count++;
      tempData = tempData + input.nextLine() + ";";
    }
    String[] tempArray = tempData.split(";");
    String[][] data = new String[tempArray.length][tempArray.length];
    for(int i = 0; i < tempArray.length; i++){
      data[i] = tempArray[i].split(",");
    }
    for(int i = 0; i < data.length; i++){
      for(int j = 0; j < data[i].length; j++){
        System.out.println(data[i][j]);
      }
      System.out.println("-----------");
    }
    System.out.println(data[0].length);
    System.out.println(data.length);

    Tree tree = new Tree();

  }

  /*Returns true if the given
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
}
