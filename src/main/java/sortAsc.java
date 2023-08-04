import com.google.common.base.Joiner;

import java.io.*;
import java.net.URL;
import java.util.*;

//keep all dataset sorted by lex-order
public class sortAsc {
    public static void main(String[] args) throws IOException {

        String input=fileToPath("retail_utility_timestamp.txt");
        String output="d:\\demo1.txt";

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));

        String thisLine;
        BufferedReader myInput = null;
        Map<Integer,Integer> itemToUtility=new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1-o2;
            }
        });
        int tid=1;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
            // for each transaction (line) in the input file
            while ((thisLine = myInput.readLine()) != null) {
                System.out.println(tid++);
                String newline="";
                String[] partions=thisLine.split(":");
                String[] items = partions[0].split(" ");
                int TU=Integer.valueOf(partions[1]);
                String[] itemsUtilities = partions[2].split(" ");
                long timestamp=Long.valueOf(partions[3]);
                for (int i = 0; i < items.length; i++) {
                    itemToUtility.put(Integer.valueOf(items[i]),Integer.valueOf(itemsUtilities[i]));
                }
                newline = Joiner.on(" ").join(itemToUtility.keySet())+":";
                newline = newline + String.valueOf(TU)+":";
                newline = newline + Joiner.on(" ").join(itemToUtility.values())+":";
                newline = newline + String.valueOf(timestamp);
                writer.write(newline);
                writer.newLine();

                itemToUtility.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(myInput != null){
                myInput.close();
            }
            writer.flush();
            writer.close();
        }


    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException{
        URL url = sortAsc.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
