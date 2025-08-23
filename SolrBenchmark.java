import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Pavel Chumakou on 2/5/2025.
 */
public class SolrBenchmark {

    private static final String COLLECTION = "wikitest";
    private static String testDocsPath = "./testdata/wikitest-docs-1000.json";
    private static String baseUrl = "http://localhost:8983/solr/wikitest/select?q.op=OR&wt=json&defType=edismax&qf=title0_t%20title1_t%20title2_t%20body0_t%20body1_t%20body2_t&fl=id&q=%7B!cache=false%7D";

    private static void commit() {
        try {
            String url = "http://localhost:8983/solr/" + COLLECTION + "/update?commit=true";
            URL u = new URI(url).toURL();
            u.openStream();
        } catch (Exception e) {
            System.out.println("Commit error: " + e.getMessage());
        }
    }

    private static void addDocuments() {
        try {
            String urlString = "http://localhost:8983/solr/" + COLLECTION + "/update";
            URL url = new URI(urlString).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                Path dataPath = Paths.get(testDocsPath);
                byte[] docs = Files.readAllBytes(dataPath);
                os.write(docs, 0, docs.length);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            commit();
            int addDocsTime = getQTime(br);
            System.out.println("Refeed time, ms: " + addDocsTime);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static int getQTime(BufferedReader br) throws Exception {
        int start;
        int end;
        int queryTime = -1;
        String s;
        while ((s = br.readLine()) != null)
        {
            if (s.contains("QTime")) {
                start = s.indexOf("QTime");
                end = s.indexOf("}", start);
                if (end < 0) {
                    end = s.indexOf(",", start);
                    if (end < 0) {
                        end = s.length();
                    }
                }
                String qTime = s.substring(start + 7, end);
                queryTime = Integer.parseInt(qTime);
                return queryTime;
            }
        }
        return -1;
    }

    private static int runTestQuery(String url) throws Exception {
        URL u = new URI(url).toURL();
        InputStream is = u.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        return getQTime(br);
    }

    private static void runTestQueries() throws Exception {
        String queryData = "anna is a 1983 play devised by collective of women based out toronto theatre company nightwood initially developed " +
                "as 20 minute production for the s perspective festival this you was re-written longer performances and went on to tour canada " +
                "britain throughout 80 created collectively in response current event time wherein german woman marianne bachmeier walked into " +
                "courtroom shot man who killed her daughter feminist explores themes violence revenge domesticity questions roles western at end " +
                "20th century creation through process medium operates from foundation performers own ideas experiences final largely within " +
                "rehearsal itself differs more traditional means which script written independently cast are aware their before begins actors " +
                "have less say content piece additionally many plays characterized an episodic nonlinear narrative playing multiple characters " +
                "collaborative creative often creators interests qualities evident it rejects linear chronology each actor number (including " +
                "four portraying different incarnation herself) original were also intimately involved text one-act composed eight scenes flow " +
                "together thematically though not necessarily connected chronologically makes use lyrical poetic style live accordion music " +
                "used accompany frequent musical numbers help tell story does work create traditionally polished aesthetic rather storytelling " +
                "ethereal experimental folk atmosphere characteristic early days most frequently recurring character played indicated named 1 " +
                "2 3 4 plot summary opens all embodiments worlds they reflect act walking shooting scene transitions that mother telling bedtime " +
                "tortured wealthy baron she loves reimagines ending include getting accordionist turns new song three with narrator providing " +
                "backstory life brief overview inspired then embody points having conversations various other absent such (her killed) " +
                "ex-partner interviewer visiting prison paraphrased version roman legend lucretia reinterpreted focus parallels between namely " +
                "victim blaming desire five take speak abuse perform regimented choreography talk amongst themselves engaging dialogue " +
                "communicates nuanced perspectives complications living leaving abusive partners six deals media portrayals victims positions " +
                "these potentially problematic one takes role grills personal demands them certain acts command seven once delve intimate " +
                "thoughts feelings surrounding concludes jury found guilty murdering murdered condemn actions defend point view (who argued " +
                "court year old had flirted him led commit murder) small snippets be spoken rapidly order overwhelming crescendo audience " +
                "shifts abruptly when resume mariannes quietly support affirm ends external links";

        String[] words = queryData.toLowerCase().split(" ");
        System.out.print("Search time, ms: ");
        for (int c = 0; c < 10; c++) {
            long searchTime = 0;
            for (int i = 0; i < words.length / 3; i++) {
                String q = words[i * 3] + "%20" + words[i * 3 + 1] + "%20" + words[i * 3 + 2];
                int result = runTestQuery(baseUrl + q);
                searchTime += result;
            }
            System.out.print(searchTime + " ");
        }
        System.out.println();
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("java SolrBenchmark.java <OPTIONS>");
        System.out.println("OPTIONS:");
        System.out.println("    -h");
        System.out.println("        Print this message");
        System.out.println("    -u <baseUrl>");
        System.out.println("        Use the specified URL, for example:  http://localhost:8983/solr/wikitest/select?q.op=OR&q=");
        System.out.println("    -d <testDocsPath>");
        System.out.println("        Path to the test data file");
        System.out.println("    -a");
        System.out.println("        Add documents to collection (refeed)");
        System.out.println("    -r");
        System.out.println("        Run test queries");
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            addDocuments();
            runTestQueries();
        }
        if (args.length > 0) {
            int i = 0;
            while (i < args.length) {
                String opt = args[i];
                if (opt.equals("-h")) {
                    printUsage();
                    return;
                }
                if (opt.equals("-a")) {
                    addDocuments();
                }
                if (opt.equals("-r")) {
                    runTestQueries();
                }
                if (opt.equals("-u")) {
                    if (i + 1 < args.length) {
                        baseUrl = args[i + 1];
                        i++;
                    }
                }
                if (opt.equals("-d")) {
                    if (i + 1 < args.length) {
                        testDocsPath = args[i + 1];
                        i++;
                    }
                }
                i++;
            }
        }
    }
}