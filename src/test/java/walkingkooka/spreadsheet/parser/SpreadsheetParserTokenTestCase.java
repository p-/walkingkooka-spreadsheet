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

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticFactoryTesting;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokenTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.math.MathContext;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetParserTokenTestCase<T extends SpreadsheetParserToken> implements ClassTesting2<T>,
        IsMethodTesting<T>,
        JsonNodeMarshallingTesting<T>,
        ParserTokenTesting<T> {

    final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;
    final static ExpressionNumberContext EXPRESSION_NUMBER_CONTEXT = ExpressionNumberContexts.basic(EXPRESSION_NUMBER_KIND, MathContext.DECIMAL32);

    SpreadsheetParserTokenTestCase() {
        super();
    }

    @Test
    public final void testPublicStaticFactoryMethod() {
        PublicStaticFactoryTesting.checkFactoryMethods(SpreadsheetParserToken.class,
                "Spreadsheet",
                ParserToken.class.getSimpleName(),
                this.type());
    }

    @Test
    public final void testWithEmptyTextFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken(""));
    }

    @Test
    public void testWithWhitespaceTextFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken("   "));
    }

    @Test
    public final void testAccept2() {
        new SpreadsheetParserTokenVisitor() {
        }.accept(this.createToken());
    }

    final void toExpressionAndFail() {
        this.toExpressionAndFail(this.createToken());
    }

    final void toExpressionAndFail(final T token) {
        final Optional<Expression> node = token.expression(EXPRESSION_NUMBER_CONTEXT);
        assertEquals(Optional.empty(), node, "toExpression");
    }

    final void toExpressionAndCheck(final Expression expected) {
        this.toExpressionAndCheck(this.createToken(), expected);
    }

    final void toExpressionAndCheck(final T token, final Expression expected) {
        final Optional<Expression> node = token.expression(EXPRESSION_NUMBER_CONTEXT);
        assertEquals(Optional.of(expected), node, "toExpression");
    }

    // IsMethodTesting.................................................................................................

    @Override
    public final T createIsMethodObject() {
        return this.createToken();
    }

    @Override
    public final String isMethodTypeNamePrefix() {
        return "Spreadsheet";
    }

    @Override
    public final String isMethodTypeNameSuffix() {
        return ParserToken.class.getSimpleName();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return (m) -> m.equals("isNoise") || m.equals("isSymbol");
    }

    // JsonNodeMarshallTesting..........................................................................................

    @Override
    public final T createJsonNodeMappingValue() {
        return this.createToken();
    }

    // ClassTestCase..............................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
