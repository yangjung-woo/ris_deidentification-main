package Data.Format;

public class Word {
    private String value;
    private final Integer start;
    private final Integer end;

    public Word(){
        this.value = "-";
        this.start = -1;
        this.end = -1;
    }
    public Word(String value, Integer start, Integer end){
        this.value = value;
        this.start = start;
        this.end = end;
    }
    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){return value;}
    public Integer getStart(){return start;}
    public Integer getEnd(){return end;}
    public Integer LengthDiffer(){
        return value.length()-(end-start);
    }
    public boolean IsEmpty(){ return value.equals("-") && start == -1 && end == -1; }
}
