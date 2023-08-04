import java.util.ArrayList;
import java.util.List;

public class TUtilityList {
	 List<Integer> itemSet=new ArrayList<>();
	 int sumIutil = 0;
	 int sumRutil = 0;

	int sumIutilLessDelta = 0;
	int sumRutilLessDelta = 0;
	 List<Elem> elements = new ArrayList<>();

	 int lastDeltaIndex=0;//deltaTimestamp所在的element的index
	 static long deltaTimestamp;
	 static double minratio;
	 static int minutil;

	public void addElement(Elem e){
		e.setIndex(elements.size()+1);
		elements.add(e);
		sumIutil += e.IUtil;
		sumRutil += e.RUtil;
		if (e.timeStamp<=TUtilityList.deltaTimestamp){
			sumIutilLessDelta+=e.IUtil;
			sumRutilLessDelta+=e.RUtil;
			lastDeltaIndex=e.getIndex();
		}

	}

	public List<Integer> getItemSet() {
		return itemSet;
	}

	public void setItemSet(List<Integer> itemSet) {
		this.itemSet = itemSet;
	}

}
