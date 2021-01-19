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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Locale;

public final class SpreadsheetDateTimeFormatPatternTest extends SpreadsheetFormatPatternTestCase<SpreadsheetDateTimeFormatPattern,
        SpreadsheetFormatDateTimeParserToken,
        LocalDateTime> {

    @Test
    public void testWithCurrencyFails() {
        this.withInvalidCharacterFails(this.currency());
    }

    @Test
    public void testWithDateFails() {
        this.withInvalidCharacterFails(this.date());
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
    public void testWithNumberFails() {
        this.withInvalidCharacterFails(this.number());
    }

    @Test
    public void testWithPercentSymbolFails() {
        this.withInvalidCharacterFails(this.percentSymbol());
    }

    @Test
    public void testWithThousandFails() {
        this.withInvalidCharacterFails(this.thousands());
    }

    @Test
    public void testWithTimeFails() {
        this.withInvalidCharacterFails(this.time());
    }

    // canFormat........................................................................................................

    @Test
    public void testCanFormatOtherTemporalType() {
        this.canFormatAndCheck4(LocalDate.of(2000, 12, 31));
    }

    @Test
    public void testCanFormatOtherTemporalType2() {
        this.canFormatAndCheck4(LocalTime.of(12, 58, 59));
    }

    private void canFormatAndCheck4(final Temporal temporal) {
        this.canFormatAndCheck(this.createPattern().createFormatter(),
                temporal,
                SpreadsheetFormatterContexts.fake(),
                false);
    }

    // ParseString.......................................................................................................

    @Test
    public void testParseStringNumberPatternFails() {
        this.parseStringFails("0#00", IllegalArgumentException.class);
    }

    // helpers.........................................................................................................

    @Override
    SpreadsheetDateTimeFormatPattern createPattern(final SpreadsheetFormatDateTimeParserToken token) {
        return SpreadsheetDateTimeFormatPattern.with(token);
    }

    @Override
    String patternText() {
        return "dd/mm/yyyy hh:mm:ss.000 A/P \"text-literal\" \\!";
    }

    @Override
    SpreadsheetFormatDateTimeParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                                 final String text) {
        return SpreadsheetFormatParserToken.dateTime(tokens, text);
    }

    @Override
    SpreadsheetFormatDateTimeParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.dateTime()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatDateTimeParserToken.class::cast)
                .get();
    }

    // HasFormatter.....................................................................................................

    @Test
    public void testFormatterFormatYy() {
        this.formatAndCheck3(
                "yy",
                LocalDate.of(2000, 12, 31),
                "00"
        );
    }

    @Test
    public void testFormatterFormatYy2() {
        this.formatAndCheck3(
                "yy",
                LocalDate.of(1999, 12, 31),
                "99"
        );
    }

    @Test
    public void testFormatterFormatYyyy() {
        this.formatAndCheck3(
                "yyyy",
                LocalDate.of(2000, 12, 31),
                "2000"
        );
    }

    @Test
    public void testFormatterFormatYyyy2() {
        this.formatAndCheck3(
                "yyyy",
                LocalDate.of(1999, 12, 31),
                "1999"
        );
    }

    @Test
    public void testFormatterFormatM() {
        this.formatAndCheck3(
                "m",
                LocalDate.of(2000, 1, 31),
                "1"
        );
    }

    @Test
    public void testFormatterFormatM2() {
        this.formatAndCheck3(
                "m",
                LocalDate.of(2000, 12, 31),
                "12"
        );
    }

    @Test
    public void testFormatterFormatMm() {
        this.formatAndCheck3(
                "mm",
                LocalDate.of(2000, 1, 31),
                "01"
        );
    }

    @Test
    public void testFormatterFormatMm2() {
        this.formatAndCheck3(
                "mm",
                LocalDate.of(2000, 12, 31),
                "12"
        );
    }

    @Test
    public void testFormatterFormatMmm() {
        this.formatAndCheck3(
                "mmm",
                LocalDate.of(2000, 1, 31),
                "Jan."
        );
    }

    @Test
    public void testFormatterFormatMmm2() {
        this.formatAndCheck3(
                "mmm",
                LocalDate.of(2000, 12, 31),
                "Dec."
        );
    }

    @Test
    public void testFormatterFormatMmmm() {
        this.formatAndCheck3(
                "mmmm",
                LocalDate.of(2000, 1, 31),
                "January"
        );
    }

    @Test
    public void testFormatterFormatMmmm2() {
        this.formatAndCheck3(
                "mmmm",
                LocalDate.of(2000, 12, 31),
                "December"
        );
    }

    @Test
    public void testFormatterFormatMmmmm() {
        this.formatAndCheck3(
                "mmmmm",
                LocalDate.of(2000, 1, 31),
                "J"
        );
    }

    @Test
    public void testFormatterFormatMmmmm2() {
        this.formatAndCheck3(
                "mmmmm",
                LocalDate.of(2000, 12, 31),
                "D"
        );
    }

    @Test
    public void testFormatterFormatD() {
        this.formatAndCheck3(
                "d",
                LocalDate.of(2000, 12, 1),
                "1"
        );
    }

    @Test
    public void testFormatterFormatD2() {
        this.formatAndCheck3(
                "d",
                LocalDate.of(2000, 12, 31),
                "31"
        );
    }

    @Test
    public void testFormatterFormatDd() {
        this.formatAndCheck3(
                "dd",
                LocalDate.of(2000, 12, 1),
                "01"
        );
    }

    @Test
    public void testFormatterFormatDd2() {
        this.formatAndCheck3(
                "dd",
                LocalDate.of(2000, 12, 31),
                "31"
        );
    }

    @Test
    public void testFormatterFormatDdd() {
        this.formatAndCheck3(
                "ddd",
                LocalDate.of(2000, 12, 1),
                "Fri."
        );
    }

    @Test
    public void testFormatterFormatDdd2() {
        this.formatAndCheck3(
                "ddd",
                LocalDate.of(2000, 12, 31),
                "Sun."
        );
    }

    @Test
    public void testFormatterFormatDddd() {
        this.formatAndCheck3(
                "dddd",
                LocalDate.of(2000, 12, 1),
                "Friday"
        );
    }

    @Test
    public void testFormatterFormatDddd2() {
        this.formatAndCheck3(
                "dddd",
                LocalDate.of(2000, 12, 31),
                "Sunday"
        );
    }

    @Test
    public void testFormatterFormatDdddd() {
        this.formatAndCheck3(
                "ddddd",
                LocalDate.of(2000, 12, 1),
                "Friday"
        );
    }

    @Test
    public void testFormatterFormatDdddd2() {
        this.formatAndCheck3(
                "ddddd",
                LocalDate.of(2000, 12, 31),
                "Sunday"
        );
    }

    @Test
    public void testFormatterFormatYyyymmdd() {
        this.formatAndCheck3(
                "yyyymmdd",
                LocalDate.of(2000, 12, 31),
                "20001231"
        );
    }

    @Test
    public void testFormatterFormatYyyymmdd2() {
        this.formatAndCheck3(
                "yyyy,mm,dd",
                LocalDate.of(2000, 12, 31),
                "2000,12,31"
        );
    }

    @Test
    public void testFormatterFormatLiteral() {
        this.formatAndCheck3(
                ",",
                LocalDate.of(2000, 12, 31),
                ","
        );
    }

    private void formatAndCheck3(final String pattern,
                                 final LocalDate date,
                                 final String expected) {
        this.formatAndCheck2(
                pattern,
                LocalDateTime.of(date, LocalTime.of(12, 58, 59)),
                expected
        );
    }


    @Test
    public void testFormatterH1() {
        this.formatAndCheck4(
                "h",
                LocalTime.of(1, 58, 59),
                "1"
        );
    }

    @Test
    public void testFormatterH2() {
        this.formatAndCheck4(
                "h",
                LocalTime.of(12, 58, 59),
                "12"
        );
    }

    @Test
    public void testFormatterHh1() {
        this.formatAndCheck4(
                "hh",
                LocalTime.of(1, 58, 59),
                "01"
        );
    }

    @Test
    public void testFormatterHh2() {
        this.formatAndCheck4(
                "hh",
                LocalTime.of(12, 58, 59),
                "12"
        );
    }

    @Test
    public void testFormatterHhh() {
        this.formatAndCheck4(
                "hhh",
                LocalTime.of(12, 58, 59),
                "12"
        );
    }

    @Test
    public void testFormatterHM1() {
        this.formatAndCheck4(
                "hm",
                LocalTime.of(12, 1, 59),
                "121"
        );
    }

    @Test
    public void testFormatterHM2() {
        this.formatAndCheck4(
                "hm",
                LocalTime.of(12, 58, 59),
                "1258"
        );
    }

    @Test
    public void testFormatterHMm1() {
        this.formatAndCheck4(
                "hmm",
                LocalTime.of(12, 1, 59),
                "1201"
        );
    }

    @Test
    public void testFormatterHMm2() {
        this.formatAndCheck4(
                "hmm",
                LocalTime.of(12, 58, 59),
                "1258"
        );
    }

    @Test
    public void testFormatterHMmm() {
        this.formatAndCheck4(
                "hmmm",
                LocalTime.of(12, 58, 59),
                "1258"
        );
    }

    @Test
    public void testFormatterHMmmAp() {
        this.formatAndCheck4(
                "hmmma/p",
                LocalTime.of(12, 58, 59),
                "1258QAM"
        );
    }

    @Test
    public void testFormatterHMmmAp2() {
        this.formatAndCheck4(
                "hmmma/p",
                LocalTime.of(23, 58, 59),
                "1158RPM"
        );
    }

    @Test
    public void testFormatterHMmmAmpm() {
        this.formatAndCheck4(
                "hmmmam/pm",
                LocalTime.of(12, 58, 59),
                "1258QAM"
        );
    }

    @Test
    public void testFormatterHMmmAmpm2() {
        this.formatAndCheck4(
                "hmmmam/pm",
                LocalTime.of(23, 58, 59),
                "1158RPM"
        );
    }

    @Test
    public void testFormatterS1() {
        this.formatAndCheck4(
                "s",
                LocalTime.of(12, 58, 1),
                "1"
        );
    }

    @Test
    public void testFormatterS2() {
        this.formatAndCheck4(
                "s",
                LocalTime.of(12, 58, 59),
                "59"
        );
    }

    @Test
    public void testFormatterSs1() {
        this.formatAndCheck4(
                "ss",
                LocalTime.of(12, 58, 1),
                "01"
        );
    }

    @Test
    public void testFormatterSs2() {
        this.formatAndCheck4(
                "ss",
                LocalTime.of(12, 58, 59),
                "59"
        );
    }

    @Test
    public void testFormatterSss() {
        this.formatAndCheck4(
                "sss",
                LocalTime.of(12, 58, 59),
                "59"
        );
    }

    @Test
    public void testFormatterSssDot() {
        this.formatAndCheck4(
                "sss.",
                LocalTime.of(12, 58, 59, 12345678),
                "59"
        );
    }

    @Test
    public void testFormatterSssDotZero() {
        this.formatAndCheck4(
                "sss.0",
                LocalTime.of(12, 58, 59, 123456789),
                "59d1"
        );
    }

    @Test
    public void testFormatterSssDotZeroZero() {
        this.formatAndCheck4(
                "sss.00",
                LocalTime.of(12, 58, 59, 123456789),
                "59d12"
        );
    }

    @Test
    public void testFormatterSssDotZeroZero2() {
        this.formatAndCheck4(
                "sss.00",
                LocalTime.of(12, 58, 59),
                "59d00"
        );
    }

    @Test
    public void testFormatterSssDotZeroZeroZero() {
        this.formatAndCheck4(
                "sss.000",
                LocalTime.of(12, 58, 59, 123456789),
                "59d123"
        );
    }

    @Test
    public void testFormatterFormatHhmmssDot0000() {
        this.formatAndCheck4(
                "hhmmss.0000",
                LocalTime.of(12, 58, 59, 123456789),
                "125859d1235"
        );
    }

    @Test
    public void testFormatterFormatASlashP() {
        this.formatAndCheck4(
                "a/p",
                LocalTime.of(12, 58, 59, 123456789),
                "QAM"
        );
    }

    @Test
    public void testFormatterFormatA2() {
        this.formatAndCheck4(
                "a/p",
                LocalTime.of(23, 58, 59, 123456789),
                "RPM"
        );
    }

    @Test
    public void testFormatterFormatAmpm() {
        this.formatAndCheck4(
                "am/pm",
                LocalTime.of(12, 58, 59, 123456789),
                "QAM"
        );
    }

    @Test
    public void testFormatterFormatAmpm2() {
        this.formatAndCheck4(
                "am/pm",
                LocalTime.of(23, 58, 59, 123456789),
                "RPM"
        );
    }

    @Test
    public void testFormatterFormatHhmmAm() {
        this.formatAndCheck4(
                "hhmma/p",
                LocalTime.of(12, 58, 59, 123456789),
                "1258QAM"
        );
    }

    private void formatAndCheck4(final String pattern,
                                 final LocalTime time,
                                 final String expected) {
        this.formatAndCheck2(
                pattern,
                LocalDateTime.of(LocalDate.of(2000, 12, 31), time),
                expected
        );
    }

    @Test
    public void testFormatterYyyymmddhhmmss() {
        this.formatAndCheck2(
                "yyyy,mm,dd,hh,mm,ss",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59),
                "2000,12,31,12,58,59"
        );
    }

    @Override
    final SpreadsheetFormatterContext formatterContext() {
        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> target) {
                return value instanceof LocalDateTime && LocalDateTime.class == target;
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return Converters.simple()
                        .convert(value, target, ConverterContexts.fake());
            }

            @Override
            public List<String> monthNames() {
                return this.dateTimeContext().monthNames();
            }

            @Override
            public String monthName(final int month) {
                return this.dateTimeContext().monthName(month);
            }

            @Override
            public List<String> monthNameAbbreviations() {
                return this.dateTimeContext().monthNameAbbreviations();
            }

            @Override
            public String monthNameAbbreviation(final int month) {
                return this.dateTimeContext().monthNameAbbreviation(month);
            }

            @Override
            public int twoDigitYear() {
                return this.dateTimeContext().twoDigitYear();
            }

            @Override
            public List<String> weekDayNames() {
                return this.dateTimeContext().weekDayNames();
            }

            @Override
            public String weekDayName(final int day) {
                return this.dateTimeContext().weekDayName(day);
            }

            @Override
            public List<String> weekDayNameAbbreviations() {
                return this.dateTimeContext().weekDayNameAbbreviations();
            }

            @Override
            public String weekDayNameAbbreviation(final int day) {
                return this.dateTimeContext().weekDayNameAbbreviation(day);
            }

            private DateTimeContext dateTimeContext() {
                return DateTimeContexts.locale(Locale.forLanguageTag("EN-AU"), 20);
            }

            @Override
            public String ampm(final int hourOfDay) {
                return hourOfDay < 13 ?
                        "QAM" :
                        "RPM";
            }

            @Override
            public char decimalSeparator() {
                return 'd';
            }
        };
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDateTimeFormatPattern> type() {
        return SpreadsheetDateTimeFormatPattern.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetDateTimeFormatPattern unmarshall(final JsonNode jsonNode,
                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetDateTimeFormatPattern.unmarshallDateTimeFormatPattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetDateTimeFormatPattern parseString(final String text) {
        return SpreadsheetDateTimeFormatPattern.parseDateTimeFormatPattern(text);
    }
}

