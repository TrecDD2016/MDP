package db;

import util.Constants;
import util.Segmenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DBWriter {

    private static int INSERT_PER_BATCH = 10000;

    private static HashMap<String, Integer> cMap = new HashMap<String, Integer>();    //语料库中词和次数

    private static HashMap<String, Integer> numDocsContainsTerm = new HashMap<String, Integer>();    //全部文档中包含某一词的文档个数

    private static int cLength = 0;

    private static DBReader3 dbService = new DBReader3();

    private static long BATCH_SIZE = 10000;

    public static void initializeDatabase() {

        DBUtility.executeTruncate();

            mysqlScanCorpus();

            System.out.println("Corpus Initialized!");

            mysqlScanDocs();
            System.out.println("Docs Initialized!");
    }


    public static void backup() {
        DBUtility.executeConsoleCommand("mysqldump -uroot -p123456 mdp pustd  > pustd_bak.sql");
        DBUtility.executeConsoleCommand("mysqldump -uroot -p123456 mdp pstd  > pstd_bak.sql");
    }


    public static void restore() {
        DBUtility.executeConsoleCommand("mysql -uroot  mdp < pustd_bak.sql");
        DBUtility.executeConsoleCommand("mysql -uroot  mdp < pstd_bak.sql");
    }

    public static void mysqlScanCorpus() {
        int i = 0;

        Map<String, String> docMap;

        while (true) {
            docMap = dbService.getDocs((i++)*BATCH_SIZE, BATCH_SIZE);
            for (Entry<String, String> entry : docMap.entrySet()) {
                String content = entry.getValue();
                updateCMap(content);
            }
            if (docMap.entrySet().size() == 0) break;
        }
    }

    private static void updateCMap(String content) {
        ArrayList<String> ss = Segmenter.segment(content);
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

    private static int updateWordCountMap(HashMap<String, Integer> map, String content) {

        int wordNum = 0;
        ArrayList<String> ss = Segmenter.segment(content);
        for (String word : ss) {

//                        把所有的non-printable字符删除了，否则数据库会把包含非可见字符的键当成相同主键而拒绝写入
//                        therefore, 需要检查删除后的字符串是否为空
            word = word.replaceAll("\\p{C}", "");
            if (word.length() > 0) {
                if (map.containsKey(word)) {
                    map.put(word, map.get(word) + 1);
                } else {
                    map.put(word, 1);
                }
                ++wordNum;
            }
        }

        return wordNum;
    }

    public static void mysqlScanDocs() {
        int i = 0;

        Map<String, String> docMap;
        HashMap<String, Integer> wordCountMap = new HashMap<String, Integer>();
        int docLength = 0;

        while (true) {
            docMap = dbService.getDocs((i++) * BATCH_SIZE, BATCH_SIZE);
            for (Entry<String, String> entry : docMap.entrySet()) {
                // reset map and length
                wordCountMap.clear();
                docLength = 0;

                String docName = entry.getKey();

                String content = entry.getValue();

                docLength += updateWordCountMap(wordCountMap, content);

                updatePstdAndPustd(wordCountMap, docLength, docName);
            }

            if (docMap.entrySet().size() == 0) break;
        }
    }


    private static void updatePstdAndPustd(HashMap<String, Integer> map, long docLength, String docName) {
        for (Entry<String, Integer> e : map.entrySet()) {
            String s = e.getKey();
            if (numDocsContainsTerm.containsKey(s)) {
                numDocsContainsTerm.put(s, numDocsContainsTerm.get(s) + 1);
            } else {
                numDocsContainsTerm.put(s, 1);
            }
        }

        HashMap<String, Double> pstdMap = new HashMap<String, Double>();
        for (Entry<String, Integer> e : map.entrySet()) {
            String s = e.getKey();
            double pstd = (map.get(s) + Constants.u * cMap.get(s) / (double) cLength) / (Constants.u + docLength);
            pstdMap.put(s, pstd);
        }
        writePstd(docName, pstdMap);


        HashMap<String, Double> pustdMap = new HashMap<String, Double>();
        for (Entry<String, Integer> e : map.entrySet()) {
            String s = e.getKey();
            double pustd = map.get(s) / (double) docLength;
            pustdMap.put(s, pustd);
        }
        writePustd(docName, pustdMap);
    }

    private static void writePstd(String doc, HashMap<String, Double> m) {
        StringBuffer sb = new StringBuffer();
        int numInBatch = 1;        //当前行是在本批中的第几个

        for (Entry<String, Double> e : m.entrySet()) {
            String word = e.getKey().replaceAll("'", "''");

            if (numInBatch == 1) {
                sb.append("insert into pstd(term, doc, pstd) values('");
                sb.append(word).append("','").append(doc).append("',").append(e.getValue()).append(")");
                numInBatch++;
            } else if (numInBatch > INSERT_PER_BATCH) {
                DBUtility.executeInsert(sb.toString());
                sb.setLength(0);
                numInBatch = 1;
            } else {
                sb.append(",('").append(word).append("','").append(doc).append("',").append(e.getValue()).append(")");
                numInBatch++;
            }
        }

        if (sb.length() != 0) {
            DBUtility.executeInsert(sb.toString());
        }
    }

    public static void updatePustdByTerm(String t, HashMap<String, Double> m) {
        StringBuffer sb = new StringBuffer();
        int numInBatch = 1;        //当前行是在本批中的第几个

        for (Entry<String, Double> e : m.entrySet()) {
            String doc = e.getKey().replaceAll("'", "''");
            if (numInBatch == 1) {
                sb.append("insert into pustd(term, doc, pustd) values('");
                sb.append(t).append("','").append(doc).append("',").append(e.getValue()).append(")");
                numInBatch++;
            } else if (numInBatch > INSERT_PER_BATCH) {
                DBUtility.executeInsert(sb.toString());
                sb.setLength(0);
                numInBatch = 1;
            } else {
                sb.append(",('").append(t).append("','").append(doc).append("',").append(e.getValue()).append(")");
                numInBatch++;
            }
        }

        sb.append(" ON DUPLICATE KEY UPDATE pustd=VALUES(pustd)");

        if (sb.length() != 0) {
            DBUtility.executeInsert(sb.toString());
        }
    }

    public static void writePustd(String doc, HashMap<String, Double> m) {
        StringBuffer sb = new StringBuffer();
        int numInBatch = 1;        //当前行是在本批中的第几个

        for (Entry<String, Double> e : m.entrySet()) {
            String word = e.getKey().replaceAll("'", "''");

            if (numInBatch == 1) {
                sb.append("insert into pustd(term, doc, pustd) values('");
                sb.append(word).append("','").append(doc).append("',").append(e.getValue()).append(")");
                numInBatch++;
            } else if (numInBatch > INSERT_PER_BATCH) {
                DBUtility.executeInsert(sb.toString());
                sb.setLength(0);
                numInBatch = 1;
            } else {
                sb.append(",('").append(word).append("','").append(doc).append("',").append(e.getValue()).append(")");
                numInBatch++;
            }
        }

        if (sb.length() != 0) {
            DBUtility.executeInsert(sb.toString());
        }
    }

    public static void main(String[] args) {
        DBWriter.initializeDatabase();
    }
}
