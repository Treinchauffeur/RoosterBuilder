package org.treinchauffeur.roosterbuilder.io;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.R;
import org.treinchauffeur.roosterbuilder.misc.Logger;
import org.treinchauffeur.roosterbuilder.misc.Tools;
import org.treinchauffeur.roosterbuilder.obj.Mentor;
import org.treinchauffeur.roosterbuilder.obj.Pupil;
import org.treinchauffeur.roosterbuilder.obj.Shift;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class PdfFactory {

    public static final String TAG = "PdfFactory";

    private Context context;
    private MainActivity activity;
    private AssetManager assets;
    private final String path, fileName; //Initialized in constructor; environment-specific values.
    private TreeMap<String, Pupil> pupilsMap;
    private TreeMap<String, Mentor> mentorsMap;

    private PdfWriter pdfWriter;

    public PdfFactory(Context context, MainActivity activity, TreeMap<String, Pupil> pupils, TreeMap<String, Mentor> mentors) {
        this.context = context;
        this.activity = activity;
        this.pupilsMap = pupils;
        this.mentorsMap = mentors;
        path = Objects.requireNonNull(context.getExternalFilesDir(null)).getPath() + File.separator;
        fileName = "Weekrooster_aspiranten_" + activity.weekNumber + "_" + activity.yearNumber + ".pdf";
        this.assets = activity.getAssets();
        cleanDirectory();
        PDFBoxResourceLoader.init(context);


        //Let's get rid of the pupils that we shouldn't display.
        ArrayList<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, Pupil> set : pupilsMap.entrySet()) {
            Pupil pupil = set.getValue();

            if (!pupil.getShouldDisplay()) toRemove.add(pupil.getName());
        }
        for (String name : toRemove) pupilsMap.remove(name);
    }

    private void cleanDirectory() {
        for (File file : Objects.requireNonNull(new File(path).listFiles()))
            if (file.exists())
                if (!file.delete()) Logger.debug(TAG, file.getName() + ": not deleted!");
                else Logger.debug(TAG, file.getName() + ": deleted!");
    }

    public void write() {
        Document document = new Document(PageSize.A4);
        Rectangle size = new Rectangle(500, Tools.getPageHeight(pupilsMap, mentorsMap));
        document.setPageSize(size);

        Font font = FontFactory.getFont(FontFactory.HELVETICA, 5);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 4);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 6);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);

        try {
            pdfWriter = PdfWriter.getInstance(document,
                    Files.newOutputStream(Paths.get(path + fileName)));

            document.open();

            float width = document.getPageSize().getWidth();
            float[] columnDefinitionSize = {20, 11.51F, 11.51F, 11.51F, 11.51F, 11.51F, 11.51F, 11.51F};

            PdfPTable table = new PdfPTable(columnDefinitionSize);
            table.setHorizontalAlignment(0);
            table.setTotalWidth(width - 75);
            table.setLockedWidth(true);

            Phrase title = new Phrase("Aspirantenrooster week " + activity.weekNumber, headerFont);
            title.getFont().setColor(0, 48, 130);
            Paragraph titleParagraph = new Paragraph(title);
            titleParagraph.setSpacingAfter(5f);
            document.add(titleParagraph);


            PdfPTable topTable = new PdfPTable(1);
            topTable.setHorizontalAlignment(0);
            topTable.setTotalWidth(350);
            topTable.setLockedWidth(true);
            PdfPCell topCell = new PdfPCell(new Phrase(context.getResources().getString(R.string.pdfTopText), font));
            topCell.setColspan(1);
            topCell.setBorder(0);
            topTable.addCell(topCell);
            document.add(topTable);

            headerFont.setSize(12);
            Phrase tableTitle = new Phrase("Rooster", headerFont);
            tableTitle.getFont().setColor(0, 48, 130);
            Paragraph tableTitleParagraph = new Paragraph(tableTitle);
            tableTitleParagraph.setSpacingAfter(5f);
            document.add(tableTitleParagraph);

            PdfPCell dayCell;
            dayCell = new PdfPCell(new Phrase("Naam", boldFont));
            dayCell.setPaddingBottom(4);
            dayCell.setBorderWidthTop(1f);
            dayCell.setBorderWidthLeft(1f);
            dayCell.setBackgroundColor(new Color(220, 220, 220));
            table.addCell(dayCell);

            dayCell = new PdfPCell(new Phrase("Maandag", boldFont));
            dayCell.setPaddingBottom(4);
            dayCell.setBorderWidthTop(1f);
            dayCell.setBorderWidthLeft(1f);
            dayCell.setBackgroundColor(new Color(220, 220, 220));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dayCell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);
            dayCell = new PdfPCell(new Phrase("Dinsdag", boldFont));
            dayCell.setPaddingBottom(4);
            dayCell.setBorderWidthTop(1f);
            dayCell.setBackgroundColor(new Color(220, 220, 220));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dayCell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);
            dayCell = new PdfPCell(new Phrase("Woensdag", boldFont));
            dayCell.setPaddingBottom(4);
            dayCell.setBorderWidthTop(1f);
            dayCell.setBackgroundColor(new Color(220, 220, 220));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dayCell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);
            dayCell = new PdfPCell(new Phrase("Donderdag", boldFont));
            dayCell.setPaddingBottom(4);
            dayCell.setBorderWidthTop(1f);
            dayCell.setBackgroundColor(new Color(220, 220, 220));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dayCell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);
            dayCell = new PdfPCell(new Phrase("Vrijdag", boldFont));
            dayCell.setPaddingBottom(4);
            dayCell.setBorderWidthTop(1f);
            dayCell.setBackgroundColor(new Color(220, 220, 220));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dayCell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);
            dayCell = new PdfPCell(new Phrase("Zaterdag", boldFont));
            dayCell.setPaddingBottom(4);
            dayCell.setBorderWidthTop(1f);
            dayCell.setBackgroundColor(new Color(220, 220, 220));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dayCell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);
            dayCell = new PdfPCell(new Phrase("Zondag", boldFont));
            dayCell.setPaddingBottom(4);
            dayCell.setBorderWidthTop(1f);
            dayCell.setBorderWidthRight(1f);
            dayCell.setBackgroundColor(new Color(220, 220, 220));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dayCell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);

            //Every pupil has two lines
            for (Map.Entry<String, Pupil> set : pupilsMap.entrySet()) {
                Pupil pupil = set.getValue();

                if (!pupil.getShouldDisplay()) continue;

                //First line: pupil's name with shifts
                PdfPCell nameCell = new PdfPCell(new Phrase(pupil.getNeatName(), boldFont));
                nameCell.setBorderWidthTop(1f);
                nameCell.setBorderWidthLeft(1f);
                table.addCell(nameCell);

                for (Shift shift : pupil.getShifts()) {
                    PdfPCell shiftNumberCell = new PdfPCell(new Phrase(shift.getNeatShiftNumber(), font));
                    if (shift.isRestingDay())
                        shiftNumberCell.setBackgroundColor(new Color(255, 221, 221));

                    if (shift.getWeekDay() == Shift.MAANDAG) shiftNumberCell.setBorderWidthLeft(1f);
                    if (shift.getWeekDay() == Shift.ZONDAG) shiftNumberCell.setBorderWidthRight(1f);
                    shiftNumberCell.setBorderWidthTop(1f);
                    shiftNumberCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(shiftNumberCell);
                }

                //Second line: mentor or extra information
                PdfPCell extraInfoFirstCell = new PdfPCell(new Phrase("Mentor / extra informatie", font));
                extraInfoFirstCell.setBorderWidthBottom(1f);
                extraInfoFirstCell.setBorderWidthLeft(1f);
                extraInfoFirstCell.setPaddingBottom(3f);
                table.addCell(extraInfoFirstCell);

                for (Shift shift : pupil.getShifts()) {
                    PdfPCell infoCell;
                    if (shift.withMentor())
                        infoCell = new PdfPCell(new Phrase(shift.getMentor().getNeatName(), smallFont));
                    else if (shift.shouldNotDisplayExtraInfo())
                        infoCell = new PdfPCell(new Phrase("-", smallFont));
                    else if (shift.getExtraInfo().length() > 1)
                        infoCell = new PdfPCell(new Phrase(shift.getExtraInfo(), smallFont));
                    else infoCell = new PdfPCell(new Phrase("-", font));

                    if (shift.getWeekDay() == Shift.MAANDAG) infoCell.setBorderWidthLeft(1f);
                    if (shift.getWeekDay() == Shift.ZONDAG) infoCell.setBorderWidthRight(1f);
                    infoCell.setBorderWidthBottom(1f);
                    infoCell.setPaddingBottom(3f);
                    infoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(infoCell);
                }
            }

            document.add(table);

            //Second part of the document. We need to add the tables for the pupil & mentor phone numbers.
            float[] phoneColumnDefinitionSize = {20, 8.51F, 10, 20, 8.51F};

            PdfPTable tablePhone = new PdfPTable(phoneColumnDefinitionSize);
            PdfPCell cellPhone = null;

            tablePhone.setHorizontalAlignment(0);
            tablePhone.setTotalWidth(300);
            tablePhone.setLockedWidth(true);

            //We temporarily fill the smallest map with duds, so that we can iterate through them easily..
            int delta = 0;
            if (pupilsMap.size() > mentorsMap.size()) {
                delta = pupilsMap.size() - mentorsMap.size();
                for (int i = 0; i < delta; i++) {
                    mentorsMap.put(Tools.dudText + i, new Mentor(Tools.dudText, Tools.dudText));
                }
            } else if (pupilsMap.size() < mentorsMap.size()) {
                delta = mentorsMap.size() - pupilsMap.size();
                for (int i = 0; i < delta; i++)
                    pupilsMap.put(Tools.dudText + i, new Pupil(Tools.dudText));
            }
            Logger.debug(TAG, pupilsMap.size() + "");

            Phrase phoneTitle = new Phrase("Telefoonnummers aspiranten & mentoren", headerFont);
            phoneTitle.getFont().setColor(0, 48, 130);
            Paragraph phoneTitleParagraph = new Paragraph(phoneTitle);
            phoneTitleParagraph.setSpacingBefore(10f);
            phoneTitleParagraph.setSpacingAfter(5f);
            document.add(phoneTitleParagraph);

            cellPhone = new PdfPCell(new Phrase("Aspiranten", boldFont));
            cellPhone.setPaddingBottom(5f);
            cellPhone.setBorder(0);
            cellPhone.setColspan(2);
            tablePhone.addCell(cellPhone);

            cellPhone = new PdfPCell(new Phrase(""));
            cellPhone.setPaddingBottom(5f);
            cellPhone.setBorder(0);
            tablePhone.addCell(cellPhone);

            cellPhone = new PdfPCell(new Phrase("Mentoren", boldFont));
            cellPhone.setPaddingBottom(5f);
            cellPhone.setBorder(0);
            cellPhone.setColspan(2);
            tablePhone.addCell(cellPhone);


            //Headers for both tables
            cellPhone = new PdfPCell(new Phrase("Naam", boldFont));
            cellPhone.setBackgroundColor(new Color(220, 220, 220));
            tablePhone.addCell(cellPhone);
            cellPhone = new PdfPCell(new Phrase("Telefoon", boldFont));
            cellPhone.setBackgroundColor(new Color(220, 220, 220));
            cellPhone.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablePhone.addCell(cellPhone);

            cellPhone = new PdfPCell(new Phrase("", font));
            cellPhone.setBorder(0);
            tablePhone.addCell(cellPhone);


            cellPhone = new PdfPCell(new Phrase("Naam", boldFont));
            cellPhone.setBackgroundColor(new Color(220, 220, 220));
            tablePhone.addCell(cellPhone);
            cellPhone = new PdfPCell(new Phrase("Telefoon", boldFont));
            cellPhone.setBackgroundColor(new Color(220, 220, 220));
            cellPhone.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablePhone.addCell(cellPhone);

            //Now that we know that both maps have the same size(), we can iterate through them safely.
            //We'll use temporary arraylists for this purpose.
            ArrayList<Pupil> pupils = new ArrayList<>(pupilsMap.values());
            ArrayList<Mentor> mentors = new ArrayList<>(mentorsMap.values());

            for (int i = 0; i < pupils.size(); i++) {
                Pupil pupil = pupils.get(i);
                Mentor mentor = mentors.get(i);

                if (!pupil.getName().startsWith(Tools.dudText) && pupil.getShouldDisplay()) {
                    cellPhone = new PdfPCell(new Phrase(pupil.getNeatName(), font));
                    tablePhone.addCell(cellPhone);
                    String phoneLine = (pupil.getPhoneNumber().length() < 2) ? "Onbekend" : pupil.getPhoneNumber();
                    cellPhone = new PdfPCell(new Phrase(phoneLine, font));
                    cellPhone.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tablePhone.addCell(cellPhone);
                } else {
                    cellPhone = new PdfPCell(new Phrase("", font));
                    cellPhone.setBorder(0);
                    tablePhone.addCell(cellPhone);
                    cellPhone = new PdfPCell(new Phrase("", font));
                    cellPhone.setBorder(0);
                    tablePhone.addCell(cellPhone);
                }

                cellPhone = new PdfPCell(new Phrase("", font));
                cellPhone.setBorder(0);
                tablePhone.addCell(cellPhone);

                if (!mentor.getName().startsWith(Tools.dudText)) {
                    cellPhone = new PdfPCell(new Phrase(mentor.getNeatName(), font));
                    tablePhone.addCell(cellPhone);
                    String phoneLine = (mentor.getPhoneNumber().length() < 2) ? "Onbekend" : mentor.getPhoneNumber();
                    cellPhone = new PdfPCell(new Phrase(phoneLine, font));
                    cellPhone.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tablePhone.addCell(cellPhone);
                } else {
                    cellPhone = new PdfPCell(new Phrase("", font));
                    cellPhone.setBorder(0);
                    tablePhone.addCell(cellPhone);
                    cellPhone = new PdfPCell(new Phrase("", font));
                    cellPhone.setBorder(0);
                    tablePhone.addCell(cellPhone);
                }
            }

            if (pupils.size() > 0) document.add(tablePhone);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        document.close();
        signPdf();
    }

    /**
     * Makes the pdf file quite a bit prettier by adding a logo and a footer.
     */
    private void signPdf() {
        File pdfFile = new File(path + fileName);
        if (pdfFile.exists()) {
            Logger.debug(TAG, "PDF created successfully at: " + pdfFile.getAbsolutePath());

            File edited = pdfFile;
            try {
                //Logo
                PDDocument document = PDDocument.load(pdfFile);
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ns_flow);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitMapData = stream.toByteArray();
                PDImageXObject imageXObject = PDImageXObject.createFromByteArray(document, bitMapData, "logo");
                PDPage page = document.getPage(0);

                PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
                int margin = 40;
                float width = 30, height = 30;
                float x = page.getBBox().getUpperRightX() - (width + margin);
                float y = page.getBBox().getUpperRightY() - (2 * margin);
                contentStream.drawImage(imageXObject, x, y, width, height);
                contentStream.close();

                //Footer
                Bitmap bitmapFooter = BitmapFactory.decodeResource(context.getResources(), R.drawable.ns_footer);
                ByteArrayOutputStream streamFooter = new ByteArrayOutputStream();
                bitmapFooter.compress(Bitmap.CompressFormat.PNG, 100, streamFooter);
                byte[] bitMapDataFooter = streamFooter.toByteArray();
                PDImageXObject imageXObjectFooter = PDImageXObject.createFromByteArray(document, bitMapDataFooter, "footer");

                PDPageContentStream contentStreamFooter = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
                float widthFooter = 500, heightFooter = 21; //should be 21.4
                float xFooter = 0;
                float yFooter = 0;
                contentStreamFooter.drawImage(imageXObjectFooter, xFooter, yFooter, widthFooter, heightFooter);
                contentStreamFooter.close();

                edited = new File(path + fileName.replace(".pdf", "") + "_rendered.pdf");
                document.save(edited);
                document.close();

                Logger.debug(TAG, "Image added to PDF successfully at: " + edited.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            sendPdf(edited);
        } else {
            Logger.debug(TAG, "PDF file doesn't exist!");
            Snackbar.make(activity.findViewById(R.id.parentView), "Er is een fout opgetreden tijdens het genereren van het pdf-bestand!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void sendPdf(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri1 = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        intent.setDataAndType(uri1, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        activity.startActivity(intent);
    }

}
