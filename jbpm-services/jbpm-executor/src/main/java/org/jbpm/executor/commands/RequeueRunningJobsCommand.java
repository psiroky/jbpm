/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.executor.commands;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.RequeueAware;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple command to log the contextual data and return empty results. After attempting to get BeanManager
 * and creating simple CDI bean based on given class name as parameter.
 * Just for demo purpose.
 *
 */
public class RequeueRunningJobsCommand implements Command{

    private static final Logger logger = LoggerFactory.getLogger(RequeueRunningJobsCommand.class);

    public ExecutionResults execute(CommandContext ctx) {


        Long olderThan = (Long) ctx.getData("MaxRunningTime");
        Long requestId = (Long) ctx.getData("RequestId");
        try {
            ExecutorService executorService = ExecutorServiceFactory.newExecutorService(null);
            if (executorService instanceof RequeueAware) {

                if (requestId != null) {
                    logger.info("Requeue jobs by id {}", requestId);
                    ((RequeueAware)executorService).requeueById(requestId);
                } else {
                    logger.info("Requeue jobs older than {}", olderThan);
                    ((RequeueAware)executorService).requeue(olderThan);
                }
            } else {
                logger.info("Executor Service is not capable of jobs requeue");
            }

        } catch (Exception e) {
            logger.error("Error while creating CDI bean from jbpm executor", e);
        }

        logger.info("Command executed on executor with data {}", ctx.getData());
        ExecutionResults executionResults = new ExecutionResults();
        return executionResults;
    }


}
