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

package walkingkooka.spreadsheet.convert;

import walkingkooka.color.Color;
import walkingkooka.spreadsheet.SpreadsheetValueVisitor;
import walkingkooka.spreadsheet.format.HasSpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceRange;
import walkingkooka.tree.expression.ExpressionNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * This {@link SpreadsheetValueVisitor} attempts to use the provided pattern to format the value except for
 * some values like {@link Character}, {@link String} and sub classes of {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection}
 * which ignore the pattern and simply converted using {@link Object#toString()}.
 * <br>
 * Note it is possible for this fail if the pattern is invalid and parsing of the pattern fails.
 */
final class StringToFormatPatternConverterSpreadsheetValueVisitor extends SpreadsheetValueVisitor {

    static String format(final Object value,
                         final String pattern,
                         final SpreadsheetConverterContext context) {
        final StringToFormatPatternConverterSpreadsheetValueVisitor visitor = new StringToFormatPatternConverterSpreadsheetValueVisitor(
                pattern,
                context
        );
        visitor.accept(value);
        return visitor.formatted;
    }

    // VisibleForTesting
    StringToFormatPatternConverterSpreadsheetValueVisitor(final String pattern,
                                                          final SpreadsheetConverterContext context) {
        super();
        this.pattern = pattern;
        this.context = context;
    }

    @Override
    protected void visit(final BigDecimal value) {
        this.formatNumber(value);
    }

    @Override
    protected void visit(final BigInteger value) {
        this.formatNumber(value);
    }

    @Override
    protected void visit(final Boolean value) {
        this.formatNumber(
                value ? 1 : 0
        );
    }

    @Override
    protected void visit(final Byte value) {
        this.formatNumber(value);
    }

    @Override
    protected void visit(final Character value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final ExpressionNumber value) {
        this.formatNumber(value);
    }

    @Override
    protected void visit(final Float value) {
        this.formatNumber(value);
    }

    @Override
    protected void visit(final Double value) {
        this.formatNumber(value);
    }

    @Override
    protected void visit(final Integer value) {
        this.formatNumber(value);
    }

    @Override
    protected void visit(final LocalDate value) {
        this.formatUsingFormatter(
                value,
                SpreadsheetPattern.parseDateFormatPattern(this.pattern)
        );
    }

    @Override
    protected void visit(final LocalDateTime value) {
        this.formatUsingFormatter(
                value,
                SpreadsheetPattern.parseDateTimeFormatPattern(this.pattern)
        );
    }

    @Override
    protected void visit(final LocalTime value) {
        this.formatUsingFormatter(
                value,
                SpreadsheetPattern.parseTimeFormatPattern(this.pattern)
        );
    }

    @Override
    protected void visit(final Long value) {
        this.formatNumber(value);
    }

    @Override
    protected void visit(final SpreadsheetCellRange value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final SpreadsheetCellReference value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final SpreadsheetColumnReferenceRange value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final SpreadsheetColumnReference value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final SpreadsheetLabelName value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final SpreadsheetRowReferenceRange value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final SpreadsheetRowReference value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final Short value) {
        this.formatNumber(value);
    }

    @Override
    protected void visit(final String value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final Object value) {
        this.formatText(value);
    }

    private void formatNumber(final Number number) {
        this.formatNumber(this.context.expressionNumberKind().create(number));
    }

    /**
     * Parses the pattern into a {@link SpreadsheetFormatter} and then formats the given number.
     */
    private void formatNumber(final ExpressionNumber number) {
        this.formatUsingFormatter(
                number,
                SpreadsheetPattern.parseNumberFormatPattern(this.pattern)
        );
    }

    private void formatUsingFormatter(final Object value,
                                      final HasSpreadsheetFormatter hasSpreadsheetFormatter) {
        this.formatText(
                hasSpreadsheetFormatter.formatter()
                        .format(
                                value,
                                SpreadsheetFormatterContexts.basic(
                                        this::numberToColor,
                                        this::nameToColor,
                                        1,
                                        SpreadsheetFormatters.fake(), // should never be called
                                        this.context
                                )
                        ).map(SpreadsheetText::text)
                        .orElse("")
        );
    }

    private Optional<Color> numberToColor(final Integer value) {
        return SpreadsheetText.WITHOUT_COLOR; // ignore the colour number
    }

    private Optional<Color> nameToColor(final SpreadsheetColorName name) {
        return SpreadsheetText.WITHOUT_COLOR; // ignore the colour name.
    }

    /**
     * Characters, strings and other non numeric values like {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection}
     * are formatted as their plain text ignoring the pattern.
     */
    private void formatText(final Object value) {
        this.formatted = value.toString();
    }

    /**
     * A pattern, depending on the value this will be used to create a sub class of {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern}.
     */
    private final String pattern;

    private final SpreadsheetConverterContext context;

    private String formatted;

    @Override
    public String toString() {
        return this.formatted;
    }
}
