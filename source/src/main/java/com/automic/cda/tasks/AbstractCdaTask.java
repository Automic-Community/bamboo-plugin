package com.automic.cda.tasks;

import org.jetbrains.annotations.NotNull;

import com.atlassian.bamboo.logger.ErrorUpdateHandler;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.CommonTaskType;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.automic.cda.configuration.PackageConfiguration;
import com.automic.cda.manager.CdaManager;
import com.automic.cda.manager.CdaPackageManagerImpl;

public abstract class AbstractCdaTask implements CommonTaskType
{
	
	    private final ErrorUpdateHandler errorUpdateHandler;

	    protected AbstractCdaTask(final ErrorUpdateHandler errorUpdateHandler)
	    {
	        this.errorUpdateHandler = errorUpdateHandler;
	    }

	    @Override
	    public TaskResult execute(@NotNull CommonTaskContext taskContext) throws TaskException
	    {
	        final TaskResultBuilder resultBuilder = TaskResultBuilder.newBuilder(taskContext);
	        final PackageConfiguration configuration = new PackageConfiguration(taskContext);
	        final CdaManager manager = new CdaPackageManagerImpl(errorUpdateHandler);
	        execute(taskContext, resultBuilder, configuration, manager);
	        return resultBuilder.build();
	    }

	    protected abstract void execute(@NotNull CommonTaskContext taskContext, @NotNull TaskResultBuilder resultBuilder, @NotNull PackageConfiguration configuration, @NotNull CdaManager controller);


}
