package com.rafael.ibooks.commons.base

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.rafael.ibooks.commons.events.ErrorEvent
import com.rafael.ibooks.commons.events.LoadingEvent
import com.rafael.ibooks.commons.events.UiEvent
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
@ExtendWith(MainCoroutineExtension::class)
@DisplayName("Testes da BaseViewModel")
internal class BaseViewModelTest {


    private class TestViewModel : BaseViewModel() {
        public override fun sendUiEvent(event: UiEvent) {
            super.sendUiEvent(event)
        }

        public override fun sendUiEventOnce(event: UiEvent) {
            super.sendUiEventOnce(event)
        }
    }

    private lateinit var viewModel: TestViewModel

    @BeforeEach
    fun setUp() {
        viewModel = TestViewModel()
    }

    @Test
    @DisplayName("`sendUiEvent` deve emitir o evento NavigateBack no uiEventFlow")
    fun sendUiEvent_shouldEmitNavigateBackEvent() = runTest {
        viewModel.uiEventFlow.test {
            viewModel.sendUiEvent(UiEvent.NavigateBack)

            assertThat(awaitItem()).isEqualTo(UiEvent.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName("`sendUiEventOnce` deve emitir o evento NavigateBack apenas uma vez")
    fun sendUiEventOnce_shouldEmitNavigateBackEventOnlyOnce() = runTest {
        viewModel.uiEventFlow.test {
            viewModel.sendUiEventOnce(UiEvent.NavigateBack)
            viewModel.sendUiEventOnce(UiEvent.NavigateBack)

            assertThat(awaitItem()).isEqualTo(UiEvent.NavigateBack)
            expectNoEvents()
        }
    }

    @Test
    @DisplayName("`launch` deve emitir Show e Hide loading em caso de sucesso")
    fun launch_shouldEmitShowAndHideLoading_onSuccess() = runTest {
        viewModel.loadingFlow.test {
            viewModel.launch {}
            assertThat(awaitItem()).isEqualTo(LoadingEvent.Show)
            assertThat(awaitItem()).isEqualTo(LoadingEvent.Hide)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName("`launch` não deve emitir eventos de loading se o loadingEvent for nulo")
    fun launch_shouldNotEmitLoading_whenLoadingEventIsNull() = runTest {
        viewModel.loadingFlow.test {
            viewModel.launch(loadingEvent = null) {}
            expectNoEvents()
        }
    }

    @Test
    @DisplayName("`launch` deve emitir NetworkError para UnknownHostException")
    fun launch_shouldEmitNetworkError_forUnknownHostException() = runTest {
        viewModel.errorFlow.test {
            viewModel.launch { throw UnknownHostException("No internet connection") }
            assertThat(awaitItem()).isInstanceOf(ErrorEvent.NetworkError::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName("`launch` deve emitir HttpError para HttpException com o código correto")
    fun launch_shouldEmitHttpError_forHttpException() = runTest {
        val mockResponse = Response.error<Any>(404, mockk(relaxed = true))
        val httpException = HttpException(mockResponse)

        viewModel.errorFlow.test {
            viewModel.launch { throw httpException }
            val emittedError = awaitItem()
            assertThat(emittedError).isInstanceOf(ErrorEvent.HttpError::class.java)
            assertThat((emittedError as ErrorEvent.HttpError).code).isEqualTo(404)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName("`launch` deve emitir UnknownError para exceções genéricas")
    fun launch_shouldEmitUnknownError_forGenericException() = runTest {
        val genericError = RuntimeException("A generic error occurred")
        viewModel.errorFlow.test {
            viewModel.launch { throw genericError }
            val emittedError = awaitItem()
            assertThat(emittedError).isInstanceOf(ErrorEvent.UnknownError::class.java)
            assertThat((emittedError as ErrorEvent.UnknownError).throwable).isEqualTo(genericError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName("`launch` deve executar o `onError` customizado em caso de erro")
    fun launch_shouldExecuteCustomOnError_whenProvided() = runTest {
        val genericError = RuntimeException("Error")
        val customOnError = mockk<suspend (Throwable) -> Unit>(relaxed = true)

        viewModel.launch(onError = { error -> customOnError(error) }) {
            throw genericError
        }
        coVerify(exactly = 1) { customOnError(genericError) }
    }

    @Test
    @DisplayName("`launch` não deve emitir no errorFlow se `onError` customizado for fornecido")
    fun launch_shouldNotEmitToErrorFlow_whenCustomOnErrorIsProvided() = runTest {
        viewModel.errorFlow.test {
            viewModel.launch(onError = { /* handler customizado */ }) { throw RuntimeException() }
            expectNoEvents()
        }
    }

    @Test
    @DisplayName("A ação de retry no ErrorEvent deve ser a mesma que foi passada para o `launch`")
    fun retryAction_inErrorEvent_shouldBeTheOnePassedToLaunch() = runTest {
        val mockRetryAction = mockk<() -> Unit>(relaxed = true)
        viewModel.errorFlow.test {
            viewModel.launch(retryAction = mockRetryAction) { throw UnknownHostException() }
            val emittedError = awaitItem() as ErrorEvent.NetworkError
            emittedError.retryAction()
            verify(exactly = 1) { mockRetryAction() }
            cancelAndIgnoreRemainingEvents()
        }
    }
}