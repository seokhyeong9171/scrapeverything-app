package com.scrapeverything.app.data.repository;

import com.scrapeverything.app.data.api.AuthApi;
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
public final class AuthRepository_Factory implements Factory<AuthRepository> {
  private final Provider<AuthApi> authApiProvider;

  private final Provider<TokenStorage> tokenStorageProvider;

  public AuthRepository_Factory(Provider<AuthApi> authApiProvider,
      Provider<TokenStorage> tokenStorageProvider) {
    this.authApiProvider = authApiProvider;
    this.tokenStorageProvider = tokenStorageProvider;
  }

  @Override
  public AuthRepository get() {
    return newInstance(authApiProvider.get(), tokenStorageProvider.get());
  }

  public static AuthRepository_Factory create(Provider<AuthApi> authApiProvider,
      Provider<TokenStorage> tokenStorageProvider) {
    return new AuthRepository_Factory(authApiProvider, tokenStorageProvider);
  }

  public static AuthRepository newInstance(AuthApi authApi, TokenStorage tokenStorage) {
    return new AuthRepository(authApi, tokenStorage);
  }
}
