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

package walkingkooka.spreadsheet.meta;

import walkingkooka.Cast;
import walkingkooka.color.Color;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTextFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePattern;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.visit.Visiting;
import walkingkooka.visit.Visitor;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * A {@link Visitor} which dispatches each {@link SpreadsheetMetadataPropertyName} to a visit method which accepts the accompanying
 * value. Note it does not visit default properties only those immediately set upon the given {@link SpreadsheetMetadata}.
 */
public abstract class SpreadsheetMetadataVisitor extends Visitor<SpreadsheetMetadata> {

    protected SpreadsheetMetadataVisitor() {
    }

    // Visitor..........................................................................................................

    @Override
    public final void accept(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        if (Visiting.CONTINUE == this.startVisit(metadata)) {
            metadata.accept(this);
        }
        this.endVisit(metadata);
    }

    // SpreadsheetMetadata........................................................................................................

    protected Visiting startVisit(final SpreadsheetMetadata metadata) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetMetadata metadata) {
        // nop
    }

    // entries..........................................................................................................

    final void acceptPropertyAndValue(final Entry<SpreadsheetMetadataPropertyName<?>, Object> entry) {
        final SpreadsheetMetadataPropertyName<?> propertyName = entry.getKey();
        final Object value = entry.getValue();

        if (Visiting.CONTINUE == this.startVisit(propertyName, value)) {
            propertyName.accept(Cast.to(value), this);
        }
        this.endVisit(propertyName, value);
    }

    protected Visiting startVisit(final SpreadsheetMetadataPropertyName<?> property, final Object value) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetMetadataPropertyName<?> property, final Object value) {
    }

    // properties..........................................................................................................

    protected void visitCellCharacterWidth(final int value) {
        // nop
    }

    protected void visitCreateDateTime(final LocalDateTime dateTime) {
        // nop
    }

    protected void visitCreator(final EmailAddress emailAddress) {
        // nop
    }

    protected void visitCurrencySymbol(final String currencySymbol) {
        // nop
    }

    protected void visitDateFormatPattern(final SpreadsheetDateFormatPattern pattern) {
        // nop
    }

    protected void visitDateParsePattern(final SpreadsheetDateParsePattern patterns) {
        // nop
    }

    protected void visitDateTimeFormatPattern(final SpreadsheetDateTimeFormatPattern pattern) {
        // nop
    }

    protected void visitDateTimeOffset(final long offset) {
        // nop
    }

    protected void visitDateTimeParsePattern(final SpreadsheetDateTimeParsePattern patterns) {
        // nop
    }

    protected void visitDecimalSeparator(final char decimalSeparator) {
        // nop
    }

    protected void visitDefaultYear(final int defaultYear) {
        // nop
    }

    protected void visitExponentSymbol(final String exponentSymbol) {
        // nop
    }

    protected void visitExpressionNumberKind(final ExpressionNumberKind expressionNumberKind) {
        // nop
    }

    protected void visitFrozenColumns(final SpreadsheetColumnReferenceRange range) {
        // nop
    }

    protected void visitFrozenRows(final SpreadsheetRowReferenceRange range) {
        // nop
    }

    protected void visitGroupingSeparator(final char groupingSeparator) {
        // nop
    }

    protected void visitLocale(final Locale locale) {
        // nop
    }

    protected void visitModifiedBy(final EmailAddress emailAddress) {
        // nop
    }

    protected void visitModifiedDateTime(final LocalDateTime dateTime) {
        // nop
    }

    protected void visitNegativeSign(final char negativeSign) {
        // nop
    }

    protected void visitNamedColor(final SpreadsheetColorName name, final Color color) {
        // nop
    }

    protected void visitNumberedColor(final int number, final Color color) {
        // nop
    }

    protected void visitNumberFormatPattern(final SpreadsheetNumberFormatPattern pattern) {
        // nop
    }

    protected void visitNumberParsePattern(final SpreadsheetNumberParsePattern patterns) {
        // nop
    }

    protected void visitPercentageSymbol(final char percentageSymbol) {
        // nop
    }

    protected void visitPositiveSign(final char positiveSign) {
        // nop
    }

    protected void visitPrecision(final int precision) {
        // nop
    }

    protected void visitRoundingMode(final RoundingMode roundingMode) {
        // nop
    }

    protected void visitSelection(final SpreadsheetViewportSelection selection) {
        // nop
    }

    protected void visitSpreadsheetId(final SpreadsheetId id) {
        // nop
    }

    protected void visitSpreadsheetName(final SpreadsheetName name) {
        // nop
    }

    protected void visitStyle(final TextStyle style) {
        // nop
    }

    protected void visitTextFormatPattern(final SpreadsheetTextFormatPattern pattern) {
        // nop
    }

    protected void visitTimeFormatPattern(final SpreadsheetTimeFormatPattern pattern) {
        // nop
    }

    protected void visitTimeParsePattern(final SpreadsheetTimeParsePattern patterns) {
        // nop
    }

    protected void visitTwoDigitYear(final int value) {
        // nop
    }

    protected void visitValueSeparator(final char valueSeparator) {
        // nop
    }

    protected void visitViewportCell(final SpreadsheetCellReference cell) {
        // nop
    }
}
