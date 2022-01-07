package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.tags;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.*;

public class TagResourceMetadata {
    
    private final Map<String, int[]> tags;
    private final Int2ObjectMap<Set<String>> tagsLookup;

    public Map<String, int[]> getTags() {
        return this.tags;
    }
    public int[] getTag(String tagName) {
        return this.tags.get(tagName);
    }
    public Set<String> getTags(int color) {
        Set<String> tags = this.tagsLookup.get(color);
        if (tags == null) {
            return Collections.emptySet();
        }
        return tags;
    }

    public TagResourceMetadata(Map<String, int[]> tags) {
        this.tags = tags;

        // Build the tags lookup map
        this.tagsLookup = new Int2ObjectArrayMap<>();

        for (Map.Entry<String, int[]> tag : this.tags.entrySet()) {
            for (int color : tag.getValue()) {
                Set<String> tagsForColor = this.tagsLookup.get(color);
                if (tagsForColor == null) {
                    tagsForColor = new HashSet<>();
                    this.tagsLookup.put(color, tagsForColor);
                }
                tagsForColor.add(tag.getKey());
            }
        }

        // Prevent modification of tags
        for (int color : this.tagsLookup.keySet()) {
            this.tagsLookup.put(color, Collections.unmodifiableSet(this.tagsLookup.get(color)));
        }
    }
    
    // <editor-fold desc="[Equality]">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagResourceMetadata that = (TagResourceMetadata) o;
        return Objects.equals(tags, that.tags) &&
                Objects.equals(tagsLookup, that.tagsLookup);
    }
    @Override
    public int hashCode() {
        return Objects.hash(tags, tagsLookup);
    }

    // </editor-fold>
}
