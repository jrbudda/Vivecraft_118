package org.vivecraft.asm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObfNames
{
    public static boolean DEBUG = false;
    private static final Pattern descPattern = Pattern.compile("L(.+?);");
    private static final Map<String, String> classMappings = new HashMap<>();
    private static final Map<String, String> fieldMappings = new HashMap<>();
    private static final Map<String, String> methodMappings = new HashMap<>();
    private static final Map<String, String> devMappings = new HashMap<>();

    public static String resolveClass(String name, boolean obfuscated)
    {
        if (obfuscated)
        {
            String s = name.replace('.', '/');

            if (classMappings.containsKey(s))
            {
                return classMappings.get(s);
            }

            if (DEBUG)
            {
                System.out.println("No obf mapping found for " + name);
            }
        }

        return name;
    }

    public static String resolveField(String name, boolean obfuscated)
    {
        if (obfuscated)
        {
            if (fieldMappings.containsKey(name))
            {
                return fieldMappings.get(name);
            }

            if (DEBUG)
            {
                System.out.println("No obf mapping found for " + name);
            }
        }

        return name;
    }

    public static String resolveMethod(String name, boolean obfuscated)
    {
        if (obfuscated)
        {
            if (methodMappings.containsKey(name))
            {
                return methodMappings.get(name);
            }

            if (DEBUG)
            {
                System.out.println("No obf mapping found for " + name);
            }
        }

        return name;
    }

    public static String resolveDescriptor(String desc, boolean obfuscated)
    {
        if (!obfuscated)
        {
            return desc;
        }
        else
        {
            Matcher matcher = descPattern.matcher(desc);
            StringBuffer stringbuffer = new StringBuffer();

            while (matcher.find())
            {
                matcher.appendReplacement(stringbuffer, "L" + resolveClass(matcher.group(1), true) + ";");
            }

            matcher.appendTail(stringbuffer);
            return stringbuffer.toString();
        }
    }

    public static String getDevMapping(String name)
    {
        if (devMappings.containsKey(name))
        {
            return devMappings.get(name);
        }
        else
        {
            if (DEBUG)
            {
                System.out.println("No dev mapping found for " + name);
            }

            return name;
        }
    }

    static
    {
        String s = System.getProperty("vivecraft.mcpconfdir");
        File file1 = new File(s == null ? "../conf" : s);
        boolean flag = (new File(file1, "joined.srg")).exists();

        if (!flag)
        {
            try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(ObfNames.class.getResourceAsStream("/mappings/vivecraft/joined.srg"))))
            {
                System.out.println("Loading obf mappings...");
                bufferedreader.lines().forEach((line) ->
                {
                    String[] astring = line.split(": ");
                    String[] astring1 = astring[1].split(" ");
                    String s = astring[0];

                    switch (s)
                    {
                        case "CL":
                            classMappings.put(astring1[1], astring1[0]);
                            break;

                        case "FD":
                            fieldMappings.put(astring1[1].substring(astring1[1].lastIndexOf(47) + 1), astring1[0].substring(astring1[0].lastIndexOf(47) + 1));
                            break;

                        case "MD":
                            methodMappings.put(astring1[2].substring(astring1[2].lastIndexOf(47) + 1), astring1[0].substring(astring1[0].lastIndexOf(47) + 1));
                    }
                });
            }
            catch (IOException ioexception)
            {
                throw new RuntimeException(ioexception);
            }
        }
        else
        {
            System.out.println("MCP conf found! Loading dev mappings...");

            try (BufferedReader bufferedreader1 = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file1, "fields.csv")))))
            {
                bufferedreader1.lines().forEach((line) ->
                {
                    String[] astring = line.split(",");

                    if (!astring[0].equals("searge"))
                    {
                        devMappings.put(astring[0], astring[1]);
                    }
                });
            }
            catch (IOException ioexception2)
            {
                ioexception2.printStackTrace();
            }

            try (BufferedReader bufferedreader2 = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file1, "methods.csv")))))
            {
                bufferedreader2.lines().forEach((line) ->
                {
                    String[] astring = line.split(",");

                    if (!astring[0].equals("searge"))
                    {
                        devMappings.put(astring[0], astring[1]);
                    }
                });
            }
            catch (IOException ioexception1)
            {
                ioexception1.printStackTrace();
            }
        }
    }
}
