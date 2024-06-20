package Data;

import Data.Format.StructedData;
import Data.Format.UnstructedData;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.eclipse.swt.internal.C;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputOutput {
    public static List<UnstructedData> readDataFromCsv(File file) throws IOException, CsvException {
        CSVReader reader = new CSVReaderBuilder(new FileReader(file)).build();
        List<String[]> tmp = reader.readAll();
        List<UnstructedData> Data = new ArrayList<>();
        for (String[] strings : tmp) {
            String s = strings[0];
            Data.add(new UnstructedData(s));
        }
        return Data;
    }

    public static File TableOutput(List<String> tagNames, List<StructedData> SData) throws IOException {
        List<List<String>> table = new ArrayList<>(SData.size());
        for (int i = 0; i < SData.size(); i++){
            table.add(new ArrayList<>());
            if(!SData.get(i).getRow().get(0).IsEmpty()) {
                for (int j = 0; j < SData.get(i).size(); j++) {
                    table.get(i).add(SData.get(i).getRow().get(j).getValue());
                }
            }
        }

        String filePath = "CSVData";
        CSVWriter writer = new CSVWriter((new FileWriter(filePath)));
        String[] temp = tagNames.toArray(new String[tagNames.size()]);
        writer.writeNext(temp);
        for (int i = 0; i < table.size(); i++){
            if(!(table.get(i).isEmpty())) {
                temp = table.get(i).toArray(new String[table.get(i).size()]);
                writer.writeNext(temp);
            }
        }
        writer.close();
        File file = new File(filePath);
        return file;
    }

    public static void writeDataToCsv(String filePath, List<UnstructedData> Data) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(filePath));
        for (UnstructedData datum : Data) {
            writer.writeNext(new String[]{datum.getDocument()});
        }
        writer.close();
    }
}
