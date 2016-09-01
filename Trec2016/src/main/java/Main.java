import db.DBReader3;
import db.DBUtility;
import db.DBWriter;
import db.DBWriter2;
import org.bson.Document;
import query.Doc2Mysql;
import query.ExtractQuery;
import search.PustdUpdater;
import search.SearchSession;

import java.util.*;


/**
 * Created by cat on 16/8/21.
 */
public class Main {

    private static DBReader3 dbReader3 = new DBReader3();

    private static int ITER_NUM = 6;

    private static String FEEDBACK_PATH = "/backup/dd_trec/jig/trec-dd-jig-master/DirectIndriMethod/DD16-1result.txt";

    private static final String[] D_PATHS = {"/backup/dd_trec/data/EbolaDataXML",
            "/backup/dd_trec/data/PolarDataXML"

    };




    public static void main(String args[]) throws Exception {

        Scanner scanner = new Scanner(System.in);
        SearchSession searchSession = new SearchSession();


        //        import doc to mysql
//        Doc2Mysql.startImport(D_PATHS);

//        DBWriter2.initializeDatabase(D_PATHS,D_PATHS);

        //        costly
//        DBWriter.initializeDatabase();
//        DBWriter.backup();




        int iterIndex = 0;


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
                }
                System.out.println("\n==============");
            }

            iterIndex++;


            System.out.print("Iter "+iterIndex+", If continue:");

            scanner.nextLine();

            //TODO update ifStop, if need continue, update the feedback content and press 'Y'
            if (iterIndex == ITER_NUM)
                ifStop = true;
        }



    }
}
