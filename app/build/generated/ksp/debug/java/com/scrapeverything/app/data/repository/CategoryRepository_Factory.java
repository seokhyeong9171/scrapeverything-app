package com.scrapeverything.app.data.repository;

import com.scrapeverything.app.data.api.CategoryApi;
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
public final class CategoryRepository_Factory implements Factory<CategoryRepository> {
  private final Provider<CategoryApi> categoryApiProvider;

  public CategoryRepository_Factory(Provider<CategoryApi> categoryApiProvider) {
    this.categoryApiProvider = categoryApiProvider;
  }

  @Override
  public CategoryRepository get() {
    return newInstance(categoryApiProvider.get());
  }

  public static CategoryRepository_Factory create(Provider<CategoryApi> categoryApiProvider) {
    return new CategoryRepository_Factory(categoryApiProvider);
  }

  public static CategoryRepository newInstance(CategoryApi categoryApi) {
    return new CategoryRepository(categoryApi);
  }
}
