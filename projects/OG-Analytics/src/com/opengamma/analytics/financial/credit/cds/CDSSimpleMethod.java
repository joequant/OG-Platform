/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.credit.cds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.YieldCurveBundle;
import com.opengamma.analytics.financial.interestrate.method.PricingMethod;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponFixed;
import com.opengamma.analytics.financial.interestrate.payments.derivative.PaymentFixed;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldAndDiscountAddZeroSpreadCurve;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.util.money.CurrencyAmount;

/**
 * Do the actual CDS calculations
 * 
 * @author Niels Stchedroff
 */
public class CDSSimpleMethod implements PricingMethod {

  private static final Logger s_logger = LoggerFactory.getLogger(CDSSimpleMethod.class);
  
  @Override
  public CurrencyAmount presentValue(InstrumentDerivative instrument, YieldCurveBundle curves) {
    
    CDSDerivative cds = (CDSDerivative) instrument;
    YieldAndDiscountCurve cdsCcyCurve = curves.getCurve(cds.getCdsCcyCurveName());
    YieldAndDiscountCurve bondCcyCurve = curves.getCurve(cds.getBondCcyCurveName());
    YieldAndDiscountCurve spreadCurve = curves.getCurve(cds.getSpreadCurveName());
    
    return CurrencyAmount.of(cds.getPremium().getCurrency(), calculate(cds, cdsCcyCurve, bondCcyCurve, spreadCurve));
  }
  
  /**
   * Build the credit curve from the bond curve and the spread curve
   * 
   * @param bondCcyCurve
   * @param spreadCurve
   * @returnThe combined curve
   */
  private static YieldAndDiscountAddZeroSpreadCurve buildCreditCurve(YieldAndDiscountCurve bondCcyCurve, YieldAndDiscountCurve spreadCurve) {
    return new YieldAndDiscountAddZeroSpreadCurve("CREDIT CURVE", false, bondCcyCurve, spreadCurve);
  }
  
  public static double calculate(CDSDerivative cds, YieldAndDiscountCurve cdsCcyCurve, YieldAndDiscountCurve bondCcyCurve, YieldAndDiscountCurve spreadCurve) {
    YieldAndDiscountCurve creditCurve = buildCreditCurve(bondCcyCurve, spreadCurve);
    return calculatePremiumLeg(cds, cdsCcyCurve, bondCcyCurve, creditCurve) - calculateDefaultLeg(cds, cdsCcyCurve, bondCcyCurve, creditCurve);
  }
  
  public static double calculatePremiumLeg(CDSDerivative cds, YieldAndDiscountCurve cdsCcyCurve, YieldAndDiscountCurve bondCcyCurve, YieldAndDiscountCurve creditCurve) {
    
    final CouponFixed[] premiumPayments = cds.getPremium().getPayments();
    final double oneMinusRecoveryRate = 1.0 - cds.getRecoveryRate();
    double total = 0.0;
    
    for (int i = 0; i < premiumPayments.length; ++i) {
      
      final double t = premiumPayments[i].getPaymentTime();
      final double probabilityOfDefault = (1.0 - (creditCurve.getDiscountFactor(t) / bondCcyCurve.getDiscountFactor(t))) / oneMinusRecoveryRate;
      final double discountedExpectedCashflow = premiumPayments[i].getAmount() * (1.0 - probabilityOfDefault) * cdsCcyCurve.getDiscountFactor(t);
      total += discountedExpectedCashflow;
      if (s_logger.isDebugEnabled()) {
        s_logger.debug("Period = " + i + ", t = " + t + ", probabilityOfDefault = " + probabilityOfDefault + ", discountedExpectedCashflow = " + discountedExpectedCashflow);
      }
    }
    if (s_logger.isDebugEnabled()) {
      s_logger.debug("total = " + total);
    }
    return total;
  }
  
