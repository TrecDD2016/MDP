import db.DBWriter;
import query.Doc2Mysql;
import query.ExtractQuery;
import search.Document;
import search.PustdUpdater;
import search.SearchSession;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by cat on 16/8/21.
 */
public class Main {
    private static String FEEDBACK_PATH = "/Volumes/HDD/link/adventure/项目/dd-trec/feedback";

    public static void main(String args[]) throws Exception {

        //TODO jiawei's first feedback
        if (args.length < 2){
            System.out.println("miss arg, arg0: query, arg1: best Document Name");
        }

        String query = args[0];

        String bestDoc = args[1];

        SearchSession searchSession = new SearchSession();

//        import doc to mysql
//        Doc2Mysql.startImport(DOC_DIR);

//        initialize values(very costly)
//        DBWriter.initializeDatabase();

        int iterIndex = 0;


        //TODO update the docList and queryList in searchSession

        searchSession.firstQueryUpdate(query,bestDoc);

        iterIndex++;

        boolean ifStop = false;

        while(ifStop) {

            if (!(iterIndex == 1)) {

//            words of pre result D
                Set<String> Dpre = new TreeSet<String>();
//            words of previous query
                Set<String> qPre = new TreeSet<String>();
//            words of current query
                Set<String> qNow = new TreeSet<String>();

                PustdUpdater.update(Dpre, qPre, qNow, searchSession);

                String newQuery = ExtractQuery.extract(FEEDBACK_PATH);

                searchSession.getSearchResults(newQuery);
            }

            //TODO update ifStop, if need continue, update the feedback content and press 'Y'
            Scanner scanner = new Scanner(System.in);
            String next = scanner.nextLine();
            ifStop = next.equals("Y");
        }

    }
}
