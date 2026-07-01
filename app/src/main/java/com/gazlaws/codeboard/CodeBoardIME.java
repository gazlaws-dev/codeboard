package com.gazlaws.codeboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ResultReceiver;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.media.MediaPlayer; // for keypress sound

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.ColorUtils;

import com.gazlaws.codeboard.clipboard.ClipboardEntry;
import com.gazlaws.codeboard.clipboard.ClipboardMonitor;
import com.gazlaws.codeboard.clipboard.ClipboardPrefs;
import com.gazlaws.codeboard.clipboard.ClipboardStorage;
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
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY;
import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

/*Created by Ruby(aka gazlaws) on 13/02/2016.
 */


public class CodeBoardIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {
    private static final String NOTIFICATION_CHANNEL_ID = "Codeboard";
    EditorInfo sEditorInfo;

    // Clipboard history
    private ClipboardMonitor clipboardMonitor;

    private boolean vibratorOn;
    private int vibrateLength;
    private boolean soundOn;
    private boolean shiftLock = false;
    private boolean ctrlLock = false;
    private boolean shift = false;
    private boolean ctrl = false;
    private int mKeyboardState = R.integer.keyboard_normal;
    private Timer timerLongPress = null;
    private KeyboardUiFactory mKeyboardUiFactory = null;
    private KeyboardLayoutView mCurrentKeyboardLayoutView = null;
    private boolean longPressedSpaceButton = false;

    @Override
    public void onKey(int primaryCode, int[] KeyCodes) {
        //NOTE: Long press goes second, this is onDown
        InputConnection ic = getCurrentInputConnection();
        char code = (char) primaryCode;
        Log.d(getClass().getSimpleName(), "onKey: " + primaryCode);

        switch (primaryCode) {
            //First handle cases that  don't use shift/ctrl meta modifiers
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
            case -1:
                //SYM
                if (ctrl && shift) {
                    // Ctrl + Shift + SYM → toggle HIST mode
                    if (mKeyboardState == R.integer.keyboard_history) {
                        mKeyboardState = R.integer.keyboard_normal;
                    } else {
                        mKeyboardState = R.integer.keyboard_history;
                    }
                } else if (mKeyboardState == R.integer.keyboard_normal && !ctrl) {
                    mKeyboardState = R.integer.keyboard_sym;
                } else if (ctrl) {
                    mKeyboardState = R.integer.keyboard_clipboard;
                } else {
                    mKeyboardState = R.integer.keyboard_normal;
                }
                // regenerate view
                //Simple remove shift
                if (shift) {
                    shift = false;
                    shiftLock = false;
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                }
                if (ctrl) {
                    ctrl = false;
                    ctrlLock = false;
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT));
                }
                setInputView(onCreateInputView());
                controlKeyUpdateView();
                shiftKeyUpdateView();
                break;

