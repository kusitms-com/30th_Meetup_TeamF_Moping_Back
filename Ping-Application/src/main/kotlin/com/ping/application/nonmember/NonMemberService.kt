package com.ping.application.nonmember

import com.ping.application.nonmember.dto.request.NonMemberCreateRequest
import com.ping.client.navermap.NaverMapClient
import com.ping.common.util.UrlUtil
import com.ping.common.exception.CustomException
import com.ping.common.exception.ExceptionContent
import com.ping.domain.nonmember.aggregate.*
import com.ping.domain.nonmember.repository.*
import org.springframework.dao.DataIntegrityViolationException
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
    fun createNonMemberPings(request: NonMemberCreateRequest) {
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
                        mcidName = bookmark.mcidName
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
                    mcidName = bookmark.mcidName
                )
            })
        bookmarkRepository.saveAll(bookmarks)
        nonMemberPlaceRepository.saveAll(nonMemberPlaces)
    }

    // 비밀번호 유효성 검증 로직
    private fun validatePassword(password: String) {
        if (!password.matches(Regex("\\d{4}"))) {
            throw CustomException(ExceptionContent.INVALID_PASSWORD_FORMAT)
        }
    }
}