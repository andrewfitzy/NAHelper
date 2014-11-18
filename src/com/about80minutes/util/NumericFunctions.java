package com.about80minutes.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.thirdparty.guava.common.collect.Lists;
import org.apache.hadoop.thirdparty.guava.common.collect.Maps;

/**
 * Utility class, this contains a number of static methods for computing
 * statistics over a set of data.
 */
public class NumericFunctions {
	
	/**
	 * Method for formatting a number so that it is comma separated and includes
	 * trailing decimal values e.g. 123,456.78
	 * 
	 * @param value a {@link java.lang.Float} containing the value to format
	 * 
	 * @return the formatted {@link java.lang.String}
	 */
	public static String formatNumber(Float value) {
		DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(Locale.UK);
		nf.setDecimalSeparatorAlwaysShown(true);
		nf.setGroupingSize(3);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		nf.setMinimumIntegerDigits(1);
		
		return nf.format(value);
	}
	
	/**
	 * Method for formatting a number so that it is comma separated
	 * e.g. 123,456
	 * 
	 * @param value a {@link java.lang.Integer} containing the value to format
	 * 
	 * @return the formatted {@link java.lang.String}
	 */
	public static String formatNumber(Integer value) {
		NumberFormat nf = NumberFormat.getInstance(Locale.UK);
		return nf.format(value);
	}
	
	/**
	 * Format a list of numbers so that the following format is produced
	 * 
	 * [1.00, 2.00, 3.00, ..., n]
	 * 
	 * @param values a {@link java.util.Collection} of {@link java.lang.Float}
	 * to be formatted
	 * 
	 * @return the formatted {@link java.lang.String}
	 */
	public static String formatNumberList(Collection<Float> values) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for(Float tmpFloat : values) {
			builder.append(NumericFunctions.formatNumber(tmpFloat));
			builder.append(", ");
		}
		if(builder.length() >= 2) {
			builder.delete(builder.length() - 2, builder.length());
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Calculate the Sum of a collection of Floats
	 *
	 * @param vals a {@link java.util.Collection} of {@link java.lang.Float} to
	 * compute stats against
	 * 
	 * @return a {@link java.lang.Float} containing the sum value
	 */
	public static Float getSum(Collection<Float> vals) {
		Float sum = Float.valueOf(0f);
		for (Float val : vals) {
			sum += val;
		}
		return sum;
	}

	/**
	 * Calculate the mean (average) of a collection of Floats
	 *
	 * @param vals a {@link java.util.Collection} of {@link java.lang.Float} to
	 * compute stats against
	 * 
	 * @return a {@link java.lang.Float} containing the mean value
	 */
	public static Float getMean(Collection<Float> vals) {
		Float total = Float.valueOf(0f);
		Integer count = vals.size();
		for (Float f : vals) {
			total += f;
		}
		return total/count;
	}

	/**
	 * Calculate the median of a collection of Floats
	 *
	 * @param vals a {@link java.util.Collection} of {@link java.lang.Float} to
	 * compute stats against
	 * 
	 * @return a {@link java.lang.Float} containing the median value
	 */
	public static Float getMedian(Collection<Float> vals){
		List<Float> valsList = new ArrayList<Float>();
		valsList.addAll(vals);
		Collections.sort(valsList);
		int medianPos = valsList.size() / 2;
		return valsList.get(medianPos);
	}

	/**
	 * Calculate the Standard Deviation of a collection of Floats
	 *
	 * @param vals a {@link java.util.Collection} of {@link java.lang.Float} to
	 * compute stats against
	 * 
	 * @return a {@link java.lang.Float} containing the standard deviation value
	 */
	public static Float getStdDeviation(Collection<Float> vals) {
		Float stdDev = Float.valueOf(0f);
		Float avg = getMean(vals);
		int count = vals.size();
		
		if (count > 0) {
			double varianceSum = 0;
			double variance = 0;
			for(Float f : vals) {
				variance = f - avg;
				varianceSum += variance * variance;
			}
			double tmpStdDev = Math.sqrt(varianceSum/count);
			stdDev = Double.valueOf(tmpStdDev).floatValue();
		}
		return stdDev;
	}
	
	/**
	 * Calculate the mode for a collection of Floats
	 *
	 * @param vals a {@link java.util.Collection} of {@link java.lang.Float} to
	 * compute stats against
	 * 
	 * @return a {@link java.util.Collection} of {@link java.lang.Float}
	 * containing the modes for the data set
	 */
	public static Collection<Float> getModes(Collection<Float> vals) {
		List<Float> mode = Lists.newArrayList();
		Map<Float, Integer> valueMap = Maps.newHashMap();
		//firstly count occurrences of values
		for(Float val : vals) {
			Integer count = valueMap.get(val);
			if(count == null) {
				count = Integer.valueOf(1);
			} else {
				count++;
			}
			valueMap.put(val, count);
		}
		
		Integer currentHighest = 0;
		//secondly work out the highest counts
		for(Entry<Float, Integer> entry : valueMap.entrySet()) {
			if(entry.getValue().compareTo(currentHighest) == 0) {
				mode.add(entry.getKey());
			} else if(entry.getValue().compareTo(currentHighest) == 1) {
				mode.clear();
				mode.add(entry.getKey());
				currentHighest = entry.getValue(); 
			} else {
				//do nothing
			}
		}
		return mode;
	}
	
	/**
	 * Calculate the range for a collection of Floats
	 *
	 * @param vals a {@link java.util.Collection} of {@link java.lang.Float} to
	 * compute stats against
	 * 
	 * @return a {@link java.lang.Float} containing the range value
	 */
	public static Float getRange(Collection<Float> vals) {
		return Collections.max(vals) - Collections.min(vals);
	}
}
