package com.automic.cda.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.credentials.CredentialsAccessor;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.automic.cda.constants.BambooConstants;
import com.automic.cda.modal.Artifact;
import com.automic.cda.modal.Condition;
import com.automic.cda.modal.PackageComponent;
import com.automic.cda.modal.PackageDynamicProperty;
import com.automic.cda.modal.PackageProperty;
import com.automic.cda.util.CommonUtil;

@Scanned
public class PackageConfigurator extends AbstractCdaConfigurator {


	 @Autowired
	public PackageConfigurator(@ComponentImport CredentialsAccessor credentialsAccessor) {
		super(credentialsAccessor);
	}

	@Override
	public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection) {
		super.validate(params, errorCollection);
		if (StringUtils.isBlank(CommonUtil.trimmedValue(params.getString(BambooConstants.APP_NAME))))
			errorCollection.addError(BambooConstants.APP_NAME, CDA_PROPERTY.getProperty("application.name.error"));

		if (StringUtils.isBlank(CommonUtil.trimmedValue(params.getString(BambooConstants.PKG_NAME))))
			errorCollection.addError(BambooConstants.PKG_NAME, CDA_PROPERTY.getProperty("package.name.error"));

		if (StringUtils.isBlank(CommonUtil.trimmedValue(params.getString(BambooConstants.PKG_TYPE))))
			errorCollection.addError(BambooConstants.PKG_TYPE, CDA_PROPERTY.getProperty("package.type.error"));

		if (StringUtils.isBlank(CommonUtil.trimmedValue(params.getString(BambooConstants.FOLDER_NAME))))
			errorCollection.addError(BambooConstants.FOLDER_NAME, CDA_PROPERTY.getProperty("folder.name.error"));

		int totalComponents = 0;
		int totalProperties = 0;
		int totalDynamicProperties = 0;

		for (String item : params.keySet()) {

			if (item.startsWith(BambooConstants.COMPONENT_NAME)) {

				totalComponents++;
			}
			if (item.startsWith(BambooConstants.PROPERTY_NAME)) {

				totalProperties++;
			}
			if (item.startsWith(BambooConstants.DYN_PROPERTY_NAME)) {

				totalDynamicProperties++;
			}
		}

		int componentIndex = 1;

		List<PackageComponent> components = new ArrayList<>();

		for (int i = 1; i <= totalComponents; i++) {

			PackageComponent component = new PackageComponent();
			while (!params.containsKey(BambooConstants.COMPONENT_NAME + componentIndex))
				componentIndex++;

			if (StringUtils.isBlank(params.getString(BambooConstants.COMPONENT_NAME + componentIndex))) {

				errorCollection.addError(BambooConstants.COMPONENT_NAME + i, "Component Name Error");
			}

			component.setName(params.getString(BambooConstants.COMPONENT_NAME + i));

			if (params.containsKey(BambooConstants.ARTIFACT_SRC + componentIndex)) {

				if (StringUtils.isBlank(params.getString(BambooConstants.ARTIFACT_SRC + componentIndex))) {

					errorCollection.addError(BambooConstants.ARTIFACT_SRC + componentIndex, "ARTIFACT_SRC");
				}

				if (StringUtils.isBlank(params.getString(BambooConstants.ARTIFACT_NAME + componentIndex))) {

					errorCollection.addError(BambooConstants.ARTIFACT_NAME + componentIndex, "ARTIFACT_NAME");
				}

				component.setArtifact(new Artifact(params.getString(BambooConstants.ARTIFACT_NAME + componentIndex),
						params.getString(BambooConstants.ARTIFACT_SRC + componentIndex),
						params.getString(BambooConstants.ARTIFACT_FIELD + componentIndex)));
			}

			List<Condition> conditions = new ArrayList<>();
			List<String> conditionsKeys = params.keySet().stream()
					.filter(item -> item.startsWith(BambooConstants.CONDITION_LOG)).collect(Collectors.toList());
			for (String key : conditionsKeys) {

				if (key.startsWith(BambooConstants.CONDITION_LOG + componentIndex + "_")) {

					conditions.add(new Condition(params.getString(key), params
							.getString(key.replace(BambooConstants.CONDITION_LOG, BambooConstants.CONDITION_VALUE))));
				}

			}
			component.setConditions(conditions);
			components.add(component);
			componentIndex++;
		}
		params.put(BambooConstants.COMPONENTS, components);

		List<PackageProperty> packageProperties = new ArrayList<>();
		int propertyIndex = 1;
		for (int i = 1; i <= totalProperties; i++) {

			while (!params.containsKey(BambooConstants.PROPERTY_NAME + propertyIndex))
				propertyIndex++;

			if (StringUtils.isBlank(params.getString(BambooConstants.PROPERTY_TYPE + propertyIndex))) {

				errorCollection.addError(BambooConstants.PROPERTY_TYPE + propertyIndex, "PROPERTY_TYPE");
			}
			if (StringUtils.isBlank(params.getString(BambooConstants.PROPERTY_NAME + propertyIndex))) {

				errorCollection.addError(BambooConstants.PROPERTY_NAME + propertyIndex, "PROPERTY_NAME");
			}
			packageProperties.add(new PackageProperty(
					params.getString(BambooConstants.PROPERTY_NAME + propertyIndex),
					params.getString(BambooConstants.PROPERTY_VALUE + propertyIndex),
					params.getString(BambooConstants.PROPERTY_TYPE + propertyIndex)));
			propertyIndex++;
		}
		params.put(BambooConstants.PROPERTIES, packageProperties);

		List<PackageDynamicProperty> dynamicProperties = new ArrayList<>();
		int dynamicPropertyIndex = 1;
		for (int i = 1; i <= totalDynamicProperties; i++) {

			while (!params.containsKey(BambooConstants.DYN_PROPERTY_NAME + dynamicPropertyIndex))
				dynamicPropertyIndex++;

			dynamicProperties.add(new PackageDynamicProperty(
					params.getString(BambooConstants.DYN_PROPERTY_NAME + dynamicPropertyIndex),
					params.getString(BambooConstants.DYN_PROPERTY_VALUE + dynamicPropertyIndex)));

			if (StringUtils.isBlank(params.getString(BambooConstants.DYN_PROPERTY_NAME + dynamicPropertyIndex))) {

				errorCollection.addError(BambooConstants.DYN_PROPERTY_NAME + dynamicPropertyIndex, "DYN_PROPERTY_NAME");
			}
			dynamicPropertyIndex++;
		}
		params.put(BambooConstants.DYNAMIC_PROPERTIES, dynamicProperties);
	
	
	}

	@Override
	public void populateContextForCreate(@NotNull final Map<String, Object> context) {
		super.populateContextForCreate(context);

		context.put(BambooConstants.COMPONENTS, new ArrayList<PackageComponent>());
		context.put(BambooConstants.PROPERTIES, new ArrayList<PackageProperty>());
		context.put(BambooConstants.DYNAMIC_PROPERTIES, new ArrayList<PackageDynamicProperty>());
		context.put("indexes", IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList()));

	}

	@Override
	public void populateContextForEdit(@NotNull final Map<String, Object> context,
			@NotNull final TaskDefinition taskDefinition) {

		super.populateContextForEdit(context, taskDefinition);
		Map<String, String> config = taskDefinition.getConfiguration();
		context.put(BambooConstants.PKG_NAME, config.get(BambooConstants.PKG_NAME));
		context.put(BambooConstants.PKG_TYPE, config.get(BambooConstants.PKG_TYPE));
		context.put(BambooConstants.APP_NAME, config.get(BambooConstants.APP_NAME));
		context.put(BambooConstants.FOLDER_NAME, config.get(BambooConstants.FOLDER_NAME));
		context.put(BambooConstants.OWNER_NAME, config.get(BambooConstants.OWNER_NAME));

		List<PackageComponent> components = new ArrayList<>();

		for (int i = 1;; i++) {

			if (!config.containsKey(BambooConstants.COMPONENT_NAME + i)) {

				break;
			}
			PackageComponent component = new PackageComponent();
			component.setName(config.get(BambooConstants.COMPONENT_NAME + i));

			String artifactName = config.get(BambooConstants.ARTIFACT_NAME + i);
			String artifactSource = config.get(BambooConstants.ARTIFACT_SRC + i);
			String artifactCustomField = config.get(BambooConstants.ARTIFACT_FIELD + i);

			if (StringUtils.isNotBlank(artifactName) || StringUtils.isNotBlank(artifactSource)
					|| StringUtils.isNotBlank(artifactCustomField)) {

				component.setArtifact(new Artifact(artifactName, artifactSource, artifactCustomField));
			}

			ArrayList<Condition> conditions = new ArrayList<>();

			int j = 1;
			while (config.containsKey(BambooConstants.CONDITION_LOG + i + "_" + j)) {

				String conditionBuildLog = config.get(BambooConstants.CONDITION_LOG + i + "_" + j);
				String conditionValue = config.get(BambooConstants.CONDITION_VALUE + i + "_" + j);
				if (StringUtils.isNotBlank(conditionBuildLog) || StringUtils.isNotBlank(conditionValue)) {
					conditions.add(new Condition(conditionBuildLog, conditionValue));
				}
				j++;
			}
			component.setConditions(conditions);

			components.add(component);
		}
		context.put(BambooConstants.COMPONENTS, components);

		List<PackageProperty> properties = getPackageProperty(config);
		context.put(BambooConstants.PROPERTIES, properties);

		List<PackageDynamicProperty> dynamicProperties = getDynamicProperties(config);
		context.put(BambooConstants.DYNAMIC_PROPERTIES, dynamicProperties);
	}

	/**
	 * Fetch Dynamic properties
	 * 
	 * @param config
	 * @return
	 */
	private List<PackageDynamicProperty> getDynamicProperties(Map<String, String> config) {
		List<PackageDynamicProperty> dynamicProperties = new ArrayList<>();
		for (int k = 1;; k++) {

			if (!config.containsKey(BambooConstants.DYN_PROPERTY_NAME + k)) {

				break;
			} else {

				String name = config.get(BambooConstants.DYN_PROPERTY_NAME + k);
				String value = config.get(BambooConstants.DYN_PROPERTY_VALUE + k);

				if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(value)) {

					dynamicProperties.add(new PackageDynamicProperty(name, value));
				}
			}
		}
		return dynamicProperties;
	}

	/**
	 * Fetch Package properties
	 * 
	 * @param config
	 * @return
	 */
	private List<PackageProperty> getPackageProperty(Map<String, String> config) {
		List<PackageProperty> properties = new ArrayList<>();
		for (int k = 1;; k++) {

			if (!config.containsKey(BambooConstants.PROPERTY_NAME + k)) {

				break;
			} else {

				String type = config.get(BambooConstants.PROPERTY_TYPE + k);
				String name = config.get(BambooConstants.PROPERTY_NAME + k);
				String value = config.get(BambooConstants.PROPERTY_VALUE + k);

				if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(value) || StringUtils.isNotBlank(type)) {

					properties.add(new PackageProperty(name, value, type));
				}
			}
		}
		return properties;
	}

	@NotNull
	@Override
	public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params,
			@Nullable final TaskDefinition previousTaskDefinition) {

		final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

		int totalComponents = 0;
		int totalProperties = 0;
		int totalDynamicProperties = 0;

		for (String item : params.keySet()) {

			if (item.startsWith(BambooConstants.COMPONENT_NAME)) {

				totalComponents++;
			}
			if (item.startsWith(BambooConstants.PROPERTY_NAME)) {

				totalProperties++;
			}
			if (item.startsWith(BambooConstants.DYN_PROPERTY_NAME)) {

				totalDynamicProperties++;
			}
		}
		config.put(BambooConstants.PKG_NAME, params.getString(BambooConstants.PKG_NAME));
		config.put(BambooConstants.PKG_TYPE, params.getString(BambooConstants.PKG_TYPE));
		config.put(BambooConstants.APP_NAME, params.getString(BambooConstants.APP_NAME));
		config.put(BambooConstants.OWNER_NAME, params.getString(BambooConstants.OWNER_NAME));
		config.put(BambooConstants.FOLDER_NAME, params.getString(BambooConstants.FOLDER_NAME));

		int componentIndex = 1;

		for (int i = 1; i <= totalComponents; i++) {

			while (!params.containsKey(BambooConstants.COMPONENT_NAME + componentIndex))
				componentIndex++;

			config.put(BambooConstants.COMPONENT_NAME + i,
					params.getString(BambooConstants.COMPONENT_NAME + componentIndex));
			config.put(BambooConstants.ARTIFACT_SRC + i,
					params.getString(BambooConstants.ARTIFACT_SRC + componentIndex));
			config.put(BambooConstants.ARTIFACT_NAME + i,
					params.getString(BambooConstants.ARTIFACT_NAME + componentIndex));
			config.put(BambooConstants.ARTIFACT_FIELD + i,
					params.getString(BambooConstants.ARTIFACT_FIELD + componentIndex));

			int conditionIndex = 1;

			for (String key : params.keySet()) {

				if (key.startsWith(BambooConstants.CONDITION_LOG + componentIndex + "_")) {

					config.put(BambooConstants.CONDITION_LOG + i + "_" + conditionIndex, params.getString(key));

					config.put(BambooConstants.CONDITION_VALUE + i + "_" + conditionIndex, params
							.getString(key.replace(BambooConstants.CONDITION_LOG, BambooConstants.CONDITION_VALUE)));

					conditionIndex++;
				}

			}
			componentIndex++;
		}

		int propertyIndex = 1;
		for (int i = 1; i <= totalProperties; i++) {

			while (!params.containsKey(BambooConstants.PROPERTY_NAME + propertyIndex))
				propertyIndex++;

			config.put(BambooConstants.PROPERTY_TYPE + i,
					params.getString(BambooConstants.PROPERTY_TYPE + propertyIndex));
			config.put(BambooConstants.PROPERTY_NAME + i,
					params.getString(BambooConstants.PROPERTY_NAME + propertyIndex));
			config.put(BambooConstants.PROPERTY_VALUE + i,
					params.getString(BambooConstants.PROPERTY_VALUE + propertyIndex));

			propertyIndex++;
		}

		int dynamicPropertyIndex = 1;
		for (int i = 1; i <= totalDynamicProperties; i++) {

			while (!params.containsKey(BambooConstants.DYN_PROPERTY_NAME + dynamicPropertyIndex))
				dynamicPropertyIndex++;

			config.put(BambooConstants.DYN_PROPERTY_NAME + i,
					params.getString(BambooConstants.DYN_PROPERTY_NAME + dynamicPropertyIndex));
			config.put(BambooConstants.DYN_PROPERTY_VALUE + i,
					params.getString(BambooConstants.DYN_PROPERTY_VALUE + dynamicPropertyIndex));

			dynamicPropertyIndex++;
		}
		return config;
	}

}
