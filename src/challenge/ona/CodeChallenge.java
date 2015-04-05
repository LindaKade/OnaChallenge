package challenge.ona;


import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Linda
 */
public class CodeChallenge {

    public Object calculate(String url) throws IOException {

        System.out.println("Calculate: ");

        JSONObject result = new JSONObject();

        int working = 0;
        int not_working = 0;
        ArrayList<String> village_list = new ArrayList<String>();
        Map community_points = new HashMap();
        Map community_ranks = new HashMap();
        Map community_works = new HashMap();
        Map community_no_works = new HashMap();
        try {
            URL ona_json = new URL(url);
            URLConnection urlconn = ona_json.openConnection();
            InputStreamReader in = new InputStreamReader(urlconn.getInputStream());
            String name;
            String villages = "";
            String works;
            int counter;
            try (JsonReader jread = new JsonReader(in)) {
                jread.beginArray();
                while (jread.hasNext()) {
                    jread.beginObject();
                    while (jread.hasNext()) {
                        name = jread.nextName();
                        if (name.equals("communities_villages")) {
                            villages = jread.nextString();
                            village_list.add(villages);
                        } else if (name.equals("water_functioning")) {
                            works = jread.nextString();
                            if (works.equals("yes")) {
                                working++;
                                if (community_works.containsKey(villages)) {
                                    counter = (Integer) community_works.get(villages);
                                    community_works.put(villages, ++counter);
                                } else {
                                    community_works.put(villages, 1);
                                }
                            } else if (works.equals("no")) {
                                not_working++;
                                if (community_no_works.containsKey(villages)) {
                                    counter = (Integer) community_no_works.get(villages);
                                    community_no_works.put(villages, ++counter);
                                } else {
                                    community_no_works.put(villages, 1);
                                }
                            }
                        } else {
                            jread.skipValue();
                        }
                    }
                    jread.endObject();
                }
                jread.endArray();
            }

            Iterator it = village_list.iterator();
            while (it.hasNext()) {
                String unique_village = (String) it.next();
                int freq = Collections.frequency(village_list, unique_village);
                community_points.put(unique_village, freq);
//                if (community_works.containsKey(unique_village)) {
//                    worked = (Integer) community_works.get(unique_village);
//                }
//                if (community_no_works.containsKey(unique_village)) {
//                    not_worked = (Integer) community_works.get(unique_village);
//                }
                int worked = community_works.containsKey(unique_village) ? (int) community_works.get(unique_village) : 0;
                int not_worked = community_no_works.containsKey(unique_village) ? (int) community_no_works.get(unique_village) : 0;
                int village_total = worked + not_worked;
                float percents = (float) not_worked / village_total * 100;
                community_ranks.put(unique_village, percents);
            }
            result.put("number_functional: ", working);
            result.put("number_water_points: ", community_points);
            result.put("community_ranking: ", community_ranks);
        } catch (MalformedURLException | JSONException ex) {
            Logger.getLogger(CodeChallenge.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static void main(String[] args) {
        String ona_url = "https://raw.githubusercontent.com/onaio/ona-tech/master/data/water_points.json";
        CodeChallenge challenge = new CodeChallenge();
        try {
            Object a = challenge.calculate(ona_url);
            System.out.println(a);
        } catch (IOException ex) {
            Logger.getLogger(CodeChallenge.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
