/**
 * 
 */
package stateful.streaming.process.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yingkaihu
 *
 */
public class BaseCounter<T> implements Serializable {

	private static final long serialVersionUID = -4373628154167935914L;
	private int numSlots;
	private Map<T, List<Integer>> counterMap = new HashMap<T, List<Integer>>();

	public BaseCounter(int numSlots) {
		super();
		if (numSlots < 1)
			throw new IllegalArgumentException(
					"Slot number can not be smaller than 1");
		this.numSlots = numSlots;
	}

	public void increment(T key, int slot) {
		List<Integer> counters = counterMap.get(key);
		if (counters != null) {
			counters.set(slot, counters.get(slot) + 1);
		} else {
			counters = new ArrayList<Integer>(this.numSlots);
			counters.set(slot, 1);
		}
	}

	public int getCount(T key) {
		List<Integer> counters = counterMap.get(key);
		int res = 0;
		if (counters != null) {
			for (int counter : counters) {
				res += counter;
			}
		}
		return res;
	}

	public Map<T, Integer> getCounts() {
		Map<T, Integer> res = new HashMap<T, Integer>();
		for (T key : counterMap.keySet()) {
			List<Integer> values = counterMap.get(key);
			int count = 0;
			for (int value : values) {
				count += value;
			}
			res.put(key, count);
		}
		return res;
	}

	public void reset(int slot) {
		for (T key : counterMap.keySet()) {
			List<Integer> values = counterMap.get(key);
			values.set(slot, 0);
			int sum = 0;
			for (int value : values) {
				sum += value;
			}
			if (sum == 0)
				cleanup(key);
		}
	}

	public void cleanup(T key) {
		counterMap.remove(key);
	}

}
