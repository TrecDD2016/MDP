package query;

import db.DBUtility;
import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Doc2Mysql {
    private static int INSERT_PER_BATCH = 100;

//    id , content
    private static Map<String,String> docMap = new HashMap();

    private static String FILE_SUFFIX = "txt";

    private static DefaultHandler handler = new DefaultHandler(){

        STATUS status = STATUS.other;

        String keyStore = "";

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("DOCNO")) {
                status = STATUS.id;
            }else if(qName.equals("DOCCONTENT")){
                status = STATUS.document;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equals("DOCNO") || qName.equals("DOCCONTENT")) {
                status = STATUS.other;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {

            switch (status){
                case id:
                    keyStore=new String(ch, start, length).trim();
                    docMap.put(keyStore,"");
                    break;
                case document:
                    docMap.put(keyStore, docMap.get(keyStore).concat(new String(ch, start, length)));
                    break;
                default:
                    break;
            }
        }
    };


    private static void writeDocs(Map<String, String> m) {
        StringBuffer sb = new StringBuffer();
        int numInBatch = 1;		//当前行是在本批中的第几个

        for (Map.Entry<String, String> e : m.entrySet()) {
            String id = e.getKey().replaceAll("\'", "''");
            String content = e.getValue().replaceAll("\'"," ");
            if (numInBatch == 1) {
                sb.append("insert into docs(doc_id, content) values('");
                sb.append(id).append("','").append(content).append("')");
                numInBatch++;
            }else if (numInBatch > INSERT_PER_BATCH) {
                DBUtility.executeInsert(sb.toString());
                sb.setLength(0);
                numInBatch = 1;
            }else {
                sb.append(",('").append(id).append("','").append(content).append("')");
                numInBatch++;
            }
        }

        if (sb.length() != 0) {
            DBUtility.executeInsert(sb.toString());
        }
    }

    private static SAXParserFactory factory = SAXParserFactory.newInstance();


    private static SAXParserImpl saxParserImpl;

    static {
        try {
            saxParserImpl = SAXParserImpl.newInstance(null);
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public static void startImport(String[] docDirs){
        DBUtility.executeSetting();
        DBUtility.executeTruncateDocs();

        for (String docDir : docDirs) {
            File cRoot = new File(docDir);
            File[] files = cRoot.listFiles();
            for (File f : files) {

                String fileName = f.getName().substring(0, f.getName().lastIndexOf('.'));

                if (!fileName.trim().equals("")) {
                    mapFile(f);
                    writeDocs(docMap);
                    docMap.clear();
                }
            }
        }
    }


    private static void mapFile(File f){
        if (f.isDirectory()){
            File[] files = f.listFiles();
            for (File file : files) {
                mapFile(file);
            }
        }else if (f.getName().endsWith(FILE_SUFFIX)){
            try {
                InputStream is = new FileInputStream(f);

                saxParserImpl.parse(is,handler);

                is.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
    }
}


enum STATUS {
    id,
    document,
    other
}