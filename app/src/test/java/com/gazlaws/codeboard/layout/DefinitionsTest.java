package com.gazlaws.codeboard.layout;

import com.gazlaws.codeboard.layout.builder.KeyboardLayoutBuilder;
import com.gazlaws.codeboard.layout.builder.KeyboardLayoutException;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class DefinitionsTest {

    @Test
    public void addArrowsRow_producesValidResult() throws KeyboardLayoutException {
        KeyboardLayoutBuilder builder = builder();
        Definitions.addArrowsRow(builder);
        validate(builder);
    }

    @Test
    public void addCopyPasteRow() throws KeyboardLayoutException {
        KeyboardLayoutBuilder builder = builder();
        Definitions.addCopyPasteRow(builder);
        validate(builder);
    }

    @Test
    public void addNumberRow() throws KeyboardLayoutException {
        KeyboardLayoutBuilder builder = builder();
        Definitions.addNumberRow(builder);
        validate(builder);
    }

    @Test
    public void addOperatorRow() throws KeyboardLayoutException {
        KeyboardLayoutBuilder builder = builder();
        Definitions.addOperatorRow(builder);
        validate(builder);
    }

    @Test
    public void addQwertyRows() throws KeyboardLayoutException {
        KeyboardLayoutBuilder builder = builder();
        Definitions.addQwertyRows(builder);
        validate(builder);
    }

    @Test
    public void addAzertyRows() throws KeyboardLayoutException {
        KeyboardLayoutBuilder builder = builder();
        Definitions.addAzertyRows(builder);
        validate(builder);
    }

    @Test
    public void addSymbolRows() throws KeyboardLayoutException {
        KeyboardLayoutBuilder builder = builder();
        Definitions.addSymbolRows(builder);
        validate(builder);
    }

    @Test
    public void addSpaceRow() throws KeyboardLayoutException {
        KeyboardLayoutBuilder builder = builder();
        Definitions.addSpaceRow(builder);
        validate(builder);
    }

    private void validate(KeyboardLayoutBuilder builder) throws KeyboardLayoutException {
        for(Key key : builder.build()){
            assertNotNull(key.info);
            assertNotNull(key.box);
            // must have either code or outputText set
            assertTrue(key.info.code != 0 || key.info.outputText != null);
        }
    }

    private KeyboardLayoutBuilder builder() {
        return new KeyboardLayoutBuilder().setBox(Box.create(0,0,1,1));
    }
}