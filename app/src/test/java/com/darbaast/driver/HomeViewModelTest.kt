package com.darbaast.driver

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.darbaast.driver.data.model.CarouselData
import com.darbaast.driver.feature.home.HomeViewModel
import com.darbaast.driver.repository.ApiRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantiationExecutorRole = InstantTaskExecutorRule()

    @Mock
    private lateinit var apiRepository: ApiRepository

    private lateinit var homeViewModel: HomeViewModel

    @Mock
    private lateinit var caruselObserver: Observer<List<CarouselData>>

    private lateinit var exceptionObserver:Observer<Exception>


    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
        exceptionObserver = mock()
        homeViewModel = HomeViewModel(apiRepository)
        homeViewModel.carouselLiveData.observeForever(caruselObserver)
        homeViewModel.exceptionLiveData.observeForever(exceptionObserver)
    }

    @Test
    fun `getCarouselData should post carousel data on success`() = runTest{
        val mockData =  listOf(CarouselData(true,"https://fakeimg.pl/350x200/ff0000,128/000,255","https://fakeimg.pl"),CarouselData(false,"https://fakeimg.pl/350x200/ff0000,128/000,255",null))
        whenever(apiRepository.getCaruselData()).thenReturn(mockData)
        homeViewModel.getCarouselData()
        verify(caruselObserver).onChanged(mockData)
    }
    @Test
    fun `getCaruselData should post exception on error`() = runTest {

        val mockException = RuntimeException("error test")
        whenever(apiRepository.getCaruselData()).thenThrow(mockException)
        homeViewModel.getCarouselData()
        verify(exceptionObserver).onChanged(mockException)

    }
}