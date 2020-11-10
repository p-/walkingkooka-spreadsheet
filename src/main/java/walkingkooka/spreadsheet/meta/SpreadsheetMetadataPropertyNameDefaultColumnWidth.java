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

final class SpreadsheetMetadataPropertyNameDefaultColumnWidth extends SpreadsheetMetadataPropertyNameDouble {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameDefaultColumnWidth instance() {
        return new SpreadsheetMetadataPropertyNameDefaultColumnWidth();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameDefaultColumnWidth() {
        super("default-column-width");
    }

    @Override
    void checkValue0(final Object value) {
        final Double doubleValue = this.checkValueType(value,
                v -> v instanceof Double);
        if (doubleValue <= 0) {
            throw new SpreadsheetMetadataPropertyValueException("Expected double > 0", this, doubleValue);
        }
    }

    @Override
    void accept(final Double value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitDefaultColumnWidth(value);
    }
}
