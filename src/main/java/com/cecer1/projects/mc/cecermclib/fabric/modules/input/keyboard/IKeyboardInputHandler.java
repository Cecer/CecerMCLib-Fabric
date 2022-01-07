package com.cecer1.projects.mc.cecermclib.fabric.modules.input.keyboard;

public interface IKeyboardInputHandler {

    default KeyboardInputResult onKeyboardKeyDown(int key, int scancode, int modifiers) {
        return KeyboardInputResult.BUBBLE_UP;
    }
    default KeyboardInputResult onKeyboardKeyUp(int key, int scancode, int modifiers) {
        return KeyboardInputResult.BUBBLE_UP;
    }

    enum KeyboardInputResult {
        /**
         * Bubble up.
         */
        BUBBLE_UP,
        /**
         * Don't bubble up. Do cancel the event.
         */
        CONSUME,
        /**
         * Don't bubble up. Don't cancel the event.
         */
        PASSIVE
    }

}
