
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

package walkingkooka.spreadsheet.reference.store;

import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.StandardThrowableTesting;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionReference;

public final class SpreadsheetExpressionReferenceLoadStoreExceptionTest implements StandardThrowableTesting<SpreadsheetExpressionReferenceLoadStoreException> {

    private final static ExpressionReference EXPRESSION_REFERENCE = new FakeExpressionReference() {
    };

    @Override
    public SpreadsheetExpressionReferenceLoadStoreException createThrowable(final String message) {
        return new SpreadsheetExpressionReferenceLoadStoreException(message, EXPRESSION_REFERENCE);
    }

    @Override
    public SpreadsheetExpressionReferenceLoadStoreException createThrowable(final String message, final Throwable cause) {
        return new SpreadsheetExpressionReferenceLoadStoreException(message, EXPRESSION_REFERENCE, cause);
    }

    @Override
    public Class<SpreadsheetExpressionReferenceLoadStoreException> type() {
        return SpreadsheetExpressionReferenceLoadStoreException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
