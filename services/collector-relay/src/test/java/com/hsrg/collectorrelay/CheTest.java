package com.hsrg.collectorrelay;

import com.benefitj.core.DateFmtter;
import com.hsrg.collectorrelay.parse.CheFileHeader;
import com.hsrg.collectorrelay.parse.CheHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.stream.Stream;

class CheTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }


  @Test
  void test_hex() {
    File dir = new File("D:\\home\\znsx\\collector-relay\\CHE");
    Stream.of(dir.listFiles())
        .filter(f -> f.getName().endsWith(".HEX"))
        .forEach(che -> {
          String filename = che.getName().replace(".HEX", ".CHE");
          che.renameTo(new File(che.getParentFile(), filename));
        });

  }

  @Test
  void test_cheChangeName() {
    File dir = new File("D:\\home\\znsx\\collector-relay\\CHE");
    Stream.of(dir.listFiles())
        .filter(f -> f.getName().endsWith(".CHE"))
        .forEach(che -> {
          CheFileHeader header = CheHelper.parseHeader(che);
          String filename = header.getDeviceId() + "-" + DateFmtter.fmt(CheHelper.getFirstTime(che) - 2000L, "yyyy_MM_dd-HH_mm_ss") + "-000.CHE";
          che.renameTo(new File(che.getParentFile(), filename));
        });
  }

}