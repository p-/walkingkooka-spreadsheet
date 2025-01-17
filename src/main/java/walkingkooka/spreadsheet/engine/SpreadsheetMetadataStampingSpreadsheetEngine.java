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

import walkingkooka.Cast;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.SpreadsheetViewport;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Wraps a {@link SpreadsheetEngine} that conditionally calls a {@link Function} to stamp and save the {@link SpreadsheetMetadata}.
 * This is particularly useful to update the last modified user and timestamp.
 */
final class SpreadsheetMetadataStampingSpreadsheetEngine implements SpreadsheetEngine {

    static SpreadsheetMetadataStampingSpreadsheetEngine with(final SpreadsheetEngine engine,
                                                             final Function<SpreadsheetMetadata, SpreadsheetMetadata> stamper) {
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(stamper, "stamper");

        return new SpreadsheetMetadataStampingSpreadsheetEngine(engine, stamper);
    }

    private SpreadsheetMetadataStampingSpreadsheetEngine(final SpreadsheetEngine engine,
                                                         final Function<SpreadsheetMetadata, SpreadsheetMetadata> stamper) {
        super();
        this.engine = engine;
        this.stamper = stamper;
    }

    @Override
    public SpreadsheetDelta loadCells(final SpreadsheetSelection selection,
                                      final SpreadsheetEngineEvaluation evaluation,
                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                      final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.loadCells(
                        selection,
                        evaluation,
                        deltaProperties,
                        context
                ),
                context
        );
    }

    @Override
    public SpreadsheetDelta loadCells(final Set<SpreadsheetCellRange> range,
                                      final SpreadsheetEngineEvaluation evaluation,
                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                      final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.loadCells(
                        range,
                        evaluation,
                        deltaProperties,
                        context
                ),
                context
        );
    }


    @Override
    public SpreadsheetDelta saveCell(final SpreadsheetCell cell,
                                     final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.saveCell(cell, context),
                context
        );
    }

    @Override
    public SpreadsheetDelta saveCells(final Set<SpreadsheetCell> cells,
                                      final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.saveCells(cells, context),
                context
        );
    }

    @Override
    public SpreadsheetDelta deleteCells(final SpreadsheetSelection cells,
                                        final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.deleteCells(cells, context),
                context
        );
    }

    @Override
    public SpreadsheetDelta loadColumn(final SpreadsheetColumnReference column,
                                       final SpreadsheetEngineContext context) {
        return this.engine.loadColumn(column, context);
    }

    @Override
    public SpreadsheetDelta saveColumn(final SpreadsheetColumn column,
                                       final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.saveColumn(column, context),
                context
        );
    }

    @Override
    public SpreadsheetDelta deleteColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.deleteColumns(column, count, context),
                context
        );
    }

    @Override
    public SpreadsheetDelta loadRow(final SpreadsheetRowReference row,
                                    final SpreadsheetEngineContext context) {
        return this.engine.loadRow(row, context);
    }

    @Override
    public SpreadsheetDelta saveRow(final SpreadsheetRow row,
                                    final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.saveRow(row, context),
                context
        );
    }

    @Override
    public SpreadsheetDelta deleteRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.deleteRows(row, count, context),
                context
        );
    }

    @Override
    public SpreadsheetDelta insertColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.insertColumns(column, count, context),
                context
        );
    }

    @Override
    public SpreadsheetDelta insertRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.insertRows(row, count, context),
                context
        );
    }

    @Override
    public SpreadsheetDelta fillCells(final Collection<SpreadsheetCell> cells,
                                      final SpreadsheetCellRange from,
                                      final SpreadsheetCellRange to,
                                      final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.fillCells(cells, from, to, context),
                context
        );
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName name,
                                                       final SpreadsheetEngineContext context) {
        return this.engine.loadLabel(name, context);
    }

    @Override
    public SpreadsheetDelta saveLabel(final SpreadsheetLabelMapping mapping,
                                      final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.saveLabel(mapping, context),
                context
        );
    }

    @Override
    public SpreadsheetDelta removeLabel(final SpreadsheetLabelName label,
                                        final SpreadsheetEngineContext context) {
        return this.stamp(
                () -> this.engine.removeLabel(label, context),
                context
        );
    }

    @Override
    public double columnWidth(final SpreadsheetColumnReference column,
                              final SpreadsheetEngineContext context) {
        return this.engine.columnWidth(column, context);
    }

    @Override
    public double rowHeight(final SpreadsheetRowReference row,
                            final SpreadsheetEngineContext context) {
        return this.engine.rowHeight(row, context);
    }

    @Override
    public Set<SpreadsheetCellRange> window(final SpreadsheetViewport viewport,
                                            final boolean includeFrozenColumnsRows,
                                            final Optional<SpreadsheetSelection> selection,
                                            final SpreadsheetEngineContext context) {
        return this.engine.window(
                viewport,
                includeFrozenColumnsRows,
                selection,
                context
        );
    }

    @Override
    public Optional<SpreadsheetViewportSelection> navigate(final SpreadsheetViewportSelection selection,
                                                           final SpreadsheetEngineContext context) {
        return this.engine.navigate(selection, context);
    }

    private <T> T stamp(final Supplier<T> supplier,
                        final SpreadsheetEngineContext context) {
        final SpreadsheetStoreRepository repository = context.storeRepository();

        final SpreadsheetMetadataStampingSpreadsheetEngineSaveWatcherDeleteWatcher watcher = SpreadsheetMetadataStampingSpreadsheetEngineSaveWatcherDeleteWatcher.create();

        final SpreadsheetCellStore cellStore = repository.cells();
        final Runnable saveWatcher1 = cellStore.addSaveWatcher(Cast.to(watcher));

        try {
            final Runnable deleteWatcher1 = cellStore.addDeleteWatcher(Cast.to(watcher));
            try {
                final SpreadsheetLabelStore labelStore = repository.labels();
                final Runnable saveWatcher2 = labelStore.addSaveWatcher(Cast.to(watcher));
                try {
                    final Runnable deleteWatcher2 = labelStore.addDeleteWatcher(Cast.to(watcher));

                    try {
                        return supplier.get();
                    } finally {
                        deleteWatcher2.run();
                        if (watcher.saveOrDeletes > 0) {
                            repository.metadatas().save(this.stamper.apply(context.metadata()));
                        }
                    }
                } finally {
                    saveWatcher2.run();
                }
            } finally {
                deleteWatcher1.run();
            }
        } finally {
            saveWatcher1.run();
        }
    }

    /**
     * The engine being wrapped.
     */
    private final SpreadsheetEngine engine;

    /**
     * May be used to set the creator and last modified user and timestamps.
     */
    private final Function<SpreadsheetMetadata, SpreadsheetMetadata> stamper;

    @Override
    public String toString() {
        return this.engine.toString();
    }
}
