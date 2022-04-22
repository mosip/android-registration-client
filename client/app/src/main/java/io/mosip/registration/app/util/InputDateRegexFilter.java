package io.mosip.registration.app.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.EditText;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class InputDateRegexFilter implements InputFilter  {

    private static final String TAG = InputTextRegexFilter.class.getSimpleName();
    EditText date;
    EditText month;
    EditText year;
    EditText age;
    Pattern fieldPattern;

    public InputDateRegexFilter(EditText date, EditText month, EditText year, EditText age, String regex) {
        this.date = date;
        this.month = month;
        this.year = year;
        this.age = age;
        this.fieldPattern = Pattern.compile(regex);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (isValidValue(date.getText().toString()) && isValidValue(month.getText().toString()) &&
                isValidValue(year.getText().toString())) {
            try {
                LocalDate localDate = LocalDate.of(Integer.valueOf(year.getText().toString()),
                        Integer.valueOf(month.getText().toString()), Integer.valueOf(date.getText().toString()));

                if (LocalDate.now().compareTo(localDate) >= 0) {
                    String dob = localDate.format(DateTimeFormatter.ofPattern(RegistrationDto.dateFormatConfig));
                    age.setError((fieldPattern.matcher(dob).matches())  ? null : "Invalid value");
                }
            } catch (Exception ex) {
                Log.e(TAG, "Failed to validate date", ex);
            }
        }
        return null;
    }

    private boolean isValidValue(String value) {
        return value != null && !value.isEmpty();
    }
}
