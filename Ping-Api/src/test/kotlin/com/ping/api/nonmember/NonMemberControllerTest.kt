package com.ping.api.nonmember

import com.ping.api.global.BaseRestDocsTest
import com.ping.application.nonmember.NonMemberLoginService
import com.ping.application.nonmember.NonMemberService
import com.ping.application.nonmember.dto.CreateNonMember
import com.ping.application.nonmember.dto.GetAllNonMemberPings
import com.ping.infra.nonmember.domain.mongo.repository.BookmarkMongoRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
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
}