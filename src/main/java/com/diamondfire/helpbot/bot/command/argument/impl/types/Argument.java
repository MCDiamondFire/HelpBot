package com.diamondfire.helpbot.bot.command.argument.impl.types;


import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import org.jetbrains.annotations.NotNull;

//Arguments simply parse a given value.
public interface Argument<T> {

    T parseValue(@NotNull String msg) throws ArgumentException;

}
 