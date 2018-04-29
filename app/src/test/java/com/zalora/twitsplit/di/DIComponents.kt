package com.zalora.twitsplit.di

import com.zalora.twitsplit.TwitSplitComponent
import com.zalora.twitsplit.business.BusinessTest
import com.zalora.twitsplit.di.scope.ItemScope
import com.zalora.twitsplit.di.scope.ScreenScope
import com.zalora.twitsplit.main.HomeTest
import com.zalora.twitsplit.main.MainModule
import dagger.Component

@ScreenScope
@Component (modules = [MainModule::class], dependencies = [TwitSplitComponent::class])
interface MainComponentTest {

    fun inject(homeTest: HomeTest)
    fun inject(businessTest: BusinessTest)

}
