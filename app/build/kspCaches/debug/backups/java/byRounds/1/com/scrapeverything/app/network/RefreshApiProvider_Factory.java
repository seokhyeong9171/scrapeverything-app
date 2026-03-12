package com.scrapeverything.app.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class RefreshApiProvider_Factory implements Factory<RefreshApiProvider> {
  @Override
  public RefreshApiProvider get() {
    return newInstance();
  }

  public static RefreshApiProvider_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RefreshApiProvider newInstance() {
    return new RefreshApiProvider();
  }

  private static final class InstanceHolder {
    private static final RefreshApiProvider_Factory INSTANCE = new RefreshApiProvider_Factory();
  }
}
