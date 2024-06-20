import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.deidentifier.arx.*;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.Data.DefaultData;
import org.deidentifier.arx.aggregates.HierarchyBuilderIntervalBased;
import org.deidentifier.arx.criteria.KAnonymity;

public class Main {
    public static void writeDataToCsv(String filePath, List<String> Data) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(filePath));
        for (int i = 0; i < Data.size(); i++){
            writer.writeNext(new String[]{(String) Data.get(i)});
        }
        writer.close();
    }
    public static List readDataFromCsv(String filePath) throws IOException, CsvException {
        CSVReader reader = new CSVReaderBuilder(new FileReader(filePath)).build(); // 1
        List<String[]> tmp = reader.readAll();
        List<String> Data = new ArrayList<>(tmp.size());
        for (int i = 0; i < tmp.size(); i++) {
            String s = tmp.get(i)[0];
            Data.add(s);
        }
        return Data;
    }
    public static void U2S(List<Queue<String>> SData, Vector<List<Integer>> Offset, String Doc, int DocNum){
        Vector<String> tmp = new Vector<String>();
        Pattern pattern = Pattern.compile("<[^<>]*>[^<>]*<[^<>]*>");
        Matcher matcher = pattern.matcher(Doc);
        while(matcher.find()){
            int idx = CheckTagName(matcher.group());
            Offset.add(List.of(DocNum, idx, matcher.start(), matcher.end())); // [Document No., TagName, Word Start address, Word end address]
            String word = matcher.group().substring(matcher.group().indexOf(">")+1, matcher.group().indexOf("<",1)); // Extract Word
            SData.get(idx).add(word);
        }
        System.out.println();
        for (int i = 0; i < tmp.size(); i++){
            System.out.println(tmp.get(i));
        }
    }
    public static int CheckTagName(String sentence){
        int idx = -1;
        String TagName = sentence.substring(1, sentence.indexOf(">"));
        switch(TagName){
            case "age": idx = 0; break;
            case "gender": idx = 1; break;
            case "weight": idx = 2; break;
            case "blood pressure": idx = 3; break;
            case "disease": idx = 4; break;
        }
        return idx;
    }
    public static void S2U(List<String> UData, List<Queue<String>> SData, Vector<List<Integer>> Offset){
        List<Integer> lengthDiffer =new ArrayList<Integer>(Collections.nCopies(UData.size(), 0));
        for(int i = 0; i < Offset.size(); i++) {
            String word = SData.get(Offset.get(i).get(1)).poll();
            int start = Offset.get(i).get(2) + lengthDiffer.get(Offset.get(i).get(0)); // Word start offset update
            int end = Offset.get(i).get(3) + lengthDiffer.get(Offset.get(i).get(0)); // Word end offset update
            lengthDiffer.set(Offset.get(i).get(0),lengthDiffer.get(Offset.get(i).get(0)) + word.length() - (Offset.get(i).get(3)-Offset.get(i).get(2))); // word length difference update
            String tmp = UData.get(Offset.get(i).get(0)).substring(0, start) + word + UData.get(Offset.get(i).get(0)).substring(end); // replace word
            UData.set(Offset.get(i).get(0),tmp); //update Document replace
        }
    }
    public static void Deidentification1(List<Queue<String>> SData) throws IOException {
        DefaultData data = Data.create();
        data.add("age", "gender", "weight", "blood pressure", "disease");

        while( !SData.get(0).isEmpty()) {
            data.add(SData.get(0).poll(), SData.get(1).poll(), SData.get(2).poll(), SData.get(3).poll(), SData.get(4).poll());
        }

        data.getDefinition().setAttributeType("age", Hierarchy.create("./Data/age hierarchy.csv", StandardCharsets.UTF_8, ','));
        data.getDefinition().setAttributeType("gender", AttributeType.INSENSITIVE_ATTRIBUTE);
        data.getDefinition().setAttributeType("weight", Hierarchy.create("./Data/weight hierarchy.csv", StandardCharsets.UTF_8, ','));
        data.getDefinition().setAttributeType("blood pressure", Hierarchy.create("./Data/blood pressure hierarchy.csv", StandardCharsets.UTF_8, ','));
        data.getDefinition().setAttributeType("disease", AttributeType.INSENSITIVE_ATTRIBUTE);


        // Create an instance of the anonymizer
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(2));
        config.setSuppressionLimit(0d);

        ARXResult result = anonymizer.anonymize(data, config);

        // Process results
        if (result.getGlobalOptimum() != null) {
            Iterator<String[]> transformed = result.getOutput(false).iterator();
            transformed.next();
            while (transformed.hasNext()) {
                String temp = Arrays.toString(transformed.next());
                List<String> lst = Stream.of(temp.split(",")).collect(Collectors.toList());
                SData.get(0).add(lst.get(0).substring(1));
                SData.get(1).add(lst.get(1));
                SData.get(2).add(lst.get(2));
                SData.get(3).add(lst.get(3));
                SData.get(4).add(lst.get(4).substring(0,lst.get(4).length()-1));
            }
        }
    }
    public static void Deidentification(List<Queue<String>> SData) throws IOException {
        //age
        DefaultData data = Data.create();
        data.add("age");

        while( !SData.get(0).isEmpty()) {
            data.add(SData.get(0).poll(), SData.get(1).poll());
        }

        data.getDefinition().setAttributeType("age", Hierarchy.create("./Data/Data file age hierarchy.csv", StandardCharsets.UTF_8, ','));
        //data.getDefinition().setAttributeType("gender", AttributeType.INSENSITIVE_ATTRIBUTE);

        // Create an instance of the anonymizer
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(3));
        config.setSuppressionLimit(0d);

        ARXResult result = anonymizer.anonymize(data, config);

        // Process results
        if (result.getGlobalOptimum() != null) {
            Iterator<String[]> transformed = result.getOutput(false).iterator();
            transformed.next();
            while (transformed.hasNext()) {
                String temp = Arrays.toString(transformed.next());
                List<String> lst = Stream.of(temp.split(",")).collect(Collectors.toList());
                SData.get(0).add(lst.get(0).substring(1, lst.get(0).length()-1));
                //SData.get(1).add(lst.get(1).substring(0,lst.get(1).length()-1));
            }
        }

        //weight
        data = Data.create();
        data.add("weight");

        while( !SData.get(2).isEmpty()) {
            data.add(SData.get(2).poll());
        }

        data.getDefinition().setAttributeType("weight", Hierarchy.create("./Data/Data file weight hierarchy.csv", StandardCharsets.UTF_8, ','));

        // Create an instance of the anonymizer
        anonymizer = new ARXAnonymizer();
        config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(3));
        config.setSuppressionLimit(0d);

        result = anonymizer.anonymize(data, config);

        // Process results
        if (result.getGlobalOptimum() != null) {
            Iterator<String[]> transformed = result.getOutput(false).iterator();
            transformed.next();
            while (transformed.hasNext()) {
                String temp = Arrays.toString(transformed.next());
                List<String> lst = Stream.of(temp.split(",")).collect(Collectors.toList());
                SData.get(2).add(lst.get(0).substring(1, lst.get(0).length()-1));
            }
        }
        //blood pressure, disease
        data = Data.create();
        data.add("blood pressure", "disease");

        while( !SData.get(3).isEmpty()) {
            data.add(SData.get(3).poll());
        }

        data.getDefinition().setAttributeType("blood pressure", Hierarchy.create("./Data/Data file blood pressure hierarchy.csv", StandardCharsets.UTF_8, ','));
        data.getDefinition().setAttributeType("disease", AttributeType.INSENSITIVE_ATTRIBUTE);


        // Create an instance of the anonymizer
        anonymizer = new ARXAnonymizer();
        config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(3));
        config.setSuppressionLimit(0d);

        result = anonymizer.anonymize(data, config);

        // Process results
        if (result.getGlobalOptimum() != null) {
            Iterator<String[]> transformed = result.getOutput(false).iterator();
            transformed.next();
            while (transformed.hasNext()) {
                String temp = Arrays.toString(transformed.next());
                List<String> lst = Stream.of(temp.split(",")).collect(Collectors.toList());
                SData.get(3).add(lst.get(0).substring(1));
                SData.get(4).add(lst.get(1).substring(0,lst.get(1).length()-1));
            }
        }
    }

    public static void main(String args[]) throws IOException, CsvException {
        // initalize
        int n = 5;
        List<String> UData = readDataFromCsv("Data file.csv"); // read Csv Data
        List<Queue<String>> SData = new ArrayList<>(n);
        Vector<List<Integer>> Offset = new Vector<>();
        for (int i = 0; i < n; i ++){
            SData.add(new LinkedList<>());
        }

        // Unstructed Data to Structed Data
        for (int i = 0; i < UData.size(); i++){
            U2S(SData, Offset, UData.get(i), i);
        }

        /*List<String> strData = new ArrayList<>();
        while (!SData.get(2).isEmpty()){
            strData.add(SData.get(2).poll());
        }
        writeDataToCsv("weight.csv", strData);*/

        Deidentification(SData);

       /* List<String> strData = new ArrayList<>();
        while (!SData.get(4).isEmpty()){
            strData.add(SData.get(4).poll());
        }
        writeDataToCsv("disease_temp.csv", strData);*/

        // Structed Data to Unstructed Data
        S2U(UData, SData, Offset);
        writeDataToCsv("result file.csv", UData);
    }
}