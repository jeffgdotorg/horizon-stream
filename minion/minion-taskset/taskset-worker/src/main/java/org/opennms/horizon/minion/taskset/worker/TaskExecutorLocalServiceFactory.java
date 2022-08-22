package org.opennms.horizon.minion.taskset.worker;

import org.opennms.taskset.model.TaskDefinition;

public interface TaskExecutorLocalServiceFactory {
    TaskExecutorLocalService create(TaskDefinition taskDefinition);
}