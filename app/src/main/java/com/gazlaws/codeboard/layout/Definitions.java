package com.gazlaws.codeboard.layout;

import android.content.Context;

import com.gazlaws.codeboard.R;
import com.gazlaws.codeboard.layout.builder.KeyboardLayoutBuilder;

public class Definitions {
    private Context context;
    private static final int CODE_ESCAPE = -2;
    private static final int CODE_SYMBOLS = -1;

    public Definitions(Context current) {
        this.context = current;
    }

    public void addArrowsRow(KeyboardLayoutBuilder keyboard) {
        int CODE_ARROW_LEFT = 5000;
        int CODE_ARROW_DOWN = 5001;
        int CODE_ARROW_UP = 5002;
        int CODE_ARROW_RIGHT = 5003;
        keyboard.newRow()
                .addKey("Esc", CODE_ESCAPE)
                .addTabKey()
                .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_left_24dp), CODE_ARROW_LEFT).asRepeatable()
                .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_down_24dp), CODE_ARROW_DOWN).asRepeatable()
                .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_up_24dp), CODE_ARROW_UP).asRepeatable()
                .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_right_24dp), CODE_ARROW_RIGHT).asRepeatable()
                .addKey("SYM", CODE_SYMBOLS).onCtrlShow("CLIP")
        ;
    }

    public void addCopyPasteRow(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey("Esc", CODE_ESCAPE)
                .addTabKey()
                .addKey(context.getDrawable(R.drawable.ic_select_all_24dp), 53737)
                .addKey(context.getDrawable(R.drawable.ic_cut_24dp), 53738)
                .addKey(context.getDrawable(R.drawable.ic_copy_24dp), 53739)
                .addKey(context.getDrawable(R.drawable.ic_paste_24dp), 53740)
                .addKey("SYM", CODE_SYMBOLS).onCtrlShow("CLIP")
        ;
    }


    public static void addCustomRow(KeyboardLayoutBuilder keyboard, String symbols) {
        keyboard.newRow();
        char[] chars = symbols.toCharArray();
        for (char aChar : chars) keyboard.addKey(aChar);
    }


    public static void addQwertyRows(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey('q').onShiftUppercase().asRepeatable()
                .addKey('w').onShiftUppercase().asRepeatable()
                .addKey('e').onShiftUppercase().asRepeatable()
                .addKey('r').onShiftUppercase().asRepeatable()
                .addKey('t').onShiftUppercase().asRepeatable()
                .addKey('y').onShiftUppercase().asRepeatable()
                .addKey('u').onShiftUppercase().asRepeatable()
                .addKey('i').onShiftUppercase().asRepeatable()
                .addKey('o').onShiftUppercase().asRepeatable()
                .addKey('p').onShiftUppercase().asRepeatable()
                .newRow()
                .addKey('a').onShiftUppercase().withSize(1.5f).asRepeatable()
                .addKey('s').onShiftUppercase().asRepeatable()
                .addKey('d').onShiftUppercase().asRepeatable()
                .addKey('f').onShiftUppercase().asRepeatable()
                .addKey('g').onShiftUppercase().asRepeatable()
                .addKey('h').onShiftUppercase().asRepeatable()
                .addKey('j').onShiftUppercase().asRepeatable()
                .addKey('k').onShiftUppercase().asRepeatable()
                .addKey('l').onShiftUppercase().withSize(1.5f).asRepeatable()
                .newRow()
                .addShiftKey()
                .addKey('z').onShiftUppercase().asRepeatable()
                .addKey('x').onShiftUppercase().asRepeatable()
                .addKey('c').onShiftUppercase().asRepeatable()
                .addKey('v').onShiftUppercase().asRepeatable()
                .addKey('b').onShiftUppercase().asRepeatable()
                .addKey('n').onShiftUppercase().asRepeatable()
                .addKey('m').onShiftUppercase().asRepeatable()
                .addBackspaceKey()
        ;
    }

    public static void addQwertzRows(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey('q').onShiftUppercase()
                .addKey('w').onShiftUppercase()
                .addKey('e').onShiftUppercase()
                .addKey('r').onShiftUppercase()
                .addKey('t').onShiftUppercase()
                .addKey('z').onShiftUppercase()
                .addKey('u').onShiftUppercase()
                .addKey('i').onShiftUppercase()
                .addKey('o').onShiftUppercase()
                .addKey('p').onShiftUppercase()
                .newRow()
                .addKey('a').onShiftUppercase().withSize(1.5f)
                .addKey('s').onShiftUppercase()
                .addKey('d').onShiftUppercase()
                .addKey('f').onShiftUppercase()
                .addKey('g').onShiftUppercase()
                .addKey('h').onShiftUppercase()
                .addKey('j').onShiftUppercase()
                .addKey('k').onShiftUppercase()
                .addKey('l').onShiftUppercase().withSize(1.5f)
                .newRow()
                .addShiftKey()
                .addKey('y').onShiftUppercase()
                .addKey('x').onShiftUppercase()
                .addKey('c').onShiftUppercase()
                .addKey('v').onShiftUppercase()
                .addKey('b').onShiftUppercase()
                .addKey('n').onShiftUppercase()
                .addKey('m').onShiftUppercase()
                .addBackspaceKey()
        ;
    }

    public static void addAzertyRows(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey('a').onShiftUppercase()
                .addKey('z').onShiftUppercase()
                .addKey('e').onShiftUppercase()
                .addKey('r').onShiftUppercase()
                .addKey('t').onShiftUppercase()
                .addKey('y').onShiftUppercase()
                .addKey('u').onShiftUppercase()
                .addKey('i').onShiftUppercase()
                .addKey('o').onShiftUppercase()
                .addKey('p').onShiftUppercase()
                .newRow()
                .addKey('q').onShiftUppercase()
                .addKey('s').onShiftUppercase()
                .addKey('d').onShiftUppercase()
                .addKey('f').onShiftUppercase()
                .addKey('g').onShiftUppercase()
                .addKey('h').onShiftUppercase()
                .addKey('j').onShiftUppercase()
                .addKey('k').onShiftUppercase()
                .addKey('l').onShiftUppercase()
                .addKey('m').onShiftUppercase()
                .addBackspaceKey()
                .newRow()
                .addShiftKey()
                .addKey('w').onShiftUppercase()
                .addKey('x').onShiftUppercase()
                .addKey('c').onShiftUppercase()
                .addKey('v').onShiftUppercase()
                .addKey('b').onShiftUppercase()
                .addKey('n').onShiftUppercase()
                .addKey('!').withSize(.8f)
                .addKey('?').withSize(.8f)
                .addTabKey();
    }

    public static void addDvorakRows(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey('!')
                .addKey('p').onShiftUppercase()
                .addKey('y').onShiftUppercase()
                .addKey('f').onShiftUppercase()
                .addKey('g').onShiftUppercase()
                .addKey('c').onShiftUppercase()
                .addKey('r').onShiftUppercase()
                .addKey('l').onShiftUppercase()
                .addEnterKey()
                .newRow()
                .addKey('a').onShiftUppercase()
                .addKey('o').onShiftUppercase()
                .addKey('e').onShiftUppercase()
                .addKey('u').onShiftUppercase()
                .addKey('i').onShiftUppercase()
                .addKey('d').onShiftUppercase()
                .addKey('h').onShiftUppercase()
                .addKey('t').onShiftUppercase()
                .addKey('n').onShiftUppercase()
                .addKey('s').onShiftUppercase()
                .addBackspaceKey()
                .newRow()
                .addShiftKey()
                .addKey('q').onShiftUppercase()
                .addKey('j').onShiftUppercase()
                .addKey('k').onShiftUppercase()
                .addKey('x').onShiftUppercase()
                .addKey('b').onShiftUppercase()
                .addKey('m').onShiftUppercase()
                .addKey('w').onShiftUppercase()
                .addKey('v').onShiftUppercase()
                .addKey('z').onShiftUppercase()
        ;
    }

    public static void addUrduRows(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey('ض').onShiftUppercase()
                .addKey('ص').onShiftUppercase()
                .addKey('غ').onShiftUppercase()
                .addKey('ڑ').onShiftUppercase()
                .addKey('ٹ').onShiftUppercase()
                .addKey('ث').onShiftUppercase()
                .addKey('ح').onShiftUppercase()
                .addKey('ئ').onShiftUppercase()
                .addKey('ظ').onShiftUppercase()
                .addKey('ط').onShiftUppercase()
                .newRow()
                .addKey('ق').onShiftUppercase()
                .addKey('و').onShiftUppercase()
                .addKey('ع').onShiftUppercase()
                .addKey('ر').onShiftUppercase()
                .addKey('ت').onShiftUppercase()
                .addKey('ے').onShiftUppercase()
                .addKey('ء').onShiftUppercase()
                .addKey('ی').onShiftUppercase()
                .addKey('ہ').onShiftUppercase()
                .addKey('پ').onShiftUppercase()
                .newRow()
                .addKey('ا').onShiftUppercase()
                .addKey('س').onShiftUppercase()
                .addKey('ڈ').onShiftUppercase()
                .addKey('د').onShiftUppercase()
                .addKey('ف').onShiftUppercase()
                .addKey('گ').onShiftUppercase()
                .addKey('ھ').onShiftUppercase()
                .addKey('ج').onShiftUppercase()
                .addKey('ک').onShiftUppercase()
                .addKey('ل').onShiftUppercase()
                .newRow()
                .addKey('ذ').onShiftUppercase()
                .addKey('ز').onShiftUppercase()
                .addKey('ش').onShiftUppercase()
                .addKey('خ').onShiftUppercase()
                .addKey('چ').onShiftUppercase()
                .addKey('ب').onShiftUppercase()
                .addKey('ں').onShiftUppercase()
                .addKey('ن').onShiftUppercase()
                .addKey('م').onShiftUppercase()
                .addBackspaceKey();
    }

    public void addSymbolRows(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey("Home", -18)
                .addKey("End", -19)
                .addKey("Del", -21)
                .addKey("PgUp", -22)
                .addKey("PgDn", -23)
                .newRow()
                .addShiftKey()
                .addKey("F1", -6)
                .addKey("F2", -7)
                .addKey("F3", -8)
                .addKey("F4", -9)
                .addKey("F5", -10)
                .addKey("F6", -11)
                .addKey("F7", -12)
                .addBackspaceKey()
                .newRow()
                .addKey("Ctrl", 17).asModifier().onCtrlShow("CTRL")
                .addKey("F8", -13)
                .addKey("F9", -14)
                .addKey("F10", -15)
                .addKey(context.getDrawable(R.drawable.ic_space_bar_24dp), 32).withSize(1.5f)
                .addKey("F11", -16)
                .addKey("F12", -17)
                .addEnterKey()
        ;

    }

    public void addClipboardActions(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey(context.getDrawable(R.drawable.ic_select_all_24dp), 53737)
                .addKey(context.getDrawable(R.drawable.ic_cut_24dp), 53738)
                .addKey(context.getDrawable(R.drawable.ic_copy_24dp), 53739)
                .addKey(context.getDrawable(R.drawable.ic_paste_24dp), 53740)
                .addKey(context.getDrawable(R.drawable.ic_undo_24dp), 53741)
                .addKey(context.getDrawable(R.drawable.ic_redo_24dp), 53742)
        ;
    }

    public void addCustomSpaceRow(KeyboardLayoutBuilder keyboard, String symbols, int spaceBarSize) {
        char[] chars = symbols.toCharArray();

        keyboard.newRow().addKey("Ctrl", 17).asModifier().onCtrlShow("CTRL");

        for (int i = 0; i < (chars.length + 1) / 2 && chars.length > 0; i++) {
            keyboard.addKey(chars[i]).withSize(.7f).asRepeatable();
        }
        float size = (float) spaceBarSize / 20;

        keyboard.addKey(context.getDrawable(R.drawable.ic_space_bar_24dp), 32).withSize(size);
        for (int i = (chars.length + 1) / 2; i < chars.length; i++) {
            keyboard.addKey(chars[i]).withSize(.7f);
        }
        keyboard.addEnterKey().asRepeatable();

    }

}
