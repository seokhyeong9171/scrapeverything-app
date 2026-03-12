package com.scrapeverything.app.ui.category;

import com.scrapeverything.app.data.repository.CategoryRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class CategoryListViewModel_Factory implements Factory<CategoryListViewModel> {
  private final Provider<CategoryRepository> categoryRepositoryProvider;

  public CategoryListViewModel_Factory(Provider<CategoryRepository> categoryRepositoryProvider) {
    this.categoryRepositoryProvider = categoryRepositoryProvider;
  }

  @Override
  public CategoryListViewModel get() {
    return newInstance(categoryRepositoryProvider.get());
  }

  public static CategoryListViewModel_Factory create(
      Provider<CategoryRepository> categoryRepositoryProvider) {
    return new CategoryListViewModel_Factory(categoryRepositoryProvider);
  }

  public static CategoryListViewModel newInstance(CategoryRepository categoryRepository) {
    return new CategoryListViewModel(categoryRepository);
  }
}
