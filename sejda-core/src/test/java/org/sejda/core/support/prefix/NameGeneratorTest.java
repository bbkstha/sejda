/*
 * Created on 03/lug/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.prefix;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * Test unit for the NameGenerator
 * 
 * @author Andrea Vacondio
 * 
 */
public class NameGeneratorTest {

    @Test
    public void testFullComplexPrefix() {
        String prefix = "BLA_[CURRENTPAGE###]_[BASENAME]";
        String originalName = "Original";
        String expected = "BLA_002_Original.pdf";
        assertEquals(expected,
                nameGenerator(prefix).generate(nameRequest().page(Integer.valueOf("2")).originalName(originalName)));
    }

    @Test
    public void testSimplePrefix() {
        String prefix = "BLA_";
        String originalName = "Original";
        String expected = "BLA_Original.pdf";
        assertEquals(expected, nameGenerator(prefix).generate(nameRequest().originalName(originalName)));
    }

    @Test
    public void testSimplePrefixWithPage() {
        String prefix = "BLA_";
        String originalName = "Original";
        String expected = "1_BLA_Original.pdf";
        assertEquals(expected,
                nameGenerator(prefix).generate(nameRequest().page(Integer.valueOf(1)).originalName(originalName)));
    }

    @Test
    public void testComplexPrefixNoSubstitution() {
        String prefix = "BLA_[CURRENTPAGE###]_[BASENAME]";
        String originalName = "Original";
        String expected = "BLA_[CURRENTPAGE###]_Original.pdf";
        assertEquals(expected, nameGenerator(prefix).generate(nameRequest().originalName(originalName)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullRequest() {
        String prefix = "BLA_";
        nameGenerator(prefix).generate(null);
    }

    @Test
    public void testMaxFilenameSize() {
        String generatedFilename = nameGenerator("BLA_[TEXT]").generate(nameRequest("pdf").text(repeat('a', 300)));
        assertThat(generatedFilename.length(), is(lessThan(256)));
        assertThat(generatedFilename, endsWith("aaa.pdf"));
    }

    @Test
    public void testMaxFilenameSizeUnicode() {
        String generatedFilename = nameGenerator("compressed_").generate(nameRequest("pdf").originalName(repeat("aว", 300)));
        assertThat(generatedFilename.length(), is(lessThan(256)));
        assertThat(generatedFilename, endsWith(".pdf"));
        assertThat(generatedFilename, startsWith("compressed_aวaว"));
    }

    @Test
    public void testMaxFilenameSizeSanitized() {
        String generatedFilename = nameGenerator("B|LA_[TEXT]").generate(nameRequest("pdf").text(repeat('a', 300)));
        assertThat(generatedFilename.length(), is(lessThan(256)));
        assertThat(generatedFilename, endsWith("aaa.pdf"));
        assertThat(generatedFilename, startsWith("BLA_aaa"));
    }

    @Test
    public void testInvalidCharacters() {
        String generatedFilename = nameGenerator("Invalid_\\").generate(nameRequest("pdf"));
        assertEquals(generatedFilename, "Invalid_.pdf");
    }

    @Test
    public void testDollarSignInFilename() {
        String generatedFilename = nameGenerator("[CURRENTPAGE]-[BASENAME]")
                .generate(nameRequest("pdf").page(99).originalName("My file 6-04-2015 $1234-56"));
        assertEquals(generatedFilename, "99-My file 6-04-2015 $1234-56.pdf");
    }

    @Test
    public void testSuffix() {
        String generatedFilename = nameGenerator("[BASENAME]_suffix")
                .generate(nameRequest("pdf").originalName("My file"));
        assertEquals(generatedFilename, "My file_suffix.pdf");
    }

    @Test
    public void testPrefixSuffix() {
        String generatedFilename = nameGenerator("prefix_[BASENAME]_suffix")
                .generate(nameRequest("pdf").originalName("My file"));
        assertEquals(generatedFilename, "prefix_My file_suffix.pdf");
    }

    // convenience method, don't pass charset and len all the time
    private String shortenFilenameBytesLength(String input) {
        return NameGenerator.shortenFilenameBytesLength(input, 255, StandardCharsets.UTF_8);
    }

    private String largeFilename = repeat("aว", 300) + ".pdf";

    @Test
    public void shortenFilenameBytesLength() {
        String shorter = shortenFilenameBytesLength(largeFilename);
        assertThat(shorter, endsWith("aวaวa.pdf"));
        assertThat(shorter, startsWith("aวaว"));
        assertThat(shorter.getBytes(StandardCharsets.UTF_8).length, is(253));
    }

    @Test
    public void shortenFilenameCharLength() {
        String shorter = NameGenerator.shortenFilenameCharLength(largeFilename, 255);
        assertThat(shorter, endsWith("aวaวa.pdf"));
        assertThat(shorter, startsWith("aวaว"));
        assertThat(shorter.length(), is(255));
    }
}
