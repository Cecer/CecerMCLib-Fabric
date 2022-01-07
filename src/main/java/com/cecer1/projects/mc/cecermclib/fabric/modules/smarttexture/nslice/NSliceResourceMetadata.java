package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.nslice;

import java.util.Arrays;
import java.util.Objects;

public class NSliceResourceMetadata {

    private final Slice[] rows;
    private final Slice[] columns;

    public Slice[] getRows() {
        return this.rows;
    }
    public Slice[] getColumns() {
        return this.columns;
    }

    public NSliceResourceMetadata(Slice[] rows, Slice[] columns) {
        this.rows = rows;
        this.columns = columns;
    }
    
    // <editor-fold desc="[Equality]">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NSliceResourceMetadata that = (NSliceResourceMetadata) o;
        return Arrays.equals(rows, that.rows) &&
                Arrays.equals(columns, that.columns);
    }
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(rows);
        result = 31 * result + Arrays.hashCode(columns);
        return result;
    }
    // </editor-fold>

    public static class Slice {
        public final int size;
        public final GrowBehaviour growBehaviour;
        public final int growWeight;

        public Slice(int size, GrowBehaviour growBehaviour, int growWeight) {
            this.size = size;
            this.growBehaviour = growBehaviour;
            this.growWeight = growWeight;
        }

        // <editor-fold desc="[Equality]">
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Slice slice = (Slice) o;
            return size == slice.size &&
                    growWeight == slice.growWeight &&
                    growBehaviour == slice.growBehaviour;
        }
        @Override
        public int hashCode() {
            return Objects.hash(size, growBehaviour, growWeight);
        }
        // </editor-fold>

        public enum GrowBehaviour {
            NONE,
            REPEAT,
            STRETCH;
        }
    }

    public static class Cell {
        private final int row;
        private final int column;
        public int getRow() {
            return row;
        }
        public int getColumn() {
            return column;
        }

        public Cell(int row, int column) {
            this.row = row;
            this.column = column;
        }

        // <editor-fold desc="[Equality]">
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return row == cell.row &&
                    column == cell.column;
        }
        @Override
        public int hashCode() {
            return Objects.hash(row, column);
        }
        // </editor-fold>
    }
}
