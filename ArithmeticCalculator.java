/*PROJECT1 JAVA, Noli Chrysoula, 2780*/

package hw1;

import java.util.regex.*;

public class ArithmeticCalculator {
    //global-shared memory:
    String mathExpression;
    tree tree = new tree();  //create object tree
    String postOrderExpr = "";


    /*----[CONSTRUCTOR]: ArithmeticCalculator ----*/
    public ArithmeticCalculator(String initMathExpression) {
        int numOfOpenParenthesis = 0;
        int numOfCloseParenthesis = 0;

        mathExpression = initMathExpression.replaceAll("\\s+", ""); //remove spaces between chars

        /* ----- [VALIDITY CHECK] -----*/

        /*CHECK[1]: expansion */
        Pattern p = Pattern.compile("\\\\"); // definition of \ is \\\\
        Matcher m = p.matcher(mathExpression);
        while (m.find()) {
            if(!isInteger(m.end())){ //if double number after \(operator), exit
                System.out.println("[ERROR] Invalid expansion expression");
                System.exit(0);
            }

            String isNumAfterOperator = String.valueOf(mathExpression.charAt(m.end() + 1));

            //if \(non-operator) or \(operator)(non-number) or \(operator)(int number < 1) ---> error (!)
            if (!isOperator(mathExpression.charAt(m.end())) || ((!isNumber(isNumAfterOperator) && (Integer.parseInt(isNumAfterOperator) <= 1)))) {
                System.out.println("[ERROR] Invalid expansion expression");
                System.exit(0);
            }
            mathExpression = expandExpression(m.end() - 2);
            p = Pattern.compile("\\\\");
            m = p.matcher(mathExpression);
        }

        /*CHECK[2]: right sum of parenthesis */
        for (int i = 0; i < mathExpression.length(); i++) {
            if (mathExpression.charAt(i) == '(')
                numOfOpenParenthesis++;
            else if (mathExpression.charAt(i) == ')')
                numOfCloseParenthesis++;
        }
        if (numOfOpenParenthesis > numOfCloseParenthesis) {
            System.out.println("[ERROR] Not closing opened parenthesis");
            System.exit(0);
        }
        else if (numOfOpenParenthesis < numOfCloseParenthesis) {
            System.out.println("[ERROR] Closing unopened parenthesis");
            System.exit(0);
        }

        /*CHECK[3]: only valid characters */
        for (int i = 0; i < mathExpression.length(); i++) {
            if (!(isValidCharacter(mathExpression.charAt(i)))) {
                System.out.println("[ERROR] Invalid character");
                System.exit(0);
            }
        }

        /*CHECK[4]: before or after an operator, not an operator again */
        for (int i = 0; i < (mathExpression.length() - 1); i++) {
            if (isOperator(mathExpression.charAt(i)) && isOperator(mathExpression.charAt(i + 1))) {
                System.out.println("[ERROR] Two consecutive operands");
                System.exit(0);
            }
        }

        /*CHECK[5]: before an operator, not '(' or after an operator, not an ')' */
        for (int i = 0; i < (mathExpression.length() - 1); i++) {
            if (mathExpression.charAt(i) == '(' && isOperator(mathExpression.charAt(i + 1))) {
                System.out.println("[ERROR] Operand appears after opening parenthesis");
                System.exit(0);
            }
            if (isOperator(mathExpression.charAt(i)) && mathExpression.charAt(i + 1) == ')') {
                System.out.println("[ERROR] Operand appears before closing parenthesis");
                System.exit(0);
            }
        }

        tree.root = findNextLastOperator(mathExpression);  //create tree

    }

    /*----isInteger function----*/
    public boolean isInteger (int indexOfBackslash){  //checks if the number after \ is int, if double return false
        int i = indexOfBackslash + 2; //format: /(operator)(int number)
        while(!String.valueOf(mathExpression.charAt(i)).equals(".")) {
            if (isOperator(mathExpression.charAt(i))) {
                return(true);
            }
            i = i + 1;
        }
        return(false);
    }

    /*----doTheMaths function----*/
    public double doTheMaths(String operator, double n1, double n2) {  //depends on the operator
        switch (operator) {
            case "+":
                return (n1 + n2);
            case "-":
                return (n1 - n2);
            case "x":
                return (n1 * n2);
            case "*":
                return (n1 * n2);
            case "/":
                return (n1 / n2);
            case "^":
                return (Math.pow(n1, n2));
            default:
                return (0);
        }
    }

