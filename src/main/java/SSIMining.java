import com.google.common.base.Joiner;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

//假设db中的项都是按照从小到大排序的
public class SSIMining {

    String dataset;
    int minUtil;
    double minUtilR;
    double minRatio;
    long deltaTimestamp;
    double delta;
    Map<Integer, Map<Integer, Integer>> mapEUCS=new HashMap<>();
    Map<Integer, Map<Integer, Integer>> mapDeltaEUCS=new HashMap<>();
    long ssiCount;
    long candidatesCount;
    long joinCount;
    String revisedDB = "D:\\code\\shortSightPattern\\shortSightItemset\\src\\main\\resources\\revisedDB.txt";
    BufferedWriter writer=null;
    public long firstScantime = 0;

    /** the time at which the algorithm ended */
    public long secondScantime = 0;
    public long miningTime = 0;
    public long totalTime=0;

    public void runAlgorithm(String input, String output, double minUtilR, double minRatio, double delta) throws IOException {

        MemoryLogger.getInstance().reset();
        this.dataset=input;

        this.minUtilR=minUtilR;
        this.minRatio=minRatio;
        this.delta=delta;

        //get the most likely timestamp:deltaTimestamp
        long firstTimestamp=getFirstTimestamp(input);
        long lastTimestamp=getLastTimestamp(input);
        this.deltaTimestamp=firstTimestamp+Math.round((lastTimestamp-firstTimestamp)*delta);

        long currentTimestamp0 = System.currentTimeMillis();
        //      * calculate TWU(<=deltaTimestamp) and TWU of each items
        //     * get revised DB (reviseDB.txt) after delete low TWU(<=deltaTimestamp) and low TWU items
        //     * get new TWU(<=deltaTimestamp) and TWU of each items in revised DB
        //     * get minutil
        PairMap pairMap = FirstScanProcessing(input,deltaTimestamp,revisedDB);

        long currentTimestamp1 = System.currentTimeMillis();
        firstScantime=currentTimestamp1-currentTimestamp0;

        TUtilityList.minutil=minUtil;
        TUtilityList.minratio=minRatio;
        TUtilityList.deltaTimestamp=deltaTimestamp;
        //     * construct utility-lists ULs for all 1-itemset which satisfy newNTWU>=minutil and newTWU(<=deltaTimestamp)>=minUtil*minRaio
        //     * build EUCS and EURCS
        List<TUtilityList> ULs=SecondScanProcessing(revisedDB,pairMap,deltaTimestamp);

        long currentTimestamp2 = System.currentTimeMillis();
        secondScantime=currentTimestamp2-currentTimestamp1;

        MemoryLogger.getInstance().checkMemory();
        writer = new BufferedWriter(new FileWriter(output));

        SSIM(null,ULs);

        long currentTimestamp3 = System.currentTimeMillis();
        miningTime=currentTimestamp3-currentTimestamp2;

        totalTime=currentTimestamp3-currentTimestamp0;

        MemoryLogger.getInstance().checkMemory();

    }

    public void SSIM(TUtilityList UL_P,List<TUtilityList> ULs) throws IOException {

        for (int i = 0; i < ULs.size(); i++) {
            candidatesCount++;
            TUtilityList UL_Px = ULs.get(i);
            if (UL_Px.sumIutil>=minUtil&&Double.compare(UL_Px.sumIutilLessDelta,UL_Px.sumIutil*minRatio)>=0){
                int tid=UL_Px.elements.get(UL_Px.lastDeltaIndex-1).tid;
                long timestamp=UL_Px.elements.get(UL_Px.lastDeltaIndex-1).timeStamp;
                Itemset SSI=new Itemset(UL_Px.getItemSet(),UL_Px.sumIutilLessDelta,UL_Px.sumIutil,tid,timestamp);
                realTimeWriteOut(SSI);
                ssiCount++;
            }

             //if(UL_Px.sumIutil+UL_Px.sumRutil>=minUtil){
           if(UL_Px.sumIutil+UL_Px.sumRutil>=minUtil&&Double.compare(UL_Px.sumIutilLessDelta+UL_Px.sumRutilLessDelta,minUtil*minRatio)>=0){

                List<TUtilityList> exULs = new ArrayList<>();

                for(int j=i+1; j < ULs.size(); j++){

                    TUtilityList UL_Py= ULs.get(j);

                    Map<Integer, Integer> mapEUCSItem = mapEUCS.get(UL_Px.itemSet.get(UL_Px.itemSet.size()-1));
                    Integer TWU = mapEUCSItem.get(UL_Py.itemSet.get(UL_Py.itemSet.size()-1));
                    Map<Integer, Integer> mapDeltaEUCSItem = mapDeltaEUCS.get(UL_Px.itemSet.get(UL_Px.itemSet.size()-1));
                    Integer deltaTWU = mapDeltaEUCSItem.get(UL_Py.itemSet.get(UL_Py.itemSet.size()-1));

                    //if (TWU!=null&&TWU>=minUtil){
                    if (TWU!=null&&TWU>=minUtil&&deltaTWU!=null&&Double.compare(deltaTWU,minUtil*minRatio)>=0){
                        TUtilityList UL_Pxy = construct(UL_P, UL_Px, UL_Py);
                        if(UL_Pxy != null && !UL_Pxy.elements.isEmpty()) {
                            exULs.add(UL_Pxy);
                            joinCount++;
                        }
                    }

                }

                    SSIM(UL_Px, exULs);

            }

        }
        MemoryLogger.getInstance().checkMemory();
    }

