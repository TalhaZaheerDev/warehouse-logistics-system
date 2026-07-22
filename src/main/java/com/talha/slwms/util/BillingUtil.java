package com.talha.slwms.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BillingUtil {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.05");

    private BillingUtil() {}

    public static BigDecimal calculateTotal(double baseCharge){
        BigDecimal base = BigDecimal.valueOf(baseCharge);
        BigDecimal tax = base.multiply(TAX_RATE);
        return base.add(tax).setScale(2, RoundingMode.HALF_UP);
    }
}
