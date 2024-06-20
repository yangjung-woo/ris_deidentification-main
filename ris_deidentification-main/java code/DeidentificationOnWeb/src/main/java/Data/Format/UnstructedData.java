package Data.Format;

public class UnstructedData {
    private String Document;
    private Integer lengthUpdate;
    public UnstructedData(String Document){
        this.Document = Document;
        this.lengthUpdate = 0;
    }

    public String getDocument() { return Document; }
    public Integer getLengthUpdate(){ return lengthUpdate; }
    public void setDocument(String Document){ this.Document = Document; }
    public void UpdateLength(Integer lengthDiffer) { this.lengthUpdate += lengthDiffer; }
}
