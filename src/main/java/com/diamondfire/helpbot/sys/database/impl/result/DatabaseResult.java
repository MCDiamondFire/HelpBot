package com.diamondfire.helpbot.sys.database.impl.result;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Iterator;

public class DatabaseResult implements Iterable<ResultSet> {

    private final ResultSet set;

    public DatabaseResult(ResultSet set) {
        this.set = set;
    }

    public boolean isEmpty() {
        if (set == null) {
            return true;
        }

        try {
            if (!set.next()) {
                return true;
            } else {
                set.beforeFirst();
            }
        } catch (SQLException ignored) {
        }

        return false;
    }

    public ResultSet getResult() {
        try {
            set.next();
        } catch (SQLException ignored) {
        }

        return set;
    }

    @NotNull
    @Override
    public Iterator<ResultSet> iterator() {
        ResultSet set = getResult();
        try {
            set.beforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                try {
                    return set.next();
                } catch (SQLException ignored) {
                    return false;
                }
            }

            @Override
            public ResultSet next() {
                return set;
            }
        };
    }

}
