package com.ping.application.nonmember

import com.ping.application.nonmember.dto.*
import com.ping.common.exception.CustomException
import com.ping.common.exception.ExceptionContent
import com.ping.common.util.ValidationUtil
import com.ping.domain.nonmember.aggregate.*
import com.ping.domain.nonmember.repository.*
import com.ping.domain.ping.PingService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.Duration
import kotlin.random.Random

@Service
@Transactional(readOnly = true)
class NonMemberService(
    private val nonMemberRepository: NonMemberRepository,
    private val shareUrlRepository: ShareUrlRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val nonMemberPlaceRepository: NonMemberPlaceRepository,
    private val nonMemberBookmarkUrlRepository: NonMemberBookmarkUrlRepository,
    private val nonMemberStoreUrlRepository: NonMemberStoreUrlRepository,
    private val profileRepository: ProfileRepository,
    private val pingService: PingService
) {
    fun login(request: LoginNonMember.Request): LoginNonMember.Response {
        ValidationUtil.validatePassword(request.password)

        val nonMember = nonMemberRepository.findById(request.nonMemberId)
            ?: throw CustomException(ExceptionContent.NON_MEMBER_NOT_FOUND)

        if (request.password != nonMember.password) {
            throw CustomException(ExceptionContent.NON_MEMBER_LOGIN_FAILED)
        }

        val savedBookmarkUrls =
            nonMemberBookmarkUrlRepository.findAllByNonMemberId(request.nonMemberId).map { it.bookmarkUrl }
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
        ValidationUtil.validateName(request.name)
        ValidationUtil.validatePassword(request.password)

        val shareUrl = shareUrlRepository.findByUuid(request.uuid)
            ?: throw CustomException(ExceptionContent.INVALID_SHARE_URL)

        nonMemberRepository.findByShareUrlIdAndName(shareUrl.id, request.name)?.let {
            throw CustomException(ExceptionContent.NON_MEMBER_ALREADY_EXISTS)
        }

        val nonMemberDomain = NonMemberDomain.of(request.name, request.password, getRandomProfileSvg(), shareUrl)
        val savedNonMember = nonMemberRepository.save(nonMemberDomain)

        val nonMemberBookmarkUrlDomains = NonMemberBookmarkUrlDomain.of(savedNonMember, request.bookmarkUrls)
        val nonMemberStoreUrlDomains = NonMemberStoreUrlDomain.of(savedNonMember, request.storeUrls)
        nonMemberBookmarkUrlRepository.saveAll(nonMemberBookmarkUrlDomains)
        nonMemberStoreUrlRepository.saveAll(nonMemberStoreUrlDomains)

        val bookmarkSids = handleBookmarkUrls(request.bookmarkUrls)
        val storeSids = handleStoreUrls(request.storeUrls)
        val allSids = bookmarkSids + storeSids

        if (allSids.isNotEmpty()) {
            val updatedShareUrl = shareUrl.copy(pingUpdateTime = LocalDateTime.now())
            shareUrlRepository.save(updatedShareUrl)
        }

        val nonMemberPlaces = allSids.map { sid -> NonMemberPlaceDomain.of(savedNonMember, sid) }
        nonMemberPlaceRepository.saveAll(nonMemberPlaces)
    }

    private fun getRandomProfileSvg(): String {
        val profiles = profileRepository.findAll()
        return profiles[Random.nextInt(profiles.size)].url
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
            val existingBookmarkUrls =
                nonMemberBookmarkUrlRepository.findAllByNonMemberId(nonMember.id).map { it.bookmarkUrl }
            val existingStoreUrls = nonMemberStoreUrlRepository.findAllByNonMemberId(nonMember.id).map { it.storeUrl }

            val bookmarkUrlSids = handleBookmarkUrls(existingBookmarkUrls)
            val storeUrlSids = handleStoreUrls(existingStoreUrls)
            val newSids = (bookmarkUrlSids + storeUrlSids)

            updateNonMemberPlacesIfNeeded(nonMember, newSids)
        }

        return createPingResponse(shareUrl, nonMemberList)
    }

    fun getRecommendPings(request: GetRecommendPings.Request): GetRecommendPings.Response {
        val bookmarks =  bookmarkRepository.findByLocationNear(request.px, request.py, request.radiusInKm)
        val nearbySids = bookmarks.map { it.sid }

        val sidCountsMap = nonMemberPlaceRepository.findCountBySidIn(nearbySids)
                .toMap()

        val recommendSids = sidCountsMap.entries
                .sortedByDescending { it.value }
                .take(5)
                .map { it.key }

        val recommendPings = bookmarks.filter { it.sid in recommendSids }
            .map { bookmark ->
                GetRecommendPings.RecommendPing(
                    placeName = bookmark.name,
                    url = bookmark.url,
                    px = bookmark.px,
                    py = bookmark.py
                )
            }

        return GetRecommendPings.Response(recommendPings)
    }

    private fun handleBookmarkUrls(
        bookmarkUrls: List<String>
    ): Set<String> {
        val bookmarks = mutableListOf<BookmarkDomain>()

        bookmarkUrls.forEach {
            bookmarks.addAll(pingService.bookmarkUrlToBookmarks(it))
        }
        bookmarkRepository.saveAll(bookmarks)
        return bookmarks.map { it.sid }.toSet()
    }

    private fun handleStoreUrls(
        storeUrls: List<String>
    ): Set<String> {
        val bookmarks = mutableListOf<BookmarkDomain>()

        storeUrls.forEach {
            bookmarks.add(pingService.storeUrlToBookmark(it))
        }

        return bookmarks.map { it.sid }.toSet()
    }

    private fun updateNonMemberPlacesIfNeeded(nonMember: NonMemberDomain, newSids: Set<String>) {
        val nonMemberPlaces = nonMemberPlaceRepository.findAllByNonMemberId(nonMember.id)
        val existingSids = nonMemberPlaces.map { it.sid }.toSet()

        if (existingSids != newSids) {
            val sidsToAdd = newSids - existingSids
            val sidsToDelete = existingSids - newSids

            val placesToAdd = sidsToAdd.map { sid -> NonMemberPlaceDomain.of(nonMember, sid) }
            nonMemberPlaceRepository.saveAll(placesToAdd)

            val placesIdToDelete = nonMemberPlaces
                .filter { it.nonMember == nonMember && it.sid in sidsToDelete }
                .map { it.id }
            nonMemberPlaceRepository.deleteAll(placesIdToDelete)

            val updatedShareUrl = nonMember.shareUrlDomain.copy(pingUpdateTime = LocalDateTime.now())
            shareUrlRepository.save(updatedShareUrl)

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
        val pingLastUpdateTime = caculateTimeDifference(shareUrl)

        val nonMembers = nonMemberList.map { nonMember ->
            GetAllNonMemberPings.NonMember(
                nonMemberId = nonMember.id,
                name = nonMember.name,
                profileSvg = nonMember.profileSvg
            )
        }

        val nonMemberPlaces = nonMembersToNonMemberPlacesMap(nonMemberList)
        val pings = nonMemberPlaces.entries.flatMapIndexed { index, nonMemberPlace ->
            val level = calculateIconLevel(index, nonMemberPlace.key)
            nonMemberPlace.value.map { bookmarkPair ->
                GetAllNonMemberPings.Ping(
                    iconLevel = level,
                    nonMembers = bookmarkPair.second.map {
                        GetAllNonMemberPings.NonMember(nonMemberId = it.id, name = it.name, profileSvg = it.profileSvg)
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
            neighborhood = shareUrl.neighborhood,
            px = shareUrl.px,
            py = shareUrl.py,
            pingLastUpdateTime = pingLastUpdateTime,
            nonMembers = nonMembers,
            pings = pings,
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

    private fun nonMembersToNonMemberPlacesMap(nonMembers: List<NonMemberDomain>): Map<Int, List<Pair<BookmarkDomain, List<NonMemberDomain>>>> {
        //list<Pair<NonMemberPlaceDomain, List<NonMemberDomain>>>
        val allNonMemberPlaces = nonMembers.flatMap { nonMember ->
            nonMemberPlaceRepository.findAllByNonMemberId(nonMember.id).map { place ->
                place to nonMember
            }
        }

        val bookmarks = bookmarkRepository.findAllBySidIn(allNonMemberPlaces.map { it.first.sid }.distinct())
        val bookmarkMap = bookmarks.associateBy { it.sid }

        //Map<Int(count),List<Pair<BookmarkDomain,List<NonMemberDomain>>>>
        val nonMemberPlaces = allNonMemberPlaces
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

    fun getNonMemberPing(nonMemberId: Long): GetNonMemberPing.Response {
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

    private fun caculateTimeDifference(shareUrl: ShareUrlDomain): String? {
        val pingLastUpdateTime: String? = shareUrl.pingUpdateTime?.let {
            val timeDifference = Duration.between(shareUrl.pingUpdateTime, LocalDateTime.now())
            when {
                timeDifference.toDays() >= 1 -> "${timeDifference.toDays()}일"
                timeDifference.toHours() >= 1 -> "${timeDifference.toHours()}시간"
                timeDifference.toMinutes() >= 1 -> "${timeDifference.toMinutes()}분"
                else -> "1분"
            }
        }
        return pingLastUpdateTime
    }
}