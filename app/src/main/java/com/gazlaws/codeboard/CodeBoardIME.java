package com.gazlaws.codeboard;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Vibrator;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.media.MediaPlayer; // for keypress sound
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.ColorUtils;

import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Definitions;
import com.gazlaws.codeboard.layout.Key;
import com.gazlaws.codeboard.layout.builder.KeyboardLayoutBuilder;
import com.gazlaws.codeboard.layout.builder.KeyboardLayoutException;
import com.gazlaws.codeboard.layout.ui.KeyboardLayoutView;
import com.gazlaws.codeboard.layout.ui.KeyboardUiFactory;
import com.gazlaws.codeboard.theme.ThemeDefinitions;
import com.gazlaws.codeboard.theme.ThemeInfo;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

/*Created by Ruby(aka gazlaws) on 13/02/2016.
 */


public class CodeBoardIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {
    private static final String NOTIFICATION_CHANNEL_ID = "Codeboard";
    EditorInfo sEditorInfo;
    private boolean vibratorOn;
    private int vibrateLength;
    private boolean soundOn;
    private boolean shiftLock = false;
    private boolean shift = false;
    private boolean ctrl = false;
    private int mKeyboardState = R.integer.keyboard_normal;
    private int mLayout, mSize;
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
                if (mKeyboardState == R.integer.keyboard_normal && !ctrl) {
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
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT));
                }
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
                int ke = 0;
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
        mSize = sharedPreferences.getPortraitSize();
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
        mCustomSymbolsMain = sharedPreferences.getCustomSymbolsMain();
        mCustomSymbolsMain2 = sharedPreferences.getCustomSymbolsMain2();
        mCustomSymbolsSym = sharedPreferences.getCustomSymbolsSym();
        mCustomSymbolsSym2 = sharedPreferences.getCustomSymbolsSym2();
        mCustomSymbolsMainBottom = sharedPreferences.getCustomSymbolsMainBottom();
        mCustomSymbolsSymBottom = sharedPreferences.getCustomSymbolsSymBottom();
        mLayout = sharedPreferences.getLayoutIndex();

        //Need this to get resources for drawables
        Definitions definitions = new Definitions(this);
        try {
            KeyboardLayoutBuilder builder = new KeyboardLayoutBuilder(this);
            builder.setBox(Box.create(0, 0, 1, 1));

            //Todo: allow rearranging of top row
            definitions.addArrowsRow(builder);

            if (mKeyboardState == R.integer.keyboard_sym) {
                if (!mCustomSymbolsSym.isEmpty()) {
                    definitions.addCustomRow(builder, mCustomSymbolsSym);
                }
                if (!mCustomSymbolsSym2.isEmpty()) {
                    definitions.addCustomRow(builder, mCustomSymbolsSym2);
                }
                definitions.addSymbolRows(builder);
                definitions.addCustomSpaceRow(builder, mCustomSymbolsSymBottom);

            } else if (mKeyboardState == R.integer.keyboard_normal) {
                if (!mCustomSymbolsMain.isEmpty()) {
                    definitions.addCustomRow(builder, mCustomSymbolsMain);
                }
                if (!mCustomSymbolsMain2.isEmpty()) {
                    definitions.addCustomRow(builder, mCustomSymbolsMain2);
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

                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard.hasPrimaryClip()) {
                    ClipData pr = clipboard.getPrimaryClip();
                    //Android only allows one item in Clipboard
                    String s = pr.getItemAt(0).getText().toString();
                    builder.newRow().addKey(s);
                } else {
                    builder.newRow().addKey("No history found").withOutputText("");
                }
                builder.addKey(sharedPreferences.getPin1());
                builder.newRow()
                        .addKey(sharedPreferences.getPin2())
                        .addKey(sharedPreferences.getPin3());
                builder.newRow()
                        .addKey(sharedPreferences.getPin4())
                        .addKey(sharedPreferences.getPin5());
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

    private void setNotification(boolean visible) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

        if (visible && mNotificationReceiver == null) {
            createNotificationChannel();
            int icon = R.drawable.icon;
            CharSequence text = "Keyboard notification enabled.";
            long when = System.currentTimeMillis();

            // TODO: clean this up?
            mNotificationReceiver = new NotificationReceiver(this);
            final IntentFilter pFilter = new IntentFilter(NotificationReceiver.ACTION_SHOW);
            pFilter.addAction(NotificationReceiver.ACTION_SETTINGS);
            registerReceiver(mNotificationReceiver, pFilter);

            Intent notificationIntent = new Intent(NotificationReceiver.ACTION_SHOW);
            PendingIntent contentIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, notificationIntent, 0);
            //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Intent configIntent = new Intent(NotificationReceiver.ACTION_SETTINGS);
            PendingIntent configPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(), 2, configIntent, 0);

            String title = "Show Codeboard Keyboard";
            String body = "Select this to open the keyboard. Disable in settings.";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon_large)
                    .setColor(0xff220044)
                    .setAutoCancel(false) //Make this notification automatically dismissed when the user touches it -> false.
                    .setTicker(text)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(contentIntent)
                    .setOngoing(true)
                    .addAction(R.drawable.newiconsmall, getString(R.string.notification_action_settings),
                            configPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // notificationId is a unique int for each notification that you must define
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