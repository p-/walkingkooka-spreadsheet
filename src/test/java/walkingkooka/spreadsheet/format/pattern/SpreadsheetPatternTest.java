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
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetDateParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDateTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetTimeParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternTest implements ClassTesting2<SpreadsheetPattern<?>>,
        SpreadsheetFormatterTesting,
        ParserTesting {


    // static factory method Locale.....................................................................................

    @Test
    public static void testDateFormatPatternLocaleNullLocaleFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetPattern.dateFormatPatternLocale(null));
    }

    @Test
    public static void testDateParsePatternsLocaleNullLocaleFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetPattern.dateParsePatternsLocale(null));
    }

    @Test
    public static void testDateTimeFormatPatternLocaleNullLocaleFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetPattern.dateTimeFormatPatternLocale(null));
    }

    @Test
    public static void testDateTimeParsePatternsLocaleNullLocaleFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetPattern.dateTimeParsePatternsLocale(null));
    }

    @Test
    public static void testTimeFormatPatternLocaleNullLocaleFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetPattern.timeFormatPatternLocale(null));
    }

    @Test
    public static void testTimeParsePatternsLocaleNullLocaleFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetPattern.timeParsePatternsLocale(null));
    }

    private final static Locale EN_AU = Locale.forLanguageTag("EN-AU");

    @Test
    public void testDateFormatPatternLocale() {
        this.localePatternFormatAndCheck(
                SpreadsheetPattern.dateFormatPatternLocale(EN_AU),
                LocalDate.of(2000, 12, 31),
                "Sunday, 31 December 2000"
        );
    }

    @Test
    public void testDateTimeFormatPatternLocale() {
        this.localePatternFormatAndCheck(
                SpreadsheetPattern.dateTimeFormatPatternLocale(EN_AU),
                LocalDateTime.of(2000, 12, 31, 12, 58),
                "Sunday, 31 December 2000 at 12:58:00 pm"
        );
    }

    @Test
    public void testTimeFormatPatternLocale() {
        this.localePatternFormatAndCheck(
                SpreadsheetPattern.timeFormatPatternLocale(EN_AU),
                LocalTime.of(12, 58, 59),
                "12:58:59 pm"
        );
    }

    private <T> void localePatternFormatAndCheck(final SpreadsheetFormatPattern<?> formatPattern,
                                                 final T value,
                                                 final String formattedText) {
        this.formatAndCheck(
                formatPattern.formatter(),
                value,
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        if (target == LocalDateTime.class) {
                            if (value instanceof LocalDate) {
                                return Converters.localDateLocalDateTime().convert(value, target, ConverterContexts.fake());
                            }
                            if (value instanceof LocalDateTime) {
                                return Either.left(Cast.to(value));
                            }
                            if (value instanceof LocalTime) {
                                return Converters.localTimeLocalDateTime().convert(value, target, ConverterContexts.fake());
                            }
                        }

                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public String ampm(final int hourOfDay) {
                        return this.dateTimeContext().ampm(hourOfDay);
                    }

                    @Override
                    public String weekDayName(final int day) {
                        return this.dateTimeContext().weekDayName(day);
                    }

                    @Override
                    public String monthName(int month) {
                        return this.dateTimeContext().monthName(month);
                    }

                    private DateTimeContext dateTimeContext() {
                        return DateTimeContexts.locale(EN_AU, 1900, 50);
                    }

                },
                formattedText
        );
    }

    @Test
    public void testDateParsePatternsLocale() {
        this.localePatternParseAndCheck(
                SpreadsheetPattern.dateParsePatternsLocale(EN_AU),
                "31/12/00",
                (t, c) -> t.cast(SpreadsheetDateParserToken.class).toLocalDate(c),
                LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testDateTimeParsePatternsLocale() {
        this.localePatternParseAndCheck(
                SpreadsheetPattern.dateTimeParsePatternsLocale(EN_AU),
                "31/12/00, 12:58",
                (t, c) -> t.cast(SpreadsheetDateTimeParserToken.class).toLocalDateTime(c),
                LocalDateTime.of(2000, 12, 31, 12, 58)
        );
    }

    @Test
    public void testTimeParsePatternsLocale() {
        this.localePatternParseAndCheck(
                SpreadsheetPattern.timeParsePatternsLocale(EN_AU),
                "12:58",
                (t, c) -> t.cast(SpreadsheetTimeParserToken.class).toLocalTime(),
                LocalTime.of(12, 58)
        );
    }

    private <T> void localePatternParseAndCheck(final SpreadsheetParsePatterns<?> parsePatterns,
                                                final String text,
                                                final BiFunction<ParserToken, ExpressionEvaluationContext, T> tokenToValue,
                                                final T expected) {
        final Parser<SpreadsheetParserContext> parser = parsePatterns.parser();
        final TextCursor cursor = TextCursors.charSequence(text);


        assertEquals(
                expected,
                parser.parse(cursor, SpreadsheetParserContexts.fake())
                        .map(t -> tokenToValue.apply(t, new FakeExpressionEvaluationContext() {
                            @Override
                            public int defaultYear() {
                                return 1800;
                            }

                            @Override
                            public int twoDigitYear() {
                                return 50;
                            }
                        }))
                        .orElse(null),
                () -> "parse " + CharSequences.quoteAndEscape(text) + " parser: " + parser
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetPattern<?>> type() {
        return Cast.to(SpreadsheetPattern.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
