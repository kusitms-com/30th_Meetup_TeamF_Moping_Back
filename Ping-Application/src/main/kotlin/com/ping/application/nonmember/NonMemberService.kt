package com.ping.application.nonmember

import com.ping.application.nonmember.dto.CreateNonMember
import com.ping.application.nonmember.dto.GetAllNonMemberPings
import com.ping.application.nonmember.dto.UpdateNonMemberPings
import com.ping.client.naver.map.NaverMapClient
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
    private val nonMemberUpdateStatusRepository: NonMemberUpdateStatusRepository,
    private val naverMapClient: NaverMapClient,
    private val validator: NonMemberValidator
) {
    @Transactional
    fun createNonMemberPings(request: CreateNonMember.Request) {
        // 이름 및 비밀번호 유효성 검증
        validator.name(request.name)
        validator.password(request.password)

        val shareUrl = shareUrlRepository.findByUuid(request.uuid)
            ?: throw CustomException(ExceptionContent.INVALID_SHARE_URL)

        // 이미 같은 name의 비회원 존재 여부 확인
        nonMemberRepository.findByShareUrlIdAndName(shareUrl.id, request.name)?.let {
            throw CustomException(ExceptionContent.NON_MEMBER_ALREADY_EXISTS)
        }

        // NonMember 생성 및 저장
        val nonMemberDomain = NonMemberDomain.of(request.name, request.password, shareUrl)
        val nonmember = nonMemberRepository.save(nonMemberDomain)

        // URL 데이터 저장
        val nonMemberBookmarkUrlDomains = NonMemberBookmarkUrlDomain.of(nonmember, request.bookmarkUrls)
        nonMemberBookmarkUrlRepository.saveAll(nonMemberBookmarkUrlDomains)

        val nonMemberStoreUrlDomains = NonMemberStoreUrlDomain.of(nonmember, request.storeUrls)
        nonMemberStoreUrlRepository.saveAll(nonMemberStoreUrlDomains)

        // 북마크와 가게 데이터를 개별 리스트로 추출
        val bookmarkPlaces = handleBookmarkUrls(request.bookmarkUrls, nonmember)
        val storePlaces = handleStoreUrls(request.storeUrls, nonmember)

        bookmarkRepository.saveAll(bookmarkPlaces.bookmarks + storePlaces.bookmarks)
        nonMemberPlaceRepository.saveAll(bookmarkPlaces.places + storePlaces.places)

        createNonMemberUpdateStatus(nonmember, shareUrl.id)
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
                nonMemberPlace.key == 1 -> 1  // 아무도 안겹친 sid (겹친 인원이 1인 경우)
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

    @Transactional
    fun updateNonMemberPings(request: UpdateNonMemberPings.Request) {
        val nonMemberDomain = nonMemberRepository.findById(request.nonMemberId)
            ?: throw CustomException(ExceptionContent.NON_MEMBER_NOT_FOUND)

        // 현재 비회원의 기존 sid 추출
        val existingSids = nonMemberPlaceRepository.findAllByNonMemberId(request.nonMemberId).map { it.sid }.toSet()

        // 새로운 북마크 및 가게 데이터 처리
        val bookmarkData = handleBookmarkUrls(request.bookmarkUrls, nonMemberDomain)
        val storeData = handleStoreUrls(request.storeUrls, nonMemberDomain)

        // 전체 새로운 sid 집합
        val allNewSids = (bookmarkData.sids + storeData.sids)
        if (existingSids != allNewSids) {
            updatePlaceSids(nonMemberDomain, allNewSids)
        }

        // 새로운 북마크 데이터 저장
        bookmarkRepository.saveAll(bookmarkData.bookmarks + storeData.bookmarks)
        nonMemberPlaceRepository.saveAll(bookmarkData.places + storeData.places)
    }

    private fun createNonMemberUpdateStatus(newNonMember: NonMemberDomain, shareUrlId: Long) {
        // 새로 생성된 비회원을 제외한 기존 비회원 목록 조회
        val existingNonMembers = nonMemberRepository.findAllByShareUrl(shareUrlId)
            .filter { it.id != newNonMember.id }

        // 기존 비회원이 없으면 return
        if (existingNonMembers.isEmpty()) return

        // 새로 생성된 비회원 기준으로 각 기존 비회원에 대한 NonMemberUpdateStatusDomain 생성
        val updateStatusForNewMember = existingNonMembers.map { existingMember ->
            NonMemberUpdateStatusDomain.of(
                nonMemberDomain = newNonMember,
                friendId = existingMember.id,
                isUpdate = false
            )
        }

        // 기존 비회원 기준으로 새로 생성된 비회원에 대한 NonMemberUpdateStatusDomain 생성
        val updateStatusForExistingMembers = existingNonMembers.map { existingMember ->
            NonMemberUpdateStatusDomain.of(
                nonMemberDomain = existingMember,
                friendId = newNonMember.id,
                isUpdate = false
            )
        }

        // 모든 NonMemberUpdateStatusDomain 저장
        nonMemberUpdateStatusRepository.saveAll(updateStatusForNewMember + updateStatusForExistingMembers)
    }

    private fun handleBookmarkUrls(
        bookmarkUrls: List<String>, nonMember: NonMemberDomain
    ): BookmarkData {
        val nonMemberPlaces = mutableListOf<NonMemberPlaceDomain>()
        val bookmarks = mutableListOf<BookmarkDomain>()
        val newSids = mutableSetOf<String>()

        bookmarkUrls.forEach { url ->
            val expandedUrl = UrlUtil.expandShortUrl(url)
            val bookmarkList = naverMapClient.bookmarkUrlToBookmarkLists(expandedUrl).bookmarkList

            bookmarkList.forEach { bookmark ->
                if (!isBookmarkExists(bookmark.sid)) {
                    bookmarks.add(
                        BookmarkDomain(
                            name = bookmark.name,
                            px = bookmark.px,
                            py = bookmark.py,
                            sid = bookmark.sid,
                            address = bookmark.address,
                            mcidName = bookmark.mcidName,
                            url = "https://map.naver.com/p/entry/place/${bookmark.sid}"
                        )
                    )
                }
                if (nonMemberPlaces.none { it.sid == bookmark.sid }) {
                    nonMemberPlaces.add(NonMemberPlaceDomain.of(nonMember, bookmark.sid))
                }
                newSids.add(bookmark.sid)
            }
        }
        return BookmarkData(nonMemberPlaces, bookmarks, newSids)
    }

    private fun handleStoreUrls(
        storeUrls: List<String>, nonMember: NonMemberDomain
    ): BookmarkData {
        val nonMemberPlaces = mutableListOf<NonMemberPlaceDomain>()
        val bookmarks = mutableListOf<BookmarkDomain>()
        val newSids = mutableSetOf<String>()

        storeUrls.forEach { url ->
            val expandedUrl = UrlUtil.expandShortUrl(url)
            val store = naverMapClient.storeUrlToBookmark(expandedUrl)

            if (!isBookmarkExists(store.sid)) {
                bookmarks.add(
                    BookmarkDomain(
                        name = store.name,
                        px = store.px,
                        py = store.py,
                        sid = store.sid,
                        address = store.address,
                        mcidName = store.mcidName,
                        url = url
                    )
                )
            }
            if (nonMemberPlaces.none { it.sid == store.sid }) {
                nonMemberPlaces.add(NonMemberPlaceDomain.of(nonMember, store.sid))
            }
            newSids.add(store.sid)
        }
        return BookmarkData(nonMemberPlaces, bookmarks, newSids)
    }

    private fun updatePlaceSids(nonMemberDomain: NonMemberDomain, newSids: Set<String>) {
        val existingPlaces = nonMemberPlaceRepository.findAllByNonMemberId(nonMemberDomain.id)
        val existingSids = existingPlaces.map { it.sid }.toSet()

        val sidsToAdd = newSids - existingSids
        val sidsToDelete = existingSids - newSids

        val placesToAdd = sidsToAdd.map { sid -> NonMemberPlaceDomain.of(nonMemberDomain, sid) }
        nonMemberPlaceRepository.saveAll(placesToAdd)

        val placesToDelete = existingPlaces.filter { it.sid in sidsToDelete }
        nonMemberPlaceRepository.deleteAll(placesToDelete)
    }

    private fun isBookmarkExists(sid: String): Boolean {
        return bookmarkRepository.findAllBySidIn(listOf(sid)).isNotEmpty()
    }
    private data class BookmarkData(
        val places: List<NonMemberPlaceDomain>,
        val bookmarks: List<BookmarkDomain>,
        val sids: Set<String>
    )
}