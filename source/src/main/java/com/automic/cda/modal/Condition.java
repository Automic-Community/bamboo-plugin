package com.automic.cda.modal;

import java.util.Set;

import com.automic.cda.constants.BambooConstants;
import com.automic.cda.util.CommonUtil;

public class Condition {

	private String buildLog;
	private String value;

	public Condition() {

	}

	public Condition(String buildLog, String values) {

		this.buildLog = buildLog;
		this.value = values;
	}

	public String getBuildLog() {
		return buildLog;
	}

	public void setBuildLog(String buildLog) {
		this.buildLog = buildLog;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Condition [buildLog=" + buildLog + ", value=" + value + "]";
	}

	 public boolean evaluate(Set<String> data) {
	        String text = getValue();
	        if (!CommonUtil.isNotEmpty(text)) {
	            return true;
	        }

	        text = text.toLowerCase();
	        boolean match = data.contains(text);
	        return BambooConstants.COMPARATOR_CONTAIN.equalsIgnoreCase(getBuildLog()) ? match : !match;
	    }
	
}
