package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBReader implements DBReadService{

	/* (non-Javadoc)
	 * @see db.DBReadService#getIdf(java.lang.String)
	 */
	public double getIdf(String t) {
		PreparedStatement ps = DBUtility.getPreparedStatement("select idf from idf where term=?");
		if (ps == null) {
			return 0;
		}
		try {
			ps.setString(1, t);
			ResultSet rs  = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see db.DBReadService#getPstd(java.lang.String, java.lang.String)
	 */
	public double getPstd(String t, String doc) {
		PreparedStatement ps = DBUtility.getPreparedStatement("select pstd from pstd where term=? and doc=?");
		if (ps == null) {
			return 0;
		}
		try {
			ps.setString(1, t);
			ps.setString(2, doc);
			ResultSet rs  = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see db.DBReadService#getPustd(java.lang.String, java.lang.String)
	 */
	public double getPustd(String t, String doc) {
		PreparedStatement ps = DBUtility.getPreparedStatement("select pustd from pustd where doc=? and term=?");
		if (ps == null) {
			return 0;
		}
		try {
			ps.setString(1, doc);
			ps.setString(2, t);
			ResultSet rs  = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


	public Map<String,Double> getPustds(String t, List<String> docs) {

		StringBuffer sql = new StringBuffer("select doc,pustd from pustd where doc in (");
		for (String doc: docs){
			sql.append("'"+doc+"',");
		}
		sql.setCharAt(sql.length()-1, ')');

		sql.append(" and term='");
		sql.append(t);
		sql.append("'");

		Map<String,Double> docPustdMap = new HashMap<String, Double>();

		try {
			PreparedStatement ps = DBUtility.getPreparedStatement(sql.toString());
			ResultSet rs  = ps.executeQuery();
			if (rs.next()) {
				docPustdMap.put(rs.getString(1),rs.getDouble(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return docPustdMap;
	}


	/* (non-Javadoc)
	 * @see db.DBReadService#getRelatedDocs(java.util.ArrayList)
	 */
	public ArrayList<String> getRelatedDocs(ArrayList<String> terms) {
		ArrayList<String> res = new ArrayList<String>();
		if (terms == null || terms.size() == 0) {
			return res;
		}
		StringBuffer sql = new StringBuffer("select distinct doc from td where term in (");
		for (int i=0;i<terms.size();i++) {
			sql.append("?,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(')');
		PreparedStatement ps = DBUtility.getPreparedStatement(sql.toString());
		try {
			for (int i=0;i<terms.size();i++) {
				ps.setString(i + 1, terms.get(i));
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
	
	public static void main(String[]args) {
//		ArrayList<String> a = new ArrayList<String>();
//		a.add("clueweb09-en0000-23-00194");
//		a.add("clueweb09-en0000-43-16967");
//		ArrayList<String> res = new DBReader().getRelatedDocs(a);
//		for (String s : res) {
//			System.out.println(s);
//		}
//		System.out.println(new DBReader().getIdf("contact"));
//		System.out.println(new DBReader().getPstd("contact", "clueweb09-en0000-43-16967"));
//		System.out.println(new DBReader().getPustd("contact", "clueweb09-en0000-43-16967"));
//
		ArrayList<String> docs = new ArrayList<String>(){};
		docs.add("ebola-b29528c05c1aea343c24dd741c2f123be551ae116e4e05724d0ff3c2939879c0");
		docs.add("ebola-2c84680fde53cb27820a387b222fbf0f9edfbcf53f2f1bb44ccc4317e0dc6c32");
		docs.add("ebola-c86b59636d85765b9981283913a7dd89a8e16ad3c76b97a6fa74c522b6122ef0");
		docs.add("ebola-3c3a76e6df5b5834e844f92824822de186397b5a5432600248ffb3ca0ce15182");
		docs.add("ebola-b59ef332171ab2fb13c4c6978157b7356e8825638c41e6ecb2657b25b96d9a4e");
		Map<String,Double> results = new DBReader().getPustds("date",docs);

		for (String key: results.keySet()){
			System.out.println(key+":"+results.get(key));
		}
	}

	/* (non-Javadoc)
	 * @see db.DBReadService#getPaths(java.util.ArrayList)
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
	 * @see db.DBReadService#initialize(java.lang.String)
	 */
	public void initialize(ArrayList<String> query) {
		// TODO Auto-generated method stub
	}

}
