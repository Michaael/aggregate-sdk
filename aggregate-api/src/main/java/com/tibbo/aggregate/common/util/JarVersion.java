package com.tibbo.aggregate.common.util;

import static java.util.Objects.requireNonNull;
import static java.util.jar.Attributes.Name.IMPLEMENTATION_VERSION;
import static java.util.jar.Attributes.Name.MAIN_CLASS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import com.tibbo.aggregate.common.Log;

public class JarVersion
{
  
  public static final String BUILD_NUMBER = "Build-Number";
  public static final JarVersion EMPTY = new JarVersion("", "", null, null);
  
  public final String jarFileName;
  public final String mainClass;
  public final String implementationVersion;
  public final String buildNumber;
  
  protected JarVersion(String jarFileName, String mainClass, String implementationVersion,
      String buildNumber)
  {
    this.jarFileName = requireNonNull(jarFileName);
    this.mainClass = mainClass;
    this.implementationVersion = implementationVersion;
    this.buildNumber = buildNumber;
  }
  
  protected JarVersion(String jarFileName)
  {
    this(jarFileName, "", null, null);
  }
  
  public static JarVersion create(String jarFileName)
  {
    JarVersion jarWithoutVersion = new JarVersion(jarFileName);
    if (!Files.isRegularFile(Paths.get(jarFileName)))
    {
      return jarWithoutVersion;
    }
    
    try (JarFile jarFile = new JarFile(jarFileName))
    {
      Manifest manifest = jarFile.getManifest();
      if (null == manifest)
      {
        return jarWithoutVersion;
      }
      Attributes mainAttributes = manifest.getMainAttributes();
      String mainClass = (String) mainAttributes.getOrDefault(MAIN_CLASS, "");
      String implementationVersion = mainAttributes.getValue(IMPLEMENTATION_VERSION);
      String buildNumber = mainAttributes.getValue(BUILD_NUMBER);
      return new JarVersion(jarFileName, mainClass, implementationVersion, buildNumber);
    }
    catch (IOException e)
    {
      Log.STDERR.error("Error reading jar file", e);
    }
    return jarWithoutVersion;
  }
  
  public static List<JarVersion> getClassPathJarVersions(List<String> classPath)
  {
    return classPath.stream()
        .filter(Objects::nonNull)
        .map(JarVersion::create)
        .collect(Collectors.toList());
  }
  
  public static Optional<JarVersion> getClassPathJarVersionsByTarget(List<String> classPath, String targetClassName) {
    return classPath.stream()
            .filter(Objects::nonNull)
            .map(JarVersion::create)
            .filter(input -> input.mainClass.equals(targetClassName))
            .findAny();
  }
  
  public String format()
  {
    Optional<String> optionalVersion = getMostSpecificVersion();
    
    if (!optionalVersion.isPresent())
    {
      return jarFileName + " -Unknown";
    }
    String version = optionalVersion.get();
    if (jarFileName.toLowerCase().endsWith("-" + version + ".jar"))
    {
      return jarFileName;
    }
    return jarFileName + " " + version;
  }
  
  private Optional<String> getMostSpecificVersion()
  {
    if (null != implementationVersion)
    {
      return Optional.of(implementationVersion);
    }
    if (null != buildNumber)
    {
      return Optional.of(buildNumber);
    }
    return Optional.empty();
  }
}
