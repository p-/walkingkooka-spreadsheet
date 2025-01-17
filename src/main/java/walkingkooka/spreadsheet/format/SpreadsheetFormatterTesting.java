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

import walkingkooka.test.Testing;
import walkingkooka.text.CharSequences;

import java.util.Optional;

/**
 * Mixin interface with default methods to assist testing of a given {@link SpreadsheetFormatter}.
 */
public interface SpreadsheetFormatterTesting extends Testing {

    default void canFormatAndCheck(final SpreadsheetFormatter formatter,
                                   final Object value,
                                   final SpreadsheetFormatterContext context,
                                   final boolean expected) {
        this.checkEquals(expected,
                formatter.canFormat(value, context),
                () -> formatter + " canFormat " + CharSequences.quoteIfChars(value));
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Object value,
                                final SpreadsheetFormatterContext context,
                                final String text) {
        this.formatAndCheck(formatter,
                value,
                context,
                SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, text));
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Object value,
                                final SpreadsheetFormatterContext context,
                                final SpreadsheetText text) {
        this.formatAndCheck(
                formatter,
                value,
                context,
                Optional.of(text)
        );
    }

    // format fail and check............................................................................................

    default void formatFailAndCheck(final SpreadsheetFormatter formatter,
                                    final Object value,
                                    final SpreadsheetFormatterContext context) {
        this.formatAndCheck(
                formatter,
                value,
                context,
                Optional.empty()
        );
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Object value,
                                final SpreadsheetFormatterContext context,
                                final Optional<SpreadsheetText> text) {
        this.checkEquals(
                text,
                formatter.format(value, context),
                () -> formatter + " " + CharSequences.quoteIfChars(value)
        );
    }
}
