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

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;

abstract class SpreadsheetMetadataPropertyNameCharacter extends SpreadsheetMetadataPropertyName<Character> {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetMetadataPropertyNameCharacter(final String name) {
        super(name);
    }

    @Override
    final void checkValue0(final Object value) {
        this.checkValueType(value,
                v -> v instanceof Character);
    }

    @Override
    final String expected() {
        return Character.class.getSimpleName();
    }

    @Override
    final Optional<Character> extractLocaleValue(final Locale locale) {
        return Optional.of(this.extractLocaleValueCharacter(DecimalFormatSymbols.getInstance(locale)));
    }

    /**
     * Template method that supports sub classing retrieving a single property from the {@link DecimalFormatSymbols}
     */
    abstract Character extractLocaleValueCharacter(final DecimalFormatSymbols symbols);

    @Override
    Class<Character> type() {
        return Character.class;
    }
}