package com.opengamma.analytics.example.curveconstruction;

// @export "imports"
import com.opengamma.financial.interestrate.ParRateCalculator;
import com.opengamma.financial.interestrate.PresentValueCalculator;
import com.opengamma.financial.interestrate.YieldCurveBundle;
import com.opengamma.financial.interestrate.cash.derivative.Cash;
import com.opengamma.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.math.curve.ConstantDoublesCurve;
import com.opengamma.util.money.Currency;
import java.io.PrintStream;

public class CashExample {
    // @export cashDemo
    public static Currency ccy = Currency.EUR;
    public static double t = 1.0;
    public static double notional = 10000.0;
    public static double r = 0.03;

    public static String yieldCurveName = "Euro Yield Curve Fixed 2%";
    public static double y = 0.02;

    public static void cashDemo(PrintStream out) {
        Cash loan = new Cash(ccy, 0.0, t, notional, r, t, yieldCurveName);

        out.println(loan.getInterestAmount());
    }

    // @export "yieldCurveBundle"
    public static YieldCurveBundle getBundle() {
        YieldCurveBundle bundle = new YieldCurveBundle();
        ConstantDoublesCurve curve = new ConstantDoublesCurve(y);
        YieldCurve yieldCurve = new YieldCurve(curve);
        bundle.setCurve(yieldCurveName, yieldCurve);
        return bundle;
    }

    // @export "parRateDemo"
    public static void parRateDemo(PrintStream out) {
        Cash loan = new Cash(ccy, 0.0, t, notional, r, t, yieldCurveName);
        YieldCurveBundle bundle = getBundle();

        ParRateCalculator parRateCalculator = ParRateCalculator.getInstance();
        double parRate = parRateCalculator.visit(loan, bundle);
        out.println(parRate);
    }

    // @export "presentValueDemo"
    public static void presentValueDemo(PrintStream out) {
        Cash loan = new Cash(ccy, 0.0, t, notional, r, t, yieldCurveName);
        YieldCurveBundle bundle = getBundle();

        PresentValueCalculator presentValueCalculator = PresentValueCalculator.getInstance();
        double presentValue = presentValueCalculator.visit(loan, bundle);
        out.println(presentValue);
    }
}
