package org.treinchauffeur.roosterbuilder.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.R;
import org.treinchauffeur.roosterbuilder.misc.Logger;
import org.treinchauffeur.roosterbuilder.obj.Mentor;

import java.util.Objects;

public class MentorDialog extends Dialog {

    public static final String TAG = "MentorDialog";
    @SuppressLint("SetTextI18n")
    public MentorDialog(@NonNull Context context, MainActivity activity, Mentor mentor) {
        super(context);
        setContentView(R.layout.details_dialog);
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        MaterialTextView nameView = findViewById(R.id.dialogPupilNameView);
        TextInputLayout fullNameField = findViewById(R.id.pupilNameLayout);
        TextInputLayout phoneNumberField = findViewById(R.id.pupilPhoneLayout);
        TextInputLayout emailField = findViewById(R.id.pupilMailLayout);

        MaterialTextView weekHeader = findViewById(R.id.weekHeaderText);
        MaterialTextView monday = findViewById(R.id.mondayShiftText);
        MaterialTextView tuesday = findViewById(R.id.tuesdayShiftText);
        MaterialTextView wednesday = findViewById(R.id.wednesdayShiftText);
        MaterialTextView thursday = findViewById(R.id.thursdayShiftText);
        MaterialTextView friday = findViewById(R.id.fridayShiftText);
        MaterialTextView saturday = findViewById(R.id.saturdayShiftText);
        MaterialTextView sunday = findViewById(R.id.sundayShiftText);

        MaterialButton buttonSave, buttonCancel;
        buttonSave = findViewById(R.id.buttonSavePupil);
        buttonCancel = findViewById(R.id.buttonCancelPupil);

        //if(mentor.getNeatName().equals("")) mentor.setNeatName(mentor.getName());

        nameView.setText(mentor.getNeatName());
        fullNameField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameView.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(fullNameField.getEditText()).setText(mentor.getNeatName());
        Objects.requireNonNull(phoneNumberField.getEditText()).setText(mentor.getPhoneNumber());
        Objects.requireNonNull(emailField.getEditText()).setText(mentor.getEmail());

        //Warn user if certain details aren't known.
        if (emailField.getEditText().getText().length() < 2) emailField.setError("Geen e-mailadres bekend!");
        if (phoneNumberField.getEditText().getText().length() < 2) phoneNumberField.setError("Geen telefoonnummer bekend!");

        //If no email is known, we'll help the user along by generating a part of it. Same goes for phone number.
        emailField.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (emailField.getEditText().getText().length() < 2) {
                if (fullNameField.getEditText().getText().toString().length() > mentor.getName().length()) {
                    String address = fullNameField.getEditText().getText().toString().toLowerCase() + "@ns.nl";
                    address = address.replaceFirst(" ", ".");
                    address = address.replace(" ", "");
                    address = address.replace("ü", "ue");
                    address = address.replace("ä", "ae");
                    address = address.replace("ö", "oe");
                    Objects.requireNonNull(emailField.getEditText()).setText(address);
                } else {
                    Objects.requireNonNull(emailField.getEditText()).setText(mentor.generateApproxEmailAddress());
                }
            }
        });
        phoneNumberField.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (phoneNumberField.getEditText().getText().length() < 2) {
                Objects.requireNonNull(phoneNumberField.getEditText()).setText("06");
            }
        });

        //Button actions
        buttonSave.setOnClickListener(v -> {
            //Save full name
            if(fullNameField.getEditText().getText().toString().length() > mentor.getName().length() / 2) {

                Objects.requireNonNull(activity.mentorsMap.get(mentor.getId())).setNeatName(fullNameField.getEditText().getText().toString());
                activity.saveData();
            } else {
                fullNameField.setError("Voornaam + Achternaam");
                return;
            }

            //Save phone number
            if(phoneNumberField.getEditText().getText().toString().startsWith("06") && phoneNumberField.getEditText().getText().toString().length() == 10) {
                Objects.requireNonNull(activity.mentorsMap.get(mentor.getId())).setPhoneNumber(phoneNumberField.getEditText().getText().toString());
                activity.saveData();
            } else {
                phoneNumberField.setError("Formaat: 0612345678");
                return;
            }

            //Save email
            if(!emailField.getEditText().getText().toString().endsWith("@ns.nl")) {
                emailField.setError("E-mailadres eindigt niet op '@ns.nl'");
                return;
            } else if(emailField.getEditText().getText().toString().length() < (mentor.getName().split(" ")[0].length() + 6)) {
                emailField.setError("E-mailadres is te kort");
                return;
            } else {
                Objects.requireNonNull(activity.mentorsMap.get(mentor.getId())).setEmail(emailField.getEditText().getText().toString());
                activity.saveData();
            }

            dismiss();
            activity.displayData();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        weekHeader.setVisibility(View.GONE);
        monday.setVisibility(View.GONE);
        tuesday.setVisibility(View.GONE);
        wednesday.setVisibility(View.GONE);
        thursday.setVisibility(View.GONE);
        friday.setVisibility(View.GONE);
        saturday.setVisibility(View.GONE);
        sunday.setVisibility(View.GONE);
    }
}
