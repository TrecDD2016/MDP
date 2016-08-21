package query;

import org.apache.commons.io.IOUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by cat on 16/8/21.
 */
public class FrequencyWords {


    public static void mostFrequentWords(String sentence, final Map<String,Integer> countMap) throws ParserConfigurationException, SAXException, IOException {
//        Pattern pattern = Pattern.compile("(?<=>)*(?=</)");

        String str = "<doc>"+sentence+"</doc>";
        InputStream in = IOUtils.toInputStream(str,"UTF-8");

        DefaultHandler handler = new DefaultHandler(){

            boolean inElement = false;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (!qName.equals("doc")) {
                    inElement = true;
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (!qName.equals("doc")) {
                    inElement = false;
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                if (inElement){
                    String word = new String(ch, start, length).trim();

                    int count = countMap.containsKey(word) ? countMap.get(word) : 0;
                    countMap.put(word, count + 1);
                }
            }
        };


        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(in, handler);
    }

    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            @Override
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return ( o1.getValue() ).compareTo( o2.getValue() );
            }
        } );

        return list;
    }
}
