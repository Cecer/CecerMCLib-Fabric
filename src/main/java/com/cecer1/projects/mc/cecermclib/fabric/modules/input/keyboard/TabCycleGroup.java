package com.cecer1.projects.mc.cecermclib.fabric.modules.input.keyboard;

import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
import com.cecer1.projects.mc.cecermclib.common._misc.collections.Tree;
import com.cecer1.projects.mc.cecermclib.fabric.modules.input.InputModule;

import java.util.ArrayList;
import java.util.List;

public class TabCycleGroup implements IKeyboardInputHandler {

    private final List<Tree<IKeyboardInputHandler>> tabStops;

    public List<Tree<IKeyboardInputHandler>> getTabStops() {
        return this.tabStops;
    }

    public TabCycleGroup() {
        this.tabStops = new ArrayList<>();
    }

    private int getCurrentIndex(KeyboardInputManager kim) {
        Tree<IKeyboardInputHandler> tree = kim.getLeafHandlerTree();
        return this.tabStops.indexOf(tree);
    }

    public Tree<IKeyboardInputHandler> tabCycleNext() {
        KeyboardInputManager kim = CecerMCLib.get(InputModule.class).getKeyboardInputManager();
        int index = this.getCurrentIndex(kim);
        if (index == -1) {
            return null;
        }

        // If tabStops is empty then index would be -1 and so we don't have to worry about being low out of bounds
        if (++index >= this.tabStops.size()) {
            index = 0;
        }

        Tree<IKeyboardInputHandler> newLeaf = this.tabStops.get(index);
        kim.setLeafHandlerTree(newLeaf);
        return newLeaf;
    }

    public Tree<IKeyboardInputHandler> tabCycleBack() {
        KeyboardInputManager kim = CecerMCLib.get(InputModule.class).getKeyboardInputManager();
        int index = this.getCurrentIndex(kim);
        if (index == -1) {
            return null;
        }

        // If tabStops is empty then index would be -1 and so we don't have to worry about being low out of bounds
        if (--index < 0) {
            index = this.tabStops.size() - 1;
        }

        Tree<IKeyboardInputHandler> newLeaf = this.tabStops.get(index);
        kim.setLeafHandlerTree(newLeaf);
        return newLeaf;
    }
}
