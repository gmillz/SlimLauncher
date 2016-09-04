/*
 * Copyright (C) 2015 The SlimRoms Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slim.launcher.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.launcher3.R;

public class DoubleNumberPickerPreference extends DialogPreference {
    private int mMin1, mMax1, mDefault1;
    private int mMin2, mMax2, mDefault2;

    private String mMaxExternalKey1, mMinExternalKey1;
    private String mMaxExternalKey2, mMinExternalKey2;

    private String mPickerTitle1;
    private String mPickerTitle2;

    private NumberPicker mNumberPicker1;
    private NumberPicker mNumberPicker2;

    public DoubleNumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray doubleNumberPickerType = context.obtainStyledAttributes(attrs,
                R.styleable.DoubleNumberPickerPreference, 0, 0);

        mMaxExternalKey1 = doubleNumberPickerType.getString(
                R.styleable.DoubleNumberPickerPreference_maxExternal1);
        mMinExternalKey1 = doubleNumberPickerType.getString(
                R.styleable.DoubleNumberPickerPreference_minExternal1);
        mMaxExternalKey2 = doubleNumberPickerType.getString(
                R.styleable.DoubleNumberPickerPreference_maxExternal2);
        mMinExternalKey2 = doubleNumberPickerType.getString(
                R.styleable.DoubleNumberPickerPreference_minExternal2);

        mPickerTitle1 = doubleNumberPickerType.getString(
                R.styleable.DoubleNumberPickerPreference_pickerTitle1);
        mPickerTitle2 = doubleNumberPickerType.getString(
                R.styleable.DoubleNumberPickerPreference_pickerTitle2);

        mMax1 = doubleNumberPickerType.getInt(R.styleable.DoubleNumberPickerPreference_max1, 5);
        mMin1 = doubleNumberPickerType.getInt(R.styleable.DoubleNumberPickerPreference_min1, 0);
        mMax2 = doubleNumberPickerType.getInt(R.styleable.DoubleNumberPickerPreference_max2, 5);
        mMin2 = doubleNumberPickerType.getInt(R.styleable.DoubleNumberPickerPreference_min2, 0);

        mDefault1 = doubleNumberPickerType.getInt(
                R.styleable.DoubleNumberPickerPreference_defaultValue1, mMin1);
        mDefault2 = doubleNumberPickerType.getInt(
                R.styleable.DoubleNumberPickerPreference_defaultValue2, mMin2);

        doubleNumberPickerType.recycle();
    }

    @Override
    protected View onCreateDialogView() {
        int max1 = mMax1;
        int min1 = mMin1;
        int max2 = mMax2;
        int min2 = mMin2;

        // External values
        if (mMaxExternalKey1 != null) {
            max1 = getSharedPreferences().getInt(mMaxExternalKey1, mMax1);
        }
        if (mMinExternalKey1 != null) {
            min1 = getSharedPreferences().getInt(mMinExternalKey1, mMin1);
        }
        if (mMaxExternalKey2 != null) {
            max2 = getSharedPreferences().getInt(mMaxExternalKey2, mMax2);
        }
        if (mMinExternalKey2 != null) {
            min2 = getSharedPreferences().getInt(mMinExternalKey2, mMin2);
        }

        View view = View.inflate(getContext(), R.layout.double_number_picker_dialog, null);

        mNumberPicker1 = (NumberPicker) view.findViewById(R.id.number_picker_1);
        mNumberPicker2 = (NumberPicker) view.findViewById(R.id.number_picker_2);

        if (mNumberPicker1 == null || mNumberPicker2 == null) {
            throw new RuntimeException("mNumberPicker1 or mNumberPicker2 is null!");
        }

        // Initialize state
        mNumberPicker1.setMaxValue(max1);
        mNumberPicker1.setMinValue(min1);
        mNumberPicker1.setValue(getPersistedValue(1));
        mNumberPicker1.setWrapSelectorWheel(false);
        mNumberPicker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumberPicker2.setMaxValue(max2);
        mNumberPicker2.setMinValue(min2);
        mNumberPicker2.setValue(getPersistedValue(2));
        mNumberPicker2.setWrapSelectorWheel(false);
        mNumberPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // Titles
        TextView pickerTitle1 = (TextView) view.findViewById(R.id.picker_title_1);
        TextView pickerTitle2 = (TextView) view.findViewById(R.id.picker_title_2);

        if (pickerTitle1 != null && pickerTitle2 != null) {
            pickerTitle1.setText(mPickerTitle1);
            pickerTitle2.setText(mPickerTitle2);
        }

        return view;
    }

    private int getPersistedValue(int value) {
        String[] values = getPersistedString(mDefault1 + "|" + mDefault2).split("\\|");
        if (value == 1) {
            try {
                return Integer.parseInt(values[0]);
            } catch (NumberFormatException e) {
                return mDefault1;
            }
        } else {
            try {
                return Integer.parseInt(values[1]);
            } catch (NumberFormatException e) {
                return mDefault2;
            }
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistString(mNumberPicker1.getValue() + "|" + mNumberPicker2.getValue());
        }
    }

    @SuppressWarnings("unused")
    public void setMin1(int min) {
        mMin1 = min;
    }

    @SuppressWarnings("unused")
    public void setMax1(int max) {
        mMax1 = max;
    }

    @SuppressWarnings("unused")
    public void setMin2(int min) {
        mMin2 = min;
    }

    @SuppressWarnings("unused")
    public void setMax2(int max) {
        mMax2 = max;
    }

    @SuppressWarnings("unused")
    public void setDefault1(int def) {
        mDefault1 = def;
    }

    @SuppressWarnings("unused")
    public void setDefault2(int def) {
        mDefault2 = def;
    }

}
