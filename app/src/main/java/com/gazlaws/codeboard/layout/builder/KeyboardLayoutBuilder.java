package com.gazlaws.codeboard.layout.builder;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.gazlaws.codeboard.R;
import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Key;

import java.util.ArrayList;

public class KeyboardLayoutBuilder {

    private Context context;
    private Box box; // the dimensions of the keyboard
    private ArrayList<KeyboardLayoutRowBuilder> rows = new ArrayList<>();
    private KeyboardLayoutRowBuilder currentRow = null;
    private KeyInfo currentKey = null;
    private float rowGap = 0; // space between keyboard rows
    private float keyGap = 0; // space between keys (horizontal
    private float padding = 0;

    public KeyboardLayoutBuilder (Context current){
        this.context = current;
    }

    public KeyboardLayoutBuilder newRow()
    {
        currentKey = null;
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

    public KeyboardLayoutBuilder addKey(String label, int code)
    {
        if (currentRow == null){
            newRow();
        }
        currentKey = new KeyInfo();
        currentKey.label = label;
        currentKey.code = code;
        currentKey.size = 1.0f;
        currentKey.isRepeatable = false;
        currentRow.addKey(currentKey);
        return this;
    }

    public KeyboardLayoutBuilder addKey(Drawable icon, int code)
    {
        return this.addKey("",code).withIcon(icon);
    }
    public KeyboardLayoutBuilder addKey(char key)
    {
        return this.addKey("" + key, (int)key);
    }

    public KeyboardLayoutBuilder addKey(float relativeSize)
    {
        return this.addKey('?').withSize(relativeSize);
    }

    public KeyboardLayoutBuilder addKey(String label)
    {
        return this.addKey(label, 0).withOutputText(label);
    }

    public KeyboardLayoutBuilder asRepeatable(boolean repeat){
        currentKey.isRepeatable = repeat;
        return this;
    }

    public KeyboardLayoutBuilder asRepeatable(){
        return this.asRepeatable(true);
    }

    public KeyboardLayoutBuilder withSize(float size) {
        currentKey.size = size;
        return this;
    }

    public KeyboardLayoutBuilder withCode(int code){
        currentKey.code = code;
        return this;
    }
    public KeyboardLayoutBuilder withIcon(Drawable icon){
        currentKey.icon = icon;
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

    public KeyboardLayoutBuilder asModifier(boolean isModifier) {
        currentKey.isModifier = isModifier;
        return this;
    }

    public KeyboardLayoutBuilder asModifier() {
        return asModifier(true);
    }

    public KeyboardLayoutBuilder withOutputText(String s) {
        currentKey.outputText = s;
        return this;
    }

    public KeyboardLayoutBuilder onShiftShow(String label){
        currentKey.onShiftLabel = label;
        return this;
    }

    public KeyboardLayoutBuilder onCtrlShow(String label){
        currentKey.onCtrlLabel = label;
        return this;
    }

    public KeyboardLayoutBuilder onShiftUppercase() {
        return onShiftShow(currentKey.label.toUpperCase());
    }

    // common key definitions (extension methods)

    public KeyboardLayoutBuilder addTabKey(){
        return addKey("Tab", 9);
    }

    public KeyboardLayoutBuilder addShiftKey(){
        return addKey("Shft", 16).asModifier()
            .onShiftShow("SHFT").withSize(1.5f);
    }

    public KeyboardLayoutBuilder addBackspaceKey(){
        return addKey(context.getDrawable(R.drawable.ic_backspace_24dp), -5).asRepeatable();
    }

    public KeyboardLayoutBuilder addEnterKey(){
        return addKey(context.getDrawable(R.drawable.ic_keyboard_return_24dp), -4).withSize(1.5f);
    }
}
