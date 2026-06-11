package ru.ulstu.shoppinglist.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ulstu.shoppinglist.data.local.dao.ShoppingDao
import ru.ulstu.shoppinglist.data.repository.ShoppingRepositoryImpl
import ru.ulstu.shoppinglist.domain.repository.ShoppingRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideShoppingRepository(dao: ShoppingDao): ShoppingRepository {
        return ShoppingRepositoryImpl(dao)
    }
}
