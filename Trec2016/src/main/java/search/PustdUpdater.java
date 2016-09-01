package search;

import db.DBReader;
import db.DBReader2;
import db.DBWriter;
import query.Doc2Mysql;

import java.util.*;

/**
 * Created by cat on 16/8/22.
 */
public class PustdUpdater {

    private static DBReader dbReader = new DBReader();

    public static void update(Set<String> DPre,Set<String> qPre, Set<String> qNow, SearchSession searchSession){

        Set<String> qTheme = new HashSet<String>();
        for (String q: qPre){
            if (qNow.contains(q)){
                qTheme.add(q);
            }
        }

        Set<String> qRemoved = new TreeSet<String>(qPre);
        qRemoved.removeAll(qTheme);

        Set<String> qAdded = new TreeSet<String>(qNow);
        qAdded.removeAll(qTheme);

        Set<String> qAll = new TreeSet<String>(qPre);
        qAll.addAll(qNow);

        ArrayList<String> termList = new ArrayList<String>(1);

        String lastBestDoc = searchSession.lastBestDoc();

        for (String t: qAll){
            termList.set(0,t);
            HashMap<String, Double> pustdMap = new HashMap<String, Double>(1);
            ArrayList<String> docs = dbReader.getRelatedDocs(termList);

            if (!DPre.contains(t) && qRemoved.contains(t)){
                continue;
            }else if(DPre.contains(t) && qRemoved.contains(t) && qAdded.contains(t)){

                for (String d: docs) {
                    double pustdOld = dbReader.getPustd(t, d);

                    double pustdLastBest = dbReader.getPustd(t, lastBestDoc);

                    double pustdNew = Math.exp(1-pustdLastBest)*pustdOld;

                    pustdMap.put(t,pustdNew);
                }

            }else if(!DPre.contains(t) && qAdded.contains(t)){
                double idfT = dbReader.getIdf(t);

                for (String d: docs) {
                    double pustdOld = dbReader.getPustd(t, d);

                    double pustdNew = Math.exp(1 + idfT) * pustdOld;

                    pustdMap.put(t, pustdNew);
                }

            }else if(qTheme.contains(t)){

                for (String d: docs) {
                    double pustdOld = dbReader.getPustd(t, d);

                    double pustdLastBest = dbReader.getPustd(t, lastBestDoc);

                    double pustdNew = Math.exp(1+(1-pustdLastBest))*pustdOld;

                    pustdMap.put(t, pustdNew);
                }

            }

            DBWriter.updatePustdByTerm(t, pustdMap);
        }
    }
}
