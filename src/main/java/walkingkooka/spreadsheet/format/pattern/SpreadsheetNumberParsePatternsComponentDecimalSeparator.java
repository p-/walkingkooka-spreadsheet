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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.text.cursor.TextCursor;

/**
 * A {@link SpreadsheetNumberParsePatternsComponent} that matches any decimal separators.
 */
final class SpreadsheetNumberParsePatternsComponentDecimalSeparator extends SpreadsheetNumberParsePatternsComponent2 {

    /**
     * Singleton
     */
    final static SpreadsheetNumberParsePatternsComponentDecimalSeparator INSTANCE = new SpreadsheetNumberParsePatternsComponentDecimalSeparator();

    private SpreadsheetNumberParsePatternsComponentDecimalSeparator() {
        super();
    }

    @Override
    void parse(final TextCursor cursor,
               final SpreadsheetNumberParsePatternsRequest request) {
        if (false == cursor.isEmpty()) {
            if (request.context.decimalSeparator() == cursor.at()) {
                request.mode.onDecimalSeparator(request);
                cursor.next();
                request.nextComponent(cursor);
            }
        }
    }

    @Override
    public String toString() {
        return ".";
    }
}
