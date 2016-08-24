import query.Doc2Mysql;
import search.SearchSession;

import java.util.Scanner;

/**
 * Created by cat on 16/8/21.
 */
public class Main {

    private static String DOC_DIR = "/Volumes/HDD/link/adventure/项目/dd-trec/EbolaDataXML";

    private static String FEEDBACK_PATH = "/Volumes/HDD/link/adventure/项目/dd-trec/feedback";

    public static void main(String args[]) throws Exception {


        //TODO jiawei's first feedback
        Scanner scanner = new Scanner(System.in);
        System.out.println("First Query:");
        String query = scanner.nextLine();

        System.out.println("Best Document:");
        String bestDoc = scanner.nextLine();

        SearchSession searchSession = new SearchSession();

//        import doc to mysql
        Doc2Mysql.startImport(DOC_DIR);

//        initialize values(very costly)
//        DBWriter.initializeDatabase();

        int iterIndex = 0;


//        //TODO update the docList and queryList in searchSession
//
//        searchSession.firstQueryUpdate(query,bestDoc);
//
//        iterIndex++;
//
//        boolean ifStop = false;
//
//        while(ifStop) {
//
//            if (!(iterIndex == 1)) {
//
////            words of pre result D
//                Set<String> Dpre = new TreeSet<String>();
////            words of previous query
//                Set<String> qPre = new TreeSet<String>();
////            words of current query
//                Set<String> qNow = new TreeSet<String>();
//
//                PustdUpdater.update(Dpre, qPre, qNow, searchSession);
//
//                String newQuery = ExtractQuery.extract(FEEDBACK_PATH);
//
//                searchSession.getSearchResults(newQuery);
//            }
//
//            //TODO update ifStop, if need continue, update the feedback content and press 'Y'
//            String next = scanner.nextLine();
//            ifStop = next.equals("Y");
//        }

    }
}
