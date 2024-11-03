package com.ping.application.nonmember

import com.ping.application.nonmember.dto.*
import com.ping.client.naver.map.NaverMapClient
import com.ping.common.exception.CustomException
import com.ping.common.exception.ExceptionContent
import com.ping.common.util.UrlUtil
import com.ping.common.util.ValidationUtil
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
    private val naverMapClient: NaverMapClient
) {
    fun login(request: LoginNonMember.Request): LoginNonMember.Response {
        ValidationUtil.validatePassword(request.password)

        val nonMember = nonMemberRepository.findById(request.nonMemberId) ?: throw CustomException(ExceptionContent.NON_MEMBER_NOT_FOUND)

        if (request.password != nonMember.password) {
            throw CustomException(ExceptionContent.NON_MEMBER_LOGIN_FAILED)
        }

        val savedBookmarkUrls = nonMemberBookmarkUrlRepository.findAllByNonMemberId(request.nonMemberId).map { it.bookmarkUrl }
        val savedStoreUrls = nonMemberStoreUrlRepository.findAllByNonMemberId(request.nonMemberId).map { it.storeUrl }

        return LoginNonMember.Response(
            nonMemberId = nonMember.id,
            name = nonMember.name,
            bookmarkUrls = savedBookmarkUrls,
            storeUrls = savedStoreUrls
        )
    }

    @Transactional
    fun createNonMemberPings(request: CreateNonMember.Request) {
        //이름 공백, 특수문자, 숫자 불가
        ValidationUtil.validateName(request.name)
        // 비밀번호 형식 검사 (4자리 숫자)
        ValidationUtil.validatePassword(request.password)

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
        nonMemberStoreUrlRepository.saveAll(nonMemberStoreUrlDomains)

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

        createNonMemberUpdateStatus(nonmember, shareUrl.id)
    }

    fun getAllNonMemberPings(uuid: String): GetAllNonMemberPings.Response {
        val shareUrl = shareUrlRepository.findByUuid(uuid)
            ?: throw CustomException(ExceptionContent.INVALID_SHARE_URL)
        val nonMemberList = nonMemberRepository.findAllByShareUrl(shareUrl.id)

        return createPingResponse(shareUrl, nonMemberList)
    }

    @Transactional
    fun updateNonMemberPings(request: UpdateNonMemberPings.Request) {
        val nonMember = nonMemberRepository.findById(request.nonMemberId)
            ?: throw CustomException(ExceptionContent.NON_MEMBER_NOT_FOUND)

        val bookmarkUrlSids = handleBookmarkUrls(request.bookmarkUrls)
        val storeUrlSids = handleStoreUrls(request.storeUrls)
        val newSids = (bookmarkUrlSids + storeUrlSids)

        updateNonMemberPlacesIfNeeded(nonMember, newSids)
        updateBookmarkUrls(nonMember, request.bookmarkUrls)
        updateStoreUrls(nonMember, request.storeUrls)
    }

    @Transactional
    fun refreshAllNonMemberPings(uuid: String): GetAllNonMemberPings.Response {
        val shareUrl = shareUrlRepository.findByUuid(uuid)
            ?: throw CustomException(ExceptionContent.INVALID_SHARE_URL)

        val nonMemberList = nonMemberRepository.findAllByShareUrl(shareUrl.id)

        nonMemberList.forEach { nonMember ->
            val existingBookmarkUrls = nonMemberBookmarkUrlRepository.findAllByNonMemberId(nonMember.id).map { it.bookmarkUrl }
            val existingStoreUrls = nonMemberStoreUrlRepository.findAllByNonMemberId(nonMember.id).map { it.storeUrl }

            val bookmarkUrlSids = handleBookmarkUrls(existingBookmarkUrls)
            val storeUrlSids = handleStoreUrls(existingStoreUrls)
            val newSids = (bookmarkUrlSids + storeUrlSids)

            updateNonMemberPlacesIfNeeded(nonMember, newSids)
        }

        return createPingResponse(shareUrl, nonMemberList)
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

        nonMemberUpdateStatusRepository.saveAll(updateStatusForNewMember + updateStatusForExistingMembers)
    }

    private fun handleBookmarkUrls(
        bookmarkUrls: List<String>
    ): Set<String> {
        val bookmarks = mutableListOf<BookmarkDomain>()
        val allSids = mutableSetOf<String>()

        bookmarkUrls.forEach { url ->
            val expandedUrl = UrlUtil.expandShortUrl(url)
            val bookmarkList = naverMapClient.bookmarkUrlToBookmarkLists(expandedUrl).bookmarkList

            bookmarkList.forEach { bookmark ->
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
                allSids.add(bookmark.sid)
            }
        }

        val existingSids = bookmarkRepository.findAllBySidIn(allSids.toList()).map { it.sid }.toSet()
        val bookmarksToAdd = bookmarks.filterNot { it.sid in existingSids }
        bookmarkRepository.saveAll(bookmarksToAdd)

        return allSids
    }

    private fun handleStoreUrls(
        storeUrls: List<String>
    ): Set<String> {
        val bookmarks = mutableListOf<BookmarkDomain>()
        val allSids = mutableSetOf<String>()

        storeUrls.forEach { url ->
            val expandedUrl = UrlUtil.expandShortUrl(url)
            val store = naverMapClient.storeUrlToBookmark(expandedUrl)

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

            allSids.add(store.sid)
        }

        val existingSids = bookmarkRepository.findAllBySidIn(allSids.toList()).map { it.sid }.toSet()
        val bookmarksToAdd = bookmarks.filterNot { it.sid in existingSids }
        bookmarkRepository.saveAll(bookmarksToAdd)

        return allSids
    }

    private fun updateNonMemberPlacesIfNeeded(nonMember: NonMemberDomain, newSids: Set<String>) {
        val nonMemberPlaces = nonMemberPlaceRepository.findAllByNonMemberId(nonMember.id)
        val existingSids = nonMemberPlaces.map { it.sid }.toSet()

        if(existingSids != newSids) {
            val sidsToAdd = newSids - existingSids
            val sidsToDelete = existingSids - newSids

            val placesToAdd = sidsToAdd.map { sid -> NonMemberPlaceDomain.of(nonMember, sid) }
            nonMemberPlaceRepository.saveAll(placesToAdd)

            val placesIdToDelete = nonMemberPlaces
                .filter { it.nonMember == nonMember && it.sid in sidsToDelete }
                .map { it.id }
            nonMemberPlaceRepository.deleteAll(placesIdToDelete)
        }
    }

    private fun updateBookmarkUrls(nonMember: NonMemberDomain, bookmarkUrls: List<String>) {
        val existingBookmarks = nonMemberBookmarkUrlRepository.findAllByNonMemberId(nonMember.id)

        val (urlsToAdd, idsToDelete) = findUrlsToUpdate(
            existingUrls = existingBookmarks,
            newUrls = bookmarkUrls,
            getUrl = { it.bookmarkUrl },
            getId = { it.id }
        )

        nonMemberBookmarkUrlRepository.saveAll(NonMemberBookmarkUrlDomain.of(nonMember, urlsToAdd))
        nonMemberBookmarkUrlRepository.deleteAllByIds(idsToDelete)
    }

    private fun updateStoreUrls(nonMember: NonMemberDomain, storeUrls: List<String>) {
        val existingStoreUrls = nonMemberStoreUrlRepository.findAllByNonMemberId(nonMember.id)

        val (urlsToAdd, idsToDelete) = findUrlsToUpdate(
            existingUrls = existingStoreUrls,
            newUrls = storeUrls,
            getUrl = { it.storeUrl },
            getId = { it.id }
        )

        nonMemberStoreUrlRepository.saveAll(NonMemberStoreUrlDomain.of(nonMember, urlsToAdd))
        nonMemberStoreUrlRepository.deleteAllByIds(idsToDelete)
    }

    private fun <T> findUrlsToUpdate(
        existingUrls: List<T>,
        newUrls: List<String>,
        getUrl: (T) -> String,
        getId: (T) -> Long
    ): Pair<List<String>, List<Long>> {
        val existingUrlSet = existingUrls.map { getUrl(it) }.toSet()
        val urlsToAdd = newUrls.filter { it !in existingUrlSet }
        val idsToDelete = existingUrls.filter { getUrl(it) !in newUrls }.map { getId(it) }

        return Pair(urlsToAdd, idsToDelete)
    }

    private fun createPingResponse(
        shareUrl: ShareUrlDomain,
        nonMemberList: List<NonMemberDomain>
    ): GetAllNonMemberPings.Response {
        val nonMembers = nonMemberList.map { nonMember ->
            GetAllNonMemberPings.NonMember(nonMemberId = nonMember.id, name = nonMember.name)
        }

        val nonMemberPlaces = nonMembersToNonMemberPlacesMap(nonMemberList)
        val pings = nonMemberPlaces.entries.flatMapIndexed { index, nonMemberPlace ->
            val level = calculateIconLevel(index, nonMemberPlace.key)
            nonMemberPlace.value.map { bookmarkPair ->
                GetAllNonMemberPings.Ping(
                    iconLevel = level,
                    nonMembers = bookmarkPair.second.map {
                        GetAllNonMemberPings.NonMember(nonMemberId = it.id, name = it.name)
                    },
                    url = bookmarkPair.first.url,
                    placeName = bookmarkPair.first.name,
                    px = bookmarkPair.first.px,
                    py = bookmarkPair.first.py,
                )
            }
        }

        return GetAllNonMemberPings.Response(
            eventName = shareUrl.eventName,
            nonMembers = nonMembers,
            px = shareUrl.latitude,
            py = shareUrl.longtitude,
            pings = pings
        )
    }

    private fun calculateIconLevel(index: Int, overlapCount: Int): Int {
        val mostOverlappedIconLevel = 4
        val secondOverlappedIconLevel = 3
        val thirdOverlappedIconLevel = 2
        val remainderIconLevel = 1
        return when {
            overlapCount == 1 -> remainderIconLevel
            index == 0 -> mostOverlappedIconLevel
            index == 1 -> secondOverlappedIconLevel
            index == 2 -> thirdOverlappedIconLevel
            else -> remainderIconLevel
        }
    }

    private fun nonMembersToNonMemberPlacesMap(nonMembers: List<NonMemberDomain>): Map<Int,List<Pair<BookmarkDomain,List<NonMemberDomain>>>> {
        //list<Pair<NonMemberPlaceDomain, List<NonMemberDomain>>>
        val allNonMemberPlaces = nonMembers.flatMap { nonMember ->
            nonMemberPlaceRepository.findAllByNonMemberId(nonMember.id).map { place ->
                place to nonMember
            }
        }

        val bookmarks = bookmarkRepository.findAllBySidIn(allNonMemberPlaces.map { it.first.sid }.distinct())
        val bookmarkMap = bookmarks.associateBy { it.sid }

        //Map<Int(count),List<Pair<BookmarkDomain,List<NonMemberDomain>>>>
        val  nonMemberPlaces = allNonMemberPlaces
            .groupBy { it.first.sid }
            .mapNotNull { (sid, placeNonMemberPairs) ->
                val bookmarkDomain = bookmarkMap[sid]
                bookmarkDomain?.let {
                    it to placeNonMemberPairs.map { placeNonMemberPair ->
                        placeNonMemberPair.second
                    }
                }
            }.sortedByDescending { it.second.size }.groupBy { it.second.size }
        return nonMemberPlaces
    }

    fun getNonMemberPing(nonMemberId: Long) : GetNonMemberPing.Response {
        val nonMemberPlaces = nonMemberPlaceRepository.findAllByNonMemberId(nonMemberId)
        val bookmarks = bookmarkRepository.findAllBySidIn(nonMemberPlaces.map { it.sid })
        return GetNonMemberPing.Response(
            pings = bookmarks.map {
                GetNonMemberPing.Ping(
                    url = it.url,
                    placeName = it.name,
                    px = it.px,
                    py = it.py
                )
            }
        )
    }
}