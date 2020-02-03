package org.chusete81.poc_cc.connectors.monitoring;

public class ConnectorMonitoring {
	public static final String CONN_INCOMING_MSG           = "C11-INCOMING_MESSAGE" + " %s %s %s";
	public static final String CONN_CONNECTOR_RESPONSE_MSG = "C12-RESPONSE_MESSAGE" + " %s %s";
	public static final String CONN_PLATFORM_TIMEOUT_ERROR = "C19-PLATFORM_TIMEOUT" + " %s %s";
	public static final String CONN_BACKEND_REQUEST        = "C21-BACKEND_REQUEST"  + " %s %s";
	public static final String CONN_BACKEND_RESPONSE       = "C22-BACKEND_RESPONSE" + " %s %s %s";
	public static final String CONN_BACKEND_ERROR          = "C28-BACKEND_ERROR"    + " %s %s";
	public static final String CONN_BACKEND_TIMEOUT_ERROR  = "C29-BACKEND_TIMEOUT"  + " %s %s";
}
