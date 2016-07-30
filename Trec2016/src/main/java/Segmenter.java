import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 *
 * @author 霄汉
 * @since 2016年4月13日 下午9:26:08
 * @version 1.0
 */
public class Segmenter {
	
	public static ArrayList<String> segment(String s) {
		
		StringReader reader = new StringReader(s);
		StandardAnalyzer analyzer = new StandardAnalyzer();
		ArrayList<String> res = new ArrayList<String>();

		try {
			TokenStream tokenStream = analyzer.tokenStream("content", reader);
			tokenStream.addAttribute(CharTermAttribute.class);
			tokenStream.reset();//必须先调用reset方法，否则会报下面的错，可以参考TokenStream的API说明
		
			while (tokenStream.incrementToken()) {
				CharTermAttribute charTermAttribute = (CharTermAttribute) tokenStream.getAttribute(CharTermAttribute.class);
				res.add(charTermAttribute.toString());
			}
			
			tokenStream.end();
			tokenStream.close();
			analyzer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

}
