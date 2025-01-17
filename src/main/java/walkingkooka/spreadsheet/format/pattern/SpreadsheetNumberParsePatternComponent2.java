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

/**
 * Base {@link SpreadsheetNumberParsePatternComponent} for all non digit {@link SpreadsheetNumberParsePatternComponent}.
 */
abstract class SpreadsheetNumberParsePatternComponent2 extends SpreadsheetNumberParsePatternComponent {

    SpreadsheetNumberParsePatternComponent2() {
        super();
    }

    /**
     * This method should never be invoked on non digit components.
     */
    @Override
    final SpreadsheetNumberParsePatternComponent lastDigit(final SpreadsheetNumberParsePatternComponentDigitMode mode) {
        throw new UnsupportedOperationException();
    }
}
