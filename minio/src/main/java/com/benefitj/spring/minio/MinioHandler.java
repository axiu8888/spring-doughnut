package com.benefitj.spring.minio;

import java.util.function.Consumer;

public interface MinioHandler<T> extends Consumer<MinioResult<T>> {
}
