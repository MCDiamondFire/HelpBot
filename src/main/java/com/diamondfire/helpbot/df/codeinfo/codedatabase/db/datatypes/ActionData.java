package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.df.codeinfo.viewables.constants.DataTypes;
import com.diamondfire.helpbot.util.Util;
import com.google.gson.JsonObject;

import java.util.List;

public class ActionData extends CodeObject {

    public ActionData(JsonObject data) {
        super(data, data.get("name").getAsString().trim());
    }

    public String getName() {
        return data.get("name").getAsString().trim();
    }

    public String getCodeblockName() {
        return data.get("codeblockName").getAsString();
    }

    public CodeBlockTagData[] getTags() {
        return CodeBlockTagData.parseTags(data.get("tags").getAsJsonArray());
    }

    public String[] getAliases() {
        return Util.jsonArrayToString(data.get("aliases").getAsJsonArray());
    }

    // Convenience method
    public CodeBlockData getCodeBlockData() {
        List<CodeBlockData> codeBlocks = CodeDatabase.getRegistry(CodeDatabase.CODEBLOCKS);
        for (CodeBlockData data : codeBlocks) {
            if (data.getName().equals(getCodeblockName())) {
                return data;
            }
        }

        return null;
    }

    @Override
    public DataTypes getEnum() {
        return DataTypes.ACTION;
    }

}
