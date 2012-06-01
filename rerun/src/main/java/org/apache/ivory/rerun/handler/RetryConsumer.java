/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ivory.rerun.handler;

import java.util.Date;

import org.apache.ivory.entity.EntityUtil;
import org.apache.ivory.rerun.event.RetryEvent;
import org.apache.ivory.rerun.queue.DelayedQueue;
import org.apache.ivory.util.GenericAlert;
import org.apache.ivory.util.StartupProperties;

public class RetryConsumer<T extends RetryHandler<DelayedQueue<RetryEvent>>>
		extends AbstractRerunConsumer<RetryEvent, T> {

	public RetryConsumer(T handler) {
		super(handler);
	}

	@Override
	protected void handleRerun(String jobStatus, RetryEvent message) {
		try {
			if (!jobStatus.equals("KILLED")) {
				LOG.debug("Re-enqueing message in RetryHandler for workflow with same delay as job status is running:"
						+ message.getWfId());
				message.setMsgInsertTime(System.currentTimeMillis());
				handler.offerToQueue(message);
				return;
			}
			LOG.info("Retrying attempt:"
					+ (message.getRunId() + 1)
					+ " out of configured: "
					+ message.getAttempts()
					+ " attempt for process instance::"
					+ message.getProcessName()
					+ ":"
					+ message.getProcessInstance()
					+ " And WorkflowId: "
					+ message.getWfId()
					+ " At time: "
					+ EntityUtil.formatDateUTC(new Date(System
							.currentTimeMillis())));
			message.getWfEngine().reRun(message.getClusterName(),
					message.getWfId(), null);
		} catch (Exception e) {
			int maxFailRetryCount = Integer.parseInt(StartupProperties.get()
					.getProperty("max.retry.failure.count", "1"));
			if (message.getFailRetryCount() < maxFailRetryCount) {
				LOG.warn(
						"Retrying again for process instance "
								+ message.getProcessName() + ":"
								+ message.getProcessInstance() + " after "
								+ message.getDelayInMilliSec()
								+ " seconds as Retry failed with message:", e);
				message.setFailRetryCount(message.getFailRetryCount() + 1);
				try {
					handler.offerToQueue(message);
				} catch (Exception ex) {
					LOG.error("Unable to re-offer to queue:", ex);
					GenericAlert.alertRetryFailed(message.getProcessName(),
							message.getProcessInstance(), message.getRunId(),
							ex.getMessage());
				}
			} else {
				LOG.warn(
						"Failure retry attempts exhausted for processInstance: "
								+ message.getProcessName() + ":"
								+ message.getProcessInstance(), e);
				GenericAlert.alertRetryFailed(message.getProcessName(),
						message.getProcessInstance(), message.getRunId(),
						e.getMessage());
			}

		}

	}

}
