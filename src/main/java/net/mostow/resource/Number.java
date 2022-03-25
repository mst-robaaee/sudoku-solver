package net.mostow.resource;

import java.util.Objects;

public class Number {
    private int number;
    private int totalCount;
    Number(int number, int totalCount) {
        this.number = number;
        this.totalCount = totalCount;
    }
    int getNumber() {
        return number;
    }
    int getTotalCount() {
        return totalCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Number that = (Number) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}