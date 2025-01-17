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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetTimeParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalTime;
import java.util.List;

public final class SpreadsheetTimeParsePatternTest extends SpreadsheetParsePatternTestCase<SpreadsheetTimeParsePattern,
        SpreadsheetFormatTimeParserToken,
        SpreadsheetTimeParserToken,
        LocalTime> {

    @Test
    public void testWithDateFails() {
        this.withInvalidCharacterFails(this.date());
    }

    @Test
    public void testWithCurrencyFails() {
        this.withInvalidCharacterFails(this.currency());
    }

    @Test
    public void testWithDateTimeFails() {
        this.withInvalidCharacterFails(this.dateTime());
    }

    @Test
    public void testWithDayFails() {
        this.withInvalidCharacterFails(this.day());
    }

    @Test
    public void testWithDigitFails() {
        this.withInvalidCharacterFails(this.digit());
    }

    @Test
    public void testWithDigitSpaceFails() {
        this.withInvalidCharacterFails(this.digitSpace());
    }

    @Test
    public void testWithExponentSymbolFails() {
        this.withInvalidCharacterFails(this.exponentSymbol());
    }

    @Test
    public void testWithPercentSymbolFails() {
        this.withInvalidCharacterFails(this.percentSymbol());
    }

    @Test
    public void testWithNumberFails() {
        this.withInvalidCharacterFails(this.number());
    }

    @Test
    public void testWithThousandsFails() {
        this.withInvalidCharacterFails(this.thousands());
    }

    @Test
    public void testWithYearFails() {
        this.withInvalidCharacterFails(this.year());
    }

    // Parse............................................................................................................

    @Test
    public void testParseStringDatePatternFails() {
        this.parseStringFails("ddmmyyyy", IllegalArgumentException.class);
    }

    @Test
    public void testParseStringNumberPatternFails() {
        this.parseStringFails("0#00", IllegalArgumentException.class);
    }

    // parser...........................................................................................................

    @Test
    public void testParserToString2() {
        final String pattern = "hh:mm:ss";
        this.toStringAndCheck(
                SpreadsheetTimeParsePattern.parseTimeParsePattern(pattern).parser(),
                '"' + pattern + '"'
        );
    }

    @Test
    public void testParseHour9() {
        this.parseAndCheck2(
                "h",
                "9",
                hour9()
        );
    }

    @Test
    public void testParseHourHour9() {
        this.parseAndCheck2(
                "hh",
                "9",
                hour9()
        );
    }

    @Test
    public void testParseHour9Colon() {
        this.parseAndCheck2(
                "h:",
                "9:",
                hour9(),
                colon()
        );
    }

    @Test
    public void testParseHour11() {
        this.parseAndCheck2(
                "hh",
                "11",
                hour11()
        );
    }

    @Test
    public void testParseHourMinutes() {
        this.parseAndCheck2(
                "hh:mm",
                "11:58",
                hour11(),
                colon(),
                minute58()
        );
    }

    @Test
    public void testParseHourMinutesSeconds() {
        this.parseAndCheck2(
                "hh:mm:ss",
                "11:58:59",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59()
        );
    }

    @Test
    public void testParseHourMinutesSecondsDecimal() {
        this.parseAndCheck2(
                "hh:mm:ss.",
                "9:58:59" + DECIMAL,
                hour9(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator()
        );
    }

    @Test
    public void testParseHourMinutesSecondsDecimal2() {
        this.parseAndCheck2(
                "hh:mm:ss.",
                "13:58:59" + DECIMAL,
                hour13(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator()
        );
    }

    @Test
    public void testParseHourMinutesSecondsDecimal1Millis() {
        this.parseAndCheck2(
                "hh:mm:ss.0",
                "11:58:59" + DECIMAL + "1",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator(),
                milli(100_000_000, "1")
        );
    }

    @Test
    public void testParseHourMinutesSecondsDecimal1Millis2() {
        this.parseAndCheck2(
                "hh:mm:ss.0",
                "11:58:59" + DECIMAL,
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator()
        );
    }

    @Test
    public void testParseHourMinutesSecondsDecimal2Millis() {
        this.parseAndCheck2(
                "hh:mm:ss.00",
                "11:58:59" + DECIMAL + "12",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator(),
                SpreadsheetParserToken.millisecond(120_000_000, "12")
        );
    }

    @Test
    public void testParseHourMinutesSecondsDecimal3Millis() {
        this.parseAndCheck2(
                "hh:mm:ss.000",
                "11:58:59" + DECIMAL + "123",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator(),
                SpreadsheetParserToken.millisecond(123_000_000, "123")
        );
    }

    @Test
    public void testParseHourMinutesSecondsDecimal3Millis2() {
        this.parseAndCheck2(
                "hh:mm:ss.000",
                "11:58:59" + DECIMAL + "12",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator(),
                SpreadsheetParserToken.millisecond(120_000_000, "12")
        );
    }

    @Test
    public void testParseHourMinutesSecondsDecimal3Millis3() {
        this.parseAndCheck2(
                "hh:mm:ss.000",
                "11:58:59" + DECIMAL + "1",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator(),
                SpreadsheetParserToken.millisecond(100_000_000, "1")
        );
    }

    @Test
    public void testParseHourMinutesSecondsDecimal3Millis4() {
        this.parseAndCheck2(
                "hh:mm:ss.000",
                "11:58:59" + DECIMAL,
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator()
        );
    }

    @Test
    public void testParseHourMinutesSecondsDecimal3Millis5() {
        this.parseAndCheck2(
                "hh:mm:ss.000",
                "11:58:59",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59()
        );
    }

    @Test
    public void testParseHourMinutesAm() {
        this.parseAndCheck2(
                "hh:mm AM/PM",
                "11:58 AM",
                hour11(),
                colon(),
                minute58(),
                textLiteralWhitespace(),
                am()
        );
    }

    @Test
    public void testParseHourMinutesPm() {
        this.parseAndCheck2(
                "hh:mm AM/PM",
                "11:58 PM",
                hour11(),
                colon(),
                minute58(),
                textLiteralWhitespace(),
                pm()
        );
    }

    @Test
    public void testParseHourMinutesSecondsAmpm() {
        this.parseAndCheck2(
                "hh:mm:ss AM/PM",
                "11:58:59 PM",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                textLiteralWhitespace(),
                pm()
        );
    }

    @Test
    public void testParseHourMinutesSecondsMillisAmpm() {
        this.parseAndCheck2(
                "hh:mm:ss.0 AM/PM",
                "11:58:59" + DECIMAL + "1 PM",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator(),
                milli(100_000_000, "1"),
                textLiteralWhitespace(),
                pm()
        );
    }

    @Test
    public void testParseHourDefaultsMinutes() {
        this.parseAndCheck2(
                "hh",
                "11",
                hour11()
        );
    }

    @Test
    public void testParsePatternTrailingSeparator() {
        this.parseAndCheck2(
                "hh;",
                "11",
                hour11()
        );
    }

    @Test
    public void testParseHourMultiplePatterns() {
        this.parseAndCheck2(
                "\"A\"hhmmss;\"B\"hhmmss",
                "B115859",
                textLiteral("B"),
                hour11(),
                minute58(),
                second59()
        );
    }

    @Test
    public void testParseHourMultiplePatternsTrailingSeparator() {
        this.parseAndCheck2(
                "\"A\"hhmmss;\"B\"hhmmss;",
                "B115859",
                textLiteral("B"),
                hour11(),
                minute58(),
                second59()
        );
    }

    @Test
    public void testParseHourCommaMinutesCommaSecondsCommaSeparator() {
        this.parseAndCheck2(
                "hh,mm,ss;",
                "11,58,59",
                hour11(),
                comma(),
                minute58(),
                comma(),
                second59()
        );
    }

    @Test
    public void testParseHourCommaMinutesCommaSecondsComma() {
        this.parseAndCheck2(
                "hh,mm,ss",
                "11,58,59",
                hour11(),
                comma(),
                minute58(),
                comma(),
                second59()
        );
    }

    @Test
    public void testParseHourBackslashEscaped() {
        this.parseAndCheck2(
                "hh\\hmm\\mss\\s",
                "11h58m59s",
                hour11(),
                textLiteral("h"),
                minute58(),
                textLiteral("m"),
                second59(),
                textLiteral("s")
        );
    }

    @Test
    public void testParseSeconds9() {
        this.parseAndCheck2(
                "s",
                "9",
                second9()
        );
    }

    @Test
    public void testParseSeconds59() {
        this.parseAndCheck2(
                "ss",
                "59",
                second59()
        );
    }

    @Test
    public void testParseSecondsDecimalSeparator() {
        this.parseAndCheck2(
                "s.",
                "9" + DECIMAL,
                second9(),
                decimalSeparator()
        );
    }

    // general........................................................................................................

    @Test
    public void testConvertTimeFails() {
        this.convertFails2("hhmmss",
                "12345!");
    }

    @Test
    public void testConvertHourMinutesOnlyPattern() {
        this.convertAndCheck2("hh:mm",
                "11:59",
                LocalTime.of(11, 59));
    }

    @Test
    public void testConvertHourMinutesSecondsOnlyPattern() {
        this.convertAndCheck2("hh:mm:ss",
                "11:58:59",
                LocalTime.of(11, 58, 59));
    }

    @Test
    public void testConvertHourMinutesSecondsAmpmOnlyPattern() {
        this.convertAndCheck2("hh:mm:ss AM/PM",
                "11:58:59 PM",
                LocalTime.of(23, 58, 59));
    }

    @Test
    public void testConvertHourDefaultsMinutes() {
        this.convertAndCheck2("hh",
                "11",
                LocalTime.of(11, 0, 0));
    }

    @Test
    public void testConvertHourMultiplePatterns() {
        this.convertAndCheck2("\"A\"hhmmss;\"B\"hhmmss",
                "B115859",
                LocalTime.of(11, 58, 59));
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetPattern.parseTimeParsePattern("hhmm"),
                "time-parse-pattern\n" +
                        "  \"hhmm\"\n"
        );
    }

    @Test
    public void testTreePrint2() {
        this.treePrintAndCheck(
                SpreadsheetPattern.parseTimeParsePattern("hhmm;hhmmss"),
                "time-parse-pattern\n" +
                        "  \"hhmm\"\n" +
                        "  \"hhmmss\"\n"
        );
    }

    // helpers..........................................................................................................

    @Override
    SpreadsheetTimeParsePattern createPattern(final ParserToken token) {
        return SpreadsheetTimeParsePattern.with(token);
    }

    @Override
    String patternText() {
        return "hh:mm:ss.000 A/P \"text-literal\" \\!";
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.timeParse();
    }

    @Override
    SpreadsheetFormatTimeParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                             final String text) {
        return SpreadsheetFormatParserToken.time(tokens, text);
    }

    @Override
    SpreadsheetTimeParserToken parent(final List<ParserToken> tokens,
                                      final String text) {
        return SpreadsheetParserToken.time(tokens, text);
    }

    @Override
    Class<LocalTime> targetType() {
        return LocalTime.class;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetTimeParsePattern> type() {
        return SpreadsheetTimeParsePattern.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetTimeParsePattern unmarshall(final JsonNode jsonNode,
                                                  final JsonNodeUnmarshallContext context) {
        return SpreadsheetTimeParsePattern.unmarshallTimeParsePattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetTimeParsePattern parseString(final String text) {
        return SpreadsheetTimeParsePattern.parseTimeParsePattern(text);
    }
}

