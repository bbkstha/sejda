/*
 * Copyright 2017 by Eduard Weissmann (edi.weissmann@gmail.com).
 *
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.output;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.exception.TaskOutputVisitException;

import java.io.File;
import java.io.IOException;

public class FileOrDirectoryTaskOutput implements SingleOrMultipleTaskOutput {

    private File file;

    public FileOrDirectoryTaskOutput(File file) {
        if (file == null) {
            throw new IllegalArgumentException("A not null file or directory instance is expected. Path: " + file);
        }
        this.file = file;
    }

    @Override
    public File getDestination() {
        return file;
    }

    @Override
    public void accept(TaskOutputDispatcher writer) throws TaskOutputVisitException {
        try {
            writer.dispatch(this);
        } catch (IOException e) {
            throw new TaskOutputVisitException("Exception dispatching the file task output.", e);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(file).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(file).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FileOrDirectoryTaskOutput)) {
            return false;
        }
        FileOrDirectoryTaskOutput output = (FileOrDirectoryTaskOutput) other;
        return new EqualsBuilder().append(file, output.file).isEquals();
    }

}