    public TUtilityList construct(TUtilityList UL_P, TUtilityList UL_Px, TUtilityList UL_Py) {

        TUtilityList UL_Pxy = new TUtilityList();

        List<Integer> PxyItemset=UL_Pxy.getItemSet();
        PxyItemset.addAll(UL_Px.getItemSet());
        PxyItemset.add(UL_Py.getItemSet().get(UL_Py.getItemSet().size()-1));

        List<Elem> elementListPx=UL_Px.elements;
        List<Elem> elementListPy=UL_Py.elements;
        //== new optimization - LA-prune == /
        int totalUtility = UL_Px.sumIutil + UL_Px.sumRutil;
        int totalUtilityLessDelta = UL_Px.sumIutilLessDelta+UL_Px.sumRutilLessDelta;
        //== new optimization - LA-prune == /

        //根据父节点的ts_extension

        for (int i = 0,j = 0; i < elementListPx.size()&&j < elementListPy.size(); ) {
            Elem ex=elementListPx.get(i);
            Elem ey=elementListPy.get(j);
            if (ex.tid==ey.tid){
                if(UL_P == null){
                    Elem newElement = new Elem(ex.tid, ex.timeStamp,ex.IUtil + ey.IUtil, ey.RUtil);
                    UL_Pxy.addElement(newElement);

                }else{
                    Elem e = findElementWithTID(UL_P, ex.tid);
                    if(e != null){
                        Elem newElement = new Elem(ex.tid, ex.timeStamp,ex.IUtil + ey.IUtil - e.IUtil, ey.RUtil);
                        UL_Pxy.addElement(newElement);

                    }else{
                        Elem newElement = new Elem(ex.tid, ex.timeStamp,ex.IUtil + ey.IUtil, ey.RUtil);
                        UL_Pxy.addElement(newElement);
                    }
                }
                i++;j++;
            }else if (ex.tid>ey.tid){
                j++;
            }else if (ex.tid<ey.tid){

                totalUtility -= (ex.IUtil+ex.RUtil);
                if (i+1<=UL_Px.lastDeltaIndex){
                    totalUtilityLessDelta -=(ex.IUtil+ex.RUtil);
                }
                //if(totalUtility < minUtil) {
                if(totalUtility < minUtil||Double.compare(totalUtilityLessDelta,minUtil*minRatio)<0) {
                   return null;
                }
                i++;
            }

        }
        return UL_Pxy;
    }

    void realTimeWriteOut(Itemset SSI) throws IOException {

        StringBuffer buffer = new StringBuffer();
        buffer.append(Joiner.on(" ").join(SSI.getItemSet()));
        buffer.append("    ");
        buffer.append("delta timestamp and tid: ");
        buffer.append(SSI.getTimestamp()).append("-").append(SSI.getTid());
        buffer.append(" # less delta UTIL: ");
        buffer.append(SSI.getUtility_one());
        buffer.append(" #total UTIL: ");
        buffer.append(SSI.getUtility());
        buffer.append(" #utility ratio: ");
        buffer.append((double) SSI.getUtility_one()/SSI.getUtility());
        writer.write(buffer.toString());
        writer.newLine();
        writer.flush();

    }

