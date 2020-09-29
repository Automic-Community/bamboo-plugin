package com.automic.cda.manager;

import org.jetbrains.annotations.NotNull;

import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.automic.cda.configuration.PackageConfiguration;

public interface CdaManager {

	 /**
     * Create a package
     */
    @NotNull
    void createPackage(@NotNull CommonTaskContext taskContext, @NotNull PackageConfiguration configuration, @NotNull TaskResultBuilder resultBuilder);

}
