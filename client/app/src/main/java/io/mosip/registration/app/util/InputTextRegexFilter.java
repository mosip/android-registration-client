package io.mosip.registration.app.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.EditText;
import com.google.android.material.textfield.TextInputLayout;
import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicTextBox;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputTextRegexFilter implements InputFilter {

    private static final String TAG = InputTextRegexFilter.class.getSimpleName();
    EditText editText;
    TextInputLayout layout;
    Pattern fieldPattern;

    public InputTextRegexFilter(TextInputLayout layout, String regex) {
        this.layout = layout;
        this.fieldPattern = Pattern.compile(regex);
    }

    public InputTextRegexFilter(EditText editText, String regex) {
        this.editText = editText;
        this.fieldPattern = Pattern.compile(regex);
    }


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = fieldPattern.matcher(dest.toString()+source);
        if(layout != null)
            layout.setError((!matcher.matches())  ? "Invalid value provided" : null);
        if(editText != null) {
            editText.setError((!matcher.matches())  ? "Invalid value provided" : null);
        }
        return null;
    }
}
