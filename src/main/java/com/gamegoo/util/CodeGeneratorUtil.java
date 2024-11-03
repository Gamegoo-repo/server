package com.gamegoo.util;

import java.security.SecureRandom;

public class CodeGeneratorUtil {

    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String ALL_CHARACTERS = UPPER_CASE + LOWER_CASE + DIGITS;
    private static final SecureRandom random = new SecureRandom();
    private static final int EMAIL_CODE_LENGTH = 5;
    private static final int PASSWORD_CODE_LENGTH = 12;

    /**
     * 이메일 인증에 사용하는 메소드 : 랜덤 코드 만들기
     *
     * @return
     */
    public static String generateEmailRandomCode() {
        StringBuilder code = new StringBuilder(EMAIL_CODE_LENGTH);
        for (int i = 0; i < EMAIL_CODE_LENGTH; i++) {
            int index = random.nextInt(UPPER_CASE.length() + DIGITS.length());
            code.append(UPPER_CASE.charAt(index % UPPER_CASE.length()));
        }
        return code.toString();
    }

    /**
     * 비밀번호 재설정하는 랜덤 코드 생성
     * @return
     */
    public static String generatePasswordRandomCode(){
        StringBuilder code = new StringBuilder(PASSWORD_CODE_LENGTH);

        // 최소한 하나의 대문자, 소문자, 숫자, 특수문자를 포함하도록 함
        code.append(UPPER_CASE.charAt(random.nextInt(UPPER_CASE.length())));
        code.append(LOWER_CASE.charAt(random.nextInt(LOWER_CASE.length())));
        code.append(DIGITS.charAt(random.nextInt(DIGITS.length())));

        // 나머지 길이를 랜덤한 모든 문자로 채움
        for (int i = 4; i < PASSWORD_CODE_LENGTH; i++) {
            int index = random.nextInt(ALL_CHARACTERS.length());
            code.append(ALL_CHARACTERS.charAt(index));
        }

        // 생성된 비밀번호를 섞어서 반환
        return shuffleString(code.toString());
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = random.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
}
