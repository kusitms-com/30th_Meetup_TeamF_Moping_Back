package com.ping.api.nonmember

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.ping.api.global.BaseRestDocsTest
import com.ping.application.nonmember.NonMemberLoginService
import com.ping.application.nonmember.NonMemberService
import com.ping.application.nonmember.dto.*
import com.ping.infra.nonmember.domain.mongo.repository.BookmarkMongoRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(NonMemberController::class)
class NonMemberControllerTest : BaseRestDocsTest() {

    @MockBean
    private lateinit var nonMemberService: NonMemberService

    @MockBean
    private lateinit var nonMemberLoginService: NonMemberLoginService

    @MockBean
    private lateinit var bookmarkMongoRepository: BookmarkMongoRepository

    @Test
    @DisplayName("비회원 로그인")
    fun loginNonMember() {
        // given
        val request = LoginNonMember.Request(nonMemberId = 1L, password = "1234")
        val response = LoginNonMember.Response(
            nonMemberId = 1L,
            name = "테스트 사용자",
            bookmarkUrls = listOf("bookmarkUrl1", "bookmarkUrl2"),
            storeUrls = listOf("storeUrl1")
        )

        // 로그인 서비스의 반환 값 설정
        Mockito.`when`(nonMemberLoginService.login(request)).thenReturn(response)

        val jsonRequest = """
        {
            "nonMemberId": 1,
            "password": "1234"
        }
        """

        // when
        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.put(NonMemberApi.LOGIN)
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.nonMemberId").value(response.nonMemberId))
            .andExpect(jsonPath("$.name").value(response.name))
            .andExpect(jsonPath("$.bookmarkUrls[0]").value(response.bookmarkUrls[0]))
            .andExpect(jsonPath("$.bookmarkUrls[1]").value(response.bookmarkUrls[1]))
            .andExpect(jsonPath("$.storeUrls[0]").value(response.storeUrls[0]))
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "nonmember/loginNonMember",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("NonMember")
                            .description("비회원 로그인")
                            .requestFields(
                                fieldWithPath("nonMemberId").description("비회원 ID"),
                                fieldWithPath("password").description("비회원 비밀번호")
                            )
                            .responseFields(
                                fieldWithPath("nonMemberId").description("로그인한 비회원의 ID"),
                                fieldWithPath("name").description("비회원의 이름"),
                                fieldWithPath("bookmarkUrls").description("비회원의 북마크 URL 목록"),
                                fieldWithPath("storeUrls").description("비회원의 스토어 URL 목록")
                            )
                            .responseSchema(Schema.schema("LoginNonMemberResponse"))
                            .build()
                    )
                )
            )
    }


    @Test
    @DisplayName("비회원 핑 생성")
    fun createNonMemberPings() {
        // given
        val createNonMemberRequest = CreateNonMember.Request(
            uuid = "test",
            name = "윤소민",
            password = "1234",
            bookmarkUrls = listOf("https://naver.me/Fqimcb8B", "https://naver.me/xUwGH5c3"),
            storeUrls = listOf("https://naver.me/GuGEom4T", "https://naver.me/FuVzL1bq")
        )
        val request = RestDocumentationRequestBuilders.post(NonMemberApi.PING)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(createNonMemberRequest))

        //when
        val result = mockMvc.perform(request)

        // then
        result.andExpect(status().isOk)
            .andDo(
                resultHandler.document(
                    requestFields(
                        fieldWithPath("uuid").description("공유 url 뒤에 붙는 uuid"),
                        fieldWithPath("name").description("비회원 이름"),
                        fieldWithPath("password").description("비회원 비밀번호"),
                        fieldWithPath("bookmarkUrls").description("네이버 북마크 링크 리스트"),
                        fieldWithPath("storeUrls").description("네이버 개별지도 링크 리스트"),
                    )
                )
            )
    }

    @Test
    @DisplayName("전체 핑 불러오기")
    fun getAllNonMemberPings() {
        // given
        val uuid = "test"
        val request = RestDocumentationRequestBuilders.get(NonMemberApi.PING)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("uuid", uuid)

        val getAllNonMemberPings = GetAllNonMemberPings.Response(
            eventName = "핑핑이들 여행",
            px = 127.00001,
            py = 37.00001,
            nonMembers = listOf(
                GetAllNonMemberPings.NonMember(nonMemberId = 1, name = "핑핑이1"),
                GetAllNonMemberPings.NonMember(nonMemberId = 2, name = "핑핑이2")
            ),
            pings = listOf(
                GetAllNonMemberPings.Ping(
                    iconLevel = 2,
                    nonMembers = listOf(
                        GetAllNonMemberPings.NonMember(nonMemberId = 1, name = "핑핑이1"),
                        GetAllNonMemberPings.NonMember(nonMemberId = 2, name = "핑핑이2")
                    ),
                    url = "https://map.naver.com/p/entry/place/1946678040",
                    placeName = "호이",
                    px = 126.971178,
                    py = 37.5302481
                ),
                GetAllNonMemberPings.Ping(
                    iconLevel = 1,
                    nonMembers = listOf(
                        GetAllNonMemberPings.NonMember(nonMemberId = 1, name = "핑핑이1"),
                    ),
                    url = "https://map.naver.com/p/entry/place/1492901893",
                    placeName = "퍼즈앤스틸",
                    px = 126.9713426,
                    py = 37.5303303
                )
            )
        )

        given(nonMemberService.getAllNonMemberPings(uuid)).willReturn(getAllNonMemberPings)

        //when
        val result = mockMvc.perform(request)

        // then
        result.andExpect(status().isOk)
            .andDo(
                resultHandler.document(
                    responseFields(
                        fieldWithPath("eventName").description("이벤트 이름"),
                        fieldWithPath("px").description("이벤트 중심 경도"),
                        fieldWithPath("py").description("이벤트 중심 위도"),
                        fieldWithPath("nonMembers[].nonMemberId").description("비회원의 id"),
                        fieldWithPath("nonMembers[].name").description("비회원 이름"),
                        fieldWithPath("pings[].iconLevel").description("아이콘 레벨\n4:가장 많이 겹침\n3:그다음\n2:그다음\n1:나머지"),
                        fieldWithPath("pings[].nonMembers[].nonMemberId").description("비회원 id"),
                        fieldWithPath("pings[].nonMembers[].name").description("비회원 이름"),
                        fieldWithPath("pings[].url").description("장소 url"),
                        fieldWithPath("pings[].placeName").description("장소 이름"),
                        fieldWithPath("pings[].px").description("경도"),
                        fieldWithPath("pings[].py").description("위도"),
                    )
                )
            )
    }

    @Test
    @DisplayName("개별 핑 불러오기")
    fun getNonMemberPing() {
        //given
        val nonMemberId = 1L
        val request = RestDocumentationRequestBuilders.get(NonMemberApi.PING_NONMEMBERID,nonMemberId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        val getNonMemberPing = GetNonMemberPing.Response(
            pings = listOf(GetNonMemberPing.Ping(
                url = "https://map.naver.com/p/entry/place/1072787710",
                placeName = "도토리",
                px = 126.9727984,
                py = 37.5319087
            ),
                GetNonMemberPing.Ping(
                    url = "https://map.naver.com/p/entry/place/1092976589",
                    placeName = "당케커피",
                    px = 126.971301,
                    py = 37.5314638
                ))
        )

        given(nonMemberService.getNonMemberPing(nonMemberId)).willReturn(getNonMemberPing)

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isOk)
            .andDo(
                resultHandler.document(
                    responseFields(
                        fieldWithPath("pings[].url").description("장소 url"),
                        fieldWithPath("pings[].placeName").description("장소 이름"),
                        fieldWithPath("pings[].px").description("경도"),
                        fieldWithPath("pings[].py").description("위도"),
                    )
                )
            )

    }

    @Test
    @DisplayName("비회원 핑 업데이트")
    fun updateNonMemberPings() {
        // given
        val request = UpdateNonMemberPings.Request(
            nonMemberId = 1L,
            bookmarkUrls = listOf("https://naver.me/Fqimcb8B"),
            storeUrls = listOf("https://naver.me/FuVzL1bq")
        )

        doNothing().`when`(nonMemberService).updateNonMemberPings(request)

        val jsonRequest = """
            {
                "nonMemberId": 1,
                "bookmarkUrls": ["https://naver.me/Fqimcb8B"],
                "storeUrls": [ "https://naver.me/FuVzL1bq"]
            }
            """

        // when
        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.put(NonMemberApi.PING)
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "nonmember/updateNonMemberPings",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("NonMember")
                            .description("비회원 핑 업데이트")
                            .requestFields(
                                fieldWithPath("nonMemberId").description("비회원 ID"),
                                fieldWithPath("bookmarkUrls").description("업데이트할 북마크 URL 목록"),
                                fieldWithPath("storeUrls").description("업데이트할 스토어 URL 목록")
                            )
//                            .responseFields(
//                                fieldWithPath("code").description("응답 코드"),
//                                fieldWithPath("message").description("응답 메시지"),
//                                fieldWithPath("data").description("응답 데이터")
//                            )
                            .responseSchema(Schema.schema("CommonResponse"))
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("비회원 모든 핑 리프레쉬")
    fun refreshAllNonMemberPings() {
        // given
        val uuid = "test-uuid"
        val response = GetAllNonMemberPings.Response(
            eventName = "Sample Event",
            nonMembers = listOf(
                GetAllNonMemberPings.NonMember(nonMemberId = 1, name = "핑핑이1"),
                GetAllNonMemberPings.NonMember(nonMemberId = 2, name = "핑핑이2")
            ),
            px = 127.00001,
            py = 37.00001,
            pings = listOf(
                GetAllNonMemberPings.Ping(
                    iconLevel = 2,
                    nonMembers = listOf(
                        GetAllNonMemberPings.NonMember(nonMemberId = 1, name = "핑핑이1"),
                        GetAllNonMemberPings.NonMember(nonMemberId = 2, name = "핑핑이2")
                    ),
                    url = "https://map.naver.com/p/entry/place/1946678040",
                    placeName = "호이",
                    px = 126.971178,
                    py = 37.5302481
                )
            )
        )

        Mockito.`when`(nonMemberService.refreshAllNonMemberPings(uuid)).thenReturn(response)

        // when
        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get(NonMemberApi.PING_REFRESH_ALL)
                .param("uuid", uuid)
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "nonmember/refreshAllNonMemberPings",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("NonMember")
                            .description("비회원 모든 핑 리프레쉬")
                            .queryParameters(
                                parameterWithName("uuid").description("이벤트 식별자 UUID")
                            )
                            .responseFields(
                                fieldWithPath("eventName").description("이벤트 이름"),
                                fieldWithPath("px").description("이벤트 중심 경도"),
                                fieldWithPath("py").description("이벤트 중심 위도"),
                                fieldWithPath("nonMembers[].nonMemberId").description("비회원의 id"),
                                fieldWithPath("nonMembers[].name").description("비회원 이름"),
                                fieldWithPath("pings[].iconLevel").description("아이콘 레벨\n4:가장 많이 겹침\n3:그다음\n2:그다음\n1:나머지"),
                                fieldWithPath("pings[].nonMembers[].nonMemberId").description("비회원 id"),
                                fieldWithPath("pings[].nonMembers[].name").description("비회원 이름"),
                                fieldWithPath("pings[].url").description("장소 URL"),
                                fieldWithPath("pings[].placeName").description("장소 이름"),
                                fieldWithPath("pings[].px").description("경도"),
                                fieldWithPath("pings[].py").description("위도")
                            )
                            .responseSchema(Schema.schema("GetAllNonMemberPingsResponse"))
                            .build()
                    )
                )
            )
    }
}