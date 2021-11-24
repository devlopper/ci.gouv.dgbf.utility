package ci.gouv.dgbf.utility;

import org.cyk.utility.__kernel__.configuration.ConfigurationHelper;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.value.ValueHelper;

public interface Helper {

	static String getApiVariableName(String identifier,String part) {
		return String.format(VARIABLE_NAME_API_FORMAT, identifier,part);
	}
	
	static String getApiHostVariableName(String identifier) {
		return getApiVariableName(identifier, "host");
	}
	
	static String getApiPortVariableName(String identifier) {
		return getApiVariableName(identifier, "port");
	}
	
	static String getApiHost(String identifier,String defaultValue) {
		return ValueHelper.defaultToIfBlank(ConfigurationHelper.getValueAsString(getApiHostVariableName(identifier)),defaultValue);
	}
	
	static String getApiHost(String identifier) {
		return getApiHost(identifier, String.format(API_HOST_NAME_FORMAT, identifier));
	}
	
	static Short getApiPort(String identifier,String defaultValue) {
		return NumberHelper.get(Short.class,ValueHelper.defaultToIfBlank(ConfigurationHelper.getValueAsString(getApiPortVariableName(identifier)),defaultValue),Short.valueOf("0"));
	}
	
	static Short getApiPort(String identifier) {
		return getApiPort(identifier, DEFAULT_PORT);
	}
	
	String VARIABLE_NAME_API_FORMAT = "mic-%s-api-%s";
	String API_HOST_NAME_FORMAT = "mic-%s-api";
	String DEFAULT_PORT = "80";
}