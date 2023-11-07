package com.benefitj.spring.influxdb.write;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.core.file.slicer.FileFactory;

import java.io.File;

public interface LineFileFactory extends FileFactory<LineFileWriter> {

  @Override
  LineFileWriter create(File file);


  static LineFileFactory newFactory() {
    return new LineFileFactoryImpl();
  }

  class LineFileFactoryImpl implements LineFileFactory {

    @Override
    public LineFileWriter create(File dir) {
      String filename = IdUtils.nextId(DateFmtter.fmtNow("yyyyMMdd_HHmmss") +"__", ".line", 15);
      return new LineFileWriter(IOUtils.createFile(dir, filename));
    }

  }

}
