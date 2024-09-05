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
import androidx.preference.SeekBarPreference;

import com.gazlaws.codeboard.theme.ThemeDefinitions;
import com.gazlaws.codeboard.theme.ThemeInfo;
import com.gazlaws.codeboard.theme.IOnFocusListenable;
import com.gazlaws.codeboard.layout.ui.KeyboardButtonView;
import com.gazlaws.codeboard.layout.ui.KeyboardLayoutView;
import com.gazlaws.codeboard.KeyboardPreferences;

// Depracated
// import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
// import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import com.github.evilbunny2008.androidmaterialcolorpickerdialog.ColorPicker;
import com.github.evilbunny2008.androidmaterialcolorpickerdialog.ColorPickerCallback;

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
        String[] numberOnlyPreferences = {"vibrate_ms", "font_size", "size_portrait", "size_landscape", "sound_volume", "repeat_interval", "initial_repeat_delay"};
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
        SeekBarPreference transparencyPreference = findPreference("button_transparency");
        if (transparencyPreference != null) {
            transparencyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                int transparencyInt = (int) newValue;
                float transparency = transparencyInt / 100f;
                keyboardPreferences.setButtonTransparency(transparency);
                preference.setSummary(String.valueOf(transparencyInt));
                return true;
            });
            float currentTransparency = keyboardPreferences.getButtonTransparency();
            transparencyPreference.setSummary(String.valueOf((int) (currentTransparency * 100)));
        }

        // Add transparency preference for background
        SeekBarPreference bgTransparencyPreference = findPreference("bg_transparency");
        if (bgTransparencyPreference != null) {
            bgTransparencyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                int bgTransparencyInt = (int) newValue;
                float bgTransparency = bgTransparencyInt / 100f;
                keyboardPreferences.setBgTransparency(bgTransparency);
                preference.setSummary(String.valueOf(bgTransparencyInt));
                return true;
            });
            float currentBgTransparency = keyboardPreferences.getBgTransparency();
            bgTransparencyPreference.setSummary(String.valueOf((int) (currentBgTransparency * 100)));
        }

        SeekBarPreference spaceBarSizePreference = findPreference("spacebar_size");
        if (spaceBarSizePreference != null) {
            spaceBarSizePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                int spaceBarSize = (int) newValue;
                keyboardPreferences.setSpaceBarSize(spaceBarSize);
                preference.setSummary(String.valueOf(spaceBarSize));
                return true;
            });
            int currentSpaceBarSize = keyboardPreferences.getSpaceBarSize();
            spaceBarSizePreference.setSummary(String.valueOf(currentSpaceBarSize));
        }

        // Add blur effect preference for button
        SwitchPreferenceCompat buttonBlurEffectPref = findPreference("button_blur_effect");
        if (buttonBlurEffectPref != null) {
            buttonBlurEffectPref.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean blurEnabled = (boolean) newValue;
                keyboardPreferences.setButtonBlurEffectEnabled(blurEnabled);
                return true;
            });
            boolean blurEnabled = keyboardPreferences.isButtonBlurEffectEnabled();
            buttonBlurEffectPref.setChecked(blurEnabled);
        }

        // Add blur effect preference for background
        SwitchPreferenceCompat bgBlurEffectPref = findPreference("bg_blur_effect");
        if (bgBlurEffectPref != null) {
            bgBlurEffectPref.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean bgBlurEnabled = (boolean) newValue;
                keyboardPreferences.setBgBlurEffectEnabled(bgBlurEnabled);
                return true;
            });
            boolean bgBlurEnabled = keyboardPreferences.isBgBlurEffectEnabled();
            bgBlurEffectPref.setChecked(bgBlurEnabled);
        }

        // Add button color preferences
        SwitchPreferenceCompat customButtonColorEnabledPref = findPreference("custom_button_color_enabled");
        Preference buttonColorPickerPref = findPreference("button_color_picker");

        if (buttonColorPickerPref != null) {
            buttonColorPickerPref.setOnPreferenceClickListener(preference -> {
                openColourPicker("button_color_picker");
                return true;
            });
        }

        if (customButtonColorEnabledPref != null) {
            customButtonColorEnabledPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean enabled = (boolean) newValue;
                    if (buttonColorPickerPref != null) {
                        buttonColorPickerPref.setEnabled(enabled);
                    }
                    return true;
                }
            });

            boolean customButtonColorEnabled = customButtonColorEnabledPref.isChecked();
            if (buttonColorPickerPref != null) {
                buttonColorPickerPref.setEnabled(customButtonColorEnabled);
            }
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
            case "button_color_picker": 
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
        keyboardPreferences.setBgColor(themeInfo.backgroundColor);
        keyboardPreferences.setFgColor(themeInfo.foregroundColor);
        keyboardPreferences.setGradientStartColor(themeInfo.buttonBodyStartColor);
        keyboardPreferences.setGradientEndColor(themeInfo.buttonBodyEndColor);
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
        } else if (key.equals("button_color_picker")) {
            color = keyboardPreferences.getCustomButtonColor(); 
        }

        ColorPicker colorPicker = new ColorPicker(getActivity(), Color.red(color), Color.green(color), Color.blue(color));
        colorPicker.enableAutoClose();
        colorPicker.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(int color) {
                if (key.equals("bg_colour_picker")) {
                    keyboardPreferences.setBgColor(color);
                } else if (key.equals("fg_colour_picker")) {
                    keyboardPreferences.setFgColor(color);
                } else if (key.equals("gradient_start_color")) {
                    keyboardPreferences.setGradientStartColor(color);
                } else if (key.equals("gradient_end_color")) {
                    keyboardPreferences.setGradientEndColor(color);
                } else if (key.equals("button_color_picker")) {
                    keyboardPreferences.setCustomButtonColor(color);                }
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
