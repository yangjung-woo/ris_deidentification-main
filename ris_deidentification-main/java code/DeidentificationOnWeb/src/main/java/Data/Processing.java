package Data;

import Data.Format.StructedData;
import Data.Format.UnstructedData;
import Data.Format.Word;
import org.deidentifier.arx.*;
import org.deidentifier.arx.criteria.KAnonymity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Processing {
    private List<String> tagNames = new ArrayList<>();
    public Processing(){}
    public Processing(List<String> tagNames){
        for(int i = 0; i < tagNames.size(); i++ ) {
            this.tagNames.add(tagNames.get(i));
        }
    }
    public List<String> getTagNames(){
        return tagNames;
    }
    public int CheckTagName(String sentence){
        int idx = -1;
        String TagName = sentence.substring(1, sentence.indexOf(">"));
        for(int i = 0; i < tagNames.size(); i++){
            if(TagName.equals(tagNames.get(i)))
                idx = i;
        }
        return idx;
    }
    public List<StructedData> U2S(List<UnstructedData> UData, Integer attributeNum){
        List<StructedData> SData = new ArrayList<>();
        for (int i = 0; i < UData.size(); i++){
            SData.add(new StructedData(attributeNum));
        }
        for (int i = 0; i < UData.size(); i++) {
            Pattern pattern = Pattern.compile("<[^<>\\n]*>[^<>\\n]*<[^<>\\n]*>");
            Matcher matcher = pattern.matcher(UData.get(i).getDocument());
            while (matcher.find()) {
                int idx = CheckTagName(matcher.group());
                if(idx != -1) {
                    String target = matcher.group().substring(matcher.group().indexOf(">") + 1, matcher.group().indexOf("<", 1));
                    Word temp = new Word(target, matcher.start(), matcher.end());
                    SData.get(i).setRow(idx, temp);
                }
            }
        }

        return SData;
    }
    public void S2U(List<UnstructedData> UData, List<StructedData> SData){
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
}
