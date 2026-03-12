package com.scrapeverything.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    // Repository 클래스들은 @Inject constructor를 사용하므로
    // 별도 @Provides 없이 Hilt가 자동으로 주입합니다.
    // 향후 인터페이스-구현체 바인딩이 필요하면 여기에 @Binds 추가
}
