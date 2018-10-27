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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
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

    public Date localDateToDate(LocalDate localDate){
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return date;
    }

    public String localDateToString(LocalDate localDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(localDateToDate(localDate));
        Integer year = calendar.get(calendar.YEAR) ;
        Integer month = calendar.get(calendar.MONTH)+1;
        Integer day = calendar.get(calendar.DAY_OF_MONTH);
        String monthString;
        if (month<10) monthString = '0' + month.toString();
        else monthString = month.toString();

        return day.toString()+'-'+monthString+'-'+year.toString();
    }

    public boolean isInteger(String number){
        boolean result;
        try {
            Integer.parseInt(number);
            result = true;
        }catch (Exception e){
            result = false;
        }
        return result;
    }

    public boolean isNumber(String number){
        boolean result;
        try {
            Double.parseDouble(number);
            result = true;
        }catch (Exception e){
            result = false;
        }
        return result;
    }

}
