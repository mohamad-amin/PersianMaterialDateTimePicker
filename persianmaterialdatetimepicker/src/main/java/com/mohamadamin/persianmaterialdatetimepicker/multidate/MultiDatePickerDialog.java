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

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mohamadamin.persianmaterialdatetimepicker.HapticFeedbackController;
import com.mohamadamin.persianmaterialdatetimepicker.R;
import com.mohamadamin.persianmaterialdatetimepicker.TypefaceHelper;
import com.mohamadamin.persianmaterialdatetimepicker.Utils;
import com.mohamadamin.persianmaterialdatetimepicker.date.AccessibleDateAnimator;
import com.mohamadamin.persianmaterialdatetimepicker.utils.LanguageUtils;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

/**
 * Dialog allowing users to select a date.
 */
public class MultiDatePickerDialog extends DialogFragment implements
  OnClickListener, DatePickerController {

  private static final String TAG = "MultiDatePickerDialog";

  private static final int UNINITIALIZED = -1;
  private static final int MONTH_AND_DAY_VIEW = 0;
  private static final int YEAR_VIEW = 1;

  private static final String KEY_SELECTED_DAYS = "selectedDays";
  private static final String KEY_LIST_POSITION = "list_position";
  private static final String KEY_WEEK_START = "week_start";
  private static final String KEY_YEAR_START = "year_start";
  private static final String KEY_YEAR_END = "year_end";
  private static final String KEY_CURRENT_VIEW = "current_view";
  private static final String KEY_LIST_POSITION_OFFSET = "list_position_offset";
  private static final String KEY_MIN_DATE = "min_date";
  private static final String KEY_MAX_DATE = "max_date";
  private static final String KEY_SELECTED_YEAR = "selected_year";
  private static final String KEY_HIGHLIGHTED_DAYS = "highlighted_days";
  private static final String KEY_SELECTABLE_DAYS = "selectable_days";
  private static final String KEY_THEME_DARK = "theme_dark";

  private static final int DEFAULT_START_YEAR = 1350;
  private static final int DEFAULT_END_YEAR = 1450;

  private static final int ANIMATION_DURATION = 300;
  private static final int ANIMATION_DELAY = 500;

  private final ArrayList<PersianCalendar> mSelectedDaysCalendars = new ArrayList<>();
  private OnDateSetListener mCallBack;
  private HashSet<OnDateChangedListener> mListeners = new HashSet<>();
  private DialogInterface.OnCancelListener mOnCancelListener;
  private DialogInterface.OnDismissListener mOnDismissListener;

  private AccessibleDateAnimator mAnimator;

  private TextView mDayOfWeekView;
  private LinearLayout mMonthAndDayView;
  private TextView mSelectedMonthTextView;
  private TextView mSelectedDayTextView;
  private TextView mYearView;
  private DayPickerView mDayPickerView;
  private YearPickerView mYearPickerView;

  private int mCurrentView = UNINITIALIZED;

  private int mWeekStart = PersianCalendar.SATURDAY;
  private int mMinYear = DEFAULT_START_YEAR;
  private int mMaxYear = DEFAULT_END_YEAR;
  private int mSelectedYear;
  private PersianCalendar mMinDate;
  private PersianCalendar mMaxDate;
  private PersianCalendar[] highlightedDays;
  private PersianCalendar[] selectableDays;
  private boolean mThemeDark;

  private HapticFeedbackController mHapticFeedbackController;

  private boolean mDelayAnimation = true;

  // Accessibility strings.
  private String mDayPickerDescription;
  private String mSelectDay;
  private String mYearPickerDescription;
  private String mSelectYear;

  private String fontName;

  /**
   * The callback used to indicate the user is done filling in the date.
   */
  public interface OnDateSetListener {

    /**
     * @param view         The view associated with this listener.
     * @param selectedDays List of days that have been selected.
     */
    void onDateSet(MultiDatePickerDialog view, ArrayList<PersianCalendar> selectedDays);
  }

  /**
   * The callback used to notify other date picker components of a change in selected date.
   */
  public interface OnDateChangedListener {

    void onDateChanged();
  }


  public MultiDatePickerDialog() {
    // Empty constructor required for dialog fragment.
  }

  /**
   * @param callBack     How the parent is notified that the date is set.
   * @param selectedDays Selected days shown on date picker.
   */
  public static MultiDatePickerDialog newInstance(OnDateSetListener callBack, @Nullable ArrayList<PersianCalendar> selectedDays) {
    MultiDatePickerDialog ret = new MultiDatePickerDialog();
    ret.initialize(callBack, selectedDays);
    return ret;
  }

  public void initialize(OnDateSetListener callBack, @Nullable ArrayList<PersianCalendar> selectedDays) {
    mCallBack = callBack;
    if (selectedDays != null) {
      setSelectedDays(selectedDays);
    } else {
      mSelectedDaysCalendars.add(new PersianCalendar(System.currentTimeMillis()));
    }
    mSelectedYear = mSelectedDaysCalendars.get(mSelectedDaysCalendars.size() - 1).getPersianYear();
    mThemeDark = false;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final Activity activity = getActivity();
    activity.getWindow().setSoftInputMode(
      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    if (savedInstanceState != null) {
      mSelectedDaysCalendars.clear();
      mSelectedDaysCalendars.addAll((ArrayList<PersianCalendar>) savedInstanceState.getSerializable(KEY_SELECTED_DAYS));
    }
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(KEY_SELECTED_DAYS, mSelectedDaysCalendars);
    outState.putInt(KEY_WEEK_START, mWeekStart);
    outState.putInt(KEY_YEAR_START, mMinYear);
    outState.putInt(KEY_YEAR_END, mMaxYear);
    outState.putInt(KEY_CURRENT_VIEW, mCurrentView);
    int listPosition = -1;
    if (mCurrentView == MONTH_AND_DAY_VIEW) {
      listPosition = mDayPickerView.getMostVisiblePosition();
    } else if (mCurrentView == YEAR_VIEW) {
      listPosition = mYearPickerView.getFirstVisiblePosition();
      outState.putInt(KEY_LIST_POSITION_OFFSET, mYearPickerView.getFirstPositionOffset());
    }
    outState.putInt(KEY_LIST_POSITION, listPosition);
    outState.putSerializable(KEY_MIN_DATE, mMinDate);
    outState.putSerializable(KEY_MAX_DATE, mMaxDate);
    outState.putSerializable(KEY_SELECTED_YEAR, mSelectedYear);
    outState.putSerializable(KEY_HIGHLIGHTED_DAYS, highlightedDays);
    outState.putSerializable(KEY_SELECTABLE_DAYS, selectableDays);
    outState.putBoolean(KEY_THEME_DARK, mThemeDark);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView: ");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

    View view = inflater.inflate(R.layout.mdtp_date_picker_dialog, null);
    final Activity activity = getActivity();
    mDayOfWeekView = view.findViewById(R.id.date_picker_header);
    if (mDayOfWeekView != null) {
      mDayOfWeekView.setTypeface(TypefaceHelper.get(activity, fontName));
    }
    mMonthAndDayView = view.findViewById(R.id.date_picker_month_and_day);
    mMonthAndDayView.setOnClickListener(this);
    mSelectedMonthTextView = view.findViewById(R.id.date_picker_month);
    mSelectedMonthTextView.setTypeface(TypefaceHelper.get(activity, fontName));
    mSelectedDayTextView = view.findViewById(R.id.date_picker_day);
    mSelectedDayTextView.setTypeface(TypefaceHelper.get(activity, fontName));
    mYearView = view.findViewById(R.id.date_picker_year);
    mYearView.setTypeface(TypefaceHelper.get(activity, fontName));
    mYearView.setOnClickListener(this);

    int listPosition = -1;
    int listPositionOffset = 0;
    int currentView = MONTH_AND_DAY_VIEW;
    if (savedInstanceState != null) {
      mWeekStart = savedInstanceState.getInt(KEY_WEEK_START);
      mMinYear = savedInstanceState.getInt(KEY_YEAR_START);
      mMaxYear = savedInstanceState.getInt(KEY_YEAR_END);
      mSelectedYear = savedInstanceState.getInt(KEY_SELECTED_YEAR);
      currentView = savedInstanceState.getInt(KEY_CURRENT_VIEW);
      listPosition = savedInstanceState.getInt(KEY_LIST_POSITION);
      listPositionOffset = savedInstanceState.getInt(KEY_LIST_POSITION_OFFSET);
      mMinDate = (PersianCalendar) savedInstanceState.getSerializable(KEY_MIN_DATE);
      mMaxDate = (PersianCalendar) savedInstanceState.getSerializable(KEY_MAX_DATE);
      highlightedDays = (PersianCalendar[]) savedInstanceState.getSerializable(KEY_HIGHLIGHTED_DAYS);
      selectableDays = (PersianCalendar[]) savedInstanceState.getSerializable(KEY_SELECTABLE_DAYS);
      mThemeDark = savedInstanceState.getBoolean(KEY_THEME_DARK);
    }

    mDayPickerView = new SimpleDayPickerView(activity, this);
    mYearPickerView = new YearPickerView(activity, this);

    Resources res = getResources();
    mDayPickerDescription = res.getString(R.string.mdtp_day_picker_description);
    mSelectDay = res.getString(R.string.mdtp_select_day);
    mYearPickerDescription = res.getString(R.string.mdtp_year_picker_description);
    mSelectYear = res.getString(R.string.mdtp_select_year);

    int bgColorResource = mThemeDark ? R.color.mdtp_date_picker_view_animator_dark_theme : R.color.mdtp_date_picker_view_animator;
    view.setBackgroundColor(activity.getResources().getColor(bgColorResource));

    mAnimator = view.findViewById(R.id.animator);
    mAnimator.addView(mDayPickerView);
    mAnimator.addView(mYearPickerView);
    mAnimator.setDateMillis(mSelectedDaysCalendars.get(mSelectedDaysCalendars.size() - 1).getTimeInMillis());
    // TODO: Replace with animation decided upon by the design team.
    Animation animation = new AlphaAnimation(0.0f, 1.0f);
    animation.setDuration(ANIMATION_DURATION);
    mAnimator.setInAnimation(animation);
    // TODO: Replace with animation decided upon by the design team.
    Animation animation2 = new AlphaAnimation(1.0f, 0.0f);
    animation2.setDuration(ANIMATION_DURATION);
    mAnimator.setOutAnimation(animation2);

    Button okButton = view.findViewById(R.id.ok);
    okButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        tryVibrate();
        if (mCallBack != null) {
          mCallBack.onDateSet(MultiDatePickerDialog.this, mSelectedDaysCalendars);
        }
        dismiss();
      }
    });
    okButton.setTypeface(TypefaceHelper.get(activity, fontName));

    Button cancelButton = view.findViewById(R.id.cancel);
    cancelButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        tryVibrate();
        getDialog().cancel();
      }
    });
    cancelButton.setTypeface(TypefaceHelper.get(activity, fontName));
    cancelButton.setVisibility(isCancelable() ? View.VISIBLE : View.GONE);

    updateDisplay(false);
    setCurrentView(currentView);

    if (listPosition != -1) {
      if (currentView == MONTH_AND_DAY_VIEW) {
        mDayPickerView.postSetSelection(listPosition);
      } else if (currentView == YEAR_VIEW) {
        mYearPickerView.postSetSelectionFromTop(listPosition, listPositionOffset);
      }
    }

    mHapticFeedbackController = new HapticFeedbackController(activity);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    mHapticFeedbackController.start();
  }

  @Override
  public void onPause() {
    super.onPause();
    mHapticFeedbackController.stop();
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    super.onCancel(dialog);
    if (mOnCancelListener != null) {
      mOnCancelListener.onCancel(dialog);
    }
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (mOnDismissListener != null) {
      mOnDismissListener.onDismiss(dialog);
    }
  }

  private void setCurrentView(final int viewIndex) {

    switch (viewIndex) {
      case MONTH_AND_DAY_VIEW:
        ObjectAnimator pulseAnimator = Utils.getPulseAnimator(mMonthAndDayView, 0.9f,
          1.05f);
        if (mDelayAnimation) {
          pulseAnimator.setStartDelay(ANIMATION_DELAY);
          mDelayAnimation = false;
        }
        mDayPickerView.onDateChanged();
        if (mCurrentView != viewIndex) {
          mMonthAndDayView.setSelected(true);
          mYearView.setSelected(false);
          mAnimator.setDisplayedChild(MONTH_AND_DAY_VIEW);
          mCurrentView = viewIndex;
        }
        pulseAnimator.start();

        String dayString = LanguageUtils.getPersianNumbers(mSelectedDaysCalendars.get(mSelectedDaysCalendars.size() - 1).getPersianLongDate());
        mAnimator.setContentDescription(mDayPickerDescription + ": " + dayString);
        Utils.tryAccessibilityAnnounce(mAnimator, mSelectDay);
        break;
      case YEAR_VIEW:
        pulseAnimator = Utils.getPulseAnimator(mYearView, 0.85f, 1.1f);
        if (mDelayAnimation) {
          pulseAnimator.setStartDelay(ANIMATION_DELAY);
          mDelayAnimation = false;
        }
        mYearPickerView.onDateChanged();
        if (mCurrentView != viewIndex) {
          mMonthAndDayView.setSelected(false);
          mYearView.setSelected(true);
          mAnimator.setDisplayedChild(YEAR_VIEW);
          mCurrentView = viewIndex;
        }
        pulseAnimator.start();

        String yearString = LanguageUtils.
          getPersianNumbers(String.valueOf(mSelectedDaysCalendars.get(mSelectedDaysCalendars.size() - 1).getPersianYear()));
        mAnimator.setContentDescription(mYearPickerDescription + ": " + yearString);
        Utils.tryAccessibilityAnnounce(mAnimator, mSelectYear);
        break;
    }
  }

  private void updateDisplay(boolean announce) {
    if (mSelectedDaysCalendars.size() == 0) {
      return;
    }
    PersianCalendar target = mSelectedDaysCalendars.get(mSelectedDaysCalendars.size() - 1);
    if (mDayOfWeekView != null) {
      mDayOfWeekView.setText(target.getPersianWeekDayName());
    }

    mSelectedMonthTextView.setText(LanguageUtils.
      getPersianNumbers(target.getPersianMonthName()));
    mSelectedDayTextView.setText(LanguageUtils.
      getPersianNumbers(String.valueOf(target.getPersianDay())));
    mYearView.setText(LanguageUtils.
      getPersianNumbers(String.valueOf(mSelectedYear)));

    // Accessibility.
    long millis = target.getTimeInMillis();
    mAnimator.setDateMillis(millis);
    String monthAndDayText = LanguageUtils.getPersianNumbers(
      target.getPersianMonthName() + " " +
        target.getPersianDay()
    );
    mMonthAndDayView.setContentDescription(monthAndDayText);

    if (announce) {
      String fullDateText = LanguageUtils.
        getPersianNumbers(target.getPersianLongDate());
      Utils.tryAccessibilityAnnounce(mAnimator, fullDateText);
    }
  }

  /**
   * Set whether the dark theme should be used
   *
   * @param themeDark true if the dark theme should be used, false if the default theme should be used
   */
  public void setThemeDark(boolean themeDark) {
    mThemeDark = themeDark;
  }

  /**
   * Returns true when the dark theme should be used
   *
   * @return true if the dark theme should be used, false if the default theme should be used
   */
  @Override
  public boolean isThemeDark() {
    return mThemeDark;
  }

  @SuppressWarnings("unused")
  public void setFirstDayOfWeek(int startOfWeek) {
    if (startOfWeek < Calendar.SUNDAY || startOfWeek > Calendar.SATURDAY) {
      throw new IllegalArgumentException("Value must be between Calendar.SUNDAY and " +
        "Calendar.SATURDAY");
    }
    mWeekStart = startOfWeek;
    if (mDayPickerView != null) {
      mDayPickerView.onChange();
    }
  }

  @SuppressWarnings("unused")
  public void setYearRange(int startYear, int endYear) {
    if (endYear < startYear) {
      throw new IllegalArgumentException("Year end must be larger than or equal to year start");
    }

    mMinYear = startYear;
    mMaxYear = endYear;
    if (mDayPickerView != null) {
      mDayPickerView.onChange();
    }
  }

  /**
   * Sets the minimal date supported by this DatePicker. Dates before (but not including) the
   * specified date will be disallowed from being selected.
   *
   * @param calendar a Calendar object set to the year, month, day desired as the mindate.
   */
  @SuppressWarnings("unused")
  public void setMinDate(PersianCalendar calendar) {
    mMinDate = calendar;

    if (mDayPickerView != null) {
      mDayPickerView.onChange();
    }
  }

  /**
   * @return The minimal date supported by this DatePicker. Null if it has not been set.
   */
  @Override
  public PersianCalendar getMinDate() {
    return mMinDate;
  }

  /**
   * Sets the minimal date supported by this DatePicker. Dates after (but not including) the
   * specified date will be disallowed from being selected.
   *
   * @param calendar a Calendar object set to the year, month, day desired as the maxdate.
   */
  @SuppressWarnings("unused")
  public void setMaxDate(PersianCalendar calendar) {
    mMaxDate = calendar;

    if (mDayPickerView != null) {
      mDayPickerView.onChange();
    }
  }

  /**
   * @return The maximal date supported by this DatePicker. Null if it has not been set.
   */
  @Override
  public PersianCalendar getMaxDate() {
    return mMaxDate;
  }

  /**
   * Sets an array of dates which should be highlighted when the picker is drawn
   *
   * @param highlightedDays an Array of Calendar objects containing the dates to be highlighted
   */
  @SuppressWarnings("unused")
  public void setHighlightedDays(PersianCalendar[] highlightedDays) {
    // Sort the array to optimize searching over it later on
    Arrays.sort(highlightedDays);
    this.highlightedDays = highlightedDays;
  }

  /**
   * @return The list of dates, as Calendar Objects, which should be highlighted. null is no dates should be highlighted
   */
  @Override
  public PersianCalendar[] getHighlightedDays() {
    return highlightedDays;
  }

  /**
   * Set's a list of days which are the only valid selections.
   * Setting this value will take precedence over using setMinDate() and setMaxDate()
   *
   * @param selectableDays an Array of Calendar Objects containing the selectable dates
   */
  @SuppressWarnings("unused")
  public void setSelectableDays(PersianCalendar[] selectableDays) {
    // Sort the array to optimize searching over it later on
    Arrays.sort(selectableDays);
    this.selectableDays = selectableDays;
  }

  /**
   * @return an Array of Calendar objects containing the list with selectable items. null if no restriction is set
   */
  @Override
  public PersianCalendar[] getSelectableDays() {
    return selectableDays;
  }

  @SuppressWarnings("unused")
  public void setOnDateSetListener(OnDateSetListener listener) {
    mCallBack = listener;
  }

  @SuppressWarnings("unused")
  public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
    mOnCancelListener = onCancelListener;
  }

  @SuppressWarnings("unused")
  public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
    mOnDismissListener = onDismissListener;
  }

  // If the newly selected month / year does not contain the currently selected day number,
  // change the selected day number to the last day of the selected month or year.
  //      e.g. Switching from Mar to Apr when Mar 31 is selected -> Apr 30
  //      e.g. Switching from 2012 to 2013 when Feb 29, 2012 is selected -> Feb 28, 2013
  private void adjustDayInMonthIfNeeded(int month, int year) {
//        int day = mPersianCalendar.getPersianDay();
//        int daysInMonth = Utils.getDaysInMonth(month, year);
//        if (day > daysInMonth) {
//            mPersianCalendar.setPersianDate(Persian);
//        } TODO
  }

  @Override
  public void onClick(View v) {
    tryVibrate();
    if (v.getId() == R.id.date_picker_year) {
      setCurrentView(YEAR_VIEW);
    } else if (v.getId() == R.id.date_picker_month_and_day) {
      setCurrentView(MONTH_AND_DAY_VIEW);
    }
  }

  @Override
  public void onYearSelected(int year) {
    mSelectedYear = year;
    adjustDayInMonthIfNeeded(mSelectedDaysCalendars.get(mSelectedDaysCalendars.size() - 1).getPersianMonth(), year);
    if (mSelectedDaysCalendars.size() == 1) {
      mSelectedDaysCalendars.get(mSelectedDaysCalendars.size() - 1).setPersianDate(year
        , mSelectedDaysCalendars.get(mSelectedDaysCalendars.size() - 1).getPersianMonth(),
        mSelectedDaysCalendars.get(mSelectedDaysCalendars.size() - 1).getPersianDay());
    }
    updatePickers();
    setCurrentView(MONTH_AND_DAY_VIEW);
    updateDisplay(true);
  }

  @Override
  public void onDaysOfMonthSelected(ArrayList<PersianCalendar> selectedDays) {
    //setSelectedDays(selectedDays);
    mSelectedYear = selectedDays.get(selectedDays.size() - 1).getPersianYear();
    updatePickers();
    updateDisplay(true);
  }

  private void updatePickers() {
    for (OnDateChangedListener listener : mListeners) {
      listener.onDateChanged();
    }
  }


  @Override
  public ArrayList<PersianCalendar> getSelectedDays() {
    return mSelectedDaysCalendars;
  }

  @Override
  public void setSelectedDays(ArrayList<PersianCalendar> selectedDays) {
    mSelectedDaysCalendars.clear();
    mSelectedDaysCalendars.addAll(selectedDays);
  }

  @Override
  public int getMinYear() {
    if (selectableDays != null) {
      return selectableDays[0].getPersianYear();
    }
    // Ensure no years can be selected outside of the given minimum date
    return mMinDate != null && mMinDate.getPersianYear() > mMinYear ? mMinDate.getPersianYear() : mMinYear;
  }

  @Override
  public int getMaxYear() {
    if (selectableDays != null) {
      return selectableDays[selectableDays.length - 1].getPersianYear();
    }
    // Ensure no years can be selected outside of the given maximum date
    return mMaxDate != null && mMaxDate.getPersianYear() < mMaxYear ? mMaxDate.getPersianYear() : mMaxYear;
  }

  @Override
  public int getSelectedYear() {
    return mSelectedYear;
  }

  @Override
  public int getFirstDayOfWeek() {
    return mWeekStart;
  }

  @Override
  public void registerOnDateChangedListener(OnDateChangedListener listener) {
    mListeners.add(listener);
  }

  @Override
  public void unregisterOnDateChangedListener(OnDateChangedListener listener) {
    mListeners.remove(listener);
  }

  @Override
  public void tryVibrate() {
    mHapticFeedbackController.tryVibrate();
  }

  @Override
  public void setTypeface(String fontName) {
    this.fontName = fontName;
  }

  @Override
  public String getTypeface() {
    return fontName;
  }
}
