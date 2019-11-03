package de.alpharogroup.spring.batch.cvs2db.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BundleApplicationsStepExecutionListener implements StepExecutionListener
{
	@Override public void beforeStep(StepExecution stepExecution)
	{
		log.info(stepExecution.getStepName());
	}

	@Override public ExitStatus afterStep(StepExecution stepExecution)
	{
		return stepExecution.getExitStatus();
	}
}
