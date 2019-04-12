package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.test.ClassTesting2;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.MemberVisibility;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public interface SpreadsheetEngineTesting<E extends SpreadsheetEngine> extends ClassTesting2<E> {

    SpreadsheetColumnReference COLUMN = SpreadsheetReferenceKind.ABSOLUTE.column(1);
    SpreadsheetRowReference ROW = SpreadsheetReferenceKind.ABSOLUTE.row(2);
    SpreadsheetCellReference REFERENCE = COLUMN.setRow(ROW);
    SpreadsheetLabelName LABEL = SpreadsheetLabelName.with("LABEL123");

    @Test
    default void testLoadCellNullCellFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().loadCell(null, SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY, this.createContext());
        });
    }

    @Test
    default void testLoadCellNullEvaluationFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().loadCell(REFERENCE,
                    null,
                    this.createContext());
        });
    }

    @Test
    default void testLoadCellNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().loadCell(REFERENCE,
                    SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                    null);
        });
    }

    @Test
    default void testSaveCellNullCellFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().saveCell(null,
                    this.createContext());
        });
    }

    @Test
    default void testSaveCellNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().saveCell(SpreadsheetCell.with(REFERENCE,
                    SpreadsheetFormula.with("1"),
                    SpreadsheetCellStyle.EMPTY),
                    null);
        });
    }

    @Test
    default void testDeleteCellNullCellFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteCell(null,
                    this.createContext());
        });
    }

    @Test
    default void testDeleteCellNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteCell(REFERENCE,
                    null);
        });
    }
    
    @Test
    default void testDeleteColumnsNullColumnFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteColumns(null, 1, this.createContext());
        });
    }

    @Test
    default void testDeleteColumnsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().deleteColumns(COLUMN, -1, this.createContext());
        });
    }

    @Test
    default void testDeleteColumnsNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteColumns(COLUMN, 1, null);
        });
    }

    @Test
    default void testDeleteRowsNullRowFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteRows(null, 1, this.createContext());
        });
    }

    @Test
    default void testDeleteRowsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().deleteRows(ROW, -1, this.createContext());
        });
    }

    @Test
    default void testDeleteRowsNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteRows(ROW, 1, null);
        });
    }

    @Test
    default void testInsertColumnsNullColumnFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().insertColumns(null, 1, this.createContext());
        });
    }

    @Test
    default void testInsertColumnsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().insertColumns(COLUMN, -1, this.createContext());
        });
    }

    @Test
    default void testInsertColumnsNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().insertColumns(COLUMN, 1, null);
        });
    }

    @Test
    default void testInsertRowsNullRowFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().insertRows(null, 1, this.createContext());
        });
    }

    @Test
    default void testInsertRowsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().insertRows(ROW, -1, this.createContext());
        });
    }

    @Test
    default void testInsertRowsNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().insertRows(ROW, 1, null);
        });
    }

    E createSpreadsheetEngine();

    SpreadsheetEngineContext createContext();

    default SpreadsheetCell loadCellOrFail(final SpreadsheetEngine engine,
                                           final SpreadsheetCellReference reference,
                                           final SpreadsheetEngineEvaluation evaluation,
                                           final SpreadsheetEngineContext context) {
        final Optional<SpreadsheetCell> cell = engine.loadCell(reference, evaluation, context);
        if (!cell.isPresent()) {
            fail("Loading " + reference + " should have succeeded");
        }
        return cell.get();
    }

    default void loadCellFailCheck(final SpreadsheetCellReference reference,
                                   final SpreadsheetEngineEvaluation evaluation) {
        this.loadCellFailCheck(reference, evaluation, this.createContext());
    }

    default void loadCellFailCheck(final SpreadsheetCellReference reference,
                                   final SpreadsheetEngineEvaluation evaluation,
                                   final SpreadsheetEngineContext context) {
        this.loadCellFailCheck(this.createSpreadsheetEngine(), reference, evaluation, context);
    }

    default void loadCellFailCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetCellReference reference,
                                   final SpreadsheetEngineContext context) {
        this.loadCellFailCheck(engine, reference, SpreadsheetEngineEvaluation.SKIP_EVALUATE, context);
    }

    default void loadCellFailCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetCellReference reference,
                                   final SpreadsheetEngineEvaluation evaluation,
                                   final SpreadsheetEngineContext context) {
        final Optional<SpreadsheetCell> cell = engine.loadCell(reference, evaluation, context);
        assertEquals(Optional.empty(),
                cell,
                () -> "Expected reference " + reference + " to fail");
    }

    default SpreadsheetCell loadCellAndCheckWithoutValueOrError(final SpreadsheetEngine engine,
                                                                final SpreadsheetCellReference reference,
                                                                final SpreadsheetEngineEvaluation evaluation,
                                                                final SpreadsheetEngineContext context) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, evaluation, context);
        assertEquals(null,
                this.valueOrError(cell, null),
                () -> "values from returned cells=" + cell);
        return cell;
    }

    default SpreadsheetCell loadCellAndCheckFormula(final SpreadsheetEngine engine,
                                                    final SpreadsheetCellReference reference,
                                                    final SpreadsheetEngineEvaluation evaluation,
                                                    final SpreadsheetEngineContext context,
                                                    final String formula) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, evaluation, context);
        this.checkFormula(cell, formula);
        return cell;
    }

    default SpreadsheetCell loadCellAndCheckFormulaAndValue(final SpreadsheetEngine engine,
                                                            final SpreadsheetCellReference reference,
                                                            final SpreadsheetEngineEvaluation evaluation,
                                                            final SpreadsheetEngineContext context,
                                                            final String formula,
                                                            final Object value) {
        final SpreadsheetCell cell = this.loadCellAndCheckFormula(engine, reference, evaluation, context, formula);
        this.checkValueOrError(cell, value);
        return cell;
    }

    default SpreadsheetCell loadCellAndCheckValue(final SpreadsheetEngine engine,
                                                  final SpreadsheetCellReference reference,
                                                  final SpreadsheetEngineEvaluation evaluation,
                                                  final SpreadsheetEngineContext context,
                                                  final Object value) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, evaluation, context);
        this.checkValueOrError(cell, value);
        return cell;
    }

    default SpreadsheetCell loadCellAndCheckFormatted(final SpreadsheetEngine engine,
                                                      final SpreadsheetCellReference reference,
                                                      final SpreadsheetEngineEvaluation evaluation,
                                                      final SpreadsheetEngineContext context,
                                                      final Object value,
                                                      final String text) {
        final SpreadsheetCell cell = this.loadCellAndCheckValue(engine, reference, evaluation, context, value);
        this.checkFormattedText(cell, text);
        return cell;
    }

    default void loadCellAndCheckError(final SpreadsheetEngine engine,
                                       final SpreadsheetCellReference reference,
                                       final SpreadsheetEngineEvaluation evaluation,
                                       final SpreadsheetEngineContext context,
                                       final String errorContains) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, evaluation, context);

        final Optional<SpreadsheetError> error = cell.formula().error();
        assertNotEquals(SpreadsheetFormula.NO_ERROR,
                error,
                () -> "Expected error missing=" + cell);
        assertTrue(error.get().value().contains(errorContains),
                () -> "Error message " + error + " missing " + CharSequences.quoteAndEscape(errorContains));
    }

    default void loadCellAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetCellReference reference,
                                  final SpreadsheetEngineEvaluation evaluation,
                                  final SpreadsheetEngineContext context,
                                  final SpreadsheetCell cell) {
        assertEquals(Optional.of(cell),
                engine.loadCell(reference, evaluation, context),
                () -> "loadCell " + reference);
    }

    default void loadLabelAndCheck(final SpreadsheetLabelStore labelStore,
                                   final SpreadsheetLabelName label,
                                   final ExpressionReference reference) {
        assertEquals(Optional.of(SpreadsheetLabelMapping.with(label, reference)),
                labelStore.load(label),
                "label loaded");
    }

    default void loadLabelFailCheck(final SpreadsheetLabelStore labelStore,
                                    final SpreadsheetLabelName label) {
        assertEquals(Optional.empty(),
                labelStore.load(label),
                "label loaded failed");
    }

    default void saveCellAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetCell save,
                                  final SpreadsheetEngineContext context,
                                  final SpreadsheetCell... updated) {
        this.saveCellAndCheck(engine, save, context, Sets.of(updated));
    }

    default void saveCellAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetCell save,
                                  final SpreadsheetEngineContext context,
                                  final Set<SpreadsheetCell> updated) {
        assertEquals(updated,
                engine.saveCell(save, context),
                () -> "saveCell " + save);
    }

    default void deleteCellAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetCellReference delete,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetCell... updated) {
        this.deleteCellAndCheck(engine, delete, context, Sets.of(updated));
    }

    default void deleteCellAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetCellReference delete,
                                    final SpreadsheetEngineContext context,
                                    final Set<SpreadsheetCell> updated) {
        assertEquals(updated,
                engine.deleteCell(delete, context),
                () -> "deleteCell " + delete);
    }

    default void countAndCheck(final Store<?, ?> store, final int count) {
        assertEquals(count,
                store.count(),
                "record count in " + store);
    }

    default void deleteColumnsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetColumnReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final SpreadsheetCell... updated) {
        this.deleteColumnsAndCheck(engine,
                column,
                count,
                context,
                Sets.of(updated));
    }

    default void deleteColumnsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetColumnReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final Set<SpreadsheetCell> updated) {
        assertEquals(updated,
                engine.deleteColumns(column, count, context),
                () -> "deleteColumns column: " + column + " count: " + count);
    }

    default void deleteRowsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetRowReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final SpreadsheetCell... updated) {
        this.deleteRowsAndCheck(engine,
                column,
                count,
                context,
                Sets.of(updated));
    }

    default void deleteRowsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetRowReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final Set<SpreadsheetCell> updated) {
        assertEquals(updated,
                engine.deleteRows(column, count, context),
                () -> "deleteRows column: " + column + " count: " + count);
    }

    default void insertColumnsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetColumnReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final SpreadsheetCell... updated) {
        this.insertColumnsAndCheck(engine,
                column,
                count,
                context,
                Sets.of(updated));
    }

    default void insertColumnsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetColumnReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final Set<SpreadsheetCell> updated) {
        assertEquals(updated,
                engine.insertColumns(column, count, context),
                () -> "insertColumns column: " + column + " count: " + count);
    }

    default void insertRowsAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetRowReference column,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetCell... updated) {
        this.insertRowsAndCheck(engine,
                column,
                count,
                context,
                Sets.of(updated));
    }

    default void insertRowsAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetRowReference column,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final Set<SpreadsheetCell> updated) {
        assertEquals(updated,
                engine.insertRows(column, count, context),
                () -> "insertRows column: " + column + " count: " + count);
    }

    default void copyCellsAndCheck(final SpreadsheetEngine engine,
                                   final Collection<SpreadsheetCell> from,
                                   final SpreadsheetRange to,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetCell... updated) {
        this.copyCellsAndCheck(engine, from, to, context, Sets.of(updated));
    }

    default void copyCellsAndCheck(final SpreadsheetEngine engine,
                                   final Collection<SpreadsheetCell> from,
                                   final SpreadsheetRange to,
                                   final SpreadsheetEngineContext context,
                                   final Set<SpreadsheetCell> updated) {
        assertEquals(updated,
                engine.copyCells(from, to, context),
                () -> "copyCells " + from + " to " + to);

        // load and check updated cells again...
        updated.forEach(c -> this.loadCellAndCheck(engine,
                c.reference(),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                c));
    }

    default void checkFormula(final SpreadsheetCell cell, final String formula) {
        assertEquals(formula,
                cell.formula().text(),
                () -> "formula.text from returned cell=" + cell);
    }

    default void checkValueOrError(final SpreadsheetCell cell, final Object value) {
        assertEquals(value,
                this.valueOrError(cell, "Value and Error absent (" + cell + ")"),
                () -> "values from returned cell=" + cell);
    }

    default Object valueOrError(final SpreadsheetCell cell, final Object bothAbsent) {
        final SpreadsheetFormula formula = cell.formula();
        return formula.value()
                .orElse(formula.error()
                        .map(e -> (Object) e.value())
                        .orElse(bothAbsent));
    }

    default void checkFormattedText(final SpreadsheetCell cell, final String text) {
        assertNotEquals(Optional.empty(), cell.formatted(), "formatted text absent");
        assertEquals(text, cell.formatted().get().text(), "formattedText");
    }

    default DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic("$", '.', 'E', ',', '-', '%', '+');
    }

    @Override
    default MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
