package org.androxyde.os;

import org.apache.commons.exec.LogOutputStream;

public class StdoutStreamParser extends LogOutputStream {

    Long id;

    public StdoutStreamParser(Long id) {
        this.id=id;
    }

    @Override
    protected void processLine(String s, int i) {

    }
}
