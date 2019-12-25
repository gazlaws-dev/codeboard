package com.gazlaws.codeboard.layout;

import com.gazlaws.codeboard.layout.builder.KeyboardLayoutBuilder;

public class Definitions {

    public static void addArrowsRow(KeyboardLayoutBuilder keyboard)
    {
        keyboard.newRow()
                .addKey("Esc", 27);
        addTabKey(keyboard);
        keyboard.addKey("◀", 5000).asRepeatable()
                .addKey("▼", 5001).asRepeatable()
                .addKey("▲", 5002).asRepeatable()
                .addKey("▶", 5003).asRepeatable()
                .addKey("SYM", -15)
        ;
    }

    public static void addCopyPasteRow(KeyboardLayoutBuilder keyboard)
    {
        keyboard.newRow()
                .addKey("Esc", 27);
        addTabKey(keyboard);
        keyboard.addKey("Select All", 53737).withSize(2.0f).asRepeatable()
                .addKey("Cut", 53738).asRepeatable()
                .addKey("Copy", 53739).asRepeatable()
                .addKey("Paste", 53740)
                .addKey("SYM", -15)
        ;
    }

    public static void addNumberRow(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey('(')
                .addKey(')')
                .addKey('1')
                .addKey('2')
                .addKey('3')
                .addKey('4')
                .addKey('5')
                .addKey('6')
                .addKey('7')
                .addKey('8')
                .addKey('9')
                .addKey('0')
                .addKey('@')
                ;
    }

    public static void addOperatorRow(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey('+')
                .addKey('-')
                .addKey('=')
                .addKey(':')
                .addKey('*')
                .addKey('^')
                .addKey('/')
                .addKey('{')
                .addKey('}')
                .addKey('_')
                .addKey('[')
                .addKey(']')
                ;
    }

    public static void addQwertyRows(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey('q').onShiftUppercase()
                .addKey('w').onShiftUppercase()
                .addKey('e').onShiftUppercase()
                .addKey('r').onShiftUppercase()
                .addKey('t').onShiftUppercase()
                .addKey('y').onShiftUppercase()
                .addKey('u').onShiftUppercase()
                .addKey('i').onShiftUppercase()
                .addKey('o').onShiftUppercase()
                .addKey('p').onShiftUppercase()
                .newRow()
                .addKey('!').withSize(.7f)
                .addKey('a').onShiftUppercase()
                .addKey('s').onShiftUppercase()
                .addKey('d').onShiftUppercase()
                .addKey('f').onShiftUppercase()
                .addKey('g').onShiftUppercase()
                .addKey('h').onShiftUppercase()
                .addKey('j').onShiftUppercase()
                .addKey('k').onShiftUppercase()
                .addKey('l').onShiftUppercase();
        addBackspaceKey(keyboard);
        keyboard.newRow();
        addShiftKey(keyboard);
        keyboard.addKey('z').onShiftUppercase()
                .addKey('x').onShiftUppercase()
                .addKey('c').onShiftUppercase()
                .addKey('v').onShiftUppercase()
                .addKey('b').onShiftUppercase()
                .addKey('n').onShiftUppercase()
                .addKey('m').onShiftUppercase()
                .addKey('?');
        addEnterKey(keyboard);
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
                .addKey('p').onShiftUppercase();
        addBackspaceKey(keyboard);
        keyboard.newRow()
                .addKey('q').onShiftUppercase()
                .addKey('s').onShiftUppercase()
                .addKey('d').onShiftUppercase()
                .addKey('f').onShiftUppercase()
                .addKey('g').onShiftUppercase()
                .addKey('h').onShiftUppercase()
                .addKey('j').onShiftUppercase()
                .addKey('k').onShiftUppercase()
                .addKey('l').onShiftUppercase()
                .addKey('m').onShiftUppercase();
        addEnterKey(keyboard);
        keyboard.newRow();
        addShiftKey(keyboard);
        keyboard.addKey('w').onShiftUppercase()
                .addKey('x').onShiftUppercase()
                .addKey('c').onShiftUppercase()
                .addKey('v').onShiftUppercase()
                .addKey('b').onShiftUppercase()
                .addKey('n').onShiftUppercase()
                .addKey('!').withSize(.8f)
                .addKey('?').withSize(.8f);
        addTabKey(keyboard);
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
                .addKey('l').onShiftUppercase();
        addEnterKey(keyboard);
        keyboard.newRow()
                .addKey('a').onShiftUppercase()
                .addKey('o').onShiftUppercase()
                .addKey('e').onShiftUppercase()
                .addKey('u').onShiftUppercase()
                .addKey('i').onShiftUppercase()
                .addKey('d').onShiftUppercase()
                .addKey('h').onShiftUppercase()
                .addKey('t').onShiftUppercase()
                .addKey('n').onShiftUppercase()
                .addKey('s').onShiftUppercase();
        addBackspaceKey(keyboard);
        keyboard.newRow();
        addShiftKey(keyboard);
        keyboard.addKey('q').onShiftUppercase()
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

    public static void addSymbolRows(KeyboardLayoutBuilder keyboard){
        keyboard.newRow()
                .addKey('~')
                .addKey('`')
                .addKey('#')
                .addKey('\\')
                .addKey('%')
                .addKey('$')
                .addKey("£", 163)
                .addKey("°", 176)
                .addKey("¬", 172);
        addBackspaceKey(keyboard);
        keyboard.newRow()
                .addKey("Sel All", 53737).asRepeatable()
                .addKey("Cut", 53738).asRepeatable()
                .addKey("Copy", 53739).asRepeatable()
                .addKey("Paste", 53740)
                .addKey("Undo", 53741)
                .addKey("Redo", 53742)
                .newRow();
        addShiftKey(keyboard);
        keyboard.addKey("for(;;){\n\t}").withSize(2.0f)
                .addKey("printf(\"\");").withSize(2.0f)
                .addKey("scanf(\"\");").withSize(2.0f);
        addEnterKey(keyboard);
    }

    public static void addSpaceRow(KeyboardLayoutBuilder keyboard){
        keyboard.newRow()
                .addKey("Ctrl",17).asModifier().onCtrlShow("CTRL").withSize(1.5f)
                .addKey('&').withSize(.7f)
                .addKey('|').withSize(.7f)
                .addKey('<').withSize(.7f)
                .addKey('>').withSize(.7f)
                .addKey("Space", 32).withSize(3.0f)
                .addKey(';').withSize(.7f)
                .addKey('"').withSize(.7f)
                .addKey('\'').withSize(.7f)
                .addKey(',').withSize(.7f)
                .addKey('.').withSize(.7f)
                ;
    }

    private static KeyboardLayoutBuilder addTabKey(KeyboardLayoutBuilder keyboard){
        return keyboard.addKey("Tab", 9);
    }

    private static KeyboardLayoutBuilder addShiftKey(KeyboardLayoutBuilder keyboard){
        return keyboard.addKey("Shft", 16).asModifier()
                .onShiftShow("SHFT").withSize(1.5f);
    }

    private static KeyboardLayoutBuilder addBackspaceKey(KeyboardLayoutBuilder keyboard){
        return keyboard.addKey("⌫", -5).asRepeatable();
    }

    private static KeyboardLayoutBuilder addEnterKey(KeyboardLayoutBuilder keyboard){
        return keyboard.addKey("Enter", -4).withSize(1.5f);
    }
}
