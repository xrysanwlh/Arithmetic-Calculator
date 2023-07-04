package hw1;

public class tree {
    node root;

    /*---- traversal function ----*/
    public void traversal(node node, StringBuffer str) {
        String temp;

        if (node != root) {
            temp = node.hashCode() + " [label=\"" + node.value + "\"]" + "\n";
            str.append(temp);
        }

        if (node.left != null) {
            temp = node.hashCode() + "--" + node.left.hashCode() + "\n";
            str.append(temp);
            traversal(node.left, str);
        }

        if (node.right != null) {
            temp = node.hashCode() + "--" + node.right.hashCode() + "\n";
            str.append(temp);
            traversal(node.right, str);
        }

    }

}

