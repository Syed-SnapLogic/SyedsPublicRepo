/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.test.bctest;

import org.apache.commons.lang.StringUtils;

public class NumUtils {
    public NumUtils() {
    }

    public static boolean isNumber(String str) {
        if (!StringUtils.isEmpty(str) && !str.startsWith("0x") && !str.endsWith("L")) {
            char[] chars = str.toCharArray();
            int sz = chars.length;
            boolean hasExp = false;
            boolean hasDecPoint = false;
            boolean allowSigns = false;
            boolean foundDigit = false;
            int start = chars[0] == '-' ? 1 : 0;
            int i;
            if (sz > start + 1 && chars[start] == '0' && chars[start + 1] == 'x') {
                i = start + 2;
                if (i == sz) {
                    return false;
                } else {
                    while(i < chars.length) {
                        if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) {
                            return false;
                        }

                        ++i;
                    }

                    return true;
                }
            } else {
                --sz;

                for(i = start; i < sz || i < sz + 1 && allowSigns && !foundDigit; ++i) {
                    if (chars[i] >= '0' && chars[i] <= '9') {
                        foundDigit = true;
                        allowSigns = false;
                    } else if (chars[i] == '.') {
                        if (hasDecPoint || hasExp) {
                            return false;
                        }

                        hasDecPoint = true;
                    } else if (chars[i] != 'e' && chars[i] != 'E') {
                        if (chars[i] != '+' && chars[i] != '-') {
                            return false;
                        }

                        if (!allowSigns) {
                            return false;
                        }

                        allowSigns = false;
                        foundDigit = false;
                    } else {
                        if (hasExp) {
                            return false;
                        }

                        if (!foundDigit) {
                            return false;
                        }

                        hasExp = true;
                        allowSigns = true;
                    }
                }

                if (i < chars.length) {
                    if (chars[i] >= '0' && chars[i] <= '9') {
                        return true;
                    } else if (chars[i] != 'e' && chars[i] != 'E') {
                        if (chars[i] == '.') {
                            return !hasDecPoint && !hasExp ? foundDigit : false;
                        } else if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                            return foundDigit;
                        } else if (chars[i] != 'l' && chars[i] != 'L') {
                            return false;
                        } else {
                            return foundDigit && !hasExp;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return !allowSigns && foundDigit;
                }
            }
        } else {
            return false;
        }
    }
}
