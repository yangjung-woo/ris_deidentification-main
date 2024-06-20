package Data;

import Data.Format.StructedData;
import com.project.model.InsertTagModel;
import org.deidentifier.arx.*;
import org.deidentifier.arx.criteria.KAnonymity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ARXOnWeb {
    public static Data DataInput(List<String> tagNames, List<StructedData> SData) throws IOException {
        Data data = Data.create(InputOutput.TableOutput(tagNames, SData), Charset.defaultCharset(), ',');
        return data;
    }
    public static void Deidentification(List<StructedData> SData, Data data, Integer k, List<InsertTagModel> tagData) throws IOException {
        for(int i = 0; i < tagData.size(); i++){
            data.getDefinition().setAttributeType(tagData.get(i).getTagName(), AttributeType.Hierarchy.create(tagData.get(i).getHierarchyfile(), StandardCharsets.UTF_8, ','));
        }

        // Create an instance of the anonymizer
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(k));
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
                    sDatum.getRow().get(0).setValue(lst.get(0).substring(1));
                    for (int i = 1; i < sDatum.getRow().size(); i++){
                        sDatum.getRow().get(i).setValue((lst.get(i).substring(0)));
                    }
                    sDatum.getRow().get(sDatum.getRow().size()-1).setValue(lst.get(sDatum.getRow().size()-1).substring(0,lst.get(sDatum.getRow().size()-1).length()-1));
                }
            }
        }
    }
}
