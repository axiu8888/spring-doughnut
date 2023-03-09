package com.hsrg.minio;

import com.benefitj.core.*;
import com.benefitj.core.functions.Pair;
import com.benefitj.core.functions.StreamBuilder;
import com.benefitj.core.local.LocalCache;
import com.benefitj.core.local.LocalCacheFactory;
import com.benefitj.frameworks.cglib.CGLibMethodInvoker;
import com.benefitj.frameworks.cglib.CGLibProxy;
import io.minio.*;
import io.minio.messages.Tags;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * MinIO工具
 */
public class MinioUtils {

  private static final LocalCache<MinioResult> localResult = LocalCacheFactory.newCache(() -> MinioResult.fail("FAIL"));

  /**
   * 获取请求结果
   */
  public static <T> MinioResult<T> getResult() {
    return localResult.get();
  }

  /**
   * 获取并移除一个请求结果
   */
  @Nonnull
  public static <T> MinioResult<T> removeResult() {
    return localResult.getAndRemove();
  }

  /**
   * 获取一个成功的结果
   */
  public static <T> MinioResult<T> obtainSucceedResult() {
    return MinioResult.succeed(null);
  }

  /**
   * 获取一个失败的结果
   */
  public static <T> MinioResult<T> obtainFailResult() {
    return MinioResult.fail("FAIL");
  }

  /**
   * 创建客户端代理
   *
   * @param client 客户端
   * @return 返回代理对象
   */
  public static IMinioClient newProxy(MinioClient client) {
    return newProxy(null, new Class[]{IMinioClient.class}, new Object[]{client});
  }

  /**
   * 创建客户端代理
   *
   * @param superclass 父类
   * @param interfaces 结构
   * @param objects    实现的对象
   * @param <T>        接口类型
   * @return 返回代理对象
   */
  public static <T> T newProxy(@Nullable Class<?> superclass, @Nonnull Class<?>[] interfaces, @Nonnull Object[] objects) {
    Map<Method, CGLibMethodInvoker> invokers = new ConcurrentHashMap<>(20);
    return CGLibProxy.newProxy(superclass, interfaces, (obj, method, args, proxy) -> {
      MinioResult result = getResult();
      try {
        CGLibMethodInvoker invoker = invokers.get(method);
        if (invoker == null) {
          invoker = invokers.computeIfAbsent(method, m -> new CGLibMethodInvoker(obj, m, proxy, interfaces, objects));
        }
        Object returnValue = invoker.invoke(args);
        result.setData(returnValue);
        result.setCode(200);
        result.setMessage("SUCCESS");
        return returnValue;
      } catch (Exception e) {
        //throw new MinioException(e.getMessage());
        result.setCode(400);
        result.setMessage(e.getMessage());
        result.setError(e);
        return null;
      }
    });
  }

  /**
   * 构建桶的参数
   *
   * @param bucketName 桶
   * @return 返回参数
   */
  public static <B extends BucketArgs, BA extends BucketArgs.Builder<BA, B>> B newBucketArgs(BA builder,
                                                                                             @Nonnull String bucketName) {
    return newBucketArgs(builder, bucketName, null, null, null);
  }

  /**
   * 检查桶是否存在的参数
   *
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 返回参数
   */
  public static <B extends BucketArgs, BA extends BucketArgs.Builder<BA, B>> B newBucketArgs(BA builder,
                                                                                             @Nonnull String bucketName,
                                                                                             @Nullable String region,
                                                                                             @Nullable Map<String, String> extraHeaders,
                                                                                             @Nullable Map<String, String> extraQueryParams) {
    BA ba = new StreamBuilder<>(builder)
        .set(b -> b.bucket(bucketName))
        .set(b -> b.region(CodecUtils.encodeURL(region)), StringUtils.isNotBlank(region))
        .set(b -> b.extraHeaders(extraHeaders), extraHeaders != null && !extraHeaders.isEmpty())
        .set(b -> b.extraQueryParams(extraQueryParams), extraQueryParams != null && !extraQueryParams.isEmpty())
        .get();
    B args = ba.build();
    if (args instanceof ObjectWriteArgs) {
      Tags tags = ((ObjectWriteArgs) args).tags();
      tags.get().forEach((key, value) -> {
        if (!matchKey(key)) {
          throw new IllegalStateException("非法的TagKey：" + key);
        }
        if (!matchValue(value)) {
          throw new IllegalStateException("非法的TagValue：" + value);
        }
      });
    }
    return args;
  }

