/*
 * Copyright 2007 Kasper B. Graversen
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.supercsv.cellprocessor.joda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.supercsv.cellprocessor.joda.SuperCsvTestUtils.ANONYMOUS_CSVCONTEXT;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.mock.IdentityTransform;
import org.supercsv.exception.SuperCsvCellProcessorException;

/**
 * Tests the ParseLocalTime cell processor.
 */
public class ParseLocalTimeTest {

	private static final String LOCAL_TIME_STRING = "01:02:03";
	private static final LocalTime LOCAL_TIME = new LocalTime(1, 2, 3, 0);

	private ParseLocalTime processor1;
	private ParseLocalTime processor2;
	private ParseLocalTime processorChain1;
	private ParseLocalTime processorChain2;
	private List<ParseLocalTime> processors;
	private DateTimeFormatter formatter;

	@Before
	public void setUp() {
		formatter = ISODateTimeFormat.localTimeParser();
		processor1 = new ParseLocalTime();
		processor2 = new ParseLocalTime(formatter);
		processorChain1 = new ParseLocalTime(new IdentityTransform());
		processorChain2 = new ParseLocalTime(formatter, new IdentityTransform());
		processors = Arrays.asList(processor1, processor2, processorChain1,
				processorChain2);
	}

	@Test
	public void testValidLocalTime() {
		for (CellProcessor p : processors) {
			assertEquals(LOCAL_TIME,
					p.execute(LOCAL_TIME_STRING, ANONYMOUS_CSVCONTEXT));
		}
	}

	@Test
	public void testNullInput() {
		for (CellProcessor p : processors) {
			try {
				p.execute(null, ANONYMOUS_CSVCONTEXT);
				fail("expecting SuperCsvCellProcessorException");
			} catch (SuperCsvCellProcessorException e) {
				assertEquals(
						"this processor does not accept null input - "
								+ "if the column is optional then chain an Optional() processor before this one",
						e.getMessage());
			}
		}
	}

	@Test
	public void testNonStringInput() {
		for (CellProcessor p : processors) {
			try {
				p.execute(123, ANONYMOUS_CSVCONTEXT);
				fail("expecting SuperCsvCellProcessorException");
			} catch (SuperCsvCellProcessorException e) {
				assertEquals(
						"the input value should be of type java.lang.String but is java.lang.Integer",
						e.getMessage());
			}
		}
	}

	@Test
	public void testUnparsableString() {
		for (CellProcessor p : processors) {
			try {
				p.execute("not valid", ANONYMOUS_CSVCONTEXT);
				fail("expecting SuperCsvCellProcessorException");
			} catch (SuperCsvCellProcessorException e) {
				assertEquals("Failed to parse value", e.getMessage());
			}
		}
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor2WithNullNext() {
		new ParseLocalTime((CellProcessor) null);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor3WithNullFormatter() {
		new ParseLocalTime((DateTimeFormatter) null);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor4WithNullFormatter() {
		new ParseLocalTime((DateTimeFormatter) null, new IdentityTransform());
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor4WithNullNext() {
		new ParseLocalTime(formatter, null);
	}

}
