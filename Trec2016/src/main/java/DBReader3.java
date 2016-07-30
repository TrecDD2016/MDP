import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @since 2016年4月18日 下午8:29:15
 * @version 1.0
 */
public class DBReader3 implements DBReadService{
	
	private HashMap<String, Double> tcMap = new HashMap<String, Double>();
	private HashMap<String, Double> idfMap = new HashMap<String, Double>();
	private HashMap<String, Integer> dlenthMap = new HashMap<String, Integer>();
	private HashMap<String, HashMap<String, Integer>> tdMap = new HashMap<String, HashMap<String,Integer>>();
	private HashSet<String> relatedDocs = new HashSet<String>();

	/* (non-Javadoc)
	 * @see DBReadService#getIdf(java.lang.String)
	 */
	public double getIdf(String t) {
		if (idfMap.containsKey(t)) {
			return idfMap.get(t);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see DBReadService#getPstd(java.lang.String, java.lang.String)
	 */
	public double getPstd(String t, String doc) {
		int td = 0;
		if (tdMap.containsKey(t) && tdMap.get(t).containsKey(doc)) {
			td = tdMap.get(t).get(doc);
		}
		double tc = 0;
		if (tcMap.containsKey(t)) {
			tc = tcMap.get(t);
		}
		int dlength = 0;
		if (dlenthMap.containsKey(doc)) {
			dlength = dlenthMap.get(doc);
		}
		return (td + Constants.u * tc) / ((double)dlength + Constants.u);
	}

	/* (non-Javadoc)
	 * @see DBReadService#getPustd(java.lang.String, java.lang.String)
	 */
	public double getPustd(String t, String doc) {
			
			int td = 0;
			if (tdMap.containsKey(t) && tdMap.get(t).containsKey(doc)) {
				td = tdMap.get(t).get(doc);
			}
			
			int dlength = 0;
			if (dlenthMap.containsKey(doc)) {
				dlength = dlenthMap.get(doc);
			}
			
			if (dlength != 0) {
				return (td ) / ((double)dlength);
			}else {
				return 0;
			}
	}

	/* (non-Javadoc)
	 * @see DBReadService#getRelatedDocs(java.util.ArrayList)
	 */
	public ArrayList<String> getRelatedDocs(ArrayList<String> terms) {
		return new ArrayList<String>(relatedDocs); 
	}
	
//	public static void main(String[]args) {
//		ArrayList<String> a = new ArrayList<String>();
//		a.add("clueweb09-en0000-23-00194");
//		a.add("clueweb09-en0000-43-16967");
//		ArrayList<String> res = new DBReader3().getRelatedDocs(a);
//		for (String s : res) {
//			System.out.println(s);
//		}
//		System.out.println(new DBReader3().getIdf("contact"));
//		System.out.println(new DBReader3().getPstd("contact", "clueweb09-en0000-43-16967"));
//		System.out.println(new DBReader3().getPustd("contact", "clueweb09-en0000-43-16967"));
//	}

	/* (non-Javadoc)
	 * @see DBReadService#getPaths(java.util.ArrayList)
	 */
	public ArrayList<String> getPaths(ArrayList<String> docs) {
		ArrayList<String> res = new ArrayList<String>();
		if (docs == null || docs.size() == 0) {
			return res;
		}
		StringBuffer sql = new StringBuffer("select path from path where doc in (");
		for (int i=0;i<docs.size();i++) {
			sql.append("?,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(')');
		PreparedStatement ps = DBUtility.getPreparedStatement(sql.toString());
		try {
			for (int i=0;i<docs.size();i++) {
				ps.setString(i + 1, docs.get(i));
			}
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				res.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see DBReadService#initialize(java.lang.String)
	 */
	public void initialize(ArrayList<String> query) {
		tcMap.clear();
		idfMap.clear();
		dlenthMap.clear();
		tdMap.clear();
		relatedDocs.clear();
		
		if (query == null || query.size() == 0) {
			return;
		}
		
		StringBuffer sb = new StringBuffer("select T.term, T.doc, T.td, tc, length, idf from ");
		sb.append("(select * from td where term in (CONDITION)) T ");
		sb.append("left join tc on T.term = tc.term left join idf on T.term = idf.term ");
		sb.append("left join dlength on T.doc = dlength.doc");
		
		StringBuffer conditionSb = new StringBuffer();
		for (int i=0;i<query.size();i++) {
			conditionSb.append("?,");
		}
		conditionSb.deleteCharAt(conditionSb.length() - 1);
		String querySQL = sb.toString().replace("CONDITION", conditionSb.toString());

		PreparedStatement ps = DBUtility.getPreparedStatement(querySQL);
		
		try {
			for (int i=0;i<query.size();i++) {
				ps.setString(i + 1, query.get(i).replaceAll("'", "''"));
			}
			
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String term = rs.getString(1);
				String doc = rs.getString(2);
				int td = rs.getInt(3);
				double tc = rs.getDouble(4);
				int dlength = rs.getInt(5);
				double idf = rs.getDouble(6);
				
				relatedDocs.add(doc);
				tcMap.put(term, tc);
				idfMap.put(term, idf);
				dlenthMap.put(doc, dlength);
				
				if (!tdMap.containsKey(term)) {
					tdMap.put(term, new HashMap<String, Integer>());
				}
				tdMap.get(term).put(doc, td);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}

}
