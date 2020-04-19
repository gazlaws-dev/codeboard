package com.gazlaws.codeboard.layout;

import com.gazlaws.codeboard.layout.builder.KeyboardLayoutBuilder;

import java.util.ArrayList;
//Esc 27
//tab 9
//arrows 5000-5003
//sym -15
//backspace -5
//shift 16
//ctrl 17
//enter -4
//space 32
public class Definitions {

    public static void addArrowsRow(KeyboardLayoutBuilder keyboard)
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

    public static void addCopyPasteRow(KeyboardLayoutBuilder keyboard)
    {
        keyboard.newRow()
                .addKey("Esc", 27)
                .addKey("Tab", 9)
                .addKey("Sel All", 53737).asRepeatable().withSize(1.4f)
                .addKey("Cut", 53738).asRepeatable()
                .addKey("Copy", 53739).asRepeatable().withSize(1.1f)
                .addKey("Paste", 53740).withSize(1.3f)
                .addKey("SYM", -15)
        ;
    }

    public static void addNumberRow(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey('`')
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
                .addKey('-')
                .addKey('=')
                ;
    }

    public static void addKeyboardOperatorsRow(KeyboardLayoutBuilder keyboard) {
        keyboard.newRow()
                .addKey('~')
                .addKey('!')
                .addKey('@')
                .addKey('#')
                .addKey('$')
                .addKey('%')
                .addKey('^')
                .addKey('&')
                .addKey('*')
                .addKey('(')
                .addKey(')')
                .addKey('_')
                .addKey('+')
        ;
    }

    public static void addCustomRow(KeyboardLayoutBuilder keyboard, String symbols){
        keyboard.newRow();
        char[] chars = symbols.toCharArray();
        for (char aChar : chars) keyboard.addKey(aChar);
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
                .addKey('(')
                .addKey(')')
                .addKey('@')
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
                .addKey("Shft", 16).asModifier().onShiftShow("SHFT").withSize(1.5f)
                .addKey('z').onShiftUppercase()
                .addKey('x').onShiftUppercase()
                .addKey('c').onShiftUppercase()
                .addKey('v').onShiftUppercase()
                .addKey('b').onShiftUppercase()
                .addKey('n').onShiftUppercase()
                .addKey('m').onShiftUppercase()
                .addKey("◀", -5).asRepeatable().withSize(1.5f)
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
                .addKey("◀", -5).asRepeatable()
                .newRow()
                .addKey("Shft", 16).asModifier().onShiftShow("SHFT").withSize(1.5f)
                .addKey('w').onShiftUppercase()
                .addKey('x').onShiftUppercase()
                .addKey('c').onShiftUppercase()
                .addKey('v').onShiftUppercase()
                .addKey('b').onShiftUppercase()
                .addKey('n').onShiftUppercase()
                .addKey('!').withSize(.8f)
                .addKey('?').withSize(.8f)
                .addKey("Tab", 9)
        ;
    }

    public static void addSymbolRows(KeyboardLayoutBuilder keyboard){
        keyboard.newRow()
                .addKey("Sel All", 53737).asRepeatable()
                .addKey("Cut", 53738).asRepeatable()
                .addKey("Copy", 53739).asRepeatable()
                .addKey("Paste", 53740)
                .newRow()
                .addKey("Shft", 16).asModifier().onShiftShow("SHFT").withSize(1.5f)
                .addKey("Undo", 53741)
                .addKey("Redo", 53742)
                .addKey("◀", -5).asRepeatable()
                ;
    }

    public static void addSpaceRow(KeyboardLayoutBuilder keyboard){
        keyboard.newRow()
                .addKey("Ctrl",17).asModifier().onCtrlShow("CTRL")
                .addKey('?').withSize(.7f)
                .addKey(',').withSize(.7f)
                .addKey('"').withSize(.7f)
                .addKey(':').withSize(.7f)
                .addKey("Space", 32).withSize(2f)
                .addKey(';').withSize(.7f)
                .addKey('.').withSize(.7f)
                .addKey('\'').withSize(.7f)
                .addKey("Enter", -4).withSize(1.5f)
                ;
    }
}
