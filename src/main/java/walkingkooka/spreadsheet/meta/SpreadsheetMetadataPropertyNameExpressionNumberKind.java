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

import walkingkooka.tree.expression.ExpressionNumberKind;

import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameExpressionNumberKind extends SpreadsheetMetadataPropertyName<ExpressionNumberKind> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameExpressionNumberKind instance() {
        return new SpreadsheetMetadataPropertyNameExpressionNumberKind();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameExpressionNumberKind() {
        super("expression-number-kind");
    }

    @Override
    void checkValue0(final Object value) {
        this.checkValueType(value,
                v -> v instanceof ExpressionNumberKind);
    }

    @Override
    String expected() {
        return ExpressionNumberKind.class.getSimpleName();
    }

    @Override
    void accept(final ExpressionNumberKind value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitExpressionNumberKind(value);
    }

    @Override
    Optional<ExpressionNumberKind> extractLocaleValue(final Locale locale) {
        return Optional.empty(); // ExpressionNumberKind have nothing todo with Locales
    }

    @Override
    Class<ExpressionNumberKind> type() {
        return ExpressionNumberKind.class;
    }
}