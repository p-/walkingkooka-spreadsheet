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
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public final class SpreadsheetTimeFormatPatternTest extends SpreadsheetFormatPatternTestCase<SpreadsheetTimeFormatPattern,
        SpreadsheetFormatTimeParserToken,
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

    // helpers..........................................................................................................

    @Override
    SpreadsheetTimeFormatPattern createPattern(final ParserToken token) {
        return SpreadsheetTimeFormatPattern.with(token);
    }

    @Override
    String patternText() {
        return "hh:mm:ss.000 A/P \"text-literal\" \\!";
    }

    @Override
    SpreadsheetFormatTimeParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                             final String text) {
        return SpreadsheetFormatParserToken.time(tokens, text);
    }

    @Override
    ParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.timeFormat()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .get();
    }

    // HasFormatter.....................................................................................................

    @Test
    public void testFormatterFormatTextLiteral() {
        this.formatAndCheck2(
                "\"abc\"",
                LocalTime.of(12, 58, 59),
                "abc"
        );
    }

    @Test
    public void testFormatterFormatCharacterLiteral() {
        this.formatAndCheck2(
                ":,",
                LocalTime.of(12, 58, 59),
                ":,"
        );
    }

    @Test
    public void testFormatterH1() {
        this.formatAndCheck2(
                "h",
                LocalTime.of(1, 58, 59),
                "1"
        );
    }

    @Test
    public void testFormatterH2() {
        this.formatAndCheck2(
                "h",
                LocalTime.of(12, 58, 59),
                "12"
        );
    }

    @Test
    public void testFormatterHh1() {
        this.formatAndCheck2(
                "hh",
                LocalTime.of(1, 58, 59),
                "01"
        );
    }

    @Test
    public void testFormatterHh2() {
        this.formatAndCheck2(
                "hh",
                LocalTime.of(12, 58, 59),
                "12"
        );
    }

    @Test
    public void testFormatterHhh() {
        this.formatAndCheck2(
                "hhh",
                LocalTime.of(12, 58, 59),
                "12"
        );
    }

    @Test
    public void testFormatterHM1() {
        this.formatAndCheck2(
                "hm",
                LocalTime.of(12, 1, 59),
                "121"
        );
    }

    @Test
    public void testFormatterHM2() {
        this.formatAndCheck2(
                "hm",
                LocalTime.of(12, 58, 59),
                "1258"
        );
    }

    @Test
    public void testFormatterHMm1() {
        this.formatAndCheck2(
                "hmm",
                LocalTime.of(12, 1, 59),
                "1201"
        );
    }

    @Test
    public void testFormatterHMm2() {
        this.formatAndCheck2(
                "hmm",
                LocalTime.of(12, 58, 59),
                "1258"
        );
    }

    @Test
    public void testFormatterHMmm() {
        this.formatAndCheck2(
                "hmmm",
                LocalTime.of(12, 58, 59),
                "1258"
        );
    }

    @Test
    public void testFormatterHMmmap() {
        this.formatAndCheck2(
                "hmmma/p",
                LocalTime.of(12, 58, 59),
                "1258q"
        );
    }

    @Test
    public void testFormatterHMmmap2() {
        this.formatAndCheck2(
                "hmmma/p",
                LocalTime.of(23, 58, 59),
                "1158r"
        );
    }

    @Test
    public void testFormatterHMmmAP() {
        this.formatAndCheck2(
                "hmmmA/P",
                LocalTime.of(12, 58, 59),
                "1258Q"
        );
    }

    @Test
    public void testFormatterHMmmaP() {
        this.formatAndCheck2(
                "hmmma/P",
                LocalTime.of(12, 58, 59),
                "1258q"
        );
    }

    @Test
    public void testFormatterHMmmampm() {
        this.formatAndCheck2(
                "hmmmam/pm",
                LocalTime.of(12, 58, 59),
                "1258qam"
        );
    }

    @Test
    public void testFormatterHMmmampm2() {
        this.formatAndCheck2(
                "hmmmam/pm",
                LocalTime.of(23, 58, 59),
                "1158rpm"
        );
    }

    @Test
    public void testFormatterS1() {
        this.formatAndCheck2(
                "s",
                LocalTime.of(12, 58, 1),
                "1"
        );
    }

    @Test
    public void testFormatterS2() {
        this.formatAndCheck2(
                "s",
                LocalTime.of(12, 58, 59),
                "59"
        );
    }

    @Test
    public void testFormatterSs1() {
        this.formatAndCheck2(
                "ss",
                LocalTime.of(12, 58, 1),
                "01"
        );
    }

    @Test
    public void testFormatterSs2() {
        this.formatAndCheck2(
                "ss",
                LocalTime.of(12, 58, 59),
                "59"
        );
    }

    @Test
    public void testFormatterSss() {
        this.formatAndCheck2(
                "sss",
                LocalTime.of(12, 58, 59),
                "59"
        );
    }

    @Test
    public void testFormatterSssDot() {
        this.formatAndCheck2(
                "sss.",
                LocalTime.of(12, 58, 59, 12345678),
                "59"
        );
    }

    @Test
    public void testFormatterSssDotZero() {
        this.formatAndCheck2(
                "sss.0",
                LocalTime.of(12, 58, 59, 123456789),
                "59D1"
        );
    }

    @Test
    public void testFormatterSssDotZeroZero() {
        this.formatAndCheck2(
                "sss.00",
                LocalTime.of(12, 58, 59, 123456789),
                "59D12"
        );
    }

    @Test
    public void testFormatterSssDotZeroZero2() {
        this.formatAndCheck2(
                "sss.00",
                LocalTime.of(12, 58, 59),
                "59D00"
        );
    }

    @Test
    public void testFormatterSssDotZeroZeroZero() {
        this.formatAndCheck2(
                "sss.000",
                LocalTime.of(12, 58, 59, 123456789),
                "59D123"
        );
    }

    @Test
    public void testFormatterFormatHhmmssDot0000() {
        this.formatAndCheck2(
                "hhmmss.0000",
                LocalTime.of(12, 58, 59, 123456789),
                "125859D1235"
        );
    }

    @Test
    public void testFormatterFormatIncludesColorName() {
        this.formatAndCheck2(
                "[red]hhmmss",
                LocalTime.of(12, 58, 59),
                SpreadsheetText.with(
                        Optional.of(RED),
                        "125859"
                )
        );
    }

    @Test
    public void testFormatterFormatIncludesColorNumber() {
        this.formatAndCheck2(
                "[color44]hhmmss",
                LocalTime.of(12, 58, 59),
                SpreadsheetText.with(
                        Optional.of(RED),
                        "125859"
                )
        );
    }

    @Test
    public void testFormatterFormatTrailingPattern() {
        this.formatAndCheck2(
                "hhmmss;",
                LocalTime.of(12, 58, 59),
                SpreadsheetText.with(
                        SpreadsheetText.WITHOUT_COLOR,
                        "125859"
                )
        );
    }

    @Test
    public void testFormatterFormatFirstPattern() {
        this.formatAndCheck2(
                "[>0]hhmmss;hhmm",
                LocalTime.of(12, 58, 59),
                SpreadsheetText.with(
                        SpreadsheetText.WITHOUT_COLOR,
                        "125859"
                )
        );
    }

    @Test
    public void testFormatterFormatSecondPattern() {
        this.formatAndCheck2(
                "[=0]hh;hhmmss",
                LocalTime.of(12, 58, 59),
                SpreadsheetText.with(
                        SpreadsheetText.WITHOUT_COLOR,
                        "125859"
                )
        );
    }

    @Test
    public void testFormatterFormatSecondPatternTrailingSeparator() {
        this.formatAndCheck2(
                "[=0]hh;hhmmss;",
                LocalTime.of(12, 58, 59),
                SpreadsheetText.with(
                        SpreadsheetText.WITHOUT_COLOR,
                        "125859"
                )
        );
    }

    @Override
    SpreadsheetFormatterContext createContext() {
        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> target) {
                return this.converter.canConvert(
                        value,
                        target,
                        this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                        value,
                        target,
                        this
                );
            }

            private final Converter<FakeSpreadsheetFormatterContext> converter = Converters.collection(
                    Lists.of(
                            Converters.localTimeNumber(),
                            Converters.localTimeLocalDateTime()
                    )
            );

            @Override
            public String ampm(final int hourOfDay) {
                return hourOfDay < 13 ?
                        "QAM" :
                        "RPM";
            }

            @Override
            public char decimalSeparator() {
                return 'D';
            }

            @Override
            public Optional<Color> colorName(final SpreadsheetColorName name) {
                checkEquals(
                        SpreadsheetColorName.with("red"),
                        name,
                        "colorName"
                );
                return Optional.of(
                        RED
                );
            }

            @Override
            public Optional<Color> colorNumber(final int number) {
                checkEquals(
                        44,
                        number,
                        "colorNumber"
                );
                return Optional.of(
                        RED
                );
            }
        };
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                this.createPattern(),
                "time-format-pattern\n" +
                        "  \"hh:mm:ss.000 A/P \\\"text-literal\\\" \\\\!\"\n"
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetTimeFormatPattern> type() {
        return SpreadsheetTimeFormatPattern.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetTimeFormatPattern unmarshall(final JsonNode jsonNode,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetTimeFormatPattern.unmarshallTimeFormatPattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetTimeFormatPattern parseString(final String text) {
        return SpreadsheetTimeFormatPattern.parseTimeFormatPattern(text);
    }
}