    /*----helpMeCalculate function----*/
    public double helpMeCalculate(node current) { //recursively calculate expression from tree

        if (current != null) {
            if (!isOperator(current.value.charAt(0))) {          //if number/leaf
                return (Double.parseDouble(current.value));
            } else {                                            //if operator call helpMeCalculate for his children
                double lefty = helpMeCalculate(current.left);
                double righty = helpMeCalculate(current.right);
                double res = doTheMaths(current.value, lefty, righty); //do the evaluation
                return (res);
            }
        }

        return (0);
    }

    /*----calculate function----*/
    public double calculate() {  //calculate the result of the math expression

        if (tree.root != null) {
            return (helpMeCalculate(tree.root));
        }

        return (0);
    }

    /*----toDotString function----*/
    public String toDotString() {
        StringBuffer DotBuff = new StringBuffer("graph ArithmeticExpressionTree {\n");
        node current = tree.root;

        DotBuff.append(current.hashCode() + " [label=\" " + tree.root.value + "\"" + "]\n");
        tree.traversal(current, DotBuff);
        DotBuff.append("}");
        return (DotBuff.toString());

    }

    /*----postOrder function----*/
    public String postOrder(node node) {  //post order traversal of the tree (recursively)

        if(node == null){
            return(postOrderExpr);
        }

        if (isOperator(node.value.charAt(0)) && node != tree.root) { //if operator and no the root open parenthesis
            postOrderExpr = postOrderExpr.concat("(");
        }
        if(!isOperator(node.value.charAt(0))){  //if number/leaf open parenthesis , value , close parenthesis
            postOrderExpr = postOrderExpr + "(" + node.value + ")";
            return(postOrderExpr);
        }
        //if operator
        postOrder(node.left);
        postOrder(node.right);

        if(node == tree.root){  //no parenthesis for the root
            postOrderExpr = postOrderExpr.concat(node.value);
            return(postOrderExpr);
        }

        postOrderExpr = postOrderExpr.concat(node.value);  //if operator and no root: value

        if (isOperator(node.value.charAt(0))) {
            postOrderExpr = postOrderExpr.concat(")");     //and close parenthesis
        }

        return(postOrderExpr);
    }

    /*----toString function----*/
    public String toString () {
        String result = postOrder(tree.root);
        return (result);
    }

    /*----calculatePriority function----*/
    public int calculatePriority ( char operator){  //in order to find the next last operator

        switch (operator) {
            case '+':  return (1);
            case '-':  return (1);
            case '*':  return (2);
            case 'x':  return (2);
            case '/':  return (2);
            case '^':  return (3);
            default:   return (0);
        }
    }

    /*----findNextLastOperator function----*/
    public node findNextLastOperator (String subtree){  //find the operator who must be inserted at tree first, second etc
        int lowestPriority = 4;  //max
        int numOfOpenedParenthesis = 0;
        int numOfClosedParenthesis = 0;
        int lastOperatorIndex = -1;  //initially
        int flag = 0;


        if (isNumber(subtree)) {  //if leaf
            node newnode = new node(subtree);
            return (newnode);
        }

        for (int i = 0; i < subtree.length(); i++) { //ignore-skip parenthesis, parenthesis->priority->down in the tree
            if (subtree.charAt(i) == '(') {
                flag = 0;
                numOfOpenedParenthesis++;
            }
            if (subtree.charAt(i) == ')') {
                flag = 0;
                numOfClosedParenthesis++;
            }

            if (numOfOpenedParenthesis == numOfClosedParenthesis) {
                flag = 1;  //now the if statement may be true
                numOfOpenedParenthesis = 0;
                numOfClosedParenthesis = 0;
            }
                                                   // initially 4 > anything
            if (isOperator(subtree.charAt(i)) && (lowestPriority >= calculatePriority(subtree.charAt(i))) && flag == 1) {
                flag = 0;
                lowestPriority = calculatePriority(subtree.charAt(i));  //refresh the lowest
                lastOperatorIndex = i;   //store this index
            }
        }

        if (flag == 1 && lastOperatorIndex == -1) {  //if not anything of the upper, inside parenthesis
            node helpNode = findNextLastOperator(subtree.substring(1, (subtree.length() - 1)));  //ignore parenthesis, call again
            return (helpNode);
        }

        node newnode = new node(String.valueOf(subtree.charAt(lastOperatorIndex))); //create object and insert

        //split in two expressions
        String leftSubstring = subtree.substring(0, lastOperatorIndex);
        String rightSubstring = subtree.substring(lastOperatorIndex + 1);

        newnode.left = findNextLastOperator(leftSubstring);   //the next one node's left child
        newnode.right = findNextLastOperator(rightSubstring); //same

        return (newnode);

    }

