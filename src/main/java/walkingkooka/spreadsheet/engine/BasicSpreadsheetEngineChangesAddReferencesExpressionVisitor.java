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

package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.ExpressionVisitor;
import walkingkooka.tree.expression.ReferenceExpression;

/**
 * Accepts an {@link Expression} passes all {@link ExpressionReference} to a {@link walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor}.
 */
final class BasicSpreadsheetEngineChangesAddReferencesExpressionVisitor extends ExpressionVisitor {

    static void processReferences(final Expression node,
                                  final SpreadsheetCellReference target,
                                  final SpreadsheetEngineContext context) {
        new BasicSpreadsheetEngineChangesAddReferencesExpressionVisitor(target, context)
                .accept(node);
    }

    // VisibleForTesting
    BasicSpreadsheetEngineChangesAddReferencesExpressionVisitor(final SpreadsheetCellReference target,
                                                                final SpreadsheetEngineContext context) {
        super();
        this.target = target;
        this.context = context;
    }

    @Override
    protected void visit(final ReferenceExpression node) {
        if (null == this.visitor) {
            this.visitor = BasicSpreadsheetEngineChangesAddReferencesExpressionVisitorSpreadsheetSelectionVisitor.with(
                    this.target,
                    this.context
            );
        }
        this.visitor.accept((SpreadsheetExpressionReference) node.value());
    }

    /**
     * The target cell.
     */
    private final SpreadsheetCellReference target;

    private final SpreadsheetEngineContext context;

    /**
     * Cache of the {@link BasicSpreadsheetEngineChangesAddReferencesExpressionVisitorSpreadsheetSelectionVisitor} that will process each and every encountered {@link ReferenceExpression}.
     */
    private BasicSpreadsheetEngineChangesAddReferencesExpressionVisitorSpreadsheetSelectionVisitor visitor;

    @Override
    public String toString() {
        return this.target.toString();
    }
}
