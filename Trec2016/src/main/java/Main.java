import db.DBReader3;
import db.DBUtility;
import db.DBWriter;
import db.DBWriter2;
import query.Doc2Mysql;
import query.ExtractQuery;
import search.PustdUpdater;
import search.SearchSession;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by cat on 16/8/21.
 */
public class Main {

    private static String DOC_DIR = "/Volumes/HDD/link/adventure/项目/dd-trec/EbolaDataXML";

    private static String FEEDBACK_PATH = "/Volumes/HDD/link/adventure/项目/dd-trec/feedback";


    private static DBReader3 dbReader3 = new DBReader3();

    public static void main(String args[]) throws Exception {


        Scanner scanner = new Scanner(System.in);
        SearchSession searchSession = new SearchSession();


//        costly
        DBUtility.executeTruncate();
        DBWriter.initializeDatabase();
//        DBUtility.executeTruncate2();
//        DBWriter2.initializeDatabase();

        //        import doc to mysql
//        DBUtility.executeTruncateDocs();
//        Doc2Mysql.startImport(DOC_DIR);


        int iterIndex = 0;


//        //TODO update the docList and queryList in searchSession

        boolean ifStop = true;

        ArrayList<String> queryList = new ArrayList<String>(1);
        queryList.add("");

        while(ifStop) {

            if (!(iterIndex == 0)) {

//            words of pre result D
                Set<String> Dpre = new TreeSet<String>();
//            words of previous query
                Set<String> qPre = new TreeSet<String>();
//            words of current query
                Set<String> qNow = new TreeSet<String>();

                PustdUpdater.update(Dpre, qPre, qNow, searchSession);
            }

            String newQuery = ExtractQuery.extract(FEEDBACK_PATH);

            queryList.set(0,newQuery);

            dbReader3.initialize(queryList);

            ArrayList<String> results = searchSession.getSearchResults(newQuery);

            if (results!=null) {
                System.out.println("==============");
                System.out.println("result docs are:");
                for (String result : results) {
                    System.out.println("Doc Name: "+result);

                    System.out.println("Doc IDs:");
                    System.out.println("    ******");
                    ArrayList<String> docNos = dbReader3.getDocNos(result);
                    for (String id: docNos){
                        System.out.println("    "+id);
                    }
                    System.out.println("\n    ******");
                }
                System.out.println("\n==============");
            }

            iterIndex++;

            //TODO update ifStop, if need continue, update the feedback content and press 'Y'
            System.out.print("Iter "+iterIndex+", If continue:");
            String next = scanner.nextLine();
            ifStop = next.equals("Y");
        }



    }
}
