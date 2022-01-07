package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.slots;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SlotsResourceMetadata {

    private final Map<String, Slot> slots;

    public Slot getSlot(String slotName) {
        return this.slots.get(slotName);
    }
    public Set<Map.Entry<String, Slot>> getSlots() {
        return this.slots.entrySet();
    }

    public SlotsResourceMetadata(Map<String, Slot> slots) {
        this.slots = slots;
    }

    // <editor-fold desc="[Equality]">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotsResourceMetadata that = (SlotsResourceMetadata) o;
        return Objects.equals(slots, that.slots);
    }
    @Override
    public int hashCode() {
        return Objects.hash(slots);
    }
    // </editor-fold>

    public static class Slot {
        public final int x;
        public final int y;
        public final int width;
        public final int height;

        public Slot(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        // <editor-fold desc="[Equality]">
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Slot slot = (Slot) o;
            return x == slot.x &&
                    y == slot.y &&
                    width == slot.width &&
                    height == slot.height;
        }
        @Override
        public int hashCode() {
            return Objects.hash(x, y, width, height);
        }
        // </editor-fold>
    }
}
