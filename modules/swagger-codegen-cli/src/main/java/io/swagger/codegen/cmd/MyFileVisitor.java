package io.swagger.codegen.cmd;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class MyFileVisitor extends SimpleFileVisitor<Path> {
    private Set<String> pathStringSet = new HashSet<>();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        pathStringSet.add(file.toString());
        return FileVisitResult.CONTINUE;
    }

    public Set<String> getPathStringSet() {
        return pathStringSet;
    }
}
