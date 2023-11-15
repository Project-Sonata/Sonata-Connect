package com.odeyalo.sonata.connect.support.utls;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public abstract class JwtUtils {

    private static final String JWT_PART_SEPARATOR = "\\.";

    /**
     * @param tokenFormat - format to check
     * @return - true if string is valid JWT format
     */
    public static boolean isValidFormat(CharSequence tokenFormat) {
        Assert.notNull(tokenFormat, "Token must be not null!");
        String[] jwtParts = StringUtils.split(tokenFormat.toString(), JWT_PART_SEPARATOR);

        return jwtParts.length == 3;
    }
}
