import java.util.ArrayList;
import java.util.List;

public class AccUList {
	 int count=0;
	 List<Integer> itemSet=new ArrayList<>();
	 int sumIutil = 0;
	 int sumRutil = 0;
	 List<Element> elements = new ArrayList<>();
	 int ts = 1;//最可能最早时间戳的index
	 int ts_extension=0;//扩展最可能最早时间戳在本list的index
	 int lastDeltaIndex=0;//deltaTimestamp所在的element的index
	 static long deltaTimestamp;
	 static double minratio;
	 static int minutil;

	public void addElement(Element element){
		if (elements.isEmpty()){
			element.setIndex(1);
			element.setAcumIUtil(element.IUtil);
			element.setURatio(1.0);
			element.setAcumRUtil(element.RUtil);
			elements.add(element);
			sumIutil += element.IUtil;
			sumRutil += element.RUtil;
			if(element.timeStamp<= AccUList.deltaTimestamp){
				lastDeltaIndex=element.index;
			}
		}else {
			Element lastElement=elements.get(elements.size()-1);
			element.setIndex(lastElement.getIndex()+1);

//			if (lastElement.timeStamp<= AccUList.deltaTimestamp&&element.timeStamp> AccUList.deltaTimestamp){
//				lastDeltaIndex=lastElement.index;
//			}else
			if(element.timeStamp<= AccUList.deltaTimestamp){
				lastDeltaIndex=element.index;
			}
			int accIU=lastElement.getAcumIUtil()+element.getIUtil();
			element.setAcumIUtil(accIU);
			double uRatio=(double) lastElement.getAcumIUtil()/accIU;
			element.setURatio(uRatio);
			int accRU=lastElement.getAcumRUtil()+element.getRUtil();
			element.setAcumRUtil(accRU);
			elements.add(element);
			//time prune to find the most likely early element show ssp in current uilitylist
			//if(elements.get(ts-1).timeStamp<=deltaTimestamp){//????????
				tPrune();
			//}
			sumIutil += element.IUtil;
			sumRutil += element.RUtil;
		}
		//time prune to find the most likely early element show ssp for exensions uilitylist
		if (Double.compare(element.acumIUtil+element.acumRUtil, AccUList.minratio* AccUList.minutil)<0){
			ts_extension=Math.max(ts_extension,element.index+1);//注意ts_extension大于element.size()的情况???
		}

	}

	private void tPrune() {
		Element curElement=elements.get(elements.size()-1);
		double ur=curElement.getURatio();
		//ur<minratio
		if (Double.compare(ur, AccUList.minratio)<0){
			ts=Math.max(ts,curElement.index);
			count++;
		}else{
//			if (Double.compare((double) elements.get(ts-1).acumIUtil/curElement.acumIUtil,UtilityList.minratio)<0){
//				ts=ts+1;
				while (Double.compare((double) elements.get(ts-1).acumIUtil/curElement.acumIUtil, AccUList.minratio)<0){
					ts=ts+1;
					count++;
				}
		//	}
		}

	}

	public List<Integer> getItemSet() {
		return itemSet;
	}

	public void setItemSet(List<Integer> itemSet) {
		this.itemSet = itemSet;
	}

	public int getTs() {
		return ts;
	}

	public void setTs(int ts) {
		this.ts = ts;
	}
}
