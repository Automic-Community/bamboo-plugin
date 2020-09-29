package com.automic.cda.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Defines constants used across Bamboo plugin classes.
 */
public final class BambooConstants {

    public static final int HTTP_NOTFOUND = 404;
    
    public static final Charset ENCODING = StandardCharsets.UTF_8;
    public static final String COMPARATOR_CONTAIN = "Contains";
    public static final String COMPARATOR_NOT_CONTAIN = "Does not contain";
    public static final String PCK_NAME = "[\\w\\d\\$\\.\\@\\#\\s\\-]+";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String ARA_TIMEZONE = "UTC";
    public static final String FORM_DATE_FORMAT = "yyyy-MM-dd HH:mm";
  
    //centralized connection parameters
    public static final String GLOBAL_SERVER = "server";
    public static final String GLOBAL_USER = "user";
    public static final String GLOBAL_PASS = "pass";
    
    // environment variables
    public static final String ENV_SERVER = "CDA_URL=";
    public static final String ENV_USER = "CDA_USR=";
    public static final String ENV_PASS = "CDA_PWD=";
    
	// global variables
    public static final String SERVER = "$CDA_URL";
    public static final String USERNAME = "$CDA_USR";
    public static final String PASS = "$CDA_PWD";
    public static final String SHARED_CREDENTIALS_ID = "sharedCredentialsId";

    
    
	public static final String COMPONENT_NAME = "component_name_";
	public static final String ARTIFACT_NAME = "artifact_name_";
	public static final String ARTIFACT_SRC = "artifact_source_";
	public static final String ARTIFACT_FIELD = "artifact_custom_field_";
	public static final String PROPERTY_NAME = "property_name_";
	public static final String PROPERTY_VALUE = "property_value_";
	public static final String PROPERTY_TYPE = "property_type_";
	public static final String DYN_PROPERTY_NAME = "d_property_name_";
	public static final String DYN_PROPERTY_VALUE = "d_property_value_";
	public static final String CONDITION_VALUE = "condition_value_";
	public static final String CONDITION_LOG = "condition_build_log_";
	
	// Common Configuration fields
	public static final String CDA_URL = "cdaUrl";
	public static final String CDA_USERNAME = "username_password";
	public static final String CDA_PASSWRD = "cdaPassword";
	
	// Package Configuration fields
	public static final String PKG_NAME = "pkgName";
	public static final String PKG_TYPE = "pkgType";
	public static final String APP_NAME = "appName";
	public static final String FOLDER_NAME = "folderName";
	public static final String OWNER_NAME = "ownerName";
	public static final String COMPONENTS = "components";
	public static final String PROPERTIES = "properties";
	public static final String DYNAMIC_PROPERTIES = "dynamicProperties";
	
	public static final String WORKFLOW = "workflows";
    public static final String MAX_RESULTS = "max_results";

	
    private BambooConstants() {
    }

}
