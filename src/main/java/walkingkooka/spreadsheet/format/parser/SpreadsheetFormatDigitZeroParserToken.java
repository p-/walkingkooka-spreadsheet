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
package walkingkooka.spreadsheet.format.parser;


import java.util.Optional;

/**
 * Represents the digit including leading zeros.
 */
public final class SpreadsheetFormatDigitZeroParserToken extends SpreadsheetFormatNonSymbolParserToken<String> {

    static SpreadsheetFormatDigitZeroParserToken with(final String value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetFormatDigitZeroParserToken(value, text);
    }

    private SpreadsheetFormatDigitZeroParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    void accept(final SpreadsheetFormatParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetFormatParserTokenKind ................................................................................

    @Override
    public Optional<SpreadsheetFormatParserTokenKind> kind(final boolean minute) {
        return SpreadsheetFormatParserTokenKind.DIGIT_ZERO.asOptional;
    }

    // equals ..........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatDigitZeroParserToken;
    }

}
