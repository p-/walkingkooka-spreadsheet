/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionReferenceTest implements ClassTesting2<SpreadsheetExpressionReference>,
        ParseStringTesting<SpreadsheetExpressionReference> {

    @Test
    public void testIsTextCellReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetExpressionReference.isTextCellReference(null);
        });
    }

    @Test
    public void testIsTextCellEmptyFalse() {
        this.isTextCellReferenceAndCheck("", false);
    }

    @Test
    public void testIsTextCellWithCellReference() {
        this.isTextCellReferenceAndCheck("A1", true);
    }

    @Test
    public void testIsTextCellWithLabel() {
        this.isTextCellReferenceAndCheck("LABEL123", false);
    }

    private void isTextCellReferenceAndCheck(final String text, final boolean expected) {
        assertEquals(expected,
                SpreadsheetExpressionReference.isTextCellReference(text),
                () -> "isTextCellReference " + CharSequences.quoteAndEscape(text));
        if (expected) {
            SpreadsheetExpressionReference.parse(text);
        }
    }

    // cellReference....................................................................................................

    @Test
    public void testCellReference() {
        assertEquals(SpreadsheetExpressionReference.parseCellReference("A1"),
                SpreadsheetExpressionReference.cellReference(SpreadsheetColumnReference.parse("A"),
                        SpreadsheetRowReference.parse("1")));
    }

    // fromJsonNode.....................................................................................................

    @Test
    public void testFromJsonNodeWithCellReference() {
        final String reference = "A1";
        assertEquals(SpreadsheetExpressionReference.parseCellReference(reference),
                SpreadsheetExpressionReference.fromJsonNode(JsonNode.string(reference)));
    }

    @Test
    public void testFromJsonNodeWithLabel() {
        final String label = "label123";
        assertEquals(SpreadsheetExpressionReference.labelName(label),
                SpreadsheetExpressionReference.fromJsonNode(JsonNode.string(label)));
    }

    // parse............................................................................................................

    @Test
    public void testParseCellReference() {
        final String reference = "A1";
        this.parseAndCheck(reference, SpreadsheetExpressionReference.parseCellReference(reference));
    }

    @Test
    public void testParseLabel() {
        final String label = "label123";
        this.parseAndCheck(label, SpreadsheetExpressionReference.labelName(label));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetExpressionReference> type() {
        return SpreadsheetExpressionReference.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetExpressionReference parse(final String text) {
        return SpreadsheetExpressionReference.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseFailedExpected(Class<? extends RuntimeException> throwing) {
        return throwing;
    }

    @Override
    public RuntimeException parseFailedExpected(final RuntimeException expected) {
        return expected;
    }
}