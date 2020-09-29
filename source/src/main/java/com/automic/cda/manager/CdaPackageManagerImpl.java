package com.automic.cda.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.atlassian.bamboo.build.LogEntry;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.logger.ErrorUpdateHandler;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.automic.cda.configuration.PackageConfiguration;
import com.automic.cda.constants.BambooConstants;
import com.automic.cda.modal.Condition;
import com.automic.cda.modal.PackageComponent;
import com.automic.cda.modal.PackageDynamicProperty;
import com.automic.cda.modal.PackageProperty;
import com.automic.cda.util.CommonUtil;
import com.automic.cda.util.PackageJsonUtil;
import com.automic.cda.util.RestServiceUtility;
import com.automic.cda.ws.rest.exceptions.BambooCdaException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class CdaPackageManagerImpl implements CdaManager {

	private final ErrorUpdateHandler errorUpdateHandler;

	public CdaPackageManagerImpl(final ErrorUpdateHandler errorUpdateHandler) {
		this.errorUpdateHandler = errorUpdateHandler;
	}

	@Override
	public void createPackage(CommonTaskContext taskContext, PackageConfiguration configuration,
			TaskResultBuilder taskResultBuilder) {
		final BuildLogger buildLogger = taskContext.getBuildLogger();

		String tempServer = CommonUtil.trimmedValue(configuration.getServer());
		
		 final Map<String, String> runtimeTaskContext = taskContext.getRuntimeTaskContext();
		        final String tempUser;
		         final String password;
		         if (runtimeTaskContext == null) {
		             tempUser = CommonUtil.trimmedValue(configuration.getUsername());
		             password = configuration.getPassword();
		         } else {
		             tempUser = CommonUtil.trimmedValue(runtimeTaskContext.getOrDefault(BambooConstants.CDA_USERNAME, configuration.getUsername()));
		             password = runtimeTaskContext.get(BambooConstants.CDA_PASSWRD);
		         }
		
		
		String tempPkgName = CommonUtil.trimmedValue(configuration.getPkgName());
		String tempPkgType = CommonUtil.trimmedValue(configuration.getPkgType());
		List<PackageComponent> tempComp = configuration.getComps();

		StringBuilder packageMessage = new StringBuilder();

		// log CDA entity information
		packageMessage.append("Server=").append(tempServer).append(" User=").append(tempUser);
		packageMessage.append(" Package=").append(tempPkgName).append(" Package type=").append(tempPkgType);
		packageMessage.append(" Application=").append(configuration.getAppName()).append(" Folder=")
				.append(configuration.getFolder());
		packageMessage.append(" Owner=").append(configuration.getOwner()).append(" Components=").append(tempComp);
		packageMessage.append(" Properties=").append(configuration.getProps()).append(" Dynamic Properties=")
				.append(configuration.getDynProps());

		buildLogger.addBuildLogEntry(packageMessage.toString());

		buildLogger.addBuildLogEntry("Creating CDA package " + tempPkgName + " of package type " + tempPkgType);

		try (RestServiceUtility ws = new RestServiceUtility(tempServer, tempUser, password)) {

			List<PackageComponent> listComp = null;
			if (tempComp != null && !tempComp.isEmpty()) {
				listComp = new ArrayList<>();
				Set<String> matchedConditions = evaluateConditions(buildLogger, tempComp);
				for (PackageComponent comp : tempComp) {
					if (CommonUtil.isNotEmpty(comp.getName()) && comp.evaluate(matchedConditions)) {
						listComp.add(comp);
					} else {
						buildLogger.addBuildLogEntry(
								String.format("Warn : Component [ %s ] will not be added to package [ %s ]",
										comp.getName(), tempPkgName));
					}
				}
			}

			JsonObject pkgJson = createPackageJson(listComp, configuration, buildLogger);

			buildLogger.addBuildLogEntry("JSON Submitted : " + pkgJson.toString());
			ws.createDeploymentPackage(pkgJson.toString());
		} catch (Exception e) {
			final String pkgError = "Error: Creation of package[" + tempPkgName + "] failed. Reason : "
					+ e.getMessage();
			reportError(taskContext, pkgError, e);
			taskResultBuilder.failedWithError();
			return;
		}

		buildLogger.addBuildLogEntry("CDA package " + tempPkgName + " created successfully");
		taskResultBuilder.success();
		return;

	}

	private JsonObject createPackageJson(List<PackageComponent> componentList, PackageConfiguration configuration,
			BuildLogger buildLogger) throws BambooCdaException {

		JsonObject pkgJsonObj = new JsonObject();
		String pkgName = configuration.getPkgName();
		if (CommonUtil.isNotEmpty(pkgName))
			pkgJsonObj.addProperty("name", pkgName);

		String folderName = configuration.getFolder();
		if (CommonUtil.isNotEmpty(folderName))
			pkgJsonObj.addProperty("folder", folderName);

		String ownerName = configuration.getOwner();
		if (CommonUtil.isNotEmpty(ownerName))
			pkgJsonObj.addProperty("owner", ownerName);

		// adding package properties
		List<PackageProperty> props = configuration.getProps();
		if (props != null) {
			JsonObject propJsonObj = PackageJsonUtil.createPkgPropertyJson(props, buildLogger);
			pkgJsonObj.add("custom", propJsonObj);
		}

		// adding dynamic properties
		List<PackageDynamicProperty> dynProps = configuration.getDynProps();
		if (dynProps != null) {
			JsonObject dynPropObj = PackageJsonUtil.createDynPropertyJson(dynProps, buildLogger);
			pkgJsonObj.add("dynamic", dynPropObj);
		}

		String application = configuration.getAppName();
		if (CommonUtil.isNotEmpty(application))
			pkgJsonObj.addProperty("application", application);

		String pkgType = configuration.getPkgType();
		if (CommonUtil.isNotEmpty(pkgType))
			pkgJsonObj.addProperty("custom_type", pkgType);

		// adding components
		if (componentList != null) {
			JsonArray compDataObj = new JsonArray();
			boolean artifactExists = false;
			for (PackageComponent component : componentList) {
				if (component.getArtifact() != null) 
					artifactExists = true;
			}
			for (PackageComponent component : componentList) {
				if(!artifactExists){
					if(CommonUtil.isNotEmpty(component.getName()))
						compDataObj.add(new JsonPrimitive(component.getName()));
				} else {
					JsonObject compJson = PackageJsonUtil.createArtifactJson(component);
					compDataObj.add(compJson);
				}
			}
			pkgJsonObj.add("components", compDataObj);
		}

		return pkgJsonObj;
	}

	/**
	 * This method evaluates all the user provided conditions and returns the
	 * conditions which we found in the build log irrespective of
	 * contains/doesn't contain.
	 */
	private Set<String> evaluateConditions(BuildLogger buildLogger, List<PackageComponent> comps) {
		Set<String> matchedconditionSet = new HashSet<>();
		Set<String> conditionSet = new HashSet<>();
		Map<String, Pattern> conditionMap = new HashMap<>();
		for (PackageComponent c : comps) {
			for (Condition condition : c.getConditions()) {
				String value = condition.getValue();
				if (CommonUtil.isNotEmpty(value)) {
					Pattern pattern;
					try {
						pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
					} catch (Exception e) {
						pattern = Pattern.compile(Pattern.quote(value), Pattern.CASE_INSENSITIVE);
					}
					value = value.toLowerCase();
					conditionMap.put(value, pattern);
					conditionSet.add(value);
				}
			}
		}

		List<LogEntry> logEntries = buildLogger.getBuildLog();
		for (LogEntry logEntry : logEntries) {
			String line = logEntry.getLog();
			for (Iterator<String> itr = conditionSet.iterator(); itr.hasNext();) {
				String val = itr.next();
				if (conditionMap.get(val).matcher(line).find()) {
					matchedconditionSet.add(val);
					conditionMap.remove(val);
					itr.remove();
				}
			}
		}
		return matchedconditionSet;
	}

	protected void reportError(@NotNull CommonTaskContext taskContext, @NotNull String message,
			@Nullable Throwable throwable) {
		final BuildLogger buildLogger = taskContext.getBuildLogger();
		final StringBuilder errorMessageBuilder = new StringBuilder(message);
		if (throwable != null) {
			errorMessageBuilder.append(". ").append(throwable.getMessage());
		}
		final String errorMessage = errorMessageBuilder.toString();
		buildLogger.addErrorLogEntry(errorMessage);
		errorUpdateHandler.recordError(taskContext.getCommonContext().getResultKey(), errorMessage, throwable);
	}

}