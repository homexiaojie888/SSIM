
public class Elem {
	public int index;
	public int tid;
	public long timeStamp;
	public int IUtil;
	public int RUtil;

	public Elem(int tid, long timeStamp, int IUtil, int RUtil) {
		this.tid = tid;
		this.timeStamp = timeStamp;
		this.IUtil = IUtil;
		this.RUtil = RUtil;
	}


	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getIUtil() {
		return IUtil;
	}

	public void setIUtil(int IUtil) {
		this.IUtil = IUtil;
	}


	public int getRUtil() {
		return RUtil;
	}

	public void setRUtil(int RUtil) {
		this.RUtil = RUtil;
	}


	public void setIndex(int index) {
		this.index=index;
	}

	public int getIndex() {
		return index;
	}
}
