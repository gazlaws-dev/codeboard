package com.gazlaws.codeboard;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.media.MediaPlayer; // for keypress sound

import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Definitions;
import com.gazlaws.codeboard.layout.Key;
import com.gazlaws.codeboard.layout.builder.KeyboardLayoutBuilder;
import com.gazlaws.codeboard.layout.builder.KeyboardLayoutException;
import com.gazlaws.codeboard.layout.ui.KeyboardLayoutView;
import com.gazlaws.codeboard.layout.ui.KeyboardUiFactory;
import com.gazlaws.codeboard.theme.ThemeDefinitions;
import com.gazlaws.codeboard.theme.ThemeInfo;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

/*Created by Ruby(aka gazlaws) on 13/02/2016.
 */


public class CodeBoardIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {
    EditorInfo sEditorInfo;
    private boolean vibratorOn;
    private boolean soundOn;
    private boolean shiftLock = false;
    private boolean shift = false;
    private boolean ctrl = false;
    private int mKeyboardState = R.integer.keyboard_normal;
    private int mLayout, mSize;
    private boolean mToprow;
    private String mCustomSymbolsMain;
    private String mCustomSymbolsMain2;
    private String mCustomSymbolsSym;
    private String mCustomSymbolsSym2;
    private String mCustomSymbolsMainBottom;
    private String mCustomSymbolsSymBottom;
    private Timer timerLongPress = null;
    private boolean long_pressed = false;
    private KeyboardUiFactory mKeyboardUiFactory = null;
    private KeyboardLayoutView mCurrentKeyboardLayoutView = null;

    @Override
    public void onKey(int primaryCode, int[] KeyCodes) {

        InputConnection ic = getCurrentInputConnection();
        char code = (char) primaryCode;
        switch (primaryCode) {
            case 53737:
                ic.performContextMenuAction(android.R.id.selectAll);
                break;
            case 53738:
                ic.performContextMenuAction(android.R.id.cut);
                break;
            case 53739:
                ic.performContextMenuAction(android.R.id.copy);
                break;
            case 53740:
                ic.performContextMenuAction(android.R.id.paste);
                break;
            case 53741:
                ic.performContextMenuAction(android.R.id.undo);
                break;
            case 53742:
                ic.performContextMenuAction(android.R.id.redo);
                break;
            case -13:
                //Switch keyboard button
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.showInputMethodPicker();
                break;
            case -15:
                //SYM
                mKeyboardState = mKeyboardState == R.integer.keyboard_normal
                        ? R.integer.keyboard_sym
                        : R.integer.keyboard_normal;

                // regenerate view
                //Simple remove shift
                shift = false;
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                ctrl = false;
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT));
                setInputView(onCreateInputView());
                controlKeyUpdateView();
                shiftKeyUpdateView();
                break;

