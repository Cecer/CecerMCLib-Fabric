package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.rescaler;

import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.nslice.NSliceResourceMetadata;

public class ScaledDivisionData {
    public final int[] sizesPixels;
    public final float[] sizesNormalized;

    private ScaledDivisionData(int[] sizesPixels, float[] sizesNormalized) {
        this.sizesPixels = sizesPixels;
        this.sizesNormalized = sizesNormalized;
    }

    public static ScaledDivisionData calculate(int fullSize, NSliceResourceMetadata.Slice[] slices) {
        int baseSize = 0;
        int growSum = 0;
        for (NSliceResourceMetadata.Slice slice : slices) {
            baseSize += slice.size;
            growSum += slice.growWeight;
        }
        int growNeeded = fullSize - baseSize;
        float growWeightUnit = 0;
        if (growSum > 0 && growNeeded > 0) {
            growWeightUnit = (float) growNeeded / growSum;
        }

        int[] sizesPixels = new int[slices.length];
        float[] sizesNormalized = new float[slices.length];
        for (int i = 0; i < slices.length; i++) {
            sizesPixels[i] = slices[i].size + (int) (slices[i].growWeight * growWeightUnit);
            sizesNormalized[i] = (float) sizesPixels[i] / fullSize;
        }
        return new ScaledDivisionData(sizesPixels, sizesNormalized);
    }
}