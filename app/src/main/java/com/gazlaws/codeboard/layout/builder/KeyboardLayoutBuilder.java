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
    private float padding = 0;

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

    public KeyboardLayoutBuilder setPadding(float size){
        this.padding = size;
        return this;
    }

    public KeyboardLayoutBuilder addKey(char key)
    {
        this.addKey(1, key);
        return this;
    }

    public KeyboardLayoutBuilder addKey(float relativeSize)
    {
        this.addKey(relativeSize, '?');
        return this;
    }

    public KeyboardLayoutBuilder addKey(float relativeSize, char key)
    {
        if (currentRow == null){
            newRow();
        }
        currentRow.addKey(relativeSize, key);
        return this;
    }

    public ArrayList<Key> build() throws KeyboardLayoutException {
        float availableWidth = box.width - 2*padding;
        float availableHeight = box.height - (rows.size()-1)*rowGap - 2*padding;
        float cursorX = box.x + padding;
        float cursorY = box.y + padding;
        ArrayList<Key> result = new ArrayList<>();
        for (KeyboardLayoutRowBuilder rowBuilder : rows) {
            rowBuilder.setGap(keyGap);
            float width = availableWidth;
            float height = availableHeight / rows.size();
            Box rowBox = Box.create(cursorX, cursorY, width, height);
            rowBuilder.setBox(rowBox);
            cursorY += rowBox.height;
            cursorY += rowGap;
            result.addAll(rowBuilder.build());
        }
        return result;
    }

}
