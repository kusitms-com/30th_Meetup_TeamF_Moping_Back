package com.ping.api.place

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.ping.api.global.BaseRestDocsTest
import com.ping.application.place.PlaceService
import com.ping.application.place.dto.GeocodePlace
import com.ping.application.place.dto.SearchPlace
import com.ping.infra.nonmember.domain.mongo.repository.BookmarkMongoRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PlaceController::class)
class PlaceControllerTest : BaseRestDocsTest() {

    @MockBean
    private lateinit var placeService: PlaceService

    @MockBean
    private lateinit var bookmarkMongoRepository: BookmarkMongoRepository

    @Test
    @DisplayName("장소 검색")
    fun searchPlace() {
        // given
        val keyword = "홍대"
        val searchPlaceResponse = listOf(
            SearchPlace.Response("홍대입구", "서울 마포구", 126.9784, 37.5665),
            SearchPlace.Response("홍익대학교", "서울 마포구", 126.9372, 37.5502)
        )
        given(placeService.searchPlace(keyword)).willReturn(searchPlaceResponse)

        // when
        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get(PlaceApi.SEARCH)
                .param("keyword", keyword)
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "place/searchPlace",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("장소")
                            .description("키워드로 장소 검색")
                            .queryParameters(
                                parameterWithName("keyword").description("검색할 장소의 키워드")
                            )
                            .responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data[].name").description("장소 이름"),
                                fieldWithPath("data[].address").description("장소 주소"),
                                fieldWithPath("data[].px").description("장소 위도"),
                                fieldWithPath("data[].py").description("장소 경도")
                            )
                            .responseSchema(Schema.schema("SearchPlaceResponse"))
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("주소로 좌표 검색")
    fun geocodePlace() {
        // given
        val address = "서울시 중구 명동"
        val geocodeResponse = GeocodePlace.Response("서울시 중구 명동", 126.9784, 37.5665)
        given(placeService.getGeocodeAddress(address)).willReturn(geocodeResponse)

        // when
        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get(PlaceApi.GEOCODE)
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "place/geocodePlace",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("장소")
                            .description("주소로 좌표 검색")
                            .queryParameters(
                                parameterWithName("address").description("좌표를 얻고자 하는 주소")
                            )
                            .responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.address").description("검색된 주소"),
                                fieldWithPath("data.px").description("주소의 위도"),
                                fieldWithPath("data.py").description("주소의 경도")
                            )
                            .responseSchema(Schema.schema("GeocodePlaceResponse"))
                            .build()
                    )
                )
            )
    }
}
