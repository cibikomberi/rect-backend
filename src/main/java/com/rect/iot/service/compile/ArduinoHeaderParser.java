package com.rect.iot.service.compile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.stereotype.Service;

@Service
public class ArduinoHeaderParser {
    public void extractZipFile(String zipFilePath, String outputDir) throws IOException {
        File destDir = new File(outputDir);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                File newFile = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipIn.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    public Map<String, List<Map<String, Object>>> parseHeaderFile(String filePath) throws IOException {
        List<Map<String, Object>> methods = new ArrayList<>();
        List<String> constants = new ArrayList<>();

        String content = Files.readString(Paths.get(filePath));

        // Regex for method declarations (return type, method name, arguments)
        Pattern methodPattern = Pattern.compile("(\\w[\\w\\s\\*]+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*;"); // Matches: returnType, methodName, args
        Matcher methodMatcher = methodPattern.matcher(content);
        while (methodMatcher.find()) {
            Map<String, Object> methodDetails = new HashMap<>();
            methodDetails.put("returnType", methodMatcher.group(1).trim());
            methodDetails.put("methodName", methodMatcher.group(2).trim());
            methodDetails.put("arguments", parseArguments(methodMatcher.group(3).trim())); // Arguments are parsed separately
            methods.add(methodDetails);
        }

        // Regex for constants (#define and const)
        Pattern definePattern = Pattern.compile("#define\\s+(\\w+)\\s+([^\\n]+)");
        Matcher defineMatcher = definePattern.matcher(content);
        while (defineMatcher.find()) {
            constants.add(defineMatcher.group());
        }

        Pattern constPattern = Pattern.compile("const\\s+(\\w[\\w\\s]*)\\s+(\\w+)\\s*=\\s*([^;]+);");
        Matcher constMatcher = constPattern.matcher(content);
        while (constMatcher.find()) {
            constants.add(constMatcher.group());
        }

        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("methods", methods);
        // result.put("constants", constants);

        return result;
    }

    // Helper method to parse method arguments
    private List<Map<String, String>> parseArguments(String args) {
        List<Map<String, String>> arguments = new ArrayList<>();
        if (args.isEmpty()) {
            return arguments;
        }

        // Split arguments and extract type and name
        String[] argArray = args.split(",");
        for (String arg : argArray) {
            String[] parts = arg.trim().split("\\s+");
            if (parts.length >= 2) {
                Map<String, String> argumentDetails = new HashMap<>();
                argumentDetails.put("type", String.join(" ", Arrays.copyOfRange(parts, 0, parts.length - 1))); // Everything except last part
                argumentDetails.put("name", parts[parts.length - 1]); // Last part
                arguments.add(argumentDetails);
            } else if (parts.length == 1) {
                Map<String, String> argumentDetails = new HashMap<>();
                argumentDetails.put("type", parts[0]); // Just the type
                argumentDetails.put("name", ""); // No name
                arguments.add(argumentDetails);
            }
        }
        return arguments;
    }
}
