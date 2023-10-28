package org.treinchauffeur.roosterbuilder.io;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.misc.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FileReader {

    private static ArrayList<String> fileContents = new ArrayList<>();
    private static final String TAG = "FileReader";
    public static final int REASON_FAILED_READ = 1, REASON_FAILED_PROCESS = 2;
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    public Context context;
    public MainActivity activity;

    public FileReader(MainActivity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public boolean startReading(Uri uri) {
        boolean success = false;
        int lines = 0;
        fileContents.clear();
        Logger.debug(TAG, "Started reading: "+uri);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


            while (reader.readLine() != null) lines++;
            reader.close();
            assert inputStream != null;
            inputStream.close();
            Logger.debug(TAG, "File has amount of lines: " + lines);

            //Reopen those streams to actually assign the filecontent lines.
            inputStream = context.getContentResolver().openInputStream(uri);

            reader = new BufferedReader(new InputStreamReader(inputStream));

            for (int i = 0; i < lines; i++) {
                fileContents.add(reader.readLine());
            }

            if(fileContents.size() > 9) { //9 is hypothetically the  minimal amount of lines
                if(processData()) {
                    success = true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "startReading: ", e);
            return false;
        }
        return success;
    }

    public boolean processData() {
        boolean success = false;
        ArrayList<String> filteredContents = new ArrayList<>();
        for(String line : fileContents) {

            //Let's start by filtering some of the formatting data
            if(!line.startsWith("-") && !line.startsWith("|")
                    && !line.startsWith(" Personeelslid") && !line.startsWith(" ---")
                    && !line.startsWith(" Toelichting:")) {
                filteredContents.add(line);
            }

            StringBuilder toWrite = new StringBuilder();
            for(String s : filteredContents) {
                activity.dataTextView.setText("");

                toWrite.append(s).append("\n");
            }
            activity.dataTextView.setText(toWrite);

        }
        return success;
    }

}
