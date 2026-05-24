/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
package esa.mo.nmf.ctt.utils;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Utility methods for JTable presentation.
 */
public final class TableUtils {

    private TableUtils() {
    }

    /**
     * Sizes each column to fit its widest content (header or any cell value),
     * with a small padding. Call this after populating the model; register it
     * on a TableModelListener to keep columns sized as rows are added/removed.
     *
     * @param table The table whose columns should be packed.
     */
    public static void packColumns(JTable table) {
        int cols = table.getColumnCount();
        if (cols == 0) {
            return;
        }

        int[] widths = new int[cols];
        int totalPacked = 0;

        for (int col = 0; col < cols; col++) {
            int width = 0;
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, table.getColumnModel().getColumn(col).getHeaderValue(),
                    false, false, 0, col);
            width = Math.max(width, headerComp.getPreferredSize().width);
            for (int row = 0; row < table.getRowCount(); row++) {
                Component cellComp = table.prepareRenderer(table.getCellRenderer(row, col), row, col);
                width = Math.max(width, cellComp.getPreferredSize().width);
            }
            widths[col] = width + 8;
            totalPacked += widths[col];
        }

        // If packed widths don't fill the viewport, scale proportionally so there is no dead space.
        // If content is wider than the viewport, use the packed widths and let the scrollbar appear.
        int viewportWidth = table.getParent() != null ? table.getParent().getWidth() : 0;
        if (viewportWidth > totalPacked && totalPacked > 0) {
            double scale = (double) viewportWidth / totalPacked;
            for (int col = 0; col < cols; col++) {
                table.getColumnModel().getColumn(col).setPreferredWidth((int) (widths[col] * scale));
            }
        } else {
            for (int col = 0; col < cols; col++) {
                table.getColumnModel().getColumn(col).setPreferredWidth(widths[col]);
            }
        }
    }
}
