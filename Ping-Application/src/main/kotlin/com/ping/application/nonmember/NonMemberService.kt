package com.ping.application.nonmember

import com.ping.application.nonmember.dto.CreateNonMember
import com.ping.application.nonmember.dto.GetAllNonMemberPings
import com.ping.application.nonmember.dto.UpdateNonMemberPings
import com.ping.application.nonmember.dto.GetNonMemberPing
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
        //이름 공백, 특수문자, 숫자 불가
        validator.name(request.name)
        // 비밀번호 형식 검사 (4자리 숫자)
        validator.password(request.password)

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

        val nonMembers = nonMemberList.map { nonMember ->
            GetAllNonMemberPings.NonMember(
                nonMemberId = nonMember.id,
                name = nonMember.name
            )
        }

        val nonMemberPlaces = nonMembersToNonMemberPlacesMap(nonMemberList)

        val pings = nonMemberPlaces.entries.mapIndexed{ index, nonMemberPlace ->
            val mostOverlappedIconLevel = 4
            val secondOverlappedIconLevel = 3
            val thirdOverlappedIconLevel = 2
            val remainderIconLevel = 1

            val level = when {
                nonMemberPlace.key == 1 -> remainderIconLevel //1명일 때
                index == 0 -> mostOverlappedIconLevel
                index == 1 -> secondOverlappedIconLevel
                index == 2 -> thirdOverlappedIconLevel
                else -> remainderIconLevel
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
            px = shareUrl.latitude,
            py = shareUrl.longtitude,
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

        // 기존 북마크 URL 및 상점 URL 조회
        val existingBookmarkUrls = nonMemberBookmarkUrlRepository.findAllByNonMemberId(nonMemberDomain.id)
        val existingStoreUrls = nonMemberStoreUrlRepository.findAllByNonMemberId(nonMemberDomain.id)

        // URL과 ID를 쉽게 매핑하기 위해 Map 형태로 변환
        val existingBookmarkMap = existingBookmarkUrls.associateBy { it.bookmarkUrl }
        val existingStoreMap = existingStoreUrls.associateBy { it.storeUrl }

        // 새 URL과 기존 URL을 비교하여 추가 및 삭제할 URL 식별
        val bookmarkUrlsToAdd = request.bookmarkUrls.filterNot { it in existingBookmarkMap.keys }
        val bookmarkUrlsToDeleteIds = existingBookmarkUrls.filter { it.bookmarkUrl !in request.bookmarkUrls }.map { it.id }

        val storeUrlsToAdd = request.storeUrls.filterNot { it in existingStoreMap.keys }
        val storeUrlsToDeleteIds = existingStoreUrls.filter { it.storeUrl !in request.storeUrls }.map { it.id }

        // 삭제할 URL을 ID 기반으로 삭제
        nonMemberBookmarkUrlRepository.deleteAllByIds(bookmarkUrlsToDeleteIds)
        nonMemberStoreUrlRepository.deleteAllByIds(storeUrlsToDeleteIds)

        // 새 URL 추가 저장
        nonMemberBookmarkUrlRepository.saveAll(bookmarkUrlsToAdd.map { url ->
            NonMemberBookmarkUrlDomain.of(nonMemberDomain, listOf(url)).first()
        })
        nonMemberStoreUrlRepository.saveAll(storeUrlsToAdd.map { url ->
            NonMemberStoreUrlDomain.of(nonMemberDomain, listOf(url)).first()
        })
    }

    fun refreshAllNonMemberPings(uuid: String): GetAllNonMemberPings.Response {
        val shareUrl = shareUrlRepository.findByUuid(uuid)
            ?: throw CustomException(ExceptionContent.INVALID_SHARE_URL)

        val nonMemberList = nonMemberRepository.findAllByShareUrl(shareUrl.id)

        nonMemberList.forEach { nonMember ->
            // 기존 sid를 추출
            val existingSids = nonMemberPlaceRepository.findAllByNonMemberId(nonMember.id).map { it.sid }.toSet()

            // 새로운 북마크 및 스토어 URL 처리 후 sid 추출
            val bookmarkData = handleBookmarkUrls(nonMemberBookmarkUrlRepository.findAllByNonMemberId(nonMember.id).map { it.bookmarkUrl }, nonMember)
            val storeData = handleStoreUrls(nonMemberStoreUrlRepository.findAllByNonMemberId(nonMember.id).map { it.storeUrl }, nonMember)

            // 새로운 sid 집합 (북마크와 스토어 데이터의 sids 결합)
            val allNewSids = (bookmarkData.sids + storeData.sids)

            // 기존 sid와 비교하여 변경 사항이 있으면 업데이트
            if (existingSids != allNewSids) {
                updatePlaceSids(nonMember, allNewSids)
            }
        }

        val nonMembers = nonMemberList.map { nonMember ->
            GetAllNonMemberPings.NonMember(
                nonMemberId = nonMember.id,
                name = nonMember.name
            )
        }

        // 핑 데이터 생성 및 아이콘 레벨 할당
        val nonMemberPlaces = nonMembersToNonMemberPlacesMap(nonMemberList)
        val pings = nonMemberPlaces.entries.mapIndexed { index, nonMemberPlace ->
            val level = calculateIconLevel(index, nonMemberPlace.key)

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
            px = shareUrl.latitude,
            py = shareUrl.longtitude,
            pings = pings
        )
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
        bookmarkUrls: List<String>, nonMember: NonMemberDomain
    ): BookmarkData {
        val nonMemberPlaces = mutableListOf<NonMemberPlaceDomain>()
        val bookmarks = mutableListOf<BookmarkDomain>()
        val newSids = mutableSetOf<String>()  // 중복 확인을 위한 sid 집합

        bookmarkUrls.forEach { url ->
            val expandedUrl = UrlUtil.expandShortUrl(url)
            val bookmarkList = naverMapClient.bookmarkUrlToBookmarkLists(expandedUrl).bookmarkList

            bookmarkList.forEach { bookmark ->
                // MongoDB에 존재하지 않는 경우에만 BookmarkDomain 생성
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

                // 중복되지 않는 경우에만 NonMemberPlace 추가
                if (newSids.add(bookmark.sid)) {
                    nonMemberPlaces.add(NonMemberPlaceDomain.of(nonMember, bookmark.sid))
                }
            }
        }
        return BookmarkData(nonMemberPlaces, bookmarks, newSids)
    }

    // storeUrls에서 중복되지 않는 NonMemberPlaceDomain과 BookmarkDomain을 생성하고 반환
    private fun handleStoreUrls(
        storeUrls: List<String>, nonMember: NonMemberDomain
    ): BookmarkData {
        val nonMemberPlaces = mutableListOf<NonMemberPlaceDomain>()
        val bookmarks = mutableListOf<BookmarkDomain>()
        val newSids = mutableSetOf<String>()  // 중복 확인을 위한 sid 집합

        storeUrls.forEach { url ->
            val expandedUrl = UrlUtil.expandShortUrl(url)
            val store = naverMapClient.storeUrlToBookmark(expandedUrl)

            // MongoDB에 존재하지 않는 경우에만 BookmarkDomain 생성
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

            // 중복되지 않는 경우에만 NonMemberPlace 추가
            if (newSids.add(store.sid)) {
                nonMemberPlaces.add(NonMemberPlaceDomain.of(nonMember, store.sid))
            }
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

    private fun isBookmarkExists(sid: String): Boolean {
        return bookmarkRepository.findAllBySidIn(listOf(sid)).isNotEmpty()
    }
    private data class BookmarkData(
        val places: List<NonMemberPlaceDomain>,
        val bookmarks: List<BookmarkDomain>,
        val sids: Set<String>
    )

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