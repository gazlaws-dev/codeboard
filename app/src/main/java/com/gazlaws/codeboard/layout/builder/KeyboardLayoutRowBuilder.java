package com.gazlaws.codeboard.layout.builder;

import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Key;

import java.util.ArrayList;

public class KeyboardLayoutRowBuilder {

    private Box box; // the dimensions of a row
    private ArrayList<KeyBlueprint> keys = new ArrayList<>();
    private float gap = 0;
    private float totalRequestedSize = 0;

    public ArrayList<Key> build() throws KeyboardLayoutException {
        checkAndUpdateDefaults();
        if (keys.size() == 0){
            throw new KeyboardLayoutException("Row cannot be built without any keys");
        }
        float availableWidth = box.width - gap * (keys.size() - 1);
        float availableHeight = box.height;
        if (availableWidth <= 0){
            throw new KeyboardLayoutException("Not enough space to fit keys in row");
        }
        float cursorX = box.x;
        float cursorY = box.y;
        ArrayList<Key> result = new ArrayList<>();
        for (KeyBlueprint blueprint : keys){
            float width = availableWidth/totalRequestedSize*blueprint.size;
            float height = availableHeight;
            Box box = Box.create(cursorX, cursorY, width, height);
            cursorX += box.width + gap;
            Key key = buildKeyFromBlueprint(blueprint, box);
            result.add(key);
        }
        return result;
    }

    public KeyboardLayoutRowBuilder addKey(float relativeSize, char key) {
        keys.add(new KeyBlueprint(relativeSize, key));
        totalRequestedSize += relativeSize;
        return this;
    }

    public KeyboardLayoutRowBuilder setBox(Box size){
        this.box = size;
        return this;
    }

    public KeyboardLayoutRowBuilder setGap(float size){
        this.gap = size;
        return this;
    }

    private void checkAndUpdateDefaults() {
        if (box == null){
            box = Box.create(0,0,0,0);
        }
    }

    private static Key buildKeyFromBlueprint(KeyBlueprint blueprint, Box box) {
        Key key = new Key();
        key.box = box;
        key.str = "" + blueprint.key;
        return key;
    }
}
