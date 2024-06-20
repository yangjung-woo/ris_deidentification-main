package Data;

import Data.Format.StructedData;
import Data.Format.UnstructedData;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputOutput {
    public static List<UnstructedData> readDataFromCsv(String filePath) throws IOException, CsvException {
        CSVReader reader = new CSVReaderBuilder(new FileReader(filePath)).build(); // 1
        List<String[]> tmp = reader.readAll();
        List<UnstructedData> Data = new ArrayList<>();
        for (String[] strings : tmp) {
            String s = strings[0];
            Data.add(new UnstructedData(s));
        }
        return Data;
    }

    public static List<List<String>> TableOutput(List<StructedData> SData){
        List<List<String>> table = new ArrayList<>(SData.size());
        for (int i = 0; i < SData.size(); i++){
            table.add(new ArrayList<>());
            for (int j = 0; j < SData.get(i).size(); j++){
                table.get(i).add(SData.get(i).getRow().get(j).getValue());
            }
        }
        /*System.out.println("age | gender | weight | blood pressure | disease");
        for(int i = 0; i < SData.size(); i++){
            String temp = Integer.toString(i);
            for (int j = 0; j < 5; j++){
                temp += " | " + SData.get(i).getRow().get(j).getValue();
            }
            System.out.println(temp);
        }*/
        return table;
    }

    public static void writeDataToCsv(String filePath, List<UnstructedData> Data) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(filePath));
        for (UnstructedData datum : Data) {
            writer.writeNext(new String[]{datum.getDocument()});
        }
        writer.close();
    }
}
