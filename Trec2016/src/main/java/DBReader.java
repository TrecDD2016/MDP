import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @since 2016年4月18日 下午8:29:15
 * @version 1.0
 */
public class DBReader implements DBReadService{

	/* (non-Javadoc)
	 * @see DBReadService#getIdf(java.lang.String)
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
	 * @see DBReadService#getPstd(java.lang.String, java.lang.String)
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
	 * @see DBReadService#getPustd(java.lang.String, java.lang.String)
	 */
	public double getPustd(String t, String doc) {
		PreparedStatement ps = DBUtility.getPreparedStatement("select pustd from pustd where term=? and doc=?");
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
	 * @see DBReadService#getRelatedDocs(java.util.ArrayList)
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
		ArrayList<String> a = new ArrayList<String>();
		a.add("clueweb09-en0000-23-00194");
		a.add("clueweb09-en0000-43-16967");
		ArrayList<String> res = new DBReader().getRelatedDocs(a);
		for (String s : res) {
			System.out.println(s);
		}
		System.out.println(new DBReader().getIdf("contact"));
		System.out.println(new DBReader().getPstd("contact", "clueweb09-en0000-43-16967"));
		System.out.println(new DBReader().getPustd("contact", "clueweb09-en0000-43-16967"));
	}

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
		// TODO Auto-generated method stub
	}

}
