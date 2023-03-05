package com.hsrg.minio;

import java.util.function.Consumer;

public interface MinioHandler<T> extends Consumer<MinioResult<T>> {
}
