package com.scrapeverything.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.scrapeverything.app.data.api.AuthApi;
import com.scrapeverything.app.data.api.CategoryApi;
import com.scrapeverything.app.data.api.MemberApi;
import com.scrapeverything.app.data.api.ScrapApi;
import com.scrapeverything.app.data.local.TokenStorage;
import com.scrapeverything.app.data.repository.AuthRepository;
import com.scrapeverything.app.data.repository.CategoryRepository;
import com.scrapeverything.app.data.repository.MemberRepository;
import com.scrapeverything.app.data.repository.ScrapRepository;
import com.scrapeverything.app.di.NetworkModule_ProvideAuthApiFactory;
import com.scrapeverything.app.di.NetworkModule_ProvideCategoryApiFactory;
import com.scrapeverything.app.di.NetworkModule_ProvideLoggingInterceptorFactory;
import com.scrapeverything.app.di.NetworkModule_ProvideMemberApiFactory;
import com.scrapeverything.app.di.NetworkModule_ProvideOkHttpClientFactory;
import com.scrapeverything.app.di.NetworkModule_ProvideRetrofitFactory;
import com.scrapeverything.app.di.NetworkModule_ProvideScrapApiFactory;
import com.scrapeverything.app.network.AuthInterceptor;
import com.scrapeverything.app.network.RefreshApiProvider;
import com.scrapeverything.app.network.RefreshInterceptor;
import com.scrapeverything.app.network.TokenAuthenticator;
import com.scrapeverything.app.ui.auth.LoginViewModel;
import com.scrapeverything.app.ui.auth.LoginViewModel_HiltModules;
import com.scrapeverything.app.ui.auth.LoginViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.scrapeverything.app.ui.auth.LoginViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.scrapeverything.app.ui.category.CategoryListViewModel;
import com.scrapeverything.app.ui.category.CategoryListViewModel_HiltModules;
import com.scrapeverything.app.ui.category.CategoryListViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.scrapeverything.app.ui.category.CategoryListViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.scrapeverything.app.ui.member.MyPageViewModel;
import com.scrapeverything.app.ui.member.MyPageViewModel_HiltModules;
import com.scrapeverything.app.ui.member.MyPageViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.scrapeverything.app.ui.member.MyPageViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.scrapeverything.app.ui.scrap.ScrapDetailViewModel;
import com.scrapeverything.app.ui.scrap.ScrapDetailViewModel_HiltModules;
import com.scrapeverything.app.ui.scrap.ScrapDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.scrapeverything.app.ui.scrap.ScrapDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.scrapeverything.app.ui.scrap.ScrapListViewModel;
import com.scrapeverything.app.ui.scrap.ScrapListViewModel_HiltModules;
import com.scrapeverything.app.ui.scrap.ScrapListViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.scrapeverything.app.ui.scrap.ScrapListViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

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
public final class DaggerScrapEverythingApplication_HiltComponents_SingletonC {
  private DaggerScrapEverythingApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public ScrapEverythingApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements ScrapEverythingApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public ScrapEverythingApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements ScrapEverythingApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public ScrapEverythingApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements ScrapEverythingApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public ScrapEverythingApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements ScrapEverythingApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public ScrapEverythingApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements ScrapEverythingApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public ScrapEverythingApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements ScrapEverythingApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public ScrapEverythingApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements ScrapEverythingApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public ScrapEverythingApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends ScrapEverythingApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends ScrapEverythingApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends ScrapEverythingApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends ScrapEverythingApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
      injectMainActivity2(arg0);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(5).put(CategoryListViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, CategoryListViewModel_HiltModules.KeyModule.provide()).put(LoginViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, LoginViewModel_HiltModules.KeyModule.provide()).put(MyPageViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, MyPageViewModel_HiltModules.KeyModule.provide()).put(ScrapDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ScrapDetailViewModel_HiltModules.KeyModule.provide()).put(ScrapListViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ScrapListViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectTokenStorage(instance, singletonCImpl.tokenStorageProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends ScrapEverythingApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<CategoryListViewModel> categoryListViewModelProvider;

    private Provider<LoginViewModel> loginViewModelProvider;

    private Provider<MyPageViewModel> myPageViewModelProvider;

    private Provider<ScrapDetailViewModel> scrapDetailViewModelProvider;

    private Provider<ScrapListViewModel> scrapListViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.categoryListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.loginViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.myPageViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.scrapDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.scrapListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(5).put(CategoryListViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) categoryListViewModelProvider)).put(LoginViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) loginViewModelProvider)).put(MyPageViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) myPageViewModelProvider)).put(ScrapDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) scrapDetailViewModelProvider)).put(ScrapListViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) scrapListViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.scrapeverything.app.ui.category.CategoryListViewModel 
          return (T) new CategoryListViewModel(singletonCImpl.categoryRepositoryProvider.get());

          case 1: // com.scrapeverything.app.ui.auth.LoginViewModel 
          return (T) new LoginViewModel(singletonCImpl.authRepositoryProvider.get());

          case 2: // com.scrapeverything.app.ui.member.MyPageViewModel 
          return (T) new MyPageViewModel(singletonCImpl.memberRepositoryProvider.get(), singletonCImpl.authRepositoryProvider.get());

          case 3: // com.scrapeverything.app.ui.scrap.ScrapDetailViewModel 
          return (T) new ScrapDetailViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.scrapRepositoryProvider.get());

          case 4: // com.scrapeverything.app.ui.scrap.ScrapListViewModel 
          return (T) new ScrapListViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.scrapRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends ScrapEverythingApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends ScrapEverythingApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends ScrapEverythingApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<TokenStorage> tokenStorageProvider;

    private Provider<AuthInterceptor> authInterceptorProvider;

    private Provider<RefreshInterceptor> refreshInterceptorProvider;

    private Provider<RefreshApiProvider> refreshApiProvider;

    private Provider<TokenAuthenticator> tokenAuthenticatorProvider;

    private Provider<HttpLoggingInterceptor> provideLoggingInterceptorProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<CategoryApi> provideCategoryApiProvider;

    private Provider<CategoryRepository> categoryRepositoryProvider;

    private Provider<AuthApi> provideAuthApiProvider;

    private Provider<AuthRepository> authRepositoryProvider;

    private Provider<MemberApi> provideMemberApiProvider;

    private Provider<MemberRepository> memberRepositoryProvider;

    private Provider<ScrapApi> provideScrapApiProvider;

    private Provider<ScrapRepository> scrapRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.tokenStorageProvider = DoubleCheck.provider(new SwitchingProvider<TokenStorage>(singletonCImpl, 0));
      this.authInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<AuthInterceptor>(singletonCImpl, 5));
      this.refreshInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<RefreshInterceptor>(singletonCImpl, 6));
      this.refreshApiProvider = DoubleCheck.provider(new SwitchingProvider<RefreshApiProvider>(singletonCImpl, 8));
      this.tokenAuthenticatorProvider = DoubleCheck.provider(new SwitchingProvider<TokenAuthenticator>(singletonCImpl, 7));
      this.provideLoggingInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<HttpLoggingInterceptor>(singletonCImpl, 9));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 4));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 3));
      this.provideCategoryApiProvider = DoubleCheck.provider(new SwitchingProvider<CategoryApi>(singletonCImpl, 2));
      this.categoryRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<CategoryRepository>(singletonCImpl, 1));
      this.provideAuthApiProvider = DoubleCheck.provider(new SwitchingProvider<AuthApi>(singletonCImpl, 11));
      this.authRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepository>(singletonCImpl, 10));
      this.provideMemberApiProvider = DoubleCheck.provider(new SwitchingProvider<MemberApi>(singletonCImpl, 13));
      this.memberRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<MemberRepository>(singletonCImpl, 12));
      this.provideScrapApiProvider = DoubleCheck.provider(new SwitchingProvider<ScrapApi>(singletonCImpl, 15));
      this.scrapRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ScrapRepository>(singletonCImpl, 14));
    }

    @Override
    public void injectScrapEverythingApplication(
        ScrapEverythingApplication scrapEverythingApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.scrapeverything.app.data.local.TokenStorage 
          return (T) new TokenStorage(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.scrapeverything.app.data.repository.CategoryRepository 
          return (T) new CategoryRepository(singletonCImpl.provideCategoryApiProvider.get());

          case 2: // com.scrapeverything.app.data.api.CategoryApi 
          return (T) NetworkModule_ProvideCategoryApiFactory.provideCategoryApi(singletonCImpl.provideRetrofitProvider.get());

          case 3: // retrofit2.Retrofit 
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get());

          case 4: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient(singletonCImpl.authInterceptorProvider.get(), singletonCImpl.refreshInterceptorProvider.get(), singletonCImpl.tokenAuthenticatorProvider.get(), singletonCImpl.provideLoggingInterceptorProvider.get());

          case 5: // com.scrapeverything.app.network.AuthInterceptor 
          return (T) new AuthInterceptor(singletonCImpl.tokenStorageProvider.get());

          case 6: // com.scrapeverything.app.network.RefreshInterceptor 
          return (T) new RefreshInterceptor(singletonCImpl.tokenStorageProvider.get());

          case 7: // com.scrapeverything.app.network.TokenAuthenticator 
          return (T) new TokenAuthenticator(singletonCImpl.tokenStorageProvider.get(), singletonCImpl.refreshApiProvider.get());

          case 8: // com.scrapeverything.app.network.RefreshApiProvider 
          return (T) new RefreshApiProvider();

          case 9: // okhttp3.logging.HttpLoggingInterceptor 
          return (T) NetworkModule_ProvideLoggingInterceptorFactory.provideLoggingInterceptor();

          case 10: // com.scrapeverything.app.data.repository.AuthRepository 
          return (T) new AuthRepository(singletonCImpl.provideAuthApiProvider.get(), singletonCImpl.tokenStorageProvider.get());

          case 11: // com.scrapeverything.app.data.api.AuthApi 
          return (T) NetworkModule_ProvideAuthApiFactory.provideAuthApi(singletonCImpl.provideRetrofitProvider.get());

          case 12: // com.scrapeverything.app.data.repository.MemberRepository 
          return (T) new MemberRepository(singletonCImpl.provideMemberApiProvider.get());

          case 13: // com.scrapeverything.app.data.api.MemberApi 
          return (T) NetworkModule_ProvideMemberApiFactory.provideMemberApi(singletonCImpl.provideRetrofitProvider.get());

          case 14: // com.scrapeverything.app.data.repository.ScrapRepository 
          return (T) new ScrapRepository(singletonCImpl.provideScrapApiProvider.get());

          case 15: // com.scrapeverything.app.data.api.ScrapApi 
          return (T) NetworkModule_ProvideScrapApiFactory.provideScrapApi(singletonCImpl.provideRetrofitProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
