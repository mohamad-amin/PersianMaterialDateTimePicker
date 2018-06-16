/**
 * Persian Calendar see: http://code.google.com/p/persian-calendar/
 * Copyright (C) 2012  Mortezaadi@gmail.com
 * PersianCalendar.java
 * Persian Calendar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mohamadamin.persianmaterialdatetimepicker.utils;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class PersianCalendar extends GregorianCalendar {

  private int persianYear;
  private int persianMonth;
  private int persianDay;
  // use to separate PersianDate's field and also Parse the DateString based
  // on this delimiter
  private String delimiter = "/";

  private long convertToMilis(long julianDate) {
    return PersianCalendarConstants.MILLIS_JULIAN_EPOCH
      + julianDate
      * PersianCalendarConstants.MILLIS_OF_A_DAY
      + PersianCalendarUtils.ceil(getTimeInMillis()
        - PersianCalendarConstants.MILLIS_JULIAN_EPOCH,
      PersianCalendarConstants.MILLIS_OF_A_DAY);
  }

  /**
   * default constructor
   * <p>
   * most of the time we don't care about TimeZone when we persisting Date or
   * doing some calculation on date. <strong> Default TimeZone was set to
   * "GMT" </strong> in order to make developer to work more convenient with
   * the library; however you can change the TimeZone as you do in
   * GregorianCalendar by calling setTimeZone()
   */
  public PersianCalendar() {
    super(TimeZone.getDefault(), Locale.getDefault());
  }

  /**
   * Calculate persian date from current Date and populates the corresponding
   * fields(persianYear, persianMonth, persianDay)
   */
  protected void calculatePersianDate() {
    YearMonthDay persianYearMonthDay = PersianCalendar.gregorianToJalali(new YearMonthDay(this.get(PersianCalendar.YEAR), this.get(PersianCalendar.MONTH), this.get(PersianCalendar.DAY_OF_MONTH)));
    this.persianYear = persianYearMonthDay.year;
    this.persianMonth = persianYearMonthDay.month;
    this.persianDay = persianYearMonthDay.day;
  }

  /**
   * Determines if the given year is a leap year in persian calendar. Returns
   * true if the given year is a leap year.
   *
   * @return boolean
   */
  public boolean isPersianLeapYear() {
    return PersianCalendarUtils.isPersianLeapYear(this.persianYear);
  }

  /**
   * set the persian date it converts PersianDate to the Julian and assigned
   * equivalent milliseconds to the instance
   *
   * @param persianYear
   * @param persianMonth
   * @param persianDay
   */
  public void setPersianDate(int persianYear, int persianMonth, int persianDay) {
    persianMonth += 1; // TODO
    this.persianYear = persianYear;
    this.persianMonth = persianMonth;
    this.persianDay = persianDay;
    YearMonthDay gregorianYearMonthDay = persianToGregorian(new YearMonthDay(persianYear, this.persianMonth - 1, persianDay));
    this.set(gregorianYearMonthDay.year, gregorianYearMonthDay.month, gregorianYearMonthDay.day);
  }

  public int getPersianYear() {
    // calculatePersianDate();
    return this.persianYear;
  }

  /**
   * @return int persian month number
   */
  public int getPersianMonth() {
    return this.persianMonth;
  }

  /**
   * @return String persian month name
   */
  public String getPersianMonthName() {
    return PersianCalendarConstants.persianMonthNames[this.persianMonth];
  }

  /**
   * @return int Persian day in month
   */
  public int getPersianDay() {
    return this.persianDay;
  }

  public String getPersianLongDateAndTime() {
    return getPersianLongDate() + " ساعت " + get(HOUR_OF_DAY) + ":" + get(MINUTE) + ":" + get(SECOND);
  }

  /**
   * @return String Name of the day in week
   */
  public String getPersianWeekDayName() {
    switch (get(DAY_OF_WEEK)) {
      case SATURDAY:
        return PersianCalendarConstants.persianWeekDays[0];
      case SUNDAY:
        return PersianCalendarConstants.persianWeekDays[1];
      case MONDAY:
        return PersianCalendarConstants.persianWeekDays[2];
      case TUESDAY:
        return PersianCalendarConstants.persianWeekDays[3];
      case WEDNESDAY:
        return PersianCalendarConstants.persianWeekDays[4];
      case THURSDAY:
        return PersianCalendarConstants.persianWeekDays[5];
      default:
        return PersianCalendarConstants.persianWeekDays[6];
    }

  }

  /**
   * @return String of Persian Date ex: شنبه 01 خرداد 1361
   */
  public String getPersianLongDate() {
    return getPersianWeekDayName() + "  "
      + formatToMilitary(this.persianDay) + "  "
      + getPersianMonthName() + "  " + this.persianYear;

  }

  /**
   * @return String of persian date formatted by
   * 'YYYY[delimiter]mm[delimiter]dd' default delimiter is '/'
   */
  public String getPersianShortDate() {
    return "" + formatToMilitary(this.persianYear) + delimiter
      + formatToMilitary(getPersianMonth()) + delimiter
      + formatToMilitary(this.persianDay);
  }

  private String formatToMilitary(int i) {
    return (i < 9) ? "0" + i : String.valueOf(i);
  }

  /**
   * add specific amout of fields to the current date for now doesnt handle
   * before 1 farvardin hejri (before epoch)
   *
   * @param field
   * @param amount <pre>
   *                                                                                                                  Usage:
   *                                                                                                                  {@code
   *                                                                                                                  addPersianDate(Calendar.YEAR, 2);
   *                                                                                                                  addPersianDate(Calendar.MONTH, 3);
   *                                                                                                                  }
   *                                                                                                                 </pre>
   *               <p>
   *               u can also use Calendar.HOUR_OF_DAY,Calendar.MINUTE,
   *               Calendar.SECOND, Calendar.MILLISECOND etc
   */
  //
  public void addPersianDate(int field, int amount) {
    if (amount == 0) {
      return; // Do nothing!
    }

    if (field < 0 || field >= ZONE_OFFSET) {
      throw new IllegalArgumentException();
    }

    if (field == YEAR) {
      setPersianDate(this.persianYear + amount, getPersianMonth(),
        this.persianDay);
      return;
    } else if (field == MONTH) {
      setPersianDate(this.persianYear
          + ((getPersianMonth() + amount) / 12),
        (getPersianMonth() + amount) % 12, this.persianDay);
      return;
    }
    add(field, amount);
    calculatePersianDate();
  }

  /**
   * <pre>
   *    use <code>{@link PersianDateParser}</code> to parse string
   *    and get the Persian Date.
   * </pre>
   *
   * @param dateString
   * @see PersianDateParser
   */
  public void parse(String dateString) {
    PersianCalendar p = new PersianDateParser(dateString, delimiter)
      .getPersianDate();
    setPersianDate(p.getPersianYear(), p.getPersianMonth(),
      p.getPersianDay());
  }

  public String getDelimiter() {
    return delimiter;
  }

  /**
   * assign delimiter to use as a separator of date fields.
   *
   * @param delimiter
   */
  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  @Override
  public String toString() {
    String str = super.toString();
    return str.substring(0, str.length() - 1) + ",PersianDate="
      + getPersianShortDate() + "]";
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);

  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public void set(int field, int value) {
    super.set(field, value);
    calculatePersianDate();
  }

  @Override
  public void setTimeInMillis(long millis) {
    super.setTimeInMillis(millis);
    calculatePersianDate();
  }

  @Override
  public void setTimeZone(TimeZone zone) {
    super.setTimeZone(zone);
    calculatePersianDate();
  }

  // Helper Functions
  private static int gregorianDaysInMonth[] = {31, 28, 31, 30, 31, 30, 31,
    31, 30, 31, 30, 31};
  private static int persianDaysInMonth[] = {31, 31, 31, 31, 31, 31, 30, 30,
    30, 30, 30, 29};

  private static YearMonthDay gregorianToJalali(YearMonthDay gregorian) {

    if (gregorian.getMonth() > 11 || gregorian.getMonth() < -11) {
      throw new IllegalArgumentException();
    }
    int persianYear;
    int persianMonth;
    int persianDay;

    int gregorianDayNo, persianDayNo;
    int persianNP;
    int i;

    gregorian.setYear(gregorian.getYear() - 1600);
    gregorian.setDay(gregorian.getDay() - 1);

    gregorianDayNo = 365 * gregorian.getYear() + (int) Math.floor((gregorian.getYear() + 3) / 4)
      - (int) Math.floor((gregorian.getYear() + 99) / 100)
      + (int) Math.floor((gregorian.getYear() + 399) / 400);
    for (i = 0; i < gregorian.getMonth(); ++i) {
      gregorianDayNo += gregorianDaysInMonth[i];
    }

    if (gregorian.getMonth() > 1 && ((gregorian.getYear() % 4 == 0 && gregorian.getYear() % 100 != 0)
      || (gregorian.getYear() % 400 == 0))) {
      ++gregorianDayNo;
    }

    gregorianDayNo += gregorian.getDay();

    persianDayNo = gregorianDayNo - 79;

    persianNP = (int) Math.floor(persianDayNo / 12053);
    persianDayNo = persianDayNo % 12053;

    persianYear = 979 + 33 * persianNP + 4 * (int) (persianDayNo / 1461);
    persianDayNo = persianDayNo % 1461;

    if (persianDayNo >= 366) {
      persianYear += (int) Math.floor((persianDayNo - 1) / 365);
      persianDayNo = (persianDayNo - 1) % 365;
    }

    for (i = 0; i < 11 && persianDayNo >= persianDaysInMonth[i]; ++i) {
      persianDayNo -= persianDaysInMonth[i];
    }
    persianMonth = i;
    persianDay = persianDayNo + 1;

    return new YearMonthDay(persianYear, persianMonth, persianDay);
  }


  private static YearMonthDay persianToGregorian(YearMonthDay persian) {
    if (persian.getMonth() > 11 || persian.getMonth() < -11) {
      throw new IllegalArgumentException();
    }

    int gregorianYear;
    int gregorianMonth;
    int gregorianDay;

    int gregorianDayNo, persianDayNo;
    int leap;

    int i;
    persian.setYear(persian.getYear() - 979);
    persian.setDay(persian.getDay() - 1);

    persianDayNo = 365 * persian.getYear() + (int) (persian.getYear() / 33) * 8
      + (int) Math.floor(((persian.getYear() % 33) + 3) / 4);
    for (i = 0; i < persian.getMonth(); ++i) {
      persianDayNo += persianDaysInMonth[i];
    }

    persianDayNo += persian.getDay();

    gregorianDayNo = persianDayNo + 79;

    gregorianYear = 1600 + 400 * (int) Math.floor(gregorianDayNo / 146097); /* 146097 = 365*400 + 400/4 - 400/100 + 400/400 */
    gregorianDayNo = gregorianDayNo % 146097;

    leap = 1;
    if (gregorianDayNo >= 36525) /* 36525 = 365*100 + 100/4 */ {
      gregorianDayNo--;
      gregorianYear += 100 * (int) Math.floor(gregorianDayNo / 36524); /* 36524 = 365*100 + 100/4 - 100/100 */
      gregorianDayNo = gregorianDayNo % 36524;

      if (gregorianDayNo >= 365) {
        gregorianDayNo++;
      } else {
        leap = 0;
      }
    }

    gregorianYear += 4 * (int) Math.floor(gregorianDayNo / 1461); /* 1461 = 365*4 + 4/4 */
    gregorianDayNo = gregorianDayNo % 1461;

    if (gregorianDayNo >= 366) {
      leap = 0;

      gregorianDayNo--;
      gregorianYear += (int) Math.floor(gregorianDayNo / 365);
      gregorianDayNo = gregorianDayNo % 365;
    }

    for (i = 0; gregorianDayNo >= gregorianDaysInMonth[i] + ((i == 1 && leap == 1) ? i : 0); i++) {
      gregorianDayNo -= gregorianDaysInMonth[i] + ((i == 1 && leap == 1) ? i : 0);
    }
    gregorianMonth = i;
    gregorianDay = gregorianDayNo + 1;

    return new YearMonthDay(gregorianYear, gregorianMonth, gregorianDay);

  }

  static class YearMonthDay {

    YearMonthDay(int year, int month, int day) {
      this.year = year;
      this.month = month;
      this.day = day;
    }

    private int year;
    private int month;
    private int day;

    public int getYear() {
      return year;
    }

    public void setYear(int year) {
      this.year = year;
    }

    public int getMonth() {
      return month;
    }

    public void setMonth(int month) {
      this.month = month;
    }

    public int getDay() {
      return day;
    }

    public void setDay(int date) {
      this.day = date;
    }

    public String toString() {
      return getYear() + "/" + getMonth() + "/" + getDay();
    }
  }
}