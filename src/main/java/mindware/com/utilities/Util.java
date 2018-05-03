package mindware.com.utilities;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Util {
    public Date stringToDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH); //
        Date fecha = new Date();
        try {
            fecha = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fecha;
    }

    public LocalDate stringToLocalDate(String fecha, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        Date parsed = null;
        try {
            parsed = sdf.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String h;
        h = DateFormat.getDateInstance().format(parsed);
        return LocalDate.parse(h, formatter);

    }

}
