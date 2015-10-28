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
	public static String COMPARE_DIR_DOESN_EXIST		= "Compare Image Directory doesnt exist.: ";
	public static String FAILED_TO_READ_FILE			= "Failed to load/read file : ";
	public static String TESTSTEP_SYNTAX_ERROR			= "Syntax error : Command is not specified. : Line : ";
	public static String TESTSTEP_SYNTAX_ERROR_SS_DESC  = "Syntax Error expected description post keyword for takeSnapShot, Line : ";
	public static String TESTSTEP_SYNTAX_ERROR_SS_FILE  = "Syntax Error expected fileName as second parameter for keyword takeSnapShot, Line : ";
	public static String TESTSTEP_SYNTAX_ERROR_SS_INC  	= "Syntax Error expected description post keyword for takeSnapShot, Line : ";
	public static String TESTSTEP_SYNTAX_ERROR_SS_RDEF  = "Syntax Error, Expected region definition, Line : ";
	public static String TESTSTEP_SYNTAX_ERROR_SS_REGF	= "Syntax Error in specifying region format [ xcord x ycord x width x height : xcord x ycord x width x height : ...], Line : ";
	public static String ERROR_EXEC_TEST_SUITE			= "Error executing Test Suite : ";
	public static String ERROR_COMPARING_IMAGES			= "Failed to compare images : ";
	public static String WARN_COPYING_IMAGE				= "Warning : could not copy image file from : ";
	public static String ERROR_WRITING_RESULT			= "Failed to write report file : ";
	public static String FAILED_LOADING_CONFIG_FILE		= "Failed to load config file : ";
	public static String INVALID_REPORT_TEMPL_FILE		= "Failed to find the report template file specified : ";
}
