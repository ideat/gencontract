package mindware.com.utilities;

import java.util.regex.Pattern;

public class NumberToLiteral {
    private final String[] UNIT = {"", "un ", "dos ", "tres ", "cuatro ", "cinco ", "seis ", "siete ", "ocho ", "nueve "};
    private final String[] TENS = {"diez ", "once ", "doce ", "trece ", "catorce ", "quince ", "dieciseis ",
            "diecisiete ", "dieciocho ", "diecinueve ", "veinte ", "veintiuno ", "veintidos ","veintitres ","veinticuatro ",
            "veinticinco ","veintiseis ", "veintisiete ", "veintiocho ", "veintinueve ", "treinta ", "cuarenta ",
            "cincuenta ", "sesenta ", "setenta ", "ochenta ", "noventa "};
    private final String[] HUNDREDS = {"", "ciento ", "doscientos ", "trecientos ", "cuatrocientos ", "quinientos ", "seiscientos ",
            "setecientos ", "ochocientos ", "novecientos "};

    public NumberToLiteral(){}


    public String Convert(String number, boolean upperCase, String currency, String typeNumber) {
        String literal = "";
        String decimalPart;
        //si el numero utiliza (.) en lugar de (,) -> se reemplaza
        number = number.replace(".", ",");
        //si el numero no tiene parte decimal, se le agrega ,00
        if (typeNumber.equals("float"))
            if(number.indexOf(",")==-1){
                number = number + ",00";
            }
        //se valida formato de entrada -> 0,00 y 999 999 999,00
        String patter="";
        if (typeNumber.equals("float"))
            patter = "\\d{1,9},\\d{1,2}";
        else {
            patter = "\\d{1,9}";
        }
        if (Pattern.matches(patter, number)) {
            //se divide el numero 0000000,00 -> entero y decimal
            String Num[]=null;

            if (typeNumber.equals("float") ) {
                Num = number.split(",");
                if (!currency.equals("")) {
                    if (Integer.parseInt(Num[1]) >= 10) {
                        decimalPart = Num[1] + "/100 " ;//+ currency;
                    } else {
                        if (Num[1].startsWith("0"))
                            decimalPart = Num[1] + "/100 ";// + currency;
                        else
                            decimalPart = Num[1] + "0/100 "; //+ currency;
                    }
                }else{
                    if (Integer.parseInt(Num[1]) >= 10) {
                        decimalPart = "punto " + getTens(Num[1]);
                    } else {
                        if (Num[1].startsWith("0"))
                            decimalPart = "punto cero " + getTens(Num[1]);
                        else
                            decimalPart = "punto " + getTens(Num[1]) ;
                    }
                }
            }else{
                Num = number.split(",");
                Num[0] = number;
                decimalPart="";
            }

            //se convierte el numero a literal
            if (Integer.parseInt(Num[0]) == 0) {//si el valor es cero
                literal = "cero ";
            } else if (Integer.parseInt(Num[0]) > 999999) {//si es millon
                literal = getMillons(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 999) {//si es miles
                literal = getThounsands(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 99) {//si es centena
                literal = getHundreds(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 9) {//si es decena
                literal = getTens(Num[0]);
            } else {//sino unidades -> 9
                literal = getUnits(Num[0]);
            }
            //devuelve el resultado en mayusculas o minusculas
            if (upperCase) {

                return (literal + decimalPart).toUpperCase();
            } else {
                return (literal + decimalPart);
            }
        } else {//error, no se puede convertir
            return literal = null;
        }
    }

    private String getUnits(String number) {// 1 - 9
        //si tuviera algun 0 antes se lo quita -> 09 = 9 o 009=9
        String num = number.substring(number.length() - 1);
        return UNIT[Integer.parseInt(num)];
    }

    private String getTens(String num) {// 99
        int n = Integer.parseInt(num);
        if (n < 10) {//para casos como -> 01 - 09
            return getUnits(num);
        } else if (n > 19) {//para 20...99
            String u = getUnits(num);
            if (u.equals("")) { //para 20,30,40,50,60,70,80,90
                if (Integer.parseInt(num.substring(0,1))==2)
                    return TENS[Integer.parseInt(num.substring(0, 1)) + 8];
                else
                    return TENS[Integer.parseInt(num.substring(0, 1)) + 17];
            } else {
                if (n > 20 && n < 30)
                    return TENS[n-10];
                else
                    return TENS[Integer.parseInt(num.substring(0, 1)) + 17] + "y " + u;
            }
        } else {//numeros entre 11 y 19
            return TENS[n - 10];
        }
    }

    private String getHundreds(String num) {// 999 o 099
        if( Integer.parseInt(num)>99 ){//es centena
            if (Integer.parseInt(num) == 100) {//caso especial
                return " cien ";
            } else {
                return HUNDREDS[Integer.parseInt(num.substring(0, 1))] + getTens(num.substring(1));
            }
        }else{//por Ej. 099
            //se quita el 0 antes de convertir a decenas
            return getTens(Integer.parseInt(num)+"");
        }
    }

    private String getThounsands(String number) {// 999 999
        //obtiene las centenas
        String c = number.substring(number.length() - 3);
        //obtiene los miles
        String m = number.substring(0, number.length() - 3);
        String n="";
        //se comprueba que miles tenga valor entero
        if (Integer.parseInt(m) > 0) {
            n = getHundreds(m);
            return n + "mil " + getHundreds(c);
        } else {
            return "" + getHundreds(c);
        }

    }

    private String getMillons(String number) { //000 000 000
        //se obtiene los miles
        String thounsands = number.substring(number.length() - 6);
        //se obtiene los millones
        String millon = number.substring(0, number.length() - 6);
        String n = "";
        if (millon.length() > 1) {
            n = getHundreds(millon) + "millones ";
        } else {
            n = getUnits(millon) + "millon ";
        }
        return n + getThounsands(thounsands);
    }
    }
