package net.mostow.resource;

import java.util.Objects;

public class Cell {
    private int rowIndex;
    private int columnIndex;
    Cell(int rowIndex, int columnIndex) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }
    public static Cell locatedIn(int rowIndex, int columnIndex){
        return new Cell(rowIndex, columnIndex);
    }
    int getRowIndex() {
        return rowIndex;
    }
    int getColumnIndex() {
        return columnIndex;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return rowIndex == cell.rowIndex &&
                columnIndex == cell.columnIndex;
    }
    @Override
    public int hashCode() {
        return Objects.hash(rowIndex, columnIndex);
    }
}
