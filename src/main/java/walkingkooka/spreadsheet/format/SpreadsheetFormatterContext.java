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

import walkingkooka.Either;
import walkingkooka.color.Color;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

import java.util.Optional;

/**
 * A {@link }Context} that accompanies a value format, holding local sensitive attributes such as the decimal point character.
 */
public interface SpreadsheetFormatterContext extends ExpressionNumberConverterContext {

    /**
     * Returns the {@link Color} with the given number.
     */
    Optional<Color> colorNumber(final int number);

    /**
     * Returns the {@link Color} with the given name.
     */
    Optional<Color> colorName(final SpreadsheetColorName name);

    /**
     * The width of the "cell" in characters.
     * This value affects STAR operator.
     */
    int width();

    /**
     * Provides a default format text.
     */
    Optional<SpreadsheetText> defaultFormatText(final Object value);

    /**
     * Tests if the given value can be converted the requested target {@link Class type}.
     */
    boolean canConvert(final Object value, final Class<?> target);

    /**
     * Handles converting the given value to the target.
     */
    <T> Either<T, String> convert(final Object value, final Class<T> target);

    /**
     * Converts the given value to the {@link Class target type} or throws a {@link SpreadsheetFormatException}
     */
    default <T> T convertOrFail(final Object value,
                                final Class<T> target) {
        final Either<T, String> converted = this.convert(value, target);
        if (converted.isRight()) {
            throw new SpreadsheetFormatException(converted.rightValue());
        }

        return converted.leftValue();
    }
}
