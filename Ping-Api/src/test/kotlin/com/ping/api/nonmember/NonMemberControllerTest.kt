package com.ping.api.nonmember

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.ping.api.global.BaseRestDocsTest
import com.ping.application.nonmember.NonMemberService
import com.ping.application.nonmember.dto.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(NonMemberController::class)
class NonMemberControllerTest : BaseRestDocsTest() {

    @MockBean
    private lateinit var nonMemberService: NonMemberService

    private val tag = "비회원"

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

        given(nonMemberService.login(request)).willReturn(response)

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.put(NonMemberApi.LOGIN)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo( // rest docs
                resultHandler.document(
                    requestFields(
                        fieldWithPath("nonMemberId").description("비회원 ID"),
                        fieldWithPath("password").description("비회원 비밀번호")
                    ),
                    responseFields(
                        fieldWithPath("nonMemberId").description("로그인한 비회원의 ID"),
                        fieldWithPath("name").description("비회원의 이름"),
                        fieldWithPath("bookmarkUrls").description("비회원의 북마크 URL 목록"),
                        fieldWithPath("storeUrls").description("비회원의 스토어 URL 목록")
                    )
                )
            )
            .andDo( // swagger
                MockMvcRestDocumentationWrapper.document(
                    identifier = "비회원 로그인",
                    resourceDetails = ResourceSnippetParametersBuilder()
                        .tag(tag)
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
                )
            )
    }


    @Test
    @DisplayName("비회원 핑 생성")
    fun createNonMemberPings() {
        // given
        val request = CreateNonMember.Request(
            uuid = "test",
            name = "윤소민",
            password = "1234",
            bookmarkUrls = listOf("https://naver.me/Fqimcb8B", "https://naver.me/xUwGH5c3"),
            storeUrls = listOf("https://naver.me/GuGEom4T", "https://naver.me/FuVzL1bq")
        )

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(NonMemberApi.PING)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        result.andExpect(status().isOk)
            .andDo( // rest docs
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
            .andDo( // swagger
                MockMvcRestDocumentationWrapper.document(
                    identifier = "비회원 핑 생성",
                    resourceDetails = ResourceSnippetParametersBuilder()
                        .tag(tag)
                        .description("비회원 핑 생성")
                        .requestFields(
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
    @DisplayName("추천 핑 저장")
    fun saveRecommendPings() {
        // given
        val request = SaveRecommendPings.Request(
            uuid = "test",
            sids = listOf(
                "1445446311",
                "1250904288",
            )
        )

        val response = GetAllNonMemberPings.Response(
            eventName = "핑핑이들 여행",
            neighborhood = "강원도 속초",
            px = 127.00001,
            py = 37.00001,
            pingLastUpdateTime = "16분",
            recommendPings = listOf(
                GetRecommendPings.RecommendPing(
                    sid = "1445446311",
                    placeName = "꾸아 광교점",
                    url = "https://map.naver.com/p/entry/place/1445446311",
                    px = 127.0548454,
                    py = 37.2938313,
                ),
                GetRecommendPings.RecommendPing(
                    sid = "1250904288",
                    placeName = "옴레스토랑 광교점",
                    url = "https://map.naver.com/p/entry/place/1250904288",
                    px = 127.0505683,
                    py = 37.2903113,
                )
            ),
            nonMembers = listOf(
                GetAllNonMemberPings.NonMember(
                    nonMemberId = 1,
                    name = "핑핑이1",
                    profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                ),
                GetAllNonMemberPings.NonMember(
                    nonMemberId = 2,
                    name = "핑핑이2",
                    profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                )
            ),
            pings = listOf(
                GetAllNonMemberPings.Ping(
                    iconLevel = 2,
                    nonMembers = listOf(
                        GetAllNonMemberPings.NonMember(
                            nonMemberId = 1,
                            name = "핑핑이1",
                            profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                        ),
                        GetAllNonMemberPings.NonMember(
                            nonMemberId = 2,
                            name = "핑핑이2",
                            profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                        )
                    ),
                    url = "https://map.naver.com/p/entry/place/1946678040",
                    placeName = "호이",
                    px = 126.971178,
                    py = 37.5302481,
                    type = "음식점"
                ),
                GetAllNonMemberPings.Ping(
                    iconLevel = 1,
                    nonMembers = listOf(
                        GetAllNonMemberPings.NonMember(
                            nonMemberId = 1,
                            name = "핑핑이1",
                            profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                        ),
                    ),
                    url = "https://map.naver.com/p/entry/place/1492901893",
                    placeName = "퍼즈앤스틸",
                    px = 126.9713426,
                    py = 37.5303303,
                    type = "음식점"
                )
            )
        )

        given(nonMemberService.saveRecommendPings(request)).willReturn(response)

        // when
        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.post(NonMemberApi.PING_RECOMMEND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        result.andExpect(status().isOk)
            .andDo( // rest docs
                resultHandler.document(
                    requestFields(
                        fieldWithPath("uuid").description("이벤트 식별자 UUID"),
                        fieldWithPath("sids").description("추천 장소 ID 리스트"),
                    ),
                    responseFields(
                        fieldWithPath("eventName").description("이벤트 이름"),
                        fieldWithPath("neighborhood").description("추천 지역 이름"),
                        fieldWithPath("px").description("중심 경도"),
                        fieldWithPath("py").description("중심 위도"),
                        fieldWithPath("pingLastUpdateTime").description("마지막 핑 업데이트 시간"),
                        fieldWithPath("recommendPings[].sid").description("추천 장소 ID"),
                        fieldWithPath("recommendPings[].placeName").description("추천 장소 이름"),
                        fieldWithPath("recommendPings[].url").description("추천 장소 URL"),
                        fieldWithPath("recommendPings[].px").description("추천 장소 경도"),
                        fieldWithPath("recommendPings[].py").description("추천 장소 위도"),
                        fieldWithPath("nonMembers[].nonMemberId").description("비회원의 id"),
                        fieldWithPath("nonMembers[].name").description("비회원 이름"),
                        fieldWithPath("nonMembers[].profileSvg").description("프로필 svg url"),
                        fieldWithPath("pings[].iconLevel").description("아이콘 레벨\n4:가장 많이 겹침\n3:그다음\n2:그다음\n1:나머지"),
                        fieldWithPath("pings[].nonMembers[].nonMemberId").description("비회원 id"),
                        fieldWithPath("pings[].nonMembers[].name").description("비회원 이름"),
                        fieldWithPath("pings[].nonMembers[].profileSvg").description("프로필 svg url"),
                        fieldWithPath("pings[].url").description("장소 url"),
                        fieldWithPath("pings[].placeName").description("장소 이름"),
                        fieldWithPath("pings[].px").description("경도"),
                        fieldWithPath("pings[].py").description("위도"),
                        fieldWithPath("pings[].type").description("업종"),
                    )
                )
            )
            .andDo( // swagger
                MockMvcRestDocumentationWrapper.document(
                    identifier = "추천 핑 저장",
                    resourceDetails = ResourceSnippetParametersBuilder()
                        .tag(tag)
                        .description("추천 핑 저장")
                        .requestFields(
                            fieldWithPath("uuid").description("이벤트 식별자 UUID"),
                            fieldWithPath("sids").description("추천 장소 ID 리스트"),
                        )
                        .responseFields(
                            fieldWithPath("eventName").description("이벤트 이름"),
                            fieldWithPath("neighborhood").description("추천 지역 이름"),
                            fieldWithPath("px").description("중심 경도"),
                            fieldWithPath("py").description("중심 위도"),
                            fieldWithPath("pingLastUpdateTime").description("마지막 핑 업데이트 시간"),
                            fieldWithPath("recommendPings[].sid").description("추천 장소 ID"),
                            fieldWithPath("recommendPings[].placeName").description("추천 장소 이름"),
                            fieldWithPath("recommendPings[].url").description("추천 장소 URL"),
                            fieldWithPath("recommendPings[].px").description("추천 장소 경도"),
                            fieldWithPath("recommendPings[].py").description("추천 장소 위도"),
                            fieldWithPath("nonMembers[].nonMemberId").description("비회원의 id"),
                            fieldWithPath("nonMembers[].name").description("비회원 이름"),
                            fieldWithPath("nonMembers[].profileSvg").description("프로필 svg url"),
                            fieldWithPath("pings[].iconLevel").description("아이콘 레벨\n4:가장 많이 겹침\n3:그다음\n2:그다음\n1:나머지"),
                            fieldWithPath("pings[].nonMembers[].nonMemberId").description("비회원 id"),
                            fieldWithPath("pings[].nonMembers[].name").description("비회원 이름"),
                            fieldWithPath("pings[].nonMembers[].profileSvg").description("프로필 svg url"),
                            fieldWithPath("pings[].url").description("장소 url"),
                            fieldWithPath("pings[].placeName").description("장소 이름"),
                            fieldWithPath("pings[].px").description("경도"),
                            fieldWithPath("pings[].py").description("위도"),
                            fieldWithPath("pings[].type").description("업종"),
                        )
                )
            )
    }

    @Test
    @DisplayName("전체 핑 불러오기")
    fun getAllNonMemberPings() {
        // given
        val uuid = "test"
        val response = GetAllNonMemberPings.Response(
            eventName = "핑핑이들 여행",
            neighborhood = "강원도 속초",
            px = 127.00001,
            py = 37.00001,
            pingLastUpdateTime = "16분",
            recommendPings = listOf(
                GetRecommendPings.RecommendPing(
                    sid = "1445446311",
                    placeName = "꾸아 광교점",
                    url = "https://map.naver.com/p/entry/place/1445446311",
                    px = 127.0548454,
                    py = 37.2938313,
                ),
                GetRecommendPings.RecommendPing(
                    sid = "1250904288",
                    placeName = "옴레스토랑 광교점",
                    url = "https://map.naver.com/p/entry/place/1250904288",
                    px = 127.0505683,
                    py = 37.2903113,
                )
            ),
            nonMembers = listOf(
                GetAllNonMemberPings.NonMember(
                    nonMemberId = 1,
                    name = "핑핑이1",
                    profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                ),
                GetAllNonMemberPings.NonMember(
                    nonMemberId = 2,
                    name = "핑핑이2",
                    profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                )
            ),
            pings = listOf(
                GetAllNonMemberPings.Ping(
                    iconLevel = 2,
                    nonMembers = listOf(
                        GetAllNonMemberPings.NonMember(
                            nonMemberId = 1,
                            name = "핑핑이1",
                            profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                        ),
                        GetAllNonMemberPings.NonMember(
                            nonMemberId = 2,
                            name = "핑핑이2",
                            profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                        )
                    ),
                    url = "https://map.naver.com/p/entry/place/1946678040",
                    placeName = "호이",
                    px = 126.971178,
                    py = 37.5302481,
                    type = "음식점"
                ),
                GetAllNonMemberPings.Ping(
                    iconLevel = 1,
                    nonMembers = listOf(
                        GetAllNonMemberPings.NonMember(
                            nonMemberId = 1,
                            name = "핑핑이1",
                            profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                        ),
                    ),
                    url = "https://map.naver.com/p/entry/place/1492901893",
                    placeName = "퍼즈앤스틸",
                    px = 126.9713426,
                    py = 37.5303303,
                    type = "음식점"
                )
            )
        )

        given(nonMemberService.getAllNonMemberPings(uuid)).willReturn(response)

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(NonMemberApi.PING)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("uuid", uuid)
        )

        // then
        result.andExpect(status().isOk)
            .andDo( //rest docs
                resultHandler.document(
                    queryParameters(
                        RequestDocumentation.parameterWithName("uuid").description("이벤트 식별자 UUID")
                    ),
                    responseFields(
                        fieldWithPath("eventName").description("이벤트 이름"),
                        fieldWithPath("neighborhood").description("추천 지역 이름"),
                        fieldWithPath("px").description("중심 경도"),
                        fieldWithPath("py").description("중심 위도"),
                        fieldWithPath("pingLastUpdateTime").description("마지막 핑 업데이트 시간"),
                        fieldWithPath("recommendPings[].sid").description("추천 장소 ID"),
                        fieldWithPath("recommendPings[].placeName").description("추천 장소 이름"),
                        fieldWithPath("recommendPings[].url").description("추천 장소 URL"),
                        fieldWithPath("recommendPings[].px").description("추천 장소 경도"),
                        fieldWithPath("recommendPings[].py").description("추천 장소 위도"),
                        fieldWithPath("nonMembers[].nonMemberId").description("비회원의 id"),
                        fieldWithPath("nonMembers[].name").description("비회원 이름"),
                        fieldWithPath("nonMembers[].profileSvg").description("프로필 svg url"),
                        fieldWithPath("pings[].iconLevel").description("아이콘 레벨\n4:가장 많이 겹침\n3:그다음\n2:그다음\n1:나머지"),
                        fieldWithPath("pings[].nonMembers[].nonMemberId").description("비회원 id"),
                        fieldWithPath("pings[].nonMembers[].name").description("비회원 이름"),
                        fieldWithPath("pings[].nonMembers[].profileSvg").description("프로필 svg url"),
                        fieldWithPath("pings[].url").description("장소 url"),
                        fieldWithPath("pings[].placeName").description("장소 이름"),
                        fieldWithPath("pings[].px").description("경도"),
                        fieldWithPath("pings[].py").description("위도"),
                        fieldWithPath("pings[].type").description("업종"),
                    )
                )
            )
            .andDo( // swagger
                MockMvcRestDocumentationWrapper.document(
                    identifier = "전체 핑 불러오기",
                    resourceDetails = ResourceSnippetParametersBuilder()
                        .tag(tag)
                        .description("전체 핑 불러오기")
                        .queryParameters(
                            parameterWithName("uuid").description("이벤트 식별자 UUID")
                        )
                        .responseFields(
                            fieldWithPath("eventName").description("이벤트 이름"),
                            fieldWithPath("neighborhood").description("추천 지역 이름"),
                            fieldWithPath("px").description("중심 경도"),
                            fieldWithPath("py").description("중심 위도"),
                            fieldWithPath("pingLastUpdateTime").description("마지막 핑 업데이트 시간"),
                            fieldWithPath("recommendPings[].sid").description("추천 장소 ID"),
                            fieldWithPath("recommendPings[].placeName").description("추천 장소 이름"),
                            fieldWithPath("recommendPings[].url").description("추천 장소 URL"),
                            fieldWithPath("recommendPings[].px").description("추천 장소 경도"),
                            fieldWithPath("recommendPings[].py").description("추천 장소 위도"),
                            fieldWithPath("nonMembers[].nonMemberId").description("비회원의 id"),
                            fieldWithPath("nonMembers[].name").description("비회원 이름"),
                            fieldWithPath("nonMembers[].profileSvg").description("프로필 svg url"),
                            fieldWithPath("pings[].iconLevel").description("아이콘 레벨\n4:가장 많이 겹침\n3:그다음\n2:그다음\n1:나머지"),
                            fieldWithPath("pings[].nonMembers[].nonMemberId").description("비회원 id"),
                            fieldWithPath("pings[].nonMembers[].name").description("비회원 이름"),
                            fieldWithPath("pings[].nonMembers[].profileSvg").description("프로필 svg url"),
                            fieldWithPath("pings[].url").description("장소 url"),
                            fieldWithPath("pings[].placeName").description("장소 이름"),
                            fieldWithPath("pings[].px").description("경도"),
                            fieldWithPath("pings[].py").description("위도"),
                            fieldWithPath("pings[].type").description("업종"),
                        )
                )
            )
    }

    @Test
    @DisplayName("개별 핑 불러오기")
    fun getNonMemberPing() {
        //given
        val nonMemberId = 1L
        val response = GetNonMemberPing.Response(
            pings = listOf(
                GetAllNonMemberPings.Ping(
                    iconLevel = 0,
                    nonMembers = listOf(
                        GetAllNonMemberPings.NonMember(
                            nonMemberId = nonMemberId,
                            name = "쏘야",
                            profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                        )
                    ),
                    url = "https://map.naver.com/p/entry/place/1072787710",
                    placeName = "도토리",
                    px = 126.9727984,
                    py = 37.5319087,
                    type = "음식점"
                ),
                GetAllNonMemberPings.Ping(
                    iconLevel = 0,
                    nonMembers = listOf(
                        GetAllNonMemberPings.NonMember(
                            nonMemberId = nonMemberId,
                            name = "쏘야",
                            profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                        )
                    ),
                    url = "https://map.naver.com/p/entry/place/1092976589",
                    placeName = "당케커피",
                    px = 126.971301,
                    py = 37.5314638,
                    type = "음식점"
                )
            )
        )

        given(nonMemberService.getNonMemberPing(nonMemberId)).willReturn(response)

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(NonMemberApi.PING_NONMEMBER_ID, nonMemberId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )

        //then
        result.andExpect(status().isOk)
            .andDo( // rest docs
                resultHandler.document(
                    responseFields(
                        fieldWithPath("pings[].iconLevel").description("아이콘 레벨: 0"),
                        fieldWithPath("pings[].nonMembers[].nonMemberId").description("비회원 id"),
                        fieldWithPath("pings[].nonMembers[].name").description("비회원 이름"),
                        fieldWithPath("pings[].nonMembers[].profileSvg").description("프로필 svg url"),
                        fieldWithPath("pings[].url").description("장소 URL"),
                        fieldWithPath("pings[].placeName").description("장소 이름"),
                        fieldWithPath("pings[].px").description("경도"),
                        fieldWithPath("pings[].py").description("위도"),
                        fieldWithPath("pings[].type").description("업종"),
                    )
                )
            )
            .andDo( // swagger
                MockMvcRestDocumentationWrapper.document(
                    identifier = "개별 핑 불러오기",
                    resourceDetails = ResourceSnippetParametersBuilder()
                        .tag(tag)
                        .description("개별 핑 불러오기")
                        .responseFields(
                            fieldWithPath("pings[].iconLevel").description("아이콘 레벨: 0"),
                            fieldWithPath("pings[].nonMembers[].nonMemberId").description("비회원 id"),
                            fieldWithPath("pings[].nonMembers[].name").description("비회원 이름"),
                            fieldWithPath("pings[].nonMembers[].profileSvg").description("프로필 svg url"),
                            fieldWithPath("pings[].url").description("장소 URL"),
                            fieldWithPath("pings[].placeName").description("장소 이름"),
                            fieldWithPath("pings[].px").description("경도"),
                            fieldWithPath("pings[].py").description("위도"),
                            fieldWithPath("pings[].type").description("업종"),
                        )
                )
            )
    }

    @Test
    @DisplayName("추천 핑 조회")
    fun getRecommendPings() {
        // given
        val uuid = "test"
        val radiusInKm = 1.0
        val response = GetRecommendPings.Response(
            recommendPings = listOf(
                GetRecommendPings.RecommendPing(
                    sid = "1445446311",
                    placeName = "꾸아 광교점",
                    url = "https://map.naver.com/p/entry/place/1445446311",
                    px = 127.0548454,
                    py = 37.2938313,
                ),
                GetRecommendPings.RecommendPing(
                    sid = "1250904288",
                    placeName = "옴레스토랑 광교점",
                    url = "https://map.naver.com/p/entry/place/1250904288",
                    px = 127.0505683,
                    py = 37.2903113,
                )
            ),
        )
        given(nonMemberService.getRecommendPings(uuid, radiusInKm)).willReturn(response)

        // when
        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get(NonMemberApi.PING_RECOMMEND)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("uuid", uuid)
                .queryParam("radiusInKm", radiusInKm.toString())
        )

        // then
        result.andExpect(status().isOk)
            .andDo( // rest docs
                resultHandler.document(
                    queryParameters(
                        RequestDocumentation.parameterWithName("uuid").description("이벤트 식별자 UUID"),
                        RequestDocumentation.parameterWithName("radiusInKm").description("추천 반경 범위 (km)"),
                    ),
                    responseFields(
                        fieldWithPath("recommendPings[].sid").description("추천 장소 ID"),
                        fieldWithPath("recommendPings[].placeName").description("추천 장소 이름"),
                        fieldWithPath("recommendPings[].url").description("추천 장소 URL"),
                        fieldWithPath("recommendPings[].px").description("추천 장소 경도"),
                        fieldWithPath("recommendPings[].py").description("추천 장소 위도"),
                    )
                )
            )
            .andDo( // swagger
                MockMvcRestDocumentationWrapper.document(
                    identifier = "추천 핑 조회",
                    resourceDetails = ResourceSnippetParametersBuilder()
                        .tag(tag)
                        .description("추천 핑 조회")
                        .queryParameters(
                            parameterWithName("uuid").description("이벤트 식별자 UUID"),
                            parameterWithName("radiusInKm").description("추천 반경 범위 (km)")
                        )
                        .responseFields(
                            fieldWithPath("recommendPings[].sid").description("추천 장소 ID"),
                            fieldWithPath("recommendPings[].placeName").description("추천 장소 이름"),
                            fieldWithPath("recommendPings[].url").description("추천 장소 URL"),
                            fieldWithPath("recommendPings[].px").description("추천 장소 경도"),
                            fieldWithPath("recommendPings[].py").description("추천 장소 위도"),
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

        // when
        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.put(NonMemberApi.PING)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo( // rest docs
                resultHandler.document(
                    requestFields(
                        fieldWithPath("nonMemberId").description("비회원 ID"),
                        fieldWithPath("bookmarkUrls").description("업데이트할 북마크 URL 목록"),
                        fieldWithPath("storeUrls").description("업데이트할 스토어 URL 목록")
                    )
                )
            )
            .andDo( // swagger
                MockMvcRestDocumentationWrapper.document(
                    identifier = "비회원 핑 업데이트",
                    resourceDetails = ResourceSnippetParametersBuilder()
                        .tag(tag)
                        .description("비회원 핑 업데이트")
                        .requestFields(
                            fieldWithPath("nonMemberId").description("비회원 ID"),
                            fieldWithPath("bookmarkUrls").description("업데이트할 북마크 URL 목록"),
                            fieldWithPath("storeUrls").description("업데이트할 스토어 URL 목록")
                        )
                )
            )
    }

    @Test
    @DisplayName("비회원 모든 핑 리프레쉬")
    fun refreshAllNonMemberPings() {
        // given
        val uuid = "test"
        val response = GetAllNonMemberPings.Response(
            eventName = "수원 광교 힙쟁이들",
            neighborhood = "수원 광교",
            recommendPings = listOf(
                GetRecommendPings.RecommendPing(
                    sid = "1445446311",
                    placeName = "꾸아 광교점",
                    url = "https://map.naver.com/p/entry/place/1445446311",
                    px = 127.0548454,
                    py = 37.2938313,
                ),
                GetRecommendPings.RecommendPing(
                    sid = "1250904288",
                    placeName = "옴레스토랑 광교점",
                    url = "https://map.naver.com/p/entry/place/1250904288",
                    px = 127.0505683,
                    py = 37.2903113,
                )
            ),
            nonMembers = listOf(
                GetAllNonMemberPings.NonMember(
                    nonMemberId = 1,
                    name = "핑핑이1",
                    profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                ),
                GetAllNonMemberPings.NonMember(
                    nonMemberId = 2,
                    name = "핑핑이2",
                    profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                )
            ),
            px = 127.00001,
            py = 37.00001,
            pingLastUpdateTime = "19분",
            pings = listOf(
                GetAllNonMemberPings.Ping(
                    iconLevel = 2,
                    nonMembers = listOf(
                        GetAllNonMemberPings.NonMember(
                            nonMemberId = 1,
                            name = "핑핑이1",
                            profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                        ),
                        GetAllNonMemberPings.NonMember(
                            nonMemberId = 2,
                            name = "핑핑이2",
                            profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg"
                        )
                    ),
                    url = "https://map.naver.com/p/entry/place/1946678040",
                    placeName = "호이",
                    px = 126.971178,
                    py = 37.5302481,
                    type = "음식점"
                )
            )
        )

        given(nonMemberService.refreshAllNonMemberPings(uuid)).willReturn(response)

        // when
        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get(NonMemberApi.PING_REFRESH_ALL)
                .param("uuid", uuid)
                .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        result.andExpect(status().isOk)
            .andDo( // rest docs
                resultHandler.document(
                    queryParameters(
                        RequestDocumentation.parameterWithName("uuid").description("이벤트 식별자 UUID")
                    ),
                    responseFields(
                        fieldWithPath("eventName").description("이벤트 이름"),
                        fieldWithPath("neighborhood").description("추천 지역 이름"),
                        fieldWithPath("px").description("중심 경도"),
                        fieldWithPath("py").description("중심 위도"),
                        fieldWithPath("pingLastUpdateTime").description("마지막 핑 업데이트 시간"),
                        fieldWithPath("recommendPings[].sid").description("추천 장소 ID"),
                        fieldWithPath("recommendPings[].placeName").description("추천 장소 이름"),
                        fieldWithPath("recommendPings[].url").description("추천 장소 URL"),
                        fieldWithPath("recommendPings[].px").description("추천 장소 경도"),
                        fieldWithPath("recommendPings[].py").description("추천 장소 위도"),
                        fieldWithPath("nonMembers[].nonMemberId").description("비회원의 id"),
                        fieldWithPath("nonMembers[].name").description("비회원 이름"),
                        fieldWithPath("nonMembers[].profileSvg").description("프로필 svg url"),
                        fieldWithPath("pings[].iconLevel").description("아이콘 레벨\n4:가장 많이 겹침\n3:그다음\n2:그다음\n1:나머지"),
                        fieldWithPath("pings[].nonMembers[].nonMemberId").description("비회원 id"),
                        fieldWithPath("pings[].nonMembers[].name").description("비회원 이름"),
                        fieldWithPath("pings[].nonMembers[].profileSvg").description("프로필 svg url"),
                        fieldWithPath("pings[].url").description("장소 URL"),
                        fieldWithPath("pings[].placeName").description("장소 이름"),
                        fieldWithPath("pings[].px").description("경도"),
                        fieldWithPath("pings[].py").description("위도"),
                        fieldWithPath("pings[].type").description("업종"),
                    )

                )
            )
            .andDo( //swagger
                MockMvcRestDocumentationWrapper.document(
                    identifier = "비회원 모든 핑 리프레쉬",
                    resourceDetails = ResourceSnippetParametersBuilder()
                        .tag(tag)
                        .description("비회원 모든 핑 리프레쉬")
                        .queryParameters(
                            parameterWithName("uuid").description("이벤트 식별자 UUID")
                        )
                        .responseFields(
                            fieldWithPath("eventName").description("이벤트 이름"),
                            fieldWithPath("neighborhood").description("추천 지역 이름"),
                            fieldWithPath("px").description("중심 경도"),
                            fieldWithPath("py").description("중심 위도"),
                            fieldWithPath("pingLastUpdateTime").description("마지막 핑 업데이트 시간"),
                            fieldWithPath("recommendPings[].sid").description("추천 장소 ID"),
                            fieldWithPath("recommendPings[].placeName").description("추천 장소 이름"),
                            fieldWithPath("recommendPings[].url").description("추천 장소 URL"),
                            fieldWithPath("recommendPings[].px").description("추천 장소 경도"),
                            fieldWithPath("recommendPings[].py").description("추천 장소 위도"),
                            fieldWithPath("nonMembers[].nonMemberId").description("비회원의 id"),
                            fieldWithPath("nonMembers[].name").description("비회원 이름"),
                            fieldWithPath("nonMembers[].profileSvg").description("프로필 svg url"),
                            fieldWithPath("pings[].iconLevel").description("아이콘 레벨\n4:가장 많이 겹침\n3:그다음\n2:그다음\n1:나머지"),
                            fieldWithPath("pings[].nonMembers[].nonMemberId").description("비회원 id"),
                            fieldWithPath("pings[].nonMembers[].name").description("비회원 이름"),
                            fieldWithPath("pings[].nonMembers[].profileSvg").description("프로필 svg url"),
                            fieldWithPath("pings[].url").description("장소 URL"),
                            fieldWithPath("pings[].placeName").description("장소 이름"),
                            fieldWithPath("pings[].px").description("경도"),
                            fieldWithPath("pings[].py").description("위도"),
                            fieldWithPath("pings[].type").description("업종"),
                        )

                )
            )
    }

    @Test
    @DisplayName("비회원 프로필 불러오기")
    fun getNonMemberProfile() {
        // given
        val nonMemberId = 1L
        val response = GetNonMemberProfile.Response(
            name = "쏘야",
            profileSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1.svg",
            profileLockSvg = "https://kr.object.ncloudstorage.com/moping-image/profile1-lock.svg"
        )
        given(nonMemberService.getNonMemberProfile(nonMemberId)).willReturn(response)

        // when
        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get(NonMemberApi.NONMEMBER_ID, nonMemberId)
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo( // rest docs
                resultHandler.document(
                    responseFields(
                        fieldWithPath("name").description("비회원 이름"),
                        fieldWithPath("profileSvg").description("프로필 svg"),
                        fieldWithPath("profileLockSvg").description("프로필 잠금 svg"),
                    )
                )
            )
            .andDo( // swagger
                MockMvcRestDocumentationWrapper.document(
                    identifier = "비회원 프로필 불러오기",
                    resourceDetails = ResourceSnippetParametersBuilder()
                        .tag(tag)
                        .description("비회원 프로필 불러오기")
                        .responseFields(
                            fieldWithPath("name").description("비회원 이름"),
                            fieldWithPath("profileSvg").description("프로필 svg"),
                            fieldWithPath("profileLockSvg").description("프로필 잠금 svg"),
                        )
                )
            )
    }

}