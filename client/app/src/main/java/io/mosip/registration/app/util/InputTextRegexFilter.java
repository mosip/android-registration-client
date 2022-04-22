package io.mosip.registration.app.util;

import android.text.InputFilter;
import android.text.Spanned;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputTextRegexFilter implements InputFilter {

    private static final String TAG = InputTextRegexFilter.class.getSimpleName();
    TextInputLayout layout;
    Pattern fieldPattern;

    public InputTextRegexFilter(TextInputLayout layout, String regex) {
        this.layout = layout;
        this.fieldPattern = Pattern.compile(regex);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = fieldPattern.matcher(dest.toString()+source);
        layout.setErrorEnabled((!matcher.matches()));
        layout.setError((!matcher.matches()) ? "Invalid value" : null);
        return null;
    }
}
