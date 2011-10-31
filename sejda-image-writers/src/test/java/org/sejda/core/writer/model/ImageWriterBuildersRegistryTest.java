/*
 * Created on 24/set/2011
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.writer.model.ImageWriter.ImageWriterBuilder;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.parameter.image.PdfToMultipleTiffParameters;
import org.sejda.model.parameter.image.PdfToSingleTiffParameters;

/**
 * @author Andrea Vacondio
 * 
 */
public class ImageWriterBuildersRegistryTest {

    private ImageWriterBuildersRegistry victim;

    @Before
    public void setUp() {
        victim = new ImageWriterBuildersRegistry();
    }

    @Test
    public void testRegistry() {
        @SuppressWarnings("unchecked")
        ImageWriterBuilder<PdfToMultipleTiffParameters> builder = mock(ImageWriterBuilder.class);
        victim.addBuilder(PdfToMultipleTiffParameters.class, builder);
        PdfToMultipleTiffParameters params = new PdfToMultipleTiffParameters(ImageColorType.BLACK_AND_WHITE);
        assertNotNull(victim.getBuilder(params));
        PdfToSingleTiffParameters notAddedParams = new PdfToSingleTiffParameters(ImageColorType.BLACK_AND_WHITE);
        assertNull(victim.getBuilder(notAddedParams));
    }
}
