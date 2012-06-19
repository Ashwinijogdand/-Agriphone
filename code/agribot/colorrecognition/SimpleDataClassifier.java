package com.agribot.colorrecognition;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.util.Log;

public class SimpleDataClassifier implements Classifier {

    HashMap<Integer, String> map = new HashMap<Integer, String>();

    private void addEntry(String name, int value) {
            map.put(value, name);
    }

    private String name = "no_source";

    public SimpleDataClassifier(AssetManager am, String fn) throws IOException {
            this.name = fn;

            InputStream is = am.open(fn);

            int cnt = 0;
            int failCnt = 0;

            Scanner s = new Scanner(new InputStreamReader(is));
            while (s.hasNextLine()) {
                    String line = s.nextLine();

                    try {
                            String[] tokens = line.split("\t");
                            addEntry(tokens[0], Integer
                                            .parseInt(tokens[1].substring(1), 16));
                            cnt++;
                    } catch (Exception e) {
                            failCnt++;
                    }
            }

            Log.i(AgribotActivity.TAG, "SDClassifier parsed " + cnt + " entries ok, " + failCnt
                            + " lines omitted");
    }

    private long distance(int a, int b) {
            int rd = Color.red(a) - Color.red(b);
            int gd = Color.green(a) - Color.green(b);
            int bd = Color.blue(a) - Color.blue(b);
            return rd * rd + gd * gd + bd * bd;
    }

    public String classify(int value) {
            long best = Long.MAX_VALUE;
            String r = "unclassified";

            for (Integer v : map.keySet()) {
                    long d = distance(value, v);
                    if (d <= best) {
                            best = d;
                            r = map.get(v);
                    }
            }

            return r;
    }

    public String getName() {
            return name;
    }
}
