
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

package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

public interface SpreadsheetRowStoreTesting<S extends SpreadsheetRowStore> extends SpreadsheetColumnOrRowStoreTesting<S, SpreadsheetRowReference, SpreadsheetRow> {

    @Test
    default void testUpSkipHiddenFirstRow() {
        this.upSkipHiddenAndCheck(
                this.createStore(),
                SpreadsheetReferenceKind.RELATIVE.firstRow()
        );
    }

    default void upSkipHiddenAndCheck(final S store,
                                      final String reference) {
        this.upSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseRow(reference)
        );
    }

    default void upSkipHiddenAndCheck(final S store,
                                      final String reference,
                                      final String expected) {
        this.upSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseRow(reference),
                SpreadsheetSelection.parseRow(expected)
        );
    }

    default void upSkipHiddenAndCheck(final S store,
                                      final SpreadsheetRowReference reference) {
        this.upSkipHiddenAndCheck(
                store,
                reference,
                reference
        );
    }

    default void upSkipHiddenAndCheck(final S store,
                                      final SpreadsheetRowReference reference,
                                      final SpreadsheetRowReference expected) {
        this.checkEquals(
                expected,
                store.upSkipHidden(reference),
                () -> reference + " upSkipHidden " + store
        );
    }

}
