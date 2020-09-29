package com.automic.cda.configuration;

import static com.atlassian.bamboo.credentials.UsernamePasswordCredentialType.CFG_PASSWORD;
import static com.atlassian.bamboo.credentials.UsernamePasswordCredentialType.CFG_USERNAME;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.atlassian.bamboo.credentials.CredentialsAccessor;
import com.atlassian.bamboo.credentials.CredentialsData;
import com.atlassian.bamboo.task.RuntimeTaskDataProvider;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.task.runtime.RuntimeTaskDefinition;
import com.atlassian.bamboo.v2.build.CommonContext;
import com.automic.cda.constants.BambooConstants;

public class SharedCredentialsRuntimeDataProvider implements RuntimeTaskDataProvider {
	@Inject
	private CredentialsAccessor credentialsAccessor;

	@NotNull
	@Override
	public Map<String, String> populateRuntimeTaskData(@NotNull TaskDefinition taskDefinition,
			@NotNull CommonContext commonContext) {
		final Map<String, String> configuration = taskDefinition.getConfiguration();
		final Map<String, String> result = new HashMap<>();
		final String credentialsId = configuration.get(AbstractCdaConfigurator.SHARED_CREDENTIALS_ID);
		if (credentialsId != null) {
			final CredentialsData credentials = credentialsAccessor.getCredentials(Long.parseLong(credentialsId));
			if (credentials == null) {
				throw new IllegalStateException("Can't find shared credentails with id " + credentialsId + " for task "
						+ (StringUtils.isEmpty(taskDefinition.getUserDescription()) ? taskDefinition.getPluginKey()
								: taskDefinition.getUserDescription()));
			}

				result.put(BambooConstants.CDA_USERNAME, credentials.getConfiguration().get(CFG_USERNAME));
				result.put(BambooConstants.CDA_PASSWRD, credentials.getConfiguration().get(CFG_PASSWORD));
		} else {
			if (StringUtils.isNotEmpty(configuration.get(BambooConstants.CDA_PASSWRD))) {
				result.put(BambooConstants.CDA_PASSWRD,configuration.get(BambooConstants.CDA_PASSWRD));
			}
		}
		return result;
	}

	@Override
	public void processRuntimeTaskData(@NotNull RuntimeTaskDefinition runtimeTaskDefinition, @NotNull CommonContext commonContext){
	}

	public void setCredentialsAccessor(CredentialsAccessor credentialsAccessor) {
		this.credentialsAccessor = credentialsAccessor;
	}

}
