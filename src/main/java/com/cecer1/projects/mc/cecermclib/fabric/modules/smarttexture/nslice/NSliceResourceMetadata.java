package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.nslice;

import java.util.Arrays;

public class NSliceResourceMetadata {

    private final NSliceSlice[] rows;
    private final NSliceSlice[] columns;

    public NSliceSlice[] getRows() {
        return this.rows;
    }
    public NSliceSlice[] getColumns() {
        return this.columns;
    }

    public NSliceResourceMetadata(NSliceSlice[] rows, NSliceSlice[] columns) {
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

}
