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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;

import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPattern extends SpreadsheetMetadataPropertyName<SpreadsheetNumberFormatPattern> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPattern instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPattern();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPattern() {
        super("number-format-pattern");
    }

    @Override
    SpreadsheetNumberFormatPattern checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetNumberFormatPattern);
    }

    @Override
    String expected() {
        return "Number format pattern";
    }

    @Override
    void accept(final SpreadsheetNumberFormatPattern value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitNumberFormatPattern(value);
    }

    @Override
    Optional<SpreadsheetNumberFormatPattern> extractLocaleValue(final Locale locale) {
        return Optional.of(
                SpreadsheetPattern.numberFormatPatternLocale(locale)
        );
    }

    @Override
    Class<SpreadsheetNumberFormatPattern> type() {
        return SpreadsheetNumberFormatPattern.class;
    }

    @Override
    String compareToName() {
        return this.value();
    }
}
