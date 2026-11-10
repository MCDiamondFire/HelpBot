package com.diamondfire.helpbot.bot.command.impl.other.dev;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MessageArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.ConnectionProvider;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class QueryCommand extends Command {

    // Space for the table inside the embed description (limit 4096), leaving room for the code fence.
    private static final int MAX_TABLE_CHARS = 3900;
    // Tables wider than this are shown one record per block instead.
    private static final int MAX_TABLE_WIDTH = 100;
    private static final int MAX_CELL_WIDTH = 40;
    // Rows read into the CSV attachment at most.
    private static final int MAX_ROWS = 10_000;
    private static final int TIMEOUT_SECONDS = 30;

    @Override
    public String getName() {
        return "query";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Runs a SQL statement on the bot's database. Results that don't fit are attached as CSV.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("query")
                );
    }

    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("query", new MessageArgument());
    }

    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }

    @Override
    public void run(CommandEvent event) {
        String query = clean(event.getArgument("query"));
        long start = System.nanoTime();

        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(TIMEOUT_SECONDS);
            statement.setMaxRows(MAX_ROWS + 1);

            if (!statement.execute(query)) {
                int affected = statement.getUpdateCount();
                event.reply(new PresetBuilder()
                        .withPreset(
                                new InformativeReply(InformativeReplyType.SUCCESS, "Query Executed",
                                        String.format("%s %s affected - %s", FormatUtil.formatNumber(affected), affected == 1 ? "row" : "rows", elapsed(start)))
                        ));
                return;
            }

            try (ResultSet set = statement.getResultSet()) {
                replyWithResult(event, set, start);
            }
        } catch (SQLException e) {
            String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            event.reply(new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, "Query Failed",
                                    String.format("```%s```\nSQL state `%s`, error code `%s`", truncate(message.replace("`", "'"), 3500), e.getSQLState(), e.getErrorCode()))
                    ));
        }
    }

    private void replyWithResult(CommandEvent event, ResultSet set, long start) throws SQLException {
        ResultSetMetaData meta = set.getMetaData();
        int width = meta.getColumnCount();
        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= width; i++) {
            columns.add(meta.getColumnLabel(i));
        }

        List<List<String>> rows = new ArrayList<>();
        boolean capped = false;
        while (set.next()) {
            if (rows.size() == MAX_ROWS) {
                capped = true;
                break;
            }

            List<String> row = new ArrayList<>(width);
            for (int i = 1; i <= width; i++) {
                Object value = set.getObject(i);
                row.add(value == null ? "NULL" : value instanceof byte[] bytes ? "<" + bytes.length + " bytes>" : value.toString());
            }
            rows.add(row);
        }
        String time = elapsed(start);

        EmbedBuilder embed = new PresetBuilder()
                .withPreset(new InformativeReply(InformativeReplyType.INFO, "Query Result", null))
                .getEmbed();

        if (rows.isEmpty()) {
            embed.setDescription("No rows returned.");
            embed.setFooter("0 rows - " + time);
            event.reply(embed);
            return;
        }

        Rendered rendered = render(columns, rows);
        embed.setDescription("```\n" + rendered.text + "```");

        boolean attach = rendered.shownRows < rows.size() || rendered.truncatedCells;
        String count = FormatUtil.formatNumber(rows.size()) + (capped ? "+" : "") + (rows.size() == 1 ? " row" : " rows");
        String shown = rendered.shownRows < rows.size() ? String.format(" (showing %s)", FormatUtil.formatNumber(rendered.shownRows)) : "";
        embed.setFooter(count + shown + " - " + time + (attach ? " - full result attached" : ""));

        MessageCreateBuilder message = new MessageCreateBuilder().setEmbeds(embed.build());
        if (attach) {
            message.addFiles(FileUpload.fromData(toCsv(columns, rows).getBytes(StandardCharsets.UTF_8), "result.csv"));
        }

        event.reply(message.build());
    }

    private record Rendered(String text, int shownRows, boolean truncatedCells) {

    }

    /**
     * Renders as many rows as fit: an aligned table, or one record per block when the table would be too wide.
     */
    private static Rendered render(List<String> columns, List<List<String>> rows) {
        boolean[] truncated = new boolean[1];
        List<List<String>> cells = rows.stream()
                .map((row) -> row.stream().map((value) -> {
                    String cell = value.replace("\r", "").replace("\n", "\\n").replace("```", "'''");
                    if (cell.length() > MAX_CELL_WIDTH) {
                        truncated[0] = true;
                        return cell.substring(0, MAX_CELL_WIDTH - 1) + "…";
                    }
                    return cell;
                }).toList())
                .toList();

        int[] widths = new int[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            widths[i] = columns.get(i).length();
            for (List<String> row : cells) {
                widths[i] = Math.max(widths[i], row.get(i).length());
            }
        }
        int tableWidth = Arrays.stream(widths).sum() + 3 * (columns.size() - 1);

        StringBuilder text = new StringBuilder();
        int shown = 0;
        if (tableWidth <= MAX_TABLE_WIDTH) {
            text.append(line(columns, widths)).append('\n');
            text.append(divider(widths)).append('\n');
            for (List<String> row : cells) {
                String line = line(row, widths) + '\n';
                if (text.length() + line.length() > MAX_TABLE_CHARS) {
                    break;
                }
                text.append(line);
                shown++;
            }
        } else {
            int labelWidth = columns.stream().mapToInt(String::length).max().orElse(0);
            for (List<String> row : cells) {
                StringBuilder record = new StringBuilder(String.format("-- row %s --\n", shown + 1));
                for (int i = 0; i < columns.size(); i++) {
                    record.append(pad(columns.get(i), labelWidth)).append(" : ").append(row.get(i)).append('\n');
                }
                if (text.length() + record.length() > MAX_TABLE_CHARS) {
                    break;
                }
                text.append(record);
                shown++;
            }
        }

        return new Rendered(text.toString(), shown, truncated[0]);
    }

    private static String line(List<String> values, int[] widths) {
        StringJoiner joiner = new StringJoiner(" | ");
        for (int i = 0; i < values.size(); i++) {
            joiner.add(pad(values.get(i), widths[i]));
        }
        return joiner.toString().stripTrailing();
    }

    private static String divider(int[] widths) {
        StringJoiner joiner = new StringJoiner("-+-");
        for (int width : widths) {
            joiner.add("-".repeat(width));
        }
        return joiner.toString();
    }

    private static String pad(String value, int width) {
        return value + " ".repeat(Math.max(0, width - value.length()));
    }

    private static String toCsv(List<String> columns, List<List<String>> rows) {
        StringBuilder csv = new StringBuilder();
        csv.append(csvLine(columns));
        for (List<String> row : rows) {
            csv.append(csvLine(row));
        }
        return csv.toString();
    }

    private static String csvLine(List<String> values) {
        StringJoiner joiner = new StringJoiner(",", "", "\n");
        for (String value : values) {
            boolean quote = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
            joiner.add(quote ? "\"" + value.replace("\"", "\"\"") + "\"" : value);
        }
        return joiner.toString();
    }

    /**
     * Strips a surrounding code block (```sql ... ```) and a trailing semicolon.
     */
    private static String clean(String query) {
        String cleaned = query.strip();
        if (cleaned.startsWith("```") && cleaned.endsWith("```") && cleaned.length() >= 6) {
            cleaned = cleaned.substring(3, cleaned.length() - 3);
            int newline = cleaned.indexOf('\n');
            if (newline >= 0 && cleaned.substring(0, newline).strip().matches("[A-Za-z]*")) {
                cleaned = cleaned.substring(newline + 1);
            }
        } else if (cleaned.startsWith("`") && cleaned.endsWith("`") && cleaned.length() >= 2) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        cleaned = cleaned.strip();
        while (cleaned.endsWith(";")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1).strip();
        }
        return cleaned;
    }

    private static String elapsed(long start) {
        long millis = (System.nanoTime() - start) / 1_000_000;
        return millis < 1000 ? millis + " ms" : String.format("%.2f s", millis / 1000.0);
    }

    private static String truncate(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max - 1) + "…";
    }

}
