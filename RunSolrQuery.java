package com.wk.tool.solr;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Created by Pavel Chumakou on 2/5/2025.
 */
public class RunSolrQuery {

    private record Result(int queryTime, int numFound) {
    }

    private static Result runSolrQuery(String url) throws Exception {
        int start;
        int end;

        URL u = new URI(url).toURL();
        InputStream is = u.openStream();
        DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
        int queryTime = -1;
        int numFound = -1;
        String s;
        while ((s = dis.readLine()) != null)
        {
            if (s.contains("QTime")) {
                start = s.indexOf("QTime");
                end = s.indexOf("}", start);
                if (end < 0) {
                    end = s.indexOf(",", start);
                }
                String qTime = s.substring(start + 7, end);
                queryTime = Integer.parseInt(qTime);
            }
            if (s.contains("numFound\"")) {
                start = s.indexOf("numFound");
                end = s.indexOf(",", start);
                String nFound = s.substring(start + 10, end);
                numFound = Integer.parseInt(nFound);
            }
        }
        return new Result(queryTime, numFound);
    }

    public static void main(String[] args) throws Exception {
        String baseUrl = "http://localhost:8983/solr/wikitest/select?q.op=OR&wt=json&q=";
        if (args.length == 1) {
            baseUrl = args[0];
        }

        String queryData = "Anna is a 1983 play devised by a collective of women based out of Toronto theatre company Nightwood Theatre Initially developed as a 20 minute production for the Women s Perspective Festival This is For You Anna was re-written for longer performances and went on to tour Canada and Britain throughout the 80 s The play was created collectively in response to a current event of the time wherein German woman Marianne Bachmeier walked into a courtroom and shot the man who killed her daughter The feminist play explores themes of violence revenge domesticity and questions the roles of western women at the end of the 20th century Collective creation This is For You Anna was developed through the process of collective creation This medium operates from a foundation of performers own ideas and experiences and the final production is largely devised within the rehearsal process itself This differs from more traditional means of theatre creation in which the script is written independently of the rehearsal process the cast are aware of their roles before the process begins and the actors have less of a say in the content of the piece Additionally many collective creation plays are characterized by an episodic nonlinear narrative actors playing multiple characters within the narrative and a collaborative creative process which often explores the creators own interests The qualities of collective creation are evident in This is For You Anna as it rejects a linear chronology each actor plays a number of roles (including each of the four actors portraying a different incarnation of Marianne Bachmeier herself) and the original cast of actors were also intimately involved in the creation of the text The script This is For You Anna is a one-act play composed of eight scenes which flow together thematically though are not necessarily connected chronologically The play makes use of a lyrical poetic style and live accordion music is used to accompany frequent musical numbers which help to tell the story This music does not work to create a traditionally polished musical theatre aesthetic Rather the music and storytelling create an ethereal experimental folk atmosphere characteristic of the early days of Nightwood Theatre The most frequently recurring character in the play is Marianne Bachmeier who is played by each of the four actors throughout the play This is indicated in the script as characters named Marianne 1 2 3 and 4 Plot summary The play opens on all four actors as different embodiments of Marianne each in their own worlds They reflect on the act of walking into the courtroom before the shooting The scene transitions into that of a mother telling her daughter a bedtime story in which a woman is tortured by a wealthy baron she loves The daughter reimagines the ending of the story to include the woman getting revenge on the man who tortured her and the accordionist turns this new ending into a song Scene three begins with a narrator providing backstory on Marianne s life and a brief overview of the event which inspired the play All four actors then embody Marianne at different points of her life having conversations with various other absent characters such as Anna (her daughter who was killed) her ex-partner and an interviewer visiting her in prison In scene four the actors tell a paraphrased version of the Roman legend of Lucretia which is reinterpreted to focus on the parallels between Marianne s own story and the legend itself: namely victim blaming and a desire for revenge In scene five the four actors take on characters which speak to their own experiences with abuse and violence They perform regimented choreography and talk amongst themselves; engaging in dialogue which communicates nuanced perspectives on the complications of living with and leaving abusive partners Scene six deals with media portrayals of victims and positions these as potentially problematic In this scene one of the actors takes on an interviewer role grills the other three with personal questions and demands them to perform certain acts on command In scene seven all actors take on the role of Marianne once more and delve into her intimate thoughts and feelings surrounding the courtroom shooting The play concludes with The Jury Scene in which the actors take on the role of the jury which found Marianne guilty of murdering the man who murdered her daughter The characters in this scene condemn Marianne for her actions and defend the man s point of view (who argued in court that Marianne s seven year old daughter had flirted with him which led him to commit the murder) These perspectives are written in small snippets to be spoken rapidly by the cast in order to create an overwhelming crescendo for the audience This shifts abruptly when the actors all resume their roles as Mariannes 1 through 4 and quietly support and affirm their actions to each other The play ends External links";

        String[] words = queryData.toLowerCase().split(" ");
        System.out.print("Search time, ms: ");
        for (int c = 0; c < 20; c++) {
            long searchTime = 0;
            for (int i = 0; i < words.length / 3; i++) {
                String q = words[i * 3] + "%20" + words[i * 3 + 1] + "%20" + words[i * 3 + 2];
                Result result = runSolrQuery(baseUrl + q);
                searchTime += result.queryTime;
            }
            System.out.print(searchTime + " ");
        }
        System.out.println();
    }
}