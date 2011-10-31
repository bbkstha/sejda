/*
 * Created on 19/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.writer.model;

import org.sejda.core.writer.model.ImageWriter.ImageWriterBuilder;
import org.sejda.core.writer.model.XmlGraphicsMultipleOutputTiffImageWriterAdapter.XmlGraphicsMultipleOutputTiffImageWriterAdapterBuilder;
import org.sejda.core.writer.model.XmlGraphicsSingleOutputTiffImageWriterAdapter.XmlGraphicsSingleOutputTiffImageWriterAdapterBuilder;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;
import org.sejda.model.parameter.image.PdfToMultipleTiffParameters;
import org.sejda.model.parameter.image.PdfToSingleTiffParameters;

/**
 * {@link ImageWriterAbstractFactory} implementation returning {@link ImageWriter} XML Graphics implementations.
 * 
 * @author Andrea Vacondio
 * 
 */
class XmlGraphicsImageWriterFactory implements ImageWriterAbstractFactory {

    private static final ImageWriterBuildersRegistry BUILDERS_REGISTRY = new ImageWriterBuildersRegistry();

    static {
        BUILDERS_REGISTRY.addBuilder(PdfToMultipleTiffParameters.class,
                new XmlGraphicsMultipleOutputTiffImageWriterAdapterBuilder());
        BUILDERS_REGISTRY.addBuilder(PdfToSingleTiffParameters.class,
                new XmlGraphicsSingleOutputTiffImageWriterAdapterBuilder());
    }

    public <T extends AbstractPdfToImageParameters> ImageWriter<T> createImageWriter(T params) {
        ImageWriterBuilder<T> builder = BUILDERS_REGISTRY.getBuilder(params);
        if (builder != null) {
            return builder.build();
        }
        return null;
    }

}
