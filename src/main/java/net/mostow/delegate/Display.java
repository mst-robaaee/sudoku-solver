package net.mostow.delegate;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class Display {
    private static final String horizontalLine = " ----------------------- ";
    private static final String matrixSeprator = "  >>>  ";

    public static void printMatrix(ArrayList<ArrayList<Integer>> matrix){
        for (int i = 0; i < matrix.size(); i++) {
            if(i%3 == 0){
                System.out.println(horizontalLine);
            }
            printRow(matrix, i);
            System.out.println("|");
        }
        System.out.println(horizontalLine);
    }
    public void printSolvingMatrices(ArrayList<ArrayList<Integer>> unsolvedMatrix, ArrayList<ArrayList<Integer>> solvedMatrix) {
        System.out.println("");
        for (int i = 0; i < unsolvedMatrix.size(); i++) {
            if(i%3 == 0){
                System.out.print(horizontalLine);
                System.out.print(matrixSeprator);
                System.out.println(horizontalLine);
            }
            printRow(unsolvedMatrix, i);
            System.out.print("|");
            System.out.print(matrixSeprator);
            printRow(solvedMatrix, i);
            System.out.println("|");
        }
        System.out.print(horizontalLine);
        System.out.print(matrixSeprator);
        System.out.println(horizontalLine);
    }

    static private void printRow(ArrayList<ArrayList<Integer>> unsolvedMatrix, int i) {
        for (int j = 0; j < unsolvedMatrix.get(i).size(); j++) {
            if(j%3 == 0){
                System.out.print("| ");
            }
            Integer number = unsolvedMatrix.get(i).get(j);
            System.out.print(String.format("%s ", number==0 ? " " : number));
        }
    }
}
