package org.treinchauffeur.roosterbuilder.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.R;
import org.treinchauffeur.roosterbuilder.io.DatabaseHandler;
import org.treinchauffeur.roosterbuilder.misc.Logger;
import org.treinchauffeur.roosterbuilder.obj.Mentor;
import org.treinchauffeur.roosterbuilder.obj.Pupil;
import org.treinchauffeur.roosterbuilder.obj.Shift;
import org.treinchauffeur.roosterbuilder.obj.StoredPupil;

import java.util.HashMap;
import java.util.Objects;

public class DatabaseDialog extends Dialog {

    public static final String TAG = "PupilDialog";
    private final DatabaseHandler databaseHandler;
    private MaterialButton importPupilButton, exportPupilButton, importMentorButton, exportMentorButton;

    private String pupilString, mentorString;

    @SuppressLint("SetTextI18n")
    public DatabaseDialog(@NonNull Context context, MainActivity activity) {
        super(context);
        databaseHandler = new DatabaseHandler(context, activity);
        setContentView(R.layout.databases_dialog);
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        importPupilButton = findViewById(R.id.pupil_import_button);
        exportPupilButton = findViewById(R.id.pupil_export_button);
        importMentorButton = findViewById(R.id.mentor_import_button);
        exportMentorButton = findViewById(R.id.mentor_export_button);

        MaterialButton buttonSave, buttonCancel;
        buttonCancel = findViewById(R.id.buttonCancelDatabase);

        SharedPreferences sharedPreferences = context.getSharedPreferences("RoosterBot", Context.MODE_PRIVATE);
        String defValue = new Gson().toJson(new HashMap<String, StoredPupil>());
        pupilString = sharedPreferences.getString("SavedPupilsMap",defValue);

        String mentorValue = new Gson().toJson(new HashMap<String, Mentor>());
        mentorString = sharedPreferences.getString("SavedMentorMap",mentorValue);

        //Button actions
        exportPupilButton.setOnClickListener(v -> {
            dismiss();
            databaseHandler.exportPupils();
        });

        exportMentorButton.setOnClickListener(v -> {
            dismiss();
            databaseHandler.exportMentors();
        });

        importPupilButton.setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            activity.startActivityForResult(intent, MainActivity.IMPORT_REQUEST_CODE_PUPILS);
        });

        importMentorButton.setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            activity.startActivityForResult(intent, MainActivity.IMPORT_REQUEST_CODE_MENTORS);
        });

        buttonCancel.setOnClickListener(v -> dismiss());
    }
}
