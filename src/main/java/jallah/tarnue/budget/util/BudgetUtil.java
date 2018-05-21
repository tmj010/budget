package jallah.tarnue.budget.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public final class BudgetUtil {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getCurrencyInstance();

    public static final String UPDATE_TEXT = "Update";

    private BudgetUtil() {
    }

    public static String formatToUSCurrency(double amount) {
        return NUMBER_FORMAT.format(amount);
    }

    public static double getDoubleValue(String strAmt) {
        return  (StringUtils.isNotBlank(strAmt)
                && NumberUtils.isDigits(strAmt)) ? Double.parseDouble(strAmt) : 0;
    }

    public static Date localDateToDate(LocalDate localDate) {
        if (null != localDate) {
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            return date;
        }
        return null;
    }
}
