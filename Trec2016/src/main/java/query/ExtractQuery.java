package query;


import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;


import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by cat on 16/8/21.
 */
public class ExtractQuery {

    private static String serializedClassifier = "/Volumes/HDD/link/adventure/项目/dd-trec/stanford_ner_demo/stanford-ner-2015-12-09/classifiers/english.all.3class.distsim.crf.ser.gz";

    public static String extract(String feedbackPath) throws Exception {

//        String feedback_path = "/Volumes/HDD/link/adventure/项目/dd-trec/feedback";

        final Map<String, Integer> countMap = new HashMap();

        AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

        System.out.println("==========");

        File file = new File(feedbackPath);
        String sentence = FileUtils.readFileToString(file, "utf-8");
        String sentence_with_label = classifier.classifyWithInlineXML(sentence);

        FrequencyWords.mostFrequentWords(sentence_with_label, countMap);

        List<Map.Entry<String, Integer>> sorted_list = FrequencyWords.sortByValue(countMap);

        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < 2 && i < sorted_list.size()) {
            sb.append(sorted_list.get(i).getKey()+" ");
            i++;
        }

        return sb.toString();
    }
}
