package app.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShippingCalculatorTest {

    // Ækvivalensklassepartionering (black-box teknik)
    @Test
    public void testDertemineShippingRate() {
        assertEquals(0, ShippingCalculator.determineShippingRate(0));
        assertEquals(0, ShippingCalculator.determineShippingRate(4719));
        assertEquals(199, ShippingCalculator.determineShippingRate(5000));
        assertEquals(199, ShippingCalculator.determineShippingRate(5999));
        assertEquals(299, ShippingCalculator.determineShippingRate(6000));
        assertEquals(299, ShippingCalculator.determineShippingRate(9999));
    }

    // Grænseværdianalyse (black-box teknik)
    @Test
    public void testDetermineShippingRateBoundaryValues() {
        assertEquals(0, ShippingCalculator.determineShippingRate(0));
        assertEquals(0, ShippingCalculator.determineShippingRate(1));
        assertEquals(0, ShippingCalculator.determineShippingRate(4719));
        assertEquals(199, ShippingCalculator.determineShippingRate(4720));
        assertEquals(199, ShippingCalculator.determineShippingRate(4999));
        assertEquals(199, ShippingCalculator.determineShippingRate(5000));
        assertEquals(199, ShippingCalculator.determineShippingRate(5999));
        assertEquals(299, ShippingCalculator.determineShippingRate(6000));
        assertEquals(299, ShippingCalculator.determineShippingRate(9999));
    }



}