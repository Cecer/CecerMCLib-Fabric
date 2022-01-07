package com.cecer1.projects.mc.cecermclib.fabric.modules.input.keyboard;

public class RootKeyboardInputHandler extends TabCycleGroup {

    @Override
    public KeyboardInputResult onKeyboardKeyDown(int key, int scancode, int modifiers) {
        return KeyboardInputResult.PASSIVE;
    }

    @Override
    public KeyboardInputResult onKeyboardKeyUp(int key, int scancode, int modifiers) {
        return KeyboardInputResult.PASSIVE;
    }
}
