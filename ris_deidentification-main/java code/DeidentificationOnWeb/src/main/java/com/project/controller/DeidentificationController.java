package com.project.controller;

import Data.ARXOnWeb;
import Data.Format.StructedData;
import Data.Format.UnstructedData;
import Data.InputOutput;
import Data.Processing;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.project.model.InsertTableModel;
import com.project.model.InsertTagModel;
import org.deidentifier.arx.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DeidentificationController {
    int attributeNum = 1;
    List<UnstructedData> UData;
    List<StructedData> SData;

    Processing processing;
    Data data;
    List<InsertTagModel> tagData;


    @GetMapping("/")
    public String printDocument(Model model) {
        UData = new ArrayList<>();
        SData = new ArrayList<>();
        processing = new Processing();
        data = Data.DefaultData.create();
        tagData = new ArrayList<>();
        model.addAttribute("attributeNum", attributeNum);
        SData.add(new StructedData(attributeNum));
        Table2Model(model, "tableList", SData);
        return "Document";
    }
    @PostMapping("fileUpload")
    public String fileUploadView(Model model, @RequestParam MultipartFile csvfile) throws IOException, CsvException {

        File file = MultipartFile2File(csvfile);
        UData = InputOutput.readDataFromCsv(file);
        model.addAttribute("DocumentList", UData);
        Table2Model(model, "tableList", SData);
        return "Document";
    }
    @PostMapping("tagNameUpload")
    public String TagNameUploadView(Model model, @RequestParam List<String> tagNames) throws IOException {
        processing = new Processing(tagNames);
        attributeNum = tagNames.size();
        SData = processing.U2S(UData, attributeNum);
        for (String tagName : tagNames) {
            tagData.add(new InsertTagModel(tagName));
        }
        model.addAttribute("TagNames", tagData);
        model.addAttribute("DocumentList", UData);
        Table2Model(model, "tableList", SData);
        data = Data.create(InputOutput.TableOutput(tagNames, SData), Charset.defaultCharset(), ',');
        return "Document";
    }

    @PostMapping("hierarchyUpload")
    public String HierarchyUploadView(Model model, @RequestParam MultipartFile hierarchyfile, @RequestParam String tagName, @RequestParam String type) throws IOException, CsvException {
        File file = MultipartFile2File(hierarchyfile);
        for (int i = 0; i < tagData.size(); i++){
            if(tagData.get(i).getTagName().equals(tagName)){
                tagData.get(i).set(type, file);
            }
        }
        model.addAttribute("TagNames", tagData);
        model.addAttribute("DocumentList", UData);
        Table2Model(model, "tableList", SData);
        return "Document";
    }

    @PostMapping("anonymize")
    public String resultView(Model model, @RequestParam Integer k/*, @RequestParam List<String> privacy*/) throws IOException {

        model.addAttribute("TagNames", tagData);
        model.addAttribute("InDocumentList", UData);
        Table2Model(model, "IntableList", SData);
        ARXOnWeb.Deidentification(SData,data,k,tagData);
        Table2Model(model, "OuttableList", SData);
        processing.S2U(UData,SData);
        model.addAttribute("OutDocumentList", UData);
        return "result";
    }

    public void Table2Model(Model model, String attributeName, List<StructedData> SData) {
        List<InsertTableModel> tables = new ArrayList<>();
        for (StructedData sDatum : SData) {
            List<String> tag = new ArrayList<>();
            for (int i = 0; i < attributeNum; i++){
                tag.add(sDatum.getRow().get(i).getValue());
            }

            InsertTableModel insertTableModel = new InsertTableModel(tag, attributeNum);
            tables.add(insertTableModel);
        }
        model.addAttribute(attributeName, tables);
    }

    public File MultipartFile2File(MultipartFile mfile) throws IOException {
        File file = new File(mfile.getOriginalFilename());
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(mfile.getBytes());
        fos.close();
        return file;
    }
}