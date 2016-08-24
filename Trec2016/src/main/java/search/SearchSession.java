package search;

import db.DBReadService;
import db.DBReader3;
import util.Segmenter;

import java.util.ArrayList;
import java.util.Collections;

public class SearchSession {

    private Document lastBestDoc = null;
    private ArrayList<String> lastQuery = null;
    private ArrayList<Document> docList = new ArrayList<Document>(); //每次查询返回的第一个文档
    private ArrayList<ArrayList<String>> queryList = new ArrayList<ArrayList<String>>();//每次的查询语句
    private ArrayList<Boolean> invalidList = new ArrayList<Boolean>();//查询语句是否是在重复的范围内
    private double alpha=2.15;
    private double beta=1.75;
    private double epsilon=0.07;
    private double delta=0.42;
    private double garma=0.8;//折扣因子

    private DBReadService dbService = new DBReader3();


//TODO update the doc list in the first query done by indiri
    public void firstQueryUpdate(String query, String doc){
        ArrayList<String> terms= Segmenter.segment(query);
        this.queryList.add(terms);
        this.invalidList.add(new Boolean(Boolean.TRUE));

        dbService.initialize(terms);	//TODO 缓存
        ArrayList<String> docList=dbService.getRelatedDocs(terms);

        double rel=this.calRelevanceRetweenSessionAndDoc(doc);
        this.docList.add(new Document(doc, rel));
    }



    /**
     *
     * @param query
     * @return 排序好的前10个doc
     */
    public ArrayList<String> getSearchResults(String query) {

        ArrayList<Document> docValueList=new ArrayList<Document>();
        ArrayList<String> terms= Segmenter.segment(query);
        dbService.initialize(terms);	//TODO 缓存
        ArrayList<String> docList=dbService.getRelatedDocs(terms);
        if(docList==null||docList.size()==0){ //无结果情况
            return null;
        }
        ArrayList<String> resultDocList=new ArrayList<String>();
        findRepeatQuery(terms);
        this.queryList.add(terms);
        this.invalidList.add(new Boolean(Boolean.TRUE));

        for(int i=0;i<docList.size();i++){
            double rel=this.calRelevanceRetweenSessionAndDoc(docList.get(i));
            if(docValueList.size()<10){
                docValueList.add(new Document(docList.get(i),rel));
                Collections.sort(docValueList);
            }else{
                if(rel>docValueList.get(0).value){
                    docValueList.remove(0);
                    docValueList.add(new Document(docList.get(i),rel));
                    Collections.sort(docValueList);
                }
            }
        }

        this.docList.add(docValueList.get(docValueList.size() - 1));	//TODO 这里原来是size


        for(int i=0;i<docValueList.size();i++){
            resultDocList.add(docValueList.get(i).fileName);
        }

        return resultDocList;
    }


    private double calRelevanceRetweenSessionAndDoc(String doc){
        double result=0;
        for(int i=0;i<queryList.size();i++){
            if(invalidList.get(i)==false){
                continue;
            }else{
                double rel=calRelevanceBetweenQueryAndDoc(queryList.get(i),doc,i+1);
                result+=rel*Math.pow(garma,queryList.size()-(i+1));
            }

        }
        return result;
    }

    private double calRelevanceBetweenQueryAndDoc(ArrayList<String> query,String doc,int index){
        double result=1;
        for(int i=0;i<query.size();i++){
            result=result*(1-dbService.getPstd(query.get(i), doc));
        }
        result=Math.log(1-result);


        if(index!=1){
            lastQuery=queryList.get(index-2);
            lastBestDoc=docList.get(index-2);
            ArrayList<Integer> indexList=this.getQThemeAndOtherIndex(lastQuery, query);
            for(int i=indexList.get(0);i<indexList.get(0)+indexList.get(2);i++){  //theme
                String tempTerm=lastQuery.get(i);
                double pustdi_1=dbService.getPustd(tempTerm, lastBestDoc.fileName);
                double pustd=dbService.getPustd(tempTerm, doc);
                result += alpha*(1-pustdi_1)*Math.log(pustd);
            }

            for(int i=0;i<query.size();i++){
                if(i<indexList.get(1)||i>=(indexList.get(1)+indexList.get(2))){ //+delta
                    String tempTerm=query.get(i);
                    double pustd=dbService.getPustd(tempTerm, doc);
                    if(dbService.getPustd(tempTerm,lastBestDoc.fileName)!=0){ //t属于d*
                        double pustdi_1=dbService.getPustd(tempTerm, lastBestDoc.fileName);
                        result -= beta*pustdi_1*Math.log(pustd);
                    }else{ //t不属于d*
                        double idf=dbService.getIdf(tempTerm);
                        result += epsilon*idf*pustd;
                    }
                }
            }

            for(int i=0;i<lastQuery.size();i++){
                if(i<indexList.get(0)||i>=(indexList.get(0)+indexList.get(2))){ //-delta
                    String tempTerm=lastQuery.get(i);
                    double pustdi_1=dbService.getPustd(tempTerm, lastBestDoc.fileName);
                    double pustd=dbService.getPustd(tempTerm, doc);
                    result-= delta*pustdi_1*Math.log(pustd);
                }
            }


        }

        return result;
    }

