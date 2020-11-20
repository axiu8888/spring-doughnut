package com.benefitj.spring.influxdb.file;

import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * 写入文件
 */
public class FileWriterPair extends Writer {

  private final File file;
  private final BufferedWriter writer;

  public FileWriterPair(File file) {
    this.file = file;
    try {
      this.writer = new BufferedWriter(new FileWriter(file));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public File getFile() {
    return file;
  }

  public BufferedWriter getWriter() {
    return writer;
  }

  @Override
  public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
    getWriter().write(cbuf, off, len);
  }

  @Override
  public void close() {
    BufferedWriter w = this.getWriter();
    try {
      w.flush();
      w.close();
    } catch (IOException ignore) {/*...*/}
  }

  @Override
  public void flush() throws IOException {
    this.getWriter().flush();
  }


  public long length() {
    return file.length();
  }

  public String getName() {
    return file.getName();
  }

}
