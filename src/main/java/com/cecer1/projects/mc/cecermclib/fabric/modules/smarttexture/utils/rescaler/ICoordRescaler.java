package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.rescaler;

public interface ICoordRescaler {
    int scaleX(int x, int fullWidth, int fullHeight);
    int scaleY(int y, int fullWidth, int fullHeight);
    int unscaleX(int x, int fullWidth, int fullHeight);
    int unscaleY(int y, int fullWidth, int fullHeight);
}
