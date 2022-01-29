package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.nslice;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;

public class NSliceResourceMetadataReader implements ResourceMetadataReader<NSliceResourceMetadata> {
    private static final int FORMAT_VERSION = 1;
    
    private static final NSliceResourceMetadataReader instance = new NSliceResourceMetadataReader();
    public static NSliceResourceMetadataReader getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return "cecermclib_nslice";
    }

    @Override
    public NSliceResourceMetadata fromJson(JsonObject obj) {
        int formatVersion = JsonHelper.getInt(obj, "formatVersion", -1);
        if (formatVersion != FORMAT_VERSION) {
            throw new JsonParseException(String.format("Unsupported smarttexture tag metadata version! supports %d but found %d", FORMAT_VERSION, formatVersion));
        }

        JsonArray rowsJson = obj.getAsJsonArray("rows");
        JsonArray columnsJson = obj.getAsJsonArray("columns");
        
        if (rowsJson == null || rowsJson.size() == 0) {
            throw new JsonParseException("Invalid n-slice metadata! rows must be a JSON array of at least 1 slice specification");
        }
        if (columnsJson == null || columnsJson.size() == 0) {
            throw new JsonParseException("Invalid n-slice metadata! columns must be a JSON array of at least 1 slice specification");
        }

        NSliceResourceMetadata.Slice[] rows = new NSliceResourceMetadata.Slice[rowsJson.size()];
        NSliceResourceMetadata.Slice[] columns = new NSliceResourceMetadata.Slice[columnsJson.size()];

        for (int i = 0; i < rows.length; i++) {
            rows[i] = this.deserializeSlice(rowsJson.get(i), i);
        }
        for (int i = 0; i < columns.length; i++) {
            columns[i] = this.deserializeSlice(columnsJson.get(i), i);
        }

        return new NSliceResourceMetadata(rows, columns);
    }

    private NSliceResourceMetadata.Slice deserializeSlice(JsonElement rowElement, int index) throws JsonParseException {
        if (!rowElement.isJsonObject()) {
            throw new JsonParseException("Invalid n-slice metadata! rows[" + index + "] is not a valid slice specification");
        }
        JsonObject rowObject = rowElement.getAsJsonObject();
        
        int size = JsonHelper.getInt(rowObject, "size");
        String growBehaviourStr = JsonHelper.getString(rowObject, "growBehaviour");
        int growWeight = JsonHelper.getInt(rowObject, "growWeight");

        NSliceResourceMetadata.Slice.GrowBehaviour growBehaviour = NSliceResourceMetadata.Slice.GrowBehaviour.valueOf(growBehaviourStr);
        return new NSliceResourceMetadata.Slice(size, growBehaviour, growWeight);
    }
}
