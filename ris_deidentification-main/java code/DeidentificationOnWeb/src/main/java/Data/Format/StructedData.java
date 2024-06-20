package Data.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StructedData {
    private final List<Word> row = new ArrayList<>();

    public StructedData(){

    }
    public StructedData(Integer length){
        for(int j = 0; j < length; j++) {
            this.row.add(new Word());
        }
    }

    public List<Word> getRow() { return row; }
    public void setRow(Integer i, Word word){ row.set(i, word); }
    public Integer size(){ return row.size(); }

    public void add(Word word){
        this.row.add(word);
    }

    public void sort(){
        row.sort(Comparator.comparingInt(Word::getStart));
    }
}
