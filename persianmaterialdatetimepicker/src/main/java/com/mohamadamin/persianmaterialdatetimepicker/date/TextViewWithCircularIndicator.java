/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.mohamadamin.persianmaterialdatetimepicker.date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.mohamadamin.persianmaterialdatetimepicker.R;
import com.mohamadamin.persianmaterialdatetimepicker.utils.LanguageUtils;

/**
 * A text view which, when pressed or activated, displays a colored circle around the text.
 */
public class TextViewWithCircularIndicator extends AppCompatTextView {

    private static final int SELECTED_CIRCLE_ALPHA = 255;

    Paint mCirclePaint = new Paint();

    private final int mRadius;
    private final int mCircleColor;
    private final String mItemIsSelectedText;

    private boolean mDrawCircle;

    public TextViewWithCircularIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = context.getResources();
        mCircleColor = res.getColor(R.color.mdtp_accent_color);
        mRadius = res.getDimensionPixelOffset(R.dimen.mdtp_month_select_circle_radius);
        mItemIsSelectedText = context.getResources().getString(R.string.mdtp_item_is_selected);

        init();
    }

    private void init() {
        mCirclePaint.setFakeBoldText(true);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setTextAlign(Align.CENTER);
        mCirclePaint.setStyle(Style.FILL);
        mCirclePaint.setAlpha(SELECTED_CIRCLE_ALPHA);
    }

    public void drawIndicator(boolean drawCircle) {
        mDrawCircle = drawCircle;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        if (mDrawCircle) {
            final int width = getWidth();
            final int height = getHeight();
            int radius = Math.min(width, height) / 2;
            canvas.drawCircle(width / 2, height / 2, radius, mCirclePaint);
        }
        setSelected(mDrawCircle);
        super.onDraw(canvas);
    }

    @SuppressLint("GetContentDescriptionOverride")
    @Override
    public CharSequence getContentDescription() {
        String itemText = LanguageUtils.getPersianNumbers(getText().toString());
        if (mDrawCircle) {
            return String.format(mItemIsSelectedText, itemText);
        } else {
            return itemText;
        }
    }
}
