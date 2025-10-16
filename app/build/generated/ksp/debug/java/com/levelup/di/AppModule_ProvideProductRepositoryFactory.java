package com.levelup.di;

import android.content.Context;
import com.google.gson.Gson;
import com.levelup.data.repository.ProductRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
    "cast"
})
public final class AppModule_ProvideProductRepositoryFactory implements Factory<ProductRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<Gson> gsonProvider;

  public AppModule_ProvideProductRepositoryFactory(Provider<Context> contextProvider,
      Provider<Gson> gsonProvider) {
    this.contextProvider = contextProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public ProductRepository get() {
    return provideProductRepository(contextProvider.get(), gsonProvider.get());
  }

  public static AppModule_ProvideProductRepositoryFactory create(Provider<Context> contextProvider,
      Provider<Gson> gsonProvider) {
    return new AppModule_ProvideProductRepositoryFactory(contextProvider, gsonProvider);
  }

  public static ProductRepository provideProductRepository(Context context, Gson gson) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideProductRepository(context, gson));
  }
}
