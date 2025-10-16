package com.levelup.data.repository;

import android.content.Context;
import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ProductRepositoryImpl_Factory implements Factory<ProductRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<Gson> gsonProvider;

  public ProductRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<Gson> gsonProvider) {
    this.contextProvider = contextProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public ProductRepositoryImpl get() {
    return newInstance(contextProvider.get(), gsonProvider.get());
  }

  public static ProductRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<Gson> gsonProvider) {
    return new ProductRepositoryImpl_Factory(contextProvider, gsonProvider);
  }

  public static ProductRepositoryImpl newInstance(Context context, Gson gson) {
    return new ProductRepositoryImpl(context, gson);
  }
}
