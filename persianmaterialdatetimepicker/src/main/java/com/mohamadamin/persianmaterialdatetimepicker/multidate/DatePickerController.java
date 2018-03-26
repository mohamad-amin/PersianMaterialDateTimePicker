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

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.ArrayList;

/**
 * Controller class to communicate among the various components of the date picker dialog.
 */
public interface DatePickerController {

  void onYearSelected(int year);

  void onDaysOfMonthSelected(ArrayList<PersianCalendar> selectedDays);

  void registerOnDateChangedListener(MultiDatePickerDialog.OnDateChangedListener listener);

  void unregisterOnDateChangedListener(MultiDatePickerDialog.OnDateChangedListener listener);

  ArrayList<PersianCalendar> getSelectedDays();

  void setSelectedDays(ArrayList<PersianCalendar> selectedDays);

  boolean isThemeDark();

  PersianCalendar[] getHighlightedDays();

  PersianCalendar[] getSelectableDays();

  int getFirstDayOfWeek();

  int getMinYear();

  int getMaxYear();

  int getSelectedYear();

  PersianCalendar getMinDate();

  PersianCalendar getMaxDate();

  void tryVibrate();

  void setTypeface(String fontName);

  String getTypeface();
}
