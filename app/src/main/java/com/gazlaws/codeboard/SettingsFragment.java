package com.gazlaws.codeboard;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.gazlaws.codeboard.theme.IOnFocusListenable;
import com.gazlaws.codeboard.theme.ThemeDefinitions;
import com.gazlaws.codeboard.theme.ThemeInfo;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import static android.provider.Settings.Secure.DEFAULT_INPUT_METHOD;


public class SettingsFragment extends PreferenceFragmentCompat implements IOnFocusListenable {
    KeyboardPreferences keyboardPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        keyboardPreferences = new KeyboardPreferences(requireActivity());

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (keyboardPreferences.isFirstStart()) {
                    Intent i = new Intent(getActivity(), IntroActivity.class);
                    startActivity(i);
                    keyboardPreferences.setFirstStart(false);
                }
            }
        });
        t.start();

        //Only allow numbers
        String[] numberOnlyPrefereces = {"vibrate_ms", "font_size", "size_portrait", "size_landscape"};
        for (String key : numberOnlyPrefereces) {
            EditTextPreference editTextPreference = getPreferenceManager().findPreference(key);
            editTextPreference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                }
            });
        }

        ListPreference themePreference = (ListPreference) getPreferenceManager().findPreference("theme");
        assert themePreference != null;
        themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!keyboardPreferences.getCustomTheme()) {
                    int index = Integer.parseInt(newValue.toString());
                    preference.setSummary(getResources().getStringArray(R.array.Themes)[index]);
                    setThemeByIndex(index);
                    return true;
                }
                preference.setSummary("Custom Theme is set");
                return false;
            }
        });

        Bundle bundle = this.getArguments();
//        Log.d(this.getClass().getSimpleName(), "onCreatePreferences: "+bundle );
        if (bundle != null &&
                (bundle.getInt("notification") == 1)) {
            scrollToPreference("notification");
        }

    }

    public static CharSequence getCurrentImeLabel(Context context) {
        CharSequence readableName = null;
        String keyboard = Settings.Secure.getString(context.getContentResolver(), DEFAULT_INPUT_METHOD);
        ComponentName componentName = ComponentName.unflattenFromString(keyboard);
        if (componentName != null) {
            String packageName = componentName.getPackageName();
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
                readableName = info.loadLabel(packageManager);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return readableName;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {

        if (preference == null || preference.getKey() == null) {
            //Run Intent
            return false;
        }
        switch (preference.getKey()) {
            case "change_keyboard":
                InputMethodManager imm = (InputMethodManager)
                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showInputMethodPicker();
                preference.setSummary(getCurrentImeLabel(getActivity().getApplicationContext()));
                break;
            case "bg_colour_picker":
            case "fg_colour_picker":
                openColourPicker(preference.getKey());
                getPreferenceManager().findPreference("theme").setSummary("Custom Theme is set");
                break;
            case "restore_default":
                confirmReset();
                break;
            case "restore_old":
                classicSymbols();
                break;
            default:
                break;
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void confirmReset() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Reset?")
                .setMessage("This will reset all your custom symbols to the default")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        keyboardPreferences.resetAllToDefault();
                        getPreferenceScreen().removeAll();
                        addPreferencesFromResource(R.xml.preferences);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    public void classicSymbols() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Reset?")
                .setMessage("This will reset all your custom symbols to the old CodeBoard layout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        keyboardPreferences.resetAllToDefault();
                        String newValue = "()1234567890#";
                        keyboardPreferences.setCustomSymbolsMain(newValue);
                        keyboardPreferences.setCustomSymbolsSym(newValue);
                        newValue = "+-=:*/{}+$[]";
                        keyboardPreferences.setCustomSymbolsMain2(newValue);
                        keyboardPreferences.setCustomSymbolsSym2(newValue);
                        newValue = "&|%\\<>;',.";
                        keyboardPreferences.setCustomSymbolsMainBottom(newValue);
                        keyboardPreferences.setCustomSymbolsSymBottom(newValue);
                        getPreferenceScreen().removeAll();
                        addPreferencesFromResource(R.xml.preferences);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    private void setThemeByIndex(int index) {
        ThemeInfo themeInfo;
        switch (index) {
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
    }

    public void openColourPicker(final String key) {
        int color = 0;
        if (key.equals("bg_colour_picker")) {
            color = keyboardPreferences.getBgColor();
        } else if (key.equals("fg_colour_picker")) {
            color = keyboardPreferences.getFgColor();
        }
        ColorPicker cp = new ColorPicker(getActivity(),
                Color.red(color),
                Color.green(color),
                Color.blue(color));
        cp.show();
        cp.enableAutoClose();
        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                if (key.equals("bg_colour_picker")) {
                    keyboardPreferences.setBgColor(String.valueOf(color));
                } else if (key.equals("fg_colour_picker")) {
                    keyboardPreferences.setFgColor(String.valueOf(color));
                }
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            Preference imePreference = (Preference) getPreferenceManager().findPreference("change_keyboard");
            imePreference.setSummary(getCurrentImeLabel(getActivity().getApplicationContext()));
        }
    }
}
