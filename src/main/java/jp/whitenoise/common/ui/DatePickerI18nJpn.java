package jp.whitenoise.common.ui;

import java.util.List;
import java.util.stream.IntStream;

import com.vaadin.flow.component.datepicker.DatePicker;

/**
 * 日付ピッカー(日本語ローカライズ).
 */
public class DatePickerI18nJpn extends DatePicker.DatePickerI18n {

	private static List<String> MONTH_NAMES = IntStream.range(1, 13).mapToObj(v -> v + "月").toList();
	private static List<String> WEEKDAYS = List.of("日曜日", "月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日");
	private static List<String> WEEKDAYS_SHORT = List.of("日", "月", "火", "水", "木", "金", "土");

	public DatePickerI18nJpn() {
		setMonthNames(MONTH_NAMES);
		setWeekdays(WEEKDAYS);
		setWeekdaysShort(WEEKDAYS_SHORT);
		setFirstDayOfWeek(0);
		setToday("今日");
		setCancel("キャンセル");
		setDateFormat("yyyy/MM/dd");
		setBadInputErrorMessage("入力日付が不正です。");
		setMaxErrorMessage("範囲外の日付が入力されています。");
		setMinErrorMessage("範囲外の日付が入力されています");
	}
}
