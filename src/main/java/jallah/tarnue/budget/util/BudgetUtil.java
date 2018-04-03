package jallah.tarnue.budget.util;

import java.text.NumberFormat;

public final class BudgetUtil {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getCurrencyInstance();

    public static final String UPDATE_TEXT = "Update";

    private BudgetUtil() {
    }

    public static String formatToUSCurrency(double amount) {
        return NUMBER_FORMAT.format(amount);
    }
}
