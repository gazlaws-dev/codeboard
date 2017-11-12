package com.gazlaws.codeboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.media.MediaPlayer; // for keypress sound

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import static android.view.KeyEvent.KEYCODE_CTRL_LEFT;
import static android.view.KeyEvent.KEYCODE_HOME;
import static android.view.KeyEvent.KEYCODE_SHIFT_LEFT;
import static android.view.KeyEvent.META_CTRL_ON;
import static android.view.KeyEvent.META_SHIFT_ON;



/*Created by Ruby(aka gazlaws) on 13/02/2016.
 */


public class CodeBoardIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {
    private KeyboardView kv;
    private Keyboard normalKeyboard, symbolKeyboard, keyboard;
    EditorInfo sEditorInfo;
    private boolean vibratorOn;
    private boolean soundOn;
    private boolean shiftLock = false;
    private boolean shift = false;
    private boolean ctrl = false;
    private int mKeyboardState = R.integer.keyboard_normal;
    private int mLayout, mToprow, mSize;
    private Timer timerLongPress = null;

    @Override
    public void onKey(int primaryCode, int[] KeyCodes) {


        InputConnection ic = getCurrentInputConnection();
        int i;
        keyboard = kv.getKeyboard();

//        shift 16
//        ctrl 	17
//        left, down, up, right         5000-5004

        switch (primaryCode) {

            case 53737:
                getCurrentInputConnection().performContextMenuAction(android.R.id.selectAll);
                break;
            case 53738:
                getCurrentInputConnection().performContextMenuAction(android.R.id.cut);
                break;
            case 53739:
                getCurrentInputConnection().performContextMenuAction(android.R.id.copy);
                break;
            case 53740:
                getCurrentInputConnection().performContextMenuAction(android.R.id.paste);
                break;
            case 53741:
                getCurrentInputConnection().performContextMenuAction(android.R.id.undo);
                break;
            case 53742:
                getCurrentInputConnection().performContextMenuAction(android.R.id.redo);
                break;
            case Keyboard.KEYCODE_DELETE:
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
                break;

            case Keyboard.KEYCODE_DONE:
                switch (sEditorInfo.imeOptions & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {

                    case EditorInfo.IME_ACTION_DONE:
                        ic.performEditorAction(EditorInfo.IME_ACTION_DONE);
                        break;
                    case EditorInfo.IME_ACTION_GO:
                        ic.performEditorAction(EditorInfo.IME_ACTION_GO);
                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        ic.performEditorAction(EditorInfo.IME_ACTION_NEXT);
                        break;
                    case EditorInfo.IME_ACTION_NONE:
                        ic.performEditorAction(EditorInfo.IME_ACTION_NONE);
                        break;
                    case EditorInfo.IME_ACTION_SEARCH:
                        ic.performEditorAction(EditorInfo.IME_ACTION_SEARCH);
                        break;
                    case EditorInfo.IME_ACTION_SEND:
                        ic.performEditorAction(EditorInfo.IME_ACTION_SEND);
                        break;

                    default:
                        if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                        {
                            //Log.i(Integer.toString(sEditorInfo.imeOptions), "DroidEdit's EditorInfo");
                            ic.performEditorAction(EditorInfo.IME_ACTION_GO);
                        } else
                            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                        //Log.i (Integer.toString(sEditorInfo.imeOptions & (EditorInfo.IME_MASK_ACTION|EditorInfo.IME_FLAG_NO_ENTER_ACTION)),"Here");
                        break;
                }

                break;

            case 27:
                //Escape
                long now = System.currentTimeMillis();
                int meta = 0;
                ic.sendKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ESCAPE, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON));

                break;

            case -13:
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showInputMethodPicker();
                break;
            case -15:
                if (kv != null) {
                    if (mKeyboardState == R.integer.keyboard_normal) {
                        //change to symbol keyboard
                        symbolKeyboard = chooseKB(mLayout, mToprow, mSize, R.integer.keyboard_sym);

                        kv.setKeyboard(symbolKeyboard);
                        mKeyboardState = R.integer.keyboard_sym;
                    } else if (mKeyboardState == R.integer.keyboard_sym) {
                        //change to normal keyboard
                        normalKeyboard = chooseKB(mLayout, mToprow, mSize, R.integer.keyboard_normal);

                        kv.setKeyboard(normalKeyboard);
                        mKeyboardState = R.integer.keyboard_normal;
                    }

                }
//                try {
//                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
//                    final IBinder token = this.getWindow().getWindow().getAttributes().token;
//                    //imm.setInputMethod(token, LATIN);
//                    imm.switchToLastInputMethod(token);
//                } catch (Throwable t) { // java.lang.NoSuchMethodError if API_level<11
//                    Log.e("WHAA","cannot set the previous input method:");
//                    t.printStackTrace();
//                }
                break;

            case 17:

                long nowCtrl = System.currentTimeMillis();
                if (ctrl)
                    ic.sendKeyEvent(new KeyEvent(nowCtrl, nowCtrl, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT, 0, META_CTRL_ON));
                else
                    ic.sendKeyEvent(new KeyEvent(nowCtrl, nowCtrl, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT, 0, META_CTRL_ON));

                ctrl = !ctrl;
                controlKeyToggle();
                break;

            case 16:
                // Log.e("CodeBoardIME", "onKey" + Boolean.toString(shiftLock));
                long nowShift = System.currentTimeMillis();
                if (shift)
                    ic.sendKeyEvent(new KeyEvent(nowShift, nowShift, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT, 0, META_CTRL_ON));
                else
                    ic.sendKeyEvent(new KeyEvent(nowShift, nowShift, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT, 0, META_CTRL_ON));

                if (shiftLock) {
                    shift = true;
                    shiftKeyToggle();
                } else {
                    shift = !shift;
                    shiftKeyToggle();
                }

                break;

            case 9:
                //tab
                // ic.commitText("\u0009", 1);
                sendDownUpKeyEvents(KeyEvent.KEYCODE_TAB);
                break;

            case 5000:
                    handleArrow(KeyEvent.KEYCODE_DPAD_LEFT);

                break;
            case 5001:
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_DOWN);
                break;
            case 5002:
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_UP);

                break;

            case 5003:
                    handleArrow(KeyEvent.KEYCODE_DPAD_RIGHT);
                break;

            default:
                char code = (char) primaryCode;
                if (ctrl) {
                    long now2 = System.currentTimeMillis();
                    switch (code) {
                        case 'a':
                        case 'A':
                            if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                            {
                                getCurrentInputConnection().performContextMenuAction(android.R.id.selectAll);
                            } else
                                ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A, 0, META_CTRL_ON));
                            break;
                        case 'c':
                        case 'C':
                            if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                            {
                                getCurrentInputConnection().performContextMenuAction(android.R.id.copy);
                            } else
                                ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_C, 0, META_CTRL_ON));
                            break;
                        case 'v':
                        case 'V':
                            if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                            {
                                getCurrentInputConnection().performContextMenuAction(android.R.id.paste);
                            } else
                                ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_V, 0, META_CTRL_ON));
                            break;
                        case 'x':
                        case 'X':
                            if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                            {
                                getCurrentInputConnection().performContextMenuAction(android.R.id.cut);
                            } else
                                ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_X, 0, META_CTRL_ON));
                            break;
                        case 'z':
                        case 'Z':
                            if (shift) {
                                if (ic != null) {
                                    if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                                    {
                                        getCurrentInputConnection().performContextMenuAction(android.R.id.redo);
                                    } else
                                        ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z, 0, META_CTRL_ON | META_SHIFT_ON));
                                    shift = false;
                                    shiftLock = false;
                                    shiftKeyToggle();
                                }
                            } else {
                                //Log.e("ctrl", "z");
                                if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                                {
                                    getCurrentInputConnection().performContextMenuAction(android.R.id.undo);
                                } else
                                    ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z, 0, META_CTRL_ON));

                            }

                            break;

                        default:

                            ic.commitText(String.valueOf(code), 1);
                            break;

                    }


                    ctrl = false;
                    controlKeyToggle();

                } else if (Character.isLetter(code) && shift) {
                    code = Character.toUpperCase(code);
                    ic.commitText(String.valueOf(code), 1);
                    if (shiftLock == false) {
                        shift = false;
                        shiftKeyToggle();
                        //Log.e("CodeboardIME", "Unshifted b/c no lock");
                    }

                } else

                    ic.commitText(String.valueOf(code), 1);
        }

    }


    @Override
    public void onPress(final int primaryCode) {
        
        if (soundOn) {
            MediaPlayer keypressSoundPlayer = MediaPlayer.create(this, R.raw.keypressSound); 
            keypressSoundPlayer.start();
        }
        if (vibratorOn) {

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(20);
        }
        if (timerLongPress != null)
            timerLongPress.cancel();

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
    public void onRelease(int primaryCode) {
        if (timerLongPress != null)
            timerLongPress.cancel();

    }

    public void onKeyLongPress(int keyCode) {
        // Process long-click here
        if (keyCode == 16) {
            shiftLock = !shiftLock;
            //Log.e("CodeBoardIME", "long press" + Boolean.toString(shiftLock));
            //and onKey will now happen
        }

        if (keyCode == 32) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showInputMethodPicker();
        }

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
    }

    @Override
    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (text.toString().contains("for")) {
            ic.commitText(text, 1);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);

        } else {
            ic.commitText(text, 1);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
        }
    }

    @Override
    public void swipeDown() {

        kv.closing();

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {

    }

    public Keyboard chooseKB(int layout, int toprow, int size, int mode) {
        Keyboard keyboard;
        if (layout == 0) {

            if (toprow == 1) {

                if (size == 0) {
                    keyboard = new Keyboard(this, R.xml.qwerty0r, mode);
                } else if (size == 1) {
                    keyboard = new Keyboard(this, R.xml.qwerty1r, mode);
                } else if (size == 2) {
                    keyboard = new Keyboard(this, R.xml.qwerty2r, mode);
                } else keyboard = new Keyboard(this, R.xml.qwerty3r, mode);
            } else {

                if (size == 0) {
                    keyboard = new Keyboard(this, R.xml.qwerty0e, mode);
                } else if (size == 1) {
                    keyboard = new Keyboard(this, R.xml.qwerty1e, mode);
                } else if (size == 2) {
                    keyboard = new Keyboard(this, R.xml.qwerty2e, mode);
                } else keyboard = new Keyboard(this, R.xml.qwerty3e, mode);
            }
        } else {
            if (toprow == 1) {
                if (size == 0) {
                    keyboard = new Keyboard(this, R.xml.azerty0r, mode);
                } else if (size == 1) {
                    keyboard = new Keyboard(this, R.xml.azerty1r, mode);
                } else if (size == 2) {
                    keyboard = new Keyboard(this, R.xml.azerty2r, mode);
                } else keyboard = new Keyboard(this, R.xml.azerty3r, mode);
            } else {
                if (size == 0) {
                    keyboard = new Keyboard(this, R.xml.azerty0e, mode);
                } else if (size == 1) {
                    keyboard = new Keyboard(this, R.xml.azerty1e, mode);
                } else if (size == 2) {
                    keyboard = new Keyboard(this, R.xml.azerty2e, mode);
                } else keyboard = new Keyboard(this, R.xml.azerty3e, mode);
            }
        }
        return keyboard;
    }

    @Override
    public View onCreateInputView() {

        SharedPreferences pre = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);

        switch (pre.getInt("RADIO_INDEX_COLOUR", 0)) {
            case 0:
                //kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
                break;
            case 1:
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard1, null);
                break;
            case 2:
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard2, null);
                break;
            case 3:
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard3, null);
                break;
            case 4:
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard4, null);
                break;
            case 5:
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard5, null);
                break;

            default:
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);

                break;


        }

        if (pre.getInt("PREVIEW", 0) == 1) {
            kv.setPreviewEnabled(true);
        } else kv.setPreviewEnabled(false);

        if (pre.getInt("SOUND", 1) == 1) {
            soundOn = true;
        } else soundOn = false;

        if (pre.getInt("VIBRATE", 1) == 1) {
            vibratorOn = true;
        } else vibratorOn = false;

        shift = false;
        ctrl = false;

        mLayout = pre.getInt("RADIO_INDEX_LAYOUT", 0);
        mSize = pre.getInt("SIZE", 2);
        mToprow = pre.getInt("ARROW_ROW", 1);
        mKeyboardState = R.integer.keyboard_normal;
        //reset to normal

        Keyboard keyboard = chooseKB(mLayout, mToprow, mSize, mKeyboardState);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);


        return kv;
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        setInputView(onCreateInputView());
        sEditorInfo = attribute;

    }

    public void controlKeyToggle() {
        keyboard = kv.getKeyboard();
        int i;
        List<Keyboard.Key> keys = keyboard.getKeys();
        for (i = 0; i < keys.size(); i++) {
            if (ctrl) {
                if (keys.get(i).label != null && keys.get(i).label.equals("Ctrl")) {
                    keys.get(i).label = "CTRL";
                    break;
                }
            } else {
                if (keys.get(i).label != null && keys.get(i).label.equals("CTRL")) {
                    keys.get(i).label = "Ctrl";
                    break;
                }
            }
        }
        kv.invalidateKey(i);
    }


    public void shiftKeyToggle() {

        keyboard = kv.getKeyboard();
        List<Keyboard.Key> keys = keyboard.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            if (shift) {
                if (keys.get(i).label != null && keys.get(i).label.equals("Shft")) {
                    keys.get(i).label = "SHFT";
                    break;
                }
            } else {
                if (keys.get(i).label != null && keys.get(i).label.equals("SHFT")) {
                    keys.get(i).label = "Shft";
                    break;
                }
            }
        }
        keyboard.setShifted(shift);
        kv.invalidateAllKeys();
    }

    public void handleArrow(int keyCode) {
        InputConnection ic = getCurrentInputConnection();
        Long now2 = System.currentTimeMillis();
        if(ctrl && shift){
            ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, KEYCODE_CTRL_LEFT, 0, META_SHIFT_ON | META_CTRL_ON));
            moveSelection(keyCode);
            ic.sendKeyEvent(new KeyEvent(now2+4, now2+4, KeyEvent.ACTION_UP, KEYCODE_CTRL_LEFT, 0, META_SHIFT_ON | META_CTRL_ON));

        }
        else if (shift)
            moveSelection(keyCode);
        else if (ctrl)
            ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, keyCode, 0, META_SHIFT_ON | META_CTRL_ON));
        else sendDownUpKeyEvents(keyCode);
    }

    private void moveSelection(int keyCode) {
//        inputMethodService.sendDownKeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, 0);
//        inputMethodService.sendDownAndUpKeyEvent(dpad_keyCode, 0);
//        inputMethodService.sendUpKeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, 0);
        InputConnection ic = getCurrentInputConnection();
        Long now2 = System.currentTimeMillis();
        ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON | META_CTRL_ON));
        if(ctrl)
            ic.sendKeyEvent(new KeyEvent(now2+1, now2+1, KeyEvent.ACTION_DOWN, keyCode, 0, META_SHIFT_ON | META_CTRL_ON));

        else
                ic.sendKeyEvent(new KeyEvent(now2+1, now2+1, KeyEvent.ACTION_DOWN, keyCode, 0, META_SHIFT_ON));
        ic.sendKeyEvent(new KeyEvent(now2+3, now2+3, KeyEvent.ACTION_UP, KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON | META_CTRL_ON));


    }
}


