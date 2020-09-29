package com.automic.cda.util;

import java.util.List;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.automic.cda.modal.CDAPropertyType;
import com.automic.cda.modal.PackageComponent;
import com.automic.cda.modal.PackageDynamicProperty;
import com.automic.cda.modal.PackageProperty;
import com.automic.cda.ws.rest.exceptions.BambooCdaException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PackageJsonUtil {

	private PackageJsonUtil() {
	}

	public static JsonObject createArtifactJson(PackageComponent component) {

		JsonObject compJson = new JsonObject();

		if (CommonUtil.isNotEmpty(component.getName())) {
			JsonObject customJson = new JsonObject();
			customJson.addProperty("name", component.getName());
			compJson.add("component", customJson);
		}
		if (component.getArtifact() != null) {

			JsonObject createArtifactReqJson = new JsonObject();
			createArtifactReqJson.addProperty("name", component.getArtifact().getName());

			if (CommonUtil.isNotEmpty(component.getArtifact().getSource()))
				createArtifactReqJson.addProperty("artifact_source", component.getArtifact().getSource());

			if (CommonUtil.isNotEmpty(component.getArtifact().getCustomField().trim())) {
				JsonObject artifactCustomJson = new JsonParser().parse(component.getArtifact().getCustomField())
						.getAsJsonObject();

				createArtifactReqJson.add("custom", artifactCustomJson);
			}
			compJson.add("artifact", createArtifactReqJson);

		}
		return compJson;
	}

	public static JsonObject createPkgPropertyJson(List<PackageProperty> props, BuildLogger buildLogger)
			throws BambooCdaException {
		JsonObject propJsonObj = new JsonObject();
		if (props != null) {
			for (PackageProperty prop : props) {
				String propName = prop.getName();
				if (CommonUtil.isNotEmpty(propName)) {
					String propValue = prop.getValue();
					String type = prop.getType();

					buildLogger.addBuildLogEntry("Set Property: Name=" + propName + ", Value= " + propValue + ", Type=" + type);
					if (CDAPropertyType.Link.toString().equals(type)) {
						String[] tmp = propValue.split(" ", 2);
						if (tmp.length<=1) {
							throw new BambooCdaException(String.format("Invalid link property: %s", propValue));
						}
						String title = tmp[1];
						String url = tmp[0];
						JsonObject tmpObj = new JsonObject();
						tmpObj.addProperty("title", title);
						tmpObj.addProperty("url", url);
						propJsonObj.add(propName, tmpObj);

					} else {
						propJsonObj.addProperty(propName, propValue);
					}
				} else {
					buildLogger.addBuildLogEntry("Skipping Property: Name=" + propName + ", Value= " + prop.getValue());
				}
			}
		}
		return propJsonObj;
	}

	public static JsonObject createDynPropertyJson(List<PackageDynamicProperty> dynProps, BuildLogger buildLogger) {
		JsonObject dynPropObj = new JsonObject();

		if (dynProps != null) {
			for (PackageDynamicProperty dynProp : dynProps) {
				String dynPropName = dynProp.getName();
				if (CommonUtil.isNotEmpty(dynPropName)) {
					String propValue = dynProp.getValue();
					buildLogger.addBuildLogEntry("Set DynamicProperty: Name=" + dynPropName + ", Value=" + propValue);
					dynPropObj.addProperty(dynPropName, propValue);
				} else {
					buildLogger.addBuildLogEntry(
							"Skipping DynamicProperty: Name=" + dynPropName + ", Value=" + dynProp.getValue());
				}
			}
		}
		return dynPropObj;
	}
}
