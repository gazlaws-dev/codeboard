package com.gazlaws.codeboard.layout.builder;

import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Key;

import java.util.ArrayList;

public class KeyboardLayoutBuilder {

    private Box box; // the dimensions of the keyboard
    private ArrayList<KeyboardLayoutRowBuilder> rows = new ArrayList<>();
    private KeyboardLayoutRowBuilder currentRow = null;
    private float rowGap = 0; // space between keyboard rows
    private float keyGap = 0; // space between keys (horizontal

    public KeyboardLayoutBuilder newRow()
    {
        currentRow = new KeyboardLayoutRowBuilder();
        rows.add(currentRow);
        return this;
    }

    public KeyboardLayoutBuilder setBox(Box box){
        this.box = box;
        return this;
    }

    public KeyboardLayoutBuilder setRowGap(float size){
        this.rowGap = size;
        return this;
    }

    public KeyboardLayoutBuilder setKeyGap(float size){
        this.keyGap = size;
        return this;
    }

    public KeyboardLayoutBuilder addKey(float relativeSize)
    {
        if (currentRow == null){
            newRow();
        }
        currentRow.addKey(relativeSize);
        return this;
    }

    public ArrayList<Key> build() throws KeyboardLayoutException {
        float availableWidth = box.width;
        float availableHeight = box.height;
        float cursorX = box.x;
        float cursorY = box.y;
        ArrayList<Key> result = new ArrayList<>();
        for (KeyboardLayoutRowBuilder rowBuilder : rows) {
            rowBuilder.setGap(keyGap);
            float width = availableWidth;
            float height = availableHeight / rows.size();
            Box rowBox = Box.create(cursorX, cursorY, width, height);
            rowBuilder.setBox(rowBox);
            cursorY += rowBox.height;
            result.addAll(rowBuilder.build());
        }
        return result;
    }

}
