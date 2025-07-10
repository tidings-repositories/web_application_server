package com.delivalue.tidings.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ForbiddenWordFilter {
    private static final Set<String> forbiddenWords;

    static {
        Set<String> tempForbiddenWords = new HashSet<>();
        try(InputStream is = ForbiddenWordFilter.class.getClassLoader().getResourceAsStream("forbiddenWords.txt")) {
            if(is == null) throw new IOException("forbiddenWords.txt 파일을 찾을 수 없습니다.");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        tempForbiddenWords.add(line.toLowerCase(Locale.ROOT));
                    }
                }
            }
        } catch (IOException e) {
            //TODO: SLF4J나 Log4j로 변경하여 로깅할 것
            tempForbiddenWords.addAll(Set.of("admin", "root", "stellagram", "system", "null"));
            e.printStackTrace();
        }

        forbiddenWords = Collections.unmodifiableSet(tempForbiddenWords);
    }

    public static boolean containsForbiddenWord(String input) {
        return forbiddenWords.contains(input.toLowerCase(Locale.ROOT));
    }
}
