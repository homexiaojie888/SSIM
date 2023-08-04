

import java.util.ArrayList;
import java.util.List;

public class Itemset {

	public List<Integer> itemSet = new ArrayList<Integer>();
	public int utility_one;
	public int utility;
 	public int tid;
	public long timestamp;

	public Itemset(List<Integer> itemSet, int utility_one, int utility, int tid, long timestamp) {
		this.itemSet = itemSet;
		this.utility_one = utility_one;
		this.utility = utility;
		this.tid = tid;
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getUtility_one() {
		return utility_one;
	}

	public void setUtility_one(int utility_one) {
		this.utility_one = utility_one;
	}

	public List<Integer> getItemSet() {
		return itemSet;
	}

	public void setItemSet(List<Integer> itemSet) {
		this.itemSet = itemSet;
	}

	public int getUtility() {
		return utility;
	}

	public void setUtility(int utility) {
		this.utility = utility;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}
}
