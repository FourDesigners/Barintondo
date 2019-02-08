package it.uniba.di.sms.barintondo.utils;

import java.util.regex.Pattern;

public class VerifyString {
    private static final int MIN_LENGTH = 8;

    public static boolean isNicknameNotValid(String nickname) {
        return nickname.equals("");
    }

    // Formato email: caratteri + @ + . + caratteri
    public static boolean isEmailNotValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return true;
        return !pat.matcher(email).matches();
    }

    // Formato password: almeno otto caratteri
    public static boolean isPasswordNotValid(String password) {
        return password.length() < MIN_LENGTH;
    }

    public static boolean notMatchingPasswords(String password1, String password2) {
        return !password1.equals(password2);
    }
}
