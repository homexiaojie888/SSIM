import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;


public class MainTestPSSIMAlgorithm_saveToFile {


	//must keep all items ascending in each transacttions of file(use sortASC.java)
	public static void main(String [] arg) throws IOException{


		String input = fileToPath("DB_Utility2.txt");
		String output = ".//outputTPrune.txt";

		double minUtilR=0.1 ;
		double minRaio=0.6;
		double delta=0.5;

		PSSIMining pssiMining = new PSSIMining();
		pssiMining.runAlgorithm(input,output,minUtilR,minRaio,delta);
		pssiMining.printStats();

}

	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestPSSIMAlgorithm_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}

}
