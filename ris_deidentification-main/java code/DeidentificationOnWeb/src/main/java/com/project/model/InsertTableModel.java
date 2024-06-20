package com.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InsertTableModel {
    private List<String> tag = new ArrayList<>();
    public InsertTableModel(List<String> tag, Integer attributeNum){
        for(int i = 0; i < attributeNum; i++){
            this.tag.add(tag.get(i));
        }
    }
}
