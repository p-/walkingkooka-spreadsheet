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

package walkingkooka.spreadsheet.meta;

import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.math.RoundingMode;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for {@link RoundingMode} entries.
 */
final class RoundingModeSpreadsheetMetadataPropertyValueHandler extends SpreadsheetMetadataPropertyValueHandler<RoundingMode> {

    /**
     * A singleton
     */
    static final RoundingModeSpreadsheetMetadataPropertyValueHandler INSTANCE = new RoundingModeSpreadsheetMetadataPropertyValueHandler();

    private RoundingModeSpreadsheetMetadataPropertyValueHandler() {
        super();
    }

    @Override
    void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        this.checkType(value, RoundingMode.class, name);
    }

    @Override
    String expectedTypeName(final Class<?> type) {
        return RoundingMode.class.getSimpleName();
    }

    @Override
    public String toString() {
        return RoundingMode.class.getSimpleName();
    }

    // HasJsonNode......................................................................................................

    @Override
    RoundingMode fromJsonNode(final JsonNode node, final SpreadsheetMetadataPropertyName<?> name) {
        return node.fromJsonNode(RoundingMode.class);
    }

    @Override
    JsonNode toJsonNode(final RoundingMode value) {
        return HasJsonNode.toJsonNodeObject(value);
    }
}