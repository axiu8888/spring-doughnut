package com.benefitj.spring.influxdb.write;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.core.file.slicer.FileFactory;

import java.io.File;
import java.nio.charset.Charset;

public interface LineFileFactory extends FileFactory<LineFileWriter> {

  @Override
  LineFileWriter create(File dir, Charset charset);


  static LineFileFactory newFactory() {
    return new LineFileFactoryImpl();
  }

  class LineFileFactoryImpl implements LineFileFactory {

    @Override
    public LineFileWriter create(File dir, Charset charset) {
      String filename = IdUtils.nextId(DateFmtter.fmtNow("yyyyMMdd__") +"__", ".line", 15);
      return new LineFileWriter(IOUtils.createFile(dir, filename), charset);
    }

  }

}
