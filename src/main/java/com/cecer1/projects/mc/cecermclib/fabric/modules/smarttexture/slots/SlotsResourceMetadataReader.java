package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.slots;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;

import java.util.HashMap;
import java.util.Map;

public class SlotsResourceMetadataReader implements ResourceMetadataReader<SlotsResourceMetadata> {
    private static final int FORMAT_VERSION = 1;
    
    private static final SlotsResourceMetadataReader instance = new SlotsResourceMetadataReader();
    public static SlotsResourceMetadataReader getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return "cecermclib_slots";
    }

    @Override
    public SlotsResourceMetadata fromJson(JsonObject obj) {
        int formatVersion = JsonHelper.getInt(obj, "formatVersion", -1);
        if (formatVersion != FORMAT_VERSION) {
            throw new JsonParseException(String.format("Unsupported slots smart texture metadata version! supports %d but found %d", FORMAT_VERSION, formatVersion));
        }

        JsonObject slotsJson = obj.getAsJsonObject("slots");
        Map<String, SlotsResourceMetadata.Slot> slots = new HashMap<>();
        
        for (Map.Entry<String, JsonElement> slotEntry : slotsJson.entrySet()) {
            JsonObject slotJson = slotEntry.getValue().getAsJsonObject();
            slots.put(slotEntry.getKey(), new SlotsResourceMetadata.Slot(
                    JsonHelper.getInt(slotJson, "x"),
                    JsonHelper.getInt(slotJson, "y"),
                    JsonHelper.getInt(slotJson, "width"),
                    JsonHelper.getInt(slotJson, "height")
            ));
        }
        
        return new SlotsResourceMetadata(slots);
    }
}
