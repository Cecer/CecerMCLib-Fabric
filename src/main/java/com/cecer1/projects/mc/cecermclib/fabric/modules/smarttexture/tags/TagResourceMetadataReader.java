package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.tags;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;

import java.util.HashMap;
import java.util.Map;

public class TagResourceMetadataReader implements ResourceMetadataReader<TagResourceMetadata> {
    private static final int FORMAT_VERSION = 1;

    private static final TagResourceMetadataReader instance = new TagResourceMetadataReader();
    public static TagResourceMetadataReader getInstance() {
        return instance;
    }
    
    @Override
    public String getKey() {
        return "cecermclib_tags";
    }

    @Override
    public TagResourceMetadata fromJson(JsonObject obj) {
        int formatVersion = JsonHelper.getInt(obj, "formatVersion");
        if (formatVersion != FORMAT_VERSION) {
            throw new JsonParseException(String.format("Unsupported tags smart texture metadata version! supports %d but found %d", FORMAT_VERSION, formatVersion));
        }

        JsonObject tagsJson = obj.getAsJsonObject("tags");
        Map<String, int[]> tags = new HashMap<>();

        for (Map.Entry<String, JsonElement> tagEntry : tagsJson.entrySet()) {
            JsonArray tagJson = tagEntry.getValue().getAsJsonArray();
            
            int[] colors = new int[tagJson.size()];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = tagJson.get(i).getAsInt();
            }
            
            tags.put(tagEntry.getKey(), colors);
        }

        return new TagResourceMetadata(tags);
    }
}
