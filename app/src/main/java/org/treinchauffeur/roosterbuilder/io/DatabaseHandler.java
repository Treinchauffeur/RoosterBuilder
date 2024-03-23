package org.treinchauffeur.roosterbuilder.io;

import static android.content.Context.MODE_PRIVATE;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.misc.Tools;
import org.treinchauffeur.roosterbuilder.obj.StoredPupil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Objects;

public class DatabaseHandler {

    public static final String TAG = "DatabaseHandler";
    private final Context context;
    private final MainActivity activity;

    private String path;
    private final String fileNameMentor = "Mentoren.json";
    private final String fileNamePupil = "Aspiranten.json";

    public DatabaseHandler(Context context, MainActivity activity) {
        this.context = context;
        this.activity = activity;
        path = Objects.requireNonNull(context.getExternalFilesDir(null)).getPath() + File.separator;
    }

    public void saveData() {
        try {
            File file = new File(path + fileNamePupil);
            String pupilsString = new Gson().toJson(activity.savedPupils);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(pupilsString);
            bw.close();

            if(file.exists())
                Log.d(TAG, "saveData: saved pupils to file: " + file.getAbsoluteFile() + " (" + file.length() + " bytes)");
        } catch (Exception e) {
            Log.e(TAG, "saveData: Error whilst writing pupil file:", e);
        }

        try {
            File file = new File(path + fileNameMentor);
            String mentorString = new Gson().toJson(MainActivity.mentorsMap);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(mentorString);
            bw.close();

            if(file.exists())
                Log.d(TAG, "saveData: saved mentors to file: " + file.getAbsoluteFile() + " (" + file.length() + " bytes)");
        } catch (Exception e) {
            Log.e(TAG, "saveData: Error whilst writing mentor file:", e);
        }
    }

    public void exportPupils() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, fileNamePupil);
        activity.startActivityForResult(intent, MainActivity.JSON_REQUEST_CODE_PUPILS);
    }

    public void exportMentors() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, fileNameMentor);
        activity.startActivityForResult(intent, MainActivity.JSON_REQUEST_CODE_MENTORS);
    }

    public void writeFile(Uri uri, String data) {
        try {
            OutputStream out = context.getContentResolver().openOutputStream(Objects.requireNonNull(uri));
            assert out != null;
            out.write(data.getBytes());
            out.close();
            if(data.getBytes().length > 0)
                Toast.makeText(context, "Succesvol geëxporteerd!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "writeFile: ", e);
            throw new RuntimeException(e);
        }
    }

    public void importPupils(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String content = reader.readLine();
            if(!Tools.isJson(content)) {
                Toast.makeText(context, "Ongeldige json!", Toast.LENGTH_SHORT).show();
                return;
            } else if(content.contains("\"id\":")) {
                Toast.makeText(context, "Dit is een mentorenbestand!", Toast.LENGTH_SHORT).show();
                return;
            } else if(!content.contains("neatName")) {
                Toast.makeText(context, "Ongeldig database-bestand!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = context.getSharedPreferences("RoosterBot", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("SavedPupilsMap", content);
            editor.apply();

            Toast.makeText(context, "De app wordt opnieuw opgestart..", Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> restartApp(context), 3000);
        } catch (Exception e) {
            Log.e(TAG, "importPupils: ", e);
        }
    }

    public void importMentors(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String content = reader.readLine();
            if(!Tools.isJson(content)) {
                Toast.makeText(context, "Ongeldige json!", Toast.LENGTH_SHORT).show();
                return;
            } else if(!content.contains("\"id\":")) {
                Toast.makeText(context, "Dit is geen mentorenbestand!", Toast.LENGTH_SHORT).show();
                return;
            } else if(!content.contains("NeatName")) {
                Toast.makeText(context, "Ongeldig database-bestand!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = context.getSharedPreferences("RoosterBot", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("SavedMentorMap", content);
            editor.apply();

            Toast.makeText(context, "De app wordt opnieuw opgestart..", Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> restartApp(context), 3000);
        } catch (Exception e) {
            Log.e(TAG, "importMentors: ", e);
        }
    }

    public static void restartApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        assert intent != null;
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        // Required for API 34 and later
        // Ref: https://developer.android.com/about/versions/14/behavior-changes-14#safer-intents
        mainIntent.setPackage(context.getPackageName());
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }
}
