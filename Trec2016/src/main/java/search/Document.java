package search;

public class Document implements Comparable<Document>{
	
	String fileName;
	double value;
	
	public Document(String fileName, double value){
		this.fileName=fileName;
		this.value=value;
	}
	
	public int compareTo(Document arg0) {
		// TODO Auto-generated method stub
		if(this.value>arg0.value){
			return 1;
		}else{
			return -1;
		}
	}
	
}