            case 17: //KEYCODE_CTRL_LEFT:
                // emulates a press down of the ctrl key
                if (ctrl)
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT));
                else
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT));

                ctrl = !ctrl;
                controlKeyUpdateView();
                break;

            case 16: //KEYCODE_SHIFT_LEFT
                // emulates press of shift key - this helps for selection with arrow keys
                if (!shiftLock && !shift) {
                    //Simple shift to true
                    shift = true;
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT));
                } else if (!shiftLock && shift) {
                    //Simple remove shift
                    shift = false;
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                } else if (shift && shiftLock) {
                    //Stay shifted if previously shifted
                }
                shiftKeyUpdateView();
                break;

            default: {
                int meta = 0;
                int ke = 0;
                if (shift) {
                    meta = KeyEvent.META_SHIFT_LEFT_ON;
                    code = Character.toUpperCase(code);
                    if (!shiftLock) {
                        shift = false;
                        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                        shiftKeyUpdateView();
                    }
                }
                if (ctrl) {
                    meta = meta | KeyEvent.META_CTRL_ON;
                    ctrl = false;
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT));
                    controlKeyUpdateView();
                }
                switch (primaryCode) {
                    case 9:
                        ke = KeyEvent.KEYCODE_TAB;
                        //ic.commitText("\u0009", 1);
                        break;

                    case 27:
                        ke = KeyEvent.KEYCODE_ESCAPE;
                        break;

                    //These are like a directional joystick - jumps outside the inputConnection
                    case 5000:
                        ke = KeyEvent.KEYCODE_DPAD_LEFT;
                        break;
                    case 5001:
                        ke = KeyEvent.KEYCODE_DPAD_DOWN;
                        break;
                    case 5002:
                        ke = KeyEvent.KEYCODE_DPAD_UP;
                        break;
                    case 5003:
                        ke = KeyEvent.KEYCODE_DPAD_RIGHT;
                        break;

                    case 32:
                        ke = KeyEvent.KEYCODE_SPACE;
                        //NOTE: Long press goes second now
                        break;
                    case -5:
                        ke = KeyEvent.KEYCODE_DEL;
                        break;
                    case -4:
                        ke = KeyEvent.KEYCODE_ENTER;
                        break;
                    default:
                        if (Character.isLetter(code)) {
                            ke = KeyEvent.keyCodeFromString("KEYCODE_" + Character.toUpperCase(code));
                        }

                }
                if (ke != 0) {
                    ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, ke, 0, meta));
                    ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, ke, 0, meta));
                } else {

                    //this doesn't use modifiers
                    ic.commitText(String.valueOf(code), 1);


//                    //Slow - and '?' becomes '/'
//                    KeyCharacterMap mKeyCharacterMap =
//                            KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
//                    String text = code + "";
//                    KeyEvent[] events = mKeyCharacterMap.getEvents(text.toCharArray());
//                    if (events.length > 0) {
//                        for (KeyEvent event2 : events) {
//                            int keycode = event2.getKeyCode();
//                            ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keycode, 0, meta));
//                            ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, keycode, 0, meta));
//                        }
//                    }

                }
            }
        }
    }

    public void onPress(final int primaryCode) {

        if (soundOn) {
            MediaPlayer keypressSoundPlayer = MediaPlayer.create(this, R.raw.keypress_sound);
            keypressSoundPlayer.start();
            keypressSoundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();

                }
            });
        }
        if (vibratorOn) {

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null)
                vibrator.vibrate(20);
        }
        clearLongPressTimer();

        timerLongPress = new Timer();

        timerLongPress.schedule(new TimerTask() {

            @Override
            public void run() {

                try {

                    Handler uiHandler = new Handler(Looper.getMainLooper());

                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {

                            try {

                                CodeBoardIME.this.onKeyLongPress(primaryCode);

                            } catch (Exception e) {
                                Log.e(CodeBoardIME.class.getSimpleName(), "uiHandler.run: " + e.getMessage(), e);
                            }

                        }
                    };

                    uiHandler.post(runnable);

                } catch (Exception e) {
                    Log.e(CodeBoardIME.class.getSimpleName(), "Timer.run: " + e.getMessage(), e);
                }
            }

        }, ViewConfiguration.getLongPressTimeout());

    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
        clearLongPressTimer();
    }

    @Override
    public void onViewClicked(boolean focusChanged) {
        super.onViewClicked(focusChanged);
        clearLongPressTimer();
    }

    public void onRelease(int primaryCode) {
        clearLongPressTimer();
    }

    public void onKeyLongPress(int keyCode) {
        // Process long-click here
        // This is followed by an onKey()
        InputConnection ic = getCurrentInputConnection();
        long_pressed = true;
        if (keyCode == 16) {
            shiftLock = !shiftLock;
            if (shiftLock) {
                shift = true;
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT));
            } else {
                shift = false;
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
            }
            shiftKeyUpdateView();
        }

        if (keyCode == 32) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.showInputMethodPicker();
        }

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibratorOn && vibrator != null)
            vibrator.vibrate(50);
    }

    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        ic.commitText(text, 1);
    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    @Override
    public View onCreateInputView() {

        if (mKeyboardUiFactory == null) {
            mKeyboardUiFactory = new KeyboardUiFactory(this);
        }

        KeyboardPreferences preferences = new KeyboardPreferences(this);

        mKeyboardUiFactory.theme = getThemeByIndex(preferences.getThemeIndex());
        mKeyboardUiFactory.theme.enablePreview = preferences.isPreviewEnabled();
        mKeyboardUiFactory.theme.enableBorder = preferences.isBorderEnabled();
        soundOn = preferences.isSoundEnabled();
        vibratorOn = preferences.isVibrateEnabled();

        shift = false;
        ctrl = false;

        mLayout = preferences.getLayoutIndex();
//                 Cannot set min. in UI, So just add 30 to the value
        mSize = preferences.getPortraitSize() + 30;
        int sizeLandscape = preferences.getLandscapeSize() + 30;
        mToprow = preferences.isArrowRowEnabled();
        mCustomSymbolsMain = preferences.getCustomSymbolsMain();
        mCustomSymbolsMain2 = preferences.getCustomSymbolsMain2();
        mCustomSymbolsSym = preferences.getCustomSymbolsSym();
        mCustomSymbolsSym2 = preferences.getCustomSymbolsSym2();
        mCustomSymbolsMainBottom = preferences.getCustomSymbolsMainBottom();
        mCustomSymbolsSymBottom = preferences.getCustomSymbolsSymBottom();

        mKeyboardUiFactory.theme.size = mSize / 100.0f;
        mKeyboardUiFactory.theme.fontSize = getResources().getDimensionPixelSize(R.dimen.fontSize);
        mKeyboardUiFactory.theme.sizeLandscape = sizeLandscape / 100.0f;

        try {
            KeyboardLayoutBuilder builder = new KeyboardLayoutBuilder();
            builder.setBox(Box.create(0, 0, 1, 1));

            if (mToprow) {
                Definitions.addCopyPasteRow(builder);
            } else {
                Definitions.addArrowsRow(builder);
            }

            if (mKeyboardState == R.integer.keyboard_sym) {
                if (!mCustomSymbolsSym.isEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsSym);
                }
                if (!mCustomSymbolsSym2.isEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsSym2);
                }
                Definitions.addSymbolRows(builder);
                Definitions.addCustomSpaceRow(builder, mCustomSymbolsSymBottom);

            } else {
                if (!mCustomSymbolsMain.isEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsMain);
                }if (!mCustomSymbolsMain2.isEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsMain2);
                }
                switch (mLayout){
                    default:
                    case 0: Definitions.addQwertyRows(builder); break;
                    case 1: Definitions.addAzertyRows(builder); break;
                    case 2: Definitions.addDvorakRows(builder); break;
                    case 3: Definitions.addQwertzRows(builder); break;
                }
                Definitions.addCustomSpaceRow(builder, mCustomSymbolsMainBottom);

            }

            Collection<Key> keyboardLayout = builder.build();
            mCurrentKeyboardLayoutView = mKeyboardUiFactory.createKeyboardView(this, keyboardLayout);
            return mCurrentKeyboardLayoutView;

        } catch (KeyboardLayoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        setInputView(onCreateInputView());
        sEditorInfo = attribute;
    }

    public void controlKeyUpdateView() {
        mCurrentKeyboardLayoutView.applyCtrlModifier(ctrl);
    }

    public void shiftKeyUpdateView() {
        mCurrentKeyboardLayoutView.applyShiftModifier(shift);
    }

    private void clearLongPressTimer(){
        if (timerLongPress != null){
            timerLongPress.cancel();
        }
        timerLongPress = null;
    }

    private ThemeInfo getThemeByIndex(int index){
        switch (index) {
            case 0:
                return ThemeDefinitions.MaterialDark();
            case 1:
                return ThemeDefinitions.MaterialWhite();
            case 2:
                return ThemeDefinitions.PureBlack();
            case 3:
                return ThemeDefinitions.White();
            case 4:
                return ThemeDefinitions.Blue();
            case 5:
                return ThemeDefinitions.Purple();
            default:
                return ThemeDefinitions.Default();
        }
    }


}