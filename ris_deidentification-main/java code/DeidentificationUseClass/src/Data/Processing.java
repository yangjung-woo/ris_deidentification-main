package Data;

import Data.Format.StructedData;
import Data.Format.UnstructedData;
import Data.Format.Word;

import org.deidentifier.arx.*;
import org.deidentifier.arx.criteria.KAnonymity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Processing {


    public static int CheckTagName(String sentence){
        int idx = -1;
        String TagName = sentence.substring(1, sentence.indexOf(">"));
        switch (TagName) {
            case "age" -> idx = 0;
            case "gender" -> idx = 1;
            case "weight" -> idx = 2;
            case "blood pressure" -> idx = 3;
            case "disease" -> idx = 4;
        }
        return idx;
    }
    public static List<StructedData> U2S(List<UnstructedData> UData, Integer attributeNum){
        List<StructedData> SData = new ArrayList<>();
        for (int i = 0; i < UData.size(); i++){
            SData.add(new StructedData(attributeNum));
        }
        for (int i = 0; i < UData.size(); i++) {
            Pattern pattern = Pattern.compile("<[^<>]*>[^<>]*<[^<>]*>");
            Matcher matcher = pattern.matcher(UData.get(i).getDocument());
            while (matcher.find()) {
                int idx = CheckTagName(matcher.group());
                String target = matcher.group().substring(matcher.group().indexOf(">") + 1, matcher.group().indexOf("<", 1));
                Word temp = new Word(target, matcher.start(), matcher.end());
                SData.get(i).setRow(idx, temp);
            }
        }

        return SData;
    }
    public static void S2U(List<UnstructedData> UData, List<StructedData> SData){
        for(int i = 0; i < SData.size(); i++) {
            StructedData temp = new StructedData();
            for (int j = 0; j < SData.get(i).size(); j++){
                if(!SData.get(i).getRow().get(j).IsEmpty()) {
                    temp.add(SData.get(i).getRow().get(j));
                }
            }
            temp.sort();
            for(int j = 0; j < temp.size(); j++){
                int start = temp.getRow().get(j).getStart() + UData.get(i).getLengthUpdate();
                int end = temp.getRow().get(j).getEnd() + UData.get(i).getLengthUpdate();
                UData.get(i).UpdateLength(temp.getRow().get(j).LengthDiffer());
                String tmp = UData.get(i).getDocument().substring(0, start) + temp.getRow().get(j).getValue() + UData.get(i).getDocument().substring(end);
                UData.get(i).setDocument(tmp);
            }
        }
    }
    public static void Deidentification(List<StructedData> SData) throws IOException {
        //age
        Data.DefaultData data = Data.create();
        data.add("age");

        for (StructedData sDatum : SData) {
            if (!sDatum.getRow().get(0).IsEmpty()) {
                data.add(sDatum.getRow().get(0).getValue());
            }
        }
        data.getDefinition().setAttributeType("age", AttributeType.Hierarchy.create("resource/hierarchy/Data file age hierarchy.csv", StandardCharsets.UTF_8, ','));

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
            for (StructedData sDatum : SData) {
                if (!sDatum.getRow().get(0).IsEmpty()) {
                    String temp = Arrays.toString(transformed.next());
                    List<String> lst = Stream.of(temp.split(",")).toList();
                    sDatum.getRow().get(0).setValue(lst.get(0).substring(1, lst.get(0).length() - 1));
                }
            }
        }

        data = Data.create();
        data.add("weight");

        for(int i = 0; i < SData.size(); i++){
            if(!SData.get(i).getRow().get(2).IsEmpty()) {
                data.add(SData.get(i).getRow().get(2).getValue());
            }
        }

        data.getDefinition().setAttributeType("weight", AttributeType.Hierarchy.create("resource/hierarchy/Data file weight hierarchy.csv", StandardCharsets.UTF_8, ','));

        // Create an instance of the anonymizer
        anonymizer = new ARXAnonymizer();
        config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(3));
        config.setSuppressionLimit(0d);

        result = anonymizer.anonymize(data, config);
        if (result.getGlobalOptimum() != null) {
            Iterator<String[]> transformed = result.getOutput(false).iterator();
            transformed.next();
            for (StructedData sDatum : SData) {
                if (!sDatum.getRow().get(2).IsEmpty()) {
                    String temp = Arrays.toString(transformed.next());
                    List<String> lst = Stream.of(temp.split(",")).toList();
                    sDatum.getRow().get(2).setValue(lst.get(0).substring(1, lst.get(0).length() - 1));
                }
            }
        }

        /*data = Data.create();
        data.add("blood pressure");

        for(int i = 0; i < SData.size(); i++){
            if(!SData.get(i).getRow().get(2).IsEmpty()) {
                data.add(SData.get(i).getRow().get(2).getValue());
            }
        }

        data.getDefinition().setAttributeType("weight", AttributeType.Hierarchy.create("resource/hierarchy/Data file weight hierarchy.csv", StandardCharsets.UTF_8, ','));

        // Create an instance of the anonymizer
        anonymizer = new ARXAnonymizer();
        config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(3));
        config.setSuppressionLimit(0d);

        result = anonymizer.anonymize(data, config);
        if (result.getGlobalOptimum() != null) {
            Iterator<String[]> transformed = result.getOutput(false).iterator();
            transformed.next();
            for (StructedData sDatum : SData) {
                if (!sDatum.getRow().get(2).IsEmpty()) {
                    String temp = Arrays.toString(transformed.next());
                    List<String> lst = Stream.of(temp.split(",")).toList();
                    sDatum.getRow().get(2).setValue(lst.get(0).substring(1, lst.get(0).length() - 1));
                }
            }
        }*/

    }
}
