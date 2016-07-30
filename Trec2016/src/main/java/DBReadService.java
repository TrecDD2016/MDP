import java.util.ArrayList;

/**
 * @since 2016年4月18日 下午7:49:32
 * @version 1.0
 */
public interface DBReadService {
	
	public void initialize(ArrayList<String> query);
	
	public double getIdf(String t);
	
	public double getPstd(String t, String doc);
	
	public double getPustd(String t, String doc);
	
	public ArrayList<String> getRelatedDocs(ArrayList<String> terms);
	
	public ArrayList<String> getPaths(ArrayList<String> docs);
	
	
	

}
