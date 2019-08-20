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

import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;

/**
 * A {@link Converter} which formats a value to {@link String text} using the given {@link SpreadsheetFormatter}.
 */
final class SpreadsheetFormatterConverter implements Converter {

    static SpreadsheetFormatterConverter with(final SpreadsheetFormatter formatter) {
        return new SpreadsheetFormatterConverter(formatter);
    }

    private SpreadsheetFormatterConverter(final SpreadsheetFormatter formatter) {
        super();

        this.formatter = formatter;
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> targetType,
                              final ConverterContext context) {
        return String.class == targetType && this.formatter.canFormat(value);
    }

    @Override
    public <T> T convert(final Object value,
                         final Class<T> targetType,
                         final ConverterContext context) {
        return targetType.cast(this.formatter.format(value, SpreadsheetFormatterConverterSpreadsheetFormatterContext.with(context))
                .map(SpreadsheetText::text)
                .orElseThrow(() -> {
                    this.failConversion(value, targetType);
                    return null;
                }));
    }

    private final SpreadsheetFormatter formatter;

    @Override
    public String toString() {
        return this.formatter.toString();
    }
}