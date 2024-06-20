package com.project.model;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ui.Model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class InsertTagModel {
    private String tagName;
    private String type;
    private File hierarchyfile;
    private List<String[]> hierarchy;
    public InsertTagModel(String tagName){
        this.tagName = tagName;
        this.type = null;
        this.hierarchyfile = null;
        this.hierarchy = null;
    }
    public void set(String type, File file) throws IOException, CsvException {
        this.type = type;
        this.hierarchyfile = file;
        this.hierarchy = CSV2Table(file);
    }
    private List<String[]> CSV2Table(File file) throws IOException, CsvException {
        CSVReader reader = new CSVReaderBuilder(new FileReader(file)).build();
        List<String[]> tmp = reader.readAll();
        List<String[]> tables = new ArrayList<>();
        for(int i = 0; i < tmp.size(); i++){
            String[] tag = new String[tmp.get(i).length];
            for(int j = 0; j < tmp.get(i).length; j++) {
                tag[j] = tmp.get(i)[j];
            }
            tables.add(tag);
        }
        return tables;
    }
}
