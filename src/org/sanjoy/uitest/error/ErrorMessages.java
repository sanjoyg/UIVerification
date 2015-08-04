package org.sanjoy.uitest.error;

import org.sanjoy.uitest.config.Configuration;

public interface ErrorMessages {
	public static String TOOL_USAGE						= "Usage <jar> RunMode (STORE/COMPARE/STANDALONE) ConfigFilePath(optional)";
	public static String INVALID_RUN_MODE				= "Invalid Run Mode use : STORE or COMPARE";
	public static String NO_TEST_STEP_CMD				= "Command line must specify one of the property : " + Configuration.TEST_STEP_FILE_KEY + " or " + Configuration.TEST_STEP_DIR_KEY;
	public static String BOTH_FILE_DIR_STEP_SPECIFIED 	= "Command line cannot specify both the properties : " + Configuration.TEST_STEP_FILE_KEY + " or " + Configuration.TEST_STEP_DIR_KEY;
	public static String INVALID_STEP_FILE				= "Specified test step file does not exist/no read permission: ";
	public static String INVALID_STEP_DIR				= "Specified test directory does not exist/not a directory/no read permission: ";
	public static String CONFIG_DEFINITION_MISSING		= "Configuration file doesnt specify the property : ";
	public static String WARN_INCORRECT_THREAD_DEFN		= "Warning: Incorrect threads number specified, setting to 1 : ";
	public static String ERROR_CREATING_DIR				= "Failed to create report dir, tried : ";
	public static String IMAGE_DIR_DOESNT_EXIST			= "Stored Image Directory doesnt exist.: ";
	public static String COMPARE_DIR_DOESN_EXIST			= "Compare Image Directory doesnt exist.: ";
}