    /*----isOperator function----*/
    public boolean isOperator ( char isReallyOperator){
        switch (isReallyOperator) {
            case '+':
                break;
            case '-':
                break;
            case '/':
                break;
            case '^':
                break;
            case '*':
                break;
            case 'x':
                break;
            default:
                return false;
        }
        return true;
    }

    /*----isNumber function----*/
    public boolean isNumber(String toBeChecked){  //double or int
        boolean numeric;

        numeric = toBeChecked.matches("-?\\d+(\\.\\d+)?");
        return(numeric);

    }

    /*----isValidCharacter function----*/
    public boolean isValidCharacter ( char toBeChecked){
        String isCharNumeric = String.valueOf(toBeChecked);

        if (isNumber(isCharNumeric)) {
            return true;
        }
        switch (toBeChecked) {
            case '(':
                break;
            case ')':
                break;
            case '+':
                break;
            case '-':
                break;
            case '*':
                break;
            case 'x':
                break;
            case '^':
                break;
            case '.':
                break;
            case ' ':
                break;
            case '/':
                break;
            default:
                return false;
        }
        return true;
    }

    /*----expandExpression function----*/
    public String expandExpression ( int indexOfBackslash){
        int numOfOpenParenthesis = 0;
        int numOfClosedParenthesis = 0;
        int beginIndex = 0;
        int times = 0;
        String operation;
        String toExpand = "";
        String extendedString = "";
        String afterExpansionString = "";
        String withParenthesis = "";


        for (int i = indexOfBackslash; i > 0; i--) { //from \'s index to lower indexes
            if (mathExpression.charAt(i) == ')')
                numOfClosedParenthesis++;
            if (mathExpression.charAt(i) == '(')
                numOfOpenParenthesis++;
            if (numOfOpenParenthesis == numOfClosedParenthesis) {  //the start of expression to expand
                beginIndex = i;
                break;
            }
        }

        //expansion expression has priority, put parenthesis in the beginning
        withParenthesis = mathExpression.substring(0, beginIndex) + "(" + mathExpression.substring(beginIndex, indexOfBackslash + 1);

        toExpand = mathExpression.substring(beginIndex, indexOfBackslash + 1);
        String[] splittedStrings = mathExpression.split("\\\\", 2);  //split in the first \'s index
        operation = String.valueOf(splittedStrings[1].charAt(0));        //save operator
        times = Character.getNumericValue(splittedStrings[1].charAt(1)); //save times

        for (int i = 1; i < times; i++) {
            extendedString = extendedString.concat(operation).concat(toExpand);
        }

        extendedString = extendedString.concat(")"); //close parenthesis which opened for expansion's priority
        splittedStrings[1] = splittedStrings[1].substring(2);  //ignore operation and times
        afterExpansionString = afterExpansionString.concat(withParenthesis).concat(extendedString).concat(splittedStrings[1]);
        return (afterExpansionString);

    }

    /*----main function----*/
    public static void main (String[]args){
        hw1.ArithmeticCalculator mainMathExpression;
        double calculateResult = 0;
        String options = "";

        java.util.Scanner sc = new java.util.Scanner(System.in);
        System.out.print("Expression: ");
        String line = sc.nextLine();

        mainMathExpression = new ArithmeticCalculator(line);  //create new object

        if(sc.hasNextLine()) {
            options = sc.nextLine();
        }

        //menu:
        for (int i = 0; i < options.length(); i++) {
            if (options.charAt(i) == 'd') {
                System.out.println("\n" + mainMathExpression.toDotString());
            }
            if (options.charAt(i) == 's') {
                System.out.println("\nPostfix: " + mainMathExpression.toString());
            }
            if (options.charAt(i) == 'c') {
                calculateResult = mainMathExpression.calculate();
                System.out.println("\nResult: " + calculateResult);
            }
        }

    }

    //end of class ArithmeticCalculator//
}
