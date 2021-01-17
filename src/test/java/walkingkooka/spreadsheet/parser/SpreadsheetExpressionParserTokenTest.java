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
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class SpreadsheetExpressionParserTokenTest extends SpreadsheetParentParserTokenTestCase<SpreadsheetExpressionParserToken> {

    @Test
    public void testWithZeroTokensFails() {
        this.createToken(" k ");
    }

    @Test
    public void testWithTwoTokensFails() {
        this.createToken(NUMBER1 + NUMBER2, this.number1(), this.number2());
    }

    @Test
    public void testWith() {
        final String text = "(" + NUMBER1 + ")";
        final SpreadsheetExpressionParserToken token = this.createToken(text, this.number1());
        this.textAndCheck(token, text);
    }

    @Test
    public void testWithSymbols() {
        final String text = "(" + NUMBER1 + ")";

        final SpreadsheetParenthesisOpenSymbolParserToken left = openParenthesisSymbol();
        final SpreadsheetParserToken number = this.number1();
        final SpreadsheetParenthesisCloseSymbolParserToken right = closeParenthesisSymbol();

        final SpreadsheetExpressionParserToken token = this.createToken(text, left, number, right);
        this.textAndCheck(token, text);
        this.checkValue(token, left, number, right);
    }

    @Test
    public void testWithSymbols2() {
        final String text = "( " + NUMBER1 + " )";

        final SpreadsheetParenthesisOpenSymbolParserToken left = this.openParenthesisSymbol();
        final SpreadsheetWhitespaceParserToken whitespace1 = this.whitespace();
        final SpreadsheetParserToken number = this.number1();
        final SpreadsheetWhitespaceParserToken whitespace2 = this.whitespace();
        final SpreadsheetParenthesisCloseSymbolParserToken right = this.closeParenthesisSymbol();

        final SpreadsheetExpressionParserToken token = this.createToken(text, left, whitespace1, number, whitespace2, right);
        this.textAndCheck(token, text);
        this.checkValue(token, left, whitespace1, number, whitespace2, right);
    }

    @Test
    public void testToExpression() {
        this.toExpressionAndCheck(Expression.expressionNumber(this.expressionNumber(NUMBER1)));
    }

    @Test
    public void testToExpressionAddition() {
        final ExpressionNumber number1 = this.expressionNumber(1);
        final ExpressionNumber number2 = this.expressionNumber(2);

        this.toExpressionAndCheck(
                SpreadsheetExpressionParserToken.with(
                        Lists.of(
                                equalsSymbol(),
                                SpreadsheetParserToken.addition(
                                        Lists.of(
                                                SpreadsheetParserToken.expressionNumber(number1, "1"),
                                                SpreadsheetParserToken.plusSymbol("+", "+"),
                                                SpreadsheetParserToken.expressionNumber(number2, "2")
                                        ),
                                        "1+2"
                                )
                        ),
                        "=1+2"
                ),
                Expression.add(
                        Expression.expressionNumber(number1),
                        Expression.expressionNumber(number2)
                )
        );
    }

    @Test
    public void testToExpressionWhitespaceAddition() {
        final ExpressionNumber number1 = this.expressionNumber(1);
        final ExpressionNumber number2 = this.expressionNumber(2);

        this.toExpressionAndCheck(
                SpreadsheetExpressionParserToken.with(
                        Lists.of(
                                equalsSymbol(),
                                whitespace(),
                                SpreadsheetParserToken.addition(
                                        Lists.of(
                                                SpreadsheetParserToken.expressionNumber(number1, "1"),
                                                SpreadsheetParserToken.plusSymbol("+", "+"),
                                                SpreadsheetParserToken.expressionNumber(number2, "2")
                                        ),
                                        "1+2"
                                )
                        ),
                        "= 1+2"
                ),
                Expression.add(
                        Expression.expressionNumber(number1),
                        Expression.expressionNumber(number2)
                )
        );
    }

    @Override
    SpreadsheetExpressionParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetParserToken.expression(tokens, text);
    }

    @Override
    public String text() {
        return "=" + NUMBER1;
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.equalsSymbol(), this.number1());
    }

    @Override
    public SpreadsheetExpressionParserToken createDifferentToken() {
        return this.createToken("=" + NUMBER2, this.equalsSymbol(), this.number2());
    }

    @Override
    public Class<SpreadsheetExpressionParserToken> type() {
        return SpreadsheetExpressionParserToken.class;
    }

    @Override
    public SpreadsheetExpressionParserToken unmarshall(final JsonNode from,
                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallExpression(from, context);
    }
}