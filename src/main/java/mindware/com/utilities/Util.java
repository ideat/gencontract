package mindware.com.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

}