  public static double calculateDefaultLeg(CDSDerivative cds, YieldAndDiscountCurve cdsCcyCurve, YieldAndDiscountCurve bondCcyCurve, YieldAndDiscountCurve creditCurve) {
    
    final PaymentFixed[] possibleDefaultPayments = cds.getPayout().getPayments();
    final double oneMinusRecoveryRate = 1.0 - cds.getRecoveryRate();
    double probabilityOfPriorDefault = 0.0;
    double total = 0.0;
    
    for (int i = 0; i < possibleDefaultPayments.length; ++i) {
      
      final double t = possibleDefaultPayments[i].getPaymentTime();   
      final double probabilityOfDefault = (1.0 - (creditCurve.getDiscountFactor(t) / bondCcyCurve.getDiscountFactor(t))) / oneMinusRecoveryRate;
      final double discountedExpectedCashflow = possibleDefaultPayments[i].getAmount() * (probabilityOfDefault - probabilityOfPriorDefault) * cdsCcyCurve.getDiscountFactor(t);
      total += discountedExpectedCashflow;
      if (s_logger.isDebugEnabled()) {
        s_logger.debug("Period = " + i + ", t = " + t + ", probabilityOfDefault = " + probabilityOfDefault + ", discountedExpectedCashflow = " + discountedExpectedCashflow);
      }
      probabilityOfPriorDefault = probabilityOfDefault;
    }
    if (s_logger.isDebugEnabled()) {
      s_logger.debug("total = " + total);
    }
    return total;
  }
  
  
  //-- Old implementation
//
//  public static double calculate(CDSSecurity cds, BondSecurity bond, YieldAndDiscountCurve bondCcyCurve, YieldAndDiscountCurve cdsCcyCurve, YieldAndDiscountCurve riskyCurve, ZonedDateTime pricingDate)
//    throws OpenGammaRuntimeException {
//
//    BusinessDayConvention convention = new FollowingBusinessDayConvention();
//    Calendar calendar = new MondayToFridayCalendar("A");
//
//    return calculatePremiumLeg(cds, bond, bondCcyCurve, cdsCcyCurve, riskyCurve, pricingDate, calendar, convention)
//        - calculateDefaultLeg(cds, bond, bondCcyCurve, cdsCcyCurve, riskyCurve, pricingDate, calendar, convention);
//  }
//
//  private static double calculatePremiumLeg(CDSSecurity cds, BondSecurity bond, YieldAndDiscountCurve bondCcyCurve, YieldAndDiscountCurve cdsCcyCurve, YieldAndDiscountCurve riskyCurve,
//    ZonedDateTime pricingDate, Calendar calendar, BusinessDayConvention convention)
//    throws OpenGammaRuntimeException {
//
//    if (s_logger.isDebugEnabled()) {
//      s_logger.debug("Premium leg");
//      s_logger.debug("cdsPremiumDate, timeToCdsPremium, bondCcyDiscountFactor, cdsCcyDiscountFactor, riskyDiscountFactor, riskFreeDefaultProbability, expectedCashflow, discountedExpCashflow");
//    }
//
//    // TODO: Move to TenorUtils class and use Tenor object in CDSSecurity
//    final ZonedDateTime cdsMaturity = cds.getMaturity();
//    final Period cdsTenor = getTenor(cds.getPremiumFrequency());
//    final List<ZonedDateTime> premiumDates = scheduleDatesInRange(cdsMaturity, cdsTenor, pricingDate, cdsMaturity, calendar, convention);
//
//    final double interestCashflow = cds.getNotional() * cds.getPremiumRate() * (cdsTenor.totalMonths() / 12.0 + cdsTenor.totalSecondsWith24HourDays() / DateUtils.SECONDS_PER_YEAR);
//
//    double total = 0.0;
//
//    for (ZonedDateTime premiumDate : premiumDates) {
//
//      //final double timeToCdsPremium = DateUtils.getDifferenceInYears(pricingDate, premiumDate);
//      final double timeToCdsPremium = TimeCalculator.getTimeBetween(pricingDate, premiumDate);
//
//      final double bondCcyDiscountFactor = bondCcyCurve.getDiscountFactor(timeToCdsPremium);
//      final double cdsCcyDiscountFactor = cdsCcyCurve.getDiscountFactor(timeToCdsPremium);
//      final double riskyDiscountFactor = riskyCurve.getDiscountFactor(timeToCdsPremium);
//
//      final double riskFreeDefaultProbability = (1.0 - (riskyDiscountFactor / bondCcyDiscountFactor)) / (1.0 - cds.getRecoveryRate());
//
//      final double expectedCashflow = interestCashflow * (1.0 - riskFreeDefaultProbability);
//      final double discountedExpCashflow = expectedCashflow * cdsCcyDiscountFactor;
//      total += discountedExpCashflow;
//
//      if (s_logger.isDebugEnabled()) {
//        s_logger.debug(premiumDate.toString() + "," + timeToCdsPremium + "," + bondCcyDiscountFactor + "," + cdsCcyDiscountFactor + "rate * rp," + riskyDiscountFactor + ","
//          + riskFreeDefaultProbability + "," + expectedCashflow + "," + discountedExpCashflow);
//      }
//    }
//
//    return total;
//  }
//
//  private static double calculateDefaultLeg(CDSSecurity cds, BondSecurity bond, YieldAndDiscountCurve bondCcyCurve, YieldAndDiscountCurve cdsCcyCurve, YieldAndDiscountCurve riskyCurve,
//    ZonedDateTime pricingDate, Calendar calendar, BusinessDayConvention convention) 
//    throws OpenGammaRuntimeException {
//
//    if (s_logger.isDebugEnabled()) {
//      s_logger.debug("Default leg");
//      s_logger.debug("bondPaymentDate, timeToBondPayment, bondCcyDiscountFactor, cdsCcyDiscountFactor, riskyDiscountFactor, riskFreeDefaultProbability, expectedCashflow, discountedExpCashflow");
//    }
//
//    final ZonedDateTime cdsMaturity = cds.getMaturity();
//    final ZonedDateTime bondMaturity = bond.getLastTradeDate().getExpiry();
//    final ZonedDateTime lastPaymentDate = cdsMaturity.isBefore(bondMaturity) ? cdsMaturity : bondMaturity;
//    final Period bondTenor = getTenor(bond.getCouponFrequency());
//
//    final List<ZonedDateTime> bondPaymentDates = scheduleDatesInRange(bondMaturity, bondTenor, pricingDate, lastPaymentDate, calendar, convention);
//
//    if (cdsMaturity.isAfter(bondMaturity)) {
//      bondPaymentDates.add(cdsMaturity);
//    }
//
//    final double defaultPayoutCashflow = cds.getNotional() * (1.0 - cds.getRecoveryRate());
//
//    double total = 0.0;
//    double previousRiskFreeDefaultProbability = 0.0;
//
//    for (ZonedDateTime bondPaymentDate : bondPaymentDates) {
//
//      //final double timeToBondPayment = DateUtils.getDifferenceInYears(pricingDate, bondPaymentDate);
//      final double timeToBondPayment = TimeCalculator.getTimeBetween(pricingDate, bondPaymentDate);
//
//      final double bondCcyDiscountFactor = bondCcyCurve.getDiscountFactor(timeToBondPayment);
//      final double cdsCcyDiscountFactor = cdsCcyCurve.getDiscountFactor(timeToBondPayment);
//      final double riskyDiscountFactor = riskyCurve.getDiscountFactor(timeToBondPayment);
//
//      final double riskFreeDefaultProbability = (1.0 - (riskyDiscountFactor / bondCcyDiscountFactor)) / (1.0 - cds.getRecoveryRate());
//
//      final double expectedDefaultPayout = defaultPayoutCashflow * (riskFreeDefaultProbability - previousRiskFreeDefaultProbability);
//      final double discountedExpDefaultPayout = expectedDefaultPayout * cdsCcyDiscountFactor;
//      total += discountedExpDefaultPayout;
//
//      previousRiskFreeDefaultProbability = riskFreeDefaultProbability;
//
//      if (s_logger.isDebugEnabled()) {
//        s_logger.debug(bondPaymentDate.toString() + ", " + timeToBondPayment + ", " + bondCcyDiscountFactor + ", " + cdsCcyDiscountFactor + ", " + riskyDiscountFactor + ", "
//            + riskFreeDefaultProbability + ", " + expectedDefaultPayout + ", " + discountedExpDefaultPayout);
//      }
//    }
//
//    return total;
//  }
//
//  // TODO: Should this method be available somewhere central?
//  // TODO: Why do other security types have conversion visitors to translate to security definition classes?
//  private static Period getTenor(final Frequency freq) {
//    if (freq instanceof PeriodFrequency) {
//      return ((PeriodFrequency) freq).getPeriod();
//    } else if (freq instanceof SimpleFrequency) {
//      return ((SimpleFrequency) freq).toPeriodFrequency().getPeriod();
//    }
//    throw new OpenGammaRuntimeException("Can only PeriodFrequency or SimpleFrequency; have " + freq.getClass());
//  }
//
//  public static List<ZonedDateTime> scheduleDatesInRange(ZonedDateTime maturity, Period term, ZonedDateTime earliest, ZonedDateTime latest,
//      Calendar calendar, BusinessDayConvention convention) {
//    
//    List<ZonedDateTime> datesInRange = new ArrayList<ZonedDateTime>();
//    ZonedDateTime scheduleDate = maturity;
//    int periods = 0;
//    
//    while (scheduleDate.isAfter(latest)) {
//      scheduleDate = convention.adjustDate(calendar, maturity.minus(term.multipliedBy(++periods)));
//    }
//    
//    while (!scheduleDate.isBefore(earliest)) {
//      datesInRange.add(scheduleDate);
//      scheduleDate = convention.adjustDate(calendar, maturity.minus(term.multipliedBy(++periods)));
//    }
//    
//    Collections.reverse(datesInRange);
//    return datesInRange;
//  }
}
