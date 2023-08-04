
public class Element {
	public int index;
	public int tid;
	public long timeStamp;
	public int IUtil;
	public int acumIUtil;
	public double URatio;
	public int RUtil;
	public int acumRUtil;

	public Element(int tid, long timeStamp, int IUtil, int RUtil) {
		this.tid = tid;
		this.timeStamp = timeStamp;
		this.IUtil = IUtil;
		this.RUtil = RUtil;
	}

	public int getAcumRUtil() {
		return acumRUtil;
	}

	public void setAcumRUtil(int acumRUtil) {
		this.acumRUtil = acumRUtil;
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

	public int getAcumIUtil() {
		return acumIUtil;
	}

	public void setAcumIUtil(int acumIUtil) {
		this.acumIUtil = acumIUtil;
	}

	public double getURatio() {
		return URatio;
	}

	public void setURatio(double URatio) {
		this.URatio = URatio;
	}

	public int getRUtil() {
		return RUtil;
	}

	public void setRUtil(int RUtil) {
		this.RUtil = RUtil;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
