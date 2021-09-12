package com.diamondfire.helpbot.sys.externalfile;

public enum ExternalFileType {
    
    UNKNOWN("unk"),
    
    TEXT("txt"),
    JSON("json", "{}"),
    
    PNG("png")
    ;
    
    private final String extension;
    private final byte[] defaultContent;
    
    ExternalFileType(String extension, byte[] defaultContent) {
        this.extension = extension;
        this.defaultContent = defaultContent;
    }
    
    ExternalFileType(String extension, String defaultContent) {
        this(extension, defaultContent.getBytes());
    }
    
    ExternalFileType(String extension) {
        this(extension, (byte[]) null);
    }
    
    public String getExtension() {
        return extension;
    }
    
    public byte[] getDefaultContent() {
        return defaultContent;
    }
    
}
