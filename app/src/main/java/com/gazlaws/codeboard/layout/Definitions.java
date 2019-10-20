package com.gazlaws.codeboard.layout;

import com.gazlaws.codeboard.layout.builder.KeyboardLayoutBuilder;

public class Definitions {

    public static void AddArrowsRow(KeyboardLayoutBuilder keyboard)
    {
        keyboard.newRow()
                .addKey("Esc", 27)
                .addKey("Tab", 9)
                .addKey("◀", 5000).asRepeatable()
                .addKey("▼", 5001).asRepeatable()
                .addKey("▲", 5002).asRepeatable()
                .addKey("▶", 5003).asRepeatable()
                .addKey("SYM", -15)
        ;
    }

    public static void AddCopyPasteRow(KeyboardLayoutBuilder keyboard)
    {
        keyboard.newRow()
                .addKey("Esc", 27)
                .addKey("Tab", 9)
                .addKey("Select All", 53737).withSize(2.0f).asRepeatable()
                .addKey("Cut", 53738).asRepeatable()
                .addKey("Copy", 53739).asRepeatable()
                .addKey("Paste", 53740)
                .addKey("SYM", -15)
        ;
    }

    public static void AddNumberRow(KeyboardLayoutBuilder keyboard) {
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

    public static void AddOperatorRow(KeyboardLayoutBuilder keyboard) {
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

    public static void AddQwertyRows(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey('q')
                .addKey('w')
                .addKey('e')
                .addKey('r')
                .addKey('t')
                .addKey('y')
                .addKey('u')
                .addKey('i')
                .addKey('o')
                .addKey('p')
                .newRow()
                .addKey('!').withSize(.8f)
                .addKey('a')
                .addKey('s')
                .addKey('d')
                .addKey('f')
                .addKey('g')
                .addKey('h')
                .addKey('j')
                .addKey('k')
                .addKey('l')
                .addKey("◀", -5).asRepeatable()
                .newRow()
                .addKey("Shft", 16).asModifier()
                .addKey('z')
                .addKey('x')
                .addKey('c')
                .addKey('v')
                .addKey('b')
                .addKey('n')
                .addKey('m')
                .addKey('?')
                .addKey("Enter", -4).withSize(1.5f)
                ;
    }


    public static void AddAzertyRows(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey('a')
                .addKey('z')
                .addKey('e')
                .addKey('r')
                .addKey('t')
                .addKey('y')
                .addKey('u')
                .addKey('i')
                .addKey('o')
                .addKey('p')
                .addKey("◀", -5).asRepeatable()
                .newRow()
                .addKey('q')
                .addKey('s')
                .addKey('d')
                .addKey('f')
                .addKey('g')
                .addKey('h')
                .addKey('j')
                .addKey('k')
                .addKey('l')
                .addKey('m')
                .addKey("Enter", -4).withSize(1.5f)
                .newRow()
                .addKey("Shft", 16).asModifier()
                .addKey('w')
                .addKey('x')
                .addKey('c')
                .addKey('v')
                .addKey('b')
                .addKey('n')
                .addKey('!').withSize(.8f)
                .addKey('?').withSize(.8f)
                .addKey("Tab", 9)
        ;
    }

    public static void AddSymbolRows(KeyboardLayoutBuilder keyboard){
        keyboard.newRow()
                .addKey('~')
                .addKey('`')
                .addKey('#')
                .addKey('\\')
                .addKey('%')
                .addKey('$')
                .addKey("£", 163)
                .addKey("°", 176)
                .addKey("¬", 172)
                .addKey("◀", -5).asRepeatable()
                .newRow()
                .addKey("Sel All", 53737).asRepeatable()
                .addKey("Cut", 53738).asRepeatable()
                .addKey("Copy", 53739).asRepeatable()
                .addKey("Paste", 53740)
                .addKey("Undo", 53741)
                .addKey("Redo", 53742)
                .newRow()
                .addKey("Shft", 16).asModifier()
                .addKey("for(;;){ }")
                .addKey("printf(\"\")")
                .addKey("scanf(\"\")")
                .addKey("Enter", -4).withSize(1.5f)
                ;
    }

    public static void AddSpaceRow(KeyboardLayoutBuilder keyboard){
        keyboard.newRow()
                .addKey("Ctrl",17).asModifier()
                .addKey('&')
                .addKey('|')
                .addKey('<')
                .addKey('>')
                .addKey("Space", 32).withSize(3.0f)
                .addKey(';').withSize(.8f)
                .addKey('"').withSize(.8f)
                .addKey('\'').withSize(.8f)
                .addKey(',').withSize(.8f)
                .addKey('.').withSize(.8f)
                ;
    }
}
