/*
 * Created on 04 feb 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.io.File;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PageTreeRebuilder;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.RepairParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of a task that tries to repair and recover data from a damaged PDF file
 * 
 * @author Andrea Vacondio
 *
 */
public class RepairTask extends BaseTask<RepairParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(CropTask.class);

    private int totalSteps;
    private PDDocumentHandler documentHandler = null;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private MultipleOutputWriter outputWriter;

    @Override
    public void before(RepairParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(RepairParameters parameters) throws TaskException {
        int currentStep = 0;

        for (PdfSource<?> source : parameters.getSourceList()) {
            executionContext().assertTaskNotCancelled();
            currentStep++;
            LOG.debug("Opening {}", source);
            try {
                documentHandler = source.open(documentLoader);
                documentHandler.setCreatorOnPDDocument();

                File tmpFile = createTemporaryPdfBuffer();
                LOG.debug("Created output on temporary buffer {}", tmpFile);
                new PageTreeRebuilder(documentHandler.getUnderlyingPDDocument()).rebuild();
                if (documentHandler.getPages().stream().count() == 0) {
                    TaskExecutionException failure = new TaskExecutionException(
                            String.format("Unable to repair and recover any page from %s", source.getName()));
                    executionContext().assertTaskIsLenient(failure);
                    notifyEvent(executionContext().notifiableTaskMetadata()).taskWarning(failure.getMessage());
                }

                documentHandler.setCompress(parameters.isCompress());
                documentHandler.savePDDocument(tmpFile);

                String outName = nameGenerator(parameters.getOutputPrefix())
                        .generate(nameRequest().originalName(source.getName()).fileNumber(currentStep));
                outputWriter.addOutput(file(tmpFile).name(outName));
            } finally {
                nullSafeCloseQuietly(documentHandler);
            }

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents recovered and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }

}
