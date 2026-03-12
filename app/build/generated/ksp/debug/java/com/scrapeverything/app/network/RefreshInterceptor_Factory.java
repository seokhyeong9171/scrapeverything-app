package com.scrapeverything.app.network;

import com.scrapeverything.app.data.local.TokenStorage;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class RefreshInterceptor_Factory implements Factory<RefreshInterceptor> {
  private final Provider<TokenStorage> tokenStorageProvider;

  public RefreshInterceptor_Factory(Provider<TokenStorage> tokenStorageProvider) {
    this.tokenStorageProvider = tokenStorageProvider;
  }

  @Override
  public RefreshInterceptor get() {
    return newInstance(tokenStorageProvider.get());
  }

  public static RefreshInterceptor_Factory create(Provider<TokenStorage> tokenStorageProvider) {
    return new RefreshInterceptor_Factory(tokenStorageProvider);
  }

  public static RefreshInterceptor newInstance(TokenStorage tokenStorage) {
    return new RefreshInterceptor(tokenStorage);
  }
}
