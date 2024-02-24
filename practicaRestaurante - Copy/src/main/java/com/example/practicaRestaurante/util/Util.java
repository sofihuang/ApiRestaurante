package com.example.practicaRestaurante.util;

import com.example.practicaRestaurante.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public class Util {
    public static boolean isInteger(String s) {
        try {
            int number = Integer.parseInt(s);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
    public static boolean isValidNumberOfGuests(int numberOfGuests) {
        return numberOfGuests >= 1 && numberOfGuests <= 8;
    }

    public static boolean isValidDay(int day) {
        return day >= 1 && day <= 31;
    }

    public static boolean isValidHour(int hour) {
        return hour >= 0 && hour <= 23;
    }

    // Método para validar tanto el día como la hora
    public static boolean isValidDateAndTime(int day, int hour) {
        return isValidDay(day) && isValidHour(hour);
    }

    public static boolean isValidStringLength(String str) {
        return str != null && str.length() <= 20;
    }

    // Método para verificar si un número de teléfono tiene un formato válido
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Expresión regular para verificar un número de teléfono de 9 dígitos
        String phoneRegex = "\\d{9}";
        return phoneNumber != null && phoneNumber.matches(phoneRegex);
    }

    public static int getDay(){
       return LocalDate.now().getDayOfMonth();
    }

}
