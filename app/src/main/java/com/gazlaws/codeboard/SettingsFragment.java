package com.gazlaws.codeboard;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.EditTextPreferenceDialogFragmentCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.gazlaws.codeboard.backup.BackupManager;
import com.gazlaws.codeboard.backup.BackupSerializer;
import com.gazlaws.codeboard.clipboard.ClipboardExpireManager;
import com.gazlaws.codeboard.clipboard.ClipboardPrefs;
import com.gazlaws.codeboard.theme.IOnFocusListenable;
import com.gazlaws.codeboard.theme.ThemeDefinitions;
import com.gazlaws.codeboard.theme.ThemeInfo;
//import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
//import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import com.github.evilbunny2008.androidmaterialcolorpickerdialog.ColorPicker;
import com.github.evilbunny2008.androidmaterialcolorpickerdialog.ColorPickerCallback;

import static android.provider.Settings.Secure.DEFAULT_INPUT_METHOD;


public class SettingsFragment extends PreferenceFragmentCompat implements IOnFocusListenable {
    KeyboardPreferences keyboardPreferences;
    ClipboardPrefs clipboardPrefs;
    BackupManager backupManager;

    // SAF launchers for the file picker
    private ActivityResultLauncher<String> exportLauncher;
    private ActivityResultLauncher<String[]> importLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backupManager = new BackupManager(requireContext());

        // Export: open the save-file dialog
        exportLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("application/json"),
            new androidx.activity.result.ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri == null) return;
                    backupManager.exportTo(uri, new BackupManager.Callback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(requireContext(),
                                getString(R.string.backup_error_prefix) + error,
                                Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        );

        // Import: open the pick-file dialog
        importLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            new androidx.activity.result.ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri == null) return;
                    backupManager.importFrom(uri, new BackupManager.Callback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                            // Recreate the activity so all preference UI
                            // immediately reflects the new values from SharedPreferences
                            requireActivity().recreate();
                        }
                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(requireContext(),
                                getString(R.string.backup_error_prefix) + error,
                                Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        );
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        keyboardPreferences = new KeyboardPreferences(requireActivity());
        clipboardPrefs      = new ClipboardPrefs(requireActivity());

        setupClipboardHistoryPrefs();

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
            case "backup_export":
                exportLauncher.launch(BackupSerializer.generateFileName());
                break;
            case "backup_import":
                importLauncher.launch(new String[]{"application/json", "*/*"});
                break;
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

    private void setupClipboardHistoryPrefs() {
        // SwitchPreferenceCompat and ListPreference write directly to SharedPreferences
        // using their own keys. ClipboardPrefs reads from the same keys.
        // No extra listener is needed to keep values in sync.

        // Only attach a listener to the custom EditTextPreference to validate input
        EditTextPreference customPref =
                getPreferenceManager().findPreference("clipboard_history_expire_custom_hours");
        if (customPref != null) {
            customPref.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull android.widget.EditText editText) {
                    editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                }
            });
            customPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        long hours = Long.parseLong(newValue.toString().trim());
                        // Clamp to the valid range
                        if (hours < 1)    hours = 1;
                        if (hours > 2160) hours = 2160;
                        preference.setSummary(hours + " hours");
                        // Save the clamped value, not the original newValue
                        PreferenceManager.getDefaultSharedPreferences(requireContext())
                                .edit()
                                .putString("clipboard_history_expire_custom_hours", String.valueOf(hours))
                                .apply();
                        return false; // false so the original newValue isn't saved
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            });
        }
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
