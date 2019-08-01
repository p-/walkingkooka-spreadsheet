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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.json.JsonNode;

import java.util.List;

public final class SpreadsheetTextFormatterDateTimePatternsTest extends SpreadsheetTextFormatterDateTimeOrNumberPatternsTestCase<SpreadsheetTextFormatterDateTimePatterns,
        SpreadsheetFormatDateTimeParserToken> {

    // Parse............................................................................................................

    @Test
    public void testParseNumberPatternFails() {
        this.parseFails("0#00", IllegalArgumentException.class);
    }

    // helpers.........................................................................................................

    @Override
    SpreadsheetTextFormatterDateTimePatterns createPattern(final List<SpreadsheetFormatDateTimeParserToken> tokens) {
        return SpreadsheetTextFormatterDateTimePatterns.with(tokens);
    }

    @Override
    String patternText() {
        return "hh:mm:ss.000";
    }

    @Override
    SpreadsheetFormatDateTimeParserToken parseParserToken(String text) {
        return SpreadsheetFormatParsers.dateTime()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatDateTimeParserToken.class::cast)
                .get();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetTextFormatterDateTimePatterns> type() {
        return SpreadsheetTextFormatterDateTimePatterns.class;
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetTextFormatterDateTimePatterns fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetTextFormatterDateTimePatterns.fromJsonNode(jsonNode);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetTextFormatterDateTimePatterns parse(final String text) {
        return SpreadsheetTextFormatterDateTimePatterns.parse(text);
    }
}

