package net.mostow.sudoku;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
/**
 * test samples are generated by:
 * https://printablecreative.com/sudoku-generator
 * note that they are all easy anyway :)
 */
public class TestSamples {
    String sampleEasy = "{" +
            "  \"cells\":[" +
            "    [0,8,0, 5,2,0, 0,0,0]," +
            "    [5,2,0, 0,0,4, 0,9,3]," +
            "    [1,0,0, 0,3,6, 0,0,5]," +
            "" +
            "    [0,6,2, 3,0,0, 0,0,9]," +
            "    [7,0,0, 0,4,0, 0,0,8]," +
            "    [8,5,3, 1,0,0, 6,4,2]," +
            "" +
            "    [6,3,5, 4,0,7, 0,2,0]," +
            "    [0,7,0, 8,1,0, 0,0,0]," +
            "    [0,0,0, 2,6,0, 3,0,0]" +
            "  ]" +
            "}";
    String sampleNormal = "{" +
            "  \"cells\":[" +
            "    [9,7,0, 3,0,0, 8,0,0]," +
            "    [0,3,0, 0,0,9, 0,2,7]," +
            "    [0,0,0, 4,0,0, 0,0,0]," +
            "" +
            "    [7,8,3, 0,0,0, 0,0,0]," +
            "    [0,0,1, 8,2,0, 0,0,9]," +
            "    [0,4,9, 0,6,7, 0,3,0]," +
            "" +
            "    [0,9,0, 0,0,5, 6,8,0]," +
            "    [0,0,0, 0,0,0, 2,0,5]," +
            "    [0,5,0, 7,8,0, 0,9,0]" +
            "  ]" +
            "}";
    String sampleMedium = "{" +
            "  \"cells\":[" +
            "    [0,0,9, 0,8,0, 0,3,2]," +
            "    [2,3,0, 0,7,0, 1,0,6]," +
            "    [0,0,6, 0,0,3, 0,0,0]," +
            "" +
            "    [0,0,0, 0,1,0, 9,8,0]," +
            "    [0,0,5, 6,0,0, 0,0,4]," +
            "    [3,0,0, 4,0,0, 0,0,1]," +
            "" +
            "    [7,0,4, 0,6,1, 3,0,0]," +
            "    [8,5,0, 0,0,0, 0,0,7]," +
            "    [0,0,3, 0,0,0, 0,0,8]" +
            "  ]" +
            "}";
    String sampleHard = "{" +
            "  \"cells\":[" +
            "    [0,2,0, 0,1,0, 0,0,5]," +
            "    [1,9,5, 0,0,0, 0,0,6]," +
            "    [4,0,0, 0,0,0, 9,0,0]," +
            "" +
            "    [0,0,0, 0,0,0, 0,7,0]," +
            "    [0,1,0, 7,6,2, 4,5,0]," +
            "    [0,0,4, 0,0,0, 0,2,9]," +
            "" +
            "    [0,0,0, 0,0,0, 5,0,0]," +
            "    [2,0,0, 0,0,0, 0,0,0]," +
            "    [0,0,1, 0,8,3, 0,0,0]" +
            "  ]" +
            "}";
    String sampleVeryHard = "{" +
            "  \"cells\":[" +
            "    [0,0,0, 0,7,0, 0,6,0]," +
            "    [0,0,0, 0,0,0, 0,0,0]," +
            "    [0,0,9, 0,0,3, 5,0,0]," +
            "" +
            "    [0,9,0, 0,0,4, 0,0,6]," +
            "    [0,8,0, 0,0,0, 3,0,0]," +
            "    [0,0,6, 0,3,0, 0,7,0]," +
            "" +
            "    [0,0,0, 0,8,9, 0,3,0]," +
            "    [0,7,0, 0,0,0, 0,0,0]," +
            "    [0,0,1, 6,5,0, 9,0,0]" +
            "  ]" +
            "}";

    String sampleImpossible = "{" +
            "  \"cells\":[" +
            "    [0,0,0, 0,0,0, 0,0,0]," +
            "    [0,0,0, 0,0,0, 0,0,0]," +
            "    [0,0,0, 0,0,0, 0,0,0]," +
            "" +
            "    [0,0,0, 0,0,0, 0,0,0]," +
            "    [0,0,1, 0,0,0, 0,0,0]," +
            "    [0,0,0, 0,0,0, 2,0,0]," +
            "" +
            "    [0,0,0, 0,0,0, 0,0,0]," +
            "    [0,0,0, 0,0,0, 0,0,0]," +
            "    [0,0,0, 0,0,0, 0,0,0]" +
            "  ]" +
            "}";
}