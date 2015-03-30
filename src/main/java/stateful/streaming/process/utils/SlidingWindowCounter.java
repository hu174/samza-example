/**
 * 
 */
package stateful.streaming.process.utils;

import java.io.Serializable;
import java.util.Map;

/**
 * @author yingkaihu
 *
 */
public class SlidingWindowCounter<T> implements Serializable {

	private static final long serialVersionUID = -5182218306139936565L;
	private BaseCounter<T> counters;
	private int current;
	private int next;
	private int windowLength;

	public SlidingWindowCounter(int _windowLength) {
		if (_windowLength < 2)
			throw new IllegalArgumentException(
					"Window length can not be smaller than 2");
		windowLength = _windowLength;
		counters = new BaseCounter<T>(windowLength);
		current = 0;
		next = (current + 1) % windowLength;
	}

	public void increment(T key) {
		counters.increment(key, current);
	}

	public Map<T, Integer> getCountsAdvanceWindow() {
		Map<T, Integer> res = counters.getCounts();
		advanceHead();
		counters.reset(current);
		return res;
	}
	
	private void advanceHead(){
		current = next;
		next = (current + 1) % windowLength;
	}

}
