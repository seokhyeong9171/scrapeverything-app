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
public final class TokenAuthenticator_Factory implements Factory<TokenAuthenticator> {
  private final Provider<TokenStorage> tokenStorageProvider;

  private final Provider<RefreshApiProvider> refreshApiProvider;

  public TokenAuthenticator_Factory(Provider<TokenStorage> tokenStorageProvider,
      Provider<RefreshApiProvider> refreshApiProvider) {
    this.tokenStorageProvider = tokenStorageProvider;
    this.refreshApiProvider = refreshApiProvider;
  }

  @Override
  public TokenAuthenticator get() {
    return newInstance(tokenStorageProvider.get(), refreshApiProvider.get());
  }

  public static TokenAuthenticator_Factory create(Provider<TokenStorage> tokenStorageProvider,
      Provider<RefreshApiProvider> refreshApiProvider) {
    return new TokenAuthenticator_Factory(tokenStorageProvider, refreshApiProvider);
  }

  public static TokenAuthenticator newInstance(TokenStorage tokenStorage,
      RefreshApiProvider refreshApiProvider) {
    return new TokenAuthenticator(tokenStorage, refreshApiProvider);
  }
}
