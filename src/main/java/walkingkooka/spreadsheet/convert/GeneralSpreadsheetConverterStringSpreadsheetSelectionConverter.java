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

import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

/**
 * A {@link Converter} that handles converting {@link String} into {@link SpreadsheetSelection} using the right
 * {@link SpreadsheetSelection} parseXXX method.
 */
final class GeneralSpreadsheetConverterStringSpreadsheetSelectionConverter implements Converter<SpreadsheetConverterContext> {

    final static GeneralSpreadsheetConverterStringSpreadsheetSelectionConverter INSTANCE = new GeneralSpreadsheetConverterStringSpreadsheetSelectionConverter();

    private GeneralSpreadsheetConverterStringSpreadsheetSelectionConverter() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return (value instanceof Character || value instanceof String) &&
                (type == SpreadsheetCellReference.class ||
                        type == SpreadsheetCellReferenceOrRange.class ||
                        type == SpreadsheetCellRange.class ||
                        type == SpreadsheetColumnReference.class ||
                        type == SpreadsheetColumnReferenceRange.class ||
                        type == SpreadsheetLabelName.class ||
                        type == SpreadsheetRowReference.class ||
                        type == SpreadsheetRowReferenceRange.class);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final SpreadsheetConverterContext context) {
        return this.canConvert(value, type, context) ?
                this.successfulConversion(
                        GeneralSpreadsheetConverterStringSpreadsheetSelectionConverterSpreadsheetValueTypeVisitor.parse(
                                (String) value,
                                Cast.to(type),
                                context
                        ),
                        type
                ) :
                this.failConversion(
                        value,
                        type
                );
    }

    @Override
    public String toString() {
        return "String->" + SpreadsheetSelection.class.getSimpleName();
    }
}
