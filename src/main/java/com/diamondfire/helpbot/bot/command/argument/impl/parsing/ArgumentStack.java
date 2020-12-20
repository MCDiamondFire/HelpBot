package com.diamondfire.helpbot.bot.command.argument.impl.parsing;

import java.util.*;

// ArgumentStacks contain a bunch of argument nodes and raw arguments.
public class ArgumentStack {
    
    private final RawArgumentStack stack;
    private final Deque<ArgumentNode<?>> args;
    
    public ArgumentStack(List<ArgumentNode<?>> args, Collection<String> rawArgs) {
        this.stack = new RawArgumentStack(new ArrayDeque<>(rawArgs));
        this.args = new ArrayDeque<>(args);
    }
    
    public RawArgumentStack getRawArguments() {
        return stack;
    }
    
    public Deque<ArgumentNode<?>> getArguments() {
        return args;
    }
    
    public static class RawArgumentStack {
        
        private Deque<String> stack;
        private Deque<String> currentStack;
        
        public RawArgumentStack(Deque<String> stack) {
            this.stack = stack;
        }
        
        public Deque<String> popStack() {
            return currentStack = new ArrayDeque<>(stack);
        }
        
        public Deque<String> pushStack() {
            return stack = currentStack;
        }
    }
}


