package com.about80minutes.helpers.na;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.hadoop.thirdparty.guava.common.collect.Lists;
import org.junit.BeforeClass;
import org.junit.Test;

import com.about80minutes.util.NumericFunctions;

/**
 * Test class for the numeric analysis functions
 */
public class NumericAnalysisUtilsTest {
	
	private static List<Float> VALUE_LIST = null;

	/**
	 * Initialisation method, sets up the test data set
	 */
	@BeforeClass
	public static void setupList() {
		VALUE_LIST = Lists.newArrayList();
		VALUE_LIST.add(1.05f);
		VALUE_LIST.add(2.78f);
		VALUE_LIST.add(3.00f);
		VALUE_LIST.add(4.00f);
		VALUE_LIST.add(1.05f);
		VALUE_LIST.add(5.5f);
		VALUE_LIST.add(3f);
		VALUE_LIST.add(3.0f);
		VALUE_LIST.add(8.4f);
		VALUE_LIST.add(-4f);
	}
	
	/**
	 * Tests the formatNumber(Float) method of {@link com.about80minutes.util.NumericFunctions}
	 */
	@Test
	public void formatNumber_Float() {
		String expected = "123,456.78";
		assertEquals(expected, NumericFunctions.formatNumber(123456.78f));
	}
	
	/**
	 * Tests the formatNumber(Integer) method of {@link com.about80minutes.util.NumericFunctions}
	 */
	@Test
	public void formatNumber_Integer() {
		String expected = "123,456";
		assertEquals(expected, NumericFunctions.formatNumber(123456));
	}
	
	/**
	 * Tests the formatNumberList method of {@link com.about80minutes.util.NumericFunctions}
	 */
	@Test
	public void formatNumberList() {
		String expected = "[1.05, 2.78, 3.00, 4.00, 1.05, 5.50, 3.00, 3.00, 8.40, -4.00]";
		assertEquals(expected, NumericFunctions.formatNumberList(VALUE_LIST));
	}
	
	/**
	 * Tests the getSum method of {@link com.about80minutes.util.NumericFunctions}
	 */
	@Test
	public void getSum() {
		Float expected = Float.valueOf(27.78f);
		assertEquals(expected, NumericFunctions.getSum(VALUE_LIST));
	}
	
	/**
	 * Tests the getMean method of {@link com.about80minutes.util.NumericFunctions}
	 */
	@Test
	public void getMean() {
		Float expected = Float.valueOf(2.778f);
		assertEquals(expected, NumericFunctions.getMean(VALUE_LIST));
	}
	
	/**
	 * Tests the getMedian method of {@link com.about80minutes.util.NumericFunctions}
	 */
	@Test
	public void getMedian() {
		Float expected = Float.valueOf(3.00f);
		assertEquals(expected, NumericFunctions.getMedian(VALUE_LIST));
	}
	
	/**
	 * Tests the getStdDeviation method of {@link com.about80minutes.util.NumericFunctions}
	 */
	@Test
	public void getStdDeviation() {
		Float expected = Float.valueOf(3.042541f);
		assertEquals(expected, NumericFunctions.getStdDeviation(VALUE_LIST));
	}
	
	/**
	 * Tests the getModes method of {@link com.about80minutes.util.NumericFunctions}
	 */
	@Test
	public void getModes() {
		List<Float> expected = null;
		expected = Lists.newArrayList();
		expected.add(3f);
		//TODO finish this to compare lists
	}
	
	/**
	 * Tests the getRange method of {@link com.about80minutes.util.NumericFunctions}
	 */
	@Test
	public void getRange() {
		Float expected = Float.valueOf(12.4f);
		assertEquals(expected, NumericFunctions.getRange(VALUE_LIST));
	}
}
