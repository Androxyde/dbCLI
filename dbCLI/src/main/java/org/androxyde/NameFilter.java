package org.androxyde;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;

public class NameFilter implements DirectoryStream.Filter<Path> {

    String pattern;

    public NameFilter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean accept(Path entry) throws IOException {
        return entry.toString().matches(pattern);
    }
}