            case 17: //KEYCODE_CTRL_LEFT:
                // emulates a press down of the ctrl key
                if (!ctrlLock && !ctrl) {
                    //Simple ctrl to true
                    ctrl = true;
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT));
                } else if (!ctrlLock && ctrl) {
                    //Simple remove ctrl
                    ctrl = false;
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT));
                }
                //else if (ctrl && ctrlLock) {
                //Stay ctrled if previously ctrled
                //}
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
                }
                //else if (shift && shiftLock) {
                //Stay shifted if previously shifted
                //}
                shiftKeyUpdateView();
                break;

            default: {
                int meta = 0;
                if (shift) {
                    meta = KeyEvent.META_SHIFT_ON;
                    code = Character.toUpperCase(code);
                    if (!shiftLock) {
                        shift = false;
                        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                        shiftKeyUpdateView();
                    }
                }
                if (ctrl) {
                    meta = meta | KeyEvent.META_CTRL_ON;
                    if (!ctrlLock) {
                        ctrl = false;
                        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT));
                        controlKeyUpdateView();
                    }
                }
                //Now shift/ctrl metadata is set
                //Convert primaryCode to KeyEvent:
                //primaryCode is the char value, it doesn't correspond to the KeyEvent that we want to press
                int ke = 0;
                switch (primaryCode) {
                    case 9:
                        ke = KeyEvent.KEYCODE_TAB;
                        //ic.commitText("\u0009", 1);
                        break;
                    case -2:
                        ke = KeyEvent.KEYCODE_ESCAPE;
                        break;
                    case 32:
                        ke = KeyEvent.KEYCODE_SPACE;
                        break;
                    case -5:
                        ke = KeyEvent.KEYCODE_DEL;
                        break;
                    case -4:
                        ke = KeyEvent.KEYCODE_ENTER;
                        break;
                    case -6:
                        ke = KeyEvent.KEYCODE_F1;
                        break;
                    case -7:
                        ke = KeyEvent.KEYCODE_F2;
                        break;
                    case -8:
                        ke = KeyEvent.KEYCODE_F3;
                        break;
                    case -9:
                        ke = KeyEvent.KEYCODE_F4;
                        break;
                    case -10:
                        ke = KeyEvent.KEYCODE_F5;
                        break;
                    case -11:
                        ke = KeyEvent.KEYCODE_F6;
                        break;
                    case -12:
                        ke = KeyEvent.KEYCODE_F7;
                        break;
                    case -13:
                        ke = KeyEvent.KEYCODE_F8;
                        break;
                    case -14:
                        ke = KeyEvent.KEYCODE_F9;
                        break;
                    case -15:
                        ke = KeyEvent.KEYCODE_F10;
                        break;
                    case -16:
                        ke = KeyEvent.KEYCODE_F11;
                        break;
                    case -17:
                        ke = KeyEvent.KEYCODE_F12;
                        break;
                    case -18:
                        ke = KeyEvent.KEYCODE_MOVE_HOME;
                        break;
                    case -19:
                        ke = KeyEvent.KEYCODE_MOVE_END;
                        break;
                    case -20:
                        ke = KeyEvent.KEYCODE_INSERT;
                        break;
                    case -21:
                        ke = KeyEvent.KEYCODE_FORWARD_DEL;
                        break;
                    case -22:
                        ke = KeyEvent.KEYCODE_PAGE_UP;
                        break;
                    case -23:
                        ke = KeyEvent.KEYCODE_PAGE_DOWN;
                        break;

                    //These are like a directional joystick - can jump outside the inputConnection
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
                    default:
                        //(t key) code 116-> ke 48
                        if (Character.isLetter(code)) {
                            ke = KeyEvent.keyCodeFromString("KEYCODE_" + Character.toUpperCase(code));
                        }
//                        if (primaryCode >= 48 && primaryCode <= 57) {
//                            ke = KeyEvent.keyCodeFromString("KEYCODE_" + code);
//                        }
                }
                if (ke != 0) {

                    Log.d(getClass().getSimpleName(), "onKey: keyEvent " + ke);

                    /*
                     *   The if statement was added in order to prevent the space button
                     *   from having an action down event attached to it.
                     *   Reason being that we first want to check
                     *   whether the space button has been long pressed or not
                     *   and afterwards produce the right output to the screen.
                     *   TODO: Investigate whether KeyEvent.ACTION_UP is still required.
                     */
                    if (primaryCode != 32) {
                        ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, ke, 0, meta));
                    }

                    ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, ke, 0, meta));
                } else {
                    //All non-letter characters are handled here
                    // This doesn't use modifiers.
                    // For most users, this usage makes sense.
                    //eg. (0 key) code 48 -> ke 7
                    // If we handled '0' with a keyEvent, shift+0 would result in ')'
                    Log.i(getClass().getSimpleName(), "onKey: committext " + String.valueOf(code));
                    ic.commitText(String.valueOf(code), 1);
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
                vibrator.vibrate(vibrateLength);
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
                                Log.e(getClass().getSimpleName(), "uiHandler.run: " + e.getMessage(), e);
                            }
                        }
                    };
                    uiHandler.post(runnable);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Timer.run: " + e.getMessage(), e);
                }
            }
        }, ViewConfiguration.getLongPressTimeout());
    }

    @Override
    public void onExtractingInputChanged(EditorInfo ei){
        Log.d(getClass().getSimpleName(), "onExtractingInputChanged: ");
    }
    @Override
    public void requestHideSelf(int flags){
        int new_flag = HIDE_IMPLICIT_ONLY;
        new_flag = flags;
        Log.d(getClass().getSimpleName(), "requestHideSelf: "+new_flag + ","+ flags);
        // do nothing
        super.requestHideSelf(new_flag);
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

        /*
         *   After the space button is released,
         *   we check whether it was long pressed or not.
         *   If it was, we don't do anything,
         *   but If it wasn't, we print a "space" to the screen.
         */
        if ((primaryCode == 32) && (!longPressedSpaceButton)) {

            InputConnection ic = getCurrentInputConnection();
            ic.commitText(String.valueOf((char) primaryCode), 1);
        }

        longPressedSpaceButton = false;

        clearLongPressTimer();
    }

    public void onKeyLongPress(int keyCode) {
        // Process long-click here
        // This is following an onKey()
        InputConnection ic = getCurrentInputConnection();
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

        if (keyCode == 17) {
            ctrlLock = !ctrlLock;
            if (ctrlLock) {
                ctrl = true;
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT));
            } else {
                ctrl = false;
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT));
            }
            controlKeyUpdateView();
        }

        if (keyCode == 32) {

            longPressedSpaceButton = true;

            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.showInputMethodPicker();
        }

        if (vibratorOn) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null)
                vibrator.vibrate(vibrateLength);
        }
    }

    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        ic.commitText(text, 1);
        clearLongPressTimer();
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
        KeyboardPreferences sharedPreferences = new KeyboardPreferences(this);
        setNotification(sharedPreferences.getNotification());
        if (sharedPreferences.getCustomTheme()) {
            mKeyboardUiFactory.theme = getDefaultThemeInfo();
            mKeyboardUiFactory.theme.foregroundColor = sharedPreferences.getFgColor();
            mKeyboardUiFactory.theme.backgroundColor = sharedPreferences.getBgColor();
        } else {
            mKeyboardUiFactory.theme = setThemeByIndex(sharedPreferences, sharedPreferences.getThemeIndex());
        }
        // Keyboard Features
        vibrateLength = sharedPreferences.getVibrateLength();
        vibratorOn = sharedPreferences.isVibrateEnabled();
        soundOn = sharedPreferences.isSoundEnabled();
        mKeyboardUiFactory.theme.enablePreview = sharedPreferences.isPreviewEnabled();
        mKeyboardUiFactory.theme.enableBorder = sharedPreferences.isBorderEnabled();
        mKeyboardUiFactory.theme.fontSize = sharedPreferences.getFontSizeAsSp();
        int mSize = sharedPreferences.getPortraitSize();
        int sizeLandscape = sharedPreferences.getLandscapeSize();
        mKeyboardUiFactory.theme.size = mSize / 100.0f;
        mKeyboardUiFactory.theme.sizeLandscape = sizeLandscape / 100.0f;
        if (sharedPreferences.getNavBarDark()) {
            Objects.requireNonNull(getWindow().getWindow()).
                    setNavigationBarColor(
                            ColorUtils.blendARGB(mKeyboardUiFactory.theme.backgroundColor,
                                    Color.BLACK, 0.2f));
        } else if (sharedPreferences.getNavBar()) {
            Objects.requireNonNull(getWindow().getWindow()).
                    setNavigationBarColor(mKeyboardUiFactory.theme.backgroundColor);
        }
        //Key Layout
        boolean mToprow = sharedPreferences.getTopRowActions();
        String mCustomSymbolsMain = sharedPreferences.getCustomSymbolsMain();
        String mCustomSymbolsMain2 = sharedPreferences.getCustomSymbolsMain2();
        String mCustomSymbolsSym = sharedPreferences.getCustomSymbolsSym();
        String mCustomSymbolsSym2 = sharedPreferences.getCustomSymbolsSym2();
        String mCustomSymbolsSym3 = sharedPreferences.getCustomSymbolsSym3();
        String mCustomSymbolsSym4 = sharedPreferences.getCustomSymbolsSym4();
        String mCustomSymbolsMainBottom = sharedPreferences.getCustomSymbolsMainBottom();
        int mLayout = sharedPreferences.getLayoutIndex();

        //Need this to get resources for drawables
        Definitions definitions = new Definitions(this);
        try {
            KeyboardLayoutBuilder builder = new KeyboardLayoutBuilder(this);
            builder.setBox(Box.create(0, 0, 1, 1));

            if (mKeyboardState != R.integer.keyboard_history) {
                if (mToprow) {
                    definitions.addCopyPasteRow(builder);
                } else {
                    definitions.addArrowsRow(builder);
                }
            }

            if (mKeyboardState == R.integer.keyboard_sym) {
                if (!mCustomSymbolsSym.isEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsSym);
                }
                if (!mCustomSymbolsSym2.isEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsSym2);
                }
                if (!mCustomSymbolsSym3.isEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsSym3);
                }
                if (!mCustomSymbolsSym4.isEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsSym4);
                }
                if (mCustomSymbolsSym3.isEmpty() && mCustomSymbolsSym4.isEmpty()) {
                    definitions.addSymbolRows(builder);
                } else {
                    definitions.addCustomSpaceRow(builder, mCustomSymbolsMainBottom);
                }
            } else if (mKeyboardState == R.integer.keyboard_normal) {
                if (!mCustomSymbolsMain.isEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsMain);
                }
                if (!mCustomSymbolsMain2.isEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsMain2);
                }
                switch (mLayout) {
                    default:
                    case 0:
                        Definitions.addQwertyRows(builder);
                        break;
                    case 1:
                        Definitions.addAzertyRows(builder);
                        break;
                    case 2:
                        Definitions.addDvorakRows(builder);
                        break;
                    case 3:
                        Definitions.addQwertzRows(builder);
                        break;
                }
                definitions.addCustomSpaceRow(builder, mCustomSymbolsMainBottom);
            } else if (mKeyboardState == R.integer.keyboard_clipboard) {
                definitions.addClipboardActions(builder);

                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard.hasPrimaryClip()
                        && clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                    ClipData pr = clipboard.getPrimaryClip();
                    //Android only allows one item in Clipboard
                    String s = pr.getItemAt(0).getText().toString();
                    builder.newRow().addKey(s);
                } else {
                    builder.newRow().addKey("Nothing copied").withOutputText("");
                }
                builder.addKey(sharedPreferences.getPin1());
                builder.newRow()
                        .addKey(sharedPreferences.getPin2())
                        .addKey(sharedPreferences.getPin3());
                builder.newRow()
                        .addKey(sharedPreferences.getPin4())
                        .addKey(sharedPreferences.getPin5());
                builder.newRow()
                        .addKey(sharedPreferences.getPin6())
                        .addKey(sharedPreferences.getPin7());
            } else if (mKeyboardState == R.integer.keyboard_history) {
                // HIST mode: doesn't use KeyboardLayoutView because its coordinates are relative
                // (every entry would shrink if divided evenly).
                // Uses ScrollView + LinearLayout with a fixed entry height.
                return buildHistoryView(mKeyboardUiFactory.theme);
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
    public void onUpdateExtractingVisibility(EditorInfo ei) {
        ei.imeOptions |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        super.onUpdateExtractingVisibility(ei);
    }

    /**
     * Build the view for HIST mode using ScrollView + LinearLayout.
     * Each entry has a fixed height so it can scroll correctly.
     */
    private android.view.View buildHistoryView(com.gazlaws.codeboard.theme.ThemeInfo theme) {
        android.util.DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenH = metrics.heightPixels;
        int screenW = metrics.widthPixels;

        int rowHeightPx = (int)(48 * metrics.density);

        // Get the keyboard height from UiTheme — exactly the same as KeyboardLayoutView.onMeasure
        // so the HIST height matches the normal keyboard height
        com.gazlaws.codeboard.theme.UiTheme uiTheme =
                com.gazlaws.codeboard.theme.UiTheme.buildFromInfo(mKeyboardUiFactory.theme);
        float kbSize = screenH > screenW ? uiTheme.portraitSize : uiTheme.landscapeSize;
        int kbHeight = (int)(screenH * kbSize);

        android.widget.LinearLayout root = new android.widget.LinearLayout(this);
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        root.setBackgroundColor(theme.backgroundColor);
        root.setLayoutParams(new android.view.ViewGroup.LayoutParams(screenW, kbHeight));
        root.setClipChildren(true);
        root.setClipToPadding(true);

        // BUG1&2 Fix: use createKeyboardView (same approach as normal mode)
        // then override onMeasure so its height is only 1 row, not the full keyboard height.
        // key.box uses relative coordinates 0.0-1.0 — KeyboardLayoutView.layout()
        // converts them to pixels. Don't populate manually.
        final int rowH = rowHeightPx;
        try {
            com.gazlaws.codeboard.layout.builder.KeyboardLayoutBuilder topBuilder =
                    new com.gazlaws.codeboard.layout.builder.KeyboardLayoutBuilder(this);
            topBuilder.setBox(com.gazlaws.codeboard.layout.Box.create(0, 0, 1, 1));
            new com.gazlaws.codeboard.layout.Definitions(this).addHistoryTopRow(topBuilder);
            java.util.Collection<com.gazlaws.codeboard.layout.Key> topKeys = topBuilder.build();

            // createKeyboardView produces a KeyboardLayoutView already populated with KeyboardButtonView
            // at the correct positions and sizes (relative → pixel conversion is done internally)
            final com.gazlaws.codeboard.layout.ui.KeyboardLayoutView topRowBase =
                    mKeyboardUiFactory.createKeyboardView(this, topKeys);

            // Wrap in a FrameLayout that overrides onMeasure to report a 1-row height
            // so the parent LinearLayout respects the rowHeightPx height
            android.widget.FrameLayout topRowWrapper = new android.widget.FrameLayout(this) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int w = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
                    setMeasuredDimension(w, rowH);
                    int wSpec = android.view.View.MeasureSpec.makeMeasureSpec(w,
                            android.view.View.MeasureSpec.EXACTLY);
                    int hSpec = android.view.View.MeasureSpec.makeMeasureSpec(rowH,
                            android.view.View.MeasureSpec.EXACTLY);
                    topRowBase.measure(wSpec, hSpec);
                }
                @Override
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    topRowBase.layout(l, t, r, b);
                }
            };
            topRowWrapper.addView(topRowBase);
            topRowWrapper.setLayoutParams(
                    new android.widget.LinearLayout.LayoutParams(screenW, rowHeightPx));
            root.addView(topRowWrapper);
        } catch (Exception e) {
            android.util.Log.e("CodeBoardIME", "Failed to build HIST top row: " + e.getMessage(), e);
        }

        // Divider below the top row
        android.view.View topDivider = new android.view.View(this);
        topDivider.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1));
        topDivider.setBackgroundColor(theme.foregroundColor & 0x33FFFFFF);
        root.addView(topDivider);

        // ScrollView — explicit height: remainder after top row + divider (1px)
        int scrollHeight = kbHeight - rowHeightPx - 1;
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        scrollView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(screenW, scrollHeight));
        scrollView.setBackgroundColor(theme.backgroundColor);
        scrollView.setFillViewport(false);

        final android.widget.LinearLayout entryList = new android.widget.LinearLayout(this);
        entryList.setOrientation(android.widget.LinearLayout.VERTICAL);

        // Show temporary loading state
        android.widget.TextView loading = new android.widget.TextView(this);
        loading.setText("Loading...");
        loading.setTextColor(theme.foregroundColor);
        loading.setGravity(android.view.Gravity.CENTER);
        loading.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, rowHeightPx));
        entryList.addView(loading);

        // BUG8 Fix: disk I/O on a background thread to avoid ANR
        final com.gazlaws.codeboard.theme.ThemeInfo themeFinal = theme;
        final int rowHeightFinal = rowHeightPx;
        final float densityFinal = metrics.density;
        final android.os.Handler mainHandler =
                new android.os.Handler(android.os.Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                ClipboardStorage histStorage = new ClipboardStorage(CodeBoardIME.this);
                final java.util.List<ClipboardEntry> entries = histStorage.readRecentEntries(200);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        entryList.removeAllViews();
                        if (entries.isEmpty()) {
                            android.widget.TextView empty = new android.widget.TextView(CodeBoardIME.this);
                            empty.setText("No history");
                            empty.setTextColor(themeFinal.foregroundColor);
                            empty.setGravity(android.view.Gravity.CENTER);
                            empty.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, rowHeightFinal));
                            entryList.addView(empty);
                        } else {
                            for (final ClipboardEntry entry : entries) {
                                android.widget.TextView tv = new android.widget.TextView(CodeBoardIME.this);
                                tv.setText(entry.text);
                                tv.setTextColor(themeFinal.foregroundColor);
                                tv.setMaxLines(1);
                                tv.setEllipsize(android.text.TextUtils.TruncateAt.END);
                                tv.setPadding((int)(12 * densityFinal), 0, (int)(12 * densityFinal), 0);
                                tv.setGravity(android.view.Gravity.CENTER_VERTICAL);
                                tv.setBackgroundColor(themeFinal.backgroundColor);
                                tv.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT, rowHeightFinal));
                                tv.setOnClickListener(new android.view.View.OnClickListener() {
                                    private long lastClick = 0;
                                    @Override
                                    public void onClick(android.view.View v) {
                                        long now = System.currentTimeMillis();
                                        if (now - lastClick < 400) {
                                            android.view.inputmethod.InputConnection ic = getCurrentInputConnection();
                                            if (ic != null) ic.commitText(entry.text, 1);
                                            lastClick = 0;
                                        } else {
                                            lastClick = now;
                                        }
                                    }
                                });
                                entryList.addView(tv);
                                android.view.View divider = new android.view.View(CodeBoardIME.this);
                                divider.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1));
                                divider.setBackgroundColor(themeFinal.foregroundColor & 0x33FFFFFF);
                                entryList.addView(divider);
                            }
                        }
                    }
                });
            }
        }).start();

        scrollView.addView(entryList);
        root.addView(scrollView);
        return root;
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        setInputView(onCreateInputView());
        sEditorInfo = attribute;

        // Start the clipboard monitor when the keyboard is active
        if (clipboardMonitor == null) {
            ClipboardPrefs clipboardPrefs = new ClipboardPrefs(this);
            clipboardMonitor = new ClipboardMonitor(this, clipboardPrefs);
        }
        clipboardMonitor.start();
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        if (clipboardMonitor != null) {
            clipboardMonitor.stop();
            clipboardMonitor = null; // Let GC collect it, executor is already shut down
        }
    }

    public void controlKeyUpdateView() {
        mCurrentKeyboardLayoutView.applyCtrlModifier(ctrl);
    }

    public void shiftKeyUpdateView() {
        mCurrentKeyboardLayoutView.applyShiftModifier(shift);
    }

    private void clearLongPressTimer() {
        if (timerLongPress != null) {
            timerLongPress.cancel();
        }
        timerLongPress = null;
    }

    private ThemeInfo setThemeByIndex(KeyboardPreferences keyboardPreferences, int index) {
        ThemeInfo themeInfo = ThemeDefinitions.Default();
        switch (index) {
            case 0:
                switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        themeInfo = ThemeDefinitions.MaterialDark();
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        themeInfo = ThemeDefinitions.MaterialWhite();
                        break;
                }
                break;
            case 1:
                themeInfo = ThemeDefinitions.MaterialDark();
                break;
            case 2:
                themeInfo = ThemeDefinitions.MaterialWhite();
                break;
            case 3:
                themeInfo = ThemeDefinitions.PureBlack();
                break;
            case 4:
                themeInfo = ThemeDefinitions.White();
                break;
            case 5:
                themeInfo = ThemeDefinitions.Blue();
                break;
            case 6:
                themeInfo = ThemeDefinitions.Purple();
                break;
            default:
                themeInfo = ThemeDefinitions.Default();
                break;
        }
        keyboardPreferences.setBgColor(String.valueOf(themeInfo.backgroundColor));
        keyboardPreferences.setFgColor(String.valueOf(themeInfo.foregroundColor));
        return themeInfo;
    }

    private ThemeInfo getDefaultThemeInfo() {
        return ThemeDefinitions.Default();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private NotificationReceiver mNotificationReceiver;

    private static final int NOTIFICATION_ONGOING_ID = 1001;

    //Code from Hacker keyboard's source
    @SuppressLint("MissingPermission")
    private void setNotification(boolean visible) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (visible && mNotificationReceiver == null) {
            CharSequence text = "Keyboard notification enabled.";
            Log.i(getClass().getSimpleName(), "setNotification:" + text);

            createNotificationChannel();
            mNotificationReceiver = new NotificationReceiver(this);
            final IntentFilter pFilter = new IntentFilter(NotificationReceiver.ACTION_SHOW);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiver(mNotificationReceiver, pFilter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(mNotificationReceiver, pFilter);
            }


            Intent imeIntent = new Intent(NotificationReceiver.ACTION_SHOW);
            PendingIntent imePendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            1, imeIntent, PendingIntent.FLAG_IMMUTABLE);

            // BUG: IM closes when notification drawer is closed
            // try making a input field here?
            // Key for the string that's delivered in the action's intent.
            String KEY_TEXT_REPLY = "key_text_reply";

            RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                    .setLabel("Now click first icon")
                    .build();


            // Build a PendingIntent for the keyboard action to trigger first
            int flags = PendingIntent.FLAG_MUTABLE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                flags = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT;
            }
            PendingIntent replyPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            2,
                            imeIntent,
                            flags);


            // Create the reply action and add the remote input.
            NotificationCompat.Action action =
                    new NotificationCompat.Action.Builder(R.drawable.icon_large,
                            getString(R.string.notification_action_open_keyboard_workaround), replyPendingIntent)
                            .addRemoteInput(remoteInput)
                            .build();

            Intent settingsIntent = new Intent(this, MainActivity.class);
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent settingsPendingIntent =
                    PendingIntent.getActivity(this,0
                            , settingsIntent, PendingIntent.FLAG_IMMUTABLE);
            String title = "Show Codeboard Keyboard";
            String body = "Select this to open the keyboard. Disable in settings. You may have to fix open the fix as a workaround for newer Android versions";

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.drawable.icon_large)
                            .setColor(0xff220044)
                            .setAutoCancel(false)
                            .setTicker(text)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setContentIntent(imePendingIntent)
                            .setOngoing(true)
                            .addAction(R.drawable.icon_large, getString(R.string.notification_action_open_keyboard),
                                    imePendingIntent)
                            .addAction(R.drawable.icon_large, getString(R.string.notification_action_settings),
                                    settingsPendingIntent)
                            .addAction(action)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(NOTIFICATION_ONGOING_ID, mBuilder.build());

        } else if (!visible && mNotificationReceiver != null) {
            mNotificationManager.cancel(NOTIFICATION_ONGOING_ID);
            unregisterReceiver(mNotificationReceiver);
            mNotificationReceiver = null;
        }
    }

    @Override
    public AbstractInputMethodImpl onCreateInputMethodInterface() {
        return new MyInputMethodImpl();
    }

    IBinder mToken;

    public class MyInputMethodImpl extends InputMethodImpl {
        @Override
        public void attachToken(IBinder token) {
            super.attachToken(token);
            Log.i(getClass().getSimpleName(), "attachToken " + token);
            if (mToken == null) {
                mToken = token;
            }
        }
    }

}