    boolean checkNotNull(Object o){
        if (o!=null){
            return true;
        }else {
            return false;
        }
    }

    /**
     *
     * @param revisedDB
     * @param pairMap
     * @param deltaTimestamp
     * @return listOfUtilityLists
     * construct utility-lists ULs for all 1-itemset which satisfy newNTWU>=minutil and newTWU(<=deltaTimestamp)>=minUtil*minRaio
     * EUCS and deltaEUCS
     * @throws IOException
     */
    public List<TUtilityList> SecondScanProcessing(String revisedDB, PairMap pairMap, long deltaTimestamp) throws IOException {

        List<TUtilityList> listOfUtilityLists=new ArrayList<>();
        Map<Integer, TUtilityList> mapItemToUtilityList = new TreeMap<>();//??hashmap
        Map<Integer, Integer> mapItemToNewTWU=pairMap.getMapItemToNewTWU();
        Map<Integer, Integer> MapItemToNewLessdeltaTWU=pairMap.getMapItemToNewLessdeltaTWU();
        for(Integer item: mapItemToNewTWU.keySet()){
            if(mapItemToNewTWU.get(item) >= minUtil && checkNotNull(MapItemToNewLessdeltaTWU.get(item))&&MapItemToNewLessdeltaTWU.get(item)>=minUtil*minRatio){
                TUtilityList uList = new TUtilityList();
                List<Integer> itemset=uList.getItemSet();
                itemset.add(item);
                mapItemToUtilityList.put(item, uList);
                listOfUtilityLists.add(uList);

            }
        }
        String thisLine;
        BufferedReader myInput = null;
        int tid=1;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(revisedDB)));
            // for each line (transaction)
            while ((thisLine = myInput.readLine()) != null) {

                String[] partions=thisLine.split(":");
                String[] items = partions[0].split(" ");
                int newTU=Integer.valueOf(partions[1]);
                String[] itemsUtilities = partions[2].split(" ");
                long timestamp=Long.valueOf(partions[3]);


                int remainingUtility =0;
                int newNewTU=0;
                List<Pair> revisedTransaction = new ArrayList<>();
                for (int i = 0; i < items.length; i++) {
                    int item=Integer.valueOf(items[i]);
                    if (mapItemToUtilityList.keySet().contains(item)){
                        Pair pair = new Pair(Integer.parseInt(items[i]),Integer.parseInt(itemsUtilities[i]));
                        revisedTransaction.add(pair);
                        remainingUtility+=Integer.parseInt(itemsUtilities[i]);
                        newNewTU+=Integer.parseInt(itemsUtilities[i]);
                    }
                }


                if (revisedTransaction.isEmpty()){
                    continue;
                }
                for (int i = 0; i < revisedTransaction.size(); i++) {

                    Pair pair=revisedTransaction.get(i);
                    remainingUtility = remainingUtility - pair.utility;

                    TUtilityList utilityListOfItem = mapItemToUtilityList.get(pair.item);
                    Elem element = new Elem(tid, timestamp, pair.utility, remainingUtility);
                    utilityListOfItem.addElement(element);

                    // BEGIN NEW OPTIMIZATION for EUCS
                    Map<Integer, Integer> mapEUCSItem=mapEUCS.get(pair.item);
                    if (mapEUCSItem == null) {
                        mapEUCSItem=new HashMap<>();
                        mapEUCS.put(pair.item,mapEUCSItem);
                    }

                    Map<Integer, Integer> mapDeltaEUCSItem=mapDeltaEUCS.get(pair.item);
                    if (mapDeltaEUCSItem==null){
                        mapDeltaEUCSItem=new HashMap<>();
                        mapDeltaEUCS.put(pair.item,mapDeltaEUCSItem);
                    }

                    for(int j = i+1; j< revisedTransaction.size(); j++){
                        Pair pairAfter = revisedTransaction.get(j);

                        Integer TWU=mapEUCSItem.get(pairAfter.item);
                        if(TWU == null) {
                            mapEUCSItem.put(pairAfter.item, newNewTU);
                        }else {
                            mapEUCSItem.put(pairAfter.item, mapEUCSItem.get(pairAfter.item) + newNewTU);
                        }
                        if (timestamp<=deltaTimestamp){
                            Integer deltaTWU=mapDeltaEUCSItem.get(pairAfter.item);
                            if(deltaTWU == null) {
                                mapDeltaEUCSItem.put(pairAfter.item, newNewTU);
                            }else {
                                mapDeltaEUCSItem.put(pairAfter.item, mapDeltaEUCSItem.get(pairAfter.item) + newNewTU);
                            }
                        }
                    }

                }
                tid++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(myInput != null){
                myInput.close();
            }
        }
        return listOfUtilityLists;
    }



    public Elem findElementWithTID(TUtilityList ulist, int tid){

        List<Elem> list = ulist.elements;

        int first = 0;
        int last = list.size() - 1;

        while( first <= last )
        {
            int middle = ( first + last ) >>> 1; // divide by 2

            if(list.get(middle).tid < tid){
                first = middle + 1;
            }else if(list.get(middle).tid > tid){
                last = middle - 1;
            }else{
                return list.get(middle);
            }
        }
        return null;
    }

    public long getLastTimestamp(String input) {
        String lastLine=readLastLineV2(input);
        String[] partions=lastLine.split(":");
        long lastTimestamp=Long.valueOf(partions[3]);
        return lastTimestamp;

    }
    public String readLastLineV2(String input) {
        File file=new File(input);
        String lastLine = "";
        try (ReversedLinesFileReader reversedLinesReader = new ReversedLinesFileReader(file, Charset.defaultCharset())) {
            lastLine = reversedLinesReader.readLine();
        } catch (Exception e) {
            System.out.println("error");
        }
        return lastLine;
    }
    public long getFirstTimestamp(String input) throws IOException {
        BufferedReader buffer = new BufferedReader(new FileReader(input));
        String firstLine = buffer.readLine();
        String[] partions=firstLine.split(":");
        long firstTimestamp=Long.valueOf(partions[3]);
        return firstTimestamp;

    }

    /**
     *
     * @param input
     * @param deltaTimestamp
     * @param revisedDB
     * @return  mapItemToNewTWU, mapItemToNewLessdeltaTWU
     * @throws IOException
     * calculate TWU(<=deltaTimestamp) and TWU of each items
     * get revised DB (reviseDB.txt) after delete low TWU(<=deltaTimestamp) and low TWU items
     * get new TWU(<=deltaTimestamp) and TWU of each items in revised DB
     */
    public PairMap FirstScanProcessing(String input, long deltaTimestamp, String revisedDB) throws IOException {

        Map<Integer, Integer> mapItemToTWU=new TreeMap<>();
        Map<Integer, Integer> mapItemToLessdeltaTWU=new TreeMap<>();

        writer = new BufferedWriter(new FileWriter(revisedDB));

        String thisLine;
        BufferedReader myInput = null;
        int totalU=0;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            // for each transaction (line) in the input file
            while ((thisLine = myInput.readLine()) != null) {
                String[] partions=thisLine.split(":");
                String[] items = partions[0].split(" ");
                int TU=Integer.valueOf(partions[1]);
                String[] itemsUtilities = partions[2].split(" ");
                long timestamp=Long.valueOf(partions[3]);

                totalU+=TU;

                if (timestamp<=deltaTimestamp){
                   for (int i = 0; i < items.length; i++) {
                       int item=Integer.valueOf(items[i]);
                       mapItemToTWU.put(item,mapItemToTWU.get(item)==null?TU:mapItemToTWU.get(item)+TU);
                       mapItemToLessdeltaTWU.put(item,mapItemToLessdeltaTWU.get(item)==null?TU:mapItemToLessdeltaTWU.get(item)+TU);
                   }
               }else {
                    for (int i = 0; i < items.length; i++) {
                        int item=Integer.valueOf(items[i]);
                        mapItemToTWU.put(item,mapItemToTWU.get(item)==null?TU:mapItemToTWU.get(item)+TU);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(myInput != null){
                myInput.close();
            }
        }

        this.minUtil=(int) (totalU*minUtilR);

        Map<Integer, Integer> mapItemToNewTWU=new TreeMap<>();
        Map<Integer, Integer> mapItemToNewLessdeltaTWU=new TreeMap<>();
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            while ((thisLine = myInput.readLine()) != null) {
                String[] partions=thisLine.split(":");
                String[] items = partions[0].split(" ");
                int TU=Integer.valueOf(partions[1]);
                String[] itemsUtilities = partions[2].split(" ");
                long timestamp=Long.valueOf(partions[3]);

                int NewTU=0;
                List<Integer> newItems=new ArrayList<>();
                List<Integer> newItemsUtilities=new ArrayList<>();
                for (int i = 0; i < items.length; i++) {
                        int item=Integer.valueOf(items[i]);
                        int itemUtility=Integer.valueOf(itemsUtilities[i]);
                        if (mapItemToTWU.get(item)>=minUtil&&checkNotNull(mapItemToLessdeltaTWU.get(item))&&Double.compare(mapItemToLessdeltaTWU.get(item),minUtil*minRatio)>=0){
                            newItems.add(item);
                            newItemsUtilities.add(itemUtility);
                            NewTU+=itemUtility;
                        }
                }
                if (newItems.isEmpty()){
                    continue;
                }

                for (int i = 0; i < newItems.size(); i++) {
                    int item=newItems.get(i);
                    if (timestamp<=deltaTimestamp){
                        mapItemToNewTWU.put(item,mapItemToNewTWU.get(item)==null?NewTU:mapItemToNewTWU.get(item)+NewTU);
                        mapItemToNewLessdeltaTWU.put(item,mapItemToNewLessdeltaTWU.get(item)==null?NewTU:mapItemToNewLessdeltaTWU.get(item)+NewTU);

                    }else {
                        mapItemToNewTWU.put(item,mapItemToNewTWU.get(item)==null?NewTU:mapItemToNewTWU.get(item)+NewTU);
                    }

                }

                StringBuffer buffer = new StringBuffer();
                buffer.append(Joiner.on(" ").join(newItems));
                buffer.append(":");
                buffer.append(NewTU);
                buffer.append(":");
                buffer.append(Joiner.on(" ").join(newItemsUtilities));
                buffer.append(":");
                buffer.append(timestamp);
                writer.write(buffer.toString());
                writer.newLine();
                writer.flush();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(myInput != null){
                myInput.close();
                writer.close();
            }
        }
        return new PairMap(mapItemToNewTWU,mapItemToNewLessdeltaTWU);
    }
    public void printStats() throws IOException {
        BufferedWriter  writer = new BufferedWriter(new FileWriter(".//experimentRes.txt"));

        System.out.println("=============  SSIM ALGORITHM - STATS =============");
        writer.write("=============  SSIM ALGORITHM - STATS =============");
        writer.newLine();
        System.out.println(" Dataset : " + dataset);
        writer.write(" Dataset : " + dataset);
        writer.newLine();
        System.out.println(" minutil : " + minUtil);
        writer.write(" minutil : " + minUtil);
        writer.newLine();
        System.out.println(" minutilR : " + minUtilR);
        writer.write(" minutilR : " + minUtilR);
        writer.newLine();
        System.out.println(" minRatio : " + minRatio);
        writer.write(" minRatio : " + minRatio);
        writer.newLine();
        System.out.println(" delta : " + delta);
        writer.write(" delta : " + delta);
        writer.newLine();
        System.out.println(" Candidates count : " + candidatesCount);
        writer.write(" Candidates count : " + candidatesCount);
        writer.newLine();
        System.out.println(" SSI count : " + ssiCount);
        writer.write(" SSI count : " + ssiCount);
        writer.newLine();
        System.out.println(" joint count : " + joinCount);
        writer.write(" joint count : " + joinCount);
        writer.newLine();
        System.out.println(" memory usage : " + MemoryLogger.getInstance().getMaxMemory()+ " MB");
        writer.write(" memory usage : " + MemoryLogger.getInstance().getMaxMemory()+ " MB");
        writer.newLine();
        System.out.println(" Total time ~ " + (double)totalTime/1000 + " s");
        writer.write(" Total time ~ " + (double)totalTime/1000 + " s");
        writer.newLine();
        System.out.println(" First Scan time ~ " + (double)firstScantime/1000 + " s");
        writer.write(" First Scan time ~ " + (double)firstScantime/1000 + " s");
        writer.newLine();
        System.out.println(" Second Scan time ~ " + (double)secondScantime/1000 + " s");
        writer.write(" Second Scan time ~ " + (double)secondScantime/1000 + " s");
        writer.newLine();
        System.out.println(" Mining time ~ " + (double)miningTime/1000 + " s");
        writer.write(" Mining time ~ " + (double)miningTime/1000 + " s");
        writer.newLine();
        System.out.println("===================================================");
        writer.write("===================================================");
        writer.newLine();
        writer.flush();
    }




}
