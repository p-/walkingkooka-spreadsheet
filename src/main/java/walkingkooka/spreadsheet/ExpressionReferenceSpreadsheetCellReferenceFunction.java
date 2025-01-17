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

package walkingkooka.spreadsheet;

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Resolves an {@link ExpressionReference} to a {@link SpreadsheetCellReference}.
 */
final class ExpressionReferenceSpreadsheetCellReferenceFunction implements Function<SpreadsheetExpressionReference, Optional<SpreadsheetCellReference>> {

    static ExpressionReferenceSpreadsheetCellReferenceFunction with(final SpreadsheetLabelStore labelStore,
                                                                    final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCellStore) {
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(rangeToCellStore, "rangeToCellStore");

        return new ExpressionReferenceSpreadsheetCellReferenceFunction(labelStore, rangeToCellStore);
    }

    private ExpressionReferenceSpreadsheetCellReferenceFunction(final SpreadsheetLabelStore labelStore,
                                                                final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCellStore) {
        this.labelStore = labelStore;
        this.rangeToCellStore = rangeToCellStore;
    }

    @Override
    public Optional<SpreadsheetCellReference> apply(final SpreadsheetExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        return ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetSelectionVisitor.toSpreadsheetCellReference(
                reference,
                this
        );
    }

    final SpreadsheetLabelStore labelStore;
    final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCellStore;

    @Override
    public String toString() {
        return ExpressionReference.class.getSimpleName() + "->" + SpreadsheetCellReference.class.getSimpleName() + "(" + this.labelStore + " " + this.rangeToCellStore + ")";
    }
}
