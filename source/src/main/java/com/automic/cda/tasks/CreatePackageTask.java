package com.automic.cda.tasks;

import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.bamboo.logger.ErrorUpdateHandler;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.automic.cda.configuration.PackageConfiguration;
import com.automic.cda.manager.CdaManager;

@Scanned
public class CreatePackageTask extends AbstractCdaTask {

	@Autowired
	public CreatePackageTask(@ComponentImport final ErrorUpdateHandler errorUpdateHandler) {

		super(errorUpdateHandler);
	}

	@Override
	protected void execute(CommonTaskContext taskContext, TaskResultBuilder resultBuilder, PackageConfiguration configuration, CdaManager manager) {
		manager.createPackage(taskContext, configuration, resultBuilder);

	}

}
