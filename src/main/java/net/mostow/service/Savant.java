package net.mostow.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.mostow.delegate.Display;
import net.mostow.resource.Matrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class Savant {
    @Autowired
    Display display;
    public ArrayList<ArrayList<Integer>> solve(String simpleTableAsString) throws Exception{
        JsonElement tableAsJsonElement = JsonParser.parseString(simpleTableAsString);
        JsonElement cells = tableAsJsonElement.getAsJsonObject().get("cells");
        if (cells == null){
            throw new Exception("cells not found");
        }
        ArrayList<ArrayList<Integer>> unsolvedMatrix = new ArrayList<>();
        int[][] values = new int[9][9];
        for (int i = 0; i < cells.getAsJsonArray().size(); i++) {
            ArrayList<Integer> rowArray = new ArrayList<>(9);
            for (int j = 0; j < cells.getAsJsonArray().get(i).getAsJsonArray().size(); j++) {
                int val = cells.getAsJsonArray().get(i).getAsJsonArray().get(j).getAsJsonPrimitive().getAsInt();
                if(val > 9 || val < 1){
                    val = 0;
                }
                rowArray.add(val);
                values[i][j] = val;
            }
            unsolvedMatrix.add(rowArray);
        }
        Matrix matrix = new Matrix(values);
        matrix.solveMatrix();

        display.printSolvingMatrices(unsolvedMatrix, matrix.getResult());
        return matrix.getResult();
    }
}
