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

import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

/**
 * A {@link Converter} that only selects converting values to a {@link String} using the given pattern
 * to create the appropriate {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern}.
 * <br>
 * This {@link Converter} exists to support functions like text where the user provides a value and a pattern.
 */
final class FormatPatternConverter implements Converter<ExpressionNumberConverterContext> {

    static FormatPatternConverter with(final String pattern) {
        CharSequences.failIfNullOrEmpty(pattern, "pattern");

        return new FormatPatternConverter(pattern);
    }

    private FormatPatternConverter(final String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final ExpressionNumberConverterContext context) {
        return String.class == type || context.canConvert(value, type);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final ExpressionNumberConverterContext context) {
        return this.canConvert(value, type, context) ?
                this.successfulConversion(
                        this.formatUsingPattern(value, context),
                        type
                ) :
                context.convert(value, type);
    }

    private String formatUsingPattern(final Object value,
                                      final ExpressionNumberConverterContext context) {
        return FormatPatternConverterSpreadsheetValueVisitor.format(
                value,
                this.pattern,
                context
        );
    }

    private final String pattern;

    @Override
    public String toString() {
        return this.pattern;
    }
}