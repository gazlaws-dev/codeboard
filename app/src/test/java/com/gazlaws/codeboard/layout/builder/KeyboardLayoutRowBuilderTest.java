package com.gazlaws.codeboard.layout.builder;

import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Key;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class KeyboardLayoutRowBuilderTest {

    private Box defaultBox = Box.create(0,0,100,10);

    @Test
    public void setBox_doesNotThrow() {
        builder().setBox(Box.create(0, 600, 320, 32));
    }

    @Test
    public void addKey_canBeCalledMultipleTimes() {
        builder().setBox(defaultBox).addKey(new KeyInfo()).addKey(new KeyInfo()).addKey(new KeyInfo());
    }

    @Test
    public void build_returnsCorrectNumberOfKeys() throws KeyboardLayoutException {
        assertEquals(2, builder().setBox(defaultBox).addKey(new KeyInfo()).addKey(new KeyInfo()).build().size());
    }

    @Test
    public void build_proportinallySplitsAvaiableWidth() throws KeyboardLayoutException {
        ArrayList<Key> result = buildTwoKeysRow();
        assertEquals(result.get(0).box.width, 25, 0.01);
        assertEquals(result.get(1).box.width, 75, 0.01);
    }

    @Test
    public void build_usesAllAvaiableHeight() throws KeyboardLayoutException {
        ArrayList<Key> result = buildTwoKeysRow();
        assertEquals(result.get(0).box.height, 10, 0.01);
    }

    @Test
    public void build_addsGapBetweenKeys() throws KeyboardLayoutException {
        ArrayList<Key> result = buildTwoKeysRow(20);
        assertEquals(result.get(0).box.width, 20, 0.01);
        assertEquals(result.get(1).box.width, 60, 0.01);
    }

    private KeyboardLayoutRowBuilder builder() {
        return new KeyboardLayoutRowBuilder();
    }

    private ArrayList<Key> buildTwoKeysRow() throws KeyboardLayoutException {
        return buildTwoKeysRow(0);
    }

    private ArrayList<Key> buildTwoKeysRow(float gap) throws KeyboardLayoutException {
        KeyInfo keyA = new KeyInfo();
        keyA.size = 1;
        KeyInfo keyB = new KeyInfo();
        keyB.size = 3;
        return builder().setBox(defaultBox)
                .setGap(gap)
                .addKey(keyA).addKey(keyB).build();
    }
}