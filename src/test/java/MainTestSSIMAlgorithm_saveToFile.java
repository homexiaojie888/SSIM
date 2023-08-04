import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;


public class MainTestSSIMAlgorithm_saveToFile {


	public static void main(String [] arg) throws IOException{

		String input = fileToPath("DB_Utility2.txt");
		String output = ".//outputwithoutTprune.txt";

		double minUtilR=0.1 ;
		double minRaio=0.6;
		double delta=0.5;

		SSIMining ssiMining = new SSIMining();
		ssiMining.runAlgorithm(input,output,minUtilR,minRaio,delta);
		ssiMining.printStats();
	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestSSIMAlgorithm_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}

}
