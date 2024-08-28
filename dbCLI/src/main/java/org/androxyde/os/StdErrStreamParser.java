package org.androxyde.os;

import org.apache.commons.exec.LogOutputStream;

public class StdErrStreamParser extends LogOutputStream {

    Long id;

    public StdErrStreamParser(Long id) {
        this.id=id;
    }

    @Override
    protected void processLine(String s, int i) {

    }
}
