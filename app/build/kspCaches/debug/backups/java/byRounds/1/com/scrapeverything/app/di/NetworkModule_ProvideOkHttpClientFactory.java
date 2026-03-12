package com.scrapeverything.app.di;

import com.scrapeverything.app.network.AuthInterceptor;
import com.scrapeverything.app.network.RefreshInterceptor;
import com.scrapeverything.app.network.TokenAuthenticator;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

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
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<AuthInterceptor> authInterceptorProvider;

  private final Provider<RefreshInterceptor> refreshInterceptorProvider;

  private final Provider<TokenAuthenticator> tokenAuthenticatorProvider;

  private final Provider<HttpLoggingInterceptor> loggingInterceptorProvider;

  public NetworkModule_ProvideOkHttpClientFactory(Provider<AuthInterceptor> authInterceptorProvider,
      Provider<RefreshInterceptor> refreshInterceptorProvider,
      Provider<TokenAuthenticator> tokenAuthenticatorProvider,
      Provider<HttpLoggingInterceptor> loggingInterceptorProvider) {
    this.authInterceptorProvider = authInterceptorProvider;
    this.refreshInterceptorProvider = refreshInterceptorProvider;
    this.tokenAuthenticatorProvider = tokenAuthenticatorProvider;
    this.loggingInterceptorProvider = loggingInterceptorProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(authInterceptorProvider.get(), refreshInterceptorProvider.get(), tokenAuthenticatorProvider.get(), loggingInterceptorProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<AuthInterceptor> authInterceptorProvider,
      Provider<RefreshInterceptor> refreshInterceptorProvider,
      Provider<TokenAuthenticator> tokenAuthenticatorProvider,
      Provider<HttpLoggingInterceptor> loggingInterceptorProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(authInterceptorProvider, refreshInterceptorProvider, tokenAuthenticatorProvider, loggingInterceptorProvider);
  }

  public static OkHttpClient provideOkHttpClient(AuthInterceptor authInterceptor,
      RefreshInterceptor refreshInterceptor, TokenAuthenticator tokenAuthenticator,
      HttpLoggingInterceptor loggingInterceptor) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(authInterceptor, refreshInterceptor, tokenAuthenticator, loggingInterceptor));
  }
}
