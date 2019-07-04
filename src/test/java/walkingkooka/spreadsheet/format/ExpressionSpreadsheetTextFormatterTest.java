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
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExpressionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.parser.Parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ExpressionSpreadsheetTextFormatterTest extends SpreadsheetTextFormatter3TestCase<ExpressionSpreadsheetTextFormatter, Object, SpreadsheetFormatExpressionParserToken> {

    private final static String TEXT = "Abc123";
    private final static Color RED = Color.fromRgb(0x0FF);
    private final static Color COLOR31 = Color.fromRgb(0x031);

    @Test
    public void testGeneralEmptyEmptyTextPlaceholderFormatNumber() {
        this.parseFormatAndCheck("General;;;@",
                BigDecimal.valueOf(9),
                "009.000");
    }

    @Test
    public void testDateTimeEmptyEmptyTextPlaceholderFormatNumber() {
        this.parseFormatAndCheck("yyyymmddhhmmss;;;@",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59),
                "20001231125859");
    }

    @Test
    public void testNumberEmptyEmptyTextPlaceholderFormatNumber() {
        this.parseFormatAndCheck("#.0\"1st\";;;@",
                123,
                "123.01st");
    }

    @Test
    public void testColorNumberNumberEmptyEmptyTextPlaceholderFormatNumber() {
        this.parseFormatAndCheck("[RED]#.0\"1st\";;;@",
                123,
                RED,
                "123.01st");
    }

    @Test
    public void testColorNameNumberEmptyEmptyTextPlaceholderFormatNumber() {
        this.parseFormatAndCheck("[COLOR 31]#.0\"1st\";;;@",
                123,
                COLOR31,
                "123.01st");
    }

    @Test
    public void testEmptyEmptyEmptyEmptyFormatText() {
        this.parseFormatAndCheck(";;;", TEXT, "");
    }

    @Test
    public void testEmptyEmptyEmptyGeneralFormatText() {
        this.parseFormatAndCheck(";;;General", TEXT, TEXT);
    }

    @Test
    public void testEmptyEmptyEmptyTextPlaceholderFormatText() {
        this.parseFormatAndCheck(";;;@", TEXT, TEXT);
    }

    // conditional................................................................................................

    @Test
    public void testConditionalNumberFormatPositiveNumber() {
        this.parseFormatAndCheck("[>100]\"positive\"0", 123, "positive123");
    }

    @Test
    public void testConditionalNumberNumberFormatPositiveNumber() {
        this.parseFormatAndCheck(NUMBER100_NUMBER, 123, "positive123");
    }

    @Test
    public void testConditionalNumberNumberFormatZero() {
        this.parseFormatAndCheck(NUMBER100_NUMBER, 0, "else0");
    }

    @Test
    public void testConditionalNumberNumberFormatNegativeNumber() {
        this.parseFormatAndCheck(NUMBER100_NUMBER, -123, "else-123");
    }

    private final static String NUMBER100_NUMBER = "[>100]\"positive\"0;\"else\"0";

    @Test
    public void testConditionalNumberConditionalNumberFormatNegativeNumber() {
        this.parseFormatAndCheck("[>100]\"positive\"0;[<-99]\"negative\"0", -123, "negative-123");
    }

    @Test
    public void testConditionalNumberConditionalNumberNumberFormatZeroNumber() {
        this.parseFormatAndCheck("[>100]\"positive\"0;[<-99]\"negative\"0;\"zero\"0", 0, "zero0");
    }

    @Test
    public void testConditionalNumberConditionalNumberNumberFormatText() {
        final String text = "abc123";
        this.parseFormatAndCheck(NUMBER100_NUMBER99_NUMBER_TEXT, text, "Text" + text);
    }

    @Test
    public void testConditionalNumberConditionalNumberNumberFormatPositiveNumberFails1stCondition() {
        this.parseFormatAndCheck(NUMBER100_NUMBER99_NUMBER_TEXT, 1, "else1");
    }

    @Test
    public void testConditionalNumberConditionalNumberNumberFormatNegativeNumber2ndCondition() {
        this.parseFormatAndCheck(NUMBER100_NUMBER99_NUMBER_TEXT, -1, "else-1");
    }

    private final static String NUMBER100_NUMBER99_NUMBER_TEXT = "[>100]\"positive\"0;[<-99]\"negative\"0;\"else\"0;\"Text\"@";

    @Test
    public void testConditionalNumberConditionalNumberNumberFormatPositiveNumber2ndCondition() {
        this.parseFormatAndCheck("[>100]\"first\"0;[>10]\"second\"0;\"else\"0;\"Text\"@", 50, "second50");
    }

    @Test
    public void testConditionalNumberConditionalNumberNumberFormatNumberFailsBothConditions() {
        this.parseFormatAndCheck("[>100]\"first\"0;[>10]\"second\"0;\"else\"0;\"Text\"@", 5, "else5");
    }

    // number|number pattern without conditions................................................................

    private final static String NUMERNUMBERPATTERN = "\"positiveAndZero\"0;\"negative\"0";

    @Test
    public void testNumberNumberFormatPositiveNumber() {
        this.parseFormatAndCheck(NUMERNUMBERPATTERN, 123, "positiveAndZero123");
    }

    @Test
    public void testNumberNumberFormatNegativeNumber() {
        this.parseFormatAndCheck(NUMERNUMBERPATTERN, -123, "negative-123");
    }

    @Test
    public void testNumberNumberFormatZeroNumber() {
        this.parseFormatAndCheck(NUMERNUMBERPATTERN, 0, "positiveAndZero0");
    }

    // number|number|number pattern without conditions................................................................

    private final static String NUMERNUMBERNUMBERPATTERN = "\"positive\"0;\"negative\"0;\"zero\"0";

    @Test
    public void testNumberNumberNumberFormatPositiveNumber() {
        this.parseFormatAndCheck(NUMERNUMBERNUMBERPATTERN, 123, "positive123");
    }

    @Test
    public void testNumberNumberNumberFormatNegativeNumber() {
        this.parseFormatAndCheck(NUMERNUMBERNUMBERPATTERN, -123, "negative-123");
    }

    @Test
    public void testNumberNumberNumberFormatZeroNumber() {
        this.parseFormatAndCheck(NUMERNUMBERNUMBERPATTERN, 0, "zero0");
    }

    // helpers.......................................................................................................

    private void parseFormatAndCheck(final String pattern,
                                     final long value,
                                     final String text) {
        this.parseFormatAndCheck(pattern, BigDecimal.valueOf(value), text);
    }


    private void parseFormatAndCheck(final String pattern,
                                     final Object value,
                                     final String text) {
        this.parseFormatAndCheck(pattern, value, SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, text));
    }

    private void parseFormatAndCheck(final String pattern,
                                     final long value,
                                     final Color color,
                                     final String text) {
        this.parseFormatAndCheck(pattern, BigDecimal.valueOf(value), color, text);
    }

    private void parseFormatAndCheck(final String pattern,
                                     final Object value,
                                     final Color color,
                                     final String text) {
        this.parseFormatAndCheck(pattern, value, SpreadsheetFormattedText.with(Optional.of(color), text));
    }

    private void parseFormatAndCheck(final String pattern,
                                     final Object value,
                                     final SpreadsheetFormattedText text) {
        this.formatAndCheck(this.createFormatter(pattern),
                value,
                this.createContext(),
                text);
    }

    @Override
    String pattern() {
        return ";;;@";
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.expression();
    }

    //toString .......................................................................................................

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern());
    }

    @Override
    ExpressionSpreadsheetTextFormatter createFormatter0(final SpreadsheetFormatExpressionParserToken token) {
        return ExpressionSpreadsheetTextFormatter.with(token, this.mathContext(), this.fractioner());
    }

    private MathContext mathContext() {
        return MATH_CONTEXT;
    }

    private final static MathContext MATH_CONTEXT = MathContext.UNLIMITED;

    private Function<BigDecimal, Fraction> fractioner() {
        return this::makeIntoFraction;
    }

    private Fraction makeIntoFraction(final BigDecimal value) {
        if (value.signum() == 0) {
            return Fraction.with(BigInteger.ZERO, BigInteger.ONE);
        }

        final int scale = value.scale();
        final BigDecimal two = BigDecimal.valueOf(2);

        return Fraction.with(
                value.scaleByPowerOfTen(scale).divide(two, MATH_CONTEXT).toBigInteger(),
                BigDecimal.ONE.scaleByPowerOfTen(scale).divide(two, MATH_CONTEXT).toBigInteger());
    }

    @Override
    public String value() {
        return "Text123";
    }

    @Override
    public SpreadsheetTextFormatContext createContext() {
        return new FakeSpreadsheetTextFormatContext() {

            @Override
            public Color colorName(final String name) {
                assertEquals("RED", name);
                return RED;
            }

            @Override
            public Color colorNumber(final int number) {
                assertEquals(31, number, "number");
                return COLOR31;
            }

            @Override
            public String currencySymbol() {
                return "$";
            }

            @Override
            public char decimalPoint() {
                return '.';
            }

            @Override
            public char exponentSymbol() {
                return 'E';
            }

            @Override
            public char groupingSeparator() {
                return ',';
            }

            @Override
            public char minusSign() {
                return '-';
            }

            @Override
            public char percentageSymbol() {
                return '%';
            }

            @Override
            public char plusSign() {
                return '+';
            }

            @Override
            public String generalDecimalFormatPattern() {
                return "000.000"; // used by testGeneralEmptyEmptyTextPlaceholderFormatNumber
            }

            @Override
            public <T> T convert(final Object value, final Class<T> target) {
                if (target.isInstance(value)) {
                    return target.cast(value);
                }
                return Converters.localDateTimeBigDecimal(Converters.EXCEL_OFFSET).convert(value,
                        target,
                        ConverterContexts.basic(this));
            }
        };
    }

    @Override
    public Class<ExpressionSpreadsheetTextFormatter> type() {
        return ExpressionSpreadsheetTextFormatter.class;
    }
}