    /**
     *
     * @param str1 前一个语句
     * @param str2 后一个语句
     * @return 第一个为theme在str1的第一个位置，第二个为theme在str2的第一个位置，第三个为theme的长度。
     */
    private ArrayList<Integer> getQThemeAndOtherIndex(ArrayList<String> str1,ArrayList<String> str2){
        int i, j;
        int len1, len2;
        len1 = str1.size();
        len2 = str2.size();
        int maxLen = len1 > len2 ? len1 : len2;
        int[] max = new int[maxLen];
        int[] maxIndex = new int[maxLen]; //str1中最大子串最后一个index
        int[] maxIndex2 = new int[maxLen];//str2中最大子串最后一个index
        int[] c = new int[maxLen]; // 记录对角线上的相等值的个数

        max[0]=0;
        for (i = 0; i < len2; i++) {
            for (j = len1 - 1; j >= 0; j--) {
                if (str2.get(i) .equals(str1.get(j))) {
                    if ((i == 0) || (j == 0))
                        c[j] = 1;
                    else
                        c[j] = c[j - 1] + 1;
                } else {
                    c[j] = 0;
                }

                if (c[j] > max[0]) { // 如果是大于那暂时只有一个是最长的,而且要把后面的清0;
                    max[0] = c[j]; // 记录对角线元素的最大值，之后在遍历时用作提取子串的长度
                    maxIndex[0] = j; // 记录对角线元素最大值的位置
                    maxIndex2[0] = i;

                    for (int k = 1; k < maxLen; k++) {
                        max[k] = 0;
                        maxIndex[k] = 0;
                        maxIndex2[k] = 0;
                    }
                } else if (c[j] == max[0]) { // 有多个是相同长度的子串
                    for (int k = 1; k < maxLen; k++) {
                        if (max[k] == 0) {
                            max[k] = c[j];
                            maxIndex[k] = j;
                            maxIndex2[k] = i;
                            break; // 在后面加一个就要退出循环了
                        }

                    }
                }
            }
        }


        ArrayList<Integer> resultList=new ArrayList<Integer>();
        if(max[0]!=0){
            resultList.add(maxIndex[0]-max[0]+1);
            resultList.add(maxIndex2[0]-max[0]+1);
            resultList.add(max[0]);
        }else{
            resultList.add(-1);
            resultList.add(-1);
            resultList.add(0);
        }
        return resultList;
    }

//TODO 存疑,是不是这样的?

    public String lastBestDoc(){
        return this.docList.get(this.docList.size()-1).fileName;
    }

    private void findRepeatQuery(ArrayList<String> query){
        for(int i=0;i<queryList.size();i++){
            if(queryEquals(queryList.get(i),query)){
                for(int j=i;j<invalidList.size();j++){
                    invalidList.remove(j);
                    invalidList.add(j, new Boolean(Boolean.FALSE));
                }
                return;
            }
        }
    }

    private boolean queryEquals(ArrayList<String> a,ArrayList<String> b){
        if(a.size()!=b.size()){
            return false;
        }else{
            for(int i=0;i<a.size();i++){
                if(a.get(i).equalsIgnoreCase(b.get(i))){
                    continue;
                }else{
                    return false;
                }
            }
        }
        return true;
    }
}
