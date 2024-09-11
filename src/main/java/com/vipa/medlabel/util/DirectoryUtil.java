package com.vipa.medlabel.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;

import java.util.Set;

public class DirectoryUtil {
    public static void deleteDirectory(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void createDirectory(Path dir, String perms) throws IOException {
        if (!Files.exists(dir)) {
            // 解析这个字符串，设置权限
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(perms);
            Files.createDirectories(dir);
            Files.setPosixFilePermissions(dir, permissions);
        }
    }
}