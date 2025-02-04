package com.aivle.project.Validator;

import java.util.regex.Pattern;

public class PasswordValidator {

    //형식 정규식
    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isValidPassword(String password) {
        return password != null && pattern.matcher(password).matches();
    }
}