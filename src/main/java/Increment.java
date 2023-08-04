import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
//prepare data with different size to test Scalability
public class Increment {
    List<String> DB=new ArrayList<>();
    int count=0;
    public void read(String input) throws IOException {

        String thisline0;
        BufferedReader br0 = new BufferedReader(new FileReader(input));
        while ((thisline0 = br0.readLine()) != null) {
            DB.add(thisline0);
        }
    }
    private void writeFile(String output) throws IOException {
        FileWriter fw = new FileWriter(output);
        for (int i = 0; ; i=(i+1)%DB.size()) {
            String line=DB.get(i);
            fw.write(line);
            fw.write("\n");
            fw.flush();
            count++;
            if (count==1024000){
                return;
            }
        }
    }
    public static void main(String[] args) throws IOException {
        String input = fileToPath("kosarak.txt");
        String output = ".//kosarak_1024k.txt";
        Increment increment=new Increment();
        increment.read(input);
        increment.writeFile(output);

    }
    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = Increment.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
