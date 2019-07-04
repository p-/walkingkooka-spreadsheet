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

package walkingkooka.spreadsheet.parser;

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.type.JavaVisibility;

public abstract class SpreadsheetParserTestCase<P extends Parser<SpreadsheetParserContext>,
        T extends SpreadsheetParserToken>
        implements ClassTesting2<P>,
        ParserTesting<P, SpreadsheetParserContext>,
        TypeNameTesting<P> {

    SpreadsheetParserTestCase() {
        super();
    }

    @Override
    public final SpreadsheetParserContext createContext() {
        return SpreadsheetParserContexts.basic(this.decimalNumberContext());
    }

    @Override
    public final String parserTokenToString(final ParserToken token) {
        return SpreadsheetParserPrettySpreadsheetParserTokenVisitor.toString(token);
    }

    // TypeNameTesting .........................................................................................

    @Override
    public final String typeNamePrefix() {
        return "Spreadsheet";
    }

    @Override
    public final String typeNameSuffix() {
        return Parser.class.getSimpleName();
    }

    // ClassTestCase .........................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}