  /**
   * 构建桶的参数
   *
   * @param objectName 对象
   * @param bucketName 桶
   * @return 返回参数
   */
  public static <B extends ObjectArgs, BA extends ObjectArgs.Builder<BA, B>> B newObjectArgs(BA builder,
                                                                                             @Nonnull String objectName,
                                                                                             @Nonnull String bucketName) {
    return newObjectArgs(builder, objectName, bucketName, null, null, null);
  }

  /**
   * 构建对象的参数
   *
   * @param objectName       对象
   * @param bucketName       桶
   * @param region           域(文件目录)
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 返回参数
   */
  public static <B extends ObjectArgs, BA extends ObjectArgs.Builder<BA, B>> B newObjectArgs(BA builder,
                                                                                             @Nonnull String objectName,
                                                                                             @Nonnull String bucketName,
                                                                                             @Nullable String region,
                                                                                             @Nullable Map<String, String> extraHeaders,
                                                                                             @Nullable Map<String, String> extraQueryParams) {
    return newBucketArgs(builder.object(objectName), bucketName, region, extraHeaders, extraQueryParams);
  }

  /**
   * 获取一个文件的md5值
   */
  public static String md5(File src) {
    try (final FileInputStream fis = new FileInputStream(src)) {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      IOUtils.read(fis, 1024 << 4, true, (buf, len) -> md5.update(buf, 0, len));
      return HexUtils.bytesToHex(md5.digest(), true);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 获取对象名
   *
   * @param path     路径
   * @param filename 名称
   * @return 返回对象名
   */
  public static String getObjectName(String path, String filename) {
    path = path == null ? "" : path;
    String newPath = path.replace("\\", "/").replace("//", "/");
    String newFilename = filename.replace("\\", "/");
    return (newPath.endsWith("/") ? newPath : newPath + "/") + (newFilename.startsWith("/") ? newFilename.substring(1) : newFilename);
  }

  /**
   * 获取文件的元信息
   *
   * @param src 文件
   * @return 返回信息
   */
  public static Map<String, String> getFileMetadata(File src) {
    return Utils.mapOf(
        Pair.of("name", CodecUtils.encodeURL(src.getName())),
        Pair.of("path", CodecUtils.encodeURL(src.getParent())),
        Pair.of("size", String.valueOf(src.length())),
        Pair.of("showSize", Utils.decimal(Utils.ofMB(src.length()), 2) + "MB"),
        Pair.of("lastModified", String.valueOf(src.lastModified()))
    );
  }

  /**
   * 文件转换成 SnowballObject
   *
   * @param path 路径
   * @param file 文件
   * @return 返回转换后的 SnowballObject
   */
  public static SnowballObject snowballObject(String path, File file) {
    return new SnowballObject(getObjectName(path, file.getName())
        , CatchUtils.tryThrow(() -> Files.newInputStream(file.toPath()))
        , file.length()
        , ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault())
    );
  }

  /**
   * 文件转换成 SnowballObject
   *
   * @param path 路径
   * @param src  文件
   * @return 返回转换后的 SnowballObject
   */
  public static PutObjectArgs.Builder putObjectArgs(String path, File src) {
    return PutObjectArgs.builder()
        .object(MinioUtils.getObjectName(path, src.getName()))
        .contentType(ContentType.get(src.getName()))
        .stream(CatchUtils.tryThrow(() -> Files.newInputStream(src.toPath())), src.length(), PutObjectArgs.MIN_MULTIPART_SIZE)
        .userMetadata(MinioUtils.getFileMetadata(src));
  }

  /**
   * 把文件转换成可上传的对象
   *
   * @param src        源文件
   * @param basePath   路径
   * @param mappedFunc 转换器
   * @return 返回转换后的集合
   */
  public static <T> List<T> listObjects(File src,
                                        String basePath,
                                        BiFunction<String, File, T> mappedFunc) {
    return listObjects(src, basePath, true, mappedFunc);
  }

  /**
   * 把文件转换成可上传的对象
   *
   * @param src        源文件
   * @param basePath   路径
   * @param recursive  是否递归转换
   * @param mappedFunc 转换器
   * @return 返回转换后的集合
   */
  public static <T> List<T> listObjects(File src,
                                        String basePath,
                                        boolean recursive,
                                        BiFunction<String, File, T> mappedFunc) {
    return listObjects(new ArrayList<>()
        , src
        , basePath
        , recursive
        , mappedFunc
        , File::isFile);
  }

  /**
   * 文件转换
   *
   * @param list       存储的List
   * @param src        源文件
   * @param basePath   路径
   * @param recursive  是否递归转换
   * @param mappedFunc 转换器
   * @param filter     过滤器
   * @return 返回转换后的集合
   */
  public static <T> List<T> listObjects(List<T> list,
                                        File src,
                                        String basePath,
                                        boolean recursive,
                                        BiFunction<String, File, T> mappedFunc,
                                        Predicate<File> filter) {
    if (filter.test(src)) {
      list.add(mappedFunc.apply(basePath, src));
    }
    if (recursive && src.isDirectory()) {
      File[] files = src.listFiles();
      if (files != null && files.length > 0) {
        //src.getParentFile().getName()
        for (File file : files) {
          String subPath = trimObjectName(basePath + "/" + src.getName());
          listObjects(list, file, subPath, recursive, mappedFunc, filter);
        }
      }
    }
    return list;
  }

  /**
   * 合并Map
   *
   * @param target 目标对象
   * @param prefix 前缀
   * @param maps   需要合并的Map
   * @return 返回合并后的Map
   */
  public static Map<String, String> concat(Map<String, String> target, String prefix, Map<String, ?>... maps) {
    for (Map<String, ?> map : maps) {
      if (map != null && !map.isEmpty()) {
        map.forEach((key, value) -> target.putIfAbsent(key.startsWith(prefix) ? key : prefix + key, value != null ? value.toString() : null));
      }
    }
    return target;
  }

  /**
   * 检查键
   */
  public static boolean matchKey(String key) {
    // 1~128个长度：^([\p{L}\p{Z}\p{N}_.:/=+\-@]*)$
    // 仅允许unicode等字符串：^([\p{L}\p{Z}\p{N}_.:/=+\-]*)$
    return key.matches("^([\\p{L}\\p{Z}\\p{N}_.:/=+\\-]*)$");
  }

  /**
   * 检查值
   */
  public static boolean matchValue(String value) {
    return value.matches("^([\\p{L}\\p{Z}\\p{N}_.:/=+\\-@]*)$");
  }

  public static String trimObjectName(String objectName) {
    return objectName
        .replace("\\", "/")
        .replace("//", "/");
  }

  public static String getNotNullStr(String str) {
    return getNotNullStr(str, "");
  }

  public static String getNotNullStr(String str, String defaultValue) {
    return str != null ? str : defaultValue;
  }

  /**
   * 转换成Map
   */
  public static <V, K> Map<K, V> mapOf(Pair<K, V>... pairs) {
    return Utils.mapOf(pairs);
  }

  /**
   * URL 编码
   */
  public static String encodeURL(String str) {
    return CodecUtils.encodeURL(str);
  }

  /**
   * URL 解码
   */
  public static String decodeURL(String str) {
    return CodecUtils.decodeURL(str);
  }

  /**
   * 获取前缀
   */
  public static String getObjectPrefix(String objectName) {
    String name = trimObjectName(objectName);
    int index = name.lastIndexOf("/");
    return index > 0 ? name.substring(0, index) : "";
  }

  /**
   * 获取后缀
   */
  public static String getSuffix(String name) {
    int index = name.lastIndexOf(".");
    return index > 0 ? name.substring(0, index) : "";
  }

}
