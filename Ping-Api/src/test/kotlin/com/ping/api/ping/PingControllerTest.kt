package com.ping.api.ping

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.ping.api.global.BaseRestDocsTest
import com.ping.application.ping.dto.IsCorrectUrl
import com.ping.domain.ping.PingService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PingController::class)
class PingControllerTest : BaseRestDocsTest() {

    @MockBean
    private lateinit var pingService: PingService

    private val tag = "핑 링크"

    @Test
    @DisplayName("유효한 북마크 링크")
    fun isCorrectBookmarkUrl() {
        // given
        val request = IsCorrectUrl.Request("https://naver.me/Fqimcb8B")

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(PingApi.BOOKMARK)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo( // rest docs
                resultHandler.document(
                    requestFields(
                        fieldWithPath("url").description("북마크 url"),
                    )
                )
            )
            .andDo( // swagger
                MockMvcRestDocumentationWrapper.document(
                    identifier = "유효한 북마크 링크",
                    resourceDetails = ResourceSnippetParametersBuilder()
                        .tag(tag)
                        .description("유효한 북마크 링크")
                        .requestFields(
                            fieldWithPath("url").description("북마크 url")
                        )
                )
            )
    }

    @Test
    @DisplayName("유효한 가게 게")
    fun isCorrectStoreUrl(){
        // given
        val request = IsCorrectUrl.Request("https://naver.me/xecfaJTy")

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(PingApi.STORE)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo( // rest docs
                resultHandler.document(
                    requestFields(
                        fieldWithPath("url").description("가게 url"),
                    )
                )
            )
            .andDo( // swagger
                MockMvcRestDocumentationWrapper.document(
                    identifier = "유효한 가게",
                    resourceDetails = ResourceSnippetParametersBuilder()
                        .tag(tag)
                        .description("유효한 가게 ")
                        .requestFields(
                            fieldWithPath("url").description("가게 url")
                        )
                )
            )
    }
}