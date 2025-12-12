package com.tibbo.aggregate.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Locale;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;

public class FileUtils
{
  public static final String BACKUP_FILE_PREFIX = "~";
  
  public static String pathToBackupFileFor(String filePath)
  {
    File file = new File(filePath);
    
    if (!file.isFile())
      return filePath;
    
    String fileName = file.getName();
    String fileDirectory = filePath.substring(0, filePath.length() - fileName.length());
    
    return fileDirectory + BACKUP_FILE_PREFIX + fileName;
  }
  
  public static void writeAtomic(String data, File file) throws IOException
  {
    writeAtomic(data, file, false);
  }
  
  public static void writeAtomic(String data, File file, boolean preserveBackup) throws IOException
  {
    String backupFilePath = pathToBackupFileFor(file.getPath());
    File backupFile = new File(backupFilePath);
    
    byte[] bytes = data.getBytes(StringUtils.UTF8_CHARSET);
    
    Files.write(backupFile.toPath(), bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
    
    if (preserveBackup)
      Files.copy(backupFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    else
      Files.move(backupFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
  }
  
  public static void deleteDirectories(@Nonnull Path directoryPath) throws IOException
  {
    Files.walk(directoryPath)
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);
  }

  public static String readTextFile(@Nonnull Path path) throws IOException
  {
    return readTextFile(path, StandardCharsets.UTF_8);
  }

  public static String readTextFile(@Nonnull Path path, @Nonnull Charset encoding) throws IOException
  {
    try (FileInputStream fis = new FileInputStream(path.toString()))
    {
      return IOUtils.toString(new BOMInputStream(fis), encoding).trim();
    }
  }
  
  @Deprecated
  public static boolean deleteDirectory(File dir)
  {
    if (dir.isDirectory())
    {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++)
      {
        boolean success = deleteDirectory(new File(dir, children[i]));
        if (!success)
        {
          return false;
        }
      }
    }
    
    // The directory is now empty so delete it
    return dir.delete();
  }
  
  @Deprecated
  public static String readTextFile(String filename) throws IOException
  {
    return readTextFile(filename, StandardCharsets.UTF_8);
  }
  
  @Deprecated
  public static String readTextFile(String filename, Charset encoding) throws IOException
  {
    return readTextFile(Paths.get(filename), encoding);
  }
  
  public static void writeTextFile(Path path, String contents, boolean append) throws IOException
  {
    Files.write(path, contents.getBytes());
  }

  public static byte[] readFile(Path path) throws IOException
  {
    return Files.readAllBytes(path);
  }
  
  public static byte[] readFile(File file) throws IOException
  {
    return readFile(file.toPath());
  }
  
  public static void writeFile(Path path, byte[] data) throws IOException
  {
    Files.write(path, data);
  }
  
  public static void writeFile(File file, byte[] data) throws IOException
  {
    writeFile(file.toPath(), data);
  }
  
  public static void copyFile(File source, File destination) throws IOException
  {
    InputStream in = new FileInputStream(source);
    
    OutputStream out = new FileOutputStream(destination);
    
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0)
    {
      out.write(buf, 0, len);
    }
    in.close();
    out.close();
  }
  
  public static String getExtension(File f)
  {
    return getExtension(f.getName());
  }
  
  public static String getExtension(String fileName)
  {
    String ext = null;
    int i = fileName.lastIndexOf('.');
    
    if (i > 0 && i < fileName.length() - 1)
    {
      ext = fileName.substring(i + 1).toLowerCase(Locale.ENGLISH);
    }
    return ext;
  }
  
  public static Long makeChecksumAdler32(File f) throws FileNotFoundException
  {
    // Compute Adler-32 checksum
    CheckedInputStream cis = new CheckedInputStream(new FileInputStream(f), new Adler32());
    return makeChecksumAdler32(cis);
  }
  
  public static Long makeChecksumAdler32(InputStream in)
  {
    // Compute Adler-32 checksum
    CheckedInputStream cis = new CheckedInputStream(in, new Adler32());
    return makeChecksumAdler32(cis);
  }
  
  private static Long makeChecksumAdler32(CheckedInputStream cis)
  {
    try
    {
      byte[] tempBuf = new byte[128]; // NOTE: too small buffer size, it should be at least 8k
      while (cis.read(tempBuf) >= 0)
      {
      }
      return cis.getChecksum().getValue();
    }
    catch (IOException ex)
    {
      return null;
    }
  }
  
}
