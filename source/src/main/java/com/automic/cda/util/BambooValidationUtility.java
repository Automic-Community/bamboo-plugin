package com.automic.cda.util;

import java.util.regex.Pattern;

import com.automic.cda.constants.BambooConstants;
import com.automic.cda.ws.rest.exceptions.BambooCdaRuntimeException;

/**
 * Defines validation and utility methods.
 */
public final class BambooValidationUtility {
    private BambooValidationUtility() {
    }

    public static String validateConnectionParameter(String url, String userName) {
        String errMsg = validateServerURL(url);
        errMsg = (errMsg != null) ? errMsg : validateUserName(userName);
        return errMsg;
    }

    public static String validateServerURL(String value) {
        String errMsg = null;
        if (!CommonUtil.isNotEmpty(value)) {
            errMsg = "The Server is mandatory";
        } else if (!CommonUtil.isValidURL(value)) {
            errMsg = "Invalid server url";
        }
        return errMsg;
    }

    public static String validateUserName(String value) {
        String errMsg = null;
        if (!CommonUtil.isNotEmpty(value)) {
            errMsg = "User Name is mandatory";
        } else {
            errMsg = (value.indexOf('/') == -1) ? "Invalid User Name" : errMsg;
        }
        return errMsg;
    }

    public static String validatePackageName(String packageName) {
        String errMsg = null;
        if (!CommonUtil.isNotEmpty(packageName)) {
            errMsg = "Package name is mandatory";
        } else {
            Pattern pattern = Pattern.compile(BambooConstants.PCK_NAME);
            errMsg = pattern.matcher(packageName).matches() ? errMsg
                    : "Package Name can only contain alphanumeric characters, blanks, '.', '-', '_', '@', '$', '#' ";
        }
        return errMsg;
    }


    public static String checkCompatibleARA(Exception ex) {
        String errMsg = null;
        if (ex instanceof BambooCdaRuntimeException
                && ((BambooCdaRuntimeException) ex).getExceptionCode() == BambooConstants.HTTP_NOTFOUND) {
            errMsg = "You might not be using compatible CDA version.";
        }
        return errMsg;
    }

}
