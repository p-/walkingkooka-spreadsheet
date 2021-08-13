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

package walkingkooka.spreadsheet.reference;

import java.util.Optional;

/**
 * Each of the {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection} require an anchor to create a {@link SpreadsheetViewportSelection}.
 * Not all combinations are valid for each of range.
 */
public enum SpreadsheetViewportSelectionAnchor {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    TOP,
    BOTTOM,
    LEFT,
    RIGHT;

    /**
     * Factory method that should only be called by a range.
     */
    SpreadsheetViewportSelection setSelection(final SpreadsheetSelection selection) {
        return SpreadsheetViewportSelection.with(selection, Optional.of(this));
    }
}