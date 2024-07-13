package com.gazlaws.codeboard;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.gazlaws.codeboard.theme.ThemeDefinitions;
import com.gazlaws.codeboard.theme.ThemeInfo;
import com.gazlaws.codeboard.theme.IOnFocusListenable;
import com.gazlaws.codeboard.layout.ui.KeyboardButtonView;
import com.gazlaws.codeboard.layout.ui.KeyboardLayoutView;
import com.gazlaws.codeboard.KeyboardPreferences;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import static android.provider.Settings.Secure.DEFAULT_INPUT_METHOD;

public class SettingsFragment extends PreferenceFragmentCompat implements IOnFocusListenable {

    KeyboardPreferences keyboardPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        keyboardPreferences = new KeyboardPreferences(requireActivity());

        // Declare a new thread to do a preference check
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

        // Only allow numbers
        String[] numberOnlyPreferences = {"vibrate_ms", "font_size", "size_portrait", "size_landscape"};
        for (String key : numberOnlyPreferences) {
            EditTextPreference editTextPreference = getPreferenceManager().findPreference(key);
            if (editTextPreference != null) {
                editTextPreference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    }
                });
            }
        }

        ListPreference themePreference = findPreference("theme");
        if (themePreference != null) {
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
        }

        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.getInt("notification") == 1) {
            scrollToPreference("notification");
        }

        // Add gradient preferences
        SwitchPreferenceCompat gradientEnabledPref = findPreference("custom_gradient_enabled");
        Preference gradientStartColorPref = findPreference("gradient_start_color_picker");
        Preference gradientEndColorPref = findPreference("gradient_end_color_picker");

        if (gradientStartColorPref != null) {
            gradientStartColorPref.setOnPreferenceClickListener(preference -> {
                openColourPicker("gradient_start_color");
                return true;
            });
        }

        if (gradientEndColorPref != null) {
            gradientEndColorPref.setOnPreferenceClickListener(preference -> {
                openColourPicker("gradient_end_color");
                return true;
            });
        }

        if (gradientEnabledPref != null) {
            gradientEnabledPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean enabled = (boolean) newValue;
                    gradientStartColorPref.setEnabled(enabled);
                    gradientEndColorPref.setEnabled(enabled);
                    return true;
                }
            });

            boolean gradientEnabled = gradientEnabledPref.isChecked();
            if (gradientStartColorPref != null) gradientStartColorPref.setEnabled(gradientEnabled);
            if (gradientEndColorPref != null) gradientEndColorPref.setEnabled(gradientEnabled);
        }

        // Add transparency preference for button
        EditTextPreference transparencyPreference = findPreference("button_transparency");
        if (transparencyPreference != null) {
            transparencyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    float transparency = Float.parseFloat((String) newValue);
                    keyboardPreferences.setButtonTransparency(transparency);
                    preference.setSummary(String.valueOf(transparency));
                    return true;
                }
            });
            float currentTransparency = keyboardPreferences.getButtonTransparency();
            transparencyPreference.setText(String.valueOf(currentTransparency));
            transparencyPreference.setSummary(String.valueOf(currentTransparency));
        }

        // Add blur effect preference for button
        SwitchPreferenceCompat blurEffectPref = findPreference("button_blur_effect");
        if (blurEffectPref != null) {
            blurEffectPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean blurEnabled = (boolean) newValue;
                    keyboardPreferences.setButtonBlurEffectEnabled(blurEnabled);
                    return true;
                }
            });
            boolean blurEnabled = keyboardPreferences.isButtonBlurEffectEnabled();
            blurEffectPref.setChecked(blurEnabled);
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
            // Run Intent
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
        keyboardPreferences.setGradientStartColor(themeInfo.buttonBodyStartColor);
        keyboardPreferences.setGradientEndColor(themeInfo.buttonBodyEndColor);
        //Worked
    }

    public void openColourPicker(final String key) {
        int color = 0;
        if (key.equals("bg_colour_picker")) {
            color = keyboardPreferences.getBgColor();
        } else if (key.equals("fg_colour_picker")) {
            color = keyboardPreferences.getFgColor();
        } else if (key.equals("gradient_start_color")) {
            color = keyboardPreferences.getGradientStartColor();
        } else if (key.equals("gradient_end_color")) {
            color = keyboardPreferences.getGradientEndColor();
        }

        ColorPicker colorPicker = new ColorPicker(getActivity(), Color.red(color), Color.green(color), Color.blue(color));
        colorPicker.enableAutoClose();
        colorPicker.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(int color) {
                if (key.equals("bg_colour_picker")) {
                    keyboardPreferences.setBgColor(String.format("#%06X", (0xFFFFFF & color)));
                } else if (key.equals("fg_colour_picker")) {
                    keyboardPreferences.setFgColor(String.format("#%06X", (0xFFFFFF & color)));
                } else if (key.equals("gradient_start_color")) {
                    keyboardPreferences.setGradientStartColor(color);
                } else if (key.equals("gradient_end_color")) {
                    keyboardPreferences.setGradientEndColor(color);
                }
            }
        });
        colorPicker.show();
    }
  

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            getPreferenceManager().findPreference("change_keyboard").setSummary(getCurrentImeLabel(getActivity().getApplicationContext()));
        }
    
    }
}
