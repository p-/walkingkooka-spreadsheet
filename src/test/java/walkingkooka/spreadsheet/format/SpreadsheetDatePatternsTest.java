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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.json.JsonNode;

import java.util.List;

public final class SpreadsheetDatePatternsTest extends SpreadsheetPatternsTestCase<SpreadsheetDatePatterns,
        SpreadsheetFormatDateParserToken> {

    // Parse............................................................................................................

    @Test
    public void testParseDateTimePatternFails() {
        this.parseFails("ddmmyyyy hhmmss", IllegalArgumentException.class);
    }

    @Test
    public void testParseNumberPatternFails() {
        this.parseFails("0#00", IllegalArgumentException.class);
    }

    // helpers.........................................................................................................

    @Override
    SpreadsheetDatePatterns createPattern(final List<SpreadsheetFormatDateParserToken> tokens) {
        return SpreadsheetDatePatterns.withDate0(tokens);
    }

    @Override
    String patternText() {
        return "ddmmyyyy";
    }

    @Override
    SpreadsheetFormatDateParserToken parseParserToken(final String text) {
        return SpreadsheetFormatParsers.date()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatDateParserToken.class::cast)
                .get();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDatePatterns> type() {
        return SpreadsheetDatePatterns.class;
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetDatePatterns fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetDatePatterns.fromJsonNodeDate(jsonNode);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetDatePatterns parse(final String text) {
        return SpreadsheetDatePatterns.parseDate0(text);
    }
}

