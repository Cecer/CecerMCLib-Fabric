package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.rescaler;

import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.nslice.NSliceResourceMetadata;

public class ScaledDivisionData {
    public final int[] targetSizesPixels;
    public final float[] targetSizesNormalized;
    public final float[] sourceSizesNormalized;

    private ScaledDivisionData(int[] targetSizesPixels, float[] targetSizesNormalized, float[] sourceSizesNormalized) {
        this.targetSizesPixels = targetSizesPixels;
        this.targetSizesNormalized = targetSizesNormalized;
        this.sourceSizesNormalized = sourceSizesNormalized;
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
        float[] sourceSizesNormalized = new float[slices.length];
        for (int i = 0; i < slices.length; i++) {
            sizesPixels[i] = slices[i].size + (int) (slices[i].growWeight * growWeightUnit);
            sizesNormalized[i] = (float) sizesPixels[i] / fullSize;
            sourceSizesNormalized[i] = (float) slices[i].size / baseSize;
        }
        return new ScaledDivisionData(sizesPixels, sizesNormalized, sourceSizesNormalized);
    }
}