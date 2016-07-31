package db;

import java.util.ArrayList;

public interface DBReadService {
	
	public void initialize(ArrayList<String> query);
	
	public double getIdf(String t);
	
	public double getPstd(String t, String doc);
	
	public double getPustd(String t, String doc);
	
	public ArrayList<String> getRelatedDocs(ArrayList<String> terms);
	
	public ArrayList<String> getPaths(ArrayList<String> docs);
	
	
	

}
