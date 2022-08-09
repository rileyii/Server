import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReadInc{

	List<String> ReadMethod(){
		File file =new File("incident.txt");
		if(!file.exists()){
        	try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
        }  
		String line = "";
		String[] Line = new String[1];
		List<String> BikeDataList = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while ((line = br.readLine()) != null) {
				BikeDataList.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return BikeDataList;
	}

}
