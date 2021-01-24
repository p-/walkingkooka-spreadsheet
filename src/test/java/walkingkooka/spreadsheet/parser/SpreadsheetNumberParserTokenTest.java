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
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetNumberParserTokenTest extends SpreadsheetNonSymbolParserTokenTestCase<SpreadsheetNumberParserToken, ExpressionNumber> {

    @Test
    public void testWithEmptyTextFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken(""));
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetNumberParserToken token = this.createToken();

        new FakeSpreadsheetParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final ParserToken t) {
                assertSame(token, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                assertSame(token, t);
                b.append("2");
            }

            @Override
            protected Visiting startVisit(final SpreadsheetParserToken t) {
                assertSame(token, t);
                b.append("3");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetParserToken t) {
                assertSame(token, t);
                b.append("4");
            }

            @Override
            protected void visit(final SpreadsheetNumberParserToken t) {
                assertSame(token, t);
                b.append("5");
            }
        }.accept(token);
        assertEquals("13542", b.toString());
    }

    @Test
    public void testToExpression() {
        this.toExpressionAndCheck(Expression.expressionNumber(this.value()));
    }

    @Override
    public String text() {
        return "123.5";
    }

    @Override
    ExpressionNumber value() {
        return EXPRESSION_NUMBER_KIND.create(new BigDecimal(this.text()));
    }

    @Override
    SpreadsheetNumberParserToken createToken(final ExpressionNumber value, final String text) {
        return SpreadsheetNumberParserToken.with(value, text);
    }

    @Override
    public SpreadsheetNumberParserToken createDifferentToken() {
        return SpreadsheetNumberParserToken.with(EXPRESSION_NUMBER_KIND.create(-1), "'different'");
    }

    @Override
    public Class<SpreadsheetNumberParserToken> type() {
        return SpreadsheetNumberParserToken.class;
    }

    @Override
    public SpreadsheetNumberParserToken unmarshall(final JsonNode from,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallNumber(from, context);
    }
}