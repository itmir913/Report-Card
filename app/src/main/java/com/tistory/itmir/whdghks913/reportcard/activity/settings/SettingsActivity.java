package com.tistory.itmir.whdghks913.reportcard.activity.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tistory.itmir.whdghks913.reportcard.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(R.id.container, new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_settings);

            setOnPreferenceClick(findPreference("license"));
            setOnPreferenceClick(findPreference("change_log"));
//            setOnPreferenceChange(findPreference("updateLife"));

            try {
                PackageManager packageManager = getActivity().getPackageManager();
                PackageInfo info = packageManager.getPackageInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
                findPreference("appVersion").setSummary(info.versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void setOnPreferenceClick(Preference mPreference) {
            mPreference.setOnPreferenceClickListener(onPreferenceClickListener);
        }

        private Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                String getKey = preference.getKey();

                if ("license".equals(getKey)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(R.string.license_title);
                    builder.setMessage(R.string.license_message);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                } else if ("change_log".equals(getKey)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(R.string.change_log_title);
                    builder.setMessage(R.string.change_log_message);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                }

                return true;
            }
        };

//        private void setOnPreferenceChange(Preference mPreference) {
//            mPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);
//
//            if (mPreference instanceof ListPreference) {
//                ListPreference listPreference = (ListPreference) mPreference;
//                int index = listPreference.findIndexOfValue(listPreference.getValue());
//                mPreference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
//            } else if (mPreference instanceof EditTextPreference) {
//                String values = ((EditTextPreference) mPreference).getText();
//                if (values == null) values = "";
//                onPreferenceChangeListener.onPreferenceChange(mPreference, values);
//            }
//        }
//
//        private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
//
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                String stringValue = newValue.toString();
//
//                if (preference instanceof EditTextPreference) {
//                    preference.setSummary(stringValue);
//
//                } else if (preference instanceof ListPreference) {
//
//                    /**
//                     * ListPreference의 경우 stringValue가 entryValues이기 때문에 바로 Summary를
//                     * 적용하지 못한다 따라서 설정한 entries에서 String을 로딩하여 적용한다
//                     */
//                    ListPreference listPreference = (ListPreference) preference;
//                    int index = listPreference.findIndexOfValue(stringValue);
//
//                    preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
//                }
//
//                return true;
//            }
//        };
    }
}
