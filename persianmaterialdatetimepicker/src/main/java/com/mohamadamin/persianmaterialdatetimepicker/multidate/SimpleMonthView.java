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

package com.mohamadamin.persianmaterialdatetimepicker.multidate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.mohamadamin.persianmaterialdatetimepicker.TypefaceHelper;
import com.mohamadamin.persianmaterialdatetimepicker.utils.LanguageUtils;

import java.util.Locale;

public class SimpleMonthView extends MonthView {
  private DatePickerController controller;

  public SimpleMonthView(Context context, AttributeSet attr, DatePickerController controller) {
    super(context, attr, controller);
    this.controller = controller;
  }

  @Override
  public void drawMonthDay(Canvas canvas, int year, int month, int day,
                           int x, int y, int startX, int stopX, int startY, int stopY) {
    boolean flag = false;
    for (int selectedDays : mSelectedDays) {
      if (day == selectedDays) {
        canvas.drawCircle(x, y - (MINI_DAY_NUMBER_TEXT_SIZE / 3), DAY_SELECTED_CIRCLE_SIZE,
          mSelectedCirclePaint);
        flag = true;
        break;
      }
    }

    if (isHighlighted(year, month, day)) {
      Typeface typefaceBold = Typeface.create(TypefaceHelper.get(getContext(), controller.getTypeface()), Typeface.BOLD);
      mMonthNumPaint.setTypeface(typefaceBold);
    } else {
      Typeface typefaceNormal = Typeface.create(TypefaceHelper.get(getContext(), controller.getTypeface()), Typeface.NORMAL);
      mMonthNumPaint.setTypeface(typefaceNormal);
    }

    // If we have a mindate or maxdate, gray out the day number if it's outside the range.
    if (isOutOfRange(year, month, day)) {
      mMonthNumPaint.setColor(mDisabledDayTextColor);
    } else if (flag) {
      mMonthNumPaint.setColor(mSelectedDayTextColor);
    } else if (mHasToday && mToday == day) {
      mMonthNumPaint.setColor(mTodayNumberColor);
    } else {
      mMonthNumPaint.setColor(isHighlighted(year, month, day) ? mHighlightedDayTextColor : mDayTextColor);
    }

    canvas.drawText(LanguageUtils.
      getPersianNumbers(String.format(Locale.getDefault(),"%d", day)), x, y, mMonthNumPaint);
  }
}
