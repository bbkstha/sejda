/*
 * Created on 19/giu/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.support.io;

import org.sejda.core.support.io.model.PopulatedFileOutput;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.output.TaskOutput;

/**
 * DSL interface to expose methods a multiple output task (tasks generating multiple files as output) needs to write its output.
 * 
 * <pre>
 * {@code
 * MultipleOutputSupport outputWriter = ....
 * outputWriter.addOutput(file(tmpFile).name("newName"));
 * ....
 * AbstractPdfOutput output = ...
 * boolean overwrite = ...
 * outputWriter.flushOutputs(output, overwrite);
 * }
 * </pre>
 * 
 * @author Andrea Vacondio
 * 
 */
public interface MultipleOutputWriter {

    /**
     * flush of the multiple outputs added to the output destination. Once flushed they are deleted and the collection emptied.
     * 
     * @param output
     *            manipulation output parameter where multiple outputs will be written.
     * @param overwrite
     *            true if the output should be overwritten if already exists
     * @throws TaskIOException
     *             in case of error
     */
    void flushOutputs(TaskOutput output, boolean overwrite) throws TaskIOException;

    /**
     * Adds the given file output (typically a temporary file) to the collection of multiple outputs ready to be flushed.
     * 
     * @param fileOutput
     */
    void addOutput(PopulatedFileOutput fileOutput);
}
