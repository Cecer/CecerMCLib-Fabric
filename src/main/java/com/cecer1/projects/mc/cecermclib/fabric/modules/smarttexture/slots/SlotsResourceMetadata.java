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

}
