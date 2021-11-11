package competitive.programming.utils;

import net.steppschuh.markdowngenerator.table.Table;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.gfm.tables.TableHead;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.node.Code;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MarkdownUtility {
    public static String parseMarkdownTable(Path targetMarkdownFile) {
        StringBuilder resultStringBuilder = new StringBuilder();
        boolean appendFlag = false;
        try (BufferedReader br = new BufferedReader(new FileReader(targetMarkdownFile.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equalsIgnoreCase("<!--TABLE_ENDS_HERE-->")) {
                    appendFlag = false;
                }
                if (appendFlag) {
                    resultStringBuilder.append(line).append("\n");
                }
                if (line.equalsIgnoreCase("<!--TABLE_STARTS_HERE-->")) {
                    appendFlag = true;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return resultStringBuilder.toString();
    }
    public static void parseTableBody(Node tableBody, Table.Builder tableBuilder) {
        TableRow row = (TableRow) tableBody.getFirstChild();
        // loop on all table rows
        while (row != null) {
            TableCell tableCell = (TableCell) row.getFirstChild();
            List<Object> cellContent = new ArrayList<>();
            while (tableCell != null) {

                // get all child of Table Cell
                Node cell = tableCell.getFirstChild();
                StringBuilder content = new StringBuilder();
                while (cell != null) {
                    if (cell instanceof Text) {
                        content.append(((Text) cell).getLiteral());
                    }
                    if (cell instanceof Link) {
                        Text title = (Text) cell.getFirstChild();
                        content.append("[")
                                .append(title.getLiteral()).append("]")
                                .append("(")
                                .append(((Link) cell).getDestination())
                                .append(")");
                    }
                    if (cell instanceof Code) {
                        content.append("`")
                                .append(((Code) cell).getLiteral())
                                .append("`");
                    }
                    cell = cell.getNext();
                }
                cellContent.add(content.toString());
                // get contents of Table Row
                tableCell = (TableCell) tableCell.getNext();
            }
            tableBuilder.addRow(cellContent.toArray());
            row = (TableRow) row.getNext();
        }
    }

    public static void paresTableHead(TableHead tableHead, Table.Builder tableBuilder) {
        TableRow headingRow = (TableRow) tableHead.getFirstChild();
        TableCell tableCell = (TableCell) headingRow.getFirstChild();
        List<Object> headings = new ArrayList<>();
        while (tableCell != null) {
            Text text = (Text) tableCell.getFirstChild();
            headings.add(text.getLiteral());
            tableCell = (TableCell) tableCell.getNext();
        }
        tableBuilder.addRow(headings.toArray());
    }

    public static String defaultTable() {
        return new Table.Builder()
                .addRow("Problem", "File", "Platform", "Date", "Tags", "Status","Comments")
                .build()
                .toString();
    }
}
