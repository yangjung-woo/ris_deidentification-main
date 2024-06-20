import java.io.IOException;
import java.util.*;

import Data.Format.StructedData;
import Data.Format.UnstructedData;
import Data.InputOutput;
import Data.Processing;
import com.opencsv.exceptions.CsvException;

public class Main {
    public static void main(String[] args) throws IOException, CsvException {
        int attributeNum = 5;
        List<UnstructedData> UData = InputOutput.readDataFromCsv("resource/discharge.csv");

        List<StructedData> SData = Processing.U2S(UData, attributeNum);

        List<List<String>> table = InputOutput.TableOutput(SData);

        Processing.Deidentification(SData);

        table = InputOutput.TableOutput(SData);

        Processing.S2U(UData, SData);

        InputOutput.writeDataToCsv("resource/result file.csv", UData);
    }
}

/*
    To do
    Deidentification
    make hierarchy
    empty data problem
 */