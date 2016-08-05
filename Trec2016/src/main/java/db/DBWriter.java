package db;

import util.Constants;
import util.Segmenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class DBWriter {
	
	private static int INSERT_PER_BATCH = 400;
	
	private static final String C_PATH = "E:/人工智能数据/url-header-html-txt";	//语料库路径
	
	private static final String D_PATH = "E:/人工智能数据/url-header-html-txt";	//文档库路径
	
	private static final String CORPUS_FILE_EXT = ".txt";	//语料库文件后缀名
	
	private static final String DOCS_FILE_EXT = ".txt";	//语料库文件后缀名
	
	private static HashMap<String, String> pathMap = new HashMap<String, String>();
	
	private static HashMap<String, Integer> cMap = new HashMap<String, Integer>();	//语料库中词和次数
	
	private static HashMap<String, Integer> numDocsContainsTerm = new HashMap<String, Integer>();	//全部文档中包含某一词的文档个数
	
	private static int cLength = 0;
	
	private static int docsCount = 0;	//全部文档总数
	
	public static void initializeDatabase(){
		
		DBUtility.executeTruncate();
		
		//扫描语料库
		File cRoot = new File(C_PATH);
		scanCorpus(cRoot);
		System.out.println("Corpus Initialized!");
		
		//扫描文档集
		File dRoot = new File(D_PATH);
		scanDocs(dRoot);
		System.out.println("Docs Initialized!");
		
		writePath(pathMap);
		System.out.println("Paths Initialized!");
		
		HashMap<String, Double> idfMap = new HashMap<String, Double>();
		for (Entry<String, Integer> e : numDocsContainsTerm.entrySet()) {
			double idf = Math.log(((double)docsCount) / e.getValue());	//TODO 这个log以多少为底？
			idfMap.put(e.getKey(), idf);
		}
		
		System.out.println(numDocsContainsTerm.size());
		
		writeIdf(idfMap);
		System.out.println("Idfs Initialized!");
		
	}
	
//	public static void main(String[]args) {
//		File doc = new File("E:/人工智能数据/url-header-html-txt/20022/clueweb09-en0000-32-20380.txt");
//		String query = "obama black monolithic";
//		System.out.println(getPqd(query, doc));
//	}
	
//	//计算query和doc的相关程度，详见论文第四节右边
//	public static double getPqd(String query, File doc) {
//		//首先统计doc中每个term出现的次数
//		ArrayList<String> terms = util.Segmenter.segment(query);
//		HashMap<String, Integer> dMap = new HashMap<String, Integer>();	//记录query中每个term出现的次数
//		int length = 0;			//文档长度
//		for (String term : terms) {
//			dMap.put(term, 0);
//		}
//		
//		try {
//			BufferedReader bf = new BufferedReader(new FileReader(doc));
//			String s = null;
//			while ((s = bf.readLine()) != null) {
//				ArrayList<String> ss = util.Segmenter.segment(s);
//				for (String word : ss) {
//					if (dMap.containsKey(word)) {
//						dMap.put(word, dMap.get(word) + 1);
//					}
//					++length;
//				}
//			}
//			bf.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
//		
//		//至此，每个term出现的次数保存在dMap中
//		double product = 1;
//		for (String s : terms) {
//			product *= (1 - (dMap.get(s) + u * cMap.get(s) / (double)cLength)/(u + length));
//		}
//		return 1 - product;
//	}
	
	private static void scanCorpus(File f) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File file : files) {
				scanCorpus(file);
			}
		}else if (f.getName().contains(CORPUS_FILE_EXT)) {
			try {
				InputStreamReader isr = new InputStreamReader(new FileInputStream(f), "UTF-8");
				BufferedReader bf = new BufferedReader(isr);
				String s = null;
				while ((s = bf.readLine()) != null) {
					ArrayList<String> ss = Segmenter.segment(s);
					for (String word : ss) {
//                        把所有的non-printable字符删除了，否则数据库会把包含非可见字符的键当成相同主键而拒绝写入
//                        therefore, 需要检查删除后的字符串是否为空
                        word = word.replaceAll("\\p{C}", "");
                        if (word.length() > 0) {
                            if (cMap.containsKey(word)) {
                                cMap.put(word, cMap.get(word) + 1);
                            } else {
                                cMap.put(word, 1);
                            }
                            ++cLength;
                        }
					}
				}
				bf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}				
		}
	}
	
	private static void scanDocs(File f) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File file : files) {
				scanDocs(file);
			}
		}else if (f.getName().contains(DOCS_FILE_EXT)) {
			HashMap<String, Integer> wordCountMap = new HashMap<String, Integer>();
			int docLength = 0;
			
			try {
				InputStreamReader isr = new InputStreamReader(new FileInputStream(f), "UTF-8");
				BufferedReader bf = new BufferedReader(isr);
				String s = null;
				while ((s = bf.readLine()) != null) {
					ArrayList<String> ss = Segmenter.segment(s);
					for (String word : ss) {

//                        把所有的non-printable字符删除了，否则数据库会把包含非可见字符的键当成相同主键而拒绝写入
//                        therefore, 需要检查删除后的字符串是否为空
                        word = word.replaceAll("\\p{C}", "");
                        if (word.length() > 0) {
                            if (wordCountMap.containsKey(word)) {
                                wordCountMap.put(word, wordCountMap.get(word) + 1);
                            } else {
                                wordCountMap.put(word, 1);
                            }
                            ++docLength;
                        }
					}
				}
				bf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//TODO 去掉扩展名就是文档名吗？
			String docName = f.getName().substring(0, f.getName().lastIndexOf('.'));
			
			pathMap.put(docName, f.getAbsolutePath().replace('\\', '/'));
			
			for (Entry<String, Integer> e : wordCountMap.entrySet()) {
				String s = e.getKey();
				if (numDocsContainsTerm.containsKey(s)) {
					numDocsContainsTerm.put(s, numDocsContainsTerm.get(s) + 1);
				}else {
					numDocsContainsTerm.put(s, 1);
				}
			}
			
			HashMap<String, Double> pstdMap = new HashMap<String, Double>();
			for (Entry<String, Integer> e : wordCountMap.entrySet()) {
				String s = e.getKey();
				double pstd = (wordCountMap.get(s) + Constants.u * cMap.get(s) / (double)cLength) / (Constants.u + docLength);
				pstdMap.put(s, pstd);
			}
			writePstd(docName, pstdMap);
			
			
			HashMap<String, Double> pustdMap = new HashMap<String, Double>();
			for (Entry<String, Integer> e : wordCountMap.entrySet()) {
				String s = e.getKey();
				double pustd = wordCountMap.get(s) / (double)docLength;
				pustdMap.put(s, pustd);
			}
			writePustd(docName, pustdMap);
			
			docsCount ++;
		}		
	}
	

	
	private static void writeIdf(HashMap<String, Double> m) {
		StringBuffer sb = new StringBuffer();
		int numInBatch = 1;		//当前行是在本批中的第几个
		
		for (Entry<String, Double> e : m.entrySet()) {
			String word = e.getKey().replaceAll("'", "''");
				if (numInBatch == 1) {
					sb.append("insert into idf(term, idf) values('");
					sb.append(word).append("',").append(e.getValue()).append(")");
					numInBatch++;
				}else if (numInBatch > INSERT_PER_BATCH) {
					DBUtility.executeInsert(sb.toString());
					sb.setLength(0);
					numInBatch = 1;
				}else {
					sb.append(",('").append(word).append("',").append(e.getValue()).append(")");
					numInBatch++;
				}
		}
		
		if (sb.length() != 0) {
			DBUtility.executeInsert(sb.toString());
		}
	}

	private static void writePath(HashMap<String, String> m) {
		StringBuffer sb = new StringBuffer();
		int numInBatch = 1;		//当前行是在本批中的第几个
		
		for (Entry<String, String> e : m.entrySet()) {
				if (numInBatch == 1) {
					sb.append("insert into path(doc, path) values('");
					sb.append(e.getKey()).append("','").append(e.getValue()).append("')");
					numInBatch++;
				}else if (numInBatch > INSERT_PER_BATCH) {
					DBUtility.executeInsert(sb.toString());
					sb.setLength(0);
					numInBatch = 1;
				}else {
					sb.append(",('").append(e.getKey()).append("','").append(e.getValue()).append("')");
					numInBatch++;
				}
		}
		
		if (sb.length() != 0) {
			DBUtility.executeInsert(sb.toString());
		}
	}

	private static void writePstd(String doc, HashMap<String, Double> m) {
		StringBuffer sb = new StringBuffer();
		int numInBatch = 1;		//当前行是在本批中的第几个
		
		for (Entry<String, Double> e : m.entrySet()) {
			String word = e.getKey().replaceAll("'", "''");
				if (numInBatch == 1) {
					sb.append("insert into pstd(term, doc, pstd) values('");
					sb.append(word).append("','").append(doc).append("',").append(e.getValue()).append(")");
					numInBatch++;
				}else if (numInBatch > INSERT_PER_BATCH) {
					DBUtility.executeInsert(sb.toString());
					sb.setLength(0);
					numInBatch = 1;
				}else {
					sb.append(",('").append(word).append("','").append(doc).append("',").append(e.getValue()).append(")");
					numInBatch++;
				}
		}
		
		if (sb.length() != 0) {
			DBUtility.executeInsert(sb.toString());
		}
	}

	private static void writePustd(String doc, HashMap<String, Double> m) {
		StringBuffer sb = new StringBuffer();
		int numInBatch = 1;		//当前行是在本批中的第几个
		
		for (Entry<String, Double> e : m.entrySet()) {
			String word = e.getKey().replaceAll("'", "''");
				if (numInBatch == 1) {
					sb.append("insert into pustd(term, doc, pustd) values('");
					sb.append(word).append("','").append(doc).append("',").append(e.getValue()).append(")");
					numInBatch++;
				}else if (numInBatch > INSERT_PER_BATCH) {
					DBUtility.executeInsert(sb.toString());
					sb.setLength(0);
					numInBatch = 1;
				}else {
					sb.append(",('").append(word).append("','").append(doc).append("',").append(e.getValue()).append(")");
					numInBatch++;
				}
		}
		
		if (sb.length() != 0) {
			DBUtility.executeInsert(sb.toString());
		}
	}
	
	public static void main(String[]args) {
		DBWriter.initializeDatabase();
	}
}
