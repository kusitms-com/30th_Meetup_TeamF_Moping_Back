package com.ping.application.nonmember

import com.ping.application.nonmember.dto.CreateNonMember
import com.ping.application.nonmember.dto.GetAllNonMemberPings
import com.ping.client.navermap.NaverMapClient
import com.ping.common.exception.CustomException
import com.ping.common.exception.ExceptionContent
import com.ping.common.util.UrlUtil
import com.ping.domain.nonmember.aggregate.*
import com.ping.domain.nonmember.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NonMemberService(
    private val nonMemberRepository: NonMemberRepository,
    private val shareUrlRepository: ShareUrlRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val nonMemberPlaceRepository: NonMemberPlaceRepository,
    private val nonMemberBookmarkUrlRepository: NonMemberBookmarkUrlRepository,
    private val nonMemberStoreUrlRepository: NonMemberStoreUrlRepository,
    private val naverMapClient: NaverMapClient
) {

    @Transactional
    fun createNonMemberPings(request: CreateNonMember.Request) {
        //이름 공백, 특수문자, 숫자 불가
        validateName(request.name)
        // 비밀번호 형식 검사 (4자리 숫자)
        validatePassword(request.password)

        val shareUrl = shareUrlRepository.findByUuid(request.uuid)
            ?: throw CustomException(ExceptionContent.INVALID_SHARE_URL)

        // shareUrlId과 name으로 비회원 존재 여부 확인
        nonMemberRepository.findByShareUrlIdAndName(shareUrl.id, request.name)?.let {
            throw CustomException(ExceptionContent.NON_MEMBER_ALREADY_EXISTS)
        }

        // NonMember 엔티티 생성 및 저장
        val nonMemberDomain = NonMemberDomain.of(request.name, request.password, shareUrl)
        val nonmember = nonMemberRepository.save(nonMemberDomain)

        //url 저장
        val nonMemberBookmarkUrlDomains = NonMemberBookmarkUrlDomain.of(nonmember, request.bookmarkUrls)
        nonMemberBookmarkUrlRepository.saveAll(nonMemberBookmarkUrlDomains)

        val nonMemberStoreUrlDomains = NonMemberStoreUrlDomain.of(nonmember, request.storeUrls)
        nonMemberStoreUrlRepository.saveALl(nonMemberStoreUrlDomains)

        val nonMemberPlaces = mutableListOf<NonMemberPlaceDomain>()
        val bookmarks = mutableListOf<BookmarkDomain>()
        //맵핀 모은 링크 추출
        bookmarks.addAll(
            request.bookmarkUrls.flatMap {
                val url = UrlUtil.expandShortUrl(it)
                naverMapClient.bookmarkUrlToBookmarkLists(url).bookmarkList.map { bookmark ->
                    //NonMemberPlace 저장
                    nonMemberPlaces
                        .takeIf { nonMemberPlace -> nonMemberPlace.none { place -> place.sid == bookmark.sid } }
                        ?.add(NonMemberPlaceDomain.of(nonmember, bookmark.sid))
                    BookmarkDomain(
                        name = bookmark.name,
                        px = bookmark.px,
                        py = bookmark.py,
                        sid = bookmark.sid,
                        address = bookmark.address,
                        mcidName = bookmark.mcidName,
                        url = "https://map.naver.com/p/entry/place/${bookmark.sid}"
                    )
                }
            })
        //맵핀 가게 링크 추출
        bookmarks.addAll(
            request.storeUrls.map {
                val url = UrlUtil.expandShortUrl(it)
                val bookmark = naverMapClient.storeUrlToBookmark(url)
                //NonMemberPlace 저장
                nonMemberPlaces
                    .takeIf { nonMemberPlace -> nonMemberPlace.none { place -> place.sid == bookmark.sid } }
                    ?.add(NonMemberPlaceDomain.of(nonmember, bookmark.sid))
                BookmarkDomain(
                    name = bookmark.name,
                    px = bookmark.px,
                    py = bookmark.py,
                    sid = bookmark.sid,
                    address = bookmark.address,
                    mcidName = bookmark.mcidName,
                    url = it
                )
            })
        bookmarkRepository.saveAll(bookmarks)
        nonMemberPlaceRepository.saveAll(nonMemberPlaces)
    }

    // 이름 우효성 검증 로직
    private fun validateName(name: String) {
        val namePattern = "^[가-힣a-zA-Z]{1,6}\$".toRegex()
        if (!namePattern.matches(name)) {
            throw CustomException(ExceptionContent.INVALID_NAME_FORMAT)
        }
    }

    // 비밀번호 유효성 검증 로직
    private fun validatePassword(password: String) {
        if (!password.matches(Regex("\\d{4}"))) {
            throw CustomException(ExceptionContent.INVALID_PASSWORD_FORMAT)
        }
    }

    fun getAllNonMemberPings(uuid: String): GetAllNonMemberPings.Response {
        val shareUrl = shareUrlRepository.findByUuid(uuid)
            ?: throw CustomException(ExceptionContent.INVALID_SHARE_URL)
        val nonMemberList = nonMemberRepository.findAllByShareUrl(shareUrl.id)

        val nonMembers = nonMemberList.map { nonMember ->
            GetAllNonMemberPings.NonMember(
                nonMemberId = nonMember.id,
                name = nonMember.name
            )
        }

        //list<Pair<NonMemberPlaceDomain, List<NonMemberDomain>>>
        val allNonMemberPlaces = nonMemberList.flatMap { nonMember ->
            nonMemberPlaceRepository.findAllByNonMemberId(nonMember.id).map { place ->
                place to nonMember
            }
        }
        val bookmarks = bookmarkRepository.findAllBySidIn(allNonMemberPlaces.map { it.first.sid }.distinct())
        val bookmarkMap = bookmarks.associateBy { it.sid }

        //Map<count,list<Pair<BookmarkDomain,List<NonMemberDomain>>>>
        val nonMemberPlaces = allNonMemberPlaces
            .groupBy { it.first.sid }
            .mapNotNull { (sid, placeNonMemberPairs) ->
                val bookmarkDomain = bookmarkMap[sid]
                bookmarkDomain?.let {
                    it to placeNonMemberPairs.map { placeNonMemberPair ->
                        placeNonMemberPair.second
                    }
                }
            }
            .sortedByDescending { it.second.size }.groupBy { it.second.size }

        val pings = nonMemberPlaces.entries.mapIndexed{ index, nonMemberPlace ->
            val level = when {
                nonMemberPlace.key == 1 -> 0  // 아무도 안겹친 sid (겹친 인원이 1인 경우)
                index == 0 -> 4        // 가장 많이 겹친 sid
                index == 1 -> 3        // 두 번째로 많이 겹친 sid
                index == 2 -> 2        // 세 번째로 많이 겹친 sid
                else -> 1              // 그 외의 겹친 sid
            }
            nonMemberPlace.value.map { bookmarkPair ->
                GetAllNonMemberPings.Ping(
                    iconLevel = level,
                    nonMembers = bookmarkPair.second.map {
                        GetAllNonMemberPings.NonMember(
                            nonMemberId = it.id,
                            name = it.name
                        )
                    },
                    url = bookmarkPair.first.url,
                    placeName = bookmarkPair.first.name,
                    px = bookmarkPair.first.px,
                    py = bookmarkPair.first.py,
                )
            }
        }.flatten()


        return GetAllNonMemberPings.Response(
            eventName = shareUrl.eventName,
            nonMembers = nonMembers,
            pings = pings
        )
    }
}