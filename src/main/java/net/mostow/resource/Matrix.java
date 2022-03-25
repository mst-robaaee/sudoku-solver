package net.mostow.resource;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("FieldCanBeLocal")
public class Matrix {
    // static ArrayList<Long> firstMethodTimings = new ArrayList<>(), secondMethodTimings = new ArrayList<>();
    private int[][] values = new int[9][9];                         // table of SoDuKo
    private Boolean[][] cellFulfilment = new Boolean[9][9];        // 9by9 of FALSE
    private Boolean[][][] cellExistences = new Boolean[9][9][9];    // 9by9by9 of FALSE
    private Boolean[][][] cellCapabilities = new Boolean[9][9][9];  // 9by9by9 of TRUE

    private HashMap<Cell, TreeSet<Integer>> cellRemainedPossibilities = new HashMap<>();

    // basic variables to fill inside other main variables
    private static Boolean[] baseTrue = new Boolean[9];
    private static Boolean[] baseFalse = new Boolean[9];
    private static Boolean[][] base2DTrue = new Boolean[9][9];
    private static Boolean[][] base2DFalse = new Boolean[9][9];
    private static TreeSet<Integer> baseCell = new TreeSet<>();

    private final String INSTANTIATE_EXCEPTION = "input is invalid";
    private final String OUT_OF_SIZE_EXCEPTION = "input is out of array size";
    static {
        Arrays.fill(baseTrue, 0, 9, true);
        Arrays.fill(baseFalse, 0, 9, false);
        for (int i = 0; i < 9; i++) {
            baseCell.add(i+1);
        }
    }
    @SuppressWarnings("unchecked")
    public Matrix(int[][] values){
        // check input parameters
        if (values == null || values.length != 9)
            throw new IllegalArgumentException(this.INSTANTIATE_EXCEPTION);
        for (int[] value : values) {
            if (value.length != 9)
                throw new IllegalArgumentException(this.INSTANTIATE_EXCEPTION);
        }
        // initialize basic multi dimension boolean arrays
        for (int i = 0; i < cellCapabilities.length; i++) {
            for (int j = 0; j < base2DTrue.length; j++) {
                base2DTrue[j]=baseTrue.clone();
            }
            cellCapabilities[i]=base2DTrue.clone();
        }

        for (int i = 0; i < cellFulfilment.length; i++) {
            cellFulfilment[i]=baseFalse.clone();
            for (int j = 0; j < base2DTrue.length; j++) {
                base2DFalse[j]=baseFalse.clone();
            }
            cellExistences[i]=base2DFalse.clone();
        }
        for (int rowIndex = 0; rowIndex < 9; rowIndex++) {
            for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
                TreeSet<Integer> setOfNumbers = (TreeSet<Integer>) baseCell.clone();
                cellRemainedPossibilities.put(new Cell(rowIndex, columnIndex), setOfNumbers);
            }
        }
        // fill basic multi dimension boolean arrays
        for (int rowIndex = 0; rowIndex < 9; rowIndex++) {
            for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
                if(values[rowIndex][columnIndex] > 0){
                    setValue(rowIndex, columnIndex, values[rowIndex][columnIndex]);
                }
            }
        }
    }

    /**
     * @param rowIndex cell row as array index => between 0 to 8
     * @param columnIndex cell column as array index => between 0 to 8
     * @param number between 1 to 9 , 0=>empty
     */
    private void setValue(int rowIndex, int columnIndex, int number){
        if (rowIndex > 8 || rowIndex < 0 || columnIndex > 8 || columnIndex < 0){
            throw new IllegalArgumentException(OUT_OF_SIZE_EXCEPTION);
        }
        // modify hashMap of remained cells
        cellRemainedPossibilities.remove(Cell.locatedIn(rowIndex, columnIndex));
        // fill internal table
        this.values[rowIndex][columnIndex] = number;
        // fill table of shadows indicator
        this.cellFulfilment[rowIndex][columnIndex] = true;
        // fill table of multi dimensional shadows indicator
        cellExistences[rowIndex][columnIndex][number-1] = true;
        // fill table of multi dimensional indicator
        for (int index = 0; index < 9; index++) {
            this.cellCapabilities[index][columnIndex][number-1] = false;
            this.cellCapabilities[rowIndex][index][number-1] = false;
            this.cellCapabilities[rowIndex][columnIndex][index] = false;
            if (cellRemainedPossibilities.containsKey(Cell.locatedIn(index, columnIndex))) {
                cellRemainedPossibilities.get(Cell.locatedIn(index, columnIndex)).remove(number);
            }
            if (cellRemainedPossibilities.containsKey(Cell.locatedIn(rowIndex, index))) {
                cellRemainedPossibilities.get(Cell.locatedIn(rowIndex, index)).remove(number);
            }
        }
        for (int blockRow = ((rowIndex)/3)*3; blockRow < ((rowIndex)/3 + 1)*3; blockRow++) {
            for (int blockColumn = ((columnIndex)/3)*3; blockColumn < ((columnIndex)/3 + 1)*3; blockColumn++) {
                this.cellCapabilities[blockRow][blockColumn][number-1] = false;
                if (cellRemainedPossibilities.containsKey(Cell.locatedIn(blockRow, blockColumn))) {
                    cellRemainedPossibilities.get(Cell.locatedIn(blockRow, blockColumn)).remove(number);
                }
            }
        }
    }

    /**
     * command to solve matrix
     */
    public void solveMatrix() {
        // if input has less than 1 cells filled in beginning we will refuse to solve it considering long process time
        int completionSizeInProcess = 1;
        // do this trick until it stops working
        while (completionSizeInProcess < this.getCompletionSize()) {
            completionSizeInProcess = this.getCompletionSize();
            // do some simple calculation
            simpleCalculation();
            if (this.isMatrixSolved()) break;
            if (completionSizeInProcess != this.getCompletionSize()) continue;
            // do some tricky calculation
            trickyCalculation();
            if (this.isMatrixSolved()) break;
            if (completionSizeInProcess != this.getCompletionSize()) continue;
            // ok let's guess the rest but guess carefully
            guessAndSolvePossibilities();
        }
    }

    /**
     * sometimes after solving by simple rules
     * there will be cells not filled,
     * requires us to guess them.
     * priority here is cells with less possible numbers in them.
     * if guess is right then we fill cells with right guess.
     */
    private void guessAndSolvePossibilities() {
        List<Map.Entry<Cell, TreeSet<Integer>>> entryListSorted =
                cellRemainedPossibilities.entrySet().stream()
                        .sorted(Comparator.comparingInt(entry -> entry.getValue().size()))
                        .collect(Collectors.toList());
        if (entryListSorted.size() > 0){
            Map.Entry<Cell, TreeSet<Integer>> nextGuess = entryListSorted.iterator().next();
            Cell thisCell = nextGuess.getKey();
            if (nextGuess.getValue().size() == 1) {
                this.setValue(thisCell.getRowIndex(), thisCell.getColumnIndex(), nextGuess.getValue().first());
            } else {
                for (Integer currentGuessNumber : nextGuess.getValue()) {
                    // guess and solve the rest
                    Matrix currentGuess = new Matrix(this.values);
                    currentGuess.setValue(thisCell.getRowIndex(), thisCell.getColumnIndex(), currentGuessNumber);
                    currentGuess.solveMatrix();
                    // if guess is resolved then go with that otherwise go with other guesses
                    if (currentGuess.isMatrixSolved()) {
                        this.setValue(thisCell.getRowIndex(), thisCell.getColumnIndex(), currentGuessNumber);
                        Set<Cell> allRemainedCell = new HashSet<>(cellRemainedPossibilities.keySet());
                        allRemainedCell.forEach(
                                cell -> this.setValue(cell.getRowIndex(), cell.getColumnIndex(), currentGuess.values[cell.getRowIndex()][cell.getColumnIndex()])
                        );
                        break;
                    }
                }
            }
        }
    }
    /**
     * do some simple math calculation here
     * for every not filled cell and check if we can fill it
     * verification is based on simple rules of sudoku matrix
     */
    private void simpleCalculation() {
        //Display.printMatrix(this.getResult());
        LinkedList<Number> numbers = getNumbersPriority();
        for (Number numberObject : numbers) {
            if (numberObject.getTotalCount() == 9) {
                continue;
            }
            // current number to start solving
            int number = numberObject.getNumber();
            for (int rowIndex = 0; rowIndex < 9; rowIndex++) {
                for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
                    if (isFilled(rowIndex, columnIndex)) {
                        continue;
                    }
                    if (this.verifyNumberForCell(rowIndex, columnIndex, number)) {
                        this.setValue(rowIndex, columnIndex, number);
                    }
                }
            }
        }
    }
    /**
     * verify number in cell by rules of sudoku matrix
     * @param rowIndex row of cell between 0 and 8
     * @param columnIndex column of cell between 0 and 8
     * @param number number to check in
     * @return true if number allowed in cell
     */
    @SuppressWarnings("all")
    private boolean verifyNumberForCell(int rowIndex, int columnIndex, int number) {
        boolean columnConstraint = true, rowConstraint = true, neighbourConstraint = true, numberConstraint = true;
        if (!this.cellCapabilities[rowIndex][columnIndex][number - 1]) {
            return false;
        }
        for (int index = 0; index < 9; index++) {
            if (index != columnIndex && this.cellCapabilities[rowIndex][index][number - 1]) {
                columnConstraint = false;
            }
            if (index != rowIndex && this.cellCapabilities[index][columnIndex][number - 1]) {
                rowConstraint = false;
            }
            int neighbourRow = ((rowIndex) / 3) * 3 + (index / 3);
            int neighbourColumn = ((columnIndex) / 3) * 3 + (index) % 3;
            if (!(neighbourRow == rowIndex && neighbourColumn == columnIndex) && this.cellCapabilities[neighbourRow][neighbourColumn][number - 1]) {
                neighbourConstraint = false;
            }
            if (index != number-1 && this.cellCapabilities[rowIndex][columnIndex][index]) {
                numberConstraint = false;
            }
        }
        return numberConstraint || rowConstraint || columnConstraint || neighbourConstraint;
    }
    /**
     * a trick out of simple rules
     * if we can guess 2-3 possibilities of a number
     * remained in 1 3by3 block is ordered in same row/column;
     * in row/column that number can not be out of that block
     */
    private void trickyCalculation() {
        LinkedList<Number> numbers = getNumbersPriority();
        for (Number numberObject : numbers) {
            // don't investigate on completed numbers
            if (numberObject.getTotalCount()==9){
                continue;
            }
            // current number to start solving
            int number = numberObject.getNumber();
            // if in any block has 2 or 3 possibilities left for any number
            for (int blockRow = 0; blockRow < 3; blockRow++) {
                for (int blockColumn = 0; blockColumn < 3; blockColumn++) {
                    int remainedPossibilitiesInBlock = 0;
                    TreeSet<Integer> rowIndexSet = new TreeSet<>(), columnIndexSet = new TreeSet<>();
                    for (int rowIndex = blockRow * 3; rowIndex < blockRow * 3 + 3; rowIndex++) {
                        for (int columnIndex = blockColumn * 3; columnIndex < blockColumn * 3 + 3; columnIndex++) {
                            if (this.cellCapabilities[rowIndex][columnIndex][number - 1]) {
                                rowIndexSet.add(rowIndex);
                                columnIndexSet.add(columnIndex);
                                remainedPossibilitiesInBlock++;
                            }
                        }
                    }
                    /*
                      if those possibilities aligned in 1 rowIndex/columnIndex remove all other possibilities of that number
                      in that rowIndex/columnIndex out of this block
                     */
                    boolean isInSameRow = rowIndexSet.size() == 1, isInSameColumn = columnIndexSet.size() == 1;
                    if ((remainedPossibilitiesInBlock == 2 || remainedPossibilitiesInBlock == 3) && (isInSameRow || isInSameColumn)) {
                        int currentRowIndex = rowIndexSet.first();
                        int currentColumnIndex = columnIndexSet.first();
                        for (int index = 0; index < 9; index++) {
                            if (isInSameRow && (index < blockColumn * 3 || index > blockColumn * 3 + 2)) {
                                this.cellCapabilities[currentRowIndex][index][number - 1] = false;
                                if (cellRemainedPossibilities.containsKey(Cell.locatedIn(currentRowIndex, index))) {
                                    TreeSet<Integer> remainedNumbers = this.cellRemainedPossibilities.get(Cell.locatedIn(currentRowIndex, index));
                                    remainedNumbers.remove(number);
                                    if (remainedNumbers.isEmpty()) {
                                        cellRemainedPossibilities.remove(Cell.locatedIn(currentRowIndex, index));
                                    }
                                }
                            }
                            if (isInSameColumn && (index < blockRow * 3 || index > blockRow * 3 + 2)) {
                                this.cellCapabilities[index][currentColumnIndex][number - 1] = false;
                                if (cellRemainedPossibilities.containsKey(Cell.locatedIn(index, currentColumnIndex))) {
                                    TreeSet<Integer> remainedNumbers = this.cellRemainedPossibilities.get(Cell.locatedIn(index, currentColumnIndex));
                                    remainedNumbers.remove(number);
                                    if (remainedNumbers.isEmpty()) {
                                        cellRemainedPossibilities.remove(Cell.locatedIn(index, columnIndexSet.first()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * this variable is here to help solving by proper priority
     * numbers will help to solve numbers with higher counts first.
     * @return a list of numbers and total count of reveals
     */
    private LinkedList<Number> getNumbersPriority() {
        LinkedList<Number> digits = new LinkedList<>();      // current count of solved numbers
        for (int number = 1; number < 10; number++) {
            Integer numberIndex = number - 1;
            int count = Arrays.stream(cellExistences).map(
                    rows -> Arrays.stream(rows).filter(existence -> existence[numberIndex]).collect(Collectors.toList()).size()
            ).reduce(Integer::sum).orElse(0);
            if (count != 9){
                digits.push(new Number(number, count));
            }
        }
        // sort for priority
        digits.sort((o1, o2) -> -1 * Integer.compare(o1.getTotalCount(), o2.getTotalCount()));
        return digits;
    }

    /**
     * @param rowIndex cell row as array index => 0 to 8
     * @param columnIndex cell column as array index => 0 to 8
     * @return true if cell located in row&column is filled
     */
    private boolean isFilled(int rowIndex, int columnIndex) {
        return this.cellFulfilment[rowIndex][columnIndex];
    }

    /**
     * @return number of cells that are filled
     */
    private int getCompletionSize() {
        Optional<Integer> sumOfFilledCells = Arrays.stream(cellFulfilment).map(
                rows -> Arrays.stream(rows).filter((filled) -> filled).collect(Collectors.toList()).size()
        ).reduce(Integer::sum);
        return sumOfFilledCells.orElse(0);
    }
    /**
     * rule of sudoku:
     * @return true if all cells are filled then matrix is solved
     */
    private boolean isMatrixSolved() {
        return getCompletionSize() == 81;
    }
    /**
     * for printing in terminal
     * @return matrix as array list
     */
    public ArrayList<ArrayList<Integer>> getResult() {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                row.add(this.values[i][j]);
            }
            result.add(row);
        }
        return result;
    }